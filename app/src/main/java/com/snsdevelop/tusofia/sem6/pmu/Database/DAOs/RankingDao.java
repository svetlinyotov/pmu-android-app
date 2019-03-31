package com.snsdevelop.tusofia.sem6.pmu.Database.DAOs;

import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.RankEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface RankingDao {

    @Query("SELECT * from ranking ORDER BY total DESC")
    List<RankEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(RankEntity word);

    @Query("DELETE FROM ranking")
    void deleteAll();
}
