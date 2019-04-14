package com.snsdevelop.tusofia.sem6.pmu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.pusher.client.channel.SubscriptionEventListener;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.PlayerEntity;
import com.snsdevelop.tusofia.sem6.pmu.Pusher.PusherConnection;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.GameStatus;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WaitingTeammatesActivity extends AppCompatActivity {

    public static final String TEAM_NAME_TO_DISPLAY_EXTRA = "team_name_extra";
    private Request serverRequest;
    private ArrayAdapter<String> playersAdapter;
    private SwipeRefreshLayout layoutSwipePlayers;
    private PusherConnection pusherConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_teammates);
        ListView mListView = findViewById(R.id.listViewTeammates);
        List<String> players = new ArrayList<>();
        Button startGame = findViewById(R.id.buttonStartTeamGame);
        TextView waitingHost = findViewById(R.id.textViewWaiting);
        ProgressBar progressBar = findViewById(R.id.progressBar_cyclic);
        TextView textViewTitleNewTeamName = findViewById(R.id.textViewTitleNewTeamName);
        Button buttonCancelTeamPlay = findViewById(R.id.buttonCancelTeamPlay);

        serverRequest = new Request(this);
        layoutSwipePlayers = findViewById(R.id.layoutSwipePlayers);


        textViewTitleNewTeamName.setText(StoredData.getString(this, StoredData.GAME_NAME));
        playersAdapter = new ArrayAdapter<>(this, R.layout.waiting_teammates_adapter_view, players);
        mListView.setAdapter(playersAdapter);

        updatePlayerList();
        layoutSwipePlayers.setOnRefreshListener(this::updatePlayerList);

        pusherConnection = new PusherConnection(this);

        Map<String, SubscriptionEventListener> events = new HashMap<>();
        events.put(PusherConnection.EVENT_NEW_TEAMMATE, (String channelName, String eventName, final String data) -> {
            JsonObject info = new Gson().fromJson(data, JsonObject.class);
            runOnUiThread(() -> {
                playersAdapter.add(info.get("name").getAsString());
                playersAdapter.notifyDataSetChanged();
            });
        });

        events.put(PusherConnection.EVENT_REMOVE_TEAMMATE, (String channelName, String eventName, final String data) -> {
            JsonObject info = new Gson().fromJson(data, JsonObject.class);
            runOnUiThread(() -> {
                playersAdapter.remove(info.get("name").getAsString());
                playersAdapter.notifyDataSetChanged();
            });
        });

        events.put(PusherConnection.EVENT_TEAM_GAME_START, (String channelName, String eventName, final String data) ->
                runOnUiThread(() -> {
                    StoredData.saveString(this, StoredData.GAME_STATUS, String.valueOf(GameStatus.RUNNING));
                    startActivity(new Intent(this, GameMapActivity.class));
                }));

        pusherConnection.bindChannelWithEvents(
                PusherConnection.formatChannelName(PusherConnection.CHANNEL_NEW_TEAMMATES, StoredData.getInt(this, StoredData.GAME_ID)),
                events
        );

        pusherConnection.connect();

        startGame.setOnClickListener((v) ->
                serverRequest.send(
                        new RequestBuilder(Method.POST, URL.GAME_START_TEAM_PLAY)
                                .setResponseListener(response -> {

                                    StoredData.saveString(this, StoredData.GAME_STATUS, String.valueOf(GameStatus.RUNNING));
                                    startActivity(new Intent(this, GameMapActivity.class));
                                })
                                .setErrorListener(error -> Toast.make(this, getString(R.string.error_staring_game)))
                                .addParam("gameId", String.valueOf(StoredData.getInt(this, StoredData.GAME_ID)))
                                .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                                .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                                .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                                .build(this)));

        buttonCancelTeamPlay.setOnClickListener((v) ->
                //TODO send the request after AlertDialog confirmation
                serverRequest.send(
                        new RequestBuilder(Method.POST, URL.START_TEAM_PLAYER_GAME_UN_JOIN_TEAM)
                                .setResponseListener(response -> {
                                    StoredData.saveString(this, StoredData.GAME_MODE, null);
                                    StoredData.saveString(this, StoredData.GAME_STATUS, null);
                                    StoredData.saveInt(this, StoredData.GAME_ID, -1);
                                    StoredData.saveString(this, StoredData.GAME_NAME, null);
                                    StoredData.saveBoolean(this, StoredData.GAME_IS_TEAM_HOST, false);
                                    startActivity(new Intent(this, LocationsActivity.class));
                                })
                                .setErrorListener(error -> Toast.make(this, getString(R.string.error_staring_game)))
                                .addParam("gameId", String.valueOf(StoredData.getInt(this, StoredData.GAME_ID)))
                                .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                                .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                                .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                                .build(this)));

        if (StoredData.getBoolean(this, StoredData.GAME_IS_TEAM_HOST)) {
            startGame.setVisibility(View.VISIBLE);
            waitingHost.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        } else {
            startGame.setVisibility(View.GONE);
            waitingHost.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        Toast.make(this, getString(R.string.error_going_back));
    }

    @Override
    protected void onStop() {
        super.onStop();
        pusherConnection.disconnect();
    }

    private void updatePlayerList() {
        serverRequest.send(
                new RequestBuilder(Method.POST, URL.GAME_START_TEAM_LIST_PLAYERS)
                        .setResponseListener(response -> {
                            List<PlayerEntity> players = new Gson().fromJson(response, new TypeToken<ArrayList<PlayerEntity>>() {
                            }.getType());

                            playersAdapter.clear();
                            playersAdapter.addAll(players.stream().map(PlayerEntity::getNames).collect(Collectors.toList()));
                            playersAdapter.notifyDataSetChanged();

                            layoutSwipePlayers.setRefreshing(false);
                        })
                        .setErrorListener(error -> {
                            Toast.make(this, getString(R.string.error_taking_player_list));
                            layoutSwipePlayers.setRefreshing(false);
                        })
                        .addParam("gameId", String.valueOf(StoredData.getInt(this, StoredData.GAME_ID)))
                        .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                        .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                        .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                        .build(this));
    }
}
