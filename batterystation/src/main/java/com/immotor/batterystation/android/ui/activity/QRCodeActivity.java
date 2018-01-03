package com.immotor.batterystation.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.immotor.batterystation.android.MyApplication;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.entity.BindCarEntry;
import com.immotor.batterystation.android.http.ApiException;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.carhttp.CarHttpFailMessage;
import com.immotor.batterystation.android.http.carhttp.CarHttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.util.LogUtil;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

import static com.immotor.batterystation.android.ui.activity.FirstGuideActivity.FIRST_TARGET;

/**
 * Created by Ashion on 2017/5/16.
 */

public class QRCodeActivity extends BaseActivity implements DecoratedBarcodeView.TorchListener{


    public static final String QR_TYPE_TARGET = "type";
    public static final int QR_TYPE_STATION = 1;  //电池柜扫描预约
    public static final int QR_TYPE_BATTERY = 2;  //电池扫描
    public static final int QR_TYPE_BATTERY_FIRST = 3;  //第一次电池扫描
    public static final int QR_TYPE_CAR = 4;  //扫描添加车辆

    public static final int MANUAL_RESULT_CODE = 234567;  //手动输入返回
    public static final String MANUAL_INPUT_TARGET = "manual_input";

    private static final int MANUAL_REQUEST_CODE = 12345;
    private boolean isRequest = false;

    @Bind(R.id.view_preview)
    SurfaceView mViewPreview;
    @Bind(R.id.view_finder)
    DecoratedBarcodeView mViewFinder;
    @Bind(R.id.btn_switch)
    View buttonSwitch;

    @Bind(R.id.manual_input)
    View manualInput;

    @Bind(R.id.flash_image)
    ImageView flashImage;

    Bundle savedInstanceState;
    private CaptureManager captureManager;
    private boolean isLightOn = false;
    private int type = 0;
    private boolean isFirst=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        if(type == 0) {
            type = getIntent().getIntExtra(QR_TYPE_TARGET, QR_TYPE_STATION);
        }
        isFirst = getIntent().getBooleanExtra(FIRST_TARGET, false);
        setContentView(R.layout.activity_qrcode);
    }
    @Override
    public void initUIView() {
        TextView  mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        ImageView mImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mImg.setImageDrawable(getDrawable(R.mipmap.nav_back_icon_white));
        if(type == QR_TYPE_STATION){
            manualInput.setVisibility(View.GONE);
            mTittle.setText(R.string.scan_open_station);
        }else if(type == QR_TYPE_BATTERY_FIRST){
            ((ViewStub) findViewById(R.id.stub)).setVisibility(View.VISIBLE);
            findViewById(R.id.second_item).setBackgroundResource(R.mipmap.corner_bg);
            mTittle.setText(R.string.bind_battery);
        }else if(type == QR_TYPE_BATTERY){
            mTittle.setText(R.string.bind_battery);
        } else if (type==QR_TYPE_CAR) {
            mTittle.setText(R.string.add_car);
        }
        if(!hasFlash()){
            if(type == QR_TYPE_STATION) {
                buttonSwitch.setVisibility(View.INVISIBLE);
            }else{
                buttonSwitch.setVisibility(View.GONE);
            }
        }
        mViewFinder.setTorchListener(this);

        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirst) {
                    Intent intent = new Intent(QRCodeActivity.this, HomeActivity.class);
                    intent.putExtra(FIRST_TARGET, true);
                    startActivity(intent);
                    finish();
                } else {
                finish();
                }
            }
        });

        //重要代码，初始化捕获
        captureManager = new CaptureManager(this, mViewFinder);
        captureManager.initializeFromIntent(getIntent(), this.savedInstanceState);
        captureManager.decode();

    }

    @Override
    public void onTorchOn() {
        isLightOn = true;
        flashImage.setImageResource(R.mipmap.flash_open);
    }

    @Override
    public void onTorchOff() {
        isLightOn = false;
        flashImage.setImageResource(R.mipmap.flash_close);
    }

    @Override
    protected void onResume() {
        super.onResume();
        captureManager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        captureManager.onPause();
    }

    @Override
    protected void onDestroy() {
        captureManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        captureManager.onSaveInstanceState(outState);
    }
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }
    @OnClick(R.id.btn_switch)
    public void swichLight(){
        if(isLightOn){
            mViewFinder.setTorchOff();
        }else{
            mViewFinder.setTorchOn();
        }
    }

    @OnClick(R.id.manual_input)
    public void gotoManualInput(){
        startActivityForResult(new Intent(this, ManualInputActivity.class), MANUAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MANUAL_REQUEST_CODE){
           /*
            if(data!=null) {
                setResult(MANUAL_RESULT_CODE,data);
            }*/
            if (data != null) {
                if (type == QR_TYPE_BATTERY_FIRST || type == QR_TYPE_BATTERY) {
                    String bID = data.getStringExtra(QRCodeActivity.MANUAL_INPUT_TARGET);
                    httpBindBattery(bID);
                } else {
                  //  setResult(MANUAL_RESULT_CODE,data);
                    String bID = data.getStringExtra(QRCodeActivity.MANUAL_INPUT_TARGET);
                    httpBindCar(this,mPreferences.getToken(),bID,8);
                }

            }

        }
    }

    private void httpBindCar(Context context, String token, String sId, int zone) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("sID",sId);
        map.put("zone", zone);
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<BindCarEntry>() {
            @Override
            public void onError(Throwable e) {
                CarHttpFailMessage.carfailMessageShow(QRCodeActivity.this,null,e);
            }

            @Override
            public void onNext(BindCarEntry data) {
                Toast.makeText(QRCodeActivity.this, R.string.bind_car_sucess, Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        CarHttpMethods.getInstance().getBindCar(new ProgressSubscriber(subscriberOnNextListener, context), map);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApplication myApplication = null;
       /* if (isFirst) {
            try {
                myApplication = (MyApplication) getApplicationContext();
            } catch (Exception e) {
                LogUtil.e( e.toString() );
                myApplication = null;
            }

            if (null == myApplication) {
                return;
            }
            myApplication.exitAllActivity();
        }else{
            finish();}*/
        if (isFirst) {
            Intent intent = new Intent(QRCodeActivity.this, HomeActivity.class);
            intent.putExtra(FIRST_TARGET, true);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    private void httpBindBattery(String bid) {
        if (!isNetworkAvaliable()) {
            return;
        }
        if (isRequest) {
            return;
        }
        isRequest = true;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onError(Throwable e) {
                isRequest = false;
                Toast.makeText(QRCodeActivity.this, R.string.bind_battery_fail, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String reslut) {
                if (Integer.parseInt(reslut) == 2) {
                 //   Preferences.getInstance(getApplicationContext()).setKeyNoticeMessageStatus(true);
                    Toast.makeText(QRCodeActivity.this,  R.string.bind_battery_sucess, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(QRCodeActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(QRCodeActivity.this, R.string.bind_battery_sucess, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(QRCodeActivity.this,HomeActivity.class));
                    finish();
                }
                isRequest = false;
            }
        };
        HttpMethods.getInstance().addBattery(new ProgressSubscriber(subscriberOnNextListener, this), mPreferences.getToken(), bid);
    }
}
