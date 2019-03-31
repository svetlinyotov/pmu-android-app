package com.snsdevelop.tusofia.sem6.pmu.Database.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.snsdevelop.tusofia.sem6.pmu.Database.DAOs.RankingDao;
import com.snsdevelop.tusofia.sem6.pmu.Database.Database;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.RankEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RankingRepository {

    private RankingDao rankingDao;

    public RankingRepository(Application application) {
        Database db = Database.getDatabase(application);
        rankingDao = db.rankingDao();
    }

    public List<RankEntity> getAll() {
        try {
            return new getAsyncTask(rankingDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            return new ArrayList<>();
        }

    }

    public void insert(RankEntity rankEntity) {
        new insertAsyncTask(rankingDao).execute(rankEntity);
    }

    public void deleteAll() {
        new deleteAllAsyncTask(rankingDao).execute();
    }

    private static class getAsyncTask extends AsyncTask<Void, Void, List<RankEntity>> {

        private RankingDao rankingDao;

        getAsyncTask(RankingDao dao) {
            rankingDao = dao;
        }

        @Override
        protected List<RankEntity> doInBackground(final Void... params) {
            return rankingDao.getAll();
        }
    }

    private static class insertAsyncTask extends AsyncTask<RankEntity, Void, Void> {

        private RankingDao rankingDao;

        insertAsyncTask(RankingDao dao) {
            rankingDao = dao;
        }

        @Override
        protected Void doInBackground(final RankEntity... params) {
            rankingDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {

        private RankingDao rankingDao;

        deleteAllAsyncTask(RankingDao dao) {
            rankingDao = dao;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            rankingDao.deleteAll();
            return null;
        }
    }
}
