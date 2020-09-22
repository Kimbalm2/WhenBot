package com.github.kimbalm2;

import java.util.HashMap;

public class userScheduleMap {
    private HashMap<String,Schedule> userMap;

    public userScheduleMap(){
        userMap = new HashMap<>();
    }

    public void adduser (String id, Schedule schedule){
        userMap.put(id,schedule);
    }

    public Schedule getUserSchedule (String id){
        return userMap.get(id);
    }

    public boolean contains (String id){
        return userMap.containsKey(id);
    }

    public void updateUserSchedule(String id, Schedule schedule){
        userMap.replace(id,schedule);
    }
}
