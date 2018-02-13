package com.example.btsearch3;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private BluetoothManager bleManager;
    private BluetoothAdapter bleAdapter;
    private BluetoothLeScanner bleScanner;
    ListView listView;
    ArrayAdapter<BluetoothDeviceInfo> adapter;
    ArrayList<String> list;

    static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        findViewById(R.id.button2).setOnClickListener(this);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);
//        list = new ArrayList<>();

        // Bluetoothの使用準備.
        bleManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bleManager.getAdapter();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //許可を求めるダイアログを表示します。
                //TODO ダイアログがでない。
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }

        }

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((bleAdapter != null) && (bleAdapter.isEnabled())) {
                    // BLEが使用可能ならスキャン開始.
                    list = new ArrayList<>();
                    scanNewDevice();
                }
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                hogeClickEvent(v);
            }
        });


    }

    private void scanNewDevice(){
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);
        list = new ArrayList<>();
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

            if (result.getDevice().getName() != null && !list.contains(result.getDevice().getAddress())){
                adapter.add(new BluetoothDeviceInfo(result.getDevice().getAddress(),result.getDevice().getName()));
                list.add(result.getDevice().getAddress());




            }
            listView.setAdapter(adapter);
//            scanNewDevice();
        }

        @Override
        public void onScanFailed(int intErrorCode) {
            Log.d(TAG,"call onScanFailed");
            super.onScanFailed(intErrorCode);
        }
    };



}
