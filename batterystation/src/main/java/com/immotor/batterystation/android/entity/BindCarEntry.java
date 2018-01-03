package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by jm on 2017/9/18 0018.
 */

public class BindCarEntry implements Serializable {

        /**
         * id : 002
         * sID : 00000002
         * owner : null
         * status : 0
         * nickName : ec
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
        private Object owner;
        private Integer status;
        private String nickName;
        private String macAddress;
        private String version;
        private String fw_version;
        private String hw_version;
        private Integer country;
        private String sn;
        private Long productionDate;
        private Integer conf;
        private String key;
        private Double remailMiles;
        private Long time;
        private Integer deviceState;
        private String location;
        private Integer soc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    public Object getOwner() {
        return owner;
    }

    public void setOwner(Object owner) {
        this.owner = owner;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Long getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Long productionDate) {
        this.productionDate = productionDate;
    }

    public Integer getConf() {
        return conf;
    }

    public void setConf(Integer conf) {
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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
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

    public Integer getSoc() {
        return soc;
    }

    public void setSoc(Integer soc) {
        this.soc = soc;
    }
}
