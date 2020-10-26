package com.github.kimbalm2;

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

    public void insert (String day, String time){
        if(isValid(time))
        week[dayMap.get(day)].add(time);
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
    private boolean isValid (String time){
        return true;
    }


}
