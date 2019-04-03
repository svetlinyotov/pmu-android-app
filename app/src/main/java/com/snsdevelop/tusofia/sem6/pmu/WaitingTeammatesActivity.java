package com.snsdevelop.tusofia.sem6.pmu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;

public class WaitingTeammatesActivity extends AppCompatActivity {

    public static final String TEAM_NAME_TO_DISPLAY_EXTRA = "team_name_extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_teammates);

        TextView textViewTitleNewTeamName = findViewById(R.id.textViewTitleNewTeamName);
        textViewTitleNewTeamName.setText(StoredData.getString(this, StoredData.GAME_NAME));

    }
}
