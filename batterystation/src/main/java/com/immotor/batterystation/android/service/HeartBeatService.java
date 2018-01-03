package com.immotor.batterystation.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.entity.HbBean;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.ui.activity.HomeActivity;
import com.immotor.batterystation.android.util.Common;
import com.immotor.batterystation.android.util.LogUtil;
import com.immotor.batterystation.android.util.NetworkUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jm on 2017/10/16.
 */
public class HeartBeatService extends Service {

    private Context context;
    private Timer timer;
    private String token;
    public Preferences mPreferences;

    private boolean isInit = false;
    private final int NOTIFICATION_BASE_NUMBER = 110;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        startForeground(0, new Notification());

        context = this;
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        if (isInit) {
            return super.onStartCommand(intent, flags, startId);
        }
        isInit = true;
        mPreferences = Preferences.getInstance(this);
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!NetworkUtils.isNetAvailable(context)) {
                    return;
                }
                token = mPreferences.getToken();
                LogUtil.v("token from heartbeat: token=" + token);
                if (TextUtils.isEmpty(token)) { // means logout
                    if (timer != null) {
                        timer.cancel();
                    }
                    isInit = false;
                    return;
                }
                SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<HbBean>() {
                    @Override
                    public void onError(Throwable e) {


                    }

                    @Override
                    public void onNext(HbBean result) {
                        if (result != null) {
                            parseNotification(result);
                            HbBean hbBean = new HbBean();
                            hbBean.setVal(result.getVal());
                            EventBus.getDefault().post(hbBean);
                        }
                    }
                };
                HttpMethods.getInstance().getPowerHb(new ProgressSubscriber(subscriberOnNextListener, context, null), token);
            }
        }, 2000, 4000);
        return super.onStartCommand(intent, flags, startId);
    }

    private void parseNotification(HbBean heartBeat) {
        int val = heartBeat.getVal();
        String msg = "";
        if (val == 10) {
            msg = getString(R.string.fetch_battery_scuess);
        } else if (val == 11) {
            msg = getString(R.string.fetch_battery_fail);
        } else if (val == 20) {
            msg = getString(R.string.exchange_battery_scuess);
        } else if (val == 21) {
            msg = getString(R.string.exchange_battery_fail);
        } else if (val == 40) {
            msg = getString(R.string.refund_battery_scuess);
        }
        sendNotification( msg);
    }

    public void sendNotification( String message) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);
        Intent notificationIntent = new Intent(this, HomeActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.app_logo)//设置状态栏里面的图标（小图标）
              //  .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_logo)) // 下拉下拉列表里面的图标（大图标） 　　　　　　　
                .setWhen(System.currentTimeMillis())//设置时间发生时间
                .setAutoCancel(true)//设置可以清除
                .setContentTitle(getString(R.string.app_name))//设置下拉列表里的标题
                .setContentText(message);//设置上下文内容
        Notification notification = builder.build();//获取一个Notification
        notification.defaults = Notification.DEFAULT_SOUND;//设置为默认的声音
        notificationManager.notify(NOTIFICATION_BASE_NUMBER, notification);
    }


    @Override
    public void onDestroy() {
        LogUtil.v("heartbeat onDestroy");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        isInit = false;
        stopForeground(true);
        super.onDestroy();
    }


}
