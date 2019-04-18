package com.snsdevelop.tusofia.sem6.pmu.Helpers.Entities;

import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.QRMarkerEntity;

import java.util.List;

public class GameStatusEntity {
    private String name;
    private int totalMarkers;
    private int foundMarkers;
    private int totalScore;
    private List<QRMarkerEntity> foundLocations;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalMarkers() {
        return totalMarkers;
    }

    public void setTotalMarkers(int totalMarkers) {
        this.totalMarkers = totalMarkers;
    }

    public int getFoundMarkers() {
        return foundMarkers;
    }

    public void setFoundMarkers(int foundMarkers) {
        this.foundMarkers = foundMarkers;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public List<QRMarkerEntity> getFoundLocations() {
        return foundLocations;
    }

    public void setFoundLocations(List<QRMarkerEntity> foundLocations) {
        this.foundLocations = foundLocations;
    }


}
