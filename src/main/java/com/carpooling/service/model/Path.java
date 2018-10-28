package com.carpooling.service.model;

import java.util.ArrayList;

public class Path {

    private String id;
    private String driverId;
    private Long startingPickup;
    private ArrayList<String> members;
    private Integer hour;
    private Integer minute;

    public Path() {

    }

    public Path(String id, String driverId, Long startingPickup, ArrayList<String> members, Integer hour, Integer minute) {
        this.id = id;
        this.driverId = driverId;
        this.startingPickup = startingPickup;
        this.members = members;
        this.hour = hour;
        this.minute = minute;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public Long getStartingPickup() {
        return startingPickup;
    }

    public void setStartingPickup(Long startingPickup) {
        this.startingPickup = startingPickup;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }
}
