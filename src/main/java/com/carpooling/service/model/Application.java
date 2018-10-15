package com.carpooling.service.model;

public class Application {

    private String id;
    private String employeeId;
    private String companyId;
    private String type;
    private Double homeLatitude;
    private Double homeLongitude;
    private Integer vehicleSeats;

    public Application(String id, String employeeId, String companyId, String type, Double homeLatitude, Double homeLongitude, Integer vehicleSeats) {
        this.id = id;
        this.employeeId = employeeId;
        this.companyId = companyId;
        this.type = type;
        this.homeLatitude = homeLatitude;
        this.homeLongitude = homeLongitude;
        this.vehicleSeats = vehicleSeats;
    }

    public Application() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getHomeLatitude() {
        return homeLatitude;
    }

    public void setHomeLatitude(Double homeLatitude) {
        this.homeLatitude = homeLatitude;
    }

    public Double getHomeLongitude() {
        return homeLongitude;
    }

    public void setHomeLongitude(Double homeLongitude) {
        this.homeLongitude = homeLongitude;
    }

    public Integer getVehicleSeats() {
        return vehicleSeats;
    }

    public void setVehicleSeats(Integer vehicleSeats) {
        this.vehicleSeats = vehicleSeats;
    }
}
