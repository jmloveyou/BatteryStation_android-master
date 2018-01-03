package com.immotor.batterystation.android.entity;

/**
 * Created by immotor on 2017/10/31.
 * 电池升级文件信息
 */

public class BatteryUpdateFileInfo {
    public int[] verInfo = new int[6];
    public String fileName; //全路径

    /**
     * @param mHW 硬件主版本
     * @param sHW 硬件子版本
     * @param mFW 固件主版本
     * @param sFW 固件子版本
     * @param cFW 固件修订版本
     * @param bFW 固件编译版本
     */
    public void setVerInfo(int mHW, int sHW, int mFW, int sFW, int cFW, int bFW){
        verInfo[0] = mHW;
        verInfo[1] = sHW;
        verInfo[2] = mFW;
        verInfo[3] = sFW;
        verInfo[4] = cFW;
        verInfo[5] = bFW;
    }
}
