package com.immotor.batterystation.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.immotor.batterystation.android.MyApplication;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.mybattery.MyBatteryActivity;
import com.immotor.batterystation.android.selectcombo.SelectComboActivity;
import com.immotor.batterystation.android.ui.base.BaseActivity;


/**
 * Created by jm.
 */

public class FirstGuideActivity extends BaseActivity {

    public static final String FIRST_TARGET = "first";

    public static final String FIRST_TARGET_TWO_STEP = "first_two_step";

    private final int REQUEST_CODE_INCLUSIVE = 1234;

    @Override
    public void initUIView() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   initScan();
        initEntry();
    }

    private void initEntry() {
        Intent startIntent = new Intent(this, FirstEntryRentPayActivity.class);
        startIntent.putExtra(FIRST_TARGET_TWO_STEP, true);
        startActivity(startIntent);
    }

    private void initScan() {
        new IntentIntegrator(this)
                .addExtra(QRCodeActivity.QR_TYPE_TARGET, QRCodeActivity.QR_TYPE_BATTERY_FIRST)
                .addExtra(FIRST_TARGET,true)
                .setOrientationLocked(false)
                .setCaptureActivity(QRCodeActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); //  初始化扫描
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (requestCode == IntentIntegrator.REQUEST_CODE) {
            String bID = null;
            if (resultCode == QRCodeActivity.MANUAL_RESULT_CODE) {
                bID = data.getStringExtra(QRCodeActivity.MANUAL_INPUT_TARGET);
            } else {
                IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (intentResult != null) {
                    bID = intentResult.getContents();
                }
            }
            //绑定电池
            if (bID != null) {
                httpBindBattery(bID);
            } else {
                Toast.makeText(FirstGuideActivity.this, "扫描电池Id失败，请重新扫描", Toast.LENGTH_SHORT).show();
              //  stypeTwo();
                initScan();
            }

        } else if (requestCode == REQUEST_CODE_INCLUSIVE) {
                Intent startIntent = new Intent(this, RechargeActivity.class);
                startIntent.putExtra(FIRST_TARGET, true);
                startActivity(startIntent);
                finish();
        }*/
        if (resultCode== AppConstant.RENT_BATTERY_RESULT_COD) {
            startActivity(new Intent(FirstGuideActivity.this,ImmediateUseActivity.class));
        }
    }

    private void stypeTwo() {
        Intent startIntent = new Intent(this, SelectComboActivity.class);
        startIntent.putExtra(FIRST_TARGET_TWO_STEP, true);
        startActivityForResult(startIntent, REQUEST_CODE_INCLUSIVE);
    }

/*    private void httpBindBattery(String bid) {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onError(Throwable e) {
                Toast.makeText(FirstGuideActivity.this, R.string.bind_battery_fail, Toast.LENGTH_SHORT).show();
             //   stypeTwo();
               // startActivity(new Intent(FirstGuideActivity.this,HomeActivity.class));
                initScan();
            }

            @Override
            public void onNext(String reslut) {
                if (Integer.parseInt(reslut) == 2) {
                //    Preferences.getInstance(getApplicationContext()).setKeyNoticeMessageStatus(true);
                    Toast.makeText(FirstGuideActivity.this, R.string.bind_battery_sucess, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FirstGuideActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(FirstGuideActivity.this, R.string.bind_battery_sucess, Toast.LENGTH_SHORT).show();
                   // startActivity(new Intent(FirstGuideActivity.this,HomeActivity.class));
                    stypeTwo();
                }
            }
        };
        HttpMethods.getInstance().addBattery(new ProgressSubscriber(subscriberOnNextListener, this), mPreferences.getToken(), bid);
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       finish();
    }
}
