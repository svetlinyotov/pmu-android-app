package com.snsdevelop.tusofia.sem6.pmu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.snsdevelop.tusofia.sem6.pmu.R;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.QuizEntity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class QuizAdapter extends ArrayAdapter<QuizEntity> {
    private Context mContext;
    private int mResource;
    private List<QuizEntity> mData;
    public static ArrayList<Integer> selectedAnswers;

    public QuizAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        mResource = resource;
        selectedAnswers = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        QuizEntity quizEntity = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.question = convertView.findViewById(R.id.tvQuestion);
            holder.answer1 = convertView.findViewById(R.id.tvAnswer1);
            holder.answer2 = convertView.findViewById(R.id.tvAnswer2);
            holder.answer3 = convertView.findViewById(R.id.tvAnswer3);
            holder.answer1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        selectedAnswers.add(quizEntity.getAnswers()[0].getId());
                }
            });
            holder.answer2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        selectedAnswers.add(quizEntity.getAnswers()[1].getId());
                }
            });
            holder.answer3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        selectedAnswers.add(quizEntity.getAnswers()[2].getId());
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (quizEntity != null) {
            holder.question.setText(quizEntity.getQuestion());
            holder.answer1.setText(quizEntity.getAnswers()[0].getAnswer());
            holder.answer2.setText(quizEntity.getAnswers()[1].getAnswer());
            holder.answer3.setText(quizEntity.getAnswers()[2].getAnswer());
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView question;
        RadioButton answer1;
        RadioButton answer2;
        RadioButton answer3;
    }
}
