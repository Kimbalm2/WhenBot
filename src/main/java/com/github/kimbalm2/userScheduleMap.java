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

    public void updateUserSchedule(String id, Schedule newDaySchedule){
        Schedule oldSchedule = userMap.get(id);
        for (String day: newDaySchedule.strArray) {
            if (newDaySchedule.getTimes(day).size() > 0){
                oldSchedule.replaceTimes(day,newDaySchedule.getTimes(day));
            }
        }
        userMap.replace(id,oldSchedule);//NOTE: this may not be necessary due to the pointer to userMap(id) = oldSchedule.
    }
}
