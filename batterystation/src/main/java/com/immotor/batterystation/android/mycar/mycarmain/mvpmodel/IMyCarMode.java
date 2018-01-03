package com.immotor.batterystation.android.mycar.mycarmain.mvpmodel;

import android.content.Context;

/**
 * Created by jm on 2017/7/31 0031.
 */

public interface IMyCarMode {
    void  requestBatteryCar(Context context, String token, MyCarMode.IMyCarListener listener);

    void requestBindCar(Context context, String token,String sId,int zone ,MyCarMode.IBindcarListener listener);

    void requestHeartBeat(Context context, String token,String sIds ,MyCarMode.IHeartBeatListener listener);
}
