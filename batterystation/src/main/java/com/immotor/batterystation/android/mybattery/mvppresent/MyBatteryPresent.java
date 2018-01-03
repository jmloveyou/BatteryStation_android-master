package com.immotor.batterystation.android.mybattery.mvppresent;

import android.content.Context;

import com.immotor.batterystation.android.entity.MybatteryListBean;
import com.immotor.batterystation.android.mybattery.mvpmodel.IMyBatteryModel;
import com.immotor.batterystation.android.mybattery.mvpmodel.MyBatteryModel;
import com.immotor.batterystation.android.mybattery.mvpView.IMyBatteryView;

/**
 * Created by jm on 2017/7/27 0027.
 */

public class MyBatteryPresent implements IMyBatteryPresent ,MyBatteryModel.IMyBatterylistener {

    private String mToken;
    private IMyBatteryView mView;
    private Context mContext;
    private  IMyBatteryModel mMyBatteryModel;

    public MyBatteryPresent(Context context, IMyBatteryView view, String token) {
        this.mContext = context;
        this.mView = view;
        this.mToken = token;
        mMyBatteryModel = new MyBatteryModel();
    }

    @Override
    public void requestBatteryMsgList() {
        mMyBatteryModel.requestMyBattery(mContext,mToken,this);
    }

    @Override
    public void requestBatteryBuy(int num) {
        mMyBatteryModel.requestBatteryBuy(mContext,mToken,num);

    }

    @Override
    public void onGetDataSuccess(MybatteryListBean mybatteryListBean) {
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
