package com.example.btsearch3;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

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
        Button btn = convertView.findViewById(R.id.button);
        btn.setTag(position);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = Integer.parseInt(v.findViewById(R.id.button).getTag().toString());
                Log.d(TAG,bleList.get(position).getName());
                Log.d(TAG,bleList.get(position).getAddress());
                Log.d(TAG,Integer.toString(bleList.get(position).getType()));

            }
        });

        return convertView;
    }
}
