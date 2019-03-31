package com.snsdevelop.tusofia.sem6.pmu.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.autofill.UserData;
import android.util.Log;

import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.LocationEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Repositories.LocationsRepository;
import com.snsdevelop.tusofia.sem6.pmu.MainActivity;
import com.snsdevelop.tusofia.sem6.pmu.R;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;

import java.util.List;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData.GAME_STATUS;

public class LocationBackgroundService extends Service {
    private static final String TAG = "LocationBackgroundService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 60000;
    private static final float LOCATION_DISTANCE = 10f;

    private static final int NEAR_LOCATION_NOTIFICATION_ID = 8969;
    private static final String NEAR_LOCATION_NOTIFICATION_CHANNEL_ID = "SET_USER_PASSWORD_NOTIFICATION";

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            LocationsRepository locationsRepository = new LocationsRepository(getApplication());
            List<LocationEntity> locations = locationsRepository.getAll();

            for (LocationEntity locationEntity : locations) {
                Location a = new Location("LocationListener: " + locationEntity.getName());
                a.setLatitude(locationEntity.getLatitude());
                a.setLongitude(locationEntity.getLongitude());
                String gameStatus = StoredData.getString(getApplicationContext(), GAME_STATUS);

                if (location.distanceTo(a) < 9000 && (gameStatus == null || gameStatus.equals("no_game"))) {

                    PendingIntent contentIntent =
                            PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), 0);

                    Notification notification = new NotificationCompat.Builder(getApplicationContext(), NEAR_LOCATION_NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.drawable.bg_flag)
                            .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                            .setOnlyAlertOnce(true)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(contentIntent)
                            .setContentTitle(getString(R.string.notification_near_location_title))
                            .setContentText(getString(R.string.notification_near_location_text))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(getString(R.string.notification_near_location_description)))
                            .build();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(NEAR_LOCATION_NOTIFICATION_CHANNEL_ID, NEAR_LOCATION_NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);

                        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                        if (notificationManager != null) {
                            notificationManager.createNotificationChannel(channel);
                            notificationManager.notify(NEAR_LOCATION_NOTIFICATION_ID, notification);
                        }
                    } else {
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        notificationManager.notify(NEAR_LOCATION_NOTIFICATION_ID, notification);
                    }


                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(NEAR_LOCATION_NOTIFICATION_CHANNEL_ID, NEAR_LOCATION_NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
                        NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                        if (notificationManager != null) {
                            notificationManager.createNotificationChannel(channel);
                            notificationManager.cancel(NEAR_LOCATION_NOTIFICATION_ID);
                        }
                    } else {
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                        notificationManager.cancel(NEAR_LOCATION_NOTIFICATION_ID);
                    }

                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }


    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
