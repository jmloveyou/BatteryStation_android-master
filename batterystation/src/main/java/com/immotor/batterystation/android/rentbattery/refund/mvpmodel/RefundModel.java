package com.immotor.batterystation.android.rentbattery.refund.mvpmodel;

import android.content.Context;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.MybatteryListBean;
import com.immotor.batterystation.android.entity.RefundPayListBean;
import com.immotor.batterystation.android.http.ApiException;
import com.immotor.batterystation.android.http.HttpFailMessage;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;


/**
 * Created by jm on 2017/7/28 0028.
 */

public class RefundModel implements IRefundModel {
    private IRefundListlistener myBatterylistener;

    @Override
    public void requestRefundListM(Context context, String token, IRefundListlistener listener) {
        myBatterylistener = listener;
        httpRefundPayList(context, token);
    }

    private void httpRefundPayList(final Context context, String token) {

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<RefundPayListBean>() {
            @Override
            public void onError(Throwable e) {
                myBatterylistener.onGetDataFailure();
            }

            @Override
            public void onNext(RefundPayListBean bean) {
                if (bean != null && bean.getContent().size() > 0) {
                    myBatterylistener.onGetDataSuccess(bean);
                } else {
                    myBatterylistener.onGetDataEmpty();
                }
            }
        };
        HttpMethods.getInstance().getMyDepositList(new ProgressSubscriber(subscriberOnNextListener, context), token,0,1000);
    }

    @Override
    public void requestRefundM(Context context, String token, String id) {
        httprequestRefundpay(context, token, id);
    }

    private void httprequestRefundpay(final Context context, final String token, String id) {

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                HttpFailMessage.showfailMessage(context, null, e);
            }
            @Override
            public void onNext(Object object) {
                Toast.makeText(context, R.string.refund_scuess, Toast.LENGTH_SHORT).show();
                httpRefundPayList(context, token);
            }
        };
        HttpMethods.getInstance().getrefundRentPay(new ProgressSubscriber(subscriberOnNextListener, context), token, id);
    }

    public interface IRefundListlistener {

        void onGetDataSuccess(RefundPayListBean refundPayListBean);

        void onGetDataEmpty();

        void onGetDataFailure();
    }
}
