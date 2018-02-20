package com.example.btsearch3;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattServer;
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

    private BluetoothManager bleManager;
    private BluetoothAdapter bleAdapter;
    private BluetoothLeScanner bleScanner;

    //UUID
//    private static final String SERVICE_UUID_YOU_CAN_CHANGE = "NWL>eS30001012WQh00000xZ0Z4>R]";
//    private static final String CHAR_UUID_YOU_CAN_CHANGE = "NWL>eS30001012WQh00000?Z0x4>R]";
//0x4e574c-3e65-5333-3030-303130
//    4e574c3e6553333030303130313257546830303030303f623462303b52

    //アドバタイズの設定
    private static final boolean CONNECTABLE = true;
    private static final int TIMEOUT = 0;

    //BLE
    private BluetoothLeAdvertiser advertiser;
    private BluetoothGattServer gattServer;


    ListView listView;
    CustomAdapter adapter;
    ArrayList<BluetoothDeviceInfo> bleList = new ArrayList<>();
    TextView proceed;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
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

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAdvertise(v.getContext());
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAdvertise();
            }
        });



    }

    private void scanNewDevice(){
        // デバイスの検出.
        bleList = new ArrayList<>();
        adapter.setBleList(bleList);
        listView.setAdapter(adapter);
        proceed = (TextView) findViewById(R.id.proceed);
        proceed.setText("サーチ中。。");
        bleScanner.startScan(scanCallback);

    }

    private void stopScanDevices(){
        proceed =(TextView) findViewById(R.id.proceed);
        proceed.setText("");
        // デバイスの検出停止
        bleScanner.stopScan(scanCallback);

    }

    public void startAdvertise(Context context) {

        //BLE各種を取得
        BluetoothManager manager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = manager.getAdapter();
        adapter.setName("NWL>");
        advertiser = getAdvertiser(adapter);
//        gattServer = getGattServer(context, manager);



//        //UUIDを設定
//        setUuid();

        //アドバタイズを開始
        advertiser.startAdvertising(makeAdvertiseSetting(),makeAdvertiseData(), advertiseCallBack);
    }

    //アドバタイズを停止
    public void stopAdvertise() {

//        //サーバーを閉じる
//        if (gattServer != null) {
//            gattServer.clearServices();
//            gattServer.close();
//            gattServer = null;
//        }

        //アドバタイズを停止
        if (advertiser != null) {
            advertiser.stopAdvertising(advertiseCallBack);
            advertiser = null;
        }
    }

    private BluetoothLeAdvertiser getAdvertiser(BluetoothAdapter adapter) {
        return adapter.getBluetoothLeAdvertiser();
    }

//    private BluetoothGattServer getGattServer(Context context, BluetoothManager manager) {
//        return manager.openGattServer(context, new BLEServer(gattServer));
//    }

    //UUIDを設定
//    private void setUuid() {
//
//        //serviceUUIDを設定
//        BluetoothGattService service = new BluetoothGattService(
//                UUID.fromString(SERVICE_UUID_YOU_CAN_CHANGE),
//                BluetoothGattService.SERVICE_TYPE_PRIMARY);
//
//        //characteristicUUIDを設定
//        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
//                UUID.nameUUIDFromBytes(SERVICE_UUID_YOU_CAN_CHANGE.getBytes()),
//                BluetoothGattCharacteristic.PROPERTY_READ |
//                        BluetoothGattCharacteristic.PROPERTY_WRITE,
//                BluetoothGattCharacteristic.PERMISSION_READ |
//                        BluetoothGattCharacteristic.PERMISSION_WRITE);
//
//        //characteristicUUIDをserviceUUIDにのせる
//        service.addCharacteristic(characteristic);
//
//        //serviceUUIDをサーバーにのせる
//        gattServer.addService(service);
//    }

    private AdvertiseSettings makeAdvertiseSetting() {

        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();

        //アドバタイズモード
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        //アドバタイズパワー
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW);
        //ペリフェラルへの接続を許可する
        builder.setConnectable(CONNECTABLE);
        //
        builder.setTimeout(TIMEOUT);

        return builder.build();
    }

    private AdvertiseData makeAdvertiseData() {

        AdvertiseData.Builder builder = new AdvertiseData.Builder();
//        builder.addServiceUuid(new ParcelUuid(UUID.nameUUIDFromBytes(SERVICE_UUID_YOU_CAN_CHANGE.getBytes())));

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


//    private class BLEServer extends BluetoothGattServerCallback {
//        //BLE
//        private BluetoothGattServer bluetoothGattServer;
//        public BLEServer(BluetoothGattServer gattServer) {
//            this.bluetoothGattServer = gattServer;
//        }
//
//        //セントラル（クライアント）からReadRequestが来ると呼ばれる
//        public void onCharacteristicReadRequest(android.bluetooth.BluetoothDevice device, int requestId,
//                                                int offset, BluetoothGattCharacteristic characteristic) {
//
//            //セントラルに任意の文字を返信する
//            characteristic.setValue("something you want to send");
//            bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
//                    characteristic.getValue());
//
//        }
//
//        //セントラル（クライアント）からWriteRequestが来ると呼ばれる
//        public void onCharacteristicWriteRequest(android.bluetooth.BluetoothDevice device, int requestId,
//                                                 BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded,
//                                                 int offset, byte[] value) {
//
//            //セントラルにnullを返信する
//            bluetoothGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, null);
//        }
//    }
}
