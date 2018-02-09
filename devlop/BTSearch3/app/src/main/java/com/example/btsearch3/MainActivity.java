package com.example.btsearch3;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
//import android.bluetooth.BluetoothGattCharacteristic;
//import android.bluetooth.BluetoothGattDescriptor;
//import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

//import java.util.UUID;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private BluetoothManager bleManager;
    private BluetoothAdapter bleAdapter;
    private BluetoothLeScanner bleScanner;
    private BluetoothGatt bleGatt;
    private boolean isBleEnabled = false;
//    private BluetoothGattCharacteristic bleCharacteristic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bluetoothの使用準備.
        bleManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = bleManager.getAdapter();

//        for (BluetoothDevice device : bleAdapter.getBondedDevices()) {
//            Log.i("BLE_TEST", "Bonded " + device.getName() + "addr:" + device.getAddress());
//        }

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
            setContentView(R.layout.activity_main);

            if (result.getDevice().getName() != null){
                ((TextView)findViewById(R.id.textView)).setText("Name:" + result.getDevice().getName());
                ((TextView)findViewById(R.id.textView2)).setText("Address:" + result.getDevice().getAddress());
                ((TextView)findViewById(R.id.textView3)).setText("BluetoothType:" + result.getDevice().getType());
                ((TextView)findViewById(R.id.textView4)).setText("Uuids:" + result.getDevice().getUuids());
                ((TextView)findViewById(R.id.textView5)).setText("BondState:" + result.getDevice().getBondState());
                try {
                    Thread.sleep(2000); //2000ミリ秒Sleepする
                } catch (InterruptedException e) {
                }
            }

//             スキャン中に見つかったデバイスに接続を試みる.第三引数には接続後に呼ばれるBluetoothGattCallbackを指定する.
//            result.getDevice().connectGatt(getApplicationContext(), true, gattCallback);
        }

        private final BluetoothGattCallback gattCallback = new BluetoothGattCallback(){
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState){
                // 接続状況が変化したら実行.
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG,"call onConnectionStateChange STATE_CONNECTED");
                    // 接続に成功したらサービスを検索する.
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    Log.d(TAG,"call onConnectionStateChange STATE_DISCONNECTED");
                    // 接続が切れたらGATTを空にする.
                    if (bleGatt != null){
                        bleGatt.close();
                        bleGatt = null;
                    }
                    isBleEnabled = false;
                }
            }
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status){
                Log.d(TAG,"service discover state = " + status);
                // Serviceが見つかったら実行.
                if (status == BluetoothGatt.GATT_SUCCESS) {

//                    // UUIDが同じかどうかを確認する.
//                    BluetoothGattService bleService = gatt.getService(UUID.fromString(getString(R.string.uuid_service)));
//                    if (bleService != null){
//                        // 指定したUUIDを持つCharacteristicを確認する.
//                        bleCharacteristic = bleService.getCharacteristic(UUID.fromString(getString(R.string.uuid_characteristic)));
//                        if (bleCharacteristic != null) {
//                            // Service, CharacteristicのUUIDが同じならBluetoothGattを更新する.
//                            bleGatt = gatt;
//                            // キャラクタリスティックが見つかったら、Notificationをリクエスト.
//                            bleGatt.setCharacteristicNotification(bleCharacteristic, true);
//
//                            // Characteristic の Notificationを有効化する.
//                            BluetoothGattDescriptor bleDescriptor = bleCharacteristic.getDescriptor(
//                                    UUID.fromString(getString(R.string.uuid_characteristic_config)));
//                            bleDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                            bleGatt.writeDescriptor(bleDescriptor);
//                            // 接続が完了したらデータ送信を開始する.
//                            isBleEnabled = true;
//                            // scan 終了
//                            Log.d(TAG,"call stop scan");
//                            bleScanner.stopScan(scanCallback);
//                        }
//                    }
                }
            }

//            @Override
//            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
//                Log.d(TAG,"call onCharacteristicChanged");
//                // キャラクタリスティックのUUIDをチェック(getUuidの結果が全て小文字で帰ってくるのでUpperCaseに変換)
//                if (getString(R.string.uuid_characteristic).equals(characteristic.getUuid().toString().toUpperCase())){
//                    runOnUiThread(
//                            () -> {
//                                // Peripheral側で更新された値をセットする.
//                                receivedValueView.setText(characteristic.getStringValue(0));
//                            });
//                }
//            }
        };

        @Override
        public void onScanFailed(int intErrorCode) {
            Log.d(TAG,"call onScanFailed");
            super.onScanFailed(intErrorCode);
        }
    };

}
