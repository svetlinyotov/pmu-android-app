package com.snsdevelop.tusofia.sem6.pmu.Database.Entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "games")
public class AllGamesEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String userId;
    private String gameId;
    private String gameName;
    private String locationId;
    private String locationName;
    private String email;
    private String names;
    private Double pointsFromMarkers;
    private Double pointsFromCorrectAnswers;
    private Double total;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
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
