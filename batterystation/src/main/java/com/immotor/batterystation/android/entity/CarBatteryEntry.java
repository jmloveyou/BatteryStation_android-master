package com.immotor.batterystation.android.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jm on 2017/9/19 0019.
 */

public class CarBatteryEntry implements Serializable {

        /**
         * bats : [{"nominalVoltage":100,"nominalCurrent":120,"damage":3,"current":10,"soc":90,"temperature":20,"fault":63,"id":"1103DCB1B1AE","cycleCount":10,"portNumber":0,"voltage":10,"capacity":10},{"nominalVoltage":100,"nominalCurrent":120,"damage":2,"current":10,"soc":0,"temperature":15,"fault":42,"id":"1203DCB1B1AE","cycleCount":255,"portNumber":0,"voltage":10,"capacity":10}]
         * packCurrent : 31
         * time : 2815265169735935
         * batteryCount : 2
         * packState : 1
         * soc : 45
         * version : 1
         * maxOutputCurrent : 30
         * packVoltage : 30
         * portState : 3
         * sID : 00000001
         * deviceState : 0
         * location : 22.586968,-113.924748
         */

        private Integer packCurrent;
        private Long time;
        private Integer batteryCount;
        private Integer packState;
        private Integer soc;
        private Integer version;
        private Integer maxOutputCurrent;
        private Integer packVoltage;
        private Integer portState;
        private String sID;
        private Integer deviceState;
        private String location;
        private List<BatsBean> bats;

    public Integer getPackCurrent() {
        return packCurrent;
    }

    public void setPackCurrent(Integer packCurrent) {
        this.packCurrent = packCurrent;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Integer getBatteryCount() {
        return batteryCount;
    }

    public void setBatteryCount(Integer batteryCount) {
        this.batteryCount = batteryCount;
    }

    public Integer getPackState() {
        return packState;
    }

    public void setPackState(Integer packState) {
        this.packState = packState;
    }

    public Integer getSoc() {
        return soc;
    }

    public void setSoc(Integer soc) {
        this.soc = soc;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getMaxOutputCurrent() {
        return maxOutputCurrent;
    }

    public void setMaxOutputCurrent(Integer maxOutputCurrent) {
        this.maxOutputCurrent = maxOutputCurrent;
    }

    public Integer getPackVoltage() {
        return packVoltage;
    }

    public void setPackVoltage(Integer packVoltage) {
        this.packVoltage = packVoltage;
    }

    public Integer getPortState() {
        return portState;
    }

    public void setPortState(Integer portState) {
        this.portState = portState;
    }

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    public Integer getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(Integer deviceState) {
        this.deviceState = deviceState;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<BatsBean> getBats() {
        return bats;
    }

    public void setBats(List<BatsBean> bats) {
        this.bats = bats;
    }
}
