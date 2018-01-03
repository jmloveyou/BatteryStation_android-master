package com.immotor.batterystation.android.mycar.mycarmain.mvppresent;

/**
 * Created by jm on 2017/7/31 0031.
 */

public interface IMyCarPresent {
   void  requestBatteryCar( String token);

   void requestBindCar(String token,int zone,String sId);

   void requestHeartBeat(String token,String sIds);
}
