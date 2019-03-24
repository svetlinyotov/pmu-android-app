package com.snsdevelop.tusofia.sem6.pmu.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.snsdevelop.tusofia.sem6.pmu.R;

public class PlayAudioService extends Service {
    private static final String TAG = "PlayAudioService";
    MediaPlayer objPlayer;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service Started!");
        objPlayer = MediaPlayer.create(this, R.raw.background_music);
        objPlayer.setLooping(true);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        objPlayer.start();
        Log.d(TAG, "Media Player started!");
        if (!objPlayer.isLooping()) {
            Log.d(TAG, "Problem in Playing Audio");
        }
        return Service.START_NOT_STICKY;
    }

    public void onStop() {
        objPlayer.stop();
        objPlayer.release();
    }

    public void onPause() {
        objPlayer.stop();
        objPlayer.release();
    }

    public void onDestroy() {
        objPlayer.stop();
        objPlayer.release();
    }

    @Override
    public IBinder onBind(Intent objIndent) {
        return null;
    }

}
