import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;

import java.io.*;

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
        HtmlAnchor gradeBook = (HtmlAnchor) page.getAnchorByText("Gradebook");
        page = gradeBook.click();
        String wholePage = page.getVisibleText();

        if(!wholePage.contains(courseNumber)){
            return null;
        }

        HtmlAnchor coursePage = (HtmlAnchor) page.getAnchorByText(courseNumber);
        page = coursePage.click();
        HtmlAnchor gradePage = (HtmlAnchor) page.getAnchorByText("Review grades for this class");
        page = gradePage.click();

        String gradePage2 = page.getBody().getVisibleText();
        String grade = gradePage2.substring(gradePage2.indexOf("Current"), gradePage2.indexOf("Period and Term Grade"));
        return grade;
    }

    public void close() throws IOException {
        HtmlAnchor closeButton = (HtmlAnchor) page.getAnchorByText("Logout");
        closeButton.click();
    }

    public String checkNextYear() throws IOException {
        HtmlAnchor change = (HtmlAnchor) page.getAnchorByText("change");
        page = change.click();
        HtmlInput input = page.getFirstByXPath("//*[@id=\"field_year3\"]");
        input.setValueAttribute("2022");
        HtmlSubmitInput button = (HtmlSubmitInput) page.getFirstByXPath("//*[@id=\"section_4\"]/tr[2]/td/input");
        page = button.click();
        HtmlAnchor courses = (HtmlAnchor) page.getAnchorByText("Student Course Choices");
        page = courses.click();
        String fullPage = page.getBody().getTextContent();
        String firstCut = fullPage.substring(fullPage.indexOf("Your counselor"), fullPage.indexOf("Copyright"));

        BufferedReader bufferedReader = new BufferedReader(new StringReader(firstCut));
        String line;
        String secondCut = "";
        while ((line = bufferedReader.readLine()) != null){
            if(!line.trim().isEmpty()) {
                secondCut = secondCut + line + "\n";
            }
        }
        String thirdCut = secondCut.substring(secondCut.indexOf("Your"), secondCut.indexOf("document.q"));
        String lastCut = thirdCut.replace(":", ":\n");
        return lastCut;
    }
}
