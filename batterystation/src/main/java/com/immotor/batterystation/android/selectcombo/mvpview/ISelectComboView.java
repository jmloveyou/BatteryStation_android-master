package com.immotor.batterystation.android.selectcombo.mvpview;

import com.immotor.batterystation.android.entity.MyComboBean;
import com.immotor.batterystation.android.entity.SelectComboBean;

import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public interface ISelectComboView {
    void showEmpty();

    void showNomal();

    void showLoading();

    void showFail();

    void addData( List<SelectComboBean> data);

    void BuyComboSuccess();

    void getAutoStatusSucess(int status);

    void autoExpenseResultSucess(int status);
    void getAutoStatusFail();

    void autoExpenseResultFail(int status);
}
