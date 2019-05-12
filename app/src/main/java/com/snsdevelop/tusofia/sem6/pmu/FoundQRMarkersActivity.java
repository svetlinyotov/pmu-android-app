package com.snsdevelop.tusofia.sem6.pmu;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snsdevelop.tusofia.sem6.pmu.Adapters.FoundMarkersAdapter;
import com.snsdevelop.tusofia.sem6.pmu.Helpers.Entities.GameStatusEntity;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

public class FoundQRMarkersActivity extends AppCompatActivity {

    private FoundMarkersAdapter qrMarkerAdapter;
    private Request serverRequest;
    private SwipeRefreshLayout layoutSwipeQRMarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found_qrmarkers);
        ListView mListView = findViewById(R.id.lvQRMarkers);

        TextView textViewTitleQRMarkers = findViewById(R.id.textViewTitleQRMarkers);
        ImageButton buttonBack = findViewById(R.id.buttonBack);

        serverRequest = new Request(this);
        qrMarkerAdapter = new FoundMarkersAdapter(this, R.layout.qrmarkers_adapter_view);
        mListView.setAdapter(qrMarkerAdapter);

        textViewTitleQRMarkers.setText(getResources().getString(R.string.title_qrmarkers, StoredData.getInt(this, StoredData.FOUND_MARKERS), StoredData.getInt(this, StoredData.TOTAL_MARKERS)));

        buttonBack.setOnClickListener((v) -> finish());

        updateData();

        layoutSwipeQRMarkers = findViewById(R.id.layoutSwipeQRMarkers);
        layoutSwipeQRMarkers.setOnRefreshListener(this::updateData);
    }

    private void updateData() {
        serverRequest.send(
                new RequestBuilder(Method.GET, URL.GAME_STATUS, StoredData.getInt(this, StoredData.GAME_ID))
                        .setResponseListener(response -> {
                            GameStatusEntity gameStatus = new Gson().fromJson(response, new TypeToken<GameStatusEntity>() {
                            }.getType());

                            Log.d("QR", response);

                            qrMarkerAdapter.clear();
                            qrMarkerAdapter.addAll(gameStatus.getFoundLocations());
                            qrMarkerAdapter.notifyDataSetChanged();
                            layoutSwipeQRMarkers.setRefreshing(false);

                        })
                        .setErrorListener(error -> {
                            Toast.make(this, getString(R.string.error_taking_marker_list));

                            layoutSwipeQRMarkers.setRefreshing(false);
                        })
                        .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                        .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                        .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                        .build(this));
    }
}
