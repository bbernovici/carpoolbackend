package com.carpooling.service.model;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

public class TimeRemaining {

    private String content;

    private DateTime arrivalTime;

    public TimeRemaining() {
    }

    public TimeRemaining(DateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String setContent(){
        return content;
    }
    public String getContent() {
        return Integer.toString(Minutes.minutesBetween(DateTime.now(), arrivalTime).getMinutes()) + " minutes remaining. Your carpool will arrive at " + arrivalTime;
    }
}
