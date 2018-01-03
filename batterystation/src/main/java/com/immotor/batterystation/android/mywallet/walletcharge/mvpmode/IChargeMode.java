package com.immotor.batterystation.android.mywallet.walletcharge.mvpmode;

import android.content.Context;

/**
 * Created by jm on 2017/7/31 0031.
 */

public interface IChargeMode {
    void  requestChargeRecord(Context context,boolean isRefresh,String token ,boolean progress,ChargeMode.IWalletChargeListener listener);
}
