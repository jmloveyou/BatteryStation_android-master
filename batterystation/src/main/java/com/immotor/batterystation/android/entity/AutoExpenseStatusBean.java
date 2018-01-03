package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by jm on 2017/8/30 0030.
 */

public class AutoExpenseStatusBean implements Serializable {

        /**
         * id : 14
         * cron : 0 0 0 1/1 * ?
         * status : 0
         * createTime : 1504081095000
         * uID : 11
         * auto : 0
         * packageID : 1
         * day : 10
         */

        private int id;
        private String cron;
        private int status;
        private long createTime;
        private int uID;
        private int auto;
        private int packageID;
        private int day;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getCron() {
            return cron;
        }

        public void setCron(String cron) {
            this.cron = cron;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public int getUID() {
            return uID;
        }

        public void setUID(int uID) {
            this.uID = uID;
        }

        public int getAuto() {
            return auto;
        }

        public void setAuto(int auto) {
            this.auto = auto;
        }

        public int getPackageID() {
            return packageID;
        }

        public void setPackageID(int packageID) {
            this.packageID = packageID;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }
}
