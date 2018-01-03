package com.immotor.batterystation.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.immotor.batterystation.android.MyApplication;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.alipay.AliPayResult;
import com.immotor.batterystation.android.alipay.PayResult;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.entity.RechargeGoodsInfo;
import com.immotor.batterystation.android.entity.RentBatteryListBean;
import com.immotor.batterystation.android.http.ApiException;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.pay.wxpay.WXPayManager;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

import static com.immotor.batterystation.android.ui.activity.FirstGuideActivity.FIRST_TARGET;

/**
 * Created by jm on 2017/11/24 0024.
 */

public class FirstEntryRentPayActivity extends BaseActivity {
    private final int PAY_TYPE_WAIT = 0;
    private final int PAY_TYPE_WX = 1;
    private final int PAY_TYPE_ZFB = 2;
    private List<RentBatteryListBean> mBatteryData;
    private final int SELECT_TYPE_ONE = 3;
    private final int SELECT_TYPE_TWO = 4;
    private int selectType;
    @Bind(R.id.wx_flag)
    ImageView wxFlag;
    @Bind(R.id.zfb_flag)
    ImageView zfbFlag;

    @Bind(R.id.first_entry_one_flag)
    ImageView oneFlag;
    @Bind(R.id.first_entry_two_flag)
    ImageView twoFlag;
    @Bind(R.id.first_entry_two_tittle)
    TextView twoTittle;
    @Bind(R.id.first_entry_one_tittle)
    TextView oneTittle;

    @Bind(R.id.first_entry_one_llyt)
    LinearLayout oneLlyt;
    @Bind(R.id.first_entry_two_llyt)
    LinearLayout twoLlyt;

    @Bind(R.id.num_select_msg)
    TextView numSelectMsg;

    @Bind(R.id.no_data_layout)
    TextView noDataLayout;

    @Bind(R.id.no_net_layout)
    LinearLayout noNetLayout;

    @Bind(R.id.tittle_up)
    LinearLayout upLlyt;

    @Bind(R.id.tittle_down)
    LinearLayout downLlyt;

