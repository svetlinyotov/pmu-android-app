package com.snsdevelop.tusofia.sem6.pmu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snsdevelop.tusofia.sem6.pmu.R;
import com.snsdevelop.tusofia.sem6.pmu.Utils.Entity.QuizEntity;

import java.util.List;

import androidx.annotation.NonNull;

public class QuizAdapter extends ArrayAdapter<QuizEntity> {
    private Context mContext;
    private int mResource;
    private List<QuizEntity> mData;

    public QuizAdapter(Context context, int resource) {
        super(context, resource);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

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
        QuizEntity quizEntity = getItem(position);
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
        TextView answer1;
        TextView answer2;
        TextView answer3;
    }
}
