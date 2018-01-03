package com.immotor.batterystation.android.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.immotor.batterystation.android.MyApplication;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.pay.wxpay.WXPayManager;
import com.immotor.batterystation.android.ui.activity.FirstEntryRentPayActivity;
import com.immotor.batterystation.android.ui.activity.HomeActivity;
import com.immotor.batterystation.android.ui.activity.ImmediateUseActivity;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.util.LogUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import butterknife.Bind;
import butterknife.OnClick;

import static com.immotor.batterystation.android.ui.activity.FirstGuideActivity.FIRST_TARGET;

/**
 * Created by Ashion on 2017/5/9.
 * 微信回调
 */

public class WXPayEntryActivity extends BaseActivity  implements IWXAPIEventHandler {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.info)
    TextView info;

    private IWXAPI api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        api =  WXPayManager.getInstance(getApplicationContext()).getApi();
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }


    @Override
    public void initUIView() {
        mToolbar.setTitle(R.string.wx_pay_result);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.getWxpayStatus()) {
                    MyApplication.setWxpayStatus(false);
                    intentToHome();
                } else {
                finish();
                }
            }
        });
        info.setText(R.string.wait);
    }
    private void intentToHome() {
        Intent intent = new Intent(WXPayEntryActivity.this, HomeActivity.class);
        intent.putExtra(FIRST_TARGET, true);
        startActivity(intent);
        finish();
    }
    @OnClick(R.id.ok)
    public void actionOk(){
        if (MyApplication.getWxpayStatus()) {
            MyApplication.setWxpayStatus(false);
            intentToImm();
        } else {
            finish();
        }
    }

    private void intentToImm() {
        Intent intent = new Intent(WXPayEntryActivity.this, ImmediateUseActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if(baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX){
            switch (baseResp.errCode){
                case BaseResp.ErrCode.ERR_OK:
                    LogUtil.d("wx pay ok");
                    queryOrder();
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    LogUtil.d("wx pay cancel");
                    if (MyApplication.getWxpayStatus()){
                        Intent intent = new Intent(this,FirstEntryRentPayActivity.class);
                        intent.putExtra("cancle_pay", 1);
                        startActivity(intent);
                    }
                    info.setText(R.string.charge_cancle);
                    break;
                default:
                    LogUtil.d("wx pay resp "+baseResp.errCode+", "+baseResp.errStr);
                    info.setText(R.string.charge_fail);
                    break;
            }
        }
    }

    /**
     * 查询订单结果
     */
    private void queryOrder(){
        if (!isNetworkAvaliable()) {
            info.setText(R.string.not_net);
            return;
        }
        info.setText(R.string.querying_wait_for_while);
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(e.getMessage());
                info.setText(R.string.query_fail);
            }

            @Override
            public void onNext(Object result) {
                info.setText( R.string.pay_scuess);
            }
        };
        HttpMethods.getInstance().wxPayOrderQuery(new ProgressSubscriber(subscriberOnNextListener, this,null), mPreferences.getToken(),MyApplication.getTradeNo());
    }

}
