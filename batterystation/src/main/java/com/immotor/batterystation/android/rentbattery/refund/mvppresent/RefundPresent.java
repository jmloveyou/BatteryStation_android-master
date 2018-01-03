package com.immotor.batterystation.android.rentbattery.refund.mvppresent;

import android.content.Context;

import com.immotor.batterystation.android.entity.MybatteryListBean;
import com.immotor.batterystation.android.entity.RefundPayListBean;
import com.immotor.batterystation.android.rentbattery.refund.mvpView.IRefundView;
import com.immotor.batterystation.android.rentbattery.refund.mvpmodel.IRefundModel;
import com.immotor.batterystation.android.rentbattery.refund.mvpmodel.RefundModel;

/**
 * Created by jm on 2017/7/27 0027.
 */

public class RefundPresent implements IRefundPresent,RefundModel.IRefundListlistener {

    private String mToken;
    private IRefundView mView;
    private Context mContext;
    private IRefundModel mMyBatteryModel;

    public RefundPresent(Context context, IRefundView view, String token) {
        this.mContext = context;
        this.mView = view;
        this.mToken = token;
        mMyBatteryModel = new RefundModel();
    }

    @Override
    public void requestRefundList() {
        mMyBatteryModel.requestRefundListM(mContext,mToken,this);
    }

    @Override
    public void requestRefund(String id) {
        mMyBatteryModel.requestRefundM(mContext,mToken,id);

    }

    @Override
    public void onGetDataSuccess(RefundPayListBean mybatteryListBean) {
        mView.addData(mybatteryListBean);
        mView.BatteryListShow();
    }

    @Override
    public void onGetDataEmpty() {
        mView.onBatteryShow();
    }

    @Override
    public void onGetDataFailure() {
        mView.getBatteryFail();
    }
}
