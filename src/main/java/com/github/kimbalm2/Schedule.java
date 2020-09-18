package com.github.kimbalm2;

import java.util.ArrayList;
import java.util.HashMap;

/*
* Days of the week are numbered starting with Sunday = 0, ending with Saturday = 6
* */
public class Schedule {
    private String[] strArray = {"MON","TUE","WED","THUR","FRI", "SAT"};
    private static HashMap<String,Integer> dayMap;
    private ArrayList<String>[] week = new ArrayList[7]; //array of array lists


    public Schedule(){
        for (ArrayList day: week) {
            day = new ArrayList();
        }
        //Set each day string to an index number for the week list.
        //week[dayMap.get("DAY")] gets the list of times for DAY
        for (int i = 0; i < 7; i++){
            dayMap.put(strArray[i],i);
        }
    }

    public void insert (String day, String time){
        week[dayMap.get(day)].add(time);
    }
    //clear the day so !Update can add into the new day
    public void clearDay (String day){
        week[dayMap.get(day)] = new ArrayList<>();
    }

    public void delete (String day, String time){
        week[dayMap.get(day)].remove(time);
    }

    //TODO: store to JSON file? Need to solve persistent calendar storage issue.
    public StringBuffer printSchedule(){
        StringBuffer message = new StringBuffer();
        for (int i = 0; i < 7; i++) {
            message.append(strArray[i] + ": ");
            for (String time : week[i]) {
                message.append(time + " ");
            }
            message.append('\n');
        }
        return message;
    }
}
