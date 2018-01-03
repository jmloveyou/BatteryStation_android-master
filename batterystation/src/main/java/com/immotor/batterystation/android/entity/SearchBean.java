package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by jm on 2016/12/19.
 */
public class SearchBean implements Serializable{

    private String title;

    private String placeId;

    private double lat;

    private double lng;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
