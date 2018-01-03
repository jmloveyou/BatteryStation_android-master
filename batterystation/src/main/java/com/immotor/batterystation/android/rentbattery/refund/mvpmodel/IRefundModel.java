package com.immotor.batterystation.android.rentbattery.refund.mvpmodel;

import android.content.Context;

/**
 * Created by jm on 2017/7/28 0028.
 */

public interface IRefundModel {
    void requestRefundListM(Context context, String token, RefundModel.IRefundListlistener listener);

    void requestRefundM(Context context, String token, String id);
}
