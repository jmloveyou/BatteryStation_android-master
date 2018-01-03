package com.immotor.batterystation.android;

import android.os.Environment;

import java.io.File;

/**
 * Created by Ashion on 2017/5/2.
 */

public class MyConfiguration {
    private static boolean TestStatus=false;

 //   public static final String CURRENT_SERVER_NOMAL_URL = "http://119.23.130.199:80/";
    /*116.62.11.10：8080*//*119.23.130.199:80*/
    public static final String CURRENT_SERVER_TEST_URL = "http://119.23.133.72:8080/";
   // public static final String CURRENT_SERVER_URL = CURRENT_SERVER_TEST_URL;
    //public static final String CURRENT_SERVER_URL = "http://ehd.immotor.com/";
  //  public static final String CAR_SERVER_URL = "http://ehd.immotor.com/";
    public static final String CURRENT_SERVER_NOMAL_URL = "http://ehd.immotor.com/";
    public static final String CAR_SERVER_NOMAL_URL = "http://ehd.immotor.com/";
    public static final String CAR_SERVER_TEST_URL = "http://120.76.72.18:8081";
    //http://120.76.72.18:8081
     // "http://120.76.157.58:8080/""ehd.immotor.com"
    public static final int DEFAULT_TIMEOUT = 20; //second

    public static final String APP_FILE_PATH = Environment.getExternalStorageDirectory().getPath()+ File.separator + "BatteryStation"+ File.separator;
    public static final String AVATAR_PATH = MyConfiguration.APP_FILE_PATH + "Avatar" + File.separator;

    public static final String UPDATE_FILE_PATH = MyConfiguration.APP_FILE_PATH + "update" + File.separator;

    public static final String UPDATE_SMART_PATH = UPDATE_FILE_PATH + "smart" + File.separator;

    public static final String UPDATE_PMS_PATH = UPDATE_FILE_PATH + "pms" + File.separator;

    public static final String REPORT_FAIL_PATH =  MyConfiguration.APP_FILE_PATH + "report" + File.separator;

    public static void setServerURL(){
    }
    public static boolean isDebug() {
        return BuildConfig.DEBUG;
//        return true;
    }

    public static String  getBaseUrl() {
        String url ;
        if (TestStatus) {
            url = CURRENT_SERVER_TEST_URL;
        } else {
            url = CURRENT_SERVER_NOMAL_URL;
        }
        return url;
    }

    public static String  getCarBaseUrl() {
        String url ;
        if (TestStatus) {
            url = CAR_SERVER_TEST_URL;
        } else {
            url = CAR_SERVER_NOMAL_URL;
        }
        return url;
    }
    public static boolean isForTest = true;

    public static final String MEDIA_FILE_PATH = MyConfiguration.APP_FILE_PATH +"BatteryStationPhoto" + File.separator;

    public static final String DIRECTIONS_URL = "https://maps.googleapis.com/maps/api/directions/json";

}
