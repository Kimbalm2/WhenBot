package com.github.kimbalm2;
//https://javacord.org/wiki/getting-started/welcome/
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

//Working in dev server https://discord.gg/ZrFYKa
//Client ID 745144168124252170
//https://discordapp.com/api/oauth2/authorize?client_id=745144168124252170&scope=bot&permissions=0

public class WhenBot {
    //key = discord userID
    private HashMap<String,Schedule> userSchedules = new HashMap();

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

    private static String getToken() throws IOException {
        // Insert your bot's token here
        String path = "resources/discordBots.txt";

            File myFile = new File(path);
            Scanner fileScan = new Scanner(myFile);
            while (fileScan.hasNextLine()){
                String Line = fileScan.nextLine();
                if(Line.startsWith("WhenBot")) return Line.split(",")[1];
            }
            return null;
    }

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

    private static void execWhen(MessageCreateEvent event, String content){ }
    private static void execSetSchedule(MessageCreateEvent event, String content){
        //probably just pass event to me
        event.getChannel().sendMessage("Please enter some times you are available for the following days in 24 hour time (e.g. 0:00-23:59 = 12:00AM - 11:59PM):");
    }
    private static void execSchedule(String content){ }
    private static void execUpdate(String content){}



}
