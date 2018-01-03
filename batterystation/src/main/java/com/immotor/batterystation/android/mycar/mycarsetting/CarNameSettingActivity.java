package com.immotor.batterystation.android.mycar.mycarsetting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.entity.UpdateEntry;
import com.immotor.batterystation.android.http.carhttp.CarHttpFailMessage;
import com.immotor.batterystation.android.http.carhttp.CarHttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.mycar.mycarsetting.mvpview.IMyCarSettingView;
import com.immotor.batterystation.android.ui.base.BaseActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jm on 2017/9/22 0022.
 */

public class CarNameSettingActivity extends BaseActivity implements IMyCarSettingView,View.OnClickListener {

    private ImageView mBackImg;
    private TextView mTittle;
    private TextView rightTittle;
    private TextView mEditText;
    private boolean isRequesting=false;
    private String mToken;
    private String sid;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_name_setting);
        mToken = Preferences.getInstance(this).getToken();
        sid = getIntent().getStringExtra(AppConstant.KEY_ENTRY_BATTERY_CAR_SID);
    }

    @Override
    public void initUIView() {
        mBackImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mBackImg.setOnClickListener(this);
        mBackImg.setImageDrawable(getResources().getDrawable(R.mipmap.nav_back_icon_white));
        mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.car_nickname);
        mTittle.setGravity(Gravity.CENTER);
        rightTittle = (TextView) findViewById(R.id.home_actionbar_right);
        rightTittle.setOnClickListener(this);
        rightTittle.setText(R.string.save);
        rightTittle.setVisibility(View.VISIBLE);
        mEditText = (TextView) findViewById(R.id.edit_tv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_actionbar_menu:
                finish();
                break;
            case R.id.home_actionbar_right:
                String name = mEditText.getText().toString().trim();
                if (name.length() > 0) {
                    requestUpdata(name);
                } else {
                    Toast.makeText(this, R.string.nick_not_import_null, Toast.LENGTH_SHORT).show();
                }
            default:
                break;

        }
    }

    private void requestUpdata(final String name) {

        if (isRequesting) {
            return;
        }
        isRequesting = true;
        Map<String, Object> map = new HashMap<>();
        map.put("token",mToken);
        map.put("sID",sid);
        map.put("nickName",name);
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<UpdateEntry>() {
            @Override
            public void onError(Throwable e) {
                CarHttpFailMessage.carfailMessageShow(CarNameSettingActivity.this,null,e);
                isRequesting = false;
            }

            @Override
            public void onNext(UpdateEntry data) {
                isRequesting = false;
                Intent intent = new Intent();
                intent.putExtra("NickName", name);
                setResult(AppConstant.RERESULT_CAR_NAME_SETTING_CODE, intent);
                finish();
            }
        };
        CarHttpMethods.getInstance().getUpdate(new ProgressSubscriber(subscriberOnNextListener, this), map);
    }

}
