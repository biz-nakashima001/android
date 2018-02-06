package com.example.btsearch;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.util.Log;

import java.util.Set;


public class MainActivity extends Activity {
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("###intent",intent.getAction());
            if (intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){
                int level = intent.getIntExtra("level",0);
                int scale = intent.getIntExtra("scale",0);
                TextView tv = (TextView) findViewById(R.id.TEXT_VIEW);
                tv.setText("バッテリー残量:" + ((float) level / (float) scale * 100) + " (%) ");
            }else if(BluetoothDevice.ACTION_FOUND.equals(intent.getAction())){
                Log.i("###intent",intent.getAction());
                TextView tv = (TextView) findViewById(R.id.TEXT_VIEW);
                tv.setText("ブルーテュース");
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);

        IntentFilter btFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, btFilter);

        mBluetoothAdapter = mBluetoothAdapter.getDefaultAdapter();
        Log.i("######","test");
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
// If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.i("######",device.getAddress());
                Log.i("######",device.getName());
            }
        }
        mBluetoothAdapter.startDiscovery(); //検索開始
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBluetoothAdapter = mBluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
// If there are paired devices
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Log.i("######",device.getAddress());
                Log.i("######",device.getName());
            }
        }
        mBluetoothAdapter.startDiscovery(); //検索開始
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mBluetoothAdapter.cancelDiscovery(); //検索キャンセル
        unregisterReceiver(broadcastReceiver); //filter解除
    }
}
