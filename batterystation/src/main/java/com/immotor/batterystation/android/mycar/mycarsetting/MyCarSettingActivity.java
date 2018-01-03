package com.immotor.batterystation.android.mycar.mycarsetting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.bluetooth.BLECommand;
import com.immotor.batterystation.android.bluetooth.BLEScanner;
import com.immotor.batterystation.android.bluetooth.DeviceAdapterService;
import com.immotor.batterystation.android.bluetooth.DeviceDataService;
import com.immotor.batterystation.android.bluetooth.DeviceInterface;
import com.immotor.batterystation.android.bluetooth.NumberBytes;
import com.immotor.batterystation.android.entity.CarListBean;
import com.immotor.batterystation.android.intentactivity.IntentActivityMethod;
import com.immotor.batterystation.android.mycar.mycarsetting.mvppresent.MyCarSettingPresent;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.util.DateTimeUtil;
import com.immotor.batterystation.android.util.LogUtil;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jm on 2017/9/18 0018.
 */

public class MyCarSettingActivity extends BaseActivity implements View.OnClickListener, DeviceInterface, BLEScanner.BLEScanListener {
    private ImageView mBackImg;
    private TextView mTittle;
    private TextView mNickName;
    private TextView mTime;
    private TextView mSerial;
    private TextView hVersion;
    private TextView fVersion;
    private CarListBean mData;
    private String mToken;
    private MyCarSettingPresent mPresent;
    private RelativeLayout mUnbind;
    private Timer mScanTimer;
    private int timeKick;

    private final int HANDLER_LBE_STOP_SCAN = 7;
    private final int HANDLER_LBE_CONNECT_CAR = 8;
    private final int HANDLER_CHECK_DEVICEID = 9;

