package com.snsdevelop.tusofia.sem6.pmu.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ranking")
public class RankEntity {

    @PrimaryKey
    @NonNull
    private String id;
    private String email;
    private String names;
    private Double pointsFromMarkers;
    private Double pointsFromCorrectAnswers;
    private Double total;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public Double getPointsFromMarkers() {
        return pointsFromMarkers;
    }

    public void setPointsFromMarkers(Double pointsFromMarkers) {
        this.pointsFromMarkers = pointsFromMarkers;
    }

    public Double getPointsFromCorrectAnswers() {
        return pointsFromCorrectAnswers;
    }

    public void setPointsFromCorrectAnswers(Double pointsFromCorrectAnswers) {
        this.pointsFromCorrectAnswers = pointsFromCorrectAnswers;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
