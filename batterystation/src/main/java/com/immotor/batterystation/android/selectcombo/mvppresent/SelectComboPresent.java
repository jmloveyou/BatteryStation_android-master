package com.immotor.batterystation.android.selectcombo.mvppresent;

import android.content.Context;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.entity.SelectComboBean;
import com.immotor.batterystation.android.selectcombo.mvpmodel.ISelectComboMode;
import com.immotor.batterystation.android.selectcombo.mvpmodel.SelectComboMode;
import com.immotor.batterystation.android.selectcombo.mvpview.ISelectComboView;

import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public class SelectComboPresent implements ISelectComboPresent, SelectComboMode.ISelectComboListener, SelectComboMode.ILowerComboListener, SelectComboMode.IUpdateComboListener, SelectComboMode.IBuyTimesComboListener, SelectComboMode.IAutoExpenseListener, SelectComboMode.IAutoStatusListener {
    private ISelectComboView mView;
    private Context mContext;
    private ISelectComboMode mSelectComboMode;

    public SelectComboPresent(Context context, ISelectComboView view) {
        mContext = context;
        mView = view;
        mSelectComboMode = new SelectComboMode();
    }

    @Override
    public void onGetDataSuccess(List<SelectComboBean> bean) {
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
    public void requestSelectCombo(String token) {
        mSelectComboMode.requestSelectCombo(mContext, token, this);

    }

    @Override
    public void requestUpdateCombo(String token, long id,int buycode, double comboprice) {
        mSelectComboMode.requestUpdateCombo(mContext, token, id,buycode,comboprice, this);
    }

    @Override
    public void requestLowerCombo(String token, long id,  int buycode, double price) {
        mSelectComboMode.requestLowerCombo(mContext, token, id,buycode,price, this);
    }

    @Override
    public void requestBuyCombo(String token, long id,int code,double price) {
        mSelectComboMode.requestBuyCombo(mContext, token, id,code,price, this);
    }

    @Override
    public void requestAutoExpense(String token, boolean status) {
        mSelectComboMode.requestAutoExpense(mContext, token, status, this);
    }

    @Override
    public void requestgetAutoStatus(String token) {
        mSelectComboMode.requestgetAutoStatus(mContext, token, this);
    }

    @Override
    public void onGetUpdateDataSuccess(Object bean) {
        //暂时这样处理
        Toast.makeText(mContext, R.string.combo_update_scuess, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetUpdateDataFailure() {
        Toast.makeText(mContext, R.string.combo_update_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetLowerDataSuccess(Object bean) {
        Toast.makeText(mContext, R.string.combo_degrade_scuess, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetLowerDataFailure() {
        Toast.makeText(mContext, R.string.combo_degrade_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetBuyTimesDataSuccess(Object object) {
        mView.BuyComboSuccess();
        Toast.makeText(mContext, R.string.combo_buy_scuess, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onGetBuyTimesDataFailure() {
        Toast.makeText(mContext, R.string.combo_buy_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAutoExpenseDataSuccess(int status) {
        if (status == AppConstant.AUTO_EXPENSE_STATUS_OPEN_SUCCESS_COD) {
            mView.autoExpenseResultSucess(AppConstant.AUTO_EXPENSE_STATUS_OPEN_SUCCESS_COD);
        } else {
            mView.autoExpenseResultSucess(AppConstant.AUTO_EXPENSE_STATUS_CLOSE_SUCCESS_COD);
        }
    }

    @Override
    public void onAutoExpenseDataFailure(int status) {
        mView.autoExpenseResultFail(status);
    }

    @Override
    public void onGetAutoStatusSuccess(int status) {
        if (status == AppConstant.AUTO_EXPENSE_STATUS_OPEN_COD) {
            mView.getAutoStatusSucess(AppConstant.AUTO_EXPENSE_STATUS_OPEN_COD);
        } else {
            mView.getAutoStatusSucess(AppConstant.AUTO_EXPENSE_STATUS_CLOSE_COD);
        }
    }

    @Override
    public void onGetAutoStatusDataFailure() {
        mView.getAutoStatusFail();
      //  mView.getAutoStatus(AppConstant.AUTO_EXPENSE_STATUS_QUERY_FAIL_COD);

    }
}
