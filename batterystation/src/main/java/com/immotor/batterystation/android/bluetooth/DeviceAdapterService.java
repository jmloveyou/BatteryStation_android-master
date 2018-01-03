package com.immotor.batterystation.android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import com.immotor.batterystation.android.util.LogUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 *
 * Created by ashion.zhong on 8/19/2016.
 */
public class DeviceAdapterService {

    private static final String TAG = DeviceAdapterService.class.getSimpleName();

    private Context mContext;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mCommandCharacteristic;
    private int availableLen;   //接收到的数据长度
    private boolean isWaitForLongTime = false;  //是否长时间等待，协议0x20响应比较慢，因为车那边需要初始化
    //状态对象
    public DeviceStateEvent mDeviceState = null;
    //回调
    private DeviceCallback mDeviceCallBack = null;
    //单例初始化
    private static DeviceAdapterService mDevService = null;

    private String curAddress = "";


    public byte[] sendDataBuffer;

    private long RECEIVED_TIME_OUT = 5;// second 没接收到发送相关的数据 timeout
    private long LONG_RECEIVED_TIME_OUT = 15; //second，长期等待超时时长
    private Timer mTimer;
    private int timeoutCount = 0;
    private boolean hasSentData = false;    //是否发送了数据
    private boolean hasReceivedData = false;    //是否收到了返回  默认返回
    private BluetoothDevice bluetoothDevice;// bluetoothDevice

    private int writeErrorCount = 0;   //写失败计数

    //单例实现
    public synchronized static DeviceAdapterService getInstance(Context context) {
        if (mDevService == null) {
            mDevService = new DeviceAdapterService(/*context*/);
        }
        mDevService.mContext = context;
        return mDevService;
    }

    //类构造方法
    private DeviceAdapterService(/*Context context*/) {

        mDeviceState = new DeviceStateEvent(); //初始化状态类
    }

    public void notifyDeviceAttach() {

    }

    public void notifyDeviceDetach() {
        disconnectDevice();
    }


    /**
     * 创建设备列表
     */
    public void startConnectDevice(String address) {

        if (initialize()) {
            if (mBluetoothAdapter == null || address == null) {
                Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
                return;
            }

            bluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
            if (bluetoothDevice == null) {
                Log.w(TAG, "Device not found.  Unable to connect.");
                mDeviceState.setListen();
                return;
            }
            // We want to directly connect to the bluetoothDevice, so we are setting the autoConnect
            // parameter to false.
            /*if (mBluetoothGatt != null) {
                LogUtil.v("init bluetooth init");
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
            }*/
            mDeviceState.setTimeout(false);

            LogUtil.v("Trying to create a new connection.");
            mBluetoothGatt = bluetoothDevice.connectGatt(mContext, false, mGattCallback);
            LogUtil.v("connection mBluetooth Gatt = "+mBluetoothGatt);

            curAddress = address;
        }else{
            LogUtil.d("ble adapter service initialize fail");
        }
    }

