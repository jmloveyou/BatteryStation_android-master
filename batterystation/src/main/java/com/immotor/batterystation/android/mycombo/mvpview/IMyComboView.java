package com.immotor.batterystation.android.mycombo.mvpview;

import com.immotor.batterystation.android.entity.MyComboBean;

import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public interface IMyComboView {
    void showEmpty();

    void showNomal();

    void showLoading();

    void showFail();

    void addData(List<MyComboBean> data);

    void RefreshView();

    void getAutoStatus(int status);

    void autoExpenseResult(int status);

    void getAutoStatusFail();

    void autoExpenseResultFail(int status);
}
