package com.immotor.batterystation.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.Preference;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.entity.BatteryStationDetailInfo;
import com.immotor.batterystation.android.entity.BatteryStationInfo;
import com.immotor.batterystation.android.entity.MyDepositInfo;
import com.immotor.batterystation.android.util.CustomCrashHandler;
import com.immotor.batterystation.android.util.LogUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Ashion on 2017/5/4.
 */

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks{
    public static MyApplication myApplication;

    /**
     * 保存当前程序所有 Activity 实例
     */
    private LinkedList<ActivityInfo> mExistedActivities = null;
    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        FileDownloader.init(this.getBaseContext());
        //记录本地崩溃
        CustomCrashHandler mCustomCrashHandler = CustomCrashHandler.getInstance();
        mCustomCrashHandler.setCustomCrashHanler(myApplication);
        registerActivityLifecycleCallbacks();

    }

    private void registerActivityLifecycleCallbacks() {
        mExistedActivities = new LinkedList<>();
        registerActivityLifecycleCallbacks(this);
    }
    private static List<BatteryStationInfo> mStationList;
    private static BatteryStationDetailInfo mStationDetail;

    public static List<BatteryStationInfo> getStationList() {
        return mStationList;
    }

    public static void setStationList(List<BatteryStationInfo> mStationList) {
        MyApplication.mStationList = mStationList;
    }

    public static BatteryStationDetailInfo getStationDetail() {
        return mStationDetail;
    }

    public static void setStationDetail(BatteryStationDetailInfo mStationDetail) {
        MyApplication.mStationDetail = mStationDetail;
    }


    private static String tradeNo;  //租借电池的交易单号

    /**
     * @return 租借电池的交易单号
     */
    public static String getTradeNo(){
        return tradeNo;
    }

    /**
     * @param no 租借电池的交易单号
     */
    public static void setTradeNo(String no){
        tradeNo = no;
    }

    private static boolean WxpayStatus = false;
    /**
     * @return 第一次进入微信支付状态
     */
    public static boolean getWxpayStatus(){
        return WxpayStatus;
    }

    /**
     *  第一次进入微信支付状态
     */
    public static void setWxpayStatus(boolean WxStatus){
        WxpayStatus = WxStatus;
    }


    private static String payNo; //支付交易单号

    /**
     * @return 支付交易单号
     */
    public static String getPayNo(){
        return payNo;
    }

    /**
     * @param no 支付交易单号
     */
    public static void setPayNo(String no){
        payNo = no;
    }


    /**
     * 计算设备唯一号
     * @return
     */
    public String calculateUUID() {
        String[] perms = {android.Manifest.permission.READ_PHONE_STATE};
        long leastSig = 0;
        if(EasyPermissions.hasPermissions(this, perms)){
            TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
            String tmDevice, tmSerial;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            leastSig = ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode();
        }else{
            leastSig = System.currentTimeMillis();
        }

        UUID deviceUuid = new UUID(android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID).hashCode(), leastSig);
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        exitAllActivity();
    }

    /**
     * 是否登录
     * 有起码以下作用：
     * 1.未登录情况下，不要弹提示重新登录（如token过期，异地登录等）的对话框
     * 2.若在登录情况下收到提示消息，登出后，点击消息不应该有跳转
     * @return
     */
    public static boolean isLogin(){
        return isLogin;
    }

    private static boolean isLogin = false;   //当前是否已经成功登录

    /**
     * 设置是否登录
     * 在以下情况设置为true：
     * 1.进入APP后判定是有效用户，直接进入Main
     * 2.在登录界面，输入正确的用户名密码，进入Main
     * 3.注册成功，进入Main
     * 4.未来可能有的验证身份后进入Main
     * 在以下情况设置为false：
     * 1.用户主动登出；
     * 2.异地登录挤出；
     * 3.刚打开APP需要默认未登录
     * @param login
     */
    public static void setIsLogin(boolean login){
        isLogin = login;
    }

    private boolean fromLogin = false;
    public void setFromLogin(boolean fromLogin){
        this.fromLogin = fromLogin;
    }

    /**
     * 是否从Login进入Main，如果是，需要强制刷新界面，避免登出后又登录，结果进入Main后由于网络问题导致显示出问题
     * @return
     */
    public boolean isFromLogin(){
        return fromLogin;
    }



    /***************************
     * 回调相关
     *****************************/

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (null != mExistedActivities && null != activity) {
            // 把新的 activity 添加到最前面，和系统的 activity 堆栈保持一致
            mExistedActivities.offerFirst(new ActivityInfo(activity, ActivityInfo.STATE_CREATE));
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (null != mExistedActivities && null != activity) {
            ActivityInfo info = findActivityInfo(activity);
            if (null != info) {
                mExistedActivities.remove(info);
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }


    public void exitAllActivity() {
        if (null != mExistedActivities) {

            // 先暂停监听（省得同时在2个地方操作列表）
            unregisterActivityLifecycleCallbacks(this);

            // 弹出的时候从头开始弹，和系统的 activity 堆栈保持一致
            for (ActivityInfo info : mExistedActivities) {
                if (null == info || null == info.mActivity) {
                    continue;
                }

                try {
                    info.mActivity.finish();
                } catch (Exception e) {
                    LogUtil.e(e.toString());
                }
            }

            mExistedActivities.clear();

            // 退出完之后再添加监听
            registerActivityLifecycleCallbacks(this);
        }
    }

    private ActivityInfo findActivityInfo(Activity activity) {
        if (null == activity || null == mExistedActivities) {
            return null;
        }

        for (ActivityInfo info : mExistedActivities) {
            if (null == info) {
                continue;
            }

            if (activity.equals(info.mActivity)) {
                return info;
            }
        }

        return null;
    }

    public final class ActivityInfo {
        public final static int STATE_NONE = 0;
        public final static int STATE_CREATE = 1;
        public final static int STATE_RUNNING = 2;
        public final static int STATE_PAUSE = 3;

        Activity mActivity;
        int mState;

        ActivityInfo() {
            mActivity = null;
            mState = STATE_NONE;
        }

        ActivityInfo(Activity activity, int state) {
            mActivity = activity;
            mState = state;
        }
    }
}
