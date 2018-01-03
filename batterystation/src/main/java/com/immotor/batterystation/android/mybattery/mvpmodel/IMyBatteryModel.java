package com.immotor.batterystation.android.mybattery.mvpmodel;

import android.content.Context;

/**
 * Created by jm on 2017/7/28 0028.
 */

public interface IMyBatteryModel {
    void requestMyBattery(Context context,String token,MyBatteryModel.IMyBatterylistener listener);

    void requestBatteryBuy(Context context,String token,int quantity);
}
