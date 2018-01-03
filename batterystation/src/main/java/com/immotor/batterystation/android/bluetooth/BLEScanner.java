package com.immotor.batterystation.android.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.util.Log;

import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.entity.AccessoriesBean;
import com.immotor.batterystation.android.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BLEScanner {
    private BluetoothUtils mBluetoothUtils;
    private boolean mScanning = false;  //是否正在扫描
    private static final long SCAN_PERIOD = 2000;
    private List<AccessoriesBean> mPeripheralDeviceList = new ArrayList<>();      //扫描的外部设备
    private Map<String, AccessoriesBean> mPeripheralDeviceMap = new HashMap<>();      //扫描的外部设备
    //public static final int FLAG_BLUETOOTH_SCAN_STOP = 101;  //蓝牙扫描结束
    private Activity mActivity;
    public Preferences mPreferences;
    private String mMacAddressPlus;
    private static BLEScanner mBLEScanner;
    private BLEScanListener mBLEScanListener;

    private boolean isScooterDiscover = false;

    public interface BLEScanListener {
        void onStopScanListener();
        void onDiscoverScooterListener(String macAddress);
        void onDiscoverPeripheralListener();
    }

    //单例实现
    public synchronized static BLEScanner getInstance(Activity activity) {
        if (mBLEScanner == null) {
            mBLEScanner = new BLEScanner();
        }
        mBLEScanner.init(activity);
        return mBLEScanner;
    }

    private void init(Activity activity){
        mActivity = activity;
        mBluetoothUtils = BluetoothUtils.getInstance(activity);
        if(mPreferences==null) {
            mPreferences = Preferences.getInstance(mActivity);
        }
    }

    public void setBLEScanListener(BLEScanListener mBLEScanListener){
        this.mBLEScanListener = mBLEScanListener;
    }

    private BLEScanner() {
    }

    private static long askOpenBLETime = 0;  //请求打开蓝牙的时间，下次请求至少5分钟后，防止用户拒绝一直弹请求

    public void scanLeDevice(Activity activity) {
        if (mScanning) {
            return;
        }
        if(!BluetoothUtils.getInstance(activity).isBluetoothOn()){  //蓝牙没打开
            mPeripheralDeviceMap.clear();
            mPeripheralDeviceList.clear();
            isScooterDiscover = false;
            if(mBLEScanListener!=null) {
                mBLEScanListener.onStopScanListener();  //考虑到用户可能半路把蓝牙关了，需要刷新列表
            }
            long now = System.currentTimeMillis();
            if(now - askOpenBLETime <  300000){  //距离上次小于5分钟，不处理
                return;
            }
            askOpenBLETime = now;
        }
        Log.d("TAG", "Starting Scan");
        // Stops scanning after a pre-defined scan period.
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (mScanning) {
//                    LogUtil.v("Stopping Scan (timeout)");
//                    stopScan();
//                }
//            }
//        }, SCAN_PERIOD);

        isScooterDiscover = false;
        mPeripheralDeviceMap.clear();
        List<ScanFilter> bleScanFilters = new ArrayList<>();
        /*bleScanFilters.add(
                new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID.fromString(BLGattAttributes.SCOOTER_SERVICE_UUID))).build()
        );
        bleScanFilters.add(
                new ScanFilter.Builder().setServiceUuid(new ParcelUuid(UUID.fromString(BLGattAttributes.PERIPHERAL_SCOOTER_SERVICE_UUID))).build()
        );*/

        ScanSettings scanSettings = new ScanSettings.Builder().build();
        BluetoothAdapter bluetoothAdapter = mBluetoothUtils.getBluetoothAdapter();
        if(bluetoothAdapter == null){
            LogUtil.d("ble scan bluetooth adapter is null return");
            return;
        }
        BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        if (bluetoothLeScanner != null) {
            mScanning = true;
            bluetoothLeScanner.startScan(bleScanFilters, scanSettings, bleScanCallback);
        }else {
            Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(mIntent, 1);
        }
    }

    public void stopScan(){
        if(!mScanning){
            return;
        }
        mScanning = false;
        // check is device disconnect
        synchronized (mPeripheralDeviceList){
            mPeripheralDeviceList.clear();
            Iterator iterator = mPeripheralDeviceMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                AccessoriesBean val = (AccessoriesBean) entry.getValue();
                mPeripheralDeviceList.add(val);
            }
        }

        BluetoothLeScanner bluetoothLeScanner = mBluetoothUtils.getBluetoothAdapter().getBluetoothLeScanner();
        if (bluetoothLeScanner != null) {
            mBluetoothUtils.getBluetoothAdapter().getBluetoothLeScanner().stopScan(bleScanCallback);
            if(mBLEScanListener!=null) {
                mBLEScanListener.onStopScanListener();
            }
        }
    }

    private boolean isContains(String macAddress){
        synchronized (mPeripheralDeviceList){
            for (AccessoriesBean device : mPeripheralDeviceList){
                if (macAddress.equals(device.getDevice().getAddress())){
                    return true;
                }
            }
            return false;
        }
    }

    private ScanCallback bleScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            //LogUtil.v("onScanResult callbackType = "+callbackType);
            ScanRecord scanRecord = result.getScanRecord();
            byte[] scanRecordBytes = scanRecord.getBytes();
            BluetoothDevice device = result.getDevice();

            String scanRecordStr = StringUtils.bytesToHexString(scanRecordBytes);
            if (/*mScanning && */scanRecordStr.length() > 58) {
                String manufacturerData = scanRecordStr.substring(46, 58);
                mMacAddressPlus = mPreferences.getCurrentMacAddress();
              //  mMacAddressPlus = "574C54631f67";
                LogUtil.v("discover manufacturerData0 = " + manufacturerData);
                String manufacturerDataStr = StringUtils.reverseString2(manufacturerData);
                if (mMacAddressPlus != null && manufacturerDataStr.equalsIgnoreCase(mMacAddressPlus) && !isScooterDiscover) {
                    LogUtil.v("discover manufacturerData = " + manufacturerDataStr);
                    isScooterDiscover = true;
                    if (mBLEScanListener != null) {
                        mBLEScanListener.onDiscoverScooterListener(device.getAddress());
                    }
                }
            }

           /* // for 外设
            if (scanRecordStr.contains(BLGattAttributes.PERIPHERAL_SCAN_SERVER_UUID)){
                LogUtil.d("scanRecord: "+scanRecordStr);
                AccessoriesBean bean = new AccessoriesBean();
                bean.setDevice(device);
                bean.setName(device.getName());
                if(scanRecordBytes[11]==0x32&&scanRecordBytes[12]==0x30){
                    bean.setType(0);    // dock
                    if(scanRecordBytes[13]==1) {
                        bean.setStatus(2);  // Charging
                    }else{
                        bean.setStatus(0);
                    }
                } else {
                    bean.setType(1);    // light
                    //bean.setStatus(2);  // on / off
                }
                mPeripheralDeviceMap.put(device.getAddress(), bean);
                if (!isContains(device.getAddress())) {
                    synchronized (mPeripheralDeviceList){
                        mPeripheralDeviceList.add(bean);
                    }
                    if(mBLEScanListener!=null) {
                        mBLEScanListener.onDiscoverPeripheralListener();
                    }

                }

            }*/

            LogUtil.v("scanRecordStr:"+scanRecordStr+" device address:"+device.getAddress()+" device name:"+device.getName()+ "  uuids:"+device.getUuids());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            LogUtil.v("onBatchScanResults");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            mPeripheralDeviceMap.clear();
            mPeripheralDeviceList.clear();
            if(mBLEScanListener!=null) {
                mBLEScanListener.onStopScanListener();
            }
            LogUtil.v("onScanFailed error code="+errorCode);
        }
    };

    public List<AccessoriesBean> getPeripheralDeviceList(){
        return mPeripheralDeviceList;
    }

    public boolean isScanning() {
        return mScanning;
    }


}
