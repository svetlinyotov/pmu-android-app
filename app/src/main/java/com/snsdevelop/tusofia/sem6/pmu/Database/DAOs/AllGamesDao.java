package com.snsdevelop.tusofia.sem6.pmu.Database.DAOs;

import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.AllGamesEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface AllGamesDao {

    @Query("SELECT * from games ORDER BY total DESC")
    List<AllGamesEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AllGamesEntity word);

    @Query("DELETE FROM games")
    void deleteAll();
}
