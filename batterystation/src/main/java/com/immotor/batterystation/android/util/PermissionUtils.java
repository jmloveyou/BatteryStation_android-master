package com.immotor.batterystation.android.util;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ashion on 2017/5/5.
 */

public class PermissionUtils {
    public static final int PERMISSION_REQUEST_CODE_LOCATION = 204;

    public static void requestPermission(AppCompatActivity activity, int requestId,
                                         String permission) {
        //permission has not been granted yet, request it.
        ActivityCompat.requestPermissions(activity, new String[]{permission}, requestId);
    }

    public static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
                                              String permission) {
        for (int i = 0; i < grantPermissions.length; i++) {
            if (permission.equals(grantPermissions[i])) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
            }
        }
        return false;
    }
}
