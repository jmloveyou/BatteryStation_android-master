package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by jm on 2017/9/20 0020.
 */

public class TripBean implements Serializable {
        /**
         * sPlaceName : 广东省深圳市宝安区新安街道宝石路
         * eLocation : 113.927252,22.579887
         * eTime : 1469699402744
         * costTime : 0
         * ePlaceName : 广东省深圳市宝安区新安街道安通达工业园4栋
         * soc : 0
         * sTime : 1469699402103
         * sLocation : 113.922868,22.585368
         * miles : 0
         */

        private String sPlaceName;
        private String eLocation;
        private long eTime;
        private int costTime;
        private String ePlaceName;
        private int soc;
        private long sTime;
        private String sLocation;
        private int miles;

        public String getSPlaceName() {
            return sPlaceName;
        }

        public void setSPlaceName(String sPlaceName) {
            this.sPlaceName = sPlaceName;
        }

        public String getELocation() {
            return eLocation;
        }

        public void setELocation(String eLocation) {
            this.eLocation = eLocation;
        }

        public long getETime() {
            return eTime;
        }

        public void setETime(long eTime) {
            this.eTime = eTime;
        }

        public int getCostTime() {
            return costTime;
        }

        public void setCostTime(int costTime) {
            this.costTime = costTime;
        }

        public String getEPlaceName() {
            return ePlaceName;
        }

        public void setEPlaceName(String ePlaceName) {
            this.ePlaceName = ePlaceName;
        }

        public int getSoc() {
            return soc;
        }

        public void setSoc(int soc) {
            this.soc = soc;
        }

        public long getSTime() {
            return sTime;
        }

        public void setSTime(long sTime) {
            this.sTime = sTime;
        }

        public String getSLocation() {
            return sLocation;
        }

        public void setSLocation(String sLocation) {
            this.sLocation = sLocation;
        }

        public int getMiles() {
            return miles;
        }

        public void setMiles(int miles) {
            this.miles = miles;
        }
}
