package com.immotor.batterystation.android.mywallet.walletexpense.mvpmode;

import android.content.Context;

/**
 * Created by jm on 2017/7/31 0031.
 */

public interface IExpenseMode {
    void  requestExpenseRecord(Context context,boolean isRefresh, String token,boolean progress, ExpenseMode.IWalletExpenseListener listener);
}
