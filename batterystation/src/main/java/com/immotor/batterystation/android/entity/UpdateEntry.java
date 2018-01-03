package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by jm on 2017/9/22 0022.
 */

public class UpdateEntry implements Serializable {

        /**
         * id : 002
         * sID : 00000002
         * owner : 18194082860
         * status : 1
         * nickName : dididi
         * macAddress : 00000002
         * version : T1
         * fw_version : 1.0.0
         * hw_version : 1.7
         * country : 3
         * sn : T1A1A040003
         * productionDate : 1480521600000
         * conf : 3
         * key : 33299924236ab42f
         * remailMiles : 4500
         * time : 1501054780033
         * deviceState : 0
         * location : 22.586968,-113.924748
         * soc : 45
         */

        private String id;
        private String sID;
        private String owner;
        private int status;
        private String nickName;
        private String macAddress;
        private String version;
        private String fw_version;
        private String hw_version;
        private int country;
        private String sn;
        private long productionDate;
        private int conf;
        private String key;
        private Double remailMiles;
        private long time;
        private int deviceState;
        private String location;
        private int soc;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSID() {
            return sID;
        }

        public void setSID(String sID) {
            this.sID = sID;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getFw_version() {
            return fw_version;
        }

        public void setFw_version(String fw_version) {
            this.fw_version = fw_version;
        }

        public String getHw_version() {
            return hw_version;
        }

        public void setHw_version(String hw_version) {
            this.hw_version = hw_version;
        }

        public int getCountry() {
            return country;
        }

        public void setCountry(int country) {
            this.country = country;
        }

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public long getProductionDate() {
            return productionDate;
        }

        public void setProductionDate(long productionDate) {
            this.productionDate = productionDate;
        }

        public int getConf() {
            return conf;
        }

        public void setConf(int conf) {
            this.conf = conf;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Double getRemailMiles() {
            return remailMiles;
        }

        public void setRemailMiles(Double remailMiles) {
            this.remailMiles = remailMiles;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public int getDeviceState() {
            return deviceState;
        }

        public void setDeviceState(int deviceState) {
            this.deviceState = deviceState;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getSoc() {
            return soc;
        }

        public void setSoc(int soc) {
            this.soc = soc;
        }
}
