package com.johnsyard.monashfriendfinder.entities;

/**
 * This is the Location entity
 * Created by xuanzhang on 28/04/2017.
 */

public class Location {
    private Integer locationId;
    private String locationName;
    private double latitude;
    private double longitude;
    private String dateTime;
    private Profile studentId;

    /**
     * Constructor
     */
    public Location(){

    }

    /**
     * Getters and setters
     */
    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Profile getStudentId() {
        return studentId;
    }

    public void setStudentId(Profile studentId) {
        this.studentId = studentId;
    }
}
