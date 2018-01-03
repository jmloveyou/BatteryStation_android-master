package com.immotor.batterystation.android.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Ashion on 2017/5/10.
 */

public class Preferences {

    private static final String KEY_USER_TOKEN = "user_token";
    private static final String KEY_NEED_DEPOSIT = "need_deposit";
    private static final String KEY_UUID = "uuid";   //手机设备唯一码
    private static final String KEY_UID = "uid";     //用户ID
    private static final String KEY_PHONE = "user_phone";  //用户手机号
    private static final String KEY_BIRTHDAY = "user_birthday";  //用户生日
    private static final String KEY_AVATAR = "user_avatar";  //用户头像
    private static final String KEY_SEX = "user_sex";   //用户性别
    private static final String KEY_CREATE_TIME = "user_create_time";  //用户创建时间
    private static final String KEY_USER_NAME = "user_name";  //用户姓名

    private static final String KEY_USER_COMBO_STATUS = "key_user_combo_status";
    private static final String KEY_USER_MYBATTEY_STATUS = "key_user_mybattey_status";
    private static final String KEY_NOTICE_MESSAGE_STATUS = "key_notice_message_status";

    private static Preferences mSharedPreferencesManager;
    SharedPreferences prefs;
    SharedPreferences.Editor prefsEditor;

    private Preferences(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.prefsEditor = prefs.edit();
    }

    public static Preferences getInstance(Context context) {
        if (mSharedPreferencesManager == null) {
            synchronized (Preferences.class) {
                if (mSharedPreferencesManager == null) {
                    mSharedPreferencesManager = new Preferences(context);
                }
            }
        }
        return mSharedPreferencesManager;
    }
    public String getOrderStationImgUrl() {
        return prefs.getString("ordered_station_img_url", null);
    }

    public void setOrderStationImgUrl(String url) {
        prefsEditor.putString("ordered_station_img_url", url).apply();
    }

    public String getOrderStation() {
        return prefs.getString("ordered_station_pid", null);
    }

    public void setOrderStation(String pid) {
        prefsEditor.putString("ordered_station_pid", pid).apply();
    }

    public void setNewVersionName(String versionName) {
        prefsEditor.putString("latest_version_name", versionName).apply();
    }

    public String getNewVersionName() {
        return prefs.getString("latest_version_name", null);
    }

    public void setNewVersionUrl(String versionUrl) {
        prefsEditor.putString("latest_version_url", versionUrl).apply();
    }

    public String getNewVersionUrl() {
        return prefs.getString("latest_version_url", null);
    }

 /*   public boolean getNoticeMessageStatus() {
        return prefs.getBoolean(KEY_NOTICE_MESSAGE_STATUS, false);
    }

    public void setKeyNoticeMessageStatus(boolean status) {
        prefsEditor.putBoolean(KEY_NOTICE_MESSAGE_STATUS, status).apply();
    }*/

    public String getCurrentMacAddress() {
        return prefs.getString("mac_address", null);
    }
    public void setCurrentMacAddress(String macAddress) {
        prefsEditor.putString("mac_address", macAddress).apply();
    }

    /**
     * 设置token
     *
     * @param token
     */
    public void setToken(String token) {
        prefsEditor.putString(KEY_USER_TOKEN, token).apply();
    }

    /**
     * 获得token，若没有，可能返回null
     *
     * @return
     */
    public String getToken() {
        return prefs.getString(KEY_USER_TOKEN, null);
    }

    /**
     * 获得是否有套餐，若没有false
     *
     * @return
     */
    public boolean getComboStatus() {
        return prefs.getBoolean(KEY_USER_COMBO_STATUS, false);
    }

    /**
     * 设置是否有套餐状态，若有true
     *
     * @return
     */
    public void setComboStatus(boolean status) {
        prefsEditor.putBoolean(KEY_USER_COMBO_STATUS, status).apply();
    }

    /**
     * 获得是否有电池，若没有false
     *
     * @return
     */
    public boolean getMybatteryStatus() {
        return prefs.getBoolean(KEY_USER_MYBATTEY_STATUS, false);
    }

    /**
     * 设置是否有电池餐状态，若有true
     *
     * @return
     */
    public void setMyBatteryStatus(boolean status) {
        prefsEditor.putBoolean(KEY_USER_MYBATTEY_STATUS, status).apply();
    }

    /**
     * 是否需要主动显示交押金界面
     *
     * @param deposit
     */
    public void setDeposit(boolean deposit) {
        prefsEditor.putBoolean(KEY_NEED_DEPOSIT, deposit).apply();
    }

    /**
     * 是否需要主动显示交押金界面
     *
     * @return
     */
    public boolean isDeposit() {
        return prefs.getBoolean(KEY_NEED_DEPOSIT, true);
    }

    /**
     * 设置手机唯一标识
     *
     * @param uuid
     */
    public void setUUID(String uuid) {
        prefsEditor.putString(KEY_UUID, uuid).apply();
    }

    /**
     * 获取手机唯一标识
     *
     * @return 可能是null
     */
    public String getUUID() {
        return prefs.getString(KEY_UUID, null);
    }


    /**
     * 用户id
     *
     * @param uid
     */
    public void setUserID(int uid) {
        prefsEditor.putInt(KEY_UID, uid).apply();
    }

    /**
     * 用户id
     *
     * @return
     */
    public int getUserID() {
        return prefs.getInt(KEY_UID, 0);
    }

    /**
     * 用户手机号
     *
     * @param phone
     */
    public void setPhone(String phone) {
        prefsEditor.putString(KEY_PHONE, phone).apply();
    }

    /**
     * 用户手机号
     *
     * @return
     */
    public String getPhone() {
        return prefs.getString(KEY_PHONE, null);
    }

    /**
     * 用户生日
     *
     * @param birthday
     */
    public void setBirthday(long birthday) {
        try {
            prefsEditor.putLong(KEY_BIRTHDAY, birthday).apply();
        } catch (Exception e) {
            prefsEditor.remove(KEY_BIRTHDAY).apply();
            prefsEditor.putLong(KEY_BIRTHDAY, birthday).apply();
        }

    }

    /**
     * 用户生日
     *
     * @return
     */
    public long getBirthday() {
        try {
            return prefs.getLong(KEY_BIRTHDAY, 0);
        } catch (Exception e) {
            prefsEditor.remove(KEY_BIRTHDAY).apply();
            return 0;
        }

    }

    /**
     * 用户头像
     *
     * @param avatar
     */
    public void setAvatar(String avatar) {
        prefsEditor.putString(KEY_AVATAR, avatar).apply();
    }

    /**
     * 用户头像
     *
     * @return
     */
    public String getAvatar() {
        return prefs.getString(KEY_AVATAR, null);
    }

    /**
     * 用户性别
     *
     * @param sex
     */
    public void setSex(int sex) {
        prefsEditor.putInt(KEY_SEX, sex).apply();
    }

    /**
     * 用户性别
     *
     * @return
     */
    public int getSex() {
        return prefs.getInt(KEY_SEX, 0);
    }

    /**
     * 用户创建时间
     *
     * @param time
     */
    public void setCreateTime(long time) {
        prefsEditor.putLong(KEY_CREATE_TIME, time).apply();
    }

    /**
     * 用户创建时间
     *
     * @return
     */
    public long getCreateTime() {
        return prefs.getLong(KEY_CREATE_TIME, 0);
    }

    /**
     * 用户名字
     *
     * @param name
     */
    public void setUserName(String name) {
        prefsEditor.putString(KEY_USER_NAME, name).apply();
    }

    /**
     * 用户名字
     *
     * @return
     */
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, null);
    }

}
