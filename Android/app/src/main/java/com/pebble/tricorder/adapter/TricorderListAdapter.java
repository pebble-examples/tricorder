package com.pebble.tricorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pebble.tricorder.R;
import com.pebble.tricorder.model.TricorderData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class TricorderListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<TricorderData> mData;

    public TricorderListAdapter(Context context, ArrayList<TricorderData> data) {
        super();
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = LayoutInflater.from(mContext).inflate(R.layout.tricorder_list_item, parent, false);

        TextView leftText = (TextView) rowView.findViewById(R.id.leftText);
        TextView rightText = (TextView) rowView.findViewById(R.id.rightText);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        TricorderData tricorderData = mData.get(position);

        leftText.setText(sdf.format(tricorderData.getTimestamp()));
        rightText.setText("Packet " + tricorderData.getPacketId());

        return rowView;
    }
}
