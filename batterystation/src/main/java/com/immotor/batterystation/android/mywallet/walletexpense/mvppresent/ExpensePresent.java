package com.immotor.batterystation.android.mywallet.walletexpense.mvppresent;

import android.content.Context;

import com.immotor.batterystation.android.entity.MyExpenseRecord;
import com.immotor.batterystation.android.mywallet.walletexpense.mvpmode.ExpenseMode;
import com.immotor.batterystation.android.mywallet.walletexpense.mvpmode.IExpenseMode;
import com.immotor.batterystation.android.mywallet.walletexpense.mvpview.IExpenseView;

import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public class ExpensePresent implements IExpensePresent, ExpenseMode.IWalletExpenseListener {
    private IExpenseView mView;
    private String mToken;
    private Context mContext;
    private final IExpenseMode mMode;

    public ExpensePresent(Context context, String token, IExpenseView view) {
        mContext = context;
        mToken = token;
        mView = view;
        mMode = new ExpenseMode();
    }

    @Override
    public void requestExpenseRecord(boolean isRefresh,boolean progress) {
        mView.showNomal();
        mMode.requestExpenseRecord(mContext,isRefresh, mToken,progress, this);
    }

    @Override
    public void onGetDataSuccess(boolean isCanPull, List<MyExpenseRecord.ContentBean> bean) {
        mView.addData(isCanPull, bean);
    }

    @Override
    public void onGetDataEmpty() {
        mView.showEmpty();

    }

    @Override
    public void onGetDataFailure() {
        mView.showFail();
    }
}
