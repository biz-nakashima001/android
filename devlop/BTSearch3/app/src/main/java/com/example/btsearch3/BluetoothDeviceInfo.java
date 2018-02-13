package com.example.btsearch3;

/**
 * Created by shinya on 2018/02/13.
 */

public class BluetoothDeviceInfo {
    private String address;
    private String name;

    public BluetoothDeviceInfo(String address, String name){
        this.address = address;
        this.name = name;
    }

    public String getAddress(){
        return address;
    }

    public String getName(){
        return name;
    }


}
