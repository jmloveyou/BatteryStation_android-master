package com.immotor.batterystation.android.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.immotor.batterystation.android.MyApplication;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.DownLoadEnd;
import com.immotor.batterystation.android.entity.DownLoadStart;
import com.immotor.batterystation.android.entity.VersionUpdateBean;
import com.immotor.batterystation.android.http.ApiException;
import com.immotor.batterystation.android.http.HttpFailMessage;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.service.VersionUpdateService;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.ui.views.CommonDialog;
import com.immotor.batterystation.android.util.DateTimeUtil;
import com.immotor.batterystation.android.util.LogUtil;
import com.immotor.batterystation.android.util.PermissionUtils;
import com.immotor.batterystation.android.util.VersionManagementUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Ashion on 2017/5/8.
 */

public class SplashActivity extends BaseActivity {

    private boolean isExpire = false;  //是否到时间
    private int status = 0; // 当前状态，0，没检测，1，检测失败，到时间进入登录界面， 2，检测成功，到时间进入主界面
    private boolean isRequesting = false;
    Handler myHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            isExpire = true;
            checkGotoNext();
        }
    };
    private String currentVersion;
    private Dialog downLoadingDialog;
    private Dialog mDownLoaddialog;
    private boolean isFirstEntry=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        EventBus.getDefault().register(this);
        currentVersion = VersionManagementUtil.getVersion(this);
       // httpVersion();
        downLoadingDialog = downLoadDialog();
    }

    private void httpVersion() {
       /* if (!isNetworkAvaliable()) {
            return;
        }*/
        if (isRequesting) {
            return;
        }
        isRequesting = true;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<VersionUpdateBean>() {
            @Override
            public void onError(Throwable e) {
                HttpFailMessage.showfailMessage(SplashActivity.this, null, e);
                httpGetUserInfo();
                isRequesting = false;
            }

            @Override
            public void onNext(VersionUpdateBean data) {
                if (data != null) {
                    mPreferences.setNewVersionName(data.getLatest());
                    mPreferences.setNewVersionUrl(data.getUrl());
                    int result = VersionManagementUtil.VersionComparison(data.getLatest(), currentVersion);
                    int resultnomal=VersionManagementUtil.VersionComparison(data.getNewest(), currentVersion);
                    //  String appName = "batterystation" + mPreferences.getNewVersionName() + ".apk";
                    //  String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator + appName;

                   /* if (VersionManagementUtil.VersionComparison(data.getLatest(), String.valueOf(VersionManagementUtil.getInstance(SplashActivity.this).getVersionCode(filePath))) == 0) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
                        startActivity(intent);
                    } else {*/
                    if (result == 1) {
                        showUpdateDialog(data, true);
                    } else if (resultnomal==1) {
                        showUpdateDialog(data, false);
                    } else {
                        httpGetUserInfo();
                    }

                }else {
                    httpGetUserInfo();
                }
                isRequesting = false;
            }
        };
        HttpMethods.getInstance().VersionUpdata(new ProgressSubscriber(subscriberOnNextListener, this, null), 0, "P1");
    }

    private void showUpdateDialog(VersionUpdateBean bean, boolean mustStatus) {
        String title = getString(R.string.find_new_version);
        String msg = "V" + bean.getNewest() + "\n" + "Update: " + bean.getContent();
        mDownLoaddialog = createUpdateDialog( title, msg, mustStatus);
        mDownLoaddialog.show();
    }
    public  Dialog createUpdateDialog( String title, String msg, final boolean mustStatus) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.download), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // positive button logic
                dialog.dismiss();
                Intent intent = new Intent(SplashActivity.this, VersionUpdateService.class);
                startService(intent);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (mustStatus) {
                   finish();
                } else {
                    httpGetUserInfo();
                }
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        return builder.setCancelable(false).create();
    }
    @Override
    public void initUIView() {

    }

    private boolean checkPermission = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPermission) {
            checkPermission = true;
            String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA};
            if (!EasyPermissions.hasPermissions(this, perms)) {
                EasyPermissions.requestPermissions(this, getString(R.string.permission_required_toast),
                        PermissionUtils.PERMISSION_REQUEST_CODE_LOCATION, perms);
                return;
            }
        }
        if (!isFirstEntry) {
            httpVersion();
            isFirstEntry = true;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void httpGetUserInfo() {
        if (TextUtils.isEmpty(mPreferences.getToken()) || !isNetworkAvaliable()) {
            status = 1;
        } else {
            updateUserToken();
        }
        myHandler.sendEmptyMessageDelayed(1, 2000);
    }

    private void updateUserToken() {
        if (!isNetworkAvaliable()) {
            status = 1;
            checkGotoNext();
            return;
        }
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                if (e.getMessage().equals("End of input at line 1 column 1")) {
                    status = 2;
                } else {
                    status = 1;
                    if (e instanceof ApiException) {
                        showSnackbar(e.getMessage());
                    } else {
                        showSnackbar(e.getMessage());
                    }
                }
                checkGotoNext();
            }

            @Override
            public void onNext(Object result) {
                status = 2;
                checkGotoNext();
            }
        };
        HttpMethods.getInstance().updateToken(new ProgressSubscriber(subscriberOnNextListener, this, null), mPreferences.getToken());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void checkGotoNext() {
        if (myHandler.hasMessages(1)) {
            myHandler.removeMessages(1);
        }

        if (status == 2 && !isExpire) {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
        finish();
    }

    public Dialog downLoadDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.download_dialog_layout, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.download_dialog);// 加载布局
        final Dialog dialog = new Dialog(this, R.style.dialog_style);// 创建自定义样式dialog
        dialog.setCancelable(false);
        dialog.setContentView(layout);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.3); // 高度设置为屏幕的0.5
        p.width = (int) (d.getWidth() * 0.5); // 宽度设置为屏幕的0.4
        dialogWindow.setAttributes(p);
        return dialog;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownLoadEventS(DownLoadStart downLoadStart) {
        if (downLoadingDialog!=null) {
        downLoadingDialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDownLoadEventE(DownLoadEnd downLoadEnd) {
        if (downLoadingDialog!=null && downLoadingDialog.isShowing()) {
        downLoadingDialog.dismiss();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApplication myApplication = null;
        try {
            myApplication = (MyApplication) getApplicationContext();
        } catch (Exception e) {
            LogUtil.e(e.toString());
            myApplication = null;
        }

        if (null == myApplication) {
            return;
        }
        myApplication.exitAllActivity();
    }

}
