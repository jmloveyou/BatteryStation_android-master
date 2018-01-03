package com.immotor.batterystation.android.mycombo.mvppresent;

import android.content.Context;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.MyComboBean;
import com.immotor.batterystation.android.mycombo.mvpmodel.MyComboMode;
import com.immotor.batterystation.android.mycombo.mvpmodel.IMyComboMode;
import com.immotor.batterystation.android.mycombo.mvpview.IMyComboView;

import java.util.List;

/**
 *
 * Created by jm on 2017/7/31 0031.
 */

public class MyComboPresent implements IMyComboPresent, MyComboMode.IMyComboListener,MyComboMode.ICancleLowerComboListener,MyComboMode.IAutoStatusListener,MyComboMode.IAutoExpenseListener{
    private IMyComboView mView;
    private Context mContext;
    private IMyComboMode mMyComboMode;

    public MyComboPresent(Context context, IMyComboView view) {
        mContext = context;
        mView = view;
        mMyComboMode = new MyComboMode();
    }

    @Override
    public void onGetDataSuccess( List<MyComboBean> bean) {
        mView.showNomal();
        mView.addData(bean);
    }

    @Override
    public void onGetDataEmpty() {
        mView.showEmpty();
    }

    @Override
    public void onGetDataFailure() {
        mView.showFail();

    }
    @Override
    public void requestCancleLowerCombo(String token) {
        mMyComboMode.requestcancleLowerCombo(mContext,token,this);
    }

    @Override
    public void requestAutoExpense(String token, boolean status) {
        mMyComboMode.requestAutoExpense(mContext,token,status,this);
    }

    @Override
    public void requestgetAutoStatus(String token) {
        mMyComboMode.requestgetAutoStatus(mContext,token,this);
    }

    @Override
    public void requestMyCombo( String token) {
        mMyComboMode.requestMyCombo(mContext,token,this);

    }

    @Override
    public void onCanclerLowerComboSuccess(Object bean) {
        Toast.makeText(mContext, R.string.cancle_combo_degrade_sucess, Toast.LENGTH_SHORT).show();
        mView.RefreshView();
    }

    @Override
    public void onCanclerLowerComboFailure() {
        Toast.makeText(mContext, R.string.cancle_combo_degrade_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAutoExpenseDataSuccess(int status) {
    mView.autoExpenseResult(status);
    }

    @Override
    public void onAutoExpenseDataFailure(int status) {
    mView.autoExpenseResultFail(status);
    }

    @Override
    public void onGetAutoStatusSuccess(int status) {
    mView.getAutoStatus(status);
    }

    @Override
    public void onGetAutoStatusDataFailure() {
    mView.getAutoStatusFail();
    }
}
