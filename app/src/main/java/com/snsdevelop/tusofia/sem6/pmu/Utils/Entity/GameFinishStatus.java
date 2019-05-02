package com.snsdevelop.tusofia.sem6.pmu.Utils.Entity;

public class GameFinishStatus {

    private int markersFound;
    private int correctAnswers;

    public GameFinishStatus(int markersFound, int correctAnswers) {
        this.markersFound = markersFound;
        this.correctAnswers = correctAnswers;
    }

    public int getMarkersFound() {
        return markersFound;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }
}
