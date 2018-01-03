package com.immotor.batterystation.android.selectcombo.mvpmodel;

import android.content.Context;

/**
 * Created by jm on 2017/7/31 0031.
 */

public interface ISelectComboMode {
    void  requestSelectCombo(Context context, String token, SelectComboMode.ISelectComboListener listener);

    void requestUpdateCombo(Context context, String token, long id,int buycode, double Comboprice ,SelectComboMode.IUpdateComboListener listener);

    void requestLowerCombo(Context context, String token, long id, final int buycode, final double price, SelectComboMode.ILowerComboListener listener);

    void requestBuyCombo(Context context, String token , long id, int code, double price, SelectComboMode.IBuyTimesComboListener listener);

    void requestAutoExpense(Context context, String token,boolean status,SelectComboMode.IAutoExpenseListener listener);

    void requestgetAutoStatus(Context context,String token,SelectComboMode.IAutoStatusListener listener);

}
