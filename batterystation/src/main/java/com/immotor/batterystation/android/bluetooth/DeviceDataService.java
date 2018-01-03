package com.immotor.batterystation.android.bluetooth;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by ashion.zhong on 8/19/2016.
 */
public class DeviceDataService extends Observable implements DeviceAdapterService.DeviceCallback {

    private static final String TAG = DeviceDataService.class.getSimpleName();
    //单例
    private static DeviceDataService mDeviceDataService = null;
    //服务实例
    public DeviceAdapterService mDeviceAdapterService = null;

    public DeviceAdapterService.DeviceStateEvent mDeviceState = null;
    private DeviceAdapterService.DeviceStateEvent mDeviceStateLast = null;

    private Context mContext;

    //观察者对象列表
    private ArrayList<DeviceInterface> mObserverList = null;

    private boolean mIsSending = false;   //是否正在发送

    //发送的数组长度
    private int mSendBufferLen;
    // 定义发送数据缓冲区
    private final static int M_DATA_BUFFER_LEN = 256;    //数据缓冲区长度
    private byte[] mSendBuffer = new byte[M_DATA_BUFFER_LEN];

    private boolean mSpecificObserverSwitch = false;  //当为true时候，屏蔽其他观察者
    private DeviceInterface mSpecificObserver = null; //特色观察者,当上面值为true时，只给这一个观察者发送消息

    private List<byte[]> mDataBuffList = new ArrayList<byte[]>();    //存储每个界面的指令集和
    private int mDataBufferIndex = 0;   //指令集合的序号
    private boolean isActiveCmd = false;    //是否主动触发请求  用户操作界面时候为true否则false.
    private boolean hasReceivedData = true;    //是否已经获得返回的数据
    private Timer loopTimer;    // send data loop timer
    private long DATA_PERIOD = 50;       //发送间隔

    public boolean isValid = false; //判断数据是否有效
    public static final int MSG_RECEIVE_DATA_FLG = 111;


    //单例实现
    public synchronized static DeviceDataService getInstance(Context context) {
        if (mDeviceDataService == null) {
            mDeviceDataService = new DeviceDataService(context);
        }
        return mDeviceDataService;
    }

    //构造方法
    private DeviceDataService(Context context) {
        mContext = context;
        mDeviceAdapterService = DeviceAdapterService.getInstance(context);
        mDeviceAdapterService.addDeviceCallback(this);
        mDeviceState = mDeviceAdapterService.mDeviceState;
        mObserverList = new ArrayList<>();
    }

    //添加观察者
    public synchronized void addObserver(DeviceInterface observer) {
        if (observer != null) {
            mObserverList.add(observer);
        }
    }

    //删除观察者
    public synchronized void delObserver(DeviceInterface observer) {
        if (observer != null) {
            mObserverList.remove(observer);
        }
    }

    /**
     * 设置蓝牙是否等待长时间，等待的话就不会主动3秒超时
     * 因为协议0x20需要等待超长时间
     * @param isWait
     */
    public void setRequestWaitForLongTime(boolean isWait){
        mDeviceAdapterService.setRequestWaitForLongTime(isWait);
    }

    /**
     * 是否鉴权
     */
    private boolean isAuth = false;

    /**
     * 设置是否鉴权通过
     * @param auth
     */
    public void setAuth(boolean auth){
        isAuth = auth;
    }

    public boolean isAuth(){
        return isAuth;
    }

