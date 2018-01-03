package com.immotor.batterystation.android.mywallet.mywalletmian;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.MyWalletBean;
import com.immotor.batterystation.android.entity.RefundPayListBean;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.mybattery.MyBatteryActivity;
import com.immotor.batterystation.android.mywallet.MyWalletAtiityDetail;
import com.immotor.batterystation.android.mywallet.mywalletmian.mvppresent.MyWalletPresent;
import com.immotor.batterystation.android.mywallet.mywalletmian.mvpview.IMyWalletView;
import com.immotor.batterystation.android.rentbattery.refund.RefundActivity;
import com.immotor.batterystation.android.ui.activity.RechargeActivity;
import com.immotor.batterystation.android.ui.base.BaseActivity;

import java.text.DecimalFormat;

/**
 * Created by jm on 2017/8/3 0003.
 */

public class MyWalletAtivity extends BaseActivity implements IMyWalletView, View.OnClickListener {

    TextView mMoneyView;
    private MyWalletPresent mMyWalletPresent;
    private String mToken;
    private TextView mRecharge;
    private TextView mRefund;
    private TextView mRent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
    }

    @Override
    public void initUIView() {
        TextView mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.my_wallet);
        ImageView mImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        TextView rightTv = (TextView) findViewById(R.id.home_actionbar_right);
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText(R.string.detail);
        mImg.setOnClickListener(this);
        rightTv.setOnClickListener(this);
        mImg.setImageDrawable(getDrawable(R.mipmap.nav_back_icon_white));
        mMoneyView = (TextView) findViewById(R.id.money);
        mRecharge = (TextView) findViewById(R.id.recharge);
        mRefund = (TextView) findViewById(R.id.fefund_btn);
        mRefund.setOnClickListener(this);
        mRent = (TextView) findViewById(R.id.rent_btn);
        mRent.setOnClickListener(this);

        mRecharge.setOnClickListener(this);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/haettenschweiler.ttf");
        mMoneyView.setTypeface(typeface);
        mMyWalletPresent = new MyWalletPresent(this, this);
        mToken = mPreferences.getToken();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMyWalletPresent.requestChargeRecord(mToken);
    }
    private void httpRefundPayList() {

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<RefundPayListBean>() {
            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(RefundPayListBean bean) {
                if (bean != null && bean.getContent().size() > 0) {
                    mRefund.setVisibility(View.VISIBLE);
                    mRent.setVisibility(View.GONE);
                } else {
                    mRefund.setVisibility(View.GONE);
                    mRent.setVisibility(View.VISIBLE);
                }
            }
        };
        HttpMethods.getInstance().getMyDepositList(new ProgressSubscriber(subscriberOnNextListener, this,null), mToken,0,10);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recharge:
                startActivity(new Intent(this, RechargeActivity.class));
                break;
            case R.id.home_actionbar_menu:
                finish();
                break;
            case R.id.home_actionbar_right:
                startActivity(new Intent(this, MyWalletAtiityDetail.class));
                break;
            case R.id.fefund_btn:
                startActivity(new Intent(this, RefundActivity.class));
                break;
            case R.id.rent_btn:
                startActivity(new Intent(this, MyBatteryActivity.class));
                break;
            default:
                break;

        }
    }

    @Override
    public void showEmpty() {
        mMoneyView.setText("0");
        httpRefundPayList();
    }

    @Override
    public void showFail() {
    }

    @Override
    public void addData(MyWalletBean data) {
        if (data.getAmount()==0) {
            mMoneyView.setText("0.00");
        }else {
            mMoneyView.setText(String.valueOf(new DecimalFormat(".00").format(data.getAmount())));
        }
        httpRefundPayList();
    }
}
