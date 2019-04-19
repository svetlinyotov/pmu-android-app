package com.snsdevelop.tusofia.sem6.pmu.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snsdevelop.tusofia.sem6.pmu.Database.Entities.QRMarkerEntity;
import com.snsdevelop.tusofia.sem6.pmu.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

public class FoundMarkersAdapter extends ArrayAdapter<QRMarkerEntity> {
    private Context mContext;
    private int mResource;
    private List<QRMarkerEntity> mData;

    public FoundMarkersAdapter(Context context, int resource) {
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
            holder.pic = convertView.findViewById(R.id.imMarkerPic);
            holder.name = convertView.findViewById(R.id.tvMarkerName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        QRMarkerEntity qrMarkerEntity = getItem(position);
        if (qrMarkerEntity != null) {
            Picasso.with(mContext).load(qrMarkerEntity.getPhoto()).into(holder.pic);
            holder.name.setText(qrMarkerEntity.getName());
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView pic;
        TextView name;
    }
}
