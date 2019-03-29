package com.snsdevelop.tusofia.sem6.pmu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class RankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        ListView mListView = findViewById(R.id.lvRank);


        ArrayList<String> rankList = new ArrayList<>();
        rankList.add("Ivan");
        rankList.add("Petur");
        rankList.add("Dragan");

        RankListAdapter adapter = new RankListAdapter(this, R.layout.rank_adapter_view, rankList);
        mListView.setAdapter(adapter);
    }
}
