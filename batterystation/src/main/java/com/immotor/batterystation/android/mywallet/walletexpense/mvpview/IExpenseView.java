package com.immotor.batterystation.android.mywallet.walletexpense.mvpview;

import com.immotor.batterystation.android.entity.MyChargeRecord;
import com.immotor.batterystation.android.entity.MyExpenseRecord;

import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public interface IExpenseView {
    void showEmpty();

    void showNomal();

    void showLoading();

    void showFail();

    void addData(boolean isCanpull, List<MyExpenseRecord.ContentBean> data);
}
