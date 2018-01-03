package com.immotor.batterystation.android.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jm on 2017/9/20 0020.
 */

public class TripDayBean implements Serializable {

        /**
         * date : 20160728
         * data : [{"sPlaceName":"广东省深圳市宝安区新安街道宝石路","eLocation":"113.927252,22.579887","eTime":1469699402744,"costTime":0,"ePlaceName":"广东省深圳市宝安区新安街道安通达工业园4栋","soc":0,"sTime":1469699402103,"sLocation":"113.922868,22.585368","miles":0}]
         * costTime : 0
         * costSoc : 1200
         * sID : 111111AAAAAA
         */

        private String date;
        private int costTime;
        private int costSoc;
        private String sID;
        private List<TripBean> data;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getCostTime() {
            return costTime;
        }

        public void setCostTime(int costTime) {
            this.costTime = costTime;
        }

        public int getCostSoc() {
            return costSoc;
        }

        public void setCostSoc(int costSoc) {
            this.costSoc = costSoc;
        }

        public String getSID() {
            return sID;
        }

        public void setSID(String sID) {
            this.sID = sID;
        }

        public List<TripBean> getData() {
            return data;
        }

        public void setData(List<TripBean> data) {
            this.data = data;
        }


}
