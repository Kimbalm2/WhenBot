package com.github.kimbalm2;

import org.javacord.api.event.message.MessageCreateEvent;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/*
* Days of the week are numbered starting with Sunday = 0, ending with Saturday = 6
* */
public class Schedule {
    public final String[] strArray = {"MON","TUE","WED","THUR","FRI", "SAT", "SUN"};
    private final static HashMap<String,Integer> dayMap = new HashMap<>();//maps the weekday strings to an index in the week arrayList
    private final ArrayList<String>[] week = new ArrayList[7]; //array of array lists uses dayMap to index them in


    public Schedule(){
        for (int i =0; i < 7; i++) {
            week[i] = new ArrayList<>();
        }
        //Set each day string to an index number for the week list.
        //week[dayMap.get("DAY")] gets the list of times for DAY
        for (int i = 0; i < 7; i++){
            dayMap.put(strArray[i],i);
        }
    }
    //used by the intersection algorithm
    public void insert(String day, String time){
        if(isValid(time)) {
            week[dayMap.get(day)].add(time);
        }
    }
    //used to store new times in new schedules or add times to an existing schedule
    public void insert (String day, String time, MessageCreateEvent event){
        if(isValid(time)) {
            week[dayMap.get(day)].add(time);
        }
        else{
            event.getChannel().sendMessage("Invalid format for the following time: " + time +
                    "\n24 hour time is required and must be defined like the following hh:mm-hh:mm\n" +
                    "Please correct the error and use !addTimes to add them to your schedule.");

        }
    }

    public void remove (String day, String time){
        if(week[dayMap.get(day)].contains(time)) {
            week[dayMap.get(day)].remove(time);
        }
    }

    public ArrayList<String> getTimes(String day){
        return week[dayMap.get(day)];
    }

    public void replaceTimes (String day, ArrayList<String> times){

        week[dayMap.get(day)] = times;
        sortSchedule();
    }

    //TODO: store to JSON file? Need to solve persistent calendar storage issue.
    public StringBuffer printSchedule(){
        StringBuffer message = new StringBuffer();
        for (int i = 0; i < 7; i++) {
            message.append(strArray[i]);
            message.append(": ");
            for (String time : week[i]) {
                message.append(time);
                message.append(" ");
            }
            message.append('\n');
        }
        return message;
    }

    public void sortSchedule(){
        for (String day: strArray) {
            Collections.sort(week[dayMap.get(day)]);
        }
    }
    //TODO: implement a time checker
    public boolean isValid (String time){
        try {
            LocalTime.parse(time);
            return true;
        } catch (DateTimeParseException | NullPointerException e) {
            System.out.println("Invalid time string: " + time);
            return false;
        }
    }


}
