package com.immotor.batterystation.android.mycar.mycarmain;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.entity.CarHeartBeatEntry;
import com.immotor.batterystation.android.entity.CarListBean;
import com.immotor.batterystation.android.intentactivity.IntentActivityMethod;
import com.immotor.batterystation.android.mycar.mycarmain.mvppresent.MyCarPresent;
import com.immotor.batterystation.android.mycar.mycarmain.mvpview.IMyCarView;
import com.immotor.batterystation.android.ui.activity.QRCodeActivity;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jm on 2017/8/3 0003.
 */

public class MyCarActivity extends BaseActivity implements IMyCarView, View.OnClickListener {

    private ImageView mBackImg;
    private TextView mTittle;
    private TextView mTittleAddCar;
    private LinearLayout mNOdataShow;
    private LinearLayout mNoNetShow;
    private LinearLayout mNoDataAddCar;
    private TextView mNoNetBtn;
    private LinearLayout mNomalShow;
    private RecyclerView mRecyView;
    //  private MyCarRecyAdapter MyCarRecyAdapter;
    private String mToken;
    private MyCarPresent myCarPresent;
    private Timer mTimer;
    private List<CarListBean> mCarList = new ArrayList<>();
    private MyCarAdapter myCarAdapter;
    private TextView mCarListTittle;
    private int mZone;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mybattery_car);
        mToken = Preferences.getInstance(this).getToken();
        myCarPresent = new MyCarPresent(this, this);
        mZone = getZone();
        Log.d("jmjm", "mZone="+mZone);
    }

    private void initHeartbeat(final String sids) {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isNetworkAvaliable()) {
                    myCarPresent.requestHeartBeat(mToken, sids);
                }
            }
        }, 0, 8000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNetworkAvaliable()) {
            myCarPresent.requestBatteryCar(mToken);
        } else {
            Toast.makeText(this, R.string.not_net, Toast.LENGTH_SHORT).show();
            showFail();
        }
    }

    @Override
    public void initUIView() {
        initView();
    }

    private void initView() {
        mBackImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mBackImg.setImageDrawable(getResources().getDrawable(R.mipmap.nav_back_icon_white));
        mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.may_car);
        mTittle.setGravity(Gravity.CENTER);
        mTittleAddCar = (TextView) findViewById(R.id.home_actionbar_right);
        // 有数据的时候显示
        mTittleAddCar.setText(R.string.add_car);
        mNOdataShow = (LinearLayout) findViewById(R.id.no_data_layout);
        mNoDataAddCar = (LinearLayout) findViewById(R.id.add_car_llyt);
        mNoNetShow = (LinearLayout) findViewById(R.id.no_net_layout);
        mNoNetBtn = (TextView) findViewById(R.id.no_net_try_tv);
        mNomalShow = (LinearLayout) findViewById(R.id.recy_llyt);
        mRecyView = (RecyclerView) findViewById(R.id.my_car_recyView);
        mRecyView.setLayoutManager(new LinearLayoutManager(this));
        //    MyCarRecyAdapter = new MyCarRecyAdapter(R.layout.item_my_car_recy,this);
        myCarAdapter = new MyCarAdapter(this);
        mCarListTittle = (TextView) findViewById(R.id.all_car_tittle);
        initListener();

    }

    private void initListener() {

        myCarAdapter.setOnItemClickListener(new MyCarAdapter.OnItemListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.item_my_car_recy:
                        String sid = mCarList.get(position).getsID();
                        IntentActivityMethod.carActivitytoCarBatteryActivity(MyCarActivity.this, sid);
                        break;
                    case R.id.my_car_setting_llyt:
                        IntentActivityMethod.carActivitytoCarSettingActivity(MyCarActivity.this, mCarList.get(position));
                        break;
                    case R.id.my_car_travel_recode:
                        IntentActivityMethod.carActivitytoMyCarTravelActivity(MyCarActivity.this, mCarList.get(position).getsID());
                        break;
                    case R.id.my_car_address_llyt:
                        IntentActivityMethod.carActivitytoMyCarAddressActivity(MyCarActivity.this, mCarList.get(position).getLocation());
                        break;
                    default:
                        break;
                }
            }
        });
      /*


        MyCarRecyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String sid = MyCarRecyAdapter.getData().get(position).getsID();
                Log.d("jmjm","1");
              //  IntentActivityMethod.carActivitytoCarBatteryActivity(MyCarActivity.this,sid);
            }
        });
        MyCarRecyAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.my_car_setting:
                        Log.d("jmjm","2");
                    //    IntentActivityMethod.carActivitytoCarSettingActivity(MyCarActivity.this,MyCarRecyAdapter.getData().get(position));
                        break;
                    case R.id.my_car_travel_recode:
                        Log.d("jmjm","3");
                   //     IntentActivityMethod.carActivitytoMyCarTravelActivity(MyCarActivity.this,MyCarRecyAdapter.getData().get(position).getsID());
                        break;
                    case R.id.my_car_address_llyt:
                        Log.d("jmjm","4");
                   //     IntentActivityMethod.carActivitytoMyCarAddressActivity(MyCarActivity.this,MyCarRecyAdapter.getData().get(position).getLocation());
                        break;
                    default:
                        break;
                }
            }
        });*/
        mBackImg.setOnClickListener(this);
        mTittleAddCar.setOnClickListener(this);
        mNoDataAddCar.setOnClickListener(this);
        mNoNetBtn.setOnClickListener(this);
    }

    @Override
    public void showEmpty() {
        mTittleAddCar.setVisibility(View.GONE);
        mNOdataShow.setVisibility(View.VISIBLE);
        mNoNetShow.setVisibility(View.GONE);
        mNomalShow.setVisibility(View.GONE);
        mCarListTittle.setVisibility(View.GONE);
    }

    @Override
    public void showNomal() {
        mTittleAddCar.setVisibility(View.VISIBLE);
        mCarListTittle.setVisibility(View.VISIBLE);
        mNomalShow.setVisibility(View.VISIBLE);
        mNOdataShow.setVisibility(View.GONE);
        mNoNetShow.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showFail() {
        mTittleAddCar.setVisibility(View.GONE);
        mNOdataShow.setVisibility(View.GONE);
        mNomalShow.setVisibility(View.GONE);
        mCarListTittle.setVisibility(View.GONE);
        mNoNetShow.setVisibility(View.VISIBLE);
    }

    @Override
    public void addData(List<CarListBean> data) {
        if (mCarList.size() > 0) {
            mCarList.clear();
        }
        mCarList = data;
        myCarAdapter.setDataList(data);
        mRecyView.setAdapter(myCarAdapter);
        startHeart(data);
    }

    private void startHeart(List<CarListBean> data) {
        StringBuffer sids = new StringBuffer();
        for (int i = 0; i < data.size(); i++) {
            if (i == 0) {
                sids = sids.append(data.get(i).getsID());
            } else if (0 < i && i < data.size()) {
                sids = sids.append(data.get(i).getsID() + ",");
            } else if (i == data.size()) {
                sids = sids.append(data.get(i).getsID());
            }
        }
        initHeartbeat(String.valueOf(sids));
    }

    @Override
    public void addHeartBeatData(List<CarHeartBeatEntry> bean) {
        //此处需判断sid是否相同，然后再去处理逻辑
        boolean replease = false;
        if (mCarList.size() > 0) {
            for (int i = 0; i < mCarList.size(); i++) {
                for (int j = 0; j < bean.size(); j++) {
                    if (i == j) {
                        int deviceState = Integer.parseInt(String.valueOf(bean.get(j).getDeviceState()));
                        int soc = Integer.parseInt(String.valueOf(bean.get(j).getSoc()));
                        String location = bean.get(j).getLocation();
                        Double remailMiles = bean.get(j).getRemailMiles();
                        mCarList.get(i).setDeviceState(deviceState);
                        mCarList.get(i).setSoc(soc);
                        mCarList.get(i).setLocation(location);
                        mCarList.get(i).setRemailMiles(remailMiles);
                        if (deviceState != mCarList.get(j).getDeviceState() || soc != mCarList.get(j).getSoc() || !location.equals(mCarList.get(j).getLocation()) ||
                                remailMiles != mCarList.get(j).getRemailMiles()) {
                            replease = true;
                        }
                    }
                }
            }
            if (replease) {
                myCarAdapter.setDataList(mCarList);
            }
        }
    }

    @Override
    public void bindCarSucess() {
        Toast.makeText(this, R.string.bind_car_sucess, Toast.LENGTH_SHORT).show();
        myCarPresent.requestBatteryCar(mToken);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.no_net_try_tv:
                if (isNetworkAvaliable()) {
                    myCarPresent.requestBatteryCar(mToken);
                }
                break;
            case R.id.home_actionbar_right:
                initScan();
                break;
            case R.id.add_car_llyt:
                initScan();
                break;
            case R.id.home_actionbar_menu:
                finish();
                break;
            default:
                break;
        }
    }

    private void initScan() {
        new IntentIntegrator(this)
                .addExtra(QRCodeActivity.QR_TYPE_TARGET, QRCodeActivity.QR_TYPE_CAR)
                .setOrientationLocked(false)
                .setCaptureActivity(QRCodeActivity.class) // 设置自定义的activity是CustomActivity
                .initiateScan(); //  初始化扫描

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            String sID = null;
            if (resultCode == QRCodeActivity.MANUAL_RESULT_CODE) {
                sID = data.getStringExtra(QRCodeActivity.MANUAL_INPUT_TARGET);
            } else {
                IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (intentResult != null) {
                    sID = intentResult.getContents();
                }
            }

            if (!TextUtils.isEmpty(sID)) {
                if (sID.length() > 12) {
                    int index = sID.indexOf("sn=");
                    if (index > 0) {
                        sID = sID.substring(index + 3);
                        if (isNetworkAvaliable()) {
                            myCarPresent.requestBindCar(mToken, mZone, sID);
                        }
                    } else {
                        Toast.makeText(MyCarActivity.this, R.string.scan_address_error_try, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isNetworkAvaliable()) {
                        myCarPresent.requestBindCar(mToken, mZone, sID);
                    }
                }
            }
        }
    }

    public int getZone() {
        int zone = 8;
        if (DateTimeUtil.isInChina()) {
            zone = 8;
        } else {
            zone = getDetailZone();
        }
        return zone;
    }

    public int getDetailZone() {
        String newZoneStr="8";
        String zoneStr = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);
        int indexS = zoneStr.indexOf("GMT");
        int indexE = zoneStr.indexOf(":");
        if (indexS>0 && indexE>0) {
        newZoneStr = zoneStr.substring(indexS + 3, indexE);
        }
        int resultzone = Integer.parseInt(newZoneStr);
        return resultzone;
    }
}
