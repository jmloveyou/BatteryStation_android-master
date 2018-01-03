package com.immotor.batterystation.android.mywallet.mywalletmian.mvppresent;

import android.content.Context;

import com.immotor.batterystation.android.entity.MyWalletBean;
import com.immotor.batterystation.android.mywallet.mywalletmian.mvpmodel.IMyWalletModel;
import com.immotor.batterystation.android.mywallet.mywalletmian.mvpmodel.MyWalletMdel;
import com.immotor.batterystation.android.mywallet.mywalletmian.mvpview.IMyWalletView;

/**
 * Created by jm on 2017/8/3 0003.
 */

public class MyWalletPresent implements IMyWalletPresent, MyWalletMdel.IMyWalletListener {

    private IMyWalletView mView;
    private Context mContext;
    private final IMyWalletModel mMyWallet;

    public MyWalletPresent(Context context, IMyWalletView view) {
        mContext = context;
        mView = view;
        mMyWallet = new MyWalletMdel();
    }

    @Override
    public void requestChargeRecord(String token) {
        mMyWallet.requestMyWalletRecord(mContext, token, this);
    }

    @Override
    public void onGetDataSuccess(MyWalletBean data) {
        mView.addData(data);
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
