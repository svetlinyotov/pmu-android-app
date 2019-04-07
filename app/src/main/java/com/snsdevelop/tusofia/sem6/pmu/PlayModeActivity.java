package com.snsdevelop.tusofia.sem6.pmu;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.GameEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.LocationEntity;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.AlertDialog;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.GameStatus;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.PlayMode;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

import static com.snsdevelop.tusofia.sem6.pmu.LocationsActivity.NEAREST_LOCATION_ID_EXTRA;
import static com.snsdevelop.tusofia.sem6.pmu.WaitingTeammatesActivity.TEAM_NAME_TO_DISPLAY_EXTRA;

public class PlayModeActivity extends AppCompatActivity {

    private Request serverRequest;
    private int locationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_mode);

        serverRequest = new Request(this);

        locationId = getIntent().getIntExtra(NEAREST_LOCATION_ID_EXTRA, 0);

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
                AlertDialog.styled(this, new AlertDialog(this).getBuilder()
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
                        .create()));

        buttonStartTeamPlayerGame.setOnClickListener((v) -> alertDialogTeamPlay());
    }

    @Override
    protected void onStop() {
        super.onStop();
        serverRequest.stop();
    }

    private void alertDialogTeamPlay() {
        AlertDialog.styled(this,
                new AlertDialog(this).getBuilder()
                        .setTitle(getString(R.string.modal_mode_choose_option))
                        .setNeutralButton(getString(R.string.modal_mode_button_join_team), (dialogInterface, which) -> alertDialogTeamPlayJoinTeam())
                        .setPositiveButton(getString(R.string.modal_mode_button_team_create), (dialogInterface, i) -> alertDialogTeamPlayCreateTeam(dialogInterface))
                        .create());
    }

    private void alertDialogTeamPlayCreateTeam(DialogInterface dialogInterface) {

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_new_team_name, null);

        AlertDialog.styled(this, new AlertDialog(this).getBuilder()
                .setView(viewInflated)
                .setPositiveButton(getString(R.string.modal_mode_button_create), (dialogInterface1, i1) -> {
                    final EditText input = viewInflated.findViewById(R.id.editTextTeamName);
                    final String newTeamName = input.getText().toString();

                    if (!newTeamName.trim().equals("")) {
                        serverRequest.send(
                                new RequestBuilder(Method.POST, URL.START_TEAM_PLAYER_GAME_CREATE_TEAM)
                                        .setResponseListener(response -> {
                                            System.out.println(response);
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
                                            System.out.println(new String(error.networkResponse.data));
                                            Toast.make(this, getString(R.string.error_taking_all_games));
                                            dialogInterface.cancel();
                                        })
                                        .addParam("locationId", String.valueOf(locationId))
                                        .addParam("name", newTeamName)
                                        .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                                        .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                                        .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                                        .build(this));
                    } else {
                        Toast.make(this, getString(R.string.error_team_name_empty));
                    }
                })
                .setNegativeButton(getString(R.string.modal_mode_button_cancel), (dialogInterface1, i1) -> {
                    dialogInterface.cancel();
                    dialogInterface1.cancel();
                }).create());

    }

    private void alertDialogTeamPlayJoinTeam() {

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_select_team, null);

        android.app.AlertDialog.Builder builder = new AlertDialog(this).getBuilder();
        List<GameEntity> teams = new ArrayList<>();

        ArrayAdapter<GameEntity> dataAdapter = new ArrayAdapter<>(this,
                R.layout.dropdown_item_1line, teams);

        builder.setTitle("Choose team")
                .setView(viewInflated)
                .setAdapter(dataAdapter, (dialogInterface, which) -> {

                    serverRequest.send(
                            new RequestBuilder(Method.POST, URL.START_TEAM_PLAYER_GAME_JOIN_TEAM)
                                    .setResponseListener(response -> {
                                        System.out.println(response);
                                        try {
                                            JsonObject gameInfo = new Gson().fromJson(response, JsonObject.class);

                                            StoredData.saveString(this, StoredData.GAME_MODE, String.valueOf(PlayMode.TEAM));
                                            StoredData.saveString(this, StoredData.GAME_STATUS, String.valueOf(GameStatus.PENDING));
                                            StoredData.saveInt(this, StoredData.GAME_ID, gameInfo.get("gameId").getAsInt());
                                            StoredData.saveString(this, StoredData.GAME_NAME, gameInfo.get("gameName").getAsString());
                                            StoredData.saveBoolean(this, StoredData.GAME_IS_TEAM_HOST, false);

                                            startActivity(new Intent(this, WaitingTeammatesActivity.class));
                                        } catch (IllegalStateException e) {
                                            e.printStackTrace();
                                            Toast.make(this, getString(R.string.error_parsin_data));
                                        }
                                    })
                                    .setErrorListener(error -> {
                                        System.out.println(new String(error.networkResponse.data));
                                        Toast.make(this, getString(R.string.error_creating_team));
                                        dialogInterface.cancel();
                                    })
                                    .addParam("locationId", String.valueOf(locationId))
                                    .addParam("gameId", String.valueOf(teams.get(which).getId()))
                                    .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                                    .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                                    .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                                    .build(this));

                });

        android.app.AlertDialog instance = builder.create();

        AlertDialog.styled(this, instance);

        Button buttonRefreshTeamsList = viewInflated.findViewById(R.id.buttonRefreshTeamsList);
        ProgressBar progressBarRefreshTeamsList = viewInflated.findViewById(R.id.progressBarRefreshTeamsList);

        if (buttonRefreshTeamsList != null) {
            buttonRefreshTeamsList.setOnClickListener((v) -> {
                progressBarRefreshTeamsList.setVisibility(View.VISIBLE);
                updateTeamsList(instance, progressBarRefreshTeamsList);
            });
        }

        updateTeamsList(instance, progressBarRefreshTeamsList);
    }

    private void updateTeamsList(final android.app.AlertDialog instance, ProgressBar progressBarRefreshTeamsList) {
        ArrayAdapter<GameEntity> adapter = (ArrayAdapter<GameEntity>) instance.getListView().getAdapter();
        adapter.clear();
        adapter.notifyDataSetChanged();
        serverRequest.send(
                new RequestBuilder(Method.POST, URL.START_TEAM_PLAYER_GAME_LIST_TEAMS)
                        .setResponseListener(response -> {
                            try {
                                ArrayList<GameEntity> teamEntities = new Gson().fromJson(response, new TypeToken<ArrayList<GameEntity>>() {
                                }.getType());
                                adapter.clear();
                                adapter.addAll(teamEntities);

                                progressBarRefreshTeamsList.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                                progressBarRefreshTeamsList.setVisibility(View.GONE);
                                Toast.make(this, getString(R.string.error_parsin_data));
                            }
                        })
                        .setErrorListener(error -> {
                            System.out.println(new String(error.networkResponse.data));
                            Toast.make(this, getString(R.string.error_taking_all_games));
                        })
                        .addParam("locationId", String.valueOf(locationId))
                        .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                        .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                        .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                        .build(this));
    }

}
