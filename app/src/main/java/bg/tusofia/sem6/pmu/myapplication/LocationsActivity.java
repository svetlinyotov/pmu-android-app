package bg.tusofia.sem6.pmu.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.PopupWindow;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import bg.tusofia.sem6.pmu.myapplication.Helpers.Auth;
import bg.tusofia.sem6.pmu.myapplication.Helpers.Entities.MarkerLocation;
import bg.tusofia.sem6.pmu.myapplication.ServerRequest.Method;
import bg.tusofia.sem6.pmu.myapplication.ServerRequest.Request;
import bg.tusofia.sem6.pmu.myapplication.ServerRequest.RequestBuilder;
import bg.tusofia.sem6.pmu.myapplication.ServerRequest.URL;
import bg.tusofia.sem6.pmu.myapplication.Utils.AlertDialog;
import bg.tusofia.sem6.pmu.myapplication.Utils.StoredData;
import bg.tusofia.sem6.pmu.myapplication.services.playAudioService;

import static bg.tusofia.sem6.pmu.myapplication.Utils.PermissionCheck.LOCATION_PERMISSION_REQUEST_CODE;

public class LocationsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Context mContext;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;
    private Request serverRequest;
    private Location currentLocation = null;
    private ImageButton startGame;
    private TextView textStartGame;
    private TreeSet<LatLng> markers = new TreeSet<>((a, b) -> {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        mRelativeLayout = findViewById(R.id.locations);

        mContext = getApplicationContext();

        startService(new Intent(LocationsActivity.this, playAudioService.class));

        serverRequest = new Request(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);


        Button buttonLogOut = findViewById(R.id.buttonLogOut);
        Button mute = findViewById(R.id.buttonMute);
        startGame = findViewById(R.id.buttonStartGame);
        textStartGame = findViewById(R.id.textStartGame);


        ((TextView) findViewById(R.id.textView)).setText("Email: " + StoredData.getString(this, StoredData.LOGGED_USER_EMAIL));

        buttonLogOut.setOnClickListener((v) -> Auth.logOut(this));
        mute.setOnClickListener((v) -> stopService(new Intent(LocationsActivity.this, playAudioService.class)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        enableMyLocationIfPermitted();

        loadMarkers();

        mMap.getUiSettings().setZoomControlsEnabled(true);


        int ZoomControlId = 0x1;
        int MyLocationButtonId = 0x2;

        if (mapFragment.getView() != null) {
            // Move the My location button
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mapFragment.getView().findViewById(MyLocationButtonId).getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rlp.setMargins(0, 230, 0, 0);

            // Move the zoom control buttons
            RelativeLayout.LayoutParams zoomControlsParams = (RelativeLayout.LayoutParams) mapFragment.getView().findViewById(ZoomControlId).getLayoutParams();
            zoomControlsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            zoomControlsParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            zoomControlsParams.setMargins(0, 0, 0, 220);

        }


        mMap.setOnMarkerClickListener(marker -> {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View popup = inflater.inflate(R.layout.marker_info_popup, null);

            mPopupWindow = new PopupWindow(
                    popup,
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            mPopupWindow.setElevation(5.0f);
            Button closeButton = popup.findViewById(R.id.dismiss);

            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPopupWindow.dismiss();
                }
            });
            mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER, 0, 0);


            return true;
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(new Intent(LocationsActivity.this, playAudioService.class));
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
        serverRequest.send(
                new RequestBuilder(Method.GET, URL.GET_ALL_LOCATIONS)
                        .setResponseListener(response -> {

                            List<MarkerLocation> locations = new Gson().fromJson(response, new TypeToken<ArrayList<MarkerLocation>>() {
                            }.getType());

                            if (locations != null) {
                                int height = 200;
                                int width = 200;
                                Bitmap b = ((BitmapDrawable) getResources().getDrawable(R.drawable.bg_flag, this.getTheme())).getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                                for (MarkerLocation location : locations) {
                                    LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                                    mMap.addMarker(new MarkerOptions()
                                            .position(position)
                                            .title(location.getName())
                                            .anchor(0.2f, 1)
                                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                    );
                                    builder.include(position);
                                    markers.add(position);
                                }

                                LatLngBounds bounds = builder.build();


                                if (!mMap.isMyLocationEnabled() || currentLocation == null) {
                                    if (locations.size() == 1) {
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 15));
                                    } else {
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                                    }
                                } else {
                                    LatLngBounds.Builder nearestMarkerAndLocationBounds = new LatLngBounds.Builder();
                                    nearestMarkerAndLocationBounds.include(markers.first()); // Get the nearest marker form the sorted set
                                    nearestMarkerAndLocationBounds.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(nearestMarkerAndLocationBounds.build(), 150));
                                }

                                Location a = new Location("KUR");
                                LatLng c = markers.first();
                                a.setLatitude(c.latitude);
                                a.setLongitude(c.longitude);
                                if (currentLocation.distanceTo(a) < 10000) {
                                    startGame.setVisibility(View.VISIBLE);
                                    textStartGame.setVisibility(View.VISIBLE);

                                }
                            }
                        })
                        .setErrorListener(error -> new AlertDialog(this).getBuilder()
                                .setTitle(getResources().getString(R.string.modal_server_error_title))
                                .setMessage(getResources().getString(R.string.modal_server_error_description))
                                .setNegativeButton(android.R.string.ok, (dialog, which) -> dialog.cancel())
                                .show()
                        )
                        .build(this)
        );
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

            if (locationManager != null)
                currentLocation = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

            mMap.setMyLocationEnabled(true);
        }
    }

}
