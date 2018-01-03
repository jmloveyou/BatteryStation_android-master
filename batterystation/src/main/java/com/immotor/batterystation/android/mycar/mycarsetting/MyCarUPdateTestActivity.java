package com.immotor.batterystation.android.mycar.mycarsetting;

import android.app.DownloadManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.immotor.batterystation.android.MyConfiguration;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.bluetooth.BLECommand;
import com.immotor.batterystation.android.bluetooth.DeviceAdapterService;
import com.immotor.batterystation.android.bluetooth.DeviceDataService;
import com.immotor.batterystation.android.bluetooth.DeviceInterface;
import com.immotor.batterystation.android.bluetooth.NumberBytes;
import com.immotor.batterystation.android.entity.FWMessageEntry;
import com.immotor.batterystation.android.entity.UpdateEntry;
import com.immotor.batterystation.android.http.carhttp.CarHttpFailMessage;
import com.immotor.batterystation.android.http.carhttp.CarHttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.ui.views.CommonDialog;
import com.immotor.batterystation.android.util.FileUtil;
import com.immotor.batterystation.android.util.LogUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jm on 2017/9/22 0022.
 */

public class MyCarUPdateTestActivity extends BaseActivity implements View.OnClickListener, DeviceInterface {

    private ImageView mBackImg;
    private TextView mTittle;
    private String mToken;
    private boolean isRequesting = false;
    private FWMessageEntry mFWData;
    private DownloadManager downloadManager;

    private static final int HANDLER_CHECK_DEVICEID = 1001;
    private int status;
    private MyHandler myHandler;
    private final int FLAG_STATUS_NORMAL = 0;
    private final int FLAG_STATUS_DOWNLOADING = 1;
    private final int FLAG_STATUS_FINISH = 2;

    private boolean hasStartRSP = false;
    private int retryCount;
    private final int TOTAL_RETRY_COUNT = 31;
    private int sendDataPosition = 0;
    private int totalCount;     // 文件发送多少次
    private boolean isUpgrading = false;
    private Animation rotateAnimation;
    private LinearLayout checkingLayout;
    private Button retryButton;
    private ProgressBar checkingProgress;
    private LinearLayout upgradeView;
    private ProgressBar loadingProgress;
    private TextView versionDesc;
    private TextView btnDownloadUpdate;
    private TextView checkingDesc;
    private TextView originalVersion;
    private TextView statusText;
    private ImageView logoIcon;
    private TextView latestVersion;
    private String hwVersion;
    private String fwVersion;
    private String mVersion;

    private String macAddress;
    private boolean IS_UPDATE_LOG = true;
    private byte[] mBinLength = new byte[4];
    private byte[] mBeginCrc;
    private String upgradePmsFilePath;
    private String upgradeSmartFilePath;
    private boolean PmsDownLoadFinish = true;
    private boolean SmartDownLoadFinish = true;
    private boolean PmsSendStatusScuess = false;
    private String sid;
    private byte[] updateStartByte;
    private static final int CRC_CHECK_CODE = 0xa55aa55a;
    private static int[] CRC_TABLE = new int[256];

