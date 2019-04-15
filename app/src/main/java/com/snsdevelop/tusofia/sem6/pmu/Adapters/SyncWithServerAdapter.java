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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.AllGamesEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.LocationEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.QRMarkerEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.RankEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.Repositories.AllGamesRepository;
import com.snsdevelop.tusofia.sem6.pmu.Database.Repositories.LocationsRepository;
import com.snsdevelop.tusofia.sem6.pmu.Database.Repositories.QRMarkersRepository;
import com.snsdevelop.tusofia.sem6.pmu.Database.Repositories.RankingRepository;
import com.snsdevelop.tusofia.sem6.pmu.Helpers.Entities.LocationWithMarkers;
import com.snsdevelop.tusofia.sem6.pmu.R;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import java.util.ArrayList;
import java.util.List;

public class SyncWithServerAdapter extends AbstractThreadedSyncAdapter {

    public static final String ACCOUNT = "main_sync_account";
    private Request serverRequest;
    private Context context;

    private LocationsRepository locationsRepository;
    private QRMarkersRepository qrMarkersRepository;
    private RankingRepository rankingRepository;
    private AllGamesRepository allGamesRepository;

    public SyncWithServerAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        rankingRepository = new RankingRepository((Application) context);
        locationsRepository = new LocationsRepository((Application) context);
        qrMarkersRepository = new QRMarkersRepository((Application) context);
        allGamesRepository = new AllGamesRepository((Application) context);
        serverRequest = new Request(context);
        this.context = context;
    }

    public SyncWithServerAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        rankingRepository = new RankingRepository((Application) context);
        locationsRepository = new LocationsRepository((Application) context);
        qrMarkersRepository = new QRMarkersRepository((Application) context);
        allGamesRepository = new AllGamesRepository((Application) context);
        serverRequest = new Request(context);
        this.context = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        if (StoredData.getString(context, StoredData.LOGGED_USER_ID) == null)
            return;

        serverRequest.send(
                new RequestBuilder(Method.GET, URL.GET_ALL_LOCATIONS)
                        .setResponseListener(response -> {
                            List<LocationWithMarkers> locationWithMarkersList = new GsonBuilder()
                                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                    .create()
                                    .fromJson(response, new TypeToken<ArrayList<LocationWithMarkers>>() {
                                    }.getType());

                            for (LocationWithMarkers locationWithMarkers : locationWithMarkersList) {
                                LocationEntity locationEntity = new LocationEntity();
                                locationEntity.setId(locationWithMarkers.getId());
                                locationEntity.setName(locationWithMarkers.getName());
                                locationEntity.setLatitude(locationWithMarkers.getLatitude());
                                locationEntity.setLongitude(locationWithMarkers.getLongitude());
                                locationsRepository.insert(locationEntity);


                                for (LocationWithMarkers.Marker marker : locationWithMarkers.getMarkers()) {
                                    QRMarkerEntity qrMarkerEntity = new QRMarkerEntity();
                                    qrMarkerEntity.setFound(false);
                                    qrMarkerEntity.setLocationId(marker.getLocationId());
                                    qrMarkerEntity.setId(marker.getId());
                                    qrMarkerEntity.setLocation_lat(marker.getLatitude());
                                    qrMarkerEntity.setLocation_lon(marker.getLongitude());
                                    qrMarkerEntity.setQRcode(marker.getQrCode());
                                    qrMarkerEntity.setTitle(marker.getName());
                                    qrMarkerEntity.setDescription(marker.getDescription());
                                    qrMarkerEntity.setPhoto(marker.getPhoto());
                                    qrMarkersRepository.insert(qrMarkerEntity);
                                }
                            }

                            Log.d("SyncWithServerAdapter", response);
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
                        .setErrorListener(error -> Toast.make(context, context.getString(R.string.error_sync_ranking)))
                        .build(context)
        );

        serverRequest.send(
                new RequestBuilder(Method.GET, URL.GET_ALL_GAMES)
                        .setResponseListener(response -> {
                            List<AllGamesEntity> allGamesEntityList = new GsonBuilder()
                                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                    .create()
                                    .fromJson(response, new TypeToken<ArrayList<AllGamesEntity>>() {
                                    }.getType());


                            allGamesRepository.deleteAll();
                            for (AllGamesEntity allGamesEntity : allGamesEntityList) {
                                allGamesRepository.insert(allGamesEntity);
                            }
                        })
                        .setErrorListener(error -> Toast.make(context, context.getString(R.string.error_sync_all_games)))

                        .addHeader("AuthOrigin", StoredData.getString(context, StoredData.LOGGED_USER_ORIGIN))
                        .addHeader("AccessToken", StoredData.getString(context, StoredData.LOGGED_USER_TOKEN))
                        .addHeader("AuthSocialId", StoredData.getString(context, StoredData.LOGGED_USER_ID))

                        .build(context)


        );
    }
}
