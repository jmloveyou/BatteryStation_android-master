package com.immotor.batterystation.android.mycar.mycarsetting.mvppresent;

import android.content.Context;

import com.immotor.batterystation.android.mycar.mycarsetting.mvpmpdel.IMyCarSettingModel;
import com.immotor.batterystation.android.mycar.mycarsetting.mvpmpdel.MyCarSettingModel;

/**
 * Created by jm on 2017/9/22 0022.
 */

public class MyCarSettingPresent implements IMyCarSettingPresent {

    private  Context mContext;
    private  String mToken;
    private final IMyCarSettingModel mCarSettingModel;

    public MyCarSettingPresent(Context context, String token) {
        mContext = context;
        mToken = token;
        mCarSettingModel = new MyCarSettingModel();
    }

    @Override
    public void requestUnBindCar(String sid) {
        mCarSettingModel.requestUnBindCar(mContext,mToken,sid);
    }

}
