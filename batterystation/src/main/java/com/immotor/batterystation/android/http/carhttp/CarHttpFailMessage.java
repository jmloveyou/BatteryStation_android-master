package com.immotor.batterystation.android.http.carhttp;

import android.content.Context;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.http.ApiException;
import com.immotor.batterystation.android.intentactivity.IntentActivityMethod;

/**
 * Created by jm on 2017/9/18 0018.
 */

public class CarHttpFailMessage {

    public static void carfailMessageShow(Context context,String type, Throwable e) {
        String message = "";
        int code = 0;
        if (e == null) {
            Toast.makeText(context, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
            return;
        }

        if (e instanceof ApiException) {
            code = ((ApiException) e).getCode();
        } else {
            return;
        }
        switch (code) {
            case 202:
                message = context.getString(R.string.http_error);
                break;
            case 204:
                message = context.getString(R.string.sid_not_legal);
                break;
            case 208:
                message = context.getString(R.string.nickname_more_long);
                break;
            case 600:
                message = context.getString(R.string.car_not_exist);
                break;
            case 601:
                message = context.getString(R.string.car_have_binded);
                break;
            case 605:
                message = context.getString(R.string.car_connect_outtime);//不存在
                break;
            case 607:
                message = context.getString(R.string.have_binded_car);
                break;
            case 608:
                message = context.getString(R.string.have_unbinded_car);
                break;
            case 616:
                message = context.getString(R.string.not_used_token);
                break;
            case 401:
                message = e.getMessage();
                IntentActivityMethod.ActivitytoLoginActivity(context);
                break;
            default:
                message = e.getMessage();
                break;

        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
