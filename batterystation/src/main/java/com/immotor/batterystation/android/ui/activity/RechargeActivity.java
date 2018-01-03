package com.immotor.batterystation.android.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
 * Created by Ashion on 2017/5/11.
 */

public class RechargeActivity extends BaseActivity {

    private final int PAY_TYPE_WAIT = 0;
    private final int PAY_TYPE_WX = 1;
    private final int PAY_TYPE_ZFB = 2;

    @Bind(R.id.recycler_container)
    RecyclerView mRecyclerContainer;

    @Bind(R.id.wx_flag)
    ImageView wxFlag;
    @Bind(R.id.zfb_flag)
    ImageView zfbFlag;

    RechargeAdapter adapter;
    private int payType = PAY_TYPE_WX;
    List<RechargeGoodsInfo> mData = new ArrayList<>();
   // boolean isFirst;
    int mPrice;
    private boolean isAliRequest = false;
    private static final int SDK_PAY_FLAG = 1;

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
                        if (payResult.getResult()!=null) {
                            AliPayResult resultData = gson.fromJson(payResult.getResult(), AliPayResult.class);
                            AliorderQueryhttp(resultData.getAlipay_trade_app_pay_response().getTrade_no());
                        }
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        if (payResult.getMemo() != null) {
                            Toast.makeText(RechargeActivity.this, payResult.getMemo() + "", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RechargeActivity.this, R.string.pay_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
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
                Toast.makeText(RechargeActivity.this, R.string.pay_fail, Toast.LENGTH_SHORT).show();
                isAliRequest = false;
            }

            @Override
            public void onNext(Object result) {
                Toast.makeText(RechargeActivity.this,  R.string.pay_scuess, Toast.LENGTH_SHORT).show();
                isAliRequest = false;
                finish();
            }
        };
        HttpMethods.getInstance().zfbPayOrderQuery(new ProgressSubscriber(subscriberOnNextListener, this, null), mPreferences.getToken(), MyApplication.getTradeNo(), AlitradNo);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    //    isFirst = getIntent().getBooleanExtra(FirstGuideActivity.FIRST_TARGET, false);
        mPrice = getIntent().getIntExtra(AppConstant.SELECT_MYCOMBO_RESULT_TAG, 0);
        setContentView(R.layout.activity_recharge);
    }

    @Override
    public void initUIView() {
        TextView mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.wallet_charge);
        ImageView mImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mImg.setImageDrawable(getDrawable(R.mipmap.nav_back_icon_white));
      /*  if (isFirst) {
            findViewById(R.id.stub).setVisibility(View.VISIBLE);
            findViewById(R.id.second_item).setBackgroundResource(R.mipmap.corner_bg);
            findViewById(R.id.third_item).setBackgroundResource(R.mipmap.corner_bg);
        //    findViewById(R.id.fourth_item).setBackgroundResource(R.mipmap.corner_bg);
        }*/

        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
               /* if (isFirst) {
                startActivity(new Intent(RechargeActivity.this, HomeActivity.class));
                    finish();
                } else {
                    finish();
                }*/
            }
        });

        adapter = new RechargeAdapter(this, mData);
        //设置网格布局管理器
        mRecyclerContainer.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerContainer.setAdapter(adapter);
        setPayUI();
        httpGetPayList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*MyApplication myApplication = null;
        if (isFirst) {
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
       /* if (isFirst) {
            Intent intent = new Intent(RechargeActivity.this, HomeActivity.class);
            intent.putExtra(FIRST_TARGET, true);
            startActivity(intent);
            finish();
        } else {
            finish();
        }*/
        finish();
    }

    private void httpGetPayList() {
        if (!isNetworkAvaliable()) {
            return;
        }
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<List<RechargeGoodsInfo>>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(e.getMessage());
            }

            @Override
            public void onNext(List<RechargeGoodsInfo> result) {
                if (result != null) {
                    mData.clear();
                    mData.addAll(result);
                    int curSelect = adapter.getSelect();
                    if (mData.size() > 0 && (curSelect < 0 || curSelect >= mData.size())) {
                        adapter.setSelect(0);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        };
        HttpMethods.getInstance().chargeAvailable(new ProgressSubscriber(subscriberOnNextListener, this, null), mPreferences.getToken());

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

    @OnClick(R.id.recharge_protocol)
    public void actionRechargeProtocol() {
       /* Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(WebActivity.TYPE_KEY, WebActivity.TYPE_VALUE_RECHARGE_PROTOCOL);
        startActivity(intent);*/
    }


    void setPayUI() {
        wxFlag.setBackgroundResource(payType == PAY_TYPE_WX ? R.mipmap.wx_button_check : R.mipmap.wx_button_uncheck);
        zfbFlag.setBackgroundResource(payType == PAY_TYPE_ZFB ? R.mipmap.wx_button_check : R.mipmap.wx_button_uncheck);
    }

    @OnClick(R.id.btn_pay)
    public void ActionPay() {
        int code;
        int select = adapter.getSelect();
        if (select >= 0 && select < mData.size()) {
            code = mData.get(select).getCode();
        } else {
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
                    showSnackbar("gotoWXPay error:" + errCode + ", " + e.getMessage());
                } else {
                    showSnackbar("gotoWXPay error:" + e.getMessage());
                }
                if (payType == PAY_TYPE_WAIT) {
                    payType = PAY_TYPE_WX;
                }
            }

            @Override
            public void onNext(Map<String, String> result) {
                if (result != null) {
                    LogUtil.d("gotoWXPay result:" + result);
                    String nonce = result.get("nonce_str");
                    String trade_no = result.get("out_trade_no");
                    String packageValue = result.get("package");
                    String sign = result.get("sign");
                    String partnerid = result.get("partnerid");
                    String prepay_id = result.get("prepay_id");
                    String timestamp = result.get("timestamp");
                    MyApplication.setTradeNo(trade_no);
                    LogUtil.d("wx pay prepayid=" + prepay_id + ", nonce_str=" + nonce + ", partnerid=" + partnerid + ", trade_no=" + trade_no);
                    WXPayManager.getInstance(getApplicationContext()).requestPay(partnerid, prepay_id, packageValue, nonce, timestamp, sign);
                    finish();
                }
            }
        };
        HttpMethods.getInstance().wxPayPreOrder(new ProgressSubscriber(subscriberOnNextListener, this), mPreferences.getToken(), code);
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
                    showSnackbar("gotoALIPay error:" + errCode + ", " + e.getMessage());
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
                            PayTask alipay = new PayTask(RechargeActivity.this);

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


    class RechargeAdapter extends RecyclerView.Adapter<RechargeAdapter.MyViewHolder> {

        Context context;
        List<RechargeGoodsInfo> data;
        private int select = -1;

        public RechargeAdapter(Context context, List<RechargeGoodsInfo> data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                    context).inflate(R.layout.item_recharge_detail, parent, false));
            return holder;
        }

        public int getSelect() {
            return select;
        }

        public void setSelect(int select) {
            this.select = select;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            RechargeGoodsInfo info = mData.get(position);
            holder.rechargeView.setText(getString(R.string.charge_chanese_symbol) +" "+(int) info.getDeposit());
            if (info.getTotal()==0) {
                holder.extraView.setVisibility(View.GONE);
            }else {
                holder.extraView.setVisibility(View.VISIBLE);
                holder.extraView.setText(getString(R.string.get_chanese_symbol)  +" "+ (int)info.getTotal());
            }
            if (position == select) {
                holder.rechargeView.setTextColor(0xffffffff);
                holder.extraView.setTextColor(0xffffffff);
                holder.containerView.setBackgroundResource(R.mipmap.recharge_item_select);
            } else {
                holder.rechargeView.setTextColor(getResources().getColor(R.color.colorAccent));
                holder.extraView.setTextColor(getResources().getColor(R.color.colorAccent));
                holder.containerView.setBackgroundResource(R.mipmap.recharge_item_nomal);
            }
            holder.containerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select = position;
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView rechargeView;
            TextView extraView;
            View containerView;

            public MyViewHolder(View itemView) {
                super(itemView);
                containerView = itemView;
                rechargeView = (TextView) itemView.findViewById(R.id.recharge_value);
                extraView = (TextView) itemView.findViewById(R.id.extra_value);
            }
        }
    }

}
