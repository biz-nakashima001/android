package com.example.btsearch3;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
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

    //BLE
    private BluetoothManager bleManager;
    private BluetoothAdapter bleAdapter;
    private BluetoothLeScanner bleScanner;
    private BluetoothLeAdvertiser advertiser;

    //アドバタイズの設定
    private static final boolean CONNECTABLE = true;
    private static final int TIMEOUT = 0;

    //Test for Find me
    String hexString = "4e574c3e65533336586436"; //DEVICE ID: 0e352303
    byte[] bytes = hexStringToByteArray(hexString);

    ListView listView;
    TextView proceed;
    CustomAdapter adapter;
    ArrayList<BluetoothDeviceInfo> bleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        adapter = new CustomAdapter(this);

        // BLEの使用準備.
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

        //スキャン開始ボタン押下
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((bleAdapter != null) && (bleAdapter.isEnabled())) {
                    // BLEが使用可能ならスキャン開始.
                    scanNewDevice();
                }
            }
        });

        //スキャン停止ボタン押下
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScanDevices();
            }
        });

        //アドバタイジング開始ボタン押下
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAdvertise(v.getContext());
            }
        });

        //アドバタイジング停止ボタン押下
        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAdvertise();
            }
        });

    }

    //スキャン開始
    private void scanNewDevice(){
        // デバイスの検出.
        bleList = new ArrayList<>();
        adapter.setBleList(bleList);
        listView.setAdapter(adapter);
        proceed = findViewById(R.id.proceed);
        proceed.setText("サーチ中。。");
        bleScanner.startScan(scanCallback);

    }

    //スキャン停止
    private void stopScanDevices(){
        proceed =(TextView) findViewById(R.id.proceed);
        proceed.setText("");
        // デバイスの検出停止
        bleScanner.stopScan(scanCallback);

    }

    //アドバタイジング開始
    public void startAdvertise(Context context) {

        String  sendData = new String(bytes);
        //パラメータをセット
        bleAdapter.setName(sendData);
        advertiser = getAdvertiser(bleAdapter);

        advertiser.startAdvertising(makeAdvertiseSetting(),makeAdvertiseData(), advertiseCallBack);
    }

    //アドバタイズを停止
    public void stopAdvertise() {

        //アドバタイズを停止
        if (advertiser != null) {
            advertiser.stopAdvertising(advertiseCallBack);
            advertiser = null;
        }
    }

    private BluetoothLeAdvertiser getAdvertiser(BluetoothAdapter adapter) {
        return adapter.getBluetoothLeAdvertiser();
    }


    private AdvertiseSettings makeAdvertiseSetting() {

        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();

        //アドバタイズモード
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        //アドバタイズパワー
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW);
        //ペリフェラルへの接続を許可する
        builder.setConnectable(CONNECTABLE);

        builder.setTimeout(TIMEOUT);

        return builder.build();
    }

    private AdvertiseData makeAdvertiseData() {

        AdvertiseData.Builder builder = new AdvertiseData.Builder();

        builder.setIncludeDeviceName(true);

        return builder.build();
    }

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.d(TAG,"call onScanResult");
            super.onScanResult(callbackType, result);
            Log.d(TAG,"call onScanSucceed");

            BluetoothDevice bleDevice = result.getDevice();

            if(!isContainsAddress(bleDevice)){
                if (bleDevice.getName() != null){
                    bleList.add(new BluetoothDeviceInfo(bleDevice.getName(), bleDevice.getAddress(), bleDevice.getType()));
                }else{
                    bleList.add(new BluetoothDeviceInfo("N/a", bleDevice.getAddress(), bleDevice.getType()));
                }
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

    private AdvertiseCallback advertiseCallBack = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.d(TAG,"call onStartSuccess");
            super.onStartSuccess(settingsInEffect);
            Log.d(TAG,"call super_onStartSucceed");
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.d(TAG,"call onStartFailure:"+errorCode);
        }
    };

    private boolean isContainsAddress(BluetoothDevice bleDevice){

        for (BluetoothDeviceInfo bleInfo:bleList){
            if (bleInfo.getAddress().equals(bleDevice.getAddress())){

                if (bleDevice.getName() != null){
                    bleInfo.setName(bleDevice.getName());
                }else{
                    bleInfo.setName("N/a");
                }
                return true;
            }
        }
        return false;
    }

    public static final byte[] hexStringToByteArray(String str)
    {
        int i = 0;
        byte[] results = new byte[str.length() / 2];
        for (int k = 0; k < str.length();)
        {
            results[i] = (byte)(Character.digit(str.charAt(k++), 16) << 4);
            results[i] += (byte)(Character.digit(str.charAt(k++), 16));
            i++;
        }
        return results;
    }
}
