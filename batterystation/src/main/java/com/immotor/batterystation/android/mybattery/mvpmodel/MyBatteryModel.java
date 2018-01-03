package com.immotor.batterystation.android.mybattery.mvpmodel;

import android.content.Context;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.MybatteryListBean;
import com.immotor.batterystation.android.http.ApiException;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;


/**
 *
 * Created by jm on 2017/7/28 0028.
 */

public class MyBatteryModel implements IMyBatteryModel {
    private IMyBatterylistener myBatterylistener;

    @Override
    public void requestMyBattery(Context context, String token,MyBatteryModel.IMyBatterylistener listener) {
        myBatterylistener = listener;
        httpMyatteryList(context, token);
    }

    private void httpMyatteryList(final Context context, String token) {

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<MybatteryListBean>() {
            @Override
            public void onError(Throwable e) {
                myBatterylistener.onGetDataFailure();
            }

            @Override
            public void onNext(MybatteryListBean bean) {
                if (bean != null && bean.getContent().size() > 0) {
                    myBatterylistener.onGetDataSuccess(bean);
                } else {
                    myBatterylistener.onGetDataEmpty();
                }
            }
        };
        HttpMethods.getInstance().myBatteryList(new ProgressSubscriber(subscriberOnNextListener, context), token);
    }

    @Override
    public void requestBatteryBuy(Context context, String token, int quantity) {
        httpMyatteryBuy(context, token, quantity);
    }

    private void httpMyatteryBuy(final Context context, final String token, int quantity) {

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {
                    int code = ((ApiException) e).getCode();
                    if (code == 626) {
                        Toast.makeText(context, R.string.wallet_not_enough, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, R.string.buy_fail, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.buy_fail, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNext(Object object) {
                Toast.makeText(context, R.string.buy_sucess, Toast.LENGTH_SHORT).show();
                httpMyatteryList(context, token);
            }
        };
        HttpMethods.getInstance().buyBattery(new ProgressSubscriber(subscriberOnNextListener, context), token, quantity);
    }

    public interface IMyBatterylistener {

        void onGetDataSuccess(MybatteryListBean mybatteryListBean);

        void onGetDataEmpty();

        void onGetDataFailure();
    }
}
