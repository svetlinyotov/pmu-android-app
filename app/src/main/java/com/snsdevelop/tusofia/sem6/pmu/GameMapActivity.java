package com.snsdevelop.tusofia.sem6.pmu;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.ui.IconGenerator;
import com.pusher.client.channel.SubscriptionEventListener;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.QRMarkerEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.ViewModels.QRMarkersViewModel;
import com.snsdevelop.tusofia.sem6.pmu.Helpers.Entities.GameStatusEntity;
import com.snsdevelop.tusofia.sem6.pmu.Pusher.PusherConnection;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.AlertDialog;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.GameStatus;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snsdevelop.tusofia.sem6.pmu.Utils.PermissionCheck.LOCATION_PERMISSION_REQUEST_CODE;

public class GameMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static String FOUND_MARKER_NOTIFICATION_CHANNEL = "FOUND_MARKER_NOTIFICATION_CHANNEL";
    private final static int FOUND_MARKER_NOTIFICATION_ID = 1001;
    private GoogleMap mMap;
    private Request serverRequest;
    private PusherConnection pusherConnection;
    private Map<String, Marker> usersMarkers;
    private List<LatLng> markersOnMap;
    private FusedLocationProviderClient fusedLocationClient;
    private QRMarkersViewModel QRMarkersViewModel;
    private RelativeLayout mRelativeLayout;
    private PopupWindow mPopupWindow;
    private Context mContext;
    private TextView foundMarkers;
    private RelativeLayout progressBar;
    public static final int requestCode = 1;

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
        progressBar = findViewById(R.id.layoutProgressBar);
        QRMarkersViewModel = ViewModelProviders.of(this).get(QRMarkersViewModel.class);
        mRelativeLayout = findViewById(R.id.gameMap);
        mContext = getApplicationContext();

        ImageButton buttonGiveUp = findViewById(R.id.buttonGiveUp);
        ImageButton buttonCamera = findViewById(R.id.buttonCamera);
        foundMarkers = findViewById(R.id.tvMarkersFound);

        foundMarkers.setOnClickListener((v) -> startActivity(new Intent(this, FoundQRMarkersActivity.class)));

        buttonCamera.setOnClickListener((v) -> startActivityForResult(new Intent(this, QRCameraActivity.class), requestCode));

        serverRequest = new Request(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapInGameMode);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        buttonGiveUp.setOnClickListener((v) ->
                AlertDialog.styled(this, new AlertDialog(this).getBuilder()
                        .setTitle(getString(R.string.are_you_sure))
                        .setPositiveButton(getString(R.string.answer_yes), (dialogInterface, which) -> {
                            StoredData.saveString(this, StoredData.GAME_STATUS, String.valueOf(GameStatus.FINISHED));
                            QRMarkersViewModel.clearFoundStatus();
                            startActivity(new Intent(this, LocationsActivity.class));
                        })
                        .setNegativeButton(getString(R.string.answer_no), (dialogInterface, which) -> dialogInterface.cancel())
                        .create()));

        markersOnMap = new ArrayList<>();
        usersMarkers = new HashMap<>();

        serverRequest.send(
                new RequestBuilder(Method.GET, URL.GAME_STATUS, StoredData.getInt(this, StoredData.GAME_ID))
                        .setResponseListener(response -> {
                            GameStatusEntity gameStatusEntities = new Gson()
                                    .fromJson(response, new TypeToken<GameStatusEntity>() {
                                    }.getType());

                            LatLngBounds.Builder nearestMarkerAndLocationBounds = new LatLngBounds.Builder();

                            boolean isAnyPoints = false;
                            if (gameStatusEntities.getFoundLocations() != null && mMap != null) {
                                for (QRMarkerEntity entity : gameStatusEntities.getFoundLocations()) {
                                    LatLng position = new LatLng(entity.getLatitude(), entity.getLongitude());
                                    if (!markersOnMap.contains(position)) {
                                        nearestMarkerAndLocationBounds.include(position);
                                        mMap.addMarker(new MarkerOptions().position(position).title(entity.getName()));
                                        markersOnMap.add(position);
                                        isAnyPoints = true;
                                    }
                                }
                            }

                            StoredData.saveInt(this, StoredData.FOUND_MARKERS, gameStatusEntities.getFoundMarkers());
                            StoredData.saveInt(this, StoredData.TOTAL_MARKERS, gameStatusEntities.getTotalMarkers());
                            StoredData.saveInt(this, StoredData.TOTAL_SCORE, gameStatusEntities.getTotalScore());

                            foundMarkers.setText(gameStatusEntities.getFoundMarkers() + " / " + gameStatusEntities.getTotalMarkers());

                            if (isAnyPoints) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(nearestMarkerAndLocationBounds.build(), 150));
                            }
                        })
                        .setErrorListener(error -> Toast.make(this, getString(R.string.error_cannot_update_latest_marker_info)))
                        .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                        .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                        .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                        .build(this)
        );

        if (StoredData.getInt(this, StoredData.FOUND_MARKERS) >= StoredData.getInt(this, StoredData.TOTAL_MARKERS) && StoredData.getInt(this, StoredData.TOTAL_MARKERS) != 0) {
            startActivity(new Intent(this, GameEndInfoActivity.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String currentUserId = StoredData.getString(this, StoredData.LOGGED_USER_ID);

        pusherConnection = new PusherConnection(this);

        IconGenerator iconFactory = new IconGenerator(this);

        Map<String, SubscriptionEventListener> pusherEvents = new HashMap<>();
        pusherEvents.put(PusherConnection.EVENT_USER_LOCATION, (String channelName, String eventName, final String data) -> {
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
//                                                .title(userNames)
                                            .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(userNames)))
                                            .position(new LatLng(latitude, longitude))
                                            .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV())
                                            .visible(true)
                            );

                            marker.showInfoWindow();

                            usersMarkers.put(userId, marker);
                        } else {
                            Marker m = usersMarkers.get(userId);
                            if (m != null) {
                                m.setPosition(new LatLng(latitude, longitude));
                                m.showInfoWindow();
                            }
                        }
                    }
                }

            });
        });
        pusherEvents.put(PusherConnection.EVENT_FOUND_QR_CODE, (String channelName, String eventName, final String data) -> {
            JsonObject info = new Gson().fromJson(data, JsonObject.class);

            String userId = info.get("userId").getAsString();
            String userName = info.get("userName").getAsString();
            String markerTitle = info.get("name").getAsString();
            double latitude = info.get("latitude").getAsDouble();
            double longitude = info.get("longitude").getAsDouble();

            runOnUiThread(() -> {
                if (mMap != null) {
                    mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(markerTitle));
                    if (!(userId.equals(StoredData.getString(this, StoredData.LOGGED_USER_ID)))){
                        int currentFoundMarkers = StoredData.getInt(this, StoredData.FOUND_MARKERS);
                        int currentTotalScore = StoredData.getInt(this, StoredData.TOTAL_SCORE);
                        StoredData.saveInt(this, StoredData.TOTAL_SCORE, currentTotalScore + 10);
                        StoredData.saveInt(this, StoredData.FOUND_MARKERS, currentFoundMarkers + 1);

                        foundMarkers.setText((currentFoundMarkers + 1) + " / " + StoredData.getInt(this, StoredData.TOTAL_MARKERS));

                        if (StoredData.getInt(this, StoredData.FOUND_MARKERS) >= StoredData.getInt(this, StoredData.TOTAL_MARKERS)
                                && mPopupWindow != null && !mPopupWindow.isShowing()
                                && StoredData.getInt(this, StoredData.TOTAL_MARKERS) != 0) {
                            startActivity(new Intent(this, GameEndInfoActivity.class));
                        }

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, FOUND_MARKER_NOTIFICATION_CHANNEL)
                                .setSmallIcon(android.R.drawable.ic_dialog_map)
                                .setContentTitle(getString(R.string.notification_found_marker, markerTitle))
                                .setContentText(getString(R.string.notification_found_marker_user, userName, markerTitle))
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            int importance = NotificationManager.IMPORTANCE_DEFAULT;
                            NotificationChannel channel = new NotificationChannel(FOUND_MARKER_NOTIFICATION_CHANNEL, "Found QR codes", importance);

                            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                            notificationManager.createNotificationChannel(channel);
                        }

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);


                        notificationManager.notify(FOUND_MARKER_NOTIFICATION_ID, builder.build());
                    }
                }
            });
        });

        pusherConnection.bindChannelWithEvents(
                PusherConnection.formatChannelName(PusherConnection.CHANNEL_USER_GAME, StoredData.getInt(this, StoredData.GAME_ID)),
                pusherEvents
        );

        pusherConnection.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            progressBar.setVisibility(View.VISIBLE);

            String result = data.getStringExtra("barcode");
            serverRequest.send(
                    new RequestBuilder(Method.POST, URL.GAME_QR)
                            .setResponseListener(response -> {
                                QRMarkerEntity qrMarkerEntity = new GsonBuilder()
                                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                        .create()
                                        .fromJson(response, new TypeToken<QRMarkerEntity>() {
                                        }.getType());

                                QRMarkersViewModel.updateIsFound(true, qrMarkerEntity.getId());

                                int currentFoundMarkers = StoredData.getInt(this, StoredData.FOUND_MARKERS);
                                int currentTotalScore = StoredData.getInt(this, StoredData.TOTAL_SCORE);
                                StoredData.saveInt(this, StoredData.TOTAL_SCORE, currentTotalScore + 10);
                                StoredData.saveInt(this, StoredData.FOUND_MARKERS, currentFoundMarkers + 1);

                                foundMarkers.setText((currentFoundMarkers + 1) + " / " + StoredData.getInt(this, StoredData.TOTAL_MARKERS));

                                String description = qrMarkerEntity.getDescription();
                                mMap.addMarker(new MarkerOptions().position(new LatLng(qrMarkerEntity.getLatitude(), qrMarkerEntity.getLongitude())).title(qrMarkerEntity.getName()).snippet(description.substring(0, Math.min(description.length(), 50)) + "..."));

                                displayMarkerInfoPopup(qrMarkerEntity.getId());

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(qrMarkerEntity.getLatitude(), qrMarkerEntity.getLongitude()), 14));

                                progressBar.setVisibility(View.GONE);
                            })
                            .setErrorListener(error -> {
                                if (error.networkResponse != null &&
                                        error.networkResponse.data != null &&
                                        new String(error.networkResponse.data).contains("QR_ALREADY_FOUND")) {
                                    AlertDialog.styled(this, new AlertDialog(this).getBuilder()
                                            .setTitle(getString(R.string.cant_read_info_twice))
                                            .setNeutralButton(getString(R.string.button_ok), (dialogInterface, which) -> dialogInterface.cancel())
                                            .create());
                                } else {
                                    AlertDialog.styled(this, new AlertDialog(this).getBuilder()
                                            .setTitle(getString(R.string.invalid_scan))
                                            .setNeutralButton(getString(R.string.button_ok), (dialogInterface, which) -> dialogInterface.cancel())
                                            .create());
                                }
                                progressBar.setVisibility(View.GONE);
                            })
                            .addParam("gameId", String.valueOf(StoredData.getInt(this, StoredData.GAME_ID)))
                            .addParam("qrCode", result)
                            .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                            .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                            .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                            .build(this));

        }
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

        LatLngBounds.Builder nearestMarkerAndLocationBounds = new LatLngBounds.Builder();
        List<QRMarkerEntity> allMarkers = QRMarkersViewModel.getAll();
        boolean isAnyPoints = false;
        for (QRMarkerEntity markerEntity : allMarkers) {
            LatLng position = new LatLng(markerEntity.getLatitude(), markerEntity.getLongitude());
            if (markerEntity.isFound() && !markersOnMap.contains(position)) {
                nearestMarkerAndLocationBounds.include(position);
                mMap.addMarker(new MarkerOptions().position(position).title(markerEntity.getName()));
                isAnyPoints = true;
            }
        }

        if (isAnyPoints) {
            mMap.setOnMapLoadedCallback(() -> mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(nearestMarkerAndLocationBounds.build(), 150)));
        }
    }

    @Override
    public void onBackPressed() {
        Toast.make(this, getString(R.string.error_going_back));
    }

    private void displayMarkerInfoPopup(int markerId) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popup = inflater.inflate(R.layout.qr_marker_popup, null);

        mPopupWindow = new PopupWindow(
                popup,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        mPopupWindow.setElevation(5.0f);
        mPopupWindow.setAnimationStyle(R.style.WindowPopupAnimation);
        ImageButton closeButton = popup.findViewById(R.id.closePopUp);
        WebView webView = popup.findViewById(R.id.webViewQRMarkerPopup);

        closeButton.setOnClickListener(view -> AlertDialog.styled(this, new AlertDialog(this).getBuilder()
                .setTitle(getString(R.string.are_you_sure_no_more))
                .setMessage(getString(R.string.are_you_sure_closing_modal))
                .setPositiveButton(getString(R.string.answer_yes), (dialogInterface, which) -> {
                    mPopupWindow.dismiss();

                    if (StoredData.getInt(this, StoredData.FOUND_MARKERS) >= StoredData.getInt(this, StoredData.TOTAL_MARKERS)
                            && StoredData.getInt(this, StoredData.TOTAL_MARKERS) != 0) {
                        startActivity(new Intent(this, GameEndInfoActivity.class));
                    }
                })
                .setNegativeButton(getString(R.string.answer_no), (dialogInterface, which) -> dialogInterface.cancel())
                .create()));

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);
            }
        });

        Map<String, String> headers = new HashMap<>();

        headers.put("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN));
        headers.put("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN));
        headers.put("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID));

        webView.loadUrl("https://snsdevelop.com/time-travellers/api/v1/app/game/qr/" + markerId, headers);
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

            fusedLocationClient.requestLocationUpdates(
                    new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setNumUpdates(1),
                    new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult location) {
                            super.onLocationResult(location);

                            if (location != null && location.getLastLocation() != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLastLocation().getLatitude(), location.getLastLocation().getLongitude()), 12));
                            }
                        }
                    },
                    null
            );

            mMap.setMyLocationEnabled(true);
        }
    }

    private void sendLocation(Location location) {
        serverRequest.send(
                new RequestBuilder(Method.POST, URL.GAME_LOCATION)
                        .addParam("gameId", String.valueOf(StoredData.getInt(this, StoredData.GAME_ID)))
                        .addParam("latitude", String.valueOf(location.getLatitude()))
                        .addParam("longitude", String.valueOf(location.getLongitude()))
                        .setErrorListener(error -> Log.d("GameMapActivity", getString(R.string.error_failed_send_location_to_server)))
                        .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                        .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                        .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                        .build(this)
        );
    }
}
