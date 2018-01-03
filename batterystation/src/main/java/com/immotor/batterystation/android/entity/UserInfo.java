package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by Ashion on 2017/5/10.
 */

public class UserInfo implements Serializable {
//    "id": 1,
//    "name": null,
//    "phone": "15989548969",
//    "deviceToken": "0",
//    "birthday": null,
//    "avatar": null,
//    "sex": 0,
//    "status": 0,
//    "createTime": 1494395712739,
//    "updateTime": null

    private int id;
    private String name;
    private String phone;
    private String deviceToken;
    private long birthday;
    private String avatar;
    private int sex;
    private int status;
    Object deposit;
    Object amount;
    private long createTime;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
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

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public Object getDeposit() {
        return deposit;
    }

    public void setDeposit(Object deposit) {
        this.deposit = deposit;
    }

    public Object getAmount() {
        return amount;
    }

    public void setAmount(Object amount) {
        this.amount = amount;
    }
}
