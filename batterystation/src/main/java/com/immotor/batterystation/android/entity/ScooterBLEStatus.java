package com.immotor.batterystation.android.entity;

/**
 * Created by Ashion on 2016/9/8.
 */
public class ScooterBLEStatus {

    private String sID;

    private boolean isCharging; //是否在充电

    private boolean isLocked;   //是否已经锁车

    private boolean isSmartKey; //智能钥匙状态。 0: Disable 1: Enable

    private float speed;        //Km/h, 当前速

    private float modeSpeed;    //Speed 当前助力模式速度。 有效值：1~5。 单位：KM/H

    private float maxSpeed;     //Mode2 的最大限速。 有效值：10~25。 单位：KM/H

    private float leftMileage;  //剩余里程

    private int leftBattery;    //剩余电量

    private boolean lightAutoMode; //自动大灯开关

    private boolean lightFront;    //前大灯开关

    private boolean lightLogo;     //logo 灯开关

    private boolean launchZeroMode;    //零速度启动 BIT[6]: 零速启动。 0: Off 1: O

    private boolean batteryPresent0; //电池卡槽  BIT 0：0 号槽位， 0: Not Present 1: Present。 BIT 1：1 号槽位， 0: Not Present 1: Present。 BIT[2-7]：0, 保留。

    private boolean batteryPresent1;

    private boolean boostMode;  //助力模式  0 disable 1 enable

    private int lightFrontValue;  //大灯亮度

    public String getsID() {
        return sID;
    }

    public void setsID(String sID) {
        this.sID = sID;
    }

    public void setCharging(boolean charging) {
        isCharging = charging;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isCharging() {
        return isCharging;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public boolean isSmartKey() {
        return isSmartKey;
    }

    public void setSmartKey(boolean smartKey) {
        isSmartKey = smartKey;
    }

    public float getModeSpeed() {
        return modeSpeed;
    }

    public void setModeSpeed(float modeSpeed) {
        this.modeSpeed = modeSpeed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(float maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public float getLeftMileage() {
        return leftMileage;
    }

    public void setLeftMileage(float leftMileage) {
        this.leftMileage = leftMileage;
    }

    public int getLeftBattery() {
        return leftBattery;
    }

    public void setLeftBattery(int leftBattery) {
        this.leftBattery = leftBattery;
    }

    public boolean isLightAutoMode() {
        return lightAutoMode;
    }

    public void setLightAutoMode(boolean lightAutoMode) {
        this.lightAutoMode = lightAutoMode;
    }

    public boolean isLightFront() {
        return lightFront;
    }

    public void setLightFront(boolean lightFront) {
        this.lightFront = lightFront;
    }

    public boolean isLightLogo() {
        return lightLogo;
    }

    public void setLightLogo(boolean lightLogo) {
        this.lightLogo = lightLogo;
    }

    public boolean isLaunchZeroMode() {
        return launchZeroMode;
    }

    public void setLaunchZeroMode(boolean launchZeroMode) {
        this.launchZeroMode = launchZeroMode;
    }

    public boolean isBatteryPresent0() {
        return batteryPresent0;
    }

    public void setBatteryPresent0(boolean batteryPresent0) {
        this.batteryPresent0 = batteryPresent0;
    }

    public boolean isBatteryPresent1() {
        return batteryPresent1;
    }

    public void setBatteryPresent1(boolean batteryPresent1) {
        this.batteryPresent1 = batteryPresent1;
    }

    public boolean isBoostMode() {
        return boostMode;
    }

    public void setBoostMode(boolean boostMode) {
        this.boostMode = boostMode;
    }

    public int getLightFrontValue() {
        return lightFrontValue;
    }

    public void setLightFrontValue(int lightFrontValue) {
        this.lightFrontValue = lightFrontValue;
    }
}
