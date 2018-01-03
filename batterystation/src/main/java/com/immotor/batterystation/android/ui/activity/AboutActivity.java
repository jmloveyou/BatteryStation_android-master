package com.immotor.batterystation.android.ui.activity;

import android.app.Dialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.DownLoadEnd;
import com.immotor.batterystation.android.entity.DownLoadStart;
import com.immotor.batterystation.android.entity.VersionUpdateBean;
import com.immotor.batterystation.android.http.ApiException;
import com.immotor.batterystation.android.http.HttpFailMessage;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.ui.views.CommonDialog;
import com.immotor.batterystation.android.util.VersionManagementUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by Ashion on 2017/5/19.
 */

public class AboutActivity extends BaseActivity {

    private TextView mVersion;
    private boolean isRequesting = false;
    private String mVersionName;
    private Dialog downLoadingDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        EventBus.getDefault().register(this);
    }

    @Override
    public void initUIView() {
        TextView mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.about_us);
        ImageView mImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mImg.setImageDrawable(getDrawable(R.mipmap.nav_back_icon_white));
        mVersion = (TextView) findViewById(R.id.version_txt);
        mVersionName = getVersion();
        mVersion.setText(mVersionName);
        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return getString(R.string.cont_get_version_code);
        }
    }

    @OnClick(R.id.version_update)
    public void httpVersionUpdate() {
        httpVersion();
    }

    private void httpVersion() {
        if (!isNetworkAvaliable()) {
            return;
        }
        if (isRequesting) {
            return;
        }
        isRequesting = true;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<VersionUpdateBean>() {
            @Override
            public void onError(Throwable e) {
                HttpFailMessage.showfailMessage(AboutActivity.this, null, e);
                isRequesting = false;

            }

            @Override
            public void onNext(VersionUpdateBean data) {
                if (data != null) {
                    mPreferences.setNewVersionName(data.getLatest());
                    mPreferences.setNewVersionUrl(data.getUrl());
                    if (VersionManagementUtil.VersionComparison(data.getNewest(), getVersion()) == 1) {
                        showUpdateDialog(data);
                    } else {
                        Toast.makeText(AboutActivity.this, R.string.now_version_is_new, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(AboutActivity.this, R.string.now_version_is_new, Toast.LENGTH_SHORT).show();
                }
                isRequesting = false;
            }
        };
        HttpMethods.getInstance().VersionUpdata(new ProgressSubscriber(subscriberOnNextListener, this), 0, "P1");
    }

    private void showUpdateDialog(VersionUpdateBean bean) {
        downLoadingDialog = downLoadDialog();
        String title = getString(R.string.find_new_version);
        String msg = "V" + bean.getNewest() + "\n" + "Update: " + bean.getContent();
        Dialog dialog = CommonDialog.createUpdateDialog(this, title, msg, false);
        dialog.show();
    }
    public Dialog downLoadDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.download_dialog_layout, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.download_dialog);// 加载布局
        final Dialog dialog = new Dialog(this, R.style.dialog_style);// 创建自定义样式dialog
        dialog.setCancelable(true);
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
}
