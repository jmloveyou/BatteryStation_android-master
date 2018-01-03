package com.immotor.batterystation.android.mycar.mycarsetting.mvpmpdel;

import android.content.Context;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.http.carhttp.CarHttpFailMessage;
import com.immotor.batterystation.android.http.carhttp.CarHttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.mycar.mycarsetting.MyCarSettingActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jm on 2017/9/22 0022.
 */

public class MyCarSettingModel implements IMyCarSettingModel {

    private boolean isRequesting;

    @Override
    public void requestUnBindCar(Context context, String token, String sid) {
        if (isRequesting) {
            return;
        }
        isRequesting = true;
        httpUnbindCar(context, token, sid);
    }

    private void httpUnbindCar(final Context context, String token, String sid) {

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("sID", sid);
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                CarHttpFailMessage.carfailMessageShow(context,null,e);
                isRequesting = false;
            }

            @Override
            public void onNext(Object data) {
                Toast.makeText(context, R.string.unbind_sucess, Toast.LENGTH_SHORT).show();
                isRequesting = false;
                if (context!=null && context instanceof MyCarSettingActivity) {
                    ((MyCarSettingActivity) context).finish();
                }
            }
        };
        CarHttpMethods.getInstance().getunbindCar(new ProgressSubscriber(subscriberOnNextListener, context), map);
    }
}
