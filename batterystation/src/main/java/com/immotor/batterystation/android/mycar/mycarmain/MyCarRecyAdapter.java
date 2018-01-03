package com.immotor.batterystation.android.mycar.mycarmain;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.CarListBean;

/**
 * Created by jm on 2017/9/14 0014.
 */

public class MyCarRecyAdapter extends BaseQuickAdapter<CarListBean, BaseViewHolder> {
    GeocodeSearch geocoderSearch;
    String address = "";
    public MyCarRecyAdapter(@LayoutRes int layoutResId, Context context) {
        super(layoutResId);
        geocoderSearch = new GeocodeSearch(context);
        initListener();
    }

    private void initListener() {
        geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                if (i == 1000) {
                    address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                    notifyDataSetChanged();
                } else {
                    address = "暂时未获取到位置";
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
    }
    @Override
    protected void convert(BaseViewHolder holder, CarListBean item) {
        getAddress(item.getLocation());
        batteryImg(holder,item.getSoc());
        holder.setText(R.id.my_car_user_name, item.getNickName())
                .setText(R.id.my_car_use_status, getTravelStatus(item.getDeviceState()))
                .setText(R.id.my_car_travel_destance, item.getRemailMiles()+"公里")
                .setText(R.id.my_car_power_tv, item.getSoc()+"%")
                .setText(R.id.my_car_address, address)
                .addOnClickListener(R.id.my_car_setting)
                .addOnClickListener(R.id.my_car_travel_recode)
                .addOnClickListener(R.id.my_car_address_llyt);
    }

    private void batteryImg(BaseViewHolder holder,int mbatteryPower) {
        ImageView view = holder.getView(R.id.my_car_power_img);
        if (0 == mbatteryPower) {
            view.setImageResource(R.mipmap.power_0);
        } else if (0 < mbatteryPower && mbatteryPower < 10) {
            view.setImageResource(R.mipmap.power_1);
        } else if (10 <= mbatteryPower && mbatteryPower < 20) {
            view.setImageResource(R.mipmap.power_2);
        } else if (20 <= mbatteryPower && mbatteryPower < 30) {
            view.setImageResource(R.mipmap.power_3);
        } else if (30 <= mbatteryPower && mbatteryPower < 40) {
            view.setImageResource(R.mipmap.power_4);
        } else if (40 <= mbatteryPower && mbatteryPower < 50) {
            view.setImageResource(R.mipmap.power_5);
        } else if (50 <= mbatteryPower && mbatteryPower < 60) {
            view.setImageResource(R.mipmap.power_6);
        } else if (60 <= mbatteryPower && mbatteryPower < 70) {
            view.setImageResource(R.mipmap.power_7);
        } else if (70 <= mbatteryPower && mbatteryPower < 80) {
            view.setImageResource(R.mipmap.power_8);
        } else if (80 <= mbatteryPower && mbatteryPower < 90) {
            view.setImageResource(R.mipmap.power_9);
        } else if (90 <= mbatteryPower && mbatteryPower < 100) {
            view.setImageResource(R.mipmap.power_9);
        } else if (100 == mbatteryPower) {
            view.setImageResource(R.mipmap.power_10);
        }
    }

    public void getAddress(String location) {
        if (location.length()<=0) {
            return;
        }
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(Double.parseDouble(location.substring(0, location.lastIndexOf(","))), Double.parseDouble(location.substring(location.lastIndexOf(",") + 1))), 200, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);

    }

    private String getTravelStatus(int status) {
        //0 代表熄火，1 代表点火
        String string = "";
        if (status == 0) {
            string = "熄火中";
        } else if (status == 1) {
            string = "行驶中";
        }
        return string;
    }
}
