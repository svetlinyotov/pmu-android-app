package com.snsdevelop.tusofia.sem6.pmu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

public class QuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.make(this, getString(R.string.error_going_back));
    }
}
