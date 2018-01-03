package com.immotor.batterystation.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.ui.base.BaseActivity;

import static com.immotor.batterystation.android.ui.activity.FirstGuideActivity.FIRST_TARGET;

/**
 * Created by jm on 2017/11/27 0027.
 */

public class ImmediateUseActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_entry_use_activity);
    }

    @Override
    public void initUIView() {
        TextView mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.use_now);
        findViewById(R.id.stub).setVisibility(View.VISIBLE);
        findViewById(R.id.second_item).setBackgroundResource(R.mipmap.entry_bg);
        findViewById(R.id.third_item).setBackgroundResource(R.mipmap.entry_bg);
        ImageView mImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mImg.setImageDrawable(getDrawable(R.mipmap.nav_back_icon_white));
        Button useButton = (Button) findViewById(R.id.btn_pay);
        useButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToHome();
            }
        });
        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToHome();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intentToHome();
    }

    private void intentToHome() {
        Intent intent = new Intent(ImmediateUseActivity.this, HomeActivity.class);
        intent.putExtra(FIRST_TARGET, true);
        startActivity(intent);
        finish();
    }
}
