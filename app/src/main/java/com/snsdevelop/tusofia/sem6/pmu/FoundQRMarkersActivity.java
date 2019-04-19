package com.snsdevelop.tusofia.sem6.pmu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snsdevelop.tusofia.sem6.pmu.Adapters.FoundMarkersAdapter;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.QRMarkerEntity;
import com.snsdevelop.tusofia.sem6.pmu.Helpers.Entities.GameStatusEntity;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FoundQRMarkersActivity extends AppCompatActivity {

    private FoundMarkersAdapter qrMarkerAdapter;
    List<QRMarkerEntity> qrMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_qrmarkers);
        ListView mListView = findViewById(R.id.lvQRMarkers);

        ImageButton buttonBack = findViewById(R.id.buttonBack);

        Request serverRequest = new Request(this);
        qrMarkerAdapter = new FoundMarkersAdapter(this, R.layout.qrmarkers_adapter_view);
        mListView.setAdapter(qrMarkerAdapter);

        buttonBack.setOnClickListener((v) -> finish());

        serverRequest.send(
                new RequestBuilder(Method.GET, URL.GAME_STATUS)
                        .setResponseListener(response -> {
                            GameStatusEntity gameStatus = new Gson().fromJson(response, new TypeToken<GameStatusEntity>() {
                            }.getType());

                            qrMarkerAdapter.clear();
                            qrMarkerAdapter.addAll(gameStatus.getFoundLocations());
                            qrMarkerAdapter.notifyDataSetChanged();

                        })
                        .setErrorListener(error -> {
                            Toast.make(this, getString(R.string.error_taking_marker_list));

                        })
                        .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                        .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                        .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                        .build(this));
    }
}
