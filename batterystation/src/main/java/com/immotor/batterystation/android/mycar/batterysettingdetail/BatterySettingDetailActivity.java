package com.immotor.batterystation.android.mycar.batterysettingdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.entity.CarBatteryEntry;
import com.immotor.batterystation.android.ui.base.BaseActivity;

/**
 *
 * Created by jm on 2017/9/19 0019.
 */

public class BatterySettingDetailActivity extends BaseActivity implements View.OnClickListener {
    private CarBatteryEntry mData;
    private TextView current;
    private TextView voltage;
    private TextView times;
    private TextView id;
    private int batteryindex;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.battery_detail_setting_activity);
        mData = (CarBatteryEntry) getIntent().getSerializableExtra(AppConstant.KEY_ENTRY_BATTERY_SETTING_DATA);
        batteryindex = getIntent().getIntExtra("batteryindex", 0);
        if (mData!=null) {
            int index = 0;
            if (mData.getBats().size()>1) {
                index = batteryindex;
            }
        setData(index);
        }
    }

    private void setData(int index) {
        id.setText(mData.getBats().get(index).getId()+"");
        times.setText(mData.getBats().get(index).getCycleCount()+"");
        voltage.setText(mData.getBats().get(index).getNominalVoltage()/100+"v");
        current.setText(mData.getBats().get(index).getNominalCurrent()*10+"mA");
    }

    @Override
    public void initUIView() {
        ImageView mBackImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mBackImg.setImageResource(R.mipmap.nav_back_icon);
        mBackImg.setOnClickListener(this);
        TextView mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.battery_detail);
        id = (TextView) findViewById(R.id.car_batttery_id);
        times = (TextView) findViewById(R.id.car_batttery_times);
        voltage = (TextView) findViewById(R.id.car_batttery_voltage);
        current = (TextView) findViewById(R.id.car_batttery_current);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.home_actionbar_menu:
            finish();
            break;
        default:
            break;
        }
    }
}
