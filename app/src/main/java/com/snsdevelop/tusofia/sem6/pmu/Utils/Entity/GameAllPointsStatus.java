package com.snsdevelop.tusofia.sem6.pmu.Utils.Entity;

public class GameAllPointsStatus {

    private String timePlay;
    private int foundMarkers;

    public GameAllPointsStatus(String timePlay, int foundMarkers) {
        this.timePlay = timePlay;
        this.foundMarkers = foundMarkers;
    }

    public String getPlayTime() {
        return timePlay;
    }

    public int getFoundMarkers() {
        return foundMarkers;
    }
}
