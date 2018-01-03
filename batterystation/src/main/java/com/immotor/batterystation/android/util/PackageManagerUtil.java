package com.immotor.batterystation.android.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.immotor.batterystation.android.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jm on 2017/9/27 0027.
 */

public class PackageManagerUtil {
    private static PackageManager mPackageManager = MyApplication.myApplication.getPackageManager();
    private static List<String> mPackageNames = new ArrayList<>();
    private static final String GAODE_PACKAGE_NAME = "com.autonavi.minimap";
    private static final String BAIDU_PACKAGE_NAME = "com.baidu.BaiduMap";


    private static void initPackageManager() {

        List<PackageInfo> packageInfos = mPackageManager.getInstalledPackages(0);
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                mPackageNames.add(packageInfos.get(i).packageName);
            }
        }
    }

    public static boolean haveGaodeMap() {
        initPackageManager();
        return mPackageNames.contains(GAODE_PACKAGE_NAME);
    }

    public static boolean haveBaiduMap() {
        initPackageManager();
        return mPackageNames.contains(BAIDU_PACKAGE_NAME);
    }

    //移动APP调起Android百度地图方式举例
    public static void startBaiduApp(Context context, Double sLat, Double sLon, Double eLat, Double eLon) {
        double[] origin = gaoDeToBaidu(sLon, sLat);
        double[] destination = gaoDeToBaidu(eLon, eLat);
        try {
            Intent i1 = new Intent();
            // 骑行导航
            i1.setData(Uri.parse("baidumap://map/bikenavi?origin=" + origin[1] + "," + origin[0] + "&destination=" + destination[1] + "," + destination[0]));
            context.startActivity(i1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startGaodeApp(Context context, double lat, double lon) {
        try {
            Intent gddtIntent = new Intent("android.intent.action.VIEW",
                    Uri.parse("androidamap://navi?sourceApplication=“e换电”&poiname="
                            + "" + "&lat=" + lat + "&lon=" + lon + "&dev=" + 0
                            + "&style=" + 2));
            gddtIntent.addCategory("android.intent.category.DEFAULT");
            gddtIntent.setPackage("com.autonavi.minimap");
            gddtIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(gddtIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }


}