    /**
     * ***************************************************
     * 该函数用于发送数据到设备
     *
     * @param sendBuf :发送数据区
     * @param needAuth :是否需要鉴权，配件，升级，getDeviceID和请求鉴权本身不需要已鉴权
     * @return 0--发送成功; -1 -- 发送失败，没连接或没认证, -2 -- 正在发送...-3发送失败，底层发送失败
     * ****************************************************
     */
    public synchronized int sendDataToDevice(byte[] sendBuf, boolean needAuth) {

        //先判断连接是否正常，如果不正常，则通知观察者
        if (!mDeviceState.isConnected()) {
            //通知观察者
            notifyDeviceConnectLost();
            return -1;
        }
        if(needAuth && !isAuth){  //需要鉴权但是还没鉴权通过
            return -1;
        }
        //检查系统是否正在发送数据
        if (mIsSending) {
            return -2;
        }
        int dataLen = sendBuf.length;

        System.arraycopy(sendBuf, 0, mSendBuffer, 0, dataLen);

        mSendBufferLen = dataLen;

        boolean success = false;
        mIsSending = true;
        // 发送命令
        success = sendCmd(mSendBuffer, mSendBufferLen);
        mIsSending = false;
        hasReceivedData = false;
        return (success?0:-3);
    }

    /**
     * @param sendBuf
     * @return 0--发送成功; -1 -- 发送失败，没连接或没认证, -2 -- 正在发送...-3发送失败，底层发送失败
     *
     */
    public  synchronized int sendDataToDevice(byte[] sendBuf){
        return sendDataToDevice(sendBuf, true);
    }

    // 发送一帧命令给设备
    private boolean sendCmd(byte[] cmd, int pLen) {
        //Log.v(TAG, "enter Send sendCmd ************************************");
        if (!mDeviceState.isConnected()) {
            //通知观察者
            notifyDeviceConnectLost();
            mIsSending = false; //关闭正在发送
            return false;
        }
        byte[] commandBuffer = new byte[pLen];
        System.arraycopy(cmd, 0, commandBuffer, 0, pLen);
        if (mDeviceAdapterService != null) {
            return mDeviceAdapterService.write(commandBuffer);
        }
        return false;
    }


    //处理收到的设备数据
    private void receiveDeviceData(byte[] dataBuffer, int dataLen) {
        //先通过计数器过滤垃圾数据
        notifyGetDataFromService(dataBuffer, dataLen);
        //String vReceive = NumberBytes.bytesToHexString(dataBuffer);
        //Log.v(TAG, "Receive byte：" + vReceive);
    }


    //通知收到的数据
    private synchronized void notifyGetDataFromService(byte[] dataBuffer, int dataLen) {
        //LogUtil.v("enter notifyGetDataFromService!");
        int len = mObserverList.size();
        if (mSpecificObserverSwitch) {
            if(mSpecificObserver!=null)
                mSpecificObserver.getDataFromService(dataBuffer, dataLen);
        }else{
            for (int i = 0; i < len; i++) {
                DeviceInterface vInterface = mObserverList.get(i);
                if (vInterface != null) {
                    vInterface.getDataFromService(dataBuffer, dataLen);
                }
            }
        }
    }


    @Override
    public void getDataFromDevice(byte[] buf, int len) {
        receiveDeviceData(buf, len);
    }


    @Override
    public void getDeviceState(DeviceAdapterService.DeviceStateEvent state) {
        Log.v(TAG, "Get device status!!!");
        mDeviceStateLast = mDeviceState;//保存历史状态
        mDeviceState = state;

        if(mDeviceState.isTimeout()){
            notifyDeviceTimeout();
            Log.v(TAG, "mDeviceState=timeout");
        }else if (mDeviceState.isConnected()) {
            notifyDeviceConnected();
            Log.v(TAG, "mDeviceState=Connected");
        }else if(mDeviceState.isConnectLost()) {
            notifyDeviceConnectLost();
            Log.v(TAG, "mDeviceState=Lose");
        }
    }


    /**
     * 通知观察者设备已经连接上了
     */
    private synchronized void notifyDeviceConnected() {
        if (mSpecificObserverSwitch) {
            if (mSpecificObserver!=null)
                mSpecificObserver.deviceConnected();
        } else {
            for (int i = 0; i < mObserverList.size(); i++) {
                DeviceInterface vInterface = mObserverList.get(i);

                    if (vInterface != null)
                        vInterface.deviceConnected();
            }
        }
    }

