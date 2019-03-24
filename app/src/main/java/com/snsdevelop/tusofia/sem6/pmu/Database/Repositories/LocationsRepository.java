package com.snsdevelop.tusofia.sem6.pmu.Database.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import com.snsdevelop.tusofia.sem6.pmu.Database.DAOs.LocationsDao;
import com.snsdevelop.tusofia.sem6.pmu.Database.Database;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.Location;

public class LocationsRepository {

    private LocationsDao locationsDao;

    public LocationsRepository(Application application) {
        Database db = Database.getDatabase(application);
        locationsDao = db.locationsDao();
    }

    public List<Location> getAll() {
        return locationsDao.getAll();
    }


    public void insert(Location location) {
        new insertAsyncTask(locationsDao).execute(location);
    }

    private static class insertAsyncTask extends AsyncTask<Location, Void, Void> {

        private LocationsDao locationsDao;

        insertAsyncTask(LocationsDao dao) {
            locationsDao = dao;
        }

        @Override
        protected Void doInBackground(final Location... params) {
            locationsDao.insert(params[0]);
            return null;
        }
    }
}
