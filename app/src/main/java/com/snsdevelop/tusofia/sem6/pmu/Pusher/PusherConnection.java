package com.snsdevelop.tusofia.sem6.pmu.Pusher;

import android.content.Context;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.snsdevelop.tusofia.sem6.pmu.R;

import java.util.Map;

public class PusherConnection {

    public static final String CHANNEL_NEW_TEAMMATES = "player-to-team-game-{id}-channel";
    public static final String CHANNEL_USER_LOCATIONS = "user-location-game-{id}-channel";
    public static final String EVENT_NEW_TEAMMATE = "App\\Events\\Pusher\\BroadcastNewPlayerToTeam";
    public static final String EVENT_REMOVE_TEAMMATE = "App\\Events\\Pusher\\BroadcastRemovePlayerFromTeam";
    public static final String EVENT_TEAM_GAME_START = "App\\Events\\Pusher\\BroadcastTeamGameStart";
    public static final String EVENT_USER_LOCATION = "App\\Events\\Pusher\\BroadcastUserLocation";

    private Pusher pusher;

    public PusherConnection(Context context) {
        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        pusher = new Pusher(context.getString(R.string.pusher_api_key), options);
    }

    public void bindChannelWithEvents(String channelName, Map<String, SubscriptionEventListener> events) {
        Channel channel = pusher.subscribe(channelName);

        for (Map.Entry<String, SubscriptionEventListener> event : events.entrySet()) {
            channel.bind(event.getKey(), event.getValue());
        }
    }

    public void connect() {
        pusher.connect();
    }

    public void disconnect() {
        pusher.disconnect();
    }

    public static String formatChannelName(String channel, int id) {
        return channel.replace("{id}", String.valueOf(id));
    }

}
