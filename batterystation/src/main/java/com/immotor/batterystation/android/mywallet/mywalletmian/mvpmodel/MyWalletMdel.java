package com.immotor.batterystation.android.mywallet.mywalletmian.mvpmodel;

import android.content.Context;

import com.immotor.batterystation.android.entity.MyWalletBean;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.mywallet.walletcharge.mvpmode.ChargeMode;
import com.immotor.batterystation.android.util.LogUtil;

/**
 *
 * Created by jm on 2017/8/3 0003.
 */

public class MyWalletMdel implements IMyWalletModel {

    private MyWalletMdel.IMyWalletListener mListener;
    private Context mContext;
    private boolean isRequest = false;

    @Override
    public void requestMyWalletRecord(Context context, String token, MyWalletMdel.IMyWalletListener listener) {
        mListener = listener;
        mContext = context;
        if (isRequest) {
            return;
        }
        isRequest = true;
        httpGetAmount(token);
    }
    private void httpGetAmount(String token){
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<MyWalletBean>() {
            @Override
            public void onError(Throwable e) {
                mListener.onGetDataFailure();
                isRequest = false;
            }

            @Override
            public void onNext(MyWalletBean result) {
                if (result != null) {
                    mListener.onGetDataSuccess(result);
                } else {
                    mListener.onGetDataEmpty();
                }
                isRequest = false;
            }
        };
        HttpMethods.getInstance().getMyAmount(new ProgressSubscriber(subscriberOnNextListener,mContext ),token);
    }

    public interface IMyWalletListener {

        void onGetDataSuccess( MyWalletBean data);

        void onGetDataEmpty();

        void onGetDataFailure();
    }


}
