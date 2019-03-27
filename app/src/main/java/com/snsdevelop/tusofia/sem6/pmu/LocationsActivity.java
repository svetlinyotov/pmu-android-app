package com.snsdevelop.tusofia.sem6.pmu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.LocationEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.ViewModels.LocationsViewModel;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import static com.snsdevelop.tusofia.sem6.pmu.Utils.PermissionCheck.LOCATION_PERMISSION_REQUEST_CODE;

public class LocationsActivity extends BaseActivity implements OnMapReadyCallback {
    private Context mContext;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;
    private Request serverRequest;
    private Location currentLocation = null;
    private ImageButton startGame;
    private TextView textStartGame;

    private LocationsViewModel locationsViewModel;
    private FusedLocationProviderClient fusedLocationClient;
    private LinkedList<LatLng> markers = new LinkedList<>();

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && markers.size() > 0) {

                Location lastLocation = locationResult.getLastLocation();
                Log.d("KUR", lastLocation.toString());
                Location a = new Location("KURI");
                LatLng c = markers.get(0);
                a.setLatitude(c.latitude);
                a.setLongitude(c.longitude);

                if (lastLocation.distanceTo(a) < 10000) {
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
        mRelativeLayout = findViewById(R.id.locations);
        mContext = getApplicationContext();

        serverRequest = new Request(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationsViewModel = ViewModelProviders.of(this).get(LocationsViewModel.class);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);


        startGame = findViewById(R.id.buttonStartGame);
        textStartGame = findViewById(R.id.textStartGame);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        ImageButton buttonRanking = findViewById(R.id.buttonRanking);

        buttonSettings.setOnClickListener((v) -> startActivity(new Intent(this, SettingsActivity.class)));
        buttonRanking.setOnClickListener((v) -> startActivity(new Intent(this, RankActivity.class)));
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
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rlp.setMargins(0, 390, 0, 0);

            // Move the zoom control buttons
            RelativeLayout.LayoutParams zoomControlsParams = (RelativeLayout.LayoutParams) mapFragment.getView().findViewById(ZoomControlId).getLayoutParams();
            zoomControlsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            zoomControlsParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            zoomControlsParams.setMargins(0, 0, 0, 20);

        }


        mMap.setOnMarkerClickListener(marker -> {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View popup = inflater.inflate(R.layout.marker_info_popup, null);

            mPopupWindow = new PopupWindow(
                    popup,
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
            );
            mPopupWindow.setElevation(5.0f);
            ImageButton closeButton = popup.findViewById(R.id.dismiss);
            WebView webView = popup.findViewById(R.id.webViewMarkerInfoPopup);

            closeButton.setOnClickListener(view -> mPopupWindow.dismiss());
            webView.loadUrl("http://google.com/" + marker.getId()); //TODO: from server
            mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);


            return true;
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        serverRequest.stop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
            }
        }
    }

    private void loadMarkers() {
        List<LocationEntity> locations = locationsViewModel.getAll();

        if (locations != null) {
            int height = 200;
            int width = 200;
            Bitmap bitmapFlag = ((BitmapDrawable) getResources().getDrawable(R.drawable.bg_flag, this.getTheme())).getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmapFlag, width, height, false);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for (LocationEntity location : locations) {
                LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(location.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                ).setTag(location.getId());

                builder.include(position);
                markers.add(position);
            }

            markers.sort((a, b) -> {
                if (currentLocation != null) {
                    Location locationA = new Location("point A");
                    locationA.setLatitude(a.latitude);
                    locationA.setLongitude(a.longitude);
                    Location locationB = new Location("point B");
                    locationB.setLatitude(b.latitude);
                    locationB.setLongitude(b.longitude);

                    float distanceOne = currentLocation.distanceTo(locationA);
                    float distanceTwo = currentLocation.distanceTo(locationB);
                    return Float.compare(distanceOne, distanceTwo);
                }
                return 0;
            });

            LatLngBounds bounds = builder.build();


            if (!mMap.isMyLocationEnabled() || currentLocation == null) {
                if (locations.size() == 1) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 15));
                } else if (locations.size() > 1) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                }
            } else {
                LatLngBounds.Builder nearestMarkerAndLocationBounds = new LatLngBounds.Builder();
                nearestMarkerAndLocationBounds.include(markers.get(0)); // Get the nearest marker form the sorted set
                nearestMarkerAndLocationBounds.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(nearestMarkerAndLocationBounds.build(), 150));
            }
        }
    }

    private void showDefaultLocation() {
        LatLng redmond = new LatLng(42.6955991, 23.183862);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            if (locationManager != null) {
                currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

                fusedLocationClient.requestLocationUpdates(
                        new LocationRequest().setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY).setInterval(1),
                        locationCallback,
                        null
                );
            }

            mMap.setMyLocationEnabled(true);
        }
    }

}
