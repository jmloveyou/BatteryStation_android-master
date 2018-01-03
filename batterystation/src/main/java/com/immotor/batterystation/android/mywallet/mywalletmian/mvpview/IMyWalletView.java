package com.immotor.batterystation.android.mywallet.mywalletmian.mvpview;

import com.immotor.batterystation.android.entity.MyWalletBean;

/**
 * Created by jm on 2017/8/3 0003.
 */

public interface IMyWalletView {
    void showEmpty();


    void showFail();

    void addData(MyWalletBean data);
}
