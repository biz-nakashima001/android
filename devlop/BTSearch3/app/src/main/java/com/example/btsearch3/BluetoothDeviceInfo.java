package com.example.btsearch3;

/**
 * Created by shinya on 2018/02/13.
 */

public class BluetoothDeviceInfo {
    private String name;
    private String address;
    private int type;
    private Long id= 0L;


    public BluetoothDeviceInfo(String name, String address, int type){
        this.name = name;
        this.address = address;
        this.type = type;
    }

    public void setName(String name){
        this.name = name;
    }

//    public void setAddress(String address){
//        this.address = address;
//    }
//
//    public void setId(Long id){
//        this.id = id;
//    }

    public String getAddress() {
        return address;
    }

    public String getName(){
        return name;
    }

    public int getType(){
        return type;
    }

    public Long getId() {
        return id;
    }


}
