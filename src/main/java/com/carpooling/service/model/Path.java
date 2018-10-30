package com.carpooling.service.model;

import java.util.ArrayList;

public class Path {

    private String id;
    private String driverFirstName;
    private String driverLastName;
    private String driverId;
    private Long startingPickup;
    private ArrayList<String> members;
    private Integer hour;
    private Integer minute;

    public Path() {

    }

    public Path(String id, String driverFirstName, String driverLastName, String driverId, Long startingPickup, ArrayList<String> members, Integer hour, Integer minute) {
        this.id = id;
        this.driverFirstName = driverFirstName;
        this.driverLastName = driverLastName;
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

    public String getDriverFirstName() {
        return driverFirstName;
    }

    public void setDriverFirstName(String driverFirstName) {
        this.driverFirstName = driverFirstName;
    }

    public String getDriverLastName() {
        return driverLastName;
    }

    public void setDriverLastName(String driverLastName) {
        this.driverLastName = driverLastName;
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
