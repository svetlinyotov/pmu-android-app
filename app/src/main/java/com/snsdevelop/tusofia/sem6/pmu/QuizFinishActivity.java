package com.snsdevelop.tusofia.sem6.pmu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snsdevelop.tusofia.sem6.pmu.Adapters.QuizAnswersAdapter;
import com.snsdevelop.tusofia.sem6.pmu.Database.ViewModels.QRMarkersViewModel;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.GameFinishStatus;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.GameStatus;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.QuizEntity;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import java.util.List;
import java.util.Locale;

public class QuizFinishActivity extends AppCompatActivity {

    public static final String USERS_QUESTIONS_STRING_LIST_EXTRA = "USERS_QUESTIONS_STRING_LIST_EXTRA";
    public static final String USERS_ANSWERS_INTEGER_LIST_EXTRA = "USERS_ANSWERS_INTEGER_LIST_EXTRA";

    private Request serverRequest;
    private RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_finish);

        progressBar = findViewById(R.id.layoutProgressBar);
        serverRequest = new Request(this);
        QRMarkersViewModel QRMarkersViewModel = ViewModelProviders.of(this).get(com.snsdevelop.tusofia.sem6.pmu.Database.ViewModels.QRMarkersViewModel.class);
        ListView mListView = findViewById(R.id.lvQuizAnswers);

        TextView textViewFoundMarkers = findViewById(R.id.textViewFoundMarkers);
        TextView textViewTrueAnswers = findViewById(R.id.textViewTrueAnswers);
        Button buttonHome = findViewById(R.id.buttonHome);

        progressBar.setVisibility(View.VISIBLE);

        serverRequest.send(new RequestBuilder(Method.POST, URL.GAME_FINISH_INFO, StoredData.getInt(this, StoredData.GAME_ID))
                .setResponseListener(response -> {
                    GameFinishStatus gameFinishStatus = new Gson()
                            .fromJson(response, new TypeToken<GameFinishStatus>() {
                            }.getType());

                    textViewFoundMarkers.setText(String.format(Locale.ENGLISH, "%d", gameFinishStatus.getMarkersFound()));
                    textViewTrueAnswers.setText(String.format(Locale.ENGLISH, "%d", gameFinishStatus.getCorrectAnswers()));
                    progressBar.setVisibility(View.GONE);
                })
                .setErrorListener(error -> {
                    Toast.make(this, getString(R.string.error_cannot_update_info));
                    progressBar.setVisibility(View.GONE);
                })
                .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                .build(this)
        );

        QuizAnswersAdapter quizAdapter = new QuizAnswersAdapter(this, getIntent().getIntegerArrayListExtra(USERS_ANSWERS_INTEGER_LIST_EXTRA), R.layout.quiz_adapter_andwers_view);
        mListView.setAdapter(quizAdapter);

        String questions = getIntent().getStringExtra(USERS_QUESTIONS_STRING_LIST_EXTRA);
        List<QuizEntity> quizEntities = new Gson().fromJson(questions, new TypeToken<List<QuizEntity>>(){}.getType());

        quizAdapter.clear();
        quizAdapter.addAll(quizEntities);
        quizAdapter.notifyDataSetChanged();

        StoredData.saveString(this, StoredData.GAME_STATUS, String.valueOf(GameStatus.FINISHED));
        QRMarkersViewModel.clearFoundStatus();

        buttonHome.setOnClickListener((v) -> startActivity(new Intent(this, LocationsActivity.class)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        serverRequest.stop();
    }

    @Override
    public void onBackPressed() {
        Toast.make(this, getString(R.string.error_going_back));
    }

}
