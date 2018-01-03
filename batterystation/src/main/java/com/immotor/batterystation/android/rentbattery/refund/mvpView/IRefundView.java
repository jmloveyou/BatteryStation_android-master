package com.immotor.batterystation.android.rentbattery.refund.mvpView;

import com.immotor.batterystation.android.entity.MybatteryListBean;
import com.immotor.batterystation.android.entity.RefundPayListBean;

/**
 *
 * Created by jm on 2017/7/27 0027.
 */

public interface IRefundView {
    void addData(RefundPayListBean bean);

    void onBatteryShow();

    void BatteryListShow();

    void getBatteryFail();
}
