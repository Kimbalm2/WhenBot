package com.github.kimbalm2;
//https://javacord.org/wiki/getting-started/welcome/
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;


//Working in dev server https://discord.gg/ZrFYKa
//Client ID 745144168124252170
//https://discordapp.com/api/oauth2/authorize?client_id=745144168124252170&scope=bot&permissions=0

public class WhenBot {
    //key = discord userID
    private static userScheduleMap userSchedules = new userScheduleMap();
    private static final String[] strArray = {"MON","TUE","WED","THUR","FRI", "SAT", "SUN"};

    public static void main(String[] args) {
        String token = "";
        try {
            token = getToken();
        }
        catch (IOException e){
            System.out.println("The token file could not be read. Please contact discord bot admins for the token.");
            System.exit(0);
        }

        DiscordApi api = new DiscordApiBuilder().setToken(token).login().join();

        // TODO
        api.addMessageCreateListener(WhenBot::onMessageCreate);

        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if (event.getMessage().getContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!");
            }
        });

        // Print the invite url of your bot
        System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
    }

    //load token from file
    private static String getToken() throws IOException {
            File myFile = new File(WhenBot.class.getClassLoader().getResource("discordBots.txt").getPath());
            Scanner fileScan = new Scanner(myFile);
            while (fileScan.hasNextLine()){
                String Line = fileScan.nextLine();
                if(Line.startsWith("WhenBot")) return Line.split(",")[1];
            }
            return null;
    }

    //wrapper to command methods.
    private static void onMessageCreate(MessageCreateEvent event) {

        if (event.getMessage().getContent().startsWith("!when")) {
            execWhen(event,event.getMessage().getContent());
        }

        else if (event.getMessage().getContent().startsWith("!setSchedule")) {
            execSetSchedule(event, event.getMessage().getContent());
        }

        else if (event.getMessage().getContent().startsWith("!schedule")) {
            execSchedule(event, event.getMessage().getContent());

        }

        if (event.getMessage().getContent().startsWith("!update")) {
            execUpdate(event, event.getMessage().getContent());
        }
    }

    // Command: !When {user1}
    // Outputs all of the free times that the command executer and {user} share.
    private static void execWhen(MessageCreateEvent event, String content){ }

    // Command: !setSchedule
    // input format are ranges DAYhh:mm-hh:mm,hh:mm-hh:mm,etc
    // Prompts the user to create their weekly schedule
    private static void execSetSchedule(MessageCreateEvent event, String content){
        //MON-hh:mm-hh:mm,TUE-hh:mm-hh:mm,WED-hh:mm-hh:mm,THU-hh:mm-hh:mm,FRI-hh:mm-hh:mm,SAT-hh:mm-hh:mm,SUN-hh:mm-hh:mm
        Schedule tempSchedule = scheduleBuilder(content);
        userSchedules.adduser(event.getMessageAuthor().getDiscriminatedName(), tempSchedule);
        // TODO: https://javacord.org/wiki/basic-tutorials/using-the-messagebuilder/
        event.getChannel().sendMessage(event.getMessageAuthor().getDisplayName() + "'s free time schedule: \n" + tempSchedule.printSchedule());
    }

    //**!Schedule [user]**
    //Will output the input user's schedule. if no user is input it will output your schedule.
    private static void execSchedule(MessageCreateEvent event, String content){
        String msg;
        String args = getCommandParams(content);
        if(args != null){
            Collection<User> usersCollection = event.getApi().getCachedUsersByName(args);
            //need to change message id to username
            if(usersCollection.size() > 0) {
                for (User u : usersCollection) {
                    if(userSchedules.contains(u.getDiscriminatedName())) {
                        msg = scheduleMessageBuilder(u.getName(), userSchedules.getUserSchedule(u.getDiscriminatedName()));
                        event.getChannel().sendMessage(msg);
                    }
                    else {
                        event.getChannel().sendMessage(u.getName() + " Doesn't have a schedule set. Ask them to use the command !setSchedule.");
                    }
                }
            }
        }
        else {
            msg = scheduleMessageBuilder(event.getMessageAuthor().getDisplayName(),userSchedules.getUserSchedule(event.getMessageAuthor().getDiscriminatedName()));
            event.getChannel().sendMessage(msg);
        }
    }

    private static void execUpdate(MessageCreateEvent event, String content){
        Schedule tempSchedule = scheduleBuilder(content);
        String id = event.getMessageAuthor().getDiscriminatedName();
        if(!userSchedules.contains(event.getMessageAuthor().getDiscriminatedName())){
            userSchedules.adduser(id,tempSchedule);
        }
        else
        userSchedules.updateUserSchedule(id, tempSchedule);
        event.getChannel().sendMessage(event.getMessageAuthor().getDisplayName() + "'s updated free time schedule: \n" + tempSchedule.printSchedule());
    }

    //Builder to create a schedule out of a passed string from commands !update or !setSchedule
    private static Schedule scheduleBuilder (String content){
        Schedule tempSchedule = new Schedule();
        content = getCommandParams(content);
        String[] varList = content.split(",");
        String day = "";
        String time = "";
        for (String s : varList) {
            if(isDay(s.substring(0,3))){
                day = s.substring(0, 3);
                time = s.substring(4);
            }
            else {
                time = s;
            }
            tempSchedule.insert(day, time);
        }
        return tempSchedule;
    }

    private static String getCommandParams(String content){

        String[] args = content.split(" ");

        if(args.length > 1) return args[1];
        else return null;
    }

    public static boolean isDay(String str){
        for(String day : strArray){
            if (str.equals(day)) return true;
        }
        return false;
    }

    //TODO: add styling and review wording.
    private static String scheduleMessageBuilder(String userName, Schedule schedule){
        return (userName + "'s free time schedule:" + schedule.printSchedule());
    }




}
