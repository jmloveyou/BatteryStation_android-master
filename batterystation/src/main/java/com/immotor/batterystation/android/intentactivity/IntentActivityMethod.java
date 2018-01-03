package com.immotor.batterystation.android.intentactivity;

import android.content.Context;
import android.content.Intent;

import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.entity.CarBatteryEntry;
import com.immotor.batterystation.android.entity.CarListBean;
import com.immotor.batterystation.android.mycar.TripHistoryActivity;
import com.immotor.batterystation.android.mycar.batterysettingdetail.BatterySettingDetailActivity;
import com.immotor.batterystation.android.mycar.mycaraddress.MyCarAddressActivity;
import com.immotor.batterystation.android.mycar.mycaraddress.MyCarAddressGmapActivity;
import com.immotor.batterystation.android.mycar.mycarbattery.MyCarBatterymainActivity;
import com.immotor.batterystation.android.mycar.mycarsetting.MyCarSettingActivity;
import com.immotor.batterystation.android.mycar.mycarsetting.MyCarUPdateActivity;
import com.immotor.batterystation.android.ui.activity.LoginActivity;
import com.immotor.batterystation.android.util.DateTimeUtil;

/**
 * Created by jm on 2017/9/18 0018.
 */

public class IntentActivityMethod {
    public static void carActivitytoCarSettingActivity(Context context,CarListBean data) {
        Intent intent = new Intent(context, MyCarSettingActivity.class);
        intent.putExtra(AppConstant.KEY_ENTRY_CAR_SETTING_DATA,data);
        context.startActivity(intent);
    }
    public static void CarSettingActivitytoCarUpdata(Context context,String version,String fwVersion,String hwVersion,String pmsfwVersion,String pmshwVersion,String macAddress,String sid) {
        Intent intent = new Intent(context, MyCarUPdateActivity.class);
        intent.putExtra(AppConstant.KEY_ENTRY_CAR_UPDATA_VERSION,version);
        intent.putExtra(AppConstant.KEY_ENTRY_CAR_UPDATA_FWVERSION,fwVersion);
        intent.putExtra(AppConstant.KEY_ENTRY_CAR_UPDATA_HWVERSION,hwVersion);
        intent.putExtra(AppConstant.KEY_ENTRY_CAR_UPDATA_PMSFWVERSION,pmsfwVersion);
        intent.putExtra(AppConstant.KEY_ENTRY_CAR_UPDATA_PMSHWVERSION,pmshwVersion);
        intent.putExtra(AppConstant.KEY_ENTRY_BATTERY_CAR_SID, sid);
        intent.putExtra("macaddress",macAddress);
        context.startActivity(intent);
    }
    public static void carActivitytoCarBatteryActivity(Context context,String sid) {
        Intent intent = new Intent(context, MyCarBatterymainActivity.class);
        intent.putExtra(AppConstant.KEY_ENTRY_BATTERY_CAR_SID_IN, sid);
        context.startActivity(intent);
    }
    public static void carActivitytoMyCarTravelActivity(Context context,String sid) {
        Intent intent = new Intent(context, TripHistoryActivity.class);
        intent.putExtra(AppConstant.KEY_ENTRY_BATTERY_CAR_TRIP_SID, sid);
        context.startActivity(intent);
    }
    public static void carActivitytoMyCarAddressActivity(Context context,String location) {
        Intent intent;
        if (DateTimeUtil.isInChina()) {
             intent = new Intent(context, MyCarAddressActivity.class);
        } else {
            intent = new Intent(context, MyCarAddressGmapActivity.class);
        }
        intent.putExtra(AppConstant.KEY_ENTRY_ADDRESSACTIVITY_LOCATION,location);
        context.startActivity(intent);
    }

    public static void ActivitytoLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
    public static void CarBatterytoBatterySettingDetail(Context context, CarBatteryEntry data ,int index) {
        Intent intent = new Intent(context, BatterySettingDetailActivity.class);
        intent.putExtra(AppConstant.KEY_ENTRY_BATTERY_SETTING_DATA,data);
        intent.putExtra("batteryindex",index);
        context.startActivity(intent);
    }

}
