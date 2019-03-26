package com.snsdevelop.tusofia.sem6.pmu.Database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.snsdevelop.tusofia.sem6.pmu.Database.DAOs.LocationsDao;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.LocationEntity;

@androidx.room.Database(entities = {LocationEntity.class}, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {
    public abstract LocationsDao locationsDao();

    private static volatile Database INSTANCE;

    public static Database getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            Database.class, "time_travellers_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
