package com.immotor.batterystation.android.mycar.mycarbattery.battery2;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.entity.BatsBean;
import com.immotor.batterystation.android.ui.base.BaseFragment;
import com.immotor.batterystation.android.ui.views.BatteryAnimView;

/**
 * Created by jm on 2017/9/19 0019.
 */

public class BatteryTwoFragment extends BaseFragment {
    private BatteryAnimView amin;
    private TextView temp;
    private TextView capacity;
    private ImageView noData;
    private LinearLayout nomal;
    private int isFail;
    private int isEmpty;
    private BatsBean mData;
    private TextView percentValue;

    @Override
    public int getContentLayout() {
        return R.layout.battery_fragment_two;
    }

    @Override
    public void initUIViews() {
        amin = (BatteryAnimView) getView().findViewById(R.id.battery_amin);
        temp = (TextView) getView().findViewById(R.id.temp_value);
        capacity = (TextView) getView().findViewById(R.id.capacity_value);
        percentValue = (TextView) getView().findViewById(R.id.capacity_percent_value);
        noData = (ImageView) getView().findViewById(R.id.battery_no_data);
        nomal = (LinearLayout) getView().findViewById(R.id.battery_data_content);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/haettenschweiler.ttf");
        temp.setTypeface(typeface);
        capacity.setTypeface(typeface);
        percentValue.setTypeface(typeface);
        initData();
    }

    private void initData() {
        Bundle bundle = getArguments();
        isEmpty = bundle.getInt(AppConstant.KEY_ENTRY_BATTERY_REQUEST_EMPTY, -1);
        isFail = bundle.getInt(AppConstant.KEY_ENTRY_BATTERY_REQUEST_FAIL, -1);
        mData = (BatsBean) bundle.getSerializable(AppConstant.KEY_ENTRY_BATTERY_REQUEST_TWO_DATA);
        if (isEmpty == 1) {
            noData.setVisibility(View.VISIBLE);
            nomal.setVisibility(View.GONE);
        } else if (isFail == 1) {
            noData.setVisibility(View.VISIBLE);
            nomal.setVisibility(View.GONE);
        } else {
            if (mData != null) {
                setData();
            } else {
                noData.setVisibility(View.VISIBLE);
                nomal.setVisibility(View.GONE);
            }
        }
    }

    private void setData() {
        amin.startAnim();
        amin.setValue(mData.getSoc());
        temp.setText(String.valueOf(mData.getTemperature()));
        capacity.setText(String.valueOf(mData.getCapacity() * 100));
        percentValue.setText(String.valueOf(mData.getSoc()));
    }
    @Override
    public void onPause() {
        super.onPause();
        amin.stopAnim();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (amin!=null) {
            amin.clearAnimation();
        }
    }
}
