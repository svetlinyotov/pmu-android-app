package com.snsdevelop.tusofia.sem6.pmu;

import android.content.Intent;
import android.os.Bundle;

import com.snsdevelop.tusofia.sem6.pmu.Helpers.BackgroundMusic;
import com.snsdevelop.tusofia.sem6.pmu.services.PlayAudioService;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BackgroundMusic.doBindService(this);
        startService(new Intent(this, PlayAudioService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BackgroundMusic.doUnbindService(this);
    }
}
