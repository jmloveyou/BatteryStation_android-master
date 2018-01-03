package com.immotor.batterystation.android.bluetooth;

/**
 *
 * Created by ashion.zhong on 8/19/2016.
 */
public interface DeviceInterface {

    //连接中断通知
    void deviceConnectLost();

    //连接成功通知
    void deviceConnected();

    void deviceTimeout();

    //收到数据通知<命令字> + <数据>
    void getDataFromService(byte[] dataBuf, int dataLen);

}
