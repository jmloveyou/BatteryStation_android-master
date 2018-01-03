package com.immotor.batterystation.android.mywallet.walletcharge.mvppresent;

import android.content.Context;

import com.immotor.batterystation.android.entity.MyChargeRecord;
import com.immotor.batterystation.android.mywallet.walletcharge.mvpmode.ChargeMode;
import com.immotor.batterystation.android.mywallet.walletcharge.mvpmode.IChargeMode;
import com.immotor.batterystation.android.mywallet.walletcharge.mvpview.IChargeView;

import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public class ChargePresent implements IChargePresent, ChargeMode.IWalletChargeListener {
    private IChargeView mView;
    private Context mContext;
    private final IChargeMode mChargeMode;

    public ChargePresent(Context context, IChargeView view) {
        mContext = context;
        mView = view;
        mChargeMode = new ChargeMode();
    }

    @Override
    public void onGetDataSuccess(boolean isCanPullup, List<MyChargeRecord.ContentBean> bean) {
        mView.showNomal();
        mView.addData(isCanPullup, bean);
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
    public void requestChargeRecord(boolean isRefresh, String token,boolean progress) {
        mChargeMode.requestChargeRecord(mContext, isRefresh,token, progress,this);

    }
}