    /**
     * @return 当前连接的设备address
     */
    String getCurAddress(){
        return curAddress;
    }

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                mDeviceState.setListen();
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            mDeviceState.setListen();
            return false;
        }
        return true;
    }

    /**
     * 断开连接设备
     */
    public void disconnectDevice() {
        Log.v(TAG, "enter disconnectDevice");
        if (mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
        LogUtil.v("disconnect Device mBluetooth Gatt = "+mBluetoothGatt);
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }

        mCommandCharacteristic = null;

        // set status
        mDeviceState.setListen();
    }

    public void close(){
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }


    /**
     * 写字符串
     *
     * @param buffer The bytes to write
     */
    public synchronized boolean write(byte[] buffer) {
        if (!mDeviceState.isConnected()) {
            return false;
        }
        try {
            //Log.v(TAG, "mCommandCharacteristic is:"+mCommandCharacteristic);
            if (mCommandCharacteristic != null) {
                if (/*mBluetoothAdapter == null || */mBluetoothGatt == null) {
                    LogUtil.v("BluetoothAdapter not initialized");
                    return false;
                }
                mCommandCharacteristic.setValue(buffer);
                boolean result = mBluetoothGatt.writeCharacteristic(mCommandCharacteristic);
                String hexS = NumberBytes.bytesToHexString(buffer);
                LogUtil.v("☆☆☆☆☆ Send:" + hexS);
                if(result) {
                    LogUtil.d("whj ble send: result: " + result + ",data: " + hexS);
                    hasSentData = true; // 标识发送数据，be use for timeout
                    sendDataBuffer = buffer;
                    hasReceivedData = false;
                    timeoutCount = 0;
                    mDeviceState.setTimeout(false);
                }else if(!isUpgrade){ //升级可能会多次尝试，不算
                    writeErrorCount++;
                    if(writeErrorCount > 5){
                        disconnectDevice();
                    }
                }
                return result;
            }
        } catch (Exception e) {
            LogUtil.v("Write data failed Connect lose exception:" + e);
            connectFailed();
        }
        return false;
    }


    private boolean isUpgrade = false;     //是否在升级

    public void setIsUpgrade(boolean upgrade){
        isUpgrade = upgrade;
    }


    /**
     * 设置回调方法
     */
    public void addDeviceCallback(DeviceCallback callback) {
        //初始化回调方法
        mDeviceCallBack = callback;
    }

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                LogUtil.v("Connected to GATT server.");
                // Attempts to discover services after successful connection.
                mBluetoothGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                LogUtil.v("Disconnected from GATT server.");
                mDeviceState.setDisconnected();
                close();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.w(TAG, "onServicesDiscovered received: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mDeviceState.setConnected();
                mBluetoothGatt.requestMtu(512);
            }
        }

        private void initCharacteristic(List<BluetoothGattService> servicesList) {
            BluetoothGattCharacteristic characteristic = null;
            int servicesSize = servicesList.size();
            boolean isAccessories = false;
            for (int i = 0; i < servicesSize; i++) {
                BluetoothGattService service = servicesList.get(i);
                String uuid = service.getUuid().toString();
                // scooter device
                if (uuid.equalsIgnoreCase(BLGattAttributes.SCOOTER_SERVICE_UUID)) {
                    List<BluetoothGattCharacteristic> gattCharacteristics = service.getCharacteristics();
                    int chaSize = gattCharacteristics.size();
                    for (int j = 0; j < chaSize; j++) {
                        BluetoothGattCharacteristic characteristicTmp = gattCharacteristics.get(j);
                        String chaUuid = characteristicTmp.getUuid().toString();
                        if (chaUuid.equalsIgnoreCase(BLGattAttributes.NOTIFICATION_CHARACTERISTIC_UUID)) {
                            characteristic = characteristicTmp;
                            isAccessories = false;
                        } else if (chaUuid.equalsIgnoreCase(BLGattAttributes.COMMAND_CHARACTERISTIC_UUID)) {
                            mCommandCharacteristic = characteristicTmp;
                        }
                    }
                    break;
                    // for accessories
                }/*else if (uuid.equalsIgnoreCase(BLGattAttributes.PERIPHERAL_SCOOTER_SERVICE_UUID)) {
                    List<BluetoothGattCharacteristic> gattCharacteristics = service.getCharacteristics();
                    int chaSize = gattCharacteristics.size();
                    for (int j = 0; j < chaSize; j++) {
                        BluetoothGattCharacteristic characteristicTmp = gattCharacteristics.get(j);
                        String chaUuid = characteristicTmp.getUuid().toString();
                        if (chaUuid.equalsIgnoreCase(BLGattAttributes.PERIPHERAL_NOTIFICATION_CHARACTERISTIC_UUID)) {
                            characteristic = characteristicTmp;
                            isAccessories = true;
                        } else if (chaUuid.equalsIgnoreCase(BLGattAttributes.PERIPHERAL_COMMAND_CHARACTERISTIC_UUID)) {
                            mCommandCharacteristic = characteristicTmp;
                        }
                    }
                    break;
                }*/

            }
            if (characteristic == null) {
                return;
            }
            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // If there is an active notification on a characteristic, clear
                // it first so it doesn't update the data field on the user interface.
                if (mNotifyCharacteristic != null) {
                    setCharacteristicNotification(isAccessories, mNotifyCharacteristic, false);
                    mNotifyCharacteristic = null;
                }
                readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = characteristic;
                setCharacteristicNotification(isAccessories, characteristic, true);
            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                parseReceiveData(characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            //Log.i(TAG, "from GATT server on Characteristic Changed.");
            parseReceiveData(characteristic);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //this.supportedMTU = mtu;//local var to record MTU size
                List<BluetoothGattService> servicesList = getSupportedGattServices();
                initCharacteristic(servicesList);
                mDeviceState.setConnected();
                startTimeoutTimer();
            }
            LogUtil.v("on mut changed mtu="+mtu+" status="+status);
        }
    };

    private void startTimeoutTimer(){
        if(mTimer!=null) {  //防止超时后计时不停止，多个timer同时计时
            mTimer.cancel();
            mTimer = null;
        }
        mTimer = new Timer();
        timeoutCount = 0;
//        hasSentData = false;
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (hasSentData && !hasReceivedData){       // 如果尚未收到返回
                    timeoutCount++;
                    LogUtil.v("timeout count="+timeoutCount);
                }
                if(hasReceivedData){
                    timeoutCount = 0;
                }
                if ((!isWaitForLongTime && timeoutCount >=  RECEIVED_TIME_OUT)||(isWaitForLongTime && timeoutCount >= LONG_RECEIVED_TIME_OUT)){
                    LogUtil.d("whj ble timeout");
                    timeoutCount = 0;
                    hasReceivedData = false; // reset
                    hasSentData = false;     // reset
                    mDeviceState.setTimeout(true);
                }
            }
        }, 1000, 1000);
    }

    /**
     * 是否长时间等待超时
     * @param isWait
     */
    public void setRequestWaitForLongTime(boolean isWait){
        if(!isWait){
            timeoutCount = 0; //重置计时
        }
          isWaitForLongTime = isWait;
    }

    private byte[] tempBuf = new byte[2048];
    private int bufSize = 0;
    private boolean isATCommand = false;   //ATCommand "AT+THROUGHPUT"
    private void parseReceiveData(BluetoothGattCharacteristic characteristic){
        final byte[] readDataBuffer = characteristic.getValue();
        LogUtil.d("whj ble recv origin: " + NumberBytes.bytesToHexString(readDataBuffer));
        //非AT命令
        hasReceivedData = true;
        hasSentData = false;
        writeErrorCount = 0;
        availableLen = readDataBuffer.length;
        LogUtil.v("☆☆☆☆☆ received:" + NumberBytes.bytesToHexString(readDataBuffer));
//            receivedDataVerify(readDataBuffer, readDataBuffer.length);
        if (mDeviceCallBack != null) {
            LogUtil.d("recv  deal with: " + NumberBytes.bytesToHexString(readDataBuffer));
            mDeviceCallBack.getDataFromDevice(readDataBuffer, availableLen);
        }

    }

    /**
     * check is send data timeout
     * @param readDataBuffer
     * @return
     */
    private void receivedDataVerify(byte[] readDataBuffer, int length){
        byte receivedByte0 = readDataBuffer[0];
        if (receivedByte0 == (byte) 0xA0 ||         //行驶中状态上报
                receivedByte0 == (byte) 0xA1 ||     //故障上报
                receivedByte0 == (byte) 0xA2 ||     //拍照按键触发
                (length >2 && readDataBuffer[0]==BLECommand.Upper_A && readDataBuffer[1]==BLECommand.Upper_T
                        && readDataBuffer[2]==BLECommand.Symbol_add)) {   //AT命令
            hasReceivedData = true;
            return;
        }
        if (sendDataBuffer != null && length > 2){
            if (sendDataBuffer[0] == readDataBuffer[0]){
                if (sendDataBuffer[0] == 0x55){
                    byte receivedByte1 = readDataBuffer[1];
                    if (sendDataBuffer[1] != receivedByte1){
                        hasReceivedData = false;
                        return;
                    }
                }
                hasReceivedData = true;
                return;
            }
        }
        hasReceivedData = false;
    }


    //连接失败处理法
    private void connectFailed() {
        mDeviceState.setListen();
    }


    //回调接口
    public interface DeviceCallback {
        
        void getDataFromDevice(byte[] buf, int len);

        void getDeviceState(DeviceStateEvent state);
    }


    //蓝牙状态
    public class DeviceStateEvent {
        //预定义的几种状态
        public static final int STATE_NONE = 0; // 无连接
        public static final int STATE_LISTEN = 1; // 监听状态
        public static final int STATE_CONNECTING = 2; // 正在连接.
        public static final int STATE_CONNECTED = 3; // 已经连接.
        public static final int STATE_TIMEOUT = 4; // 通讯超时
        //设备状态变量
        private int mState = STATE_NONE;
        public boolean isTimeout = false;

        /**
         * 设置State状态
         */
        private void setState(int state) {
            //Log.v(TAG, "setState() " + "Serial Port State = " + state);
            if(state!=mState) {
                mState = state;
                if (mDeviceCallBack != null) {
                    mDeviceCallBack.getDeviceState(mDeviceState);
                }
            }
        }

        private void setState() {
            //Log.v(TAG, "setState() " + "Serial Port State = " + state);
            if (mDeviceCallBack != null) {
                mDeviceCallBack.getDeviceState(mDeviceState);
            }
        }

        //
        public void setNone(){
            isTimeout = false;
            setState(STATE_NONE);
        }

        //设置设备为监听状态
        public void setListen() {
            isTimeout = false;
            setState(STATE_LISTEN);
        }

        //设置为正在连接状态
        public void setConnecting() {
            setState(STATE_CONNECTING);
        }

        //设置为已经连接状态
        public void setConnected() {
            isTimeout = false;
            setState(STATE_CONNECTED);
        }

        //设置为断开连接状态
        public void setDisconnected() {
            isTimeout = false;
            setState(STATE_NONE);
        }

        //设置为已经接收指令超时
        public void setTimeout(boolean isTimeout) {
            this.isTimeout = isTimeout;
            if (isTimeout){
                setState();
            }
        }

        //判断是否已经连接
        public boolean isConnected() {
            if (mState == STATE_CONNECTED)
                return true;
            else
                return false;
        }

        //判断是否超时
        public boolean isTimeout() {
            if (isTimeout)
                return true;
            else
                return false;
        }

        //判断是否连接中断
        public boolean isConnectLost() {
            if (mState == STATE_LISTEN || mState == STATE_NONE/* || mState == STATE_TIMEOUT*/)
                return true;
            else
                return false;
        }
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(boolean isAccessories, BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        boolean isSuccess = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        if (isSuccess && isAccessories) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(BLGattAttributes.SCOOTER_SERVICE_UUID));
            if(descriptor != null){
                if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                } else if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                }
                mBluetoothGatt.writeDescriptor(descriptor);
                LogUtil.i("Characteristic set notification is Success!");
            }
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected bluetoothDevice. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

//    public void setNull(){
//        mDevService = null;
//    }
}
