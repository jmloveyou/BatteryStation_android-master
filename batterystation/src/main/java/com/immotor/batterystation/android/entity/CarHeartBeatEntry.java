package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by jm on 2017/9/18 0018.
 */

public class CarHeartBeatEntry implements Serializable {
        /**
         * remailMiles : 0
         * time : null
         * deviceState : null
         * location : 22.577497,113.926989
         * soc : null
         */

        private Double remailMiles;
        private Object time;
        private Object deviceState;
        private String location;
        private Object soc;

    public Double getRemailMiles() {
        return remailMiles;
    }

    public void setRemailMiles(Double remailMiles) {
        this.remailMiles = remailMiles;
    }

    public Object getTime() {
        return time;
    }

    public void setTime(Object time) {
        this.time = time;
    }

    public Object getDeviceState() {
        return deviceState;
    }

    public void setDeviceState(Object deviceState) {
        this.deviceState = deviceState;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Object getSoc() {
        return soc;
    }

    public void setSoc(Object soc) {
        this.soc = soc;
    }
}
