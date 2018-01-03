package com.immotor.batterystation.android.mywallet.walletcharge.mvpview;

import com.immotor.batterystation.android.entity.MyChargeRecord;

import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public interface IChargeView {
    void showEmpty();

    void showNomal();

    void showLoading();

    void showFail();

    void addData(boolean isCannpullup,List<MyChargeRecord.ContentBean> data);
}