    private int[] crc_table = new int[256];
    private String pmsfwVersion;
    private String pmshwVersion;
    private boolean SmartFileSended=false;
    private String lastpmshwVersion;
    private String lastpmsfwVersion;
    private TextView updateFinish;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (isStop()) {
                return;
            }
            switch (msg.what) {
                case DeviceAdapterService.DeviceStateEvent.STATE_CONNECTED: //3
                    //  sendPMSCommand();
                    LogUtil.v("ble connected");
                    break;
                case DeviceAdapterService.DeviceStateEvent.STATE_NONE:  //0
                    if (!isUpgrading) {
                        Toast.makeText(MyCarUPdateTestActivity.this, R.string.blue_not_connect, Toast.LENGTH_SHORT).show();
                    }
                    mBackImg.setEnabled(true);
                    break;
                case DeviceAdapterService.DeviceStateEvent.STATE_TIMEOUT:  //4
                    LogUtil.v("timeout----------------------");
                    Toast.makeText(MyCarUPdateTestActivity.this, R.string.blue_connect_timeout, Toast.LENGTH_SHORT).show();
                    if (isUpgrading) {
                        upgradeFailed();
                    }
                    isUpgrading = false;
                    btnDownloadUpdate.setEnabled(true);
                    loadingProgress.setVisibility(View.GONE);
                    statusText.setVisibility(View.GONE);
                    logoIcon.clearAnimation();
                    break;
                case DeviceDataService.MSG_RECEIVE_DATA_FLG:
                    byte[] receivedData = (byte[]) msg.obj;
                    parseResponseData(receivedData);
                    break;
                case HANDLER_CHECK_DEVICEID:
                    if (TextUtils.isEmpty(fwVersion) || TextUtils.isEmpty(hwVersion)) {
                        sendDeviceIDCommand();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // 开始链接蓝牙设备
    private void startConnectDevice() {
        if (macAddress != null) {
            deviceDataService.connect(macAddress);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_update_activity);

        mVersion = getIntent().getStringExtra(AppConstant.KEY_ENTRY_CAR_UPDATA_VERSION);
        fwVersion = getIntent().getStringExtra(AppConstant.KEY_ENTRY_CAR_UPDATA_FWVERSION);
        hwVersion = getIntent().getStringExtra(AppConstant.KEY_ENTRY_CAR_UPDATA_HWVERSION);
        pmsfwVersion = getIntent().getStringExtra(AppConstant.KEY_ENTRY_CAR_UPDATA_PMSFWVERSION);
        pmshwVersion = getIntent().getStringExtra(AppConstant.KEY_ENTRY_CAR_UPDATA_PMSHWVERSION);

        macAddress = getIntent().getStringExtra("macaddress");
        sid = getIntent().getStringExtra(AppConstant.KEY_ENTRY_BATTERY_CAR_SID);
        mToken = mPreferences.getToken();
        myHandler = new MyHandler();
        displayUpgradeUI();
     //   httpPms();
    }

    @Override
    public void initUIView() {
        mBackImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mBackImg.setOnClickListener(this);
        mBackImg.setImageDrawable(getResources().getDrawable(R.mipmap.nav_back_icon_white));
        mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.firmware_update);
        mTittle.setGravity(Gravity.CENTER);

        checkingLayout = (LinearLayout) findViewById(R.id.checking_layout);
        retryButton = (Button) findViewById(R.id.retry);
        retryButton.setOnClickListener(this);
        rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_h_rotate);
        checkingProgress = (ProgressBar) findViewById(R.id.checking_progress);
        upgradeView = (LinearLayout) findViewById(R.id.upgrade_view);
        loadingProgress = (ProgressBar) findViewById(R.id.loading_progress);
        versionDesc = (TextView) findViewById(R.id.version_desc);
        btnDownloadUpdate = (TextView) findViewById(R.id.btn_download_update);
        btnDownloadUpdate.setText("升级");
        btnDownloadUpdate.setOnClickListener(this);
        checkingDesc = (TextView) findViewById(R.id.checking_desc);
        originalVersion = (TextView) findViewById(R.id.original_version);
        statusText = (TextView) findViewById(R.id.status_text);
        logoIcon = (ImageView) findViewById(R.id.logo_icon);
        latestVersion = (TextView) findViewById(R.id.latest_version_text);
        updateFinish = (TextView) findViewById(R.id.update_finish_tv);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        deviceDataService.addObserver(this);
        if (isUpgrading) {
            deviceTimeout();
        } else {
          //  checkingLayout.setVisibility(View.VISIBLE);
          //  upgradeView.setVisibility(View.GONE);
            if (deviceDataService.isConnected()) {
                if (TextUtils.isEmpty(hwVersion) || TextUtils.isEmpty(fwVersion)) {
                    sendDeviceIDCommand();
                }
            } else {
                checkingProgress.setVisibility(View.GONE);
                checkingDesc.setText(R.string.open_blue_to_connect);
            }
        }
    }

