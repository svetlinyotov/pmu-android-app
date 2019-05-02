package com.snsdevelop.tusofia.sem6.pmu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.snsdevelop.tusofia.sem6.pmu.R;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.QuizEntity;

import java.util.ArrayList;
import java.util.List;

public class QuizAdapter extends BaseAdapter {
    private Context mContext;
    private int mResource;
    private List<QuizEntity> data = new ArrayList<>();
    public static ArrayList<Integer> selectedAnswers;

    public QuizAdapter(Context context, int resource) {
        mContext = context;
        mResource = resource;
        selectedAnswers = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public QuizEntity getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).getId();
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (quizEntity != null) {
            holder.question.setText(quizEntity.getQuestion());
            holder.answer1.setText(quizEntity.getAnswers()[0].getAnswer());
            holder.answer2.setText(quizEntity.getAnswers()[1].getAnswer());
            holder.answer3.setText(quizEntity.getAnswers()[2].getAnswer());

            holder.answer1.setChecked(selectedAnswers.contains(quizEntity.getAnswers()[0].getId()));
            holder.answer2.setChecked(selectedAnswers.contains(quizEntity.getAnswers()[1].getId()));
            holder.answer3.setChecked(selectedAnswers.contains(quizEntity.getAnswers()[2].getId()));

            holder.answer1.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedAnswers.add(quizEntity.getAnswers()[0].getId());
                } else {
                    selectedAnswers.remove((Integer) quizEntity.getAnswers()[0].getId());
                }
            });
            holder.answer2.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedAnswers.add(quizEntity.getAnswers()[1].getId());
                } else {
                    selectedAnswers.remove((Integer) quizEntity.getAnswers()[1].getId());
                }
            });
            holder.answer3.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedAnswers.add(quizEntity.getAnswers()[2].getId());
                } else {
                    selectedAnswers.remove((Integer) quizEntity.getAnswers()[2].getId());
                }
            });
        }

        return convertView;
    }

    public void addAll(List<QuizEntity> quizEntities) {
        data.addAll(quizEntities);
    }

    public void clear() {
        data.clear();
    }

    @Override
    public int getViewTypeCount() {
        return 50;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private static class ViewHolder {
        TextView question;
        RadioButton answer1;
        RadioButton answer2;
        RadioButton answer3;
    }
}
