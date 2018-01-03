package com.immotor.batterystation.android.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;

import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.entity.DownLoadEnd;
import com.immotor.batterystation.android.entity.DownLoadStart;
import com.immotor.batterystation.android.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * Created by jm on 2017/8/18 0018.
 */

public class VersionUpdateService extends Service{
    private DownloadManager downloadManager;
    private BroadcastReceiver receiver;
    private Preferences mPreferences;
    private String versionUrl;
    private String appName;
    private String filePath;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mPreferences = Preferences.getInstance(this);
        versionUrl = mPreferences.getNewVersionUrl();
      //  "http://app-global.pgyer.com/48445a9382bba5715347dfc81d8a44b3.apk?e=1504683195&attname=e%E6%8D%A2%E7%94%B5.apk&token=6fYeQ7_TVB5L0QSzosNFfw2HU8eJhAirMF5VxV9G:LVYAykK83gKxOesJOfB1RoKNjUE=&sign=805d67205f4c17fdf73177d6f81d5ad0&t=59afa4bb";
        appName = "batterystation" + mPreferences.getNewVersionName()+".apk";
        filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator + appName;

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //filePath = Environment.getExternalStorageDirectory().getPath() +File.separator + Environment.DIRECTORY_DOWNLOADS + File.separator + "Android.apk";
                LogUtil.v("file fileUri:"+filePath);
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    // 下载完成，dialog消失
                    EventBus.getDefault().post(new DownLoadEnd());
                }
                try {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(getUriForFile(new File(filePath)), "application/vnd.android.package-archive");
                    startActivity(intent);
                    stopSelf();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        startDownload();
        return Service.START_STICKY;
    }
    public Uri getUriForFile( File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(getApplicationContext(), "com.immotor.batterystation.android.provider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void startDownload() {

        File file = new File(filePath);
        if (file.exists()){
            file.delete();
        }

        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        LogUtil.v("version url = "+versionUrl);
        LogUtil.v("app name:"+appName);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(versionUrl));
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, appName);

        downloadManager.enqueue(request);
        // 显示正在下载的弹框
        EventBus.getDefault().post(new DownLoadStart());

    }
}