    @Override
    protected void onPause() {
        if (myHandler.hasMessages(HANDLER_CHECK_DEVICEID)) {
            myHandler.removeMessages(HANDLER_CHECK_DEVICEID);
        }
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        deviceDataService.delObserver(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_actionbar_menu:
                finish();
                break;
            case R.id.retry:
                checkingLayout.setVisibility(View.VISIBLE);
                retryButton.setVisibility(View.GONE);
                //   httpGetScooterVersion();
                httpPms();
                break;
            case R.id.btn_download_update:
                    if (deviceDataService.isConnected()) {
                        DeviceAdapterService.getInstance(MyCarUPdateTestActivity.this).setIsUpgrade(true);
                        loadingProgress.setVisibility(View.VISIBLE);
                        loadingProgress.setProgress(0);
                        statusText.setText(String.valueOf(0) + "%");
                        isUpgrading = true;
                        statusText.setVisibility(View.VISIBLE);
                        btnDownloadUpdate.setEnabled(false);
                        logoIcon.startAnimation(rotateAnimation);
                        //statusText.setText(R.string.start_upgrade);
                        // initFileData();
                        if (!upgradePmsFilePath.isEmpty()) {
                            byte[] version = getVersionInFile(upgradePmsFilePath);
                            if (version != null) {
                                startUpgrade(version);
                            } else {
                                Toast.makeText(this, "未获取到版本号", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            byte[] version = getVersionInFile(upgradeSmartFilePath);
                            if (version != null) {
                                Toast.makeText(this, "升级smart", Toast.LENGTH_SHORT).show();
                                startUpgrade(version);
                            } else {
                                Toast.makeText(this, "未获取到版本号", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(MyCarUPdateTestActivity.this, R.string.blue_not_connect, Toast.LENGTH_SHORT).show();
                    }
                break;
            default:
                break;
        }
    }

    //发送查询信息的指令
    private void sendDeviceIDCommand() {
        if (myHandler.hasMessages(HANDLER_CHECK_DEVICEID)) {
            myHandler.removeMessages(HANDLER_CHECK_DEVICEID);
        }
        myHandler.sendEmptyMessageDelayed(HANDLER_CHECK_DEVICEID, 5000);
        byte[] bytes = BLECommand.getDeviceID();
        deviceDataService.sendDataToDevice(bytes, false);
    }
   /* //发送信息的指令
    private void sendPMSCommand() {
        if (myHandler.hasMessages(HANDLER_CHECK_DEVICEID)) {
            myHandler.removeMessages(HANDLER_CHECK_DEVICEID);
        }
        //    myHandler.sendEmptyMessageDelayed(HANDLER_CHECK_DEVICEID, 3000);
        byte[] bytes = BLECommand.getPMS();
        deviceDataService.sendDataToDevice(bytes, false);
    }

      byte[] bytes = BLECommand.getPms();
                    deviceDataService.sendDataToDevice(bytes, false);
    */

    private void getPmsInfo() {
        byte[] bytes = BLECommand.getPms();
        deviceDataService.sendDataToDevice(bytes, false);
    }

    private void startUpgrade(byte[] version) {
        //upgradeFilePath
        hasStartRSP = false;
        retryCount = 0;
        deviceDataService.setRequestWaitForLongTime(true);
        if (PmsDownLoadFinish) {
            if (PmsSendStatusScuess) {
                if (SmartDownLoadFinish) {
                    updateStartByte = BLECommand.firmwareUpdateStart(mBinLength, version);
                }
            } else {
                updateStartByte = BLECommand.pmsUpdateStart(mBinLength, version);
            }
        } else {
            if (SmartDownLoadFinish ) {
                updateStartByte = BLECommand.firmwareUpdateStart(mBinLength, version);
            }
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!hasStartRSP && isUpgrading) {
                    retryCount++;
                    if (retryCount > TOTAL_RETRY_COUNT) {
                        return;
                    }
                    deviceDataService.sendDataToDevice(updateStartByte, false);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void initCRCTable() {
        int c;
        int i, j;
        for (i = 0; i < 256; i++) {
            c = i;
            for (j = 0; j < 8; j++) {
                if ((c & 1) != 0)
                    c = (int) (0xedb88320L ^ (c >>> 1));
                else
                    c = c >>> 1;
            }
            CRC_TABLE[i] = c;
        }
    }

    private synchronized byte[] getVersionInFile(String fullName) {
        if (IS_UPDATE_LOG) {
            LogUtil.d("get version in file " + fullName);
        }
        if (TextUtils.isEmpty(fullName)) {
            return null;
        }
        File updateFile = new File(fullName);
        if (updateFile.exists()) {
            try {
                RandomAccessFile rf = new RandomAccessFile(updateFile, "r");
                long fLength = rf.length();
                if (IS_UPDATE_LOG) {
                    LogUtil.d("file length = " + fLength);
                }
                if (fLength < 128 * 2 || fLength % 128 != 0) {
                    rf.close();
                    if (!MyConfiguration.isForTest) {
                        updateFile.delete();
                    }
                    return null;
                }
                byte[] data = new byte[128];
                rf.seek(fLength - 128);
                rf.read(data, 0, 128);
                byte[] intData = new byte[4];
                System.arraycopy(data, 128 - 4, intData, 0, 4);
                int crcInfo = NumberBytes.bytesToIntDi(intData);  //文件中的crc
                initCRCTable();

                int crcCalc = CRC_CHECK_CODE;  //计算的crc
                for (int i = 0; i < 124; i++) {
                    crcCalc = CRC_TABLE[(crcCalc ^ data[i]) & 0xff] ^ (crcCalc >>> 8);
                }

                if (crcCalc != crcInfo) {
                    if (IS_UPDATE_LOG) {
                        LogUtil.d("update file " + fullName + " 文件crc校验不通过, 读取为" + crcInfo + ", 计算为" + crcCalc);
                    }
                    rf.close();
                    if (!MyConfiguration.isForTest) {
                        updateFile.delete();
                    }
                    return null;
                }

                mBeginCrc = new byte[4];
                System.arraycopy(data, 0, mBeginCrc, 0, 4);
                System.arraycopy(data, 4, intData, 0, 4);
                mBinLength = intData;
                int binLength = NumberBytes.bytesToIntDi(intData);
                if (binLength <= fLength - 128 * 2 || binLength > fLength - 128) {
                    if (IS_UPDATE_LOG) {
                        LogUtil.d("update file " + fullName + " 文件data长度错误 实际文件大小：" + fLength + ", 读取数据大小：" + binLength);
                    }
                    rf.close();
                    if (!MyConfiguration.isForTest) {
                        updateFile.delete();
                    }
                    return null;
                }
              /*  int verM = NumberBytes.byteToInt(data[8]);
                int verS1 = NumberBytes.byteToInt(data[9]);
                int verS2 = NumberBytes.byteToInt(data[10]);

                System.arraycopy(data,11,intData,0,4);
                int verSVN = NumberBytes.bytesToInt(intData);
                int hwM = NumberBytes.byteToInt(data[15]);
                int hwS = NumberBytes.byteToInt(data[16]);
*/
                byte[] rlt = new byte[7];
                rlt[0] = data[8];
                rlt[1] = data[9];
                rlt[2] = data[10];
                rlt[3] = data[11];
                rlt[4] = data[12];
                rlt[5] = data[13];
                rlt[6] = data[14];

                if (IS_UPDATE_LOG) {
                    LogUtil.d("file " + fullName + ", hw=" + rlt[0] + "." + rlt[1] + ", fw=" + rlt[2] + "." +
                            rlt[3] + "." + rlt[4] + "." + rlt[5]);
                }
                return rlt;

            } catch (Exception e) {
                if (IS_UPDATE_LOG) {
                    LogUtil.d("update get file " + fullName + " version error:" + e.getMessage());
                }
            }
        } else {
            if (IS_UPDATE_LOG) {
                LogUtil.d("file not exist");
            }
        }
        return null;

    }

    private void httpPms() {
        if (!isNetworkAvaliable()) {
            return;
        }

        if (isRequesting) {
            return;
        }
        isRequesting = true;
        Map<String, Object> map = new HashMap<>();
        map.put("token", mToken);
        map.put("version", mVersion);
        map.put("smart_fw", fwVersion);
        map.put("smart_hw", hwVersion);
        map.put("pms_fw", pmsfwVersion);
        map.put("pms_hw", pmshwVersion);

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<FWMessageEntry>() {
            @Override
            public void onError(Throwable e) {
                CarHttpFailMessage.carfailMessageShow(MyCarUPdateTestActivity.this, null, e);
                isRequesting = false;
            }

            @Override
            public void onNext(FWMessageEntry data) {
                if (data != null) {
                    if (data.getStatus() == 0) {
                        //显示已经是最新版本UI
                        displayNormalUI();
                    } else {
                        //status==1,显示下载按钮，隐藏最新版本按钮
                        mFWData = data;
                        if (!data.getList().get(0).getVersion().equals(fwVersion) || data.getStatus() == 1) {   // 需要升级
                            displayUpgradeUI();
                        }
                    }
                }
                isRequesting = false;
            }
        };
        CarHttpMethods.getInstance().getfwMessage(new ProgressSubscriber(subscriberOnNextListener, this, null), map);
    }

    @Override
    public void deviceConnectLost() {
        myHandler.sendEmptyMessage(DeviceAdapterService.DeviceStateEvent.STATE_NONE);
    }

    @Override
    public void deviceConnected() {
        myHandler.sendEmptyMessage(DeviceAdapterService.DeviceStateEvent.STATE_CONNECTED);
    }

    @Override
    public void deviceTimeout() {
        myHandler.sendEmptyMessage(DeviceAdapterService.DeviceStateEvent.STATE_TIMEOUT);
    }

    @Override
    public void getDataFromService(byte[] dataBuf, int dataLen) {
        LogUtil.v("getDataFromService dataLen=" + dataLen);
        deviceDataService.setHasReceivedData(true);

        Message message = myHandler.obtainMessage(DeviceDataService.MSG_RECEIVE_DATA_FLG);
        message.obj = dataBuf;
        myHandler.sendMessage(message);

    }

    private void displayUpgradeUI() {
        checkingLayout.setVisibility(View.GONE);
        upgradeView.setVisibility(View.VISIBLE);

        latestVersion.setVisibility(View.VISIBLE);
        btnDownloadUpdate.setVisibility(View.VISIBLE);
        latestVersion.setText(/*"v" + mFWData.getList().get(0).getVersion()*/"有可更新版本");
        getFilePath();
    //    versionDesc.setText(mFWData.getList().get(0).getContent());
        //之后在加版本号识别文件名
       /* if (isFileExist()) {
            btnDownloadUpdate.setText("固件更新");
            status = FLAG_STATUS_FINISH;
        }*/
    }

    private void getFilePath() {
        //0 smart ,1 pms
        upgradePmsFilePath = getUpgradePmsFilePath();
        upgradeSmartFilePath = getUpgradeSmartFilePath();
        /*for (int i = 0; i < mFWData.getList().size(); i++) {
            if (mFWData.getList().get(i).getType() == 1) {
                upgradePmsFilePath = getUpgradePmsFilePath(i);
            } else if (mFWData.getList().get(i).getType() == 0) {
                upgradeSmartFilePath = getUpgradeSmartFilePath(i);
            }
        }*/
    }

    private void displayNormalUI() {
        checkingLayout.setVisibility(View.GONE);
        upgradeView.setVisibility(View.VISIBLE);
        btnDownloadUpdate.setVisibility(View.GONE);
        String format = getString(R.string.tos_version);
        originalVersion.setText(String.format(format, fwVersion));

        latestVersion.setText(getString(R.string.current_fw_version_is_latest));
        loadingProgress.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);
        logoIcon.clearAnimation();
    }

    private void displayNewUI(String fwVersion) {
        checkingLayout.setVisibility(View.GONE);
        //   upgradeView.setVisibility(View.VISIBLE);
        upgradeView.setVisibility(View.GONE);
        btnDownloadUpdate.setVisibility(View.GONE);
        //   String format = getString(R.string.tos_version);
//        if (fwVersion != null) {
//            originalVersion.setText(String.format(format, fwVersion));
//        } else {
//            originalVersion.setText(getString(R.string.current_fw_version_is_latest));
//        }

        //   versionDesc.setText(getString(R.string.current_fw_version_is_latest));
        originalVersion.setVisibility(View.GONE);
        versionDesc.setVisibility(View.GONE);
        loadingProgress.setVisibility(View.GONE);
        statusText.setVisibility(View.GONE);
        logoIcon.clearAnimation();

        updateFinish.setVisibility(View.VISIBLE);
    }


    private String getUpgradePmsFilePath() {
        String path = MyConfiguration.UPDATE_PMS_PATH;
        File savePathFile = new File(path);
        if (!savePathFile.exists()) {
            savePathFile.mkdirs();
        }
        String savePath = savePathFile.getAbsolutePath() + File.separator + FileUtil.getServerFileName("pms.bin");
        return savePath;
    }

    private String getUpgradeSmartFilePath() {
        String path = MyConfiguration.UPDATE_SMART_PATH;
        File savePathFile = new File(path);
        if (!savePathFile.exists()) {
            savePathFile.mkdirs();
        }
        String savePath = savePathFile.getAbsolutePath() + File.separator + FileUtil.getServerFileName("smart.bin");
        return savePath;
    }

    /*private boolean isFileExist() {
        File file = new File();
        return file.exists();
    }*/

    private void downloadSmartwareFile(final int i) {
        latestVersion.setText("smart");
        btnDownloadUpdate.setEnabled(false);
        FileDownloader.getImpl().create(mFWData.getList().get(i).getUrl())
                .setPath(upgradeSmartFilePath)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtil.v("totalBytes is:" + totalBytes);
                        int progress = (soFarBytes * 100) / totalBytes;
                        statusText.setText(String.valueOf(progress) + "%");
                        loadingProgress.setProgress(progress);
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        //      Toast.makeText(MyCarUPdateActivity.this, "Smart" + getString(R.string.download_sucess), Toast.LENGTH_SHORT).show();
                        if (i == 0) {
                            if (mFWData.getList().size() == 2) {
                                RandomAccessFile rf = null;
                                long len = 0;
                                try {
                                    rf = new RandomAccessFile(upgradeSmartFilePath, "r");
                                    len = rf.length();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                if (len > 512) {
                                    SmartDownLoadFinish = true;
                                    if (PmsDownLoadFinish) {
                                        btnDownloadUpdate.setText(getString(R.string.firmware_update));
                                        loadingProgress.setProgress(100);
                                        statusText.setText("下载完成");
                                        loadingProgress.invalidate();
                                        btnDownloadUpdate.setEnabled(true);
                                        status = FLAG_STATUS_FINISH; // finish
                                    } else {
                                        downloadPmswareFile(1);
                                    }
                                } else {
                                    btnDownloadUpdate.setText(R.string.retry_down);
                                    btnDownloadUpdate.setEnabled(true);
                                    statusText.setText(String.valueOf(0) + "%");
                                    loadingProgress.setProgress(0);
                                    Toast.makeText(MyCarUPdateTestActivity.this, R.string.download_fail, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                SmartDownLoadFinish = true;
                                btnDownloadUpdate.setEnabled(true);
                                btnDownloadUpdate.setText(getString(R.string.firmware_update));
                                loadingProgress.setProgress(100);
                                statusText.setText("下载完成");
                                loadingProgress.invalidate();
                                status = FLAG_STATUS_FINISH; // finish
                            }
                        } else if (i == 1) {
                            RandomAccessFile rf = null;
                            long len = 0;
                            try {
                                rf = new RandomAccessFile(upgradeSmartFilePath, "r");
                                len = rf.length();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (len > 512) {
                                SmartDownLoadFinish = true;
                                btnDownloadUpdate.setEnabled(true);
                                btnDownloadUpdate.setText(getString(R.string.firmware_update));
                                loadingProgress.setProgress(100);
                                statusText.setText("下载完成");
                                loadingProgress.invalidate();
                                status = FLAG_STATUS_FINISH; // finish
                            } else {
                                btnDownloadUpdate.setEnabled(true);
                                btnDownloadUpdate.setText(R.string.retry_down);
                                statusText.setText(String.valueOf(0) + "%");
                                loadingProgress.setProgress(0);
                                Toast.makeText(MyCarUPdateTestActivity.this, R.string.download_fail, Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        LogUtil.v("download error.");
                        Toast.makeText(MyCarUPdateTestActivity.this, R.string.download_fail, Toast.LENGTH_SHORT).show();
                        btnDownloadUpdate.setText(R.string.retry_down);
                        btnDownloadUpdate.setEnabled(true);
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                }).start();
    }

    private void downloadPmswareFile(final int i) {
        latestVersion.setText("pms");
        btnDownloadUpdate.setEnabled(false);
        FileDownloader.getImpl().create(mFWData.getList().get(i).getUrl())
                .setPath(upgradePmsFilePath)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtil.v("totalBytes is:" + totalBytes);
                        int progress = (soFarBytes * 100) / totalBytes;
                        statusText.setText(String.valueOf(progress) + "%");
                        loadingProgress.setProgress(progress);
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        LogUtil.v("completed download");
                        //   Toast.makeText(MyCarUPdateActivity.this, "Pms" + getString(R.string.download_sucess), Toast.LENGTH_SHORT).show();
                        //statusText.setText(R.string.download_success);

                        if (i == 0) {
                            if (mFWData.getList().size() == 2) {
                                RandomAccessFile rf = null;
                                long len = 0;
                                try {
                                    rf = new RandomAccessFile(upgradePmsFilePath, "r");
                                    len = rf.length();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (len > 512) {
                                    PmsDownLoadFinish = true;
                                    if (SmartDownLoadFinish) {
                                        loadingProgress.setProgress(100);
                                        statusText.setText("下载完成");
                                        loadingProgress.invalidate();
                                        btnDownloadUpdate.setText(getString(R.string.firmware_update));
                                        btnDownloadUpdate.setEnabled(true);
                                        status = FLAG_STATUS_FINISH; // finish
                                    } else {
                                        downloadSmartwareFile(1);
                                    }

                                } else {
                                    btnDownloadUpdate.setText(getString(R.string.retry_down));
                                    btnDownloadUpdate.setEnabled(true);
                                    statusText.setText(String.valueOf(0) + "%");
                                    loadingProgress.setProgress(0);
                                }
                            } else {
                                PmsDownLoadFinish = true;
                                loadingProgress.setProgress(100);
                                statusText.setText("下载完成");
                                loadingProgress.invalidate();
                                btnDownloadUpdate.setText(getString(R.string.firmware_update));
                                btnDownloadUpdate.setEnabled(true);
                                status = FLAG_STATUS_FINISH; // finish
                            }
                        } else if (i == 1) {
                            RandomAccessFile rf = null;
                            long len = 0;
                            try {
                                rf = new RandomAccessFile(upgradePmsFilePath, "r");
                                len = rf.length();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (len > 512) {
                                PmsDownLoadFinish = true;
                                btnDownloadUpdate.setText(getString(R.string.firmware_update));
                                btnDownloadUpdate.setEnabled(true);
                                loadingProgress.setProgress(100);
                                statusText.setText("下载完成");
                                loadingProgress.invalidate();
                                status = FLAG_STATUS_FINISH; // finish
                            } else {
                                btnDownloadUpdate.setText(getString(R.string.retry_down));
                                btnDownloadUpdate.setEnabled(true);
                                statusText.setText(String.valueOf(0) + "%");
                                loadingProgress.setProgress(0);
                            }

                        }
                      /*
                        btnDownloadUpdate.setText("固件更新");
                        *//*loadingProgress.setProgress(100);
                        loadingProgress.invalidate();*//*
                        status = FLAG_STATUS_FINISH; // finish*/
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        LogUtil.v("download error.");
                        Toast.makeText(MyCarUPdateTestActivity.this, R.string.download_fail, Toast.LENGTH_SHORT).show();
                        btnDownloadUpdate.setText(R.string.retry_down);
                        btnDownloadUpdate.setEnabled(true);
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                }).start();
    }

    private boolean isStartRSP(byte[] bytes) {
        if (bytes[0] == (byte) 0x7E &&
                bytes[1] == (byte) 0x20 &&
                bytes[2] == (byte) 0x01 &&
                bytes[3] == (byte) 0x00 &&
                bytes[4] == (byte) 0xFF) {
            return true;
        }
        return false;
    }

    private boolean isUpdatingRSP(byte[] bytes) {
        if (bytes[0] == (byte) 0x7E &&
                bytes[1] == (byte) 0x21 &&
                bytes[2] == (byte) 0x01 &&
                bytes[3] == (byte) 0x00 &&
                bytes[4] == (byte) 0xFF) {
            return true;
        }
        return false;
    }

    private boolean isUpdateDoneRSP(byte[] bytes) {
        if (bytes[0] == (byte) 0x7E &&
                bytes[1] == (byte) 0x22 &&
                bytes[2] == (byte) 0x01 &&
                bytes[3] == (byte) 0x00 &&
                bytes[4] == (byte) 0xFF) {
            return true;
        }
        return false;
    }

    private boolean isUpdateCrcFailRSP(byte[] bytes) {
        if (bytes[0] == (byte) 0x7E &&
                bytes[1] == (byte) 0x22 &&
                bytes[2] == (byte) 0x01 &&
                bytes[3] == (byte) 0x03 &&
                bytes[4] == (byte) 0xFF) {
            return true;
        }
        return false;
    }

    /**
     * 是否发送完数据
     *
     * @param pos offset
     * @return true
     */
    private boolean sendBinDataUpdating(int pos) {
        try {
            String updateFilePath;
            if (PmsDownLoadFinish && !PmsSendStatusScuess) {
                updateFilePath = upgradePmsFilePath;
                latestVersion.setText("pms");
            } else {
                updateFilePath = upgradeSmartFilePath;
                latestVersion.setText("smart");
            }
            RandomAccessFile rf = new RandomAccessFile(updateFilePath, "r");
            long len = rf.length();
            int readBuffLen = 128;  // 每次要读取的字节数量
            totalCount = (int) (len) / 128;
            if (pos == totalCount) { // 如果读取文件最后一组数据
                readBuffLen = (int) (len) % 128;  //刚好
                if (readBuffLen == 0) {  //
                    return true;
                }
            } else if (pos > totalCount) {
                return true;
            }

            byte[] buff = new byte[readBuffLen];
            LogUtil.v("readBuffLen========= : " + readBuffLen + " pos=" + pos + " total:" + totalCount);
            rf.seek(128 * pos);
            rf.read(buff);
            rf.close();
            byte[] offset = NumberBytes.intToBytes(pos * 128);
            byte[] updateStartByte = BLECommand.firmwareUpdating(buff, offset);
            int maxLength = 20;
            int num = updateStartByte.length / maxLength;
            int result = -1;
            for (int i = 0; i < num; i++) {
                byte[] data20 = new byte[maxLength];
                System.arraycopy(updateStartByte, i * maxLength, data20, 0, maxLength);
                do {
                    result = deviceDataService.sendDataToDevice(data20, false);
                } while (result == -2 || result == -3);

            }
            if ((updateStartByte.length % maxLength) != 0) {
                byte[] dataTail = new byte[updateStartByte.length % maxLength];
                System.arraycopy(updateStartByte, num * maxLength, dataTail, 0, dataTail.length);
                do {
                    result = deviceDataService.sendDataToDevice(dataTail, false);
                } while (result == -2 || result == -3);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 发送完成指令
     */
    private void sendUpdateDone() {
        byte[] updateDoneBytes = BLECommand.firmwareUpdateDone(mBeginCrc);
        deviceDataService.sendDataToDevice(updateDoneBytes, false);
    }

    private void upgradeFailed() {
        isUpgrading = false;
        mBackImg.setEnabled(true);
        DeviceAdapterService.getInstance(MyCarUPdateTestActivity.this).setIsUpgrade(false);
        CommonDialog.createAlertDialog(this, null, getString(R.string.update_fail), false).show();
        deviceDataService.setRequestWaitForLongTime(false);
        loadingProgress.setProgress(0);
        statusText.setText(String.valueOf(0) + "%");
    }

    private void upgradeSuccess() {
        isUpgrading = false;
        mBackImg.setEnabled(true);
        String newFwVersion = "";
        DeviceAdapterService.getInstance(MyCarUPdateTestActivity.this).setIsUpgrade(false);
        deviceDataService.setRequestWaitForLongTime(false);
        deviceDataService.disconnect();
        btnDownloadUpdate.setEnabled(true);


        Toast.makeText(MyCarUPdateTestActivity.this, R.string.update_sucess, Toast.LENGTH_SHORT).show();

       /* if (SmartDownLoadFinish) {
            for (int i = 0; i < mFWData.getList().size(); i++) {
                if (mFWData.getList().get(i).getType() == 0) {
                    requestUpdata(mFWData.getList().get(i).getVersion(), mFWData.getList().get(i).getHw_version());
                    newFwVersion = mFWData.getList().get(i).getVersion();
                }
            }
        }*/
        displayNewUI(newFwVersion);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void requestUpdata(String fw_version, String hw_version) {

        if (isRequesting) {
            return;
        }
        isRequesting = true;
        Map<String, Object> map = new HashMap<>();
        map.put("token", mToken);
        map.put("sID", sid);
        map.put("fw_version", fw_version);
        map.put("hw_version", hw_version);

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<UpdateEntry>() {
            @Override
            public void onError(Throwable e) {
                CarHttpFailMessage.carfailMessageShow(MyCarUPdateTestActivity.this, null, e);
                isRequesting = false;
            }

            @Override
            public void onNext(UpdateEntry data) {
                isRequesting = false;
            }
        };
        CarHttpMethods.getInstance().getUpdate(new ProgressSubscriber(subscriberOnNextListener, this), map);
    }

    @Override
    public void onBackPressed() {
        if (isUpgrading) {
            return;
        } else {
            super.onBackPressed();
        }
    }

    public static String printHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            result = result + hex.toUpperCase() + " ";
        }
        return result;

    }

    //解析蓝牙返回的数据
    private void parseResponseData(byte[] data) {
        byte command = data[0];
        LogUtil.v("get ble data" + NumberBytes.bytesToHexString(data));
        switch (command) {
            case BLECommand.code_19:
                if (data[2] == 0x00) {
                    if (myHandler.hasMessages(HANDLER_CHECK_DEVICEID)) {
                        myHandler.removeMessages(HANDLER_CHECK_DEVICEID);
                    }
                    int hwMainVer = NumberBytes.byteToInt(data[5]);
                    int hwSubVer = NumberBytes.byteToInt(data[6]);

                    int fwMainVer = NumberBytes.byteToInt(data[7]);
                    int fwSubVer = NumberBytes.byteToInt(data[8]);
                    int fwMinorVer = NumberBytes.byteToInt(data[9]);
                    byte[] buildBytes = {data[13], data[12], data[11], data[10]};
                    int fwBuildNum = NumberBytes.bytesToInt(buildBytes);

                    hwVersion = String.valueOf(hwMainVer + "." + hwSubVer);
                    //   hwVersion = hwVersion.equals("0.1") ? "1.8" : hwVersion; //硬件问题，若返回0.1，则强制升到1.8
                    if (TextUtils.isEmpty(fwVersion)) {
                        fwVersion = String.valueOf(fwMainVer + "." + fwSubVer + "." + fwMinorVer + "." + (fwBuildNum));
                    }
                    httpPms();
                }
                break;
            case BLECommand.code_01:
                if (data[2] == 0x00) {
                    if (myHandler.hasMessages(HANDLER_CHECK_DEVICEID)) {
                        myHandler.removeMessages(HANDLER_CHECK_DEVICEID);
                    }
                    int hwMainVer = NumberBytes.byteToInt(data[5]);
                    int hwSubVer = NumberBytes.byteToInt(data[6]);

                    int fwMainVer = NumberBytes.byteToInt(data[7]);
                    int fwSubVer = NumberBytes.byteToInt(data[8]);
                    int fwMinorVer = NumberBytes.byteToInt(data[9]);
                    byte[] buildBytes = {data[13], data[12], data[11], data[10]};
                    int fwBuildNum = NumberBytes.bytesToInt(buildBytes);
                    lastpmshwVersion = String.valueOf(hwMainVer + "." + hwSubVer);
                    //   hwVersion = hwVersion.equals("0.1") ? "1.8" : hwVersion; //硬件问题，若返回0.1，则强制升到1.8
                    if (TextUtils.isEmpty(lastpmsfwVersion)) {
                        lastpmsfwVersion = String.valueOf(fwMainVer + "." + fwSubVer + "." + fwMinorVer + "." + (fwBuildNum));
                    }
                    httpPms();
                }
                break;
            default:
                //已经超时过了，不处理
                if (!isUpgrading) {
                    //  if (!isUpdateResetRSP(data)) {
                    upgradeFailed();
                    //  }
                    return;
                }
                if (isStartRSP(data)) {
                    LogUtil.v("got feedback received data");
                    mBackImg.setEnabled(false);
                    sendDataPosition = 0;
                    hasStartRSP = true;
                    sendBinDataUpdating(sendDataPosition);
                } else if (isUpdatingRSP(data)) {
                    sendDataPosition++;
                    boolean finished = sendBinDataUpdating(sendDataPosition);
                    if (finished) {
                        sendUpdateDone();
                    }
                    int progress = sendDataPosition * 100 / totalCount;
                    loadingProgress.setProgress(progress);
                    statusText.setText(String.valueOf(progress) + "%");

                } else if (isUpdateDoneRSP(data)) {
                    if (PmsDownLoadFinish) {
                        PmsSendStatusScuess = true;
                    } else {
                        upgradeSuccess();
                    }
                    //如果有pms，上传完成。smart未上传完成（2）没有pms,只有smart(3)没有smart只有pms

                    if (PmsSendStatusScuess && SmartDownLoadFinish && !SmartFileSended) {
                        //       Log.d("jmjm", "sendSmartFile");
                        byte[] version = getVersionInFile(upgradeSmartFilePath);
                        if (version != null) {
                            SmartFileSended = true;
                            startUpgrade(version);
                            latestVersion.setText("smart板更新中");
                        } else {
                            mBackImg.setEnabled(true);
                            Toast.makeText(this, "未获取到smart版本号", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        upgradeSuccess();
                    }
                } else if (isUpdateCrcFailRSP(data)) {
                    mBackImg.setEnabled(true);
                    btnDownloadUpdate.setEnabled(true);
                    btnDownloadUpdate.setText("下载");
                    status = FLAG_STATUS_NORMAL;
                    logoIcon.clearAnimation();
                    Toast.makeText(this, "文件校验失败", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
