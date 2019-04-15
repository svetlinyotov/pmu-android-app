package com.snsdevelop.tusofia.sem6.pmu.Database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.snsdevelop.tusofia.sem6.pmu.Database.DAOs.AllGamesDao;
import com.snsdevelop.tusofia.sem6.pmu.Database.DAOs.LocationsDao;
import com.snsdevelop.tusofia.sem6.pmu.Database.DAOs.QRMarkersDao;
import com.snsdevelop.tusofia.sem6.pmu.Database.DAOs.RankingDao;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.AllGamesEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.LocationEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.QRMarkerEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.RankEntity;

@androidx.room.Database(entities = {LocationEntity.class, RankEntity.class, AllGamesEntity.class, QRMarkerEntity.class}, version = 7, exportSchema = false)

public abstract class Database extends RoomDatabase {
    public abstract LocationsDao locationsDao();

    public abstract RankingDao rankingDao();

    public abstract AllGamesDao allGamesDao();

    public abstract QRMarkersDao qrMarkersDao();

    private static volatile Database INSTANCE;

    public static Database getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            Database.class, "time_travellers_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
