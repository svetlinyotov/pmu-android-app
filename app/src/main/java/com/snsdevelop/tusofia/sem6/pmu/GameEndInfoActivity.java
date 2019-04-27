package com.snsdevelop.tusofia.sem6.pmu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.QRMarkerEntity;
import com.snsdevelop.tusofia.sem6.pmu.Helpers.Entities.GameStatusEntity;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.GameAllPointsStatus;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import java.util.Locale;

public class GameEndInfoActivity extends AppCompatActivity {

    private Request serverRequest;
    private RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end_info);

        progressBar = findViewById(R.id.layoutProgressBar);
        serverRequest = new Request(this);

        TextView textViewTimePlaying = findViewById(R.id.textViewTimePlaying);
        TextView textViewFoundMarkers = findViewById(R.id.textViewFoundMarkers);
        ImageButton buttonTest = findViewById(R.id.buttonTest);

        progressBar.setVisibility(View.VISIBLE);

        serverRequest.send(new RequestBuilder(Method.POST, URL.GAME_ALL_MARKERS_FOUND_INFO, StoredData.getInt(this, StoredData.GAME_ID))
                .setResponseListener(response -> {
                    GameAllPointsStatus gameAllPointsStatus = new Gson()
                            .fromJson(response, new TypeToken<GameAllPointsStatus>() {
                            }.getType());

                    textViewTimePlaying.setText(gameAllPointsStatus.getPlayTime());
                    textViewFoundMarkers.setText(String.format(Locale.ENGLISH, "%d", gameAllPointsStatus.getFoundMarkers()));
                    progressBar.setVisibility(View.GONE);
                })
                .setErrorListener(error -> {
                    Toast.make(this, getString(R.string.error_cannot_update_info));
                    progressBar.setVisibility(View.GONE);
                })
                .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                .build(this)
        );

        buttonTest.setOnClickListener((v) -> startActivity(new Intent(this, QuizActivity.class)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        serverRequest.stop();
    }

    @Override
    public void onBackPressed() {
        Toast.make(this, getString(R.string.error_going_back));
    }
}
