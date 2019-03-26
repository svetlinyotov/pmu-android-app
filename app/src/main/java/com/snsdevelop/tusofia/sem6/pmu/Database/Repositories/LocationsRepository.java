package com.snsdevelop.tusofia.sem6.pmu.Database.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.snsdevelop.tusofia.sem6.pmu.Database.DAOs.LocationsDao;
import com.snsdevelop.tusofia.sem6.pmu.Database.Database;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.LocationEntity;

public class LocationsRepository {

    private LocationsDao locationsDao;

    public LocationsRepository(Application application) {
        Database db = Database.getDatabase(application);
        locationsDao = db.locationsDao();
    }

    public List<LocationEntity> getAll() {
        try {
            return new getAsyncTask(locationsDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            return new ArrayList<>();
        }

    }


    public void insert(LocationEntity locationEntity) {
        new insertAsyncTask(locationsDao).execute(locationEntity);
    }

    private static class getAsyncTask extends AsyncTask<Void, Void, List<LocationEntity>> {

        private LocationsDao locationsDao;

        getAsyncTask(LocationsDao dao) {
            locationsDao = dao;
        }

        @Override
        protected List<LocationEntity> doInBackground(final Void... params) {
            return locationsDao.getAll();
        }
    }

    private static class insertAsyncTask extends AsyncTask<LocationEntity, Void, Void> {

        private LocationsDao locationsDao;

        insertAsyncTask(LocationsDao dao) {
            locationsDao = dao;
        }

        @Override
        protected Void doInBackground(final LocationEntity... params) {
            locationsDao.insert(params[0]);
            return null;
        }
    }
}
