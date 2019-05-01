package com.snsdevelop.tusofia.sem6.pmu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.snsdevelop.tusofia.sem6.pmu.Adapters.QuizAdapter;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Method;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.Request;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.RequestBuilder;
import com.snsdevelop.tusofia.sem6.pmu.ServerRequest.URL;
import com.snsdevelop.tusofia.sem6.pmu.Utils.AlertDialog;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.QuizEntity;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Toast;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private QuizAdapter quizAdapter;
    private Button buttonFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Request serverRequest = new Request(this);
        ListView mListView = findViewById(R.id.lvQuiz);
        buttonFinish = findViewById(R.id.buttonFinish);

        quizAdapter = new QuizAdapter(this, R.layout.quiz_adapter_view);
        mListView.setAdapter(quizAdapter);

        serverRequest.send(
                new RequestBuilder(Method.GET, URL.GAME_QUIZ, StoredData.getInt(this, StoredData.GAME_ID))
                        .setResponseListener(response -> {
                            List<QuizEntity> quizEntities = new GsonBuilder()
                                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                    .create()
                                    .fromJson(response, new TypeToken<ArrayList<QuizEntity>>() {
                                    }.getType());

                            quizAdapter.clear();
                            quizAdapter.addAll(quizEntities);
                            quizAdapter.notifyDataSetChanged();
                        })

                        .setErrorListener(error -> Toast.make(this, getString(R.string.error_cannot_retrieve_questions)))
                        .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                        .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                        .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                        .build(this)
        );

        buttonFinish.setOnClickListener((v) -> {
            AlertDialog.styled(this,
                    new AlertDialog(this).getBuilder()
                            .setTitle(getString(R.string.are_you_ready))
                            .setNegativeButton(getString(R.string.answer_no), (dialogInterface, which) -> dialogInterface.cancel())
                            .setPositiveButton(getString(R.string.answer_yes), (dialogInterface, which) -> {

                            })
                            .create());
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.make(this, getString(R.string.error_going_back));
    }

}
