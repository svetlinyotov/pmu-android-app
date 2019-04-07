package com.snsdevelop.tusofia.sem6.pmu;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pusher.client.channel.SubscriptionEventListener;
import com.snsdevelop.tusofia.sem6.pmu.Pusher.PusherConnection;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.AlertDialog;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.GameStatus;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import java.util.HashMap;
import java.util.Map;

import static com.snsdevelop.tusofia.sem6.pmu.Utils.PermissionCheck.LOCATION_PERMISSION_REQUEST_CODE;

public class GameMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Request serverRequest;
    private PusherConnection pusherConnection;
    private Map<String, Marker> usersMarkers;
    private FusedLocationProviderClient fusedLocationClient;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null) {

                Location lastLocation = locationResult.getLastLocation();

                sendLocation(lastLocation);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_map);

        String currentUserId = StoredData.getString(this, StoredData.LOGGED_USER_ID);
        ImageButton buttonGiveUp = findViewById(R.id.buttonGiveUp);

        serverRequest = new Request(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapInGameMode);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        buttonGiveUp.setOnClickListener((v) ->
                AlertDialog.styled(this, new AlertDialog(this).getBuilder()
                                .setTitle(getString(R.string.are_you_sure))
                                .setPositiveButton(getString(R.string.answer_yes), (dialogInterface, which) -> {
                                    StoredData.saveString(this, StoredData.GAME_STATUS, String.valueOf(GameStatus.FINISHED));
                                    startActivity(new Intent(this, LocationsActivity.class));
                                        })
                                .setNegativeButton(getString(R.string.answer_no), (dialogInterface, which) -> dialogInterface.cancel())
                                .create()));

        usersMarkers = new HashMap<>();

        pusherConnection = new PusherConnection(this);

        pusherConnection.bindChannelWithEvents(
                PusherConnection.formatChannelName(PusherConnection.CHANNEL_USER_LOCATIONS, StoredData.getInt(this, StoredData.GAME_ID)),
                new HashMap<String, SubscriptionEventListener>() {{
                    put(PusherConnection.EVENT_USER_LOCATION, (String channelName, String eventName, final String data) -> {
                        JsonObject info = new Gson().fromJson(data, JsonObject.class);

                        String userId = info.get("userId").getAsString();
                        String userNames = info.get("userNames").getAsString();
                        double latitude = info.get("latitude").getAsDouble();
                        double longitude = info.get("longitude").getAsDouble();

                        runOnUiThread(() -> {
                            if (mMap != null) {
                                if (!userId.equals(currentUserId)) {
                                    if (!usersMarkers.containsKey(userId)) {
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .title(userNames)
                                                .snippet(userNames)
                                                .position(new LatLng(latitude, longitude))
                                                .visible(true)
                                        );

                                        marker.showInfoWindow();

                                        usersMarkers.put(userId, marker);
                                    } else {
                                        Marker m = usersMarkers.get(userId);
                                        m.setPosition(new LatLng(latitude, longitude));
                                        m.showInfoWindow();
                                    }
                                }
                            }

                        });
                    });
                }}
        );

        pusherConnection.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        serverRequest.stop();
        pusherConnection.disconnect();

        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocationIfPermitted();

    }

    @Override
    public void onBackPressed(){
        Toast.make(this, getString(R.string.error_going_back));
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            fusedLocationClient.requestLocationUpdates(
                    new LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).setInterval(10),
                    locationCallback,
                    null
            );

            mMap.setMyLocationEnabled(true);
        }
    }

    private void sendLocation(Location location){
        serverRequest.send(
                new RequestBuilder(Method.POST, URL.GAME_LOCATION)
                        .addParam("gameId", String.valueOf(StoredData.getInt(this, StoredData.GAME_ID)))
                        .addParam("latitude", String.valueOf(location.getLatitude()))
                        .addParam("longitude",String.valueOf(location.getLongitude()))
                        .setErrorListener(error -> Log.d("GameMapActivity", getString(R.string.error_failed_send_location_to_server)))
                        .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                        .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                        .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                        .build(this)
        );
    }
}
