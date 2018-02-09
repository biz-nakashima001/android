package com.example.btsearch3;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

//import java.util.ArrayList;
//import java.util.List;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private BluetoothManager bleManager;
    private BluetoothAdapter bleAdapter;
    private BluetoothLeScanner bleScanner;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);
        list = new ArrayList<>();

        // Bluetoothの使用準備.
        bleManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bleManager.getAdapter();

        if ((bleAdapter != null) || (bleAdapter.isEnabled())) {
            // BLEが使用可能ならスキャン開始.
            this.scanNewDevice();
        }
    }

    private void scanNewDevice(){
        bleScanner = bleAdapter.getBluetoothLeScanner();
        // デバイスの検出.
        bleScanner.startScan(scanCallback);
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG,"call onScanResult");
            super.onScanResult(callbackType, result);
            Log.d(TAG,"call onScanSucceed");

            if (result.getDevice().getName() != null && !list.contains(result.getDevice().getName())){
                adapter.add(result.getDevice().getName());
                list.add(result.getDevice().getName());

//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_main,);
//                ((TextView)findViewById(R.id.textView)).setText("Name:" + result.getDevice().getName());
//                ((TextView)findViewById(R.id.textView2)).setText("Address:" + result.getDevice().getAddress());
//                ((TextView)findViewById(R.id.textView3)).setText("BluetoothType:" + result.getDevice().getType());
//                ((TextView)findViewById(R.id.textView4)).setText("Uuids:" + result.getDevice().getUuids());
//                ((TextView)findViewById(R.id.textView5)).setText("BondState:" + result.getDevice().getBondState());


            }
            listView.setAdapter(adapter);
            scanNewDevice();
        }

        @Override
        public void onScanFailed(int intErrorCode) {
            Log.d(TAG,"call onScanFailed");
            super.onScanFailed(intErrorCode);
        }
    };

}
