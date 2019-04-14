package com.snsdevelop.tusofia.sem6.pmu.Database.Repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.snsdevelop.tusofia.sem6.pmu.Database.DAOs.QRMarkersDao;
import com.snsdevelop.tusofia.sem6.pmu.Database.Database;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.QRMarkerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QRMarkersRepository {

    private QRMarkersDao qrMarkersDao;

    public QRMarkersRepository(Application application) {
        Database db = Database.getDatabase(application);
        qrMarkersDao = db.qrMarkersDao();
    }

    public List<QRMarkerEntity> getAll() {
        try {
            return new getAsyncTask(qrMarkersDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            return new ArrayList<>();
        }

    }

    public List<QRMarkerEntity> getMarker(String result){
        try {
            return new getAsyncTask(qrMarkersDao).execute().get();
        } catch (ExecutionException | InterruptedException e) {
            return new ArrayList<>();
        }
    }

    public void insert(QRMarkerEntity qrMarkerEntity) {
        new insertAsyncTask(qrMarkersDao).execute(qrMarkerEntity);
    }

    public void deleteAll() {
        new deleteAllAsyncTask(qrMarkersDao).execute();
    }

    private static class getAsyncTask extends AsyncTask<Void, Void, List<QRMarkerEntity>> {

        private QRMarkersDao qrMarkersDao;

        getAsyncTask(QRMarkersDao dao) {
            qrMarkersDao = dao;
        }

        @Override
        protected List<QRMarkerEntity> doInBackground(final Void... params) {
            return qrMarkersDao.getAll();
        }
    }

    private static class insertAsyncTask extends AsyncTask<QRMarkerEntity, Void, Void> {

        private QRMarkersDao qrMarkersDao;

        insertAsyncTask(QRMarkersDao dao) {
            qrMarkersDao = dao;
        }

        @Override
        protected Void doInBackground(final QRMarkerEntity... params) {
            qrMarkersDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {

        private QRMarkersDao qrMarkersDao;

        deleteAllAsyncTask(QRMarkersDao dao) {
            qrMarkersDao = dao;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            qrMarkersDao.deleteAll();
            return null;
        }
    }
}
