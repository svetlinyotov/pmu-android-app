package com.snsdevelop.tusofia.sem6.pmu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.RankEntity;
import com.snsdevelop.tusofia.sem6.pmu.R;
import com.snsdevelop.tusofia.sem6.pmu.Utils.StoredData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

public class RankListAdapter extends ArrayAdapter<RankEntity> {
    private Context mContext;
    private int mResource;
    private List<RankEntity> mData;

    public RankListAdapter(Context context, int resource) {
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
            holder.place = convertView.findViewById(R.id.tvPlaceInRank);
            holder.name = convertView.findViewById(R.id.tvNameInRank);
            holder.points = convertView.findViewById(R.id.tvPointInRank);
            holder.star = convertView.findViewById(R.id.ivStar);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        RankEntity rankEntity = getItem(position);
        if (rankEntity != null) {

            holder.place.setText(String.format(Locale.getDefault(), "%d.", position + 1));
            holder.name.setText(rankEntity.getNames());
            holder.points.setText(String.format(Locale.getDefault(), "%d", rankEntity.getTotal() == null ? 0 : rankEntity.getTotal().intValue()));

            if (StoredData.getString(mContext, StoredData.LOGGED_USER_ID).equals(rankEntity.getId())) {
                Animation rotate = AnimationUtils.loadAnimation(mContext, R.anim.rotate);
                holder.star.startAnimation(rotate);

                holder.star.setVisibility(View.VISIBLE);
                holder.place.setVisibility(View.GONE);
            } else {
                holder.star.setVisibility(View.GONE);
                holder.place.setVisibility(View.VISIBLE);
            }

        }


        return convertView;
    }

    private static class ViewHolder {
        ImageView star;
        TextView place;
        TextView name;
        TextView points;
    }
}
