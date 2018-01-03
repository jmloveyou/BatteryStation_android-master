package com.immotor.batterystation.android.mycar.mycarmain.mvpview;

import com.immotor.batterystation.android.entity.CarHeartBeatEntry;
import com.immotor.batterystation.android.entity.CarListBean;

import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public interface IMyCarView {
    void showEmpty();

    void showNomal();

    void showLoading();

    void showFail();

    void addData( List<CarListBean> data);

    void addHeartBeatData(List<CarHeartBeatEntry> bean);

    void bindCarSucess();
}
