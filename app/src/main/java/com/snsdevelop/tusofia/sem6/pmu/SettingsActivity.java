package com.snsdevelop.tusofia.sem6.pmu;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.snsdevelop.tusofia.sem6.pmu.Fragments.SettingsFragment;
import com.snsdevelop.tusofia.sem6.pmu.Helpers.Auth;
import com.snsdevelop.tusofia.sem6.pmu.Utils.AlertDialog;

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
        Button buttonLogOut = findViewById(R.id.buttonLogout);

        buttonBack.setOnClickListener((v) -> finish());
        buttonLogOut.setOnClickListener((v) -> new AlertDialog(this).getBuilder()
                .setTitle("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialogInterface, i) -> Auth.logOut(this))
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel())
                .show()
        );

    }

}