    private final int HANDLER_CHECK_PMSID = 10;
    private Handler lbeHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case DeviceAdapterService.DeviceStateEvent.STATE_CONNECTED:
                    // onDiscoverScooter((String) msg.obj);
                    sendDeviceIDCommand();
                    Toast.makeText(MyCarSettingActivity.this, R.string.connect_car, Toast.LENGTH_SHORT).show();
                    break;
                case HANDLER_LBE_CONNECT_CAR:
                    stopBLEScan();
                    onDiscoverScooter((String) msg.obj);
                    //  Toast.makeText(MyCarSettingActivity.this, R.string.scan_get_car, Toast.LENGTH_SHORT).show();
                    break;
                case DeviceAdapterService.DeviceStateEvent.STATE_NONE:
                    Toast.makeText(MyCarSettingActivity.this, R.string.cont_connect_car, Toast.LENGTH_SHORT).show();
                    break;
                case HANDLER_LBE_STOP_SCAN:
                    //   Toast.makeText(MyCarSettingActivity.this, R.string.blue_stop_scan, Toast.LENGTH_SHORT).show();
                    break;
                case DeviceDataService.MSG_RECEIVE_DATA_FLG:
                    byte[] receivedData = (byte[]) msg.obj;
                    parseResponseData(receivedData);
                    break;
                case DeviceAdapterService.DeviceStateEvent.STATE_TIMEOUT:  //4
                    if (timeKick == 5) {
                        BLEScanner.getInstance(MyCarSettingActivity.this).stopScan();
                        Toast.makeText(MyCarSettingActivity.this, R.string.connect_car_timeout, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case HANDLER_CHECK_DEVICEID:
                    if (TextUtils.isEmpty(fwVersion) || TextUtils.isEmpty(hwVersion)) {
                        sendDeviceIDCommand();
                    }
                    break;
                case HANDLER_CHECK_PMSID:
                    if (TextUtils.isEmpty(PmsfwVersion) || TextUtils.isEmpty(PmshwVersion)) {
                        sendPmsCommand();
                    }
                    break;

                default:
                    break;
            }
        }
    };
    private String hwVersion;
    private String fwVersion;
    private String PmshwVersion;
    private String PmsfwVersion;
    private boolean pmsStatus;

    //发送smart查询信息的指令
    private void sendDeviceIDCommand() {
        if (lbeHandler.hasMessages(HANDLER_CHECK_DEVICEID)) {
            lbeHandler.removeMessages(HANDLER_CHECK_DEVICEID);
        }
        lbeHandler.sendEmptyMessageDelayed(HANDLER_CHECK_DEVICEID, 3000);
        byte[] bytes = BLECommand.getDeviceID();
        deviceDataService.sendDataToDevice(bytes, false);
    }

    //发送pms查询信息的指令
    private void sendPmsCommand() {
        if (lbeHandler.hasMessages(HANDLER_CHECK_PMSID)) {
            lbeHandler.removeMessages(HANDLER_CHECK_PMSID);
        }
        lbeHandler.sendEmptyMessageDelayed(HANDLER_CHECK_PMSID, 2000);
        byte[] bytes = BLECommand.getPms();
        deviceDataService.sendDataToDevice(bytes, false);
    }

    @Override
    public final void onStopScanListener() {
        lbeHandler.sendEmptyMessage(HANDLER_LBE_STOP_SCAN);
    }

    @Override
    public final void onDiscoverScooterListener(String macAddress) {
        Message msg = lbeHandler.obtainMessage(HANDLER_LBE_CONNECT_CAR);
        msg.obj = macAddress;
        lbeHandler.sendMessage(msg);
    }

    @Override
    public final void onDiscoverPeripheralListener() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_car_setting_activity);
        mData = (CarListBean) getIntent().getSerializableExtra(AppConstant.KEY_ENTRY_CAR_SETTING_DATA);
        mToken = mPreferences.getToken();
        mPresent = new MyCarSettingPresent(this, mToken);
        if (mData != null) {
            mPreferences.setCurrentMacAddress(mData.getMacAddress());
            setData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pmsStatus = false;
        deviceDataService.addObserver(this);
        startBLEScan();
        BLEScanner.getInstance(this).setBLEScanListener(this);

    }

    @Override
    protected void onPause() {
        if (lbeHandler.hasMessages(HANDLER_CHECK_DEVICEID)) {
            lbeHandler.removeMessages(HANDLER_CHECK_DEVICEID);
        }
        super.onPause();
        hVersion = null;
        fwVersion = null;
        PmshwVersion = null;
        PmsfwVersion = null;
        deviceDataService.delObserver(this);
    }

    public void onDiscoverScooter(String macAddress) {
        if (macAddress == null) {
            return;
        }
        String newMacaddress = macAddress.replace(":", "");
        if (deviceDataService != null) {
            if (deviceDataService.getAddress() != null && !deviceDataService.getAddress().equals(macAddress)) {
                deviceDataService.disconnect();
            }
            if (!deviceDataService.isConnected()) {
                if (mData != null && mData.getMacAddress() != null && mData.getMacAddress().equals(newMacaddress)) {
                    deviceDataService.connect(macAddress);
                }
            }
        }
    }

    private void setData() {
        mNickName.setText(mData.getNickName() + "");
        mTime.setText(DateTimeUtil.getDateTimeString("yyyy-MM-dd", mData.getProductionDate()) + "");
        if (mData.getSn() != null) {
            mSerial.setText(mData.getSn() + "");
        }
        if (mData.getHw_version() != null) {
            hVersion.setText(mData.getHw_version() + "");
        }
        if (mData.getFw_version() != null) {
            fVersion.setText(mData.getFw_version() + "");
        }
    }

    @Override
    public void initUIView() {
        mBackImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mBackImg.setOnClickListener(this);
        mBackImg.setImageDrawable(getResources().getDrawable(R.mipmap.nav_back_icon_white));
        mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.car_setting);
        mTittle.setGravity(Gravity.CENTER);

        RelativeLayout mNickNameLlyt = (RelativeLayout) findViewById(R.id.nickname_layout);
        mNickNameLlyt.setOnClickListener(this);
        mNickName = (TextView) findViewById(R.id.nickname_view);
        mTime = (TextView) findViewById(R.id.time_tag_tv);
        mSerial = (TextView) findViewById(R.id.serial_tag_tv);
        hVersion = (TextView) findViewById(R.id.h_version_tag_tv);
        fVersion = (TextView) findViewById(R.id.f_version_tag_tv);
        mUnbind = (RelativeLayout) findViewById(R.id.unbind_layout);
        mUnbind.setOnClickListener(this);
        RelativeLayout mUpdate = (RelativeLayout) findViewById(R.id.update_llyt);
        mUpdate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nickname_layout:
                Intent intent = new Intent(this, CarNameSettingActivity.class);
                if (mData != null) {
                    intent.putExtra(AppConstant.KEY_ENTRY_BATTERY_CAR_SID, mData.getsID());
                }
                startActivityForResult(intent, AppConstant.REQUEST_CAR_NAME_SETTING_CODE);
                break;
            case R.id.update_llyt:
                if (deviceDataService.isConnected() && fwVersion != null && hwVersion != null && PmsfwVersion != null && PmshwVersion != null && pmsStatus ) {
                    IntentActivityMethod.CarSettingActivitytoCarUpdata(MyCarSettingActivity.this, mData.getVersion(), fwVersion, hwVersion, PmsfwVersion, PmshwVersion, deviceDataService.getAddress(), mData.getsID());
                } else if (deviceDataService.isConnected() && (fwVersion == null || hwVersion == null || PmsfwVersion == null || PmshwVersion == null) && pmsStatus ) {
                    sendDeviceIDCommand();
                    Toast.makeText(MyCarSettingActivity.this, R.string.blue_date_return, Toast.LENGTH_SHORT).show();
                } else if (deviceDataService.isConnected()  && !pmsStatus) {
                    sendDeviceIDCommand();
                    Toast.makeText(MyCarSettingActivity.this, R.string.system_preparing, Toast.LENGTH_SHORT).show();
                } else {
                    startBLEScan();
                    BLEScanner.getInstance(this).setBLEScanListener(this);
                    Toast.makeText(MyCarSettingActivity.this, R.string.cont_get_smvn_now_scan, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.home_actionbar_menu:
                finish();
                break;
            case R.id.unbind_layout:
                showLowerDialog();
                break;

            default:
                break;

        }
    }

    private void showLowerDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage(R.string.unbind_car_dialog_msg);
        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (isNetworkAvaliable()) {
                    mPresent.requestUnBindCar(mData.getsID());
                }
            }
        });
        dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void startBLEScan() {
        if (mScanTimer == null) {
            mScanTimer = new Timer();
            timeKick = 0;
            mScanTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (timeKick == 0) {
                        BLEScanner.getInstance(MyCarSettingActivity.this).scanLeDevice(MyCarSettingActivity.this);
                    } else if (timeKick == 5) {
                        stopBLEScan();
                    }
                    timeKick++;
                    timeKick = timeKick > 6 ? 5 : timeKick;
                }
            }, 0, 1000);
        }
    }

    private void stopBLEScan() {
        if (mScanTimer != null) {
            mScanTimer.cancel();
            mScanTimer = null;
        }
        if (BLEScanner.getInstance(this).isScanning()) {
            BLEScanner.getInstance(this).stopScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstant.REQUEST_CAR_NAME_SETTING_CODE) {
            String name = "";
            if (resultCode == AppConstant.RERESULT_CAR_NAME_SETTING_CODE) {
                name = data.getStringExtra("NickName");
            }
            if (!name.isEmpty()) {
                mNickName.setText(name);
            }
        }
    }


    @Override
    public void deviceConnectLost() {

        lbeHandler.sendEmptyMessage(DeviceAdapterService.DeviceStateEvent.STATE_NONE);
    }

    @Override
    public void deviceConnected() {

        lbeHandler.sendEmptyMessage(DeviceAdapterService.DeviceStateEvent.STATE_CONNECTED);
    }

    @Override
    public void deviceTimeout() {

        lbeHandler.sendEmptyMessage(DeviceAdapterService.DeviceStateEvent.STATE_TIMEOUT);
    }

    @Override
    public void getDataFromService(byte[] dataBuf, int dataLen) {
        LogUtil.v("getDataFromService dataLen=" + dataLen);
        deviceDataService.setHasReceivedData(true);
        Message message = lbeHandler.obtainMessage(DeviceDataService.MSG_RECEIVE_DATA_FLG);
        message.obj = dataBuf;
        lbeHandler.sendMessage(message);

    }

    //解析蓝牙返回的数据
    private void parseResponseData(byte[] data) {
        if (data.length < 3) {
            return;
        }
        byte command = data[0];
        LogUtil.v("get ble data" + NumberBytes.bytesToHexString(data));
        switch (command) {
            case BLECommand.code_19:
                if (data[2] == 0x00) {
                    int hwMainVer = NumberBytes.byteToInt(data[4]);
                    int hwSubVer = NumberBytes.byteToInt(data[5]);
                    int fwMainVer = NumberBytes.byteToInt(data[6]);
                    int fwSubVer = NumberBytes.byteToInt(data[7]);
                    int fwMinorVer = NumberBytes.byteToInt(data[8]);
                    byte[] buildBytes = {data[12], data[11], data[10], data[9]};
                    int fwBuildNum = NumberBytes.bytesToInt(buildBytes);

                    hwVersion = String.valueOf(hwMainVer + "." + hwSubVer);
                    //  hwVersion = hwVersion.equals("0.1")?"1.8":hwVersion; //硬件问题，若返回0.1，则强制升到1.8
                    if (TextUtils.isEmpty(fwVersion)) {
                        fwVersion = String.valueOf(fwMainVer + "." + fwSubVer + "." + fwMinorVer + "." + (fwBuildNum));
                    }
                    if (hwVersion != null && fwVersion != null) {
                        byte[] bytes = BLECommand.getPms();
                        deviceDataService.sendDataToDevice(bytes, false);
                    }
                }
                break;

            case BLECommand.code_01:
                if (data[2] == 0x00) {
                    int hwMainVer = NumberBytes.byteToInt(data[4]);
                    int hwSubVer = NumberBytes.byteToInt(data[5]);
                    int fwMainVer = NumberBytes.byteToInt(data[6]);
                    int fwSubVer = NumberBytes.byteToInt(data[7]);
                    int fwMinorVer = NumberBytes.byteToInt(data[8]);
                    byte[] buildBytes = {data[12], data[11], data[10], data[9]};
                    int fwBuildNum = NumberBytes.bytesToInt(buildBytes);
                    String dataInfoStr = com.immotor.batterystation.android.util.NumberBytes.byteToBit(data[13]);
                    boolean a = (dataInfoStr.charAt(6) == '1');
                    boolean b = (dataInfoStr.charAt(7) == '1');
                    if (a && b) {
                        pmsStatus = true;
                    }
                    PmshwVersion = String.valueOf(hwMainVer + "." + hwSubVer);
                    //  hwVersion = hwVersion.equals("0.1")?"1.8":hwVersion; //硬件问题，若返回0.1，则强制升到1.8
                    if (TextUtils.isEmpty(PmsfwVersion)) {
                        PmsfwVersion = String.valueOf(fwMainVer + "." + fwSubVer + "." + fwMinorVer + "." + (fwBuildNum));
                    }
                }
                break;

            default:
                break;
        }
    }
}
