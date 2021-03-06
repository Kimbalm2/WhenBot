package com.github.kimbalm2;
//https://javacord.org/wiki/getting-started/welcome/
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.MessageDecoration;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Scanner;


//Working in dev server https://discord.gg/ZrFYKa
//Client ID 745144168124252170
//https://discordapp.com/api/oauth2/authorize?client_id=745144168124252170&scope=bot&permissions=0

public class WhenBot {
    //key = discord userID
    private static userScheduleMap userSchedules = new userScheduleMap();
    private static final String[] strArray = {"MON","TUE","WED","THU","FRI", "SAT", "SUN"};

    public static void main(String[] args) {
        DiscordApi api;
        String token = "";
        try {
            token = getToken();
        }
        catch (IOException e){
            System.out.println("The token file could not be read. Please contact discord bot admins for the token.");
            System.exit(0);
        }

        try {
            api = new DiscordApiBuilder().setToken(token).login().join();
        }
        catch (Exception e){
            System.out.printf("ERROR: %s%n", e.getMessage());
            throw e;
        }
        // TODO
        api.addMessageCreateListener(WhenBot::onMessageCreate);
        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if (event.getMessage().getContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!");
            }
        });
        // Print the invite url of your bot
        //System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());
        System.out.println("Returned from the api");
    }

    //load token from file
    private static String getToken() throws IOException {
        String path = Objects.requireNonNull(WhenBot.class.getClassLoader().getResource("discordBots.txt")).getPath();
            File myFile = new File(path);
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
        else if (event.getMessage().getContent().startsWith("!help")) {
            execHelp(event);
        }


    }

    private static void execHelp(MessageCreateEvent event){
        new MessageBuilder()
                .append("When:", MessageDecoration.BOLD, MessageDecoration.UNDERLINE)
                .append("   This command will output all of the free times that users share.\n")
                .append("Usage:", MessageDecoration.BOLD, MessageDecoration.UNDERLINE)
                .append("   !when user1 user2 userX")
                .appendNewLine()
                .appendNewLine()
                .append("Set Schedule:", MessageDecoration.BOLD, MessageDecoration.UNDERLINE)
                .append("   Create your new weekly set of free times. Time format is 24 hour time. Days are shortened to the first three letters.\n")
                .append("Format:", MessageDecoration.BOLD, MessageDecoration.UNDERLINE)
                .append("   !setSchedule DAY HH:mm-HH:mm,HH:mm-HH:mm DAY HH:mm-HH:mm\n")
                .append("Usage:", MessageDecoration.BOLD, MessageDecoration.UNDERLINE)
                .append("   !setSchedule ")
                .append("MON 19:00-21:00,09:00-11:00 TUE 19:00-21:00 THU 19:00-21:00, SAT 09:00-10:00")
                .appendNewLine()
                .appendNewLine()
                .append("Update:", MessageDecoration.BOLD, MessageDecoration.UNDERLINE)
                .append("   Allows you to update the input day's set of free times. ")
                .append("Note this will overwrite the whole schedule for the days entered. ")
                .append("Use addTimes or removeTimes if you want to make small changes to a single day.\n")
                .append("Usage:",MessageDecoration.BOLD, MessageDecoration.UNDERLINE)
                .append("   !update WED-04:00-05:00 TUE-19:00-20:00")
                .appendNewLine()
                .appendNewLine()
                .append("Schedule:", MessageDecoration.BOLD, MessageDecoration.UNDERLINE)
                .append("   Outputs the user's set of free times. If no user is input it will display your schedule.\n")
                .append("Usage:",MessageDecoration.BOLD, MessageDecoration.UNDERLINE)
                .append("   !schedule userName")
                .appendNewLine()
                .appendNewLine()
                .append("Add Times:", MessageDecoration.BOLD, MessageDecoration.UNDERLINE)
                .append("   Add free times to a day.\n")
                .append("Usage:",MessageDecoration.BOLD, MessageDecoration.UNDERLINE)
                .append("   !addTimes SAT 09:00-12:00,17:00-19:00")
                .appendNewLine()
                .appendNewLine()
                .append("Remove Times:", MessageDecoration.BOLD, MessageDecoration.UNDERLINE)
                .append("   Remove free times from day.\n")
                .append("Usage:", MessageDecoration.BOLD, MessageDecoration.UNDERLINE)
                .append("   !removeTimes SAT 09:00-12:00,17:00-19:00")
                .appendNewLine()
                .appendNewLine()
                .send(event.getChannel());
    }

    //!removeTimes DAY hh:mm-hh:mm,hh:mm-hh:mm
    private static void execRemoveTimes(MessageCreateEvent event, String content) {
        String day;
        String discriminatedName;
        Schedule userSchedule;
        discriminatedName = event.getMessageAuthor().getDiscriminatedName();
        String[] args = getArgs(content);
        if (args != null){
            day = args[1];
            if (!isDay(day)){
                event.getChannel().sendMessage("Please include a valid day of the week. (MON, TUE, WED, THU, FRI, SAT, SUN)");
                return;
            }
            userSchedule = userSchedules.getUserSchedule(discriminatedName);
            if(userSchedule != null) {
                for (int i = 2; i < args.length; i++) {
                    userSchedule.remove(day, args[i]);
                }
                userSchedule.sortSchedule();
                userSchedules.updateUserSchedule(discriminatedName, userSchedule);
            }
            else{
                event.getChannel().sendMessage("Could not find a schedule, use setSchedule if this is your first time creating one.");
            }
        }
        else{
            event.getChannel().sendMessage("Please include the DAY and at least one time, for example: !removeTimes MON 09:00-10:00,13:00-14:00");
        }
    }
    //!addTimes DAY hh:mm-hh:mm,hh:mm-hh:mm
    private static void execAddTimes(MessageCreateEvent event, String content) {
        String day;
        String discriminatedName;
        Schedule userSchedule;
        discriminatedName = event.getMessageAuthor().getDiscriminatedName();
        String[] args = getArgs(content);
        if(args != null){
            day = args[1];
            if (!isDay(day)){
                event.getChannel().sendMessage("Please include a valid day of the week. (MON, TUE, WED, THU, FRI, SAT, SUN)");
                return;
            }
            userSchedule = userSchedules.getUserSchedule(discriminatedName);
            if(userSchedule != null) {
                for (int i = 2; i < args.length; i++) {
                    userSchedule.insert(day, args[i], event);
                }
                userSchedule.sortSchedule();
                userSchedules.updateUserSchedule(discriminatedName, userSchedule);
            }
            else{
                event.getChannel().sendMessage("Could not find a schedule, use setSchedule if this is your first time creating one.");
            }
        }
        else{
            event.getChannel().sendMessage("Please include the DAY and at least one time, for example:!addTimes MON 09:00-10:00,13:00-14:00");
        }

    }

    // Command: !When {user1}
    // Outputs all of the free times that the command invoker and {user} share.
    //get both user's schedules
    private static void execWhen(MessageCreateEvent event, String content){
        String[] userList;
        Schedule sameFreeTimes = null;
        String otherUserDisplayName;
        Schedule otherUserSchedule = new Schedule();//the user passed in as an argument to the !when command.
        Schedule superUserSchedule;
        Collection<User> usersCollection;
        String[] args;
        args = getArgs(content);


        if(!userSchedules.contains(event.getMessageAuthor().getDiscriminatedName())){
            event.getChannel().sendMessage("You do not have a free time schedule set. Please set a schedule before using this command.");
            return;
        }

        if (args != null) {
            userList = new String[args.length-1];
            for (int i = 1; i < args.length; i++) {
                userList[i-1] = args[i];
            }
        }
        else{
            event.getChannel().sendMessage("No user entered, please enter one or more users after the command e.g.'!when user1 user2 etc.'");
            return;
        }
        //userList = content.split(" ");
        //if we are only comparing with on other person.
        if(userList.length >= 1){
            for (String otherUser: userList) {
                usersCollection = event.getApi().getCachedUsersByName(otherUser);
                //get the other otherUser
                if(usersCollection.size() > 0) {
                    for (User u : usersCollection) {
                        otherUserDisplayName = u.getDisplayName(event.getServer().get());//TODO: fix warning
                        if(otherUserDisplayName.equals(otherUser) && userSchedules.contains(u.getDiscriminatedName())) {
                            otherUserSchedule = userSchedules.getUserSchedule(u.getDiscriminatedName());
                        }
                        else {
                            event.getChannel().sendMessage(otherUserDisplayName + " does not have a schedule set.");
                            return;
                        }
                    }
                }
                else{
                    event.getChannel().sendMessage("Can't find a user with the username " + otherUser);
                    return;
                }
                if(userSchedules.contains(event.getMessageAuthor().getDiscriminatedName()) && sameFreeTimes == null){
                    superUserSchedule = userSchedules.getUserSchedule(event.getMessageAuthor().getDiscriminatedName());
                }
                else if(sameFreeTimes != null){
                    superUserSchedule = sameFreeTimes;
                }
                else {
                    //TODO: send error message to server.
                    event.getChannel().sendMessage("You do not have a free time schedule set. Please set a schedule before using this command.");
                    return;
                }
                sameFreeTimes = findScheduleIntersection(superUserSchedule,otherUserSchedule);
                if(sameFreeTimes == null){
                    event.getChannel().sendMessage("The users do not share any free times.");
                }
            }
        }
        event.getChannel().sendMessage("The Users are all free at the following times this week:\n"
                                        + sameFreeTimes.printSchedule());
    }

    // Command: !setSchedule
    // input format are ranges DAY hh:mm-hh:mm,hh:mm-hh:mm,DAY hh:mm,hh:mm-hh:mm
    // Prompts the user to create their weekly schedule
    private static void execSetSchedule(MessageCreateEvent event, String content){
        //MON-hh:mm-hh:mmn TUE-hh:mm-hh:mm WED-hh:mm-hh:mm THU-hh:mm-hh:mm FRI-hh:mm-hh:mm SAT-hh:mm-hh:mm SUN-hh:mm-hh:mm
        Schedule tempSchedule = scheduleBuilder(content, event);
        if (tempSchedule == null){
            event.getChannel().sendMessage("Example Usage: !setSchedule MON 19:00-21:00,09:00-11:00 TUE 19:00-21:00");
            event.getChannel().sendMessage("Failed to create a Schedule please try again.");
            return;
        }
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
            else{
              event.getChannel().sendMessage("Could not find a user with the name " + user + " in the server list.");
            }
        }
        else {
            //output the super user scheduel
            if(userSchedules.getUserSchedule(event.getMessageAuthor().getDiscriminatedName()) != null) {
                msg = scheduleMessageBuilder(event.getMessageAuthor().getDisplayName(), userSchedules.getUserSchedule(event.getMessageAuthor().getDiscriminatedName()));
                event.getChannel().sendMessage(msg);
            }
            else{
                event.getChannel().sendMessage("You do not have a schedule set to set use the command !setSchedule.");
            }

        }
    }

    private static void execUpdate(MessageCreateEvent event, String content){
        Schedule tempSchedule = scheduleBuilder(content, event);
        if (tempSchedule == null){
            event.getChannel().sendMessage("Example Usage: !update MON 19:00-21:00,09:00-11:00");
            event.getChannel().sendMessage("Failed to create a Schedule please try again");
            return;
        }
        String id = event.getMessageAuthor().getDiscriminatedName();
        if(!userSchedules.contains(event.getMessageAuthor().getDiscriminatedName())){
            userSchedules.adduser(id,tempSchedule);
        }
        else {
            userSchedules.updateUserSchedule(id, tempSchedule);
        }
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
    private static Schedule scheduleBuilder (String content, MessageCreateEvent event){
        Schedule tempSchedule = new Schedule();
        String day = "";
        String time;
        String[] timeList;
        String[] args;
        args = getArgs(content);

        if (args == null){
            //incorrect parameters after !setSchedule.
            //Example Usage: !setSchedule MON 19:00-21:00,09:00-11:00 TUE 19:00-21:00
            event.getChannel().sendMessage("Incorrect or missing input after command");

            return null;
        }
        for(int i = 1; i < args.length; i++){
            if(!isDay(args[i]) && !isTimeList(args[i])){
                event.getChannel().sendMessage("Unknown command input: " + args[i]);

                return null;
            }
            if(isDay(args[i])){
                day = args[i];
            }
            else if (isTimeList(args[i])){
                timeList = args[i].split(",");
                for (String s : timeList){
                    time = s;
                    if(tempSchedule.isValid(time)){
                      tempSchedule.insert(day,time);
                    }
                    else{
                        event.getChannel().sendMessage("Error, invalid format for input time: " + time);
                        return null;
                    }
                }
            }
        }
        return tempSchedule;
    }

    private static String[] getArgs(String content) {
        content = content.trim();
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
        return (userName + "'s free time schedule:\n" + schedule.printSchedule());
    }

    public static boolean isTimeList(String time){
        return time.matches("(\\d{2}:\\d{2}-\\d{2}:\\d{2})(,\\d{2}:\\d{2}-\\d{2}:\\d{2})*");
    }




}
