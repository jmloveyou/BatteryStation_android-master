package com.immotor.batterystation.android.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jm on 2017/8/4 0004.
 */

public class SelectComboBean implements Serializable {

    /**
     * id : 1
     * name : 12次月套餐
     * type : 0
     * status : 1
     * original_price : 0.01
     * price : 0.01
     * duration : 180000
     * times : 12
     * createTime : 1496718960000
     */

    private int id;
    private String name;
    private int type;
    private int status;
    private double original_price;
    private double price;
    private int duration;
    private int times;
    private long createTime;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private int code;

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

    public double getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(double original_price) {
        this.original_price = original_price;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}

