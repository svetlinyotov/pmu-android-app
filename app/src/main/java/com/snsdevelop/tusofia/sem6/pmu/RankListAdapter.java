package com.snsdevelop.tusofia.sem6.pmu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class RankListAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private int mResource;

    private static class ViewHolder {
        TextView name;
        TextView points;
    }

    public RankListAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder= new ViewHolder();
            holder.name = convertView.findViewById(R.id.tvNameInRank);
            holder.points = convertView.findViewById(R.id.tvPointInRank);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText("Ime");
        holder.points.setText("653");

        ImageView star = convertView.findViewById(R.id.ivStar);

        Animation rotate = AnimationUtils.loadAnimation(mContext, android.R.anim.bounce_interpolator);
        star.startAnimation(rotate);


        return convertView;
    }



}
