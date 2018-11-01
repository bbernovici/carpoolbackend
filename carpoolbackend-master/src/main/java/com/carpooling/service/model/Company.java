package com.carpooling.service.model;

public class Company extends User {
    private String id;
    private String name;
    private String mail;
    private String password;
    private String token;
    private String locationLatitude;
    private String locationLongitude;

    public Company(String id, String name, String mail, String password, String token, String locationLatitude, String locationLongitude) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.token = token;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
    }

    public Company() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(String locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public String getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(String locationLongitude) {
        this.locationLongitude = locationLongitude;
    }
}
