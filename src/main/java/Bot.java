import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.IOException;

public class Bot extends ListenerAdapter {

    final static String token = "ODQ2NjIzMjIwOTU0MTY5Mzg0.YKyNUQ.KXHCUBEG3KEKC5TcU3PVe_tfwXc";
    private static int[] id = new int[1000];
    private static String[] usernameCollection = new String[1000];
    private static String[] passwordCollection = new String[1000];
    private static int count = 0;
    private static int checkedPeople = 0;

    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault(token).build();
        jda.addEventListener(new Bot());
    }

    public void onMessageReceived(MessageReceivedEvent e){
        if(e.getAuthor().isBot()){return;}
        Message msg = e.getMessage();

        if(msg.getContentRaw().equals("-command")){
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.blue);
            builder.setTitle("Commands");;
            builder.setDescription("use ``-login + usernamne + password`` to login\n\tfor example: ``-login john-smith abc12345``\n\nuse ``-list`` to see all your classes and your course number\n\nuse ``-check + course number`` to check your grades for the class\n for example: ``-check 123141-3``");
            e.getChannel().sendMessage(builder.build()).queue();
        }

        if(msg.getContentRaw().equals("-info")){
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.blue);
            builder.setTitle("Disclaimer");;
            builder.setDescription("``I do not keep your username and password``\n\n``Use at will``\n\n``Login message will be deleted after the message was sent so do not worry if anyone's gonna see your password``\n\n ``For your grades, private message will send to your dm, so no need to worry about your grade being exposed``");
            e.getChannel().sendMessage(builder.build()).queue();
        }
        //login, saves data
        if(msg.getContentRaw().startsWith("-login")){
            msg.getChannel().deleteMessageById(msg.getId()).queue();
            //check if user is in the database
            boolean inData = inData((int) msg.getAuthor().getIdLong());
            if(inData == true){
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                builder.setDescription("You've already logged in");
                msg.getChannel().sendMessage(builder.build()).queue();
                return;
            }

            //check if the format is right, if not message then return
            boolean enough = enough(msg.getContentRaw());
            if(enough == false){
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                builder.setDescription("Wrong Format");
                msg.getChannel().sendMessage(builder.build()).queue();
                return;
            }

            //retrieve username and password
            int firstSpace = msg.getContentRaw().indexOf(" ");
            int lastSpace = msg.getContentRaw().lastIndexOf(" ");
            String user = msg.getContentRaw().substring(firstSpace+1, lastSpace);
            String password = msg.getContentRaw().substring(lastSpace+1);

            //login
            try {
                SchoolMax schoolMax = new SchoolMax();
                String confirmation = schoolMax.login(user, password);
                if(confirmation.equals("SchoolMAX(TM)- Student Profile")){
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.green);
                    builder.setDescription("Login Successful!");
                    msg.getChannel().sendMessage(builder.build()).queue();
                    // save into database
                    id[count] = (int) msg.getAuthor().getIdLong();
                    usernameCollection[count] = user;
                    passwordCollection[count] = password;
                    count++;
                    schoolMax.close();
                }else{ //wrong user/password message
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.red);
                    builder.setDescription("Wrong username/password");
                    msg.getChannel().sendMessage(builder.build()).queue();
                }
            }catch (Exception exception){}
        }

        //check classes
        if(msg.getContentRaw().equals("-list")){
            //check if user is in the database, if not ask to login
            boolean inData = inData((int) msg.getAuthor().getIdLong());
            if(inData == false){
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                builder.setDescription("Please Log in");
                msg.getChannel().sendMessage(builder.build()).queue();
                return;
            }
            //retrieve id location in the id collection
            int location = idLocation((int) msg.getAuthor().getIdLong());
            if(location == -1){
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                builder.setDescription("Error");
                msg.getChannel().sendMessage(builder.build()).queue();
            }

            try {
                SchoolMax schoolMax = new SchoolMax();
                schoolMax.login(usernameCollection[location], passwordCollection[location]);
                String list = schoolMax.classes();
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Your Classes");
                builder.setColor(Color.blue);
                builder.setDescription(list);
                msg.getChannel().sendMessage(builder.build()).queue();
                schoolMax.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        if(msg.getContentRaw().startsWith("-check")){
            //check if user is in the database, if not ask to login
            boolean inData = inData((int) msg.getAuthor().getIdLong());
            if(inData == false){
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                builder.setDescription("Please Log in");
                msg.getChannel().sendMessage(builder.build()).queue();
                return;
            }
            //retrieve id location in the id collection
            int location = idLocation((int) msg.getAuthor().getIdLong());
            if(location == -1){
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                builder.setDescription("Error");
                msg.getChannel().sendMessage(builder.build()).queue();
            }

            try {
                SchoolMax schoolMax = new SchoolMax();
                schoolMax.login(usernameCollection[location], passwordCollection[location]);

                int firstSpace = msg.getContentRaw().indexOf(" ");
                String newMsg = msg.getContentRaw().substring(firstSpace+1);
                String list = schoolMax.grade(newMsg);
                if(list != null) {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setTitle("Your Grade");
                    builder.setColor(Color.blue);
                    builder.setDescription(list);
                    User user = e.getAuthor();
                    user.openPrivateChannel().queue((channel) -> {
                        channel.sendMessage(builder.build()).queue();
                    });
                }else {
                    EmbedBuilder builder = new EmbedBuilder();
                    builder.setColor(Color.red);
                    builder.setDescription("Class doesn't exist");
                    msg.getChannel().sendMessage(builder.build()).queue();

                }
                schoolMax.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            checkedPeople++;
            System.out.println(checkedPeople);
        }

        //erase data
        if(msg.getContentRaw().equals("-logout")){
            int tempid = (int) e.getAuthor().getIdLong();
            if(!inData(tempid)){
                EmbedBuilder builder = new EmbedBuilder();
                builder.setColor(Color.red);
                builder.setDescription("You need to login first");
                msg.getChannel().sendMessage(builder.build()).queue();
                return;
            }
            int location = idLocation(tempid);
            id[location] = 0;
            usernameCollection[location] = null;
            passwordCollection[location] = null;
            EmbedBuilder builder = new EmbedBuilder();
            builder.setColor(Color.green);
            builder.setDescription("Logout Successful!");
            msg.getChannel().sendMessage(builder.build()).queue();
        }


    }
    //to check the format is right for login
    private boolean enough(String message){
        int space = 0;
        for(int i = 0; i < message.length(); i++){
            if(String.valueOf(message.charAt(i)).equals(" ")){
                space++;
            }
        }
        if(space != 2)
            return false;
        return true;
    }
    //to check if the user is in the database
    private boolean inData(int idnumber){
        for(int i = 0; i < id.length; i++){
            if(id[i] == idnumber){
                return true;
            }
        }
        return false;
    }
    //return userid location
    private int idLocation(int idnumber){
        for(int i = 0; i < id.length; i++){
            if(id[i] == idnumber){
                return i;
            }
        }
        return -1;
    }
}


