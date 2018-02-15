package com.example.btsearch3;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private BluetoothManager bleManager;
    private BluetoothAdapter bleAdapter;
    private BluetoothLeScanner bleScanner;
    ListView listView;
    CustomAdapter adapter;
    ArrayList<BluetoothDeviceInfo> bleList = new ArrayList<>();
    TextView proceed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        adapter = new CustomAdapter(this);


        // Bluetoothの使用準備.
        bleManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bleManager.getAdapter();
        bleScanner = bleAdapter.getBluetoothLeScanner();

        //TODO BLE検索にGPS権限許可を求めるダイアログの対応（未）
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                //許可を求めるダイアログを表示します。
//                //TODO ダイアログがでない。
//                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//            }
//
//        }

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((bleAdapter != null) && (bleAdapter.isEnabled())) {
                    // BLEが使用可能ならスキャン開始.
                    scanNewDevice();
                }
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScanDevices();
            }
        });

    }

    private void scanNewDevice(){
        // デバイスの検出.
        bleList = new ArrayList<>();

        bleScanner.startScan(scanCallback);

    }

    private void stopScanDevices(){
        proceed = findViewById(R.id.proceed);
        proceed.setText("");
        // デバイスの検出停止
        bleScanner.stopScan(scanCallback);

    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            proceed = findViewById(R.id.proceed);
//            Log.d(TAG,"call onScanResult");
            super.onScanResult(callbackType, result);
            Log.d(TAG,"call onScanSucceed");
//            proceed.setText("サーチ完了");
            BluetoothDevice bleDevice = result.getDevice();

            if (bleDevice.getName() != null && !isContainsAddress(bleDevice.getAddress())){
                bleList.add(new BluetoothDeviceInfo(bleDevice.getName(), bleDevice.getAddress()));
            }
            adapter.setBleList(bleList);
            listView.setAdapter(adapter);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int intErrorCode) {
            Log.d(TAG,"call onScanFailed");
            super.onScanFailed(intErrorCode);
        }
    };

    private boolean isContainsAddress(String address){

        for (BluetoothDeviceInfo bleInfo:bleList){
            if (bleInfo.getAddress().equals(address)){
                return true;
            }
        }
        return false;
    }



}
