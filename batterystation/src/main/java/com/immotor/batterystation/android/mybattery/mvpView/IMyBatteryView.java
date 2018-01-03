package com.immotor.batterystation.android.mybattery.mvpView;

import com.immotor.batterystation.android.entity.MybatteryListBean;

/**
 *
 * Created by jm on 2017/7/27 0027.
 */

public interface IMyBatteryView {
    void addData(MybatteryListBean bean);

    void onBatteryShow();

    void BatteryListShow();

    void getBatteryFail();
}
