package com.snsdevelop.tusofia.sem6.pmu;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        serverRequest = new Request(this);
        ListView mListView = findViewById(R.id.listViewTeammates);
        List<String> players = new ArrayList<>();
        layoutSwipePlayers = findViewById(R.id.layoutSwipePlayers);

        TextView textViewTitleNewTeamName = findViewById(R.id.textViewTitleNewTeamName);
        textViewTitleNewTeamName.setText(StoredData.getString(this, StoredData.GAME_NAME));
        playersAdapter = new ArrayAdapter<>(this,
                R.layout.waiting_teammates_adapter_view, players);
        mListView.setAdapter(playersAdapter);

        updatePlayerList();
        layoutSwipePlayers.setOnRefreshListener(this::updatePlayerList);

        pusherConnection = new PusherConnection(this);

        pusherConnection.bindChannelWithEvents(
                PusherConnection.formatChannelName(PusherConnection.CHANNEL_NEW_TEAMMATES, StoredData.getInt(this, StoredData.GAME_ID)),
                new HashMap<String, SubscriptionEventListener>() {{
                    put(PusherConnection.EVENT_NEW_TEAMMATE, (String channelName, String eventName, final String data) -> {
                        JsonObject info = new Gson().fromJson(data, JsonObject.class);
                        runOnUiThread(() -> {
                            playersAdapter.add(info.get("name").getAsString());
                            playersAdapter.notifyDataSetChanged();
                        });
                    });
                }}
        );

        pusherConnection.connect();

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
//                            Toast.make(this, getString(R.string.error_taking_player_list));
                            layoutSwipePlayers.setRefreshing(false);
                        })
                        .addParam("gameId", String.valueOf(StoredData.getInt(this, StoredData.GAME_ID)))
                        .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                        .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                        .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                        .build(this));
    }
}
