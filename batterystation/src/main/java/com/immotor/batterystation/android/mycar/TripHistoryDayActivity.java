package com.immotor.batterystation.android.mycar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;


import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.TripDayBean;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.util.LogUtil;

import butterknife.Bind;

public class TripHistoryDayActivity extends BaseActivity {


    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private TripDayBean tripDayBean;
    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history_day);
    }

    @Override
    public void initUIView() {
        Intent intent = getIntent();
        String dateStr = intent.getStringExtra("DATE_STR");
        tripDayBean = (TripDayBean)intent.getSerializableExtra("TRIP_DAY_BEAN");
        mToolbar.setTitle(dateStr);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fragment = TripHistoryFragment.newInstance(tripDayBean);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (fragment instanceof TripHistoryFragment) {
                ((TripHistoryFragment) fragment).startHeadAnimation();
            }
        }catch (Exception e){
            LogUtil.d(e.getMessage());
        }
    }
}
