package com.snsdevelop.tusofia.sem6.pmu.Database.DAOs;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.Location;

@Dao
public interface LocationsDao {

    @Query("SELECT * from locations ORDER BY id ASC")
    List<Location> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Location word);
}
