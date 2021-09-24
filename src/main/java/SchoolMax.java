import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import javax.lang.model.type.ArrayType;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class SchoolMax{

    private WebClient client;
    private static String link = "https://family.sis.pgcps.org/schoolmax/reset.do?0uw3YEa.aU7zaju.xnn.xGOGS-Oh-Gh%2BSd_GD_Gh.qh6gwUVm3sEVzWgkz13SdGSFq0hOhqh6_OgwEkeUs3uYAEEsaU7.LUazsrgjumkz13-SgsUWVjUVm3mWgwkmpwUVm31mLUjsegMmr3gYEjWekr3%3Dx";
    private HtmlPage page;

    public SchoolMax() throws IOException {
        client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        page = client.getPage(link);
    }
    //login
    public String login(String username, String password) throws IOException {
        HtmlInput districtInput = page.getFirstByXPath("//*[@id=\"district_number\"]");
        HtmlInput usernameInput = page.getFirstByXPath("//*[@id=\"username\"]");
        HtmlInput passwordInput = page.getFirstByXPath("//*[@id=\"password\"]");

        districtInput.setValueAttribute("16");
        usernameInput.setValueAttribute(username);
        passwordInput.setValueAttribute(password);

        HtmlSubmitInput button = (HtmlSubmitInput) page.getFirstByXPath("//*[@id=\"section_4\"]/tr[2]/td[2]/input");
        page = button.click();
        return page.getTitleText();
    }
    //return all the classes

    public String classes() throws IOException {
        //open gradebook
        HtmlAnchor gradeBook = (HtmlAnchor) page.getAnchorByText("Gradebook");
        page = gradeBook.click();

        String all = page.getVisibleText();
        String newAll = all.substring(all.indexOf("Instructor(s)") + 15, all.lastIndexOf("(primary)"));
        String noGrade = newAll.replaceAll("\\[Grades]", "");
        String noAssignment = noGrade.replaceAll("\\[Assignments]", "\n");
        String noPrimary = noAssignment.replaceAll("\\(primary", "");
        String noPar = noPrimary.replaceAll("\\)", "");
        return noPar;
    }

    public String grade(String courseNumber) throws IOException {
        try {
            HtmlAnchor gradeBook = (HtmlAnchor) page.getAnchorByText("Gradebook");
            page = gradeBook.click();
            String wholePage = page.getVisibleText();

            if (!wholePage.contains(courseNumber)) {
                return null;
            }

            HtmlAnchor coursePage = (HtmlAnchor) page.getAnchorByText(courseNumber);
            page = coursePage.click();
            HtmlAnchor gradePage = (HtmlAnchor) page.getAnchorByText("Review grades for this class");
            page = gradePage.click();

            String gradePage2 = page.getBody().getVisibleText();
            String grade = gradePage2.substring(gradePage2.indexOf("Current"), gradePage2.indexOf("Period and Term Grade"));
            return grade;
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            String grade = "Error";
            return grade;
        }
    }

    public void close() throws IOException {
        HtmlAnchor closeButton = (HtmlAnchor) page.getAnchorByText("Logout");
        closeButton.click();
    }

    public String checkNextYear() throws IOException {
        try {
            HtmlAnchor change = (HtmlAnchor) page.getAnchorByText("change");
            page = change.click();
            HtmlInput input = page.getFirstByXPath("//*[@id=\"field_year3\"]");
            input.setValueAttribute("2023");
            HtmlSubmitInput button = (HtmlSubmitInput) page.getFirstByXPath("//*[@id=\"section_4\"]/tr[2]/td/input");
            page = button.click();
            HtmlAnchor courses = (HtmlAnchor) page.getAnchorByText("Student Course Choices");
            page = courses.click();
            String fullPage = page.getBody().getTextContent();
            String firstCut = fullPage.substring(fullPage.indexOf("Your counselor"), fullPage.indexOf("Copyright"));

            BufferedReader bufferedReader = new BufferedReader(new StringReader(firstCut));
            String line;
            String secondCut = "";
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    secondCut = secondCut + line + "\n";
                }
            }
            String thirdCut = secondCut.substring(secondCut.indexOf("Your"), secondCut.indexOf("document.q"));
            String lastCut = thirdCut.replace(":", ":\n");
            return lastCut;
        }
        catch (Exception e){
            return "Schedule Not Available";
        }
    }

    public ArrayList<String> checkAll() throws IOException {
        //open gradebook
        HtmlAnchor gradeBook = (HtmlAnchor) page.getAnchorByText("Gradebook");
        page = gradeBook.click();
        String all = page.getVisibleText();
        String newAll = all.substring(all.indexOf("Instructor(s)") + 15, all.lastIndexOf("(primary)"));
        String noGrade = newAll.replaceAll("\\[Grades]", "");
        String noAssignment = noGrade.replaceAll("\\[Assignments]", "");
        String noPrimary = noAssignment.replaceAll("\\(primary", "");
        String noPar = noPrimary.replaceAll("\\)", "");

        BufferedReader bufferedReader = new BufferedReader(new StringReader(noPar));
        String line;
        ArrayList<String> classCode = new ArrayList<>();
        while((line = bufferedReader.readLine()) != null){
            if((line.length() != 0) && (!line.contains("LUNCH"))) {
                int space = line.indexOf(" ");
                classCode.add(line.substring(0, space));
            }
        }
        for(int i = 0; i < classCode.size(); i++){
            if(classCode.get(i).startsWith("L"))
                classCode.remove(i);
        }

        ArrayList<String> allGrade = new ArrayList<>();
        for(int i = 0; i < classCode.size(); i++){
            try {
                allGrade.add(this.grade(classCode.get(i)));
            }catch (ElementNotFoundException ignore){}
        }
        return allGrade;
    }

    public String checkAllQuarter() throws IOException {
        ArrayList<String> every = (ArrayList<String>) this.checkAll().clone();
        for(int i = 0; i < every.size(); i++){
            int first = every.get(i).indexOf("Period Weighted") + 27;
            int last = every.get(i).lastIndexOf("Current") - 3;
            try {
                every.set(i, every.get(i).substring(first, last));
            }catch (StringIndexOutOfBoundsException ignore){}
        }

        //open gradebook
        HtmlAnchor gradeBook = (HtmlAnchor) page.getAnchorByText("Gradebook");
        page = gradeBook.click();

        String all = page.getVisibleText();
        String newAll = all.substring(all.indexOf("Instructor(s)") + 15, (all.lastIndexOf("(primary)") + 7)); //special +7 to include primar
        String noGrade = newAll.replaceAll("\\[Grades]", "");
        String noAssignment = noGrade.replaceAll("\\[Assignments]", "");
        //String noPrimary = noAssignment.replaceAll("\\(primary", "");
        //String noPar = noPrimary.replaceAll("\\)", "");
        BufferedReader br = new BufferedReader(new StringReader(noAssignment));
        String line;
        ArrayList<String> allNames = new ArrayList<>();
        while((line = br.readLine()) != null){
            if((!(line.startsWith("LUNCH")) && line.contains("primar")) || line.contains("Released")){
                if(!(line.trim().isEmpty())){
                    if(!(line.contains("primary"))){   //to fix the primary problem
                        line += "y)";
                    }
                    allNames.add(line);
                }
            }
        }
        int difference = 0;
        if(allNames.size() != every.size()){
            difference = Math.abs(allNames.size() - every.size());
        }

        while(difference != 0){
            if(allNames.size() >= every.size()){
                every.add("");
            }else {
                allNames.add("");
            }
            difference--;
        }

        for(int i = 0; i < allNames.size(); i++){
            allNames.set(i, allNames.get(i) + ": " + every.get(i));
        }
        String all2 = "";
        for(int i = 0; i < allNames.size(); i++){
            all2 += allNames.get(i) + "\n\n";
        }
        return all2;
    }

    public String checkAllFY() throws IOException {
        ArrayList<String> every = (ArrayList<String>) this.checkAll().clone();
        for(int i = 0; i < every.size(); i++){
            int first = every.get(i).indexOf(" Grade Weighted") + 27;
            int last = every.get(i).indexOf("%") + 1;
            try {
                every.set(i, every.get(i).substring(first, last));
            }catch (StringIndexOutOfBoundsException ignore){}
        }

        //open gradebook
        HtmlAnchor gradeBook = (HtmlAnchor) page.getAnchorByText("Gradebook");
        page = gradeBook.click();

        String all = page.getVisibleText();
        String newAll = all.substring(all.indexOf("Instructor(s)") + 15, (all.lastIndexOf("(primary)") + 7)); //special +7 to include primar
        String noGrade = newAll.replaceAll("\\[Grades]", "");
        String noAssignment = noGrade.replaceAll("\\[Assignments]", "");
        //String noPrimary = noAssignment.replaceAll("\\(primary", "");
        //String noPar = noPrimary.replaceAll("\\)", "");
        BufferedReader br = new BufferedReader(new StringReader(noAssignment));
        String line;
        ArrayList<String> allNames = new ArrayList<>();
        while((line = br.readLine()) != null){
            if((!(line.startsWith("LUNCH")) && line.contains("primar")) || line.contains("Released")){
                if(!(line.trim().isEmpty())){
                    if(!(line.contains("primary"))){   //to fix the primary problem
                        line += "y)";
                    }
                    allNames.add(line);
                }
            }
        }

        int difference = 0;
        if(allNames.size() != every.size()){
            difference = Math.abs(allNames.size() - every.size());
        }

        while(difference != 0){
            if(allNames.size() >= every.size()){
                every.add("");
            }else {
                allNames.add("");
            }
            difference--;
        }

        for(int i = 0; i < allNames.size(); i++){
            allNames.set(i, allNames.get(i) + ": " + every.get(i));
        }
        String all2 = "";
        for(int i = 0; i < allNames.size(); i++){
            all2 += allNames.get(i) + "\n\n";
        }
        return all2;
    }
}
