package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by Ashion on 2017/5/4.
 * 电池桩里的单个电池信息
 */

public class BatteryInStationInfo    implements Serializable {
//    "current":10,
//    "port":1,
//    "soc":20,
//    "temperature":1,
//    "fault":1,
//    "nominal_voltage":10,
//    "bID":"0C0000000C00",
//    "cycle":10,
//    "nominal_current":10,
//    "voltage":10,
//    "capacity":12,
//    "status":1
    private int current;
    private int port;
    private int soc;  //剩余电量
    private int temperature;
    private int fault;  //错误码
    private int nominal_voltage;
    private String bID;
    private int cycle;
    private int nominal_current;
    private int voltage;
    private int capacity;
    private int status = 0;    //1表示已经被借了


    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSoc() {
        return soc;
    }

    public void setSoc(int soc) {
        this.soc = soc;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getFault() {
        return fault;
    }

    public void setFault(int fault) {
        this.fault = fault;
    }

    public int getNominal_voltage() {
        return nominal_voltage;
    }

    public void setNominal_voltage(int nominal_voltage) {
        this.nominal_voltage = nominal_voltage;
    }

    public String getbID() {
        return bID;
    }

    public void setbID(String bID) {
        this.bID = bID;
    }

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public int getNominal_current() {
        return nominal_current;
    }

    public void setNominal_current(int nominal_current) {
        this.nominal_current = nominal_current;
    }

    public int getVoltage() {
        return voltage;
    }

    public void setVoltage(int voltage) {
        this.voltage = voltage;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
