package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by jm
 */

public class MyRentInfo implements Serializable {

    /**
     * powerStation : {"id":5,"name":"中国广东省深圳市南山区西丽街道同乐路","pID":"efe7245903623c24","img":"http://imgcn.immotor.com/power/power/img/1501552589429filename.jpg","status":0,"longitude":113.926232,"latitude":22.577438,"createTime":null,"updateTime":1501552590000}
     * uID : 11
     * finishTime : null
     * code : 1937
     * createTime : 1503048929511
     * expenseTime : null
     * num : 1
     * expire : 90
     * pID : efe7245903623c24
     * id : null
     * type : 2
     * status : 0
     */

    private PowerStationBean powerStation;
    private int uID;
    private Object finishTime;
    private int code;
    private long createTime;
    private Object expenseTime;
    private int num;
    private int expire;
    private String pID;
    private Object id;
    private int type;
    private int status;

    public PowerStationBean getPowerStation() {
        return powerStation;
    }

    public void setPowerStation(PowerStationBean powerStation) {
        this.powerStation = powerStation;
    }

    public int getUID() {
        return uID;
    }

    public void setUID(int uID) {
        this.uID = uID;
    }

    public Object getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Object finishTime) {
        this.finishTime = finishTime;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Object getExpenseTime() {
        return expenseTime;
    }

    public void setExpenseTime(Object expenseTime) {
        this.expenseTime = expenseTime;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public String getPID() {
        return pID;
    }

    public void setPID(String pID) {
        this.pID = pID;
    }

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
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

    public static class PowerStationBean {
        /**
         * id : 5
         * name : 中国广东省深圳市南山区西丽街道同乐路
         * pID : efe7245903623c24
         * img : http://imgcn.immotor.com/power/power/img/1501552589429filename.jpg
         * status : 0
         * longitude : 113.926232
         * latitude : 22.577438
         * createTime : null
         * updateTime : 1501552590000
         */

        private int id;
        private String name;
        private String pID;
        private String img;
        private int status;
        private double longitude;
        private double latitude;
        private Object createTime;
        private long updateTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPID() {
            return pID;
        }

        public void setPID(String pID) {
            this.pID = pID;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public Object getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Object createTime) {
            this.createTime = createTime;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }
    }
}

