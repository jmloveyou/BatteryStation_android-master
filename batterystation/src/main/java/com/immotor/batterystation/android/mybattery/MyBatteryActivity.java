package com.immotor.batterystation.android.mybattery;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.entity.MybatteryListBean;
import com.immotor.batterystation.android.entity.RentBatteryListBean;
import com.immotor.batterystation.android.http.ApiException;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.rentbattery.pay.RentBateryPayActivity;
import com.immotor.batterystation.android.rentbattery.refund.RefundActivity;
import com.immotor.batterystation.android.ui.activity.HomeActivity;
import com.immotor.batterystation.android.ui.activity.ProfileActivity;
import com.immotor.batterystation.android.ui.activity.QRCodeActivity;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.mybattery.mvpView.IMyBatteryView;
import com.immotor.batterystation.android.mybattery.mvppresent.MyBatteryPresent;
import com.immotor.batterystation.android.util.PermissionUtils;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;

/**
 * Created by jm on 2017/7/27.
 */

public class MyBatteryActivity extends BaseActivity implements View.OnClickListener, IMyBatteryView {
    private LinearLayout mNoBateryView;
    private RecyclerView mRecyView;
    private String token;
    private MyBatteryPresent myBatteryPresent;
    private MybatteryListBean mDataBean;
    private LinearLayout mNoNetLayout;
    private TextView mAddBattery;
    private TextView mRentbattery;
    private TextView mExplain;
    private BottomSheetDialog mDialog;
    private boolean notice_entry=false;
    private boolean misNoticeRequest=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_battery);
        token = mPreferences.getToken();
  //      notice_entry = getIntent().getBooleanExtra("notice_entry", false);
        myBatteryPresent = new MyBatteryPresent(this, this, token);
    }

    private void initData() {
        myBatteryPresent.requestBatteryMsgList();
    }
    private void initNotice() {
        if (!isNetworkAvaliable()) {
            return;
        }
        if (misNoticeRequest) {
            return;
        }
        misNoticeRequest = true;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {
                    int code = ((ApiException) e).getCode();
                    if (code == 601) {
                        mapNoticeDialog().show();
                    }
                }
                misNoticeRequest = false;
            }

            @Override
            public void onNext(Object object) {

                misNoticeRequest = false;
            }
        };
        HttpMethods.getInstance().notice(new ProgressSubscriber(subscriberOnNextListener, this, null), mPreferences.getToken());
    }
    public Dialog mapNoticeDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.notice_dialog_layout, null);// 得到加载view
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.notice_dialog);// 加载布局
        TextView tipTextView = (TextView) v.findViewById(R.id.notice_sure);// 提示文字
        final Dialog dialog = new Dialog(this, R.style.dialog_style);// 创建自定义样式dialog
        dialog.setCancelable(true);

        dialog.setContentView(layout);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.65); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);

        tipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void initUIView() {
        TextView mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText("我的电池");
        ImageView mImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mImg.setOnClickListener(this);
        mImg.setImageDrawable(getDrawable(R.mipmap.nav_back_icon_white));
        mNoNetLayout = (LinearLayout) findViewById(R.id.no_net_layout);
        mRecyView = (RecyclerView) findViewById(R.id.my_battery_recyview);
        mRecyView.setLayoutManager(new GridLayoutManager(this, 1));
        mNoBateryView = (LinearLayout) findViewById(R.id.my_battery_no_battery);
        mExplain = (TextView) findViewById(R.id.my_battery_explain);
        mRentbattery = (TextView) findViewById(R.id.my_battery_rent);
        ititListener();
    }

    private void ititListener() {
        mRentbattery.setOnClickListener(this);
        findViewById(R.id.my_battery_buy).setOnClickListener(this);
        mAddBattery = (TextView) findViewById(R.id.my_battery_add);
        mAddBattery.setOnClickListener(this);
        findViewById(R.id.no_net_try_tv).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_battery_buy:
               // showdialog();
                break;
            case R.id.my_battery_add:
                BatteryScan();
                break;
            case R.id.home_actionbar_menu:
                finish();
                break;
            case R.id.no_net_try_tv:
                myBatteryPresent.requestBatteryMsgList();
                break;
            case R.id.my_battery_rent:
                if (mDataBean!=null && mDataBean.getContent().size()==2) {
                    Toast.makeText(this, R.string.two_battery_limt, Toast.LENGTH_SHORT).show();
                }else {
                    httpRentBattery();
                }
                break;
            default:
                break;

        }
    }

    private void httpRentBattery() {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<List<RentBatteryListBean>>() {
            @Override
            public void onError(Throwable e) {
                Toast.makeText(MyBatteryActivity.this, R.string.get_rent_list_fail, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(List<RentBatteryListBean> reslut) {
                if (reslut != null) {
                    initBottomSheet(reslut);
                } else {
                    Toast.makeText(MyBatteryActivity.this, R.string.no_battery_rent, Toast.LENGTH_SHORT).show();
                }
            }
        };
        HttpMethods.getInstance().getMyRentBatteryList(new ProgressSubscriber(subscriberOnNextListener, this), token);
    }

    public void initBottomSheet(final List<RentBatteryListBean> reslut) {
        mDialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.rent_battery_dialog, null, false);
        TextView cancle = (TextView) view.findViewById(R.id.rent_dialog_cancle);
        TextView rentOne = (TextView) view.findViewById(R.id.rent_one_battery);
        TextView rentTwo = (TextView) view.findViewById(R.id.rent_two_battery);
        if (reslut.get(0) != null) {
            String tittle = getString(R.string.one_battery);
            if (reslut.get(0).getNum() == 1) {
                tittle = getString(R.string.one_battery);
            } else if (reslut.get(0).getNum() == 2){
                tittle =getString(R.string.two_battery);
            }
            rentOne.setText(tittle+ " ("+getString(R.string.peldge)+": " + reslut.get(0).getDeposit() +getString(R.string.chense_money)+ ")");
        } else {
            rentOne.setVisibility(View.GONE);
        }
        if (reslut.get(1) != null) {
                String tittle = getString(R.string.one_battery);
                if (reslut.get(1).getNum() == 1) {
                    tittle = getString(R.string.one_battery);
                } else if (reslut.get(1).getNum() == 2){
                    tittle =getString(R.string.two_battery);
                }
                rentTwo.setText(tittle+ " ("+getString(R.string.peldge)+": "  + reslut.get(1).getDeposit() + getString(R.string.chense_money)+ ")");
        } else {
            rentTwo.setVisibility(View.GONE);
        }
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        rentOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityToPay(reslut.get(0).getDeposit(),reslut.get(0).getCode());
                mDialog.dismiss();
            }
        });
        rentTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityToPay(reslut.get(1).getDeposit(),reslut.get(1).getCode());
                mDialog.dismiss();
            }
        });
        mDialog.setContentView(view);
        mDialog.show();
    }
       private void startActivityToPay(double price,int code){
           Intent intent = new Intent(this,RentBateryPayActivity.class);
           intent.putExtra("rent_money",price);
           intent.putExtra("rent_code",code);
           startActivity(intent);
       }


    private void showdialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("购买电池");
        dialog.setMessage("是否购买电池");
        dialog.setPositiveButton("购买", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myBatteryPresent.requestBatteryBuy(1);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void BatteryScan() {
        new IntentIntegrator(this)
                .addExtra(QRCodeActivity.QR_TYPE_TARGET, QRCodeActivity.QR_TYPE_BATTERY)
                .setOrientationLocked(false)
                .setCaptureActivity(QRCodeActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); //  初始化扫描
    }

    @Override
    public void addData(MybatteryListBean bean) {
        this.mDataBean = bean;
        MyBatteryAdapter myBatteryAdapter = new MyBatteryAdapter(R.layout.item_my_battery_recy_layout, mDataBean.getContent());
        myBatteryAdapter.openLoadAnimation(ALPHAIN);
        mRecyView.setAdapter(myBatteryAdapter);

    }

    @Override
    public void onBatteryShow() {
        if (notice_entry) {
            mAddBattery.setVisibility(View.VISIBLE);
        } else {
            mAddBattery.setVisibility(View.GONE);
        }

        mRentbattery.setVisibility(View.VISIBLE);
        mExplain.setVisibility(View.VISIBLE);
        mNoNetLayout.setVisibility(View.GONE);
        mRecyView.setVisibility(View.GONE);
        mNoBateryView.setVisibility(View.VISIBLE);
    }

    @Override
    public void BatteryListShow() {
        boolean haveNotfetch = false;
        for (int i=0;i<mDataBean.getContent().size();i++){
            if (mDataBean.getContent().get(i).getStatus()==0) {
                haveNotfetch = true;
            }
        }

        if (notice_entry || haveNotfetch) {
            mAddBattery.setVisibility(View.VISIBLE);
        } else {
            mAddBattery.setVisibility(View.GONE);
        }

        mRentbattery.setVisibility(View.VISIBLE);
        mExplain.setVisibility(View.VISIBLE);
        mNoNetLayout.setVisibility(View.GONE);
        mRecyView.setVisibility(View.VISIBLE);
        mNoBateryView.setVisibility(View.GONE);
    }

    @Override
    public void getBatteryFail() {
        mAddBattery.setVisibility(View.GONE);
        mExplain.setVisibility(View.GONE);
        mRentbattery.setVisibility(View.GONE);
        mRecyView.setVisibility(View.GONE);
        mNoBateryView.setVisibility(View.GONE);
        mNoNetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            String bID = null;
            if (resultCode == QRCodeActivity.MANUAL_RESULT_CODE) {
                bID = data.getStringExtra(QRCodeActivity.MANUAL_INPUT_TARGET);
            } else {
                IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (intentResult != null) {
                    bID = intentResult.getContents();
                }
            }
            if (bID != null) {
                httpBindBattery(bID);
            }
        }
    }

    private void httpBindBattery(String bid) {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onError(Throwable e) {

                Toast.makeText(MyBatteryActivity.this, R.string.bind_battery_fail, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String reslut) {
                if (reslut != null) {
                    if (Integer.parseInt(reslut) == 2) {
                     //   Preferences.getInstance(getApplicationContext()).setKeyNoticeMessageStatus(true);
                        Toast.makeText(MyBatteryActivity.this, R.string.bind_battery_sucess, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MyBatteryActivity.this, HomeActivity.class));
                    } else {
                        initData();
                        Toast.makeText(MyBatteryActivity.this, R.string.bind_battery_sucess, Toast.LENGTH_SHORT).show();

                    }
                }
            }
        };
        HttpMethods.getInstance().addBattery(new ProgressSubscriber(subscriberOnNextListener, this), token, bid);
    }
}
