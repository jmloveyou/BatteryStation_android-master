package com.immotor.batterystation.android.app;

/**
 * Created by ${jm} on 2017/7/19 0019.
 */

public class AppConstant {

    public final static int TAG_MAP_TYPE = 10;
    public final static int TAG_GOOGLE_TYPE = 11;
    public final static String RECHARGE_RESULT_TAG = "recharge_result_data";
    public final static String RQ_RESULT_TAG = "rq_result_data";
    public final static String SELECT_MYCOMBO_RESULT_TAG = "select_mycombo_result_data";
    public final static int RECHARGE_RESULT_COD = 101;
    public final static int RQ_RESULT_COD = 102;
    public final static int SELECT_MYCOMBO_RESULT_COD = 103;
    public final static int RENT_BATTERY_RESULT_COD = 104;

    public final static int AUTO_EXPENSE_STATUS_OPEN_FAIL_COD = 106;
    public final static int AUTO_EXPENSE_STATUS_CLOSE_FAIL_COD = 107;
    public final static int AUTO_EXPENSE_STATUS_OPEN_SUCCESS_COD = 108;
    public final static int AUTO_EXPENSE_STATUS_CLOSE_SUCCESS_COD = 109;

 //   public final static int AUTO_EXPENSE_STATUS_QUERY_SUCCESS_COD = 110;
  //  public final static int AUTO_EXPENSE_STATUS_QUERY_FAIL_COD = 111;
    public final static int AUTO_EXPENSE_STATUS_OPEN_COD = 110;
    public final static int AUTO_EXPENSE_STATUS_CLOSE_COD = 111;

    public final static String KEY_LATITUDE_TYPE = "Latitude";
    public final static String KEY_LONGITUDE_TYPE = "Longitude";
    public final static String KEY_HLATITUDE_TYPE = "HLatitude";
    public final static String KEY_HLONGITUDE_TYPE = "HLongitude";

    public final static String KEY_ENTRY_HOMEATY_TYPE = "entryhomeaty";
    public final static String KEY_ENTRY_ADDRESSACTIVITY_LOCATION = "addressactivity_location";
    public final static String KEY_ENTRY_BATTERY_CAR_SID = "battery_car_sid";
    public final static String KEY_ENTRY_BATTERY_CAR_SID_IN = "battery_car_sid_in";
    public final static String KEY_ENTRY_BATTERY_REQUEST_FAIL = "battery_request_fail";
    public final static String KEY_ENTRY_BATTERY_REQUEST_EMPTY = "battery_request_empty";
    public final static String KEY_ENTRY_BATTERY_REQUEST_ONE_DATA = "battery_request_one_data";
    public final static String KEY_ENTRY_BATTERY_REQUEST_TWO_DATA = "battery_request_two_data";
    public final static String KEY_ENTRY_BATTERY_SETTING_DATA = "batteryt_setting_data";
    public final static String KEY_ENTRY_BATTERY_CAR_TRIP_SID = "battery_car_trip_sid";
    public final static String KEY_ENTRY_CAR_SETTING_DATA = "entry_car_setting_data";
    public final static String KEY_ENTRY_CAR_UPDATA_VERSION = "entry_car_updata_version";
    public final static String KEY_ENTRY_CAR_UPDATA_FWVERSION = "entry_car_updata_fwversion";
    public final static String KEY_ENTRY_CAR_UPDATA_HWVERSION = "entry_car_updata_hwversion";
    public final static String KEY_ENTRY_CAR_UPDATA_PMSFWVERSION = "entry_car_updata_pmsfwversion";
    public final static String KEY_ENTRY_CAR_UPDATA_PMSHWVERSION = "entry_car_updata_pmshwversion";
    public final static int TAG_CAR_NAME_SETTING_TYPE = 12;
    public final static int REQUEST_CAR_NAME_SETTING_CODE = 13;
    public final static int RERESULT_CAR_NAME_SETTING_CODE = 14;

    public static int update_status = 0;    //0表示没在升级，1表示CTRL升级，2表示PMS升级，3表示APK升级,4表示BAT升级
}
