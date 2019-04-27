package com.snsdevelop.tusofia.sem6.pmu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.LocationEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.QRMarkerEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.ViewModels.LocationsViewModel;
import com.snsdevelop.tusofia.sem6.pmu.Database.ViewModels.QRMarkersViewModel;
import com.snsdevelop.tusofia.sem6.pmu.Helpers.Entities.LocationWithMarkers;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.GameStatus;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.snsdevelop.tusofia.sem6.pmu.Utils.PermissionCheck.LOCATION_PERMISSION_REQUEST_CODE;

public class LocationsActivity extends BaseActivity implements OnMapReadyCallback {
    public static final String NEAREST_LOCATION_ID_EXTRA = "nearest_location_id";

    private Context mContext;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;
    private Request serverRequest;
    private Location currentLocation = null;
    private ImageButton startGame;
    private TextView textStartGame;

    private RelativeLayout progressBar;
    private LocationsViewModel locationsViewModel;
    private QRMarkersViewModel qrMarkersViewModel;
    private LinkedList<Marker> markers = new LinkedList<>();
    FusedLocationProviderClient fusedLocationClient;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult != null && markers.size() > 0) {

                Location lastLocation = locationResult.getLastLocation();
                currentLocation = new Location(lastLocation);
                sortMarkers();

                Location a = new Location("Current Location");
                sortMarkers();
                Marker c = markers.get(0);
                a.setLatitude(c.getPosition().latitude);
                a.setLongitude(c.getPosition().longitude);

                Log.d("KUR", c.getTitle() + " " + c.getTag());
                Log.d("KUR", String.valueOf(lastLocation.distanceTo(a)));

                if (lastLocation.distanceTo(a) < 200009999) {
                    startGame.setVisibility(View.VISIBLE);
                    textStartGame.setVisibility(View.VISIBLE);
                } else {
                    startGame.setVisibility(View.GONE);
                    textStartGame.setVisibility(View.GONE);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        progressBar = findViewById(R.id.layoutProgressBar);
        mRelativeLayout = findViewById(R.id.locations);
        mContext = getApplicationContext();

        serverRequest = new Request(this);
        locationsViewModel = ViewModelProviders.of(this).get(LocationsViewModel.class);
        qrMarkersViewModel = ViewModelProviders.of(this).get(QRMarkersViewModel.class);

        if ((StoredData.getInt(this, StoredData.GAME_ID) != -1) && (StoredData.getString(this, StoredData.GAME_STATUS) != null) &&
                !(StoredData.getString(this, StoredData.GAME_STATUS).equals(String.valueOf(GameStatus.FINISHED)))) {
            if (StoredData.getString(this, StoredData.GAME_STATUS).equals(String.valueOf(GameStatus.PENDING))) {
                startActivity(new Intent(this, WaitingTeammatesActivity.class));
            } else if (StoredData.getString(this, StoredData.GAME_STATUS).equals(String.valueOf(GameStatus.RUNNING))) {
                startActivity(new Intent(this, GameMapActivity.class));
            }
        } else {
            StoredData.saveString(this, StoredData.GAME_MODE, null);
            StoredData.saveString(this, StoredData.GAME_STATUS, null);
            StoredData.saveInt(this, StoredData.GAME_ID, -1);
            StoredData.saveString(this, StoredData.GAME_NAME, null);
            StoredData.saveBoolean(this, StoredData.GAME_IS_TEAM_HOST, false);
            StoredData.saveInt(this, StoredData.TOTAL_MARKERS, 0);
            StoredData.saveInt(this, StoredData.TOTAL_SCORE, 0);
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);


        startGame = findViewById(R.id.buttonStartGame);
        textStartGame = findViewById(R.id.textStartGame);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        ImageButton buttonRanking = findViewById(R.id.buttonRanking);
        ImageButton buttonAllGames = findViewById(R.id.buttonAllGames);

        buttonSettings.setOnClickListener((v) -> startActivity(new Intent(this, SettingsActivity.class)));
        buttonRanking.setOnClickListener((v) -> startActivity(new Intent(this, RankActivity.class)));
        buttonAllGames.setOnClickListener((v) -> startActivity(new Intent(this, AllGamesActivity.class)));
        startGame.setOnClickListener((v) -> startActivity(new Intent(this, PlayModeActivity.class).putExtra(NEAREST_LOCATION_ID_EXTRA, (int) markers.get(0).getTag())));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        enableMyLocationIfPermitted();

        mMap.setOnMapLoadedCallback(this::loadMarkers);

        mMap.getUiSettings().setZoomControlsEnabled(true);


        int ZoomControlId = 0x1;
        int MyLocationButtonId = 0x2;

        if (mapFragment.getView() != null) {
            // Move the My location button
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mapFragment.getView().findViewById(MyLocationButtonId).getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            rlp.setMargins(20, 20, 0, 0);

            // Move the zoom control buttons
            RelativeLayout.LayoutParams zoomControlsParams = (RelativeLayout.LayoutParams) mapFragment.getView().findViewById(ZoomControlId).getLayoutParams();
            zoomControlsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            zoomControlsParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            zoomControlsParams.setMargins(0, 0, 0, 20);

        }


        mMap.setOnMarkerClickListener(marker -> {
            progressBar.setVisibility(View.VISIBLE);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View popup = inflater.inflate(R.layout.marker_info_popup, null);

            mPopupWindow = new PopupWindow(
                    popup,
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
            );
            mPopupWindow.setElevation(5.0f);
            mPopupWindow.setAnimationStyle(R.style.WindowPopupAnimation);
            ImageButton closeButton = popup.findViewById(R.id.dismiss);
            WebView webView = popup.findViewById(R.id.webViewMarkerInfoPopup);

            closeButton.setOnClickListener(view -> mPopupWindow.dismiss());

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    progressBar.setVisibility(View.GONE);
                    mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);
                }
            });

            webView.loadUrl("https://snsdevelop.com/time-travellers/api/v1/app/locations/" + marker.getTag());

            return true;
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        serverRequest.stop();
        if (fusedLocationClient != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocationIfPermitted();
            } else {
                showDefaultLocation();
            }
        }
    }

    private void loadMarkers() {
        List<LocationEntity> locations = locationsViewModel.getAll();

        if (locations == null || locations.size() == 0) {
            serverRequest.send(
                    new RequestBuilder(Method.GET, URL.GET_ALL_LOCATIONS)
                            .setResponseListener(response -> {
                                List<LocationWithMarkers> locationWithMarkersList = new GsonBuilder()
                                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                        .create()
                                        .fromJson(response, new TypeToken<ArrayList<LocationWithMarkers>>() {
                                        }.getType());

                                List<LocationEntity> locationEntities = new ArrayList<>(locationWithMarkersList.size());

                                for (LocationWithMarkers locationWithMarkers : locationWithMarkersList) {
                                    LocationEntity locationEntity = new LocationEntity();
                                    locationEntity.setId(locationWithMarkers.getId());
                                    locationEntity.setName(locationWithMarkers.getName());
                                    locationEntity.setLatitude(locationWithMarkers.getLatitude());
                                    locationEntity.setLongitude(locationWithMarkers.getLongitude());
                                    locationsViewModel.insert(locationEntity);
                                    locationEntities.add(locationEntity);


                                    for (LocationWithMarkers.Marker marker : locationWithMarkers.getMarkers()) {
                                        QRMarkerEntity qrMarkerEntity = new QRMarkerEntity();
                                        qrMarkerEntity.setFound(false);
                                        qrMarkerEntity.setLocationId(marker.getLocationId());
                                        qrMarkerEntity.setId(marker.getId());
                                        qrMarkerEntity.setLatitude(marker.getLatitude());
                                        qrMarkerEntity.setLongitude(marker.getLongitude());
                                        qrMarkerEntity.setQRcode(marker.getQrCode());
                                        qrMarkerEntity.setName(marker.getName());
                                        qrMarkerEntity.setDescription(marker.getDescription());
                                        qrMarkerEntity.setPhoto(marker.getPhoto());
                                        qrMarkersViewModel.insert(qrMarkerEntity);
                                    }
                                }

                                displayMarkers(locationEntities);
                            })
                            .setErrorListener(error -> Toast.make(this, getString(R.string.error_sync_locations))
                            )
                            .build(this)
            );
        } else {
            displayMarkers(locations);
        }
    }

    private void displayMarkers(List<LocationEntity> locations) {
        if (locations != null) {
            int height = 150;
            int width = 150;
            Bitmap bitmapFlag = ((BitmapDrawable) getResources().getDrawable(R.drawable.bg_flag, this.getTheme())).getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmapFlag, width, height, false);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (LocationEntity location : locations) {
                LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(location.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                );
                marker.setTag(location.getId());

                builder.include(marker.getPosition());
                markers.add(marker);
            }

            sortMarkers();

            if (locations.size() > 0) {
                LatLngBounds bounds = builder.build();

                if (!mMap.isMyLocationEnabled() || currentLocation == null) {
                    if (locations.size() == 1) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 15));
                    } else if (locations.size() > 1) {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                    }
                } else {
                    LatLngBounds.Builder nearestMarkerAndLocationBounds = new LatLngBounds.Builder();
                    nearestMarkerAndLocationBounds.include(markers.get(0).getPosition()); // Get the nearest marker form the sorted set
                    nearestMarkerAndLocationBounds.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(nearestMarkerAndLocationBounds.build(), 150));
                }
            }
        }
    }

    private void sortMarkers() {
        markers.sort((a, b) -> {
            if (currentLocation != null) {
                Location locationA = new Location("point A");
                locationA.setLatitude(a.getPosition().latitude);
                locationA.setLongitude(a.getPosition().longitude);
                Location locationB = new Location("point B");
                locationB.setLatitude(b.getPosition().latitude);
                locationB.setLongitude(b.getPosition().longitude);

                float distanceOne = currentLocation.distanceTo(locationA);
                float distanceTwo = currentLocation.distanceTo(locationB);
                return Float.compare(distanceOne, distanceTwo);
            }
            return 0;
        });
    }

    private void showDefaultLocation() {
        LatLng redmond = new LatLng(42.6955991, 23.183862);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
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

}
