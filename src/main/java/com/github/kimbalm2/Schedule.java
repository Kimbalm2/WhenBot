package com.github.kimbalm2;

import java.util.ArrayList;
import java.util.HashMap;

/*
* Days of the week are numbered starting with Sunday = 0, ending with Saturday = 6
* */
public class Schedule {
    private String[] strArray = {"MON","TUE","WED","THUR","FRI", "SAT"};
    private static HashMap<String,Integer> dayMap;
    private ArrayList<String>[] week = new ArrayList[7];


    public Schedule(){
        for (ArrayList day: week) {
            day = new ArrayList();
        }
        for (int i = 0; i < 7; i++){
            dayMap.put(strArray[i],i);
        }
    }

    public void insert (String day, String time){
        week[dayMap.get(day)].add(time);
    }

    public void delete (String day, String time){
        week[dayMap.get(day)].remove(time);
    }
    //TODO: store to JSON file? Need to solve persistent storage issue.
    public void printSchedule(){
        for (int i = 0; i < 7; i++) {
            System.out.print(strArray[i] + ": ");
            for (String time : week[i]) {
                System.out.print(time + " ");
            }
            System.out.println();
        }
    }
}
