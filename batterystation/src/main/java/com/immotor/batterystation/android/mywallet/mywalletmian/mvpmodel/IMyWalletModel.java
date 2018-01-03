package com.immotor.batterystation.android.mywallet.mywalletmian.mvpmodel;

import android.content.Context;


/**
 * Created by jm on 2017/8/3 0003.
 */

public interface IMyWalletModel {
    void  requestMyWalletRecord(Context context,String token ,MyWalletMdel.IMyWalletListener listener);

}
