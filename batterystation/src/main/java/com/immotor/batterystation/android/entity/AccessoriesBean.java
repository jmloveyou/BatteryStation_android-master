package com.immotor.batterystation.android.entity;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

/**
 * Created by Ashion on 2017/1/16.
 */

public class AccessoriesBean implements Serializable {

    private String name;

    private int type;       // 0: dock  1: light

    private int status;     //若是dock，0是idle，2是charge

    private BluetoothDevice device;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
}
