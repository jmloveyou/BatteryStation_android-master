package com.immotor.batterystation.android.http.subscriber;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.immotor.batterystation.android.MyApplication;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.http.progress.ProgressCancelListener;
import com.immotor.batterystation.android.http.progress.ProgressDialogHandler;
import com.immotor.batterystation.android.ui.activity.LoginActivity;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.util.LogUtil;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by Ashion on 16/6/8.
 */
public class ProgressSubscriber<T> extends Subscriber<T> implements ProgressCancelListener {

    private SubscriberOnNextListener mSubscriberOnNextListener;
    private ProgressDialogHandler mProgressDialogHandler;
    private Context context;

    public ProgressSubscriber(SubscriberOnNextListener mSubscriberOnNextListener, Context context) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = context;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, true);
    }

    public ProgressSubscriber(SubscriberOnNextListener mSubscriberOnNextListener, Context context, ProgressDialogHandler progressDialogHandler) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = context;
        mProgressDialogHandler = progressDialogHandler;
    }

    private void showProgressDialog(){
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog(){
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }

    /**
     * 订阅开始时调用
     * 显示ProgressDialog
     */
    @Override
    public void onStart() {
        showProgressDialog();
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if(context!=null && context instanceof BaseActivity ) {
            if (((BaseActivity)context).isStop()) {   //activity 已经stop，不响应，否则若操作UI可能出问题
                context = null;
                return;
            }
        }
        String errorMsg;
        try {
            if (e instanceof SocketTimeoutException) {
                errorMsg = context.getString(R.string.connection_timeout);
                e = new Throwable(errorMsg);
            } else if (e instanceof ConnectException ||
                    e instanceof UnknownHostException) {
                errorMsg = context.getString(R.string.network_error);
                e = new Throwable(errorMsg);
            }
            e.printStackTrace();
        }catch (Exception exception) {
            LogUtil.e(exception.toString());
        }

        dismissProgressDialog();
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onError(e);
            if (e instanceof HttpException) {
                int code=((HttpException) e).code();
                if (code==401) {
                    if (context==null) {
                        return;
                    }
                    Preferences.getInstance(context).setToken(null);
                    Toast.makeText(context, R.string.plase_login_agin, Toast.LENGTH_SHORT).show();
                    intentLoginActivity();
                }
            }
        }
    }

    private void intentLoginActivity() {
        Preferences.getInstance(MyApplication.myApplication).setToken(null);
        Preferences.getInstance(MyApplication.myApplication).setComboStatus(false);
        Preferences.getInstance(MyApplication.myApplication).setMyBatteryStatus(false);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context = null;
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if(context!=null && context instanceof BaseActivity) {
            if (((BaseActivity)context).isStop()) {   //activity 已经stop，不响应，否则若操作UI可能出问题
                context = null;
                return;
            }
        }
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onNext(t);
        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress() {
        if (!this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }
}