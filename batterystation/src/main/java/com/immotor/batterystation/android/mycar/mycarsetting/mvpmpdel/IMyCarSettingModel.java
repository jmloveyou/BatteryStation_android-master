package com.immotor.batterystation.android.mycar.mycarsetting.mvpmpdel;

import android.content.Context;

/**
 * Created by jm on 2017/9/21 0021.
 */

public interface IMyCarSettingModel {

    void requestUnBindCar(Context context,String token,String sid);

}
