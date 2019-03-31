package com.snsdevelop.tusofia.sem6.pmu;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.snsdevelop.tusofia.sem6.pmu.Adapters.RankListAdapter;
import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.RankEntity;
import com.snsdevelop.tusofia.sem6.pmu.Database.ViewModels.RankingViewModel;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class RankActivity extends AppCompatActivity {

    private Request serverRequest;
    private RankListAdapter rankListAdapter;
    private SwipeRefreshLayout layoutSwipeRanking;
    private RankingViewModel rankingViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        layoutSwipeRanking = findViewById(R.id.layoutSwipeRanking);
        ListView mListView = findViewById(R.id.lvRank);

        serverRequest = new Request(this);
        rankingViewModel = ViewModelProviders.of(this).get(RankingViewModel.class);

        rankListAdapter = new RankListAdapter(this, R.layout.rank_adapter_view);
        mListView.setAdapter(rankListAdapter);
        rankListAdapter.addAll(rankingViewModel.getAll());

        layoutSwipeRanking.setOnRefreshListener(this::updateData);

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
                new RequestBuilder(Method.GET, URL.GET_GLOBAL_RANKING)
                        .setResponseListener(response -> {
                            List<RankEntity> rankEntities = new GsonBuilder()
                                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                    .create()
                                    .fromJson(response, new TypeToken<ArrayList<RankEntity>>() {
                                    }.getType());

                            rankListAdapter.clear();
                            rankListAdapter.addAll(rankEntities);
                            rankListAdapter.notifyDataSetChanged();

                            rankingViewModel.deleteAll();
                            for (RankEntity rankEntity : rankEntities) {
                                rankingViewModel.insert(rankEntity);
                            }

                            layoutSwipeRanking.setRefreshing(false);
                        })
                        .setErrorListener(error -> {
                            Toast.make(this, getString(R.string.error_taking_ranking_list));
                            layoutSwipeRanking.setRefreshing(false);
                        })
                        .build(this)
        );
    }
}
