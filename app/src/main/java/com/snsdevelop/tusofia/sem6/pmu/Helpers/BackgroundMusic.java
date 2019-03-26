package com.snsdevelop.tusofia.sem6.pmu.Helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.services.PlayAudioService;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;

public class BackgroundMusic {
    private static WeakReference<Context> contextWeakReference;
    private static boolean mIsBound = false;
    private static PlayAudioService mService;
    private static ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName name, IBinder binder) {
            mService = ((PlayAudioService.ServiceBinder) binder).getService();

            if (contextWeakReference != null && !StoredData.getBoolean(contextWeakReference.get(), StoredData.SETTINGS_IS_BG_MUSIC_PLAYING)) {
                BackgroundMusic.stop();
            } else {
                BackgroundMusic.resume();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    public static void startService(@NonNull Context context) {
        context.startService(new Intent(context, PlayAudioService.class));
    }

    public static void doBindService(@NonNull Context context) {
        context.bindService(new Intent(context, PlayAudioService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        contextWeakReference = new WeakReference<>(context);
    }

    public static void doUnbindService(@NonNull Context context) {
        if (mIsBound) {
            context.unbindService(serviceConnection);
            mIsBound = false;
        }
    }

    public static void resume() {
        if (mService != null)
            mService.resumeMusic();
    }

    public static void pause() {
        if (mService != null)
            mService.pauseMusic();
    }

    private static void stop() {
        if (mService != null)
            mService.stopMusic();
    }
}
