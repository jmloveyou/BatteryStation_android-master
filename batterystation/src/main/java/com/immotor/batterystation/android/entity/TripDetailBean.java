package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by jm on 2017/9/21 0021.
 */

public class TripDetailBean implements Serializable {
        /**
         * soc : 70
         * location : 114.065391,22.535637
         * time : 1500875773601
         */

        private int soc;
        private String location;
        private long time;

        public int getSoc() {
            return soc;
        }

        public void setSoc(int soc) {
            this.soc = soc;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
}
