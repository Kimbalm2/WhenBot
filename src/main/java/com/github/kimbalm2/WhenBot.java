package com.github.kimbalm2;
//https://javacord.org/wiki/getting-started/welcome/
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.IOException;
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
            execSchedule(event.getMessage().getContent());

        }

        if (event.getMessage().getContent().startsWith("!update")) {
            execUpdate(event.getMessage().getContent());
        }
    }
    // Command: !When {user1}
    // Outputs all of the free times that the command executer and {user} share.
    private static void execWhen(MessageCreateEvent event, String content){ }

    // Command: !setSchedule [{time1},{time2},....]
    // input format are ranges DAYhh:mm-hh:mm,hh:mm-hh:mm,etc
    // Prompts the user to create their weekly schedule

    private static void execSetSchedule(MessageCreateEvent event, String content){
        //MON-hh:mm-hh:mm,TUE-hh:mm-hh:mm,WED-hh:mm-hh:mm,THU-hh:mm-hh:mm,FRI-hh:mm-hh:mm,SAT-hh:mm-hh:mm,SUN-hh:mm-hh:mm
        //probably just pass event to me

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
        userSchedules.adduser(event.getMessageAuthor().getIdAsString(), tempSchedule);
        // TODO: https://javacord.org/wiki/basic-tutorials/using-the-messagebuilder/
        event.getChannel().sendMessage(event.getMessageAuthor().getDisplayName() + "'s free time schedule: \n" + tempSchedule.printSchedule());
    }
    private static void execSchedule(String content){ }
    private static void execUpdate(String content){}
    private static String getCommandParams(String content){
        return content.split(" ")[1];
    }
    public static boolean isDay(String str){
        for(String day : strArray){
            if (str.equals(day)) return true;
        }
        return false;
    }




}
