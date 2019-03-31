package com.snsdevelop.tusofia.sem6.pmu.Adapters;

import android.accounts.Account;
import android.app.Application;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.LocationEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.RankEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Repositories.LocationsRepository;
import com.snsdevelop.tusofia.sem6.pmu.Database.Repositories.RankingRepository;
import com.snsdevelop.tusofia.sem6.pmu.R;
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
    private RankingRepository rankingRepository;

    public SyncWithServerAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        rankingRepository = new RankingRepository((Application) context);
        locationsRepository = new LocationsRepository((Application) context);
        serverRequest = new Request(context);
        this.context = context;
    }

    public SyncWithServerAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        rankingRepository = new RankingRepository((Application) context);
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
                        .setErrorListener(error -> Toast.make(context, context.getString(R.string.error_sync_locations))
                        )
                        .build(context)
        );

        serverRequest.send(
                new RequestBuilder(Method.GET, URL.GET_GLOBAL_RANKING)
                        .setResponseListener(response -> {
                            List<RankEntity> rankEntities = new GsonBuilder()
                                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                    .create()
                                    .fromJson(response, new TypeToken<ArrayList<RankEntity>>() {
                                    }.getType());

                            for (RankEntity rankEntity : rankEntities) {
                                rankingRepository.insert(rankEntity);
                            }
                        })
                        .setErrorListener(error -> {
                            Toast.make(context, context.getString(R.string.error_sync_ranking));
                        })
                        .build(context)
        );
    }
}
