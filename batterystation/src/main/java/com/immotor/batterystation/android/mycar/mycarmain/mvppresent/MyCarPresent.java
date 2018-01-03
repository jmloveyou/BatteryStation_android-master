package com.immotor.batterystation.android.mycar.mycarmain.mvppresent;

import android.content.Context;
import android.widget.Toast;

import com.immotor.batterystation.android.entity.BindCarEntry;
import com.immotor.batterystation.android.entity.CarHeartBeatEntry;
import com.immotor.batterystation.android.entity.CarListBean;
import com.immotor.batterystation.android.http.carhttp.CarHttpFailMessage;
import com.immotor.batterystation.android.mycar.mycarmain.mvpmodel.MyCarMode;
import com.immotor.batterystation.android.mycar.mycarmain.mvpmodel.IMyCarMode;
import com.immotor.batterystation.android.mycar.mycarmain.mvpview.IMyCarView;

import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public class MyCarPresent implements IMyCarPresent, MyCarMode.IMyCarListener, MyCarMode.IBindcarListener, MyCarMode.IHeartBeatListener {
    private IMyCarView mView;
    private Context mContext;
    private IMyCarMode mMyCarMode;
    private String mToken;

    public MyCarPresent(Context context, IMyCarView view) {
        mContext = context;
        mView = view;
        mMyCarMode = new MyCarMode();
    }

    @Override
    public void onGetDataSuccess(List<CarListBean> bean) {
        mView.showNomal();
        mView.addData(bean);

    }

    @Override
    public void onGetDataEmpty() {
        mView.showEmpty();
    }

    @Override
    public void onGetDataFailure(Throwable e) {
        mView.showFail();
    }

    @Override
    public void requestBatteryCar(String token) {
        this.mToken = token;
        mMyCarMode.requestBatteryCar(mContext, token, this);

    }

    @Override
    public void requestBindCar(String token, int zone, String sId) {
        mMyCarMode.requestBindCar(mContext, token, sId, zone, this);
    }

    @Override
    public void requestHeartBeat(String token, String sIds) {
        mMyCarMode.requestHeartBeat(mContext, token, sIds, this);
    }

    @Override
    public void onGetonBindCarSuccess(BindCarEntry bean) {
        mView.bindCarSucess();
    }

    @Override
    public void onGetonBindCarDataEmpty() {

    }

    @Override
    public void onGetonBindCarDataFailure(Throwable e) {
        CarHttpFailMessage.carfailMessageShow(mContext, null, e);
    }

    @Override
    public void onGetHeartBeatSuccess(List<CarHeartBeatEntry> bean) {
        mView.addHeartBeatData(bean);
    }

    @Override
    public void onGetHeartBeatDataEmpty() {

    }

    @Override
    public void onGetHeartBeatDataFailure(Throwable e) {
        CarHttpFailMessage.carfailMessageShow(mContext, null, e);
    }
}
