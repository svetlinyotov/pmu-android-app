package com.snsdevelop.tusofia.sem6.pmu.Database.DAOs;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.QRMarkerEntity;

@Dao
public interface QRMarkersDao {
    @Query("SELECT * from QRMarkers ORDER BY locationId DESC, name DESC")
    List<QRMarkerEntity> getAll();

    @Query("SELECT * from QRMarkers WHERE QRcode = :result")
    List<QRMarkerEntity> getMarker(String result);

    @Query("UPDATE QRMarkers SET isFound = :isFound WHERE id = :markerId")
    void updateIsFound(boolean isFound, int markerId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(QRMarkerEntity word);

    @Query("DELETE FROM QRMarkers")
    void deleteAll();
}
