package com.snsdevelop.tusofia.sem6.pmu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.AlertDialog;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.GameStatus;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.PlayMode;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static com.snsdevelop.tusofia.sem6.pmu.LocationsActivity.NEAREST_LOCATION_ID_EXTRA;

public class PlayModeActivity extends AppCompatActivity {

    private Request serverRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_mode);

        serverRequest = new Request(this);

        int locationId = getIntent().getIntExtra(NEAREST_LOCATION_ID_EXTRA, 0);

        if (locationId == 0) {
            Toast.make(this, "Error taking nearest location");
            finish();
            return;
        }

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        ImageButton buttonSettings = findViewById(R.id.buttonSettings);
        ImageButton buttonStartSinglePlayerGame = findViewById(R.id.buttonStartSinglePlayerGame);
        ImageButton buttonStartTeamPlayerGame = findViewById(R.id.buttonStartTeamPlayerGame);

        buttonBack.setOnClickListener((v) -> finish());
        buttonSettings.setOnClickListener((v) -> startActivity(new Intent(this, SettingsActivity.class)));

        buttonStartSinglePlayerGame.setOnClickListener((v) ->
                new AlertDialog(this).getBuilder()
                        .setTitle(getString(R.string.modal_mode_are_you_ready))
                        .setPositiveButton(getString(R.string.modal_mode_button_start_game), (dialogInterface, which) ->
                                serverRequest.send(
                                        new RequestBuilder(Method.POST, URL.START_SINGLE_PLAYER_GAME)
                                                .setResponseListener(response -> {
                                                    try {
                                                        JsonObject gameInfo = new Gson().fromJson(response, JsonObject.class);

                                                        StoredData.saveString(this, StoredData.GAME_MODE, String.valueOf(PlayMode.SINGLE));
                                                        StoredData.saveString(this, StoredData.GAME_STATUS, String.valueOf(GameStatus.RUNNING));
                                                        StoredData.saveInt(this, StoredData.GAME_ID, gameInfo.get("gameId").getAsInt());
                                                        StoredData.saveString(this, StoredData.GAME_NAME, gameInfo.get("gameName").getAsString());

                                                        startActivity(new Intent(this, GameMapActivity.class));
                                                    } catch (IllegalStateException e) {
                                                        e.printStackTrace();
                                                        Toast.make(this, "Error parsing data");
                                                    }
                                                })
                                                .setErrorListener(error -> {
                                                    Toast.make(this, getString(R.string.error_taking_all_games));
                                                    dialogInterface.cancel();
                                                })
                                                .addParam("locationId", String.valueOf(locationId))
                                                .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                                                .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                                                .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))

                                                .build(this)


                                ))
                        .setNegativeButton("Cancel", (dialogInterface, which) -> dialogInterface.cancel())
                        .show());

        buttonStartTeamPlayerGame.setOnClickListener((v) -> {

            View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_new_team_name, null);

            new AlertDialog(this).getBuilder()
                    .setTitle(getString(R.string.modal_mode_choose_option))
                    .setNeutralButton(getString(R.string.modal_mode_button_join_team), (dialogInterface, which) -> {

                    })
                    .setPositiveButton(getString(R.string.modal_mode_button_team_create), (dialogInterface, i) ->
                            new AlertDialog(this).getBuilder()
                                    .setView(viewInflated)
                                    .setPositiveButton(getString(R.string.modal_mode_button_create), (dialogInterface1, i1) -> {
                                        final EditText input = viewInflated.findViewById(R.id.editTextTeamName);
                                        String newTeamName = input.getText().toString();

                                        if (!newTeamName.trim().equals("")) {
                                            new RequestBuilder(Method.POST, URL.START_TEAM_PLAYER_GAME_CREATE_TEAM)
                                                    .setResponseListener(response -> {
                                                        try {
                                                            JsonObject gameInfo = new Gson().fromJson(response, JsonObject.class);

                                                            StoredData.saveString(this, StoredData.GAME_MODE, String.valueOf(PlayMode.TEAM));
                                                            StoredData.saveString(this, StoredData.GAME_STATUS, String.valueOf(GameStatus.PENDING));
                                                            StoredData.saveInt(this, StoredData.GAME_ID, gameInfo.get("gameId").getAsInt());
                                                            StoredData.saveString(this, StoredData.GAME_NAME, gameInfo.get("gameName").getAsString());
                                                            StoredData.saveBoolean(this, StoredData.GAME_IS_TEAM_HOST, true);

                                                            startActivity(new Intent(this, WaitingTeammatesActivity.class));
                                                        } catch (IllegalStateException e) {
                                                            e.printStackTrace();
                                                            Toast.make(this, getString(R.string.error_parsin_data));
                                                        }
                                                    })
                                                    .setErrorListener(error -> {
                                                        Toast.make(this, getString(R.string.error_taking_all_games));
                                                        dialogInterface.cancel();
                                                    })
                                                    .addParam("name", newTeamName)
                                                    .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                                                    .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                                                    .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))

                                                    .build(this);
                                        } else {
                                            Toast.make(this, getString(R.string.error_team_name_empty));
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.modal_mode_button_cancel), (dialogInterface1, i1) -> {
                                        dialogInterface.cancel();
                                        dialogInterface1.cancel();
                                    })
                                    .show()
                    )
                    .show();
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        serverRequest.stop();
    }

}
