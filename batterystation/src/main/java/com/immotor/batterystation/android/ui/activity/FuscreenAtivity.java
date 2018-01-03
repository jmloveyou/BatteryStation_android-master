package com.immotor.batterystation.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.ui.base.BaseActivity;

/**
 * Created by jm on 2017/9/28 0028.
 */

public class FuscreenAtivity extends BaseActivity {

    private String station_url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_img_dialog_layout);
        station_url = mPreferences.getOrderStationImgUrl();
        ImageView imageView = (ImageView) findViewById(R.id.station_img_dialog);
        Glide.with(this).load(station_url).into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initUIView() {

    }
}
