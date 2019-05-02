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

public class QuizAnswersAdapter extends BaseAdapter {
    private Context mContext;
    private int mResource;
    private List<QuizEntity> data = new ArrayList<>();
    private List<Integer> selectedAnswers;

    public QuizAnswersAdapter(Context context, List<Integer> selectedAnswers, int resource) {
        mContext = context;
        mResource = resource;
        this.selectedAnswers = selectedAnswers;
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


            List<RadioButton> buttons = new ArrayList<>();
            buttons.add(holder.answer1);
            buttons.add(holder.answer2);
            buttons.add(holder.answer3);

            for (int i = 0; i < buttons.size(); i++) {
                RadioButton radioButton = buttons.get(i);

                radioButton.setText(quizEntity.getAnswers()[i].getAnswer());
                radioButton.setChecked(selectedAnswers.contains(quizEntity.getAnswers()[i].getId()));

                if (quizEntity.getAnswers()[i].getIs_correct() == 1) {
                    radioButton.setTextColor(mContext.getColor(android.R.color.holo_green_dark));
                } else if (quizEntity.getAnswers()[i].getIs_correct() ==  0 && selectedAnswers.contains(quizEntity.getAnswers()[i].getId())) {
                    radioButton.setTextColor(mContext.getColor(android.R.color.holo_red_dark));
                }
            }
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
