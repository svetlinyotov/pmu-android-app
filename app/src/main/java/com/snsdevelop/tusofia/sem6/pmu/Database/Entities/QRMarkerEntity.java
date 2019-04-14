package com.snsdevelop.tusofia.sem6.pmu.Database.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "QRMarkers")
public class QRMarkerEntity {

    @PrimaryKey
    private int id;
    private String locationId;
    private String title;
    private String QRcode;
    private String location_lat;
    private String location_lon;
    private boolean isFound;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQRcode() {
        return QRcode;
    }

    public void setQRcode(String QRcode) {
        this.QRcode = QRcode;
    }

    public String getLocation_lat() {
        return location_lat;
    }

    public void setLocation_lat(String location_lat) {
        this.location_lat = location_lat;
    }

    public String getLocation_lon() {
        return location_lon;
    }

    public void setLocation_lon(String location_lon) {
        this.location_lon = location_lon;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setFound(boolean found) {
        isFound = found;
    }
}
