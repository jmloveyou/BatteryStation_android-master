package com.immotor.batterystation.android.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashion on 2017/5/2.
 * 电池桩信息，
 */

public class BatteryStationInfo  implements Serializable{

//    [
//        {
//            "img":"https://immotor.com/Images-1/China.svg",
//            "latitude":22.577416591651726,
//            "name":"大兴地铁站",
//            "available":2,
//            "id":"3C001100",
//            "longitude":113.9264765381813
//        },
//        {
//            "img":"https://immotor.com/Images-1/China.svg",
//            "latitude":22.586959816816638,
//            "name":"大兴地铁站",
//            "available":1,
//            "id":"11141722",
//            "longitude":113.92474383115768
//        }
//    ]


    String img;     //充电站缩略图
    double latitude;
    double longitude;
    String name;
    String pID;      //
    int valid;  //可以更换的电池数量
    int empty;  //空仓数量
    private List<String> ports = new ArrayList<>();  //在订单里表示订单的端口



    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getpID() {
        return pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public int getEmpty() {
        return empty;
    }

    public void setEmpty(int empty) {
        this.empty = empty;
    }

//    public List<String> getPorts() {
//        return ports;
//    }

//    public void setPorts(List<String> ports) {
//        this.ports = ports;
//    }

    public void clone(BatteryStationInfo info){
        img = info.img;     //充电站缩略图
        latitude = info.latitude;
        longitude = info.longitude;
        name = info.name;
        pID = info.pID;      //
        valid = info.valid;  //可以更换的电池数量
        empty = info.empty;
//        ports.clear();
//        ports.addAll(info.ports);
    }

}
