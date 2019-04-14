package com.snsdevelop.tusofia.sem6.pmu.Helpers.Entities;

import java.util.List;

public class LocationWithMarkers {

    private int id;
    private String name;
    private String description;
    private Double latitude;
    private Double longitude;
    private List<Marker> markers;

    public LocationWithMarkers(int id, String name, String description, Double latitude, Double longitude, List<Marker> markers) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.markers = markers;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public class Marker {
        private int id;
        private int locationId;
        private String name;
        private String photo;
        private String qrCode;
        private String description;
        private int points;
        private Double latitude;
        private Double longitude;

        public Marker(int id, int locationId, String name, String photo, String qrCode, String description, int points, Double latitude, Double longitude) {
            this.id = id;
            this.locationId = locationId;
            this.name = name;
            this.photo = photo;
            this.qrCode = qrCode;
            this.description = description;
            this.points = points;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public int getId() {
            return id;
        }

        public int getLocationId() {
            return locationId;
        }

        public String getName() {
            return name;
        }

        public String getPhoto() {
            return photo;
        }

        public String getQrCode() {
            return qrCode;
        }

        public String getDescription() {
            return description;
        }

        public int getPoints() {
            return points;
        }

        public Double getLatitude() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }
    }
}


