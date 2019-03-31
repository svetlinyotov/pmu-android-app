package com.snsdevelop.tusofia.sem6.pmu;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.snsdevelop.tusofia.sem6.pmu.Adapters.AllGamesAdapter;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.AllGamesEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.ViewModels.AllGamesViewModel;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class AllGamesActivity extends AppCompatActivity {

    private Request serverRequest;
    private AllGamesAdapter allGamesAdapter;
    private SwipeRefreshLayout layoutSwipeAllGames;
    private AllGamesViewModel allGamesViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_games);
        layoutSwipeAllGames = findViewById(R.id.layoutSwipeAllGames);
        ListView mListView = findViewById(R.id.lvAllGames);

        serverRequest = new Request(this);
        allGamesViewModel = ViewModelProviders.of(this).get(AllGamesViewModel.class);

        allGamesAdapter = new AllGamesAdapter(this, R.layout.all_games_adapter_view);
        mListView.setAdapter(allGamesAdapter);
        allGamesAdapter.addAll(allGamesViewModel.getAll());

        layoutSwipeAllGames.setOnRefreshListener(this::updateData);

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener((v) -> finish());
    }

    @Override
    protected void onStop() {
        super.onStop();
        serverRequest.stop();
    }

    private void updateData() {
        serverRequest.send(
                new RequestBuilder(Method.GET, URL.GET_ALL_GAMES)
                        .setResponseListener(response -> {
                            List<AllGamesEntity> allGamesEntityList = new GsonBuilder()
                                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                    .create()
                                    .fromJson(response, new TypeToken<ArrayList<AllGamesEntity>>() {
                                    }.getType());

                            allGamesAdapter.clear();
                            allGamesAdapter.addAll(allGamesEntityList);
                            allGamesAdapter.notifyDataSetChanged();

                            allGamesViewModel.deleteAll();
                            for (AllGamesEntity allGamesEntity : allGamesEntityList) {
                                allGamesViewModel.insert(allGamesEntity);
                            }

                            layoutSwipeAllGames.setRefreshing(false);
                        })
                        .setErrorListener(error -> {
                            Toast.make(this, getString(R.string.error_taking_all_games));
                            layoutSwipeAllGames.setRefreshing(false);
                        })

                        .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                        .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                        .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))

                        .build(this)


        );
    }
}
