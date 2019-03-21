package bg.tusofia.sem6.pmu.myapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import bg.tusofia.sem6.pmu.myapplication.Helpers.Auth;
import bg.tusofia.sem6.pmu.myapplication.Utils.StoredData;

public class LocationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);

        Button buttonLogOut = findViewById(R.id.buttonLogOut);

        ((TextView) findViewById(R.id.textView)).setText("Email: " + StoredData.getString(this, StoredData.LOGGED_USER_EMAIL));

        buttonLogOut.setOnClickListener((v) -> {
            Auth.logOut(this);
        });
    }
}
