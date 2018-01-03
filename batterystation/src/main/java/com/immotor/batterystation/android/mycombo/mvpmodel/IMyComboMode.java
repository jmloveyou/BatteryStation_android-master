package com.immotor.batterystation.android.mycombo.mvpmodel;

import android.content.Context;


/**
 * Created by jm on 2017/7/31 0031.
 */

public interface IMyComboMode {
    void  requestMyCombo(Context context, String token, MyComboMode.IMyComboListener listener);
    void requestcancleLowerCombo(Context context, String token, MyComboMode.ICancleLowerComboListener listener );

    void requestAutoExpense(Context context, String token,boolean status,MyComboMode.IAutoExpenseListener listener);

    void requestgetAutoStatus(Context context,String token,MyComboMode.IAutoStatusListener listener);

}
