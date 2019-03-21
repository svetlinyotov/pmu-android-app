package bg.tusofia.sem6.pmu.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.AppCompatActivity;
import bg.tusofia.sem6.pmu.myapplication.Helpers.Auth;
import bg.tusofia.sem6.pmu.myapplication.Utils.StoredData;

public class LocationsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button buttonLogOut = findViewById(R.id.buttonLogOut);

        ((TextView) findViewById(R.id.textView)).setText("Email: " + StoredData.getString(this, StoredData.LOGGED_USER_EMAIL));

        buttonLogOut.setOnClickListener((v) -> {
            Auth.logOut(this);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng tsarevets = new LatLng(43.084900, 25.652565);
        mMap.addMarker(new MarkerOptions().position(tsarevets).title("Marker in Tsarevets").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tsarevets));
    }
}
