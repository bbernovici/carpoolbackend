package com.carpooling.service.model;

public class Pickup {
    private String id;
    private String companyId;
    private String type;
    private Double latitude;
    private Double longitude;

    public Pickup() {
    }

    public Pickup(String id, String companyId, String type, Double latitude, Double longitude) {
        this.id = id;
        this.companyId = companyId;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
