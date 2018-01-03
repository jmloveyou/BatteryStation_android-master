package com.immotor.batterystation.android.ui.base;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.bluetooth.DeviceDataService;
import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.entity.HbBean;
import com.immotor.batterystation.android.util.Common;
import com.immotor.batterystation.android.util.NetworkUtils;
import com.umeng.analytics.MobclickAgent;


import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

/**
 * Created by Ashion on 2017/4/18.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public Preferences mPreferences;
    private boolean isStop = false;   //是否已经停止

    private View mRootView;    //根视图
    public static boolean isExitStatus = false;
    public DeviceDataService deviceDataService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = Preferences.getInstance(this);
        deviceDataService = DeviceDataService.getInstance(this);
        //  mRootView = findViewById(android.R.id.content);
     /*   IntentFilter filter = new IntentFilter();
        filter.addAction(Common.SERVICE_BORROW_NOTIFICATION);
        registerReceiver(receiver, filter);*/

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
    }


    /*  BroadcastReceiver receiver = new BroadcastReceiver() {
          @Override
          public void onReceive(Context context, Intent intent) {
              if (Common.SERVICE_BORROW_NOTIFICATION.equals(intent.getAction())) {
                  int mVal = intent.getIntExtra("sucess_val",0);
                  if (mVal!=0) {
                  HbBean hbBean= new HbBean();
                  hbBean.setVal(mVal);
                  EventBus.getDefault().post(hbBean);
                  }
              }
          }
      };*/
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        initUIView();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        // unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStop = false;
    }

    @Override
    protected void onStop() {
        isStop = true;
        super.onStop();
    }

    public abstract void initUIView();

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public boolean isStop() {
        return isStop;
    }

    /**
     * 隐藏键盘
     */
    public void hideSoftKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            if (realHeight - usableHeight < realHeight / 10) {
                return 0;
            } else {
                return realHeight - usableHeight;
            }
        } else {
            return 0;
        }
    }

    public void showSnackbar(int resId) {
        //    Snackbar.make(mRootView, getString(resId), Snackbar.LENGTH_SHORT).show();
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    public void showSnackbar(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        //   Snackbar.make(mRootView, message, Snackbar.LENGTH_SHORT).show();
    }

    public boolean isNetworkAvaliable() {
        if (!NetworkUtils.isNetAvailable(this)) {
//            Toast.makeText(this, R.string.network_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


}
