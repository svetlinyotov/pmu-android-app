package com.snsdevelop.tusofia.sem6.pmu.Adapters;

import android.accounts.Account;
import android.app.Application;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.LocationEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Repositories.LocationsRepository;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import java.util.ArrayList;
import java.util.List;

public class SyncWithServerAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACCOUNT = "main_sync_account";
    private Request serverRequest;
    private Context context;

    private LocationsRepository locationsRepository;

    public SyncWithServerAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        locationsRepository = new LocationsRepository((Application) context);
        serverRequest = new Request(context);
        this.context = context;
    }

    public SyncWithServerAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        locationsRepository = new LocationsRepository((Application) context);
        serverRequest = new Request(context);
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        serverRequest.send(
                new RequestBuilder(Method.GET, URL.GET_ALL_LOCATIONS)
                        .setResponseListener(response -> {
                            List<LocationEntity> locationEntities = new Gson().fromJson(response, new TypeToken<ArrayList<LocationEntity>>() {
                            }.getType());

                            for (LocationEntity locationEntity : locationEntities) {
                                locationsRepository.insert(locationEntity);
                            }

                            Log.d("SyncWithServerAdapter", response.toString());
                        })
                        .setErrorListener(error -> Toast.make(context, "Error syncing data")
                        )
                        .build(context)
        );

        Log.d("SyncWithServerAdapter", locationsRepository.getAll().toString());
        Log.d("SyncWithServerAdapter", "----------------------------------------------------");
    }
}
