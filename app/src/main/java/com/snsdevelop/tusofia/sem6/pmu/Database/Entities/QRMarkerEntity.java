package com.snsdevelop.tusofia.sem6.pmu.Database.Entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "QRMarkers",
        foreignKeys =
            @ForeignKey(entity = LocationEntity.class,
                parentColumns = "id",
                childColumns = "locationId",
                onDelete = CASCADE))
public class QRMarkerEntity {

    @PrimaryKey
    private int id;
    private int locationId;
    private String title;
    private String QRcode;
    private String photo;
    private String description;
    private Double location_lat;
    private Double location_lon;
    private boolean isFound;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLocation_lat() {
        return location_lat;
    }

    public void setLocation_lat(Double location_lat) {
        this.location_lat = location_lat;
    }

    public Double getLocation_lon() {
        return location_lon;
    }

    public void setLocation_lon(Double location_lon) {
        this.location_lon = location_lon;
    }

    public boolean isFound() {
        return isFound;
    }

    public void setFound(boolean found) {
        isFound = found;
    }
}
