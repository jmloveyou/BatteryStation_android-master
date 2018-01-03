package com.immotor.batterystation.android.mywallet.walletcharge.mvppresent;

/**
 * Created by jm on 2017/7/31 0031.
 */

public interface IChargePresent {
   void  requestChargeRecord(boolean isRefresh,String token,boolean progress);
}
