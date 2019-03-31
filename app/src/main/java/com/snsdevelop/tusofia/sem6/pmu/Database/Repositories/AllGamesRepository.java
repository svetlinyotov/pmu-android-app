package com.snsdevelop.tusofia.sem6.pmu.Database.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.snsdevelop.tusofia.sem6.pmu.Database.DAOs.AllGamesDao;
import com.snsdevelop.tusofia.sem6.pmu.Database.Database;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.AllGamesEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AllGamesRepository {

    private AllGamesDao allGamesDao;

    public AllGamesRepository(Application application) {
        Database db = Database.getDatabase(application);
        allGamesDao = db.allGamesDao();
    }

    public List<AllGamesEntity> getAll() {
        try {
            return new getAsyncTask(allGamesDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            return new ArrayList<>();
        }

    }

    public void insert(AllGamesEntity allGamesEntity) {
        new insertAsyncTask(allGamesDao).execute(allGamesEntity);
    }

    public void deleteAll() {
        new deleteAllAsyncTask(allGamesDao).execute();
    }

    private static class getAsyncTask extends AsyncTask<Void, Void, List<AllGamesEntity>> {

        private AllGamesDao allGamesDao;

        getAsyncTask(AllGamesDao dao) {
            allGamesDao = dao;
        }

        @Override
        protected List<AllGamesEntity> doInBackground(final Void... params) {
            return allGamesDao.getAll();
        }
    }

    private static class insertAsyncTask extends AsyncTask<AllGamesEntity, Void, Void> {

        private AllGamesDao allGamesDao;

        insertAsyncTask(AllGamesDao dao) {
            allGamesDao = dao;
        }

        @Override
        protected Void doInBackground(final AllGamesEntity... params) {
            allGamesDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {

        private AllGamesDao allGamesDao;

        deleteAllAsyncTask(AllGamesDao dao) {
            allGamesDao = dao;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            allGamesDao.deleteAll();
            return null;
        }
    }
}
