package com.snsdevelop.tusofia.sem6.pmu;

import android.os.Bundle;
import android.widget.ImageButton;

import com.snsdevelop.tusofia.sem6.pmu.Fragments.SettingsFragment;
import com.snsdevelop.tusofia.sem6.pmu.Helpers.Auth;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentSettings, new SettingsFragment())
                .commit();

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        ImageButton buttonLogOut = findViewById(R.id.buttonLogOut);

        buttonBack.setOnClickListener((v) -> finish());
        buttonLogOut.setOnClickListener((v) -> Auth.logOut(this));

    }

}
