package com.snsdevelop.tusofia.sem6.pmu.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.snsdevelop.tusofia.sem6.pmu.R;

public class PlayAudioService extends Service implements MediaPlayer.OnErrorListener {
    private final IBinder mBinder = new ServiceBinder();
    MediaPlayer mPlayer;
    private int length = 0;

    public PlayAudioService() {
    }

    public class ServiceBinder extends Binder {
        public PlayAudioService getService() {
            return PlayAudioService.this;
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createPlayer();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mPlayer != null)
            mPlayer.start();
        return START_STICKY;
    }

    public void pauseMusic() {
        if (mPlayer == null)
            createPlayer();

        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            length = mPlayer.getCurrentPosition();

        }
    }

    public void resumeMusic() {
        if (mPlayer == null)
            createPlayer();

        if (!mPlayer.isPlaying()) {
            mPlayer.seekTo(length);
            mPlayer.start();
        }
    }

    public void stopMusic() {
        if (mPlayer == null)
            createPlayer();

        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }
        mPlayer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
                Log.d("Audio", "mPlayer is NULL");
            }
        }
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {

        Toast.makeText(this, "music player failed", Toast.LENGTH_SHORT).show();
        if (mPlayer != null) {
            try {
                mPlayer.stop();
                mPlayer.release();
            } finally {
                mPlayer = null;
            }
        }
        return false;
    }

    private void createPlayer() {
        mPlayer = MediaPlayer.create(this, R.raw.background_music);
        mPlayer.setOnErrorListener(this);

        if (mPlayer != null) {
            mPlayer.setLooping(true);
            mPlayer.setVolume(100, 100);
        }


        mPlayer.setOnErrorListener((mp, what, extra) -> {
//                onError(mPlayer, what, extra);
            return true;
        });
    }
}
