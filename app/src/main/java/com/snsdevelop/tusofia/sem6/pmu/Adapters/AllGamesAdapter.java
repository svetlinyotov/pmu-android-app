package com.snsdevelop.tusofia.sem6.pmu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.AllGamesEntity;
import com.snsdevelop.tusofia.sem6.pmu.R;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

public class AllGamesAdapter extends ArrayAdapter<AllGamesEntity> {
    private Context mContext;
    private int mResource;
    private List<AllGamesEntity> mData;

    public AllGamesAdapter(Context context, int resource) {
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
            holder.game = convertView.findViewById(R.id.tvGameName);
            holder.location = convertView.findViewById(R.id.tvLocation);
            holder.points = convertView.findViewById(R.id.tvPointsInGame);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AllGamesEntity allGamesEntity = getItem(position);
        if (allGamesEntity != null) {

            holder.game.setText(allGamesEntity.getGameName());
            holder.location.setText(allGamesEntity.getLocationName());
            holder.points.setText(String.format(Locale.getDefault(), "%d", allGamesEntity.getTotal() == null ? 0 : allGamesEntity.getTotal().intValue()));

        }


        return convertView;
    }

    private static class ViewHolder {
        TextView game;
        TextView location;
        TextView points;
    }
}