    /**
     * 通知观察者设备已经超时了
     */
    private synchronized void notifyDeviceTimeout() {
        for (int i = 0; i < mObserverList.size(); i++) {
            DeviceInterface vInterface = mObserverList.get(i);
            if (vInterface != null) {
                vInterface.deviceTimeout();
            }
        }
    }

    /**
     * 通知观察者设备中断
     */
    private synchronized void notifyDeviceConnectLost() {
        if (loopTimer != null) {
            loopTimer.cancel();
        }
        for (int i = 0; i < mObserverList.size(); i++) {
            DeviceInterface vInterface = mObserverList.get(i);
            if (vInterface != null) {
                vInterface.deviceConnectLost();
            }
        }
    }


    /**
     * 用户主动请求发送的指令
     *
     * @param sendBuf
     */
    public void sendActiveDate(byte[] sendBuf) {
        isActiveCmd = true;
        for (int i = 0; i < 3; i++) {  // retry 3 times
            int result = sendDataToDevice(sendBuf);
            if (result != -2) {
                break;
            }
        }
        isActiveCmd = false;
    }


    /**
     * 循环发送指令
     */
    public void sendCommandLoop(List<byte[]> sendBufferList) {
        mDataBuffList = sendBufferList;
        loopTimer = new Timer();
        loopTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (isActiveCmd) {
                    return;
                }
                if (!hasReceivedData /*&& mCounter < 3*/) {
                    //mCounter++;
                    return;
                }
                int len = mDataBuffList.size();
                if (mDataBufferIndex >= len) {
                    mDataBufferIndex = 0;
                }
                try {
                    byte[] dataBuff = mDataBuffList.get(mDataBufferIndex);
                    sendDataToDevice(dataBuff);
                    mDataBufferIndex++;
                }catch (Exception e){}
            }
        }, 100, DATA_PERIOD);
    }

    /**
     * 获取当前发送的指令
     *
     * @return
     */
    public byte[] getSendBuffer() {
        return mSendBuffer;
    }


    /**
     * 设置是否收到了数据
     */
    public void setHasReceivedData(boolean hasReceivedData) {
        this.hasReceivedData = hasReceivedData;
    }


    /**
     * stop send loop data buffer
     */
    public void stopSendLoop() {
        if (loopTimer != null) {
            loopTimer.cancel();
            loopTimer = null;
        }
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public void connect(final String address) {
        mDeviceAdapterService.startConnectDevice(address);
    }


    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        mDeviceAdapterService.disconnectDevice();
    }

    /**
     * @return 当前设备连接的address
     */
    public String getAddress(){
        return mDeviceAdapterService.getCurAddress();
    }


    /**
     * 判断收到的数据是否跟发送的匹配
     * @return
     */
    public boolean verifyReceiveData(byte[] receivedData, byte[] sendData){
        boolean result = false;
        byte receiveByte0 = 0x00;
        byte sendByte0 = 0x00;
        if (receivedData != null && receivedData.length > 0){
            receiveByte0 = receivedData[0];
        }
        if (sendData != null && sendData.length > 0){
            sendByte0 = sendData[0];
        }
        if (receiveByte0 == sendByte0 ||
                receiveByte0 == (byte)0xA0 ||
                receiveByte0 == (byte)0xA1 ||
                receiveByte0 == (byte)0xA2 ||
                (receivedData.length >2 && receivedData[0]==BLECommand.Upper_A && receivedData[1]==BLECommand.Upper_T
                && receivedData[2]==BLECommand.Symbol_add)){
            result = true;
        }
        return result;
    }

    public boolean isConnected(){
        return mDeviceAdapterService.mDeviceState.isConnected();
    }

    public boolean isConnectLost(){
        return mDeviceAdapterService.mDeviceState.isConnectLost();
    }

    public boolean isTimeout(){
        return mDeviceAdapterService.mDeviceState.isTimeout();
    }

//    public void setNullService(){
//        mDeviceDataService = null;
//        mDeviceAdapterService.setNull();
//    }
}