    private int payType = PAY_TYPE_WX;
    List<RechargeGoodsInfo> mData = new ArrayList<>();
    private boolean isAliRequest = false;
    private static final int SDK_PAY_FLAG = 1;
    private int code = -1;
    private int mOneSelectItem = -1;
    private int mTwoSelectItem = -1;
    private static final int ALI_PAY_RESULT=2;

    Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG:
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    Gson gson = new Gson();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        if (payResult.getResult() != null) {
                            AliPayResult resultData = gson.fromJson(payResult.getResult(), AliPayResult.class);
                            AliorderQueryhttp(resultData.getAlipay_trade_app_pay_response().getTrade_no());
                        }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (payResult.getMemo() != null) {
                            Toast.makeText(FirstEntryRentPayActivity.this, payResult.getMemo() + "", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FirstEntryRentPayActivity.this, R.string.pay_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case ALI_PAY_RESULT:
                    intentToHome();
                    break;
                default:
                    break;
            }
        }
    };


    private void AliorderQueryhttp(String AlitradNo) {
        if (!isNetworkAvaliable()) {
            return;
        }
        if (isAliRequest) {
            return;
        }
        isAliRequest = true;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                Toast.makeText(FirstEntryRentPayActivity.this, R.string.pay_fail, Toast.LENGTH_SHORT).show();
                isAliRequest = false;
                Message msg = new Message();
                msg.what =ALI_PAY_RESULT;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onNext(Object result) {
                Toast.makeText(FirstEntryRentPayActivity.this,  R.string.pay_scuess, Toast.LENGTH_SHORT).show();
                isAliRequest = false;
                Message msg = new Message();
                msg.what =ALI_PAY_RESULT;
                mHandler.sendMessage(msg);
            }
        };
        HttpMethods.getInstance().zfbPayOrderQuery(new ProgressSubscriber(subscriberOnNextListener, this, null), mPreferences.getToken(), MyApplication.getTradeNo(), AlitradNo);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* mPrice = getIntent().getDoubleExtra("rent_money", -1);
        code = getIntent().getIntExtra("rent_code",-1);
        status = getIntent().getIntExtra("entry_activity",-1);*/
        int mCanclePayStatus = getIntent().getIntExtra("cancle_pay", -1);
        setContentView(R.layout.activity_first_entry_rent_activity);
        if (mCanclePayStatus == 1) {
            Toast.makeText(this, R.string.cancle_operate, Toast.LENGTH_SHORT).show();
        }
        httpRentBattery();
    }

    @Override
    public void initUIView() {
        TextView mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.rent_battery);
        ImageView mImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mImg.setImageDrawable(getDrawable(R.mipmap.nav_back_icon_white));
        findViewById(R.id.stub).setVisibility(View.VISIBLE);
        findViewById(R.id.second_item).setBackgroundResource(R.mipmap.entry_bg);
        findViewById(R.id.third_item).setBackgroundResource(R.mipmap.step_white_circle);
        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentToHome();
            }
        });
        setPayUI();
    }

    private void httpRentBattery() {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<List<RentBatteryListBean>>() {
            @Override
            public void onError(Throwable e) {
                Toast.makeText(FirstEntryRentPayActivity.this, R.string.get_rent_list_fail, Toast.LENGTH_SHORT).show();
                noNetView();
            }

            @Override
            public void onNext(List<RentBatteryListBean> reslut) {
                if (reslut != null) {
                    mBatteryData = reslut;

                    for (int i = 0; i < reslut.size(); i++) {
                        if (reslut.get(i).getNum() == 1) {
                            oneLlyt.setVisibility(View.VISIBLE);
                            mOneSelectItem = i;
                            oneTittle.setText("("+getString(R.string.one_battery_unit) + reslut.get(i).getDeposit() + getString(R.string.chense_money)+")");

                        } else if (reslut.get(i).getNum() == 2) {
                            twoLlyt.setVisibility(View.VISIBLE);
                            mTwoSelectItem = i;
                            twoTittle.setText("("+getString(R.string.two_battery_unit) + reslut.get(i).getDeposit() + getString(R.string.chense_money)+")");
                        }
                    }
                    nomalView();
                } else {
                    emptyView();
                }
            }
        };
        HttpMethods.getInstance().getMyRentBatteryList(new ProgressSubscriber(subscriberOnNextListener, this), mPreferences.getToken());
    }

    private void emptyView() {
        noDataLayout.setVisibility(View.VISIBLE);
        upLlyt.setVisibility(View.GONE);
        downLlyt.setVisibility(View.GONE);
        noNetLayout.setVisibility(View.GONE);
    }

    private void noNetView() {
        noDataLayout.setVisibility(View.GONE);
        upLlyt.setVisibility(View.GONE);
        downLlyt.setVisibility(View.GONE);
        noNetLayout.setVisibility(View.VISIBLE);
    }

    private void nomalView() {
        if (mOneSelectItem!= -1 ) {
            selectType = SELECT_TYPE_ONE;
            numSelectMsg.setText(mBatteryData.get(mOneSelectItem).getDeposit() + getString(R.string.chense_money));
        } else if (mTwoSelectItem!= -1) {
            selectType = SELECT_TYPE_TWO;
            numSelectMsg.setText(mBatteryData.get(mTwoSelectItem).getDeposit() +getString(R.string.chense_money));
        }
        noDataLayout.setVisibility(View.GONE);
        upLlyt.setVisibility(View.VISIBLE);
        downLlyt.setVisibility(View.VISIBLE);
        noNetLayout.setVisibility(View.GONE);
        setSelectUI();
    }

    private void setSelectUI() {
        oneFlag.setBackgroundResource(selectType == SELECT_TYPE_ONE ? R.mipmap.wx_button_check : R.mipmap.wx_button_uncheck);
        twoFlag.setBackgroundResource(selectType == SELECT_TYPE_TWO ? R.mipmap.wx_button_check : R.mipmap.wx_button_uncheck);
    }

    @OnClick(R.id.no_net_try_tv)
    public void noNetTry() {
        httpRentBattery();
    }
    @OnClick(R.id.first_entry_one_llyt)
    public void actionOneSelect() {
        selectType = SELECT_TYPE_ONE;
        if (mOneSelectItem != -1) {
            numSelectMsg.setText(mBatteryData.get(mOneSelectItem).getDeposit() + getString(R.string.chense_money));
        }
        setSelectUI();
    }

    @OnClick(R.id.first_entry_two_llyt)
    public void actionTWOSelect() {
        selectType = SELECT_TYPE_TWO;
        if (mTwoSelectItem != -1) {
            numSelectMsg.setText(mBatteryData.get(mTwoSelectItem).getDeposit() +getString(R.string.chense_money));
        }
        setSelectUI();
    }

    @OnClick(R.id.wxpay_panel)
    public void actionWXPay() {
        payType = PAY_TYPE_WX;
        setPayUI();
    }

    @OnClick(R.id.zfbpay_panel)
    public void actionZFBPay() {
        payType = PAY_TYPE_ZFB;
        setPayUI();
    }

    void setPayUI() {
        wxFlag.setBackgroundResource(payType == PAY_TYPE_WX ? R.mipmap.wx_button_check : R.mipmap.wx_button_uncheck);
        zfbFlag.setBackgroundResource(payType == PAY_TYPE_ZFB ? R.mipmap.wx_button_check : R.mipmap.wx_button_uncheck);
    }

    @OnClick(R.id.btn_pay)
    public void ActionPay() {
        if (mBatteryData != null) {
            if (selectType==SELECT_TYPE_ONE) {
                code = mBatteryData.get(mOneSelectItem).getCode();
            }else if (selectType==SELECT_TYPE_TWO){
                code = mBatteryData.get(mTwoSelectItem).getCode();
            }
        }
        if (code == -1) {
            showSnackbar(getString(R.string.please_select_more_one));
            return;
        }
        if (payType == PAY_TYPE_WX) {
            payType = PAY_TYPE_WAIT;
            gotoWXPay(code);
        } else if (payType == PAY_TYPE_ZFB) {
            payType = PAY_TYPE_WAIT;
            gotoZFBPay(code);
        }
    }


    private void gotoWXPay(int code) {
        if (!isNetworkAvaliable()) {
            payType = PAY_TYPE_WX;
            return;
        }
        if (!WXPayManager.getInstance(getApplicationContext()).isSupport()) {
            showSnackbar(getString(R.string.not_install_wx));
            payType = PAY_TYPE_WX;
            return;
        }
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Map<String, String>>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {
                    int errCode = ((ApiException) e).getCode();
                    if (errCode == 635) {
                        Toast.makeText(FirstEntryRentPayActivity.this, R.string.two_battery_limt, Toast.LENGTH_SHORT).show();
                    } else {
                        showSnackbar("gotoWXPay error:" + errCode + ", " + e.getMessage());
                    }
                } else {
                    showSnackbar("gotoWXPay error:" + e.getMessage());
                }
                if (payType == PAY_TYPE_WAIT) {
                    payType = PAY_TYPE_WX;
                }
               // setReult();
            }

            @Override
            public void onNext(Map<String, String> result) {
                if (result != null) {
               //     LogUtil.d("gotoWXPay result:" + result);
                    String nonce = result.get("nonce_str");
                    String trade_no = result.get("out_trade_no");
                    String packageValue = result.get("package");
                    String sign = result.get("sign");
                    String partnerid = result.get("partnerid");
                    String prepay_id = result.get("prepay_id");
                    String timestamp = result.get("timestamp");
                    MyApplication.setTradeNo(trade_no);
              //      LogUtil.d("wx pay prepayid=" + prepay_id + ", nonce_str=" + nonce + ", partnerid=" + partnerid + ", trade_no=" + trade_no);
                    MyApplication.setWxpayStatus(true);
                    WXPayManager.getInstance(getApplicationContext()).requestPay(partnerid, prepay_id, packageValue, nonce, timestamp, sign);
                  //  setReult();
                }
            }
        };
        HttpMethods.getInstance().wxPayPreOrder(new ProgressSubscriber(subscriberOnNextListener, this), mPreferences.getToken(), code);
    }

    private void setReult() {
        Intent intent = new Intent();
        setResult(AppConstant.RENT_BATTERY_RESULT_COD, intent);
        finish();
    }

    private void gotoZFBPay(int code) {
        payType = PAY_TYPE_ZFB;
        getAliPreOrderHttp(code);
    }

    public void getAliPreOrderHttp(int code) {
        if (!isNetworkAvaliable()) {
            payType = PAY_TYPE_ZFB;
            return;
        }

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Map<String, String>>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {
                    int errCode = ((ApiException) e).getCode();
                    if (errCode == 635) {
                        Toast.makeText(FirstEntryRentPayActivity.this, R.string.two_battery_limt, Toast.LENGTH_SHORT).show();
                    } else {
                        showSnackbar("gotoWXPay error:" + errCode + ", " + e.getMessage());
                    }
                } else {
                    showSnackbar("gotoALIPay error:" + e.getMessage());
                }
                if (payType == PAY_TYPE_WAIT) {
                    payType = PAY_TYPE_ZFB;
                }
            }

            @Override
            public void onNext(final Map<String, String> result) {
                if (result != null) {
                    final String orderStr = result.get("orderStr");
                    Runnable payRunnable = new Runnable() {
                        @Override
                        public void run() {
                            PayTask alipay = new PayTask(FirstEntryRentPayActivity.this);

                            String out_trade_no = "";
                            int index = orderStr.indexOf("out_trade_no%22%3A%22");
                            int endIndex = orderStr.indexOf("%22%2C%22");
                            if (index > 0) {
                                out_trade_no = orderStr.substring(index + 21, endIndex);
                            }
                            MyApplication.setTradeNo(out_trade_no);
                            Map<String, String> payResult = alipay.payV2(orderStr, true);
                            Message msg = new Message();
                            msg.what = SDK_PAY_FLAG;
                            msg.obj = payResult;
                            mHandler.sendMessage(msg);
                        }
                    };

                    Thread payThread = new Thread(payRunnable);
                    payThread.start();
                }
            }
        };
        HttpMethods.getInstance().zfbPayOrder(new ProgressSubscriber(subscriberOnNextListener, this), mPreferences.getToken(), code);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intentToHome();
    }

    private void intentToHome() {
        Intent intent = new Intent(FirstEntryRentPayActivity.this, HomeActivity.class);
        intent.putExtra(FIRST_TARGET, true);
        startActivity(intent);
        finish();
    }
}
