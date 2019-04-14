package com.snsdevelop.tusofia.sem6.pmu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

public class FoundQRMarkersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_qrmarkers);

        ImageButton buttonBack = findViewById(R.id.buttonBack);

        buttonBack.setOnClickListener((v) -> finish());
    }
}
