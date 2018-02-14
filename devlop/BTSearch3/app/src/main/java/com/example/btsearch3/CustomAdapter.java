package com.example.btsearch3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by shinya on 2018/02/14.
 */

public class CustomAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater = null;
    ArrayList<BluetoothDeviceInfo> bleList;

    public CustomAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setBleList(ArrayList<BluetoothDeviceInfo> bleList) {
        this.bleList = bleList;
    }

    @Override
    public int getCount() {
        return bleList.size();
    }

    @Override
    public Object getItem(int position) {
        return bleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return bleList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.textview,parent,false);

        ((TextView)convertView.findViewById(R.id.name)).setText(bleList.get(position).getName());
        ((TextView)convertView.findViewById(R.id.address)).setText(bleList.get(position).getAddress());

        return convertView;
    }
}
