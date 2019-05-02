package com.snsdevelop.tusofia.sem6.pmu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
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

import static com.snsdevelop.tusofia.sem6.pmu.QuizFinishActivity.USERS_ANSWERS_INTEGER_LIST_EXTRA;
import static com.snsdevelop.tusofia.sem6.pmu.QuizFinishActivity.USERS_QUESTIONS_STRING_LIST_EXTRA;

public class QuizActivity extends AppCompatActivity {

    private QuizAdapter quizAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Request serverRequest = new Request(this);
        ListView mListView = findViewById(R.id.lvQuiz);
        Button buttonFinish = findViewById(R.id.buttonFinish);
        List<QuizEntity> quizEntitiesState = new ArrayList<>();

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
                            quizEntitiesState.addAll(quizEntities);
                            quizAdapter.notifyDataSetChanged();
                        })

                        .setErrorListener(error -> Toast.make(this, getString(R.string.error_cannot_retrieve_questions)))
                        .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                        .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                        .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                        .build(this)
        );

        buttonFinish.setOnClickListener((v) -> AlertDialog.styled(this,
                new AlertDialog(this).getBuilder()
                        .setTitle(getString(R.string.are_you_ready))
                        .setNegativeButton(getString(R.string.answer_no), (dialogInterface, which) -> dialogInterface.cancel())
                        .setPositiveButton(getString(R.string.answer_yes), (dialogInterface, which) -> serverRequest.send(
                                new RequestBuilder(Method.POST, URL.GAME_QUIZ,  StoredData.getInt(this, StoredData.GAME_ID))
                                        .setResponseListener(response -> {
                                            Intent i = new Intent(this, QuizFinishActivity.class);
                                            i.putExtra(USERS_QUESTIONS_STRING_LIST_EXTRA, new Gson().toJson(quizEntitiesState));
                                            i.putIntegerArrayListExtra(USERS_ANSWERS_INTEGER_LIST_EXTRA, QuizAdapter.selectedAnswers);
                                            startActivity(i);
                                        })
                                        .addParam("answers", convertAnswers())
                                        .setErrorListener(error -> Toast.make(this, getString(R.string.error_cannot_send_answers)))
                                        .addHeader("AuthOrigin", StoredData.getString(this, StoredData.LOGGED_USER_ORIGIN))
                                        .addHeader("AccessToken", StoredData.getString(this, StoredData.LOGGED_USER_TOKEN))
                                        .addHeader("AuthSocialId", StoredData.getString(this, StoredData.LOGGED_USER_ID))
                                        .build(this)
                        ))
                        .create()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.make(this, getString(R.string.error_going_back));
    }

    public String convertAnswers(){
        StringBuilder answers = new StringBuilder();
        for (int i = 0; i < QuizAdapter.selectedAnswers.size(); i++) {
            if (i < QuizAdapter.selectedAnswers.size() - 1) {
                answers.append(QuizAdapter.selectedAnswers.get(i)).append("|");
            } else {
                answers.append(QuizAdapter.selectedAnswers.get(i));
            }
        }
        return answers.toString();
    }

}
