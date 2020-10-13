package com.github.kimbalm2;
//https://javacord.org/wiki/getting-started/welcome/
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

        else if (event.getMessage().getContent().startsWith("!update")) {
            execUpdate(event, event.getMessage().getContent());
        }
        else if (event.getMessage().getContent().startsWith("!addTimes")) {
            execAddTimes(event, event.getMessage().getContent());
        }
        else if (event.getMessage().getContent().startsWith("!removeTimes")) {
            execRemoveTimes(event, event.getMessage().getContent());
        }

    }

    //!removeTimes DAY hh:mm-hh:mm,hh:mm-hhmm
    private static void execRemoveTimes(MessageCreateEvent event, String content) {
        String day;
        String discriminatedName;
        Schedule userSchedule;
        discriminatedName = event.getMessageAuthor().getDiscriminatedName();
        String[] args = getArgs(content);
        day = args[1];
        userSchedule = userSchedules.getUserSchedule(discriminatedName);
        for(int i = 2; i < args.length; i++){
            userSchedule.remove(day,args[i]);
        }
        userSchedule.sortSchedule();
        userSchedules.updateUserSchedule(discriminatedName, userSchedule);
    }
    //!addTimes DAY hh:mm-hh:mm,hh:mm-hh:mm
    private static void execAddTimes(MessageCreateEvent event, String content) {
        String day;
        String discriminatedName;
        Schedule userSchedule;
        discriminatedName = event.getMessageAuthor().getDiscriminatedName();
        String[] args = getArgs(content);
        day = args[1];
        userSchedule = userSchedules.getUserSchedule(discriminatedName);
        for(int i = 2; i < args.length; i++){
            userSchedule.insert(day,args[i]);
        }
        userSchedule.sortSchedule();
        userSchedules.updateUserSchedule(discriminatedName, userSchedule);
    }

    // Command: !When {user1}
    // Outputs all of the free times that the command executer and {user} share.
    //get both user's schedules

    private static void execWhen(MessageCreateEvent event, String content){
        String[] userList;
        String otherUser;
        String otherUserDisplayName;
        Schedule otherUserSchedule = new Schedule();//the user passed in as an argument to the !when command.
        Schedule superUserSchedule = new Schedule();
        Collection<User> usersCollection;
        String[] args;
        args = getArgs(content);
        if (args != null)
            content = args[1];
        else{
            event.getChannel().sendMessage("No user entered, please enter one or more users after the command e.g.'!when user1 user2 etc.'");
            return;
        }
        userList = content.split(" ");
        if(userList.length == 1){
            otherUser = userList[0];
            usersCollection = event.getApi().getCachedUsersByName(otherUser);
            //get the other otherUser
            if(usersCollection.size() > 0) {
                for (User u : usersCollection) {
                    otherUserDisplayName = u.getDisplayName(event.getServer().get());
                    if(otherUserDisplayName.equals(otherUser) && userSchedules.contains(otherUserDisplayName)) {
                        otherUserSchedule = userSchedules.getUserSchedule(u.getDiscriminatedName());
                    }
                    else {
                        //TODO: send error message to server
                        event.getChannel().sendMessage(otherUserDisplayName + " does not have a schedule set.");
                        return;
                    }
                }
            }
            else{
                //TODO: send error message to server
                event.getChannel().sendMessage("Can't find a user with the username " + otherUser);
                return;
            }
            if(userSchedules.contains(event.getMessageAuthor().getDiscriminatedName())){
                superUserSchedule = userSchedules.getUserSchedule(event.getMessageAuthor().getDiscriminatedName());
            }
            else {
                //TODO: send error message to server.
                event.getChannel().sendMessage("You do no have a free time schedule set. Please set a schedule before using this command.");
                return;
            }
            Schedule sameFreeTimes = findScheduleIntersection(superUserSchedule,otherUserSchedule);
            if(sameFreeTimes == null){
                event.getChannel().sendMessage("The two users do not share any free times.");
            }
            else{
                StringBuffer msg = new StringBuffer("User's " + event.getMessageAuthor().getDisplayName() + " and " + otherUser + " are both free at the following times this week:\n");
                msg.append(sameFreeTimes.printSchedule());
                event.getChannel().sendMessage(msg.toString());
            }
        }
        //TODO: handle multiple users?
    }

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
        String user;
        String[] args;

        args = getArgs(content);
        if(args != null){
            user = args[1];
            Collection<User> usersCollection = event.getApi().getCachedUsersByName(user);
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
        event.getChannel().sendMessage(event.getMessageAuthor().getDisplayName() + "'s updated free time schedule: \n" + userSchedules.getUserSchedule(id).printSchedule());
    }

    //for each day perform the following algorithm:
    //https://www.geeksforgeeks.org/find-intersection-of-intervals-given-by-two-lists/
    //Maintain two pointers i and j to traverse the two interval lists, arr1 and arr2 respectively.
    //Now, if arr1[i] has smallest endpoint, it can only intersect with arr2[j]. Similarly, if arr2[j] has smallest endpoint, it can only intersect with arr1[i]. If intersection occurs, find the intersecting segment.
    //[l, r] will be the intersecting segment iff l <= r, where l = max(arr1[i][0], arr2[j][0]) and r = min(arr1[i][1], arr2[j][1]).
    //Increment the i and j pointers accordingly to move ahead.
    private static Schedule findScheduleIntersection(Schedule s1, Schedule s2){
        timeInterval t1,t2;
        String[] s1Arr;
        String[] s2Arr;
        Schedule tempSchedule = new Schedule();
        boolean foundIntersection = false;
        for (String day: s1.strArray) {
            int i = 0;
            int j = 0;
            ArrayList<String> arr1 = s1.getTimes(day);
            ArrayList<String> arr2 = s2.getTimes(day);
            int n = arr1.size();
            int m = arr2.size();
            while (i < n && j < m)
            {
                s1Arr = arr1.get(i).split("-");
                s2Arr = arr2.get(j).split("-");
                t1 = new timeInterval(s1Arr[0],s1Arr[1]);
                t2 = new timeInterval(s2Arr[0],s2Arr[1]);
                if (t1.doesIntersect(t2)){
                    foundIntersection = true;
                    tempSchedule.insert(day,t1.getIntersection(t2).toString());
                }
                if (t1.end < t2.end)
                    i++;
                else
                    j++;
            }
        }
        if (foundIntersection)
            return tempSchedule;
        else
            return null;
    }

    //Builder to create a schedule out of a passed string from commands !update or !setSchedule
    private static Schedule scheduleBuilder (String content){
        Schedule tempSchedule = new Schedule();
        String day = "";
        String time = "";
        String[] varList;
        String[] args;

        args = getArgs(content);
        if (args != null)
        content = args[1];
        else{
            return null;
        }
        varList = content.split(",");

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
        tempSchedule.sortSchedule();
        return tempSchedule;
    }

    private static String[] getArgs(String content) {
        String[] args = content.split(" ");
        if (args.length > 1) {
           return args;
        } else {
            return null;//return null if only a command was passed.
        }
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
