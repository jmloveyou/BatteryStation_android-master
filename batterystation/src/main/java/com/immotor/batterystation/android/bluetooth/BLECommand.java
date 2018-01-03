package com.immotor.batterystation.android.bluetooth;

import com.immotor.batterystation.android.util.DateTimeUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by Ashion on 2016/8/4.
 */
public class BLECommand {

    public static final byte command_01 = 0x01; //鉴权
    public static final byte command_02 = 0x02; //到此命令将会清空已有的蓝牙记录
    public static final byte command_03 = 0x03; //获取车辆信息
    public static final byte command_04 = 0x04; //获取电池信息
    public static final byte command_05 = 0x05; //锁动作
    public static final byte command_06 = 0x06; //车辆模式切换
    public static final byte command_07 = 0x07; //获取体检结果
    public static final byte command_08 = 0x08; //车灯操作
    public static final byte command_09 = 0x09; //设置车灯、限速
    public static final byte command_0A = 0x0A; //设置车的蓝牙密码
    public static final byte command_0B = 0x0B; //获取车的蓝牙密码
    public static final byte command_0C = 0x0C; //事件指示
    public static final byte command_55 = 0x20; //升级开始  0x55
    public static final byte command_21 = 0x21; //升级发送数据
    public static final byte command_22 = 0x22; //AppRom Update Done
    public static final byte command_23 = 0x23; //MCU Reset
    public static final byte command_24 = 0x24;  //文件传输开始
    public static final byte command_25 = 0x25;  //文件传输内容
    public static final byte command_26 = 0x26;  //文件传输结束
    public static final byte command_27 = 0x27;  //获取file id
    public static final byte command_28 = 0x28;  //获取大灯亮度

    public static final byte command_2C = 0x2c;  //设置时间


    public static final byte command_A0 = (byte)0xA0; // 行驶中状态上报(0xA0)
    public static final byte command_A1 = (byte) 0xA1; // 故障上报(0xA1)
    public static final byte command_A2 = (byte) 0xA2; // 拍照按键触发(0xA1)

    public static final byte Upper_A = 0x41;      //字母A
    public static final byte Upper_T = 0x54;      //字母T
    public static final byte Symbol_add = 0x2B;   //符号+

    public static final byte code_00 = 0x00;  // SUCCESS 成功
    public static final byte code_01 = 0x1A;  //获取pms板子信息
    public static final byte code_02 = 0x02;  // ERR_NA_AUTH  未授权
    public static final byte code_03 = 0x03;  // 获取电池数据
    public static final byte code_04 = 0x04;  //
    public static final byte code_05 = 0x05;  // ERR_USERID_LEN_INVALID UserID 长度非法
    public static final byte code_06 = 0x06;  // ERR_BACKUP_POWER_LACK  备用电池将耗尽
    public static final byte code_07 = 0x07;  // ERR_POWER_UNPLUG 动力电池被拔出
    public static final byte code_08 = 0x08;  // ERR_DRIVING_HOLD 车辆行驶中，无法加锁
    public static final byte code_09 = 0x09;  // ERR_TESTING_HOLD 正在体检，无法切换模式
    public static final byte code_0A = 0x0A;  // ERR_TEST_RESULT 体检有问题
    public static final byte code_0B = 0x0B;  // ERR_USERID_INVALID UserID 无效

    public static final byte code_0C = 0x0C;  //  事件指示

    public static final byte code_10 = 0x10;  // ERR_PARA_INVALID 参数无效
    public static final byte code_11 = 0x11;  // ERR_PERMISSION 非车主，无权限
    public static final byte code_19 = 0x19;  // 获取固件信息
    public static final byte code_FF = (byte) 0xFF;  // ERROR 未知错误

    public static HashMap errorCodeMap;
    static {
        errorCodeMap = new HashMap();
        errorCodeMap.put(code_00, "SUCCESS");
        errorCodeMap.put(code_02, "ERR_NA_AUTH");
        errorCodeMap.put(code_05, "ERR_USERID_LEN_INVALID");
        errorCodeMap.put(code_06, "ERR_BACKUP_POWER_LACK");
        errorCodeMap.put(code_07, "ERR_POWER_UNPLUG");
        errorCodeMap.put(code_08, "ERR_DRIVING_HOLD");
        errorCodeMap.put(code_09, "ERR_TESTING_HOLD");
        errorCodeMap.put(code_0A, "ERR_TEST_RESULT");
        errorCodeMap.put(code_0B, "ERR_USERID_INVALID");
        errorCodeMap.put(code_10, "ERR_PARA_INVALID");
        errorCodeMap.put(code_11, "ERR_PERMISSION");
        errorCodeMap.put(code_FF, "ERROR");



    }
    /**
     * Payload: MD5密钥, 长度为16的字符串，通过和本地密钥比较来决定鉴权通过与否，
     本地MD5,有2组，车主：MD5（BTMAC+immotor000）
     Guest：MD5（BTMAC+immotor001）
     * @param key
     * @return
     */
    public static byte[] authentication(String key){

        byte[] result = new byte[18];
        result[0] = command_01;
        result[1] = 0x12;
        byte[] keyBytes = key.getBytes();

        System.arraycopy(keyBytes, 0, result, 2, keyBytes.length);

        return result;
    }


    /**
     *收到该命令，中控会清空之前的所有配对记录（不包括BLE）
     * @return
     */
    public static byte[] cleanPairedRecord(){

        byte[] result = new byte[3];
        result[0] = command_02;
        result[1] = 0x3;
        result[2] = 0x00;

        return result;
    }

    /**
     *3.3.	获取车辆信息
     * @return
     */
    public static byte[] getScooterInfo(){

        byte[] result = new byte[2];
        result[0] = command_03;
        result[1] = 0x2;

        return result;
    }

    /**
     *3.3.	获取车辆信息
     * @return
     */
    public static byte[] getBatteryInfo(int index){
        byte[] result = new byte[3];
        result[0] = command_04;
        result[1] = 0x3;
        if (index == 1){
            result[2] = 0x01;
        }else if (index == 2){
            result[2] = 0x02;
        }
        return result;
    }

    /**
     *3.5.	加解锁命令
     * Lock Command:   0x01: Lock
     \   0x02: unlock
     * @return
     */
    public static byte[] lockOrUnlock(boolean isLock){
        byte[] result = new byte[3];
        result[0] = command_05;
        result[1] = 0x3;
        if (isLock){
            result[2] = 0x01;       //lock
        }else {
            result[2] = 0x00;       // unlock
        }
        return result;
    }


    /**
     *3.6.	车辆模式切换
     * mode type:
     0x01: 正常模式
     0x02: 定时上报心跳模式
     0x03: 体检模式
     Param: 当mode type == 0x02时，可设置为心跳间隔：单位0.1秒
     * @return
     */
    public static byte[] modelSwitch(int type, int param){
        byte[] result = new byte[4];
        result[0] = command_06;
        result[1] = 0x4;
        if (type == 1){
            result[2] = 0x01;
        }else if (type == 2){
            result[2] = 0x02;
        }
        result[3] = 0x00;
        return result;
    }

    /**
     *3.7.	获取体检结果命令
     * @return
     */
    public static byte[] getDiagnoseResult(){
        byte[] result = new byte[2];
        result[0] = command_07;
        result[1] = 0x02;
        return result;
    }

    /**
     *3.8.	车灯开关命令
     mode type:   0x01: ON
                  0x02: OFF
     * @return
     *//*
    public static byte[] lightOnOff(int frontType, int atmosphere){
        byte[] result = new byte[4];
        result[0] = command_08;
        result[1] = 0x4;
        if (frontType == 0){
            result[2] = 0x00;
        }else if (frontType == 1){
            result[2] = 0x01;
        }

        if (atmosphere == 0) {
            result[3] = 0x00;
        }else {
            result[3] = 0x01;
        }
        return result;
    }*/

    /**
     *3.9.	车辆设置(氛围灯索引、最大速、灯光自动开启，蓝牙自动解锁)
     0x01	自动灯光开关	1	0：关 1：开
     0x02	蓝牙自动解锁	1	0：关 1: 开
     0x03	最大限速	    2	字节1：mode1最大限速 字节2：mode2最大限速
     0x04	氛围灯方案索引	1	最大1-5， 0为默认
     0x05   零速度启动      1   0x00 零速启动  0x01 非零速启动
     0x06   大灯开关        1   0x00  OFF 关  0x01 ON 开

     * @return
     */
    public static byte[] scooterSetting(int type, int length, int value, int value2){
        int len = 4+length;
        byte[] result = new byte[len];
        result[0] = command_09;
        result[1] = (byte)len;
        result[2] = (byte)type;
        result[3] = (byte)length;
        if (length == 2){
            result[5] = (byte)value2;
        }
        result[4] = (byte)value;
        return result;
    }


    /**
     *3.10.	设置车的蓝牙密码
     * Password string: 蓝牙密码的字符串，固定4位数字。
     * @return
     */
    public static byte[] setBluetoothPassword(byte[] password){
        int len = 2 + password.length;
        byte[] result = new byte[len];
        result[0] = command_0A;
        result[1] = (byte)len;
        System.arraycopy(password, 0, result, 2, password.length);
        return result;
    }


    /**
     *3.11.	获取车的蓝牙密码
     * 获得车的蓝牙的密码，用户可通过手机查看到。
     * @return
     */
    public static byte[] getBluetoothPassword(){
        int len = 2;
        byte[] result = new byte[len];
        result[0] = command_0B;
        result[1] = (byte)len;
        return result;
    }

    /**
     *3.17.获取车的固件信息
     * @return
     */
    public static byte[] getDeviceID() {

        byte[] result = new byte[2];
      //  result[0] = command_55;
        result[0] = (byte)0x19;
        result[1] = (byte) 0x02;
    //    result[3] = (byte) 0xAA;
        return result;
    }

    /**
     *获取车的信息
     * @return
     */
    public static byte[] getPms() {

        byte[] result = new byte[2];
        //  result[0] = command_55;
        result[0] = (byte) 0x1A;
        result[1] = (byte) 0x02;
        //    result[3] = (byte) 0xAA;
        return result;
    }


    /**
     * 获取车内的文件id
     * @param fileType 文件类型
     * @return
     */
    public static byte[] getFileId(byte fileType){
        byte[] result = new byte[3];
        result[0] = command_27;
        result[1] = 0x03;
        result[2] = fileType;
        return result;
    }

    public static byte[] requestTransfer(byte fileType, short fileId, int length){
        byte[] rlt = new byte[9];
        rlt[0] = command_24;
        rlt[1] = 0x09;
        rlt[2] = fileType;
        System.arraycopy(NumberBytes.shortToBytes(fileId),0,rlt,3,2);
        System.arraycopy(NumberBytes.intToBytes(length),0,rlt,5,4);
        return rlt;
    }

    /**
     *3.12.	固件更新开始请求smart。
     * @return
     */
    public static byte[] firmwareUpdateStart(byte[] lengthBytes, byte[] fmVersionBytes){
        int len = 17;
        byte[] result = new byte[len];
        result[0] = (byte)0x7E;
        result[1] = (byte)0x20;
        result[2] = (byte)0x0D;
        result[3] = (byte)0x00;
        result[4] = (byte)0x00;
        System.arraycopy(lengthBytes, 0, result, 5, 4);
        System.arraycopy(fmVersionBytes, 0, result, 9, 7);
        byte[] endByte = new byte[]{(byte) 0xFF};
        System.arraycopy(endByte, 0, result, 16, 1);
        return result;
    }
    /**
     *3.12.	pms。
     * @return
     */
    public static byte[] pmsUpdateStart(byte[] lengthBytes, byte[] fmVersionBytes){
        int len = 17;
        byte[] result = new byte[len];
        result[0] = (byte)0x7E;
        result[1] = (byte)0x20;
        result[2] = (byte)0x0D;
        result[3] = (byte)0x01;
        result[4] = (byte)0x00;
        System.arraycopy(lengthBytes, 0, result, 5, 4);
        System.arraycopy(fmVersionBytes, 0, result, 9, 7);
        byte[] endByte = new byte[]{(byte) 0xFF};
        System.arraycopy(endByte, 0, result, 16, 1);
        return result;
    }

    private static byte[] convertData(byte[] data){

        byte[] rltData = new byte[data.length*2];
        int newIndex = 0;
        for(int i = 0; i < data.length;i++){
            if(data[i]==0x7e){
                rltData[newIndex]=(byte)0x8c;
                newIndex++;
                rltData[newIndex]=(byte)0x81;
            }else if(data[i]==(byte)0xff){
                rltData[newIndex]=(byte)0x8c;
                newIndex++;
                rltData[newIndex]=(byte)0x00;
            }else if(data[i]==(byte)0x8c){
                rltData[newIndex]=(byte)0x8c;
                newIndex++;
                rltData[newIndex]=(byte)0x73;
            }else{
                rltData[newIndex] = data[i];
            }
            newIndex++;
        }
        byte[] finalData = new byte[newIndex];
        System.arraycopy(rltData,0,finalData,0,newIndex);
        return finalData;
    }

    /**
     *3.13.	固件更新进行中请求。
     * @return
     */
    public static byte[] firmwareUpdating(byte[] fileData, byte[] offset){
        byte[] data = new byte[fileData.length+4+1];
        data[0]= (byte) (fileData.length+4);
        System.arraycopy(offset,0,data,1,offset.length);
        System.arraycopy(fileData,0,data,5,fileData.length);
        byte[] newData = convertData(data);

        byte[] sendData = new byte[newData.length+3];
        sendData[0] = 0x7E;
        sendData[1] =(byte)0x21;
        System.arraycopy(newData, 0, sendData, 2, newData.length);
        sendData[sendData.length-1] = (byte)0xff;
        return sendData;
        /*int len = fileData.length + 8;
        byte[] result = new byte[len];
        result[0] = command_55;
        result[1] = (byte)0x21;
        result[2] = (byte)(len-2);

        System.arraycopy(offset, 0, result, 3, 4);
        System.arraycopy(fileData, 0, result, 7, fileData.length);
        byte[] endByte = new byte[]{(byte) 0xAA};
        System.arraycopy(endByte, 0, result, len-1, 1);

        return result;*/
    }

    /**
     *3.14.固件更新结束请求
     * @return
     */
    public static byte[] firmwareUpdateDone(byte[] binDataRcr){
        int len = 8;
        byte[] result = new byte[len];
        result[0] = (byte)0x7E;
        result[1] = (byte)0x22;
        result[2] = (byte)0x04;
        result[7] = (byte) 0xFF;
        System.arraycopy(binDataRcr, 0, result, 3, 4);
        return result;
    }

    /**
     *3.15.固件复位请求
     * @return
     */
    public static byte[] firmwareUpdateRest(){
        byte[] result = new byte[3];
        result[0] = (byte)0x23;
        result[1] = (byte)0x03;
        result[2] = (byte)0x01;
        return result;
    }



    /**
     *3.1行驶中状态上报
     * @return
     */
    public static Map<String, Object> parseScooterStatus(byte[] params){
        Map<String, Object> result = new HashMap<>();

        if (params != null && params.length == 5) {
            result.put("Speed", params[2]);
            result.put("Battery", params[3]);
            result.put("Trip", params[4]);
        }
        return result;
    }

    /**
     *4.2	故障上报
     0x01: 电量低
     0x02: GPS无法定位
     0x03: 网络连接不上
     0x04: 通讯故障, 与动力系统

     * @return
     */
    public static Map<String, Object> parseScooterError(byte[] params){
        Map<String, Object> result = new HashMap<>();

        if (params != null && params.length == 3) {
            if (params[2] == 0x01){
                result.put("error", "power_low");
            }else if (params[2] == 0x02){
                result.put("error", "gps_lose");
            }else if (params[2] == 0x03){
                result.put("error", "network");
            }else if (params[2] == 0x04){
                result.put("error", "connection");
            }

        }
        return result;
    }

    /**
     *4.3	拍照按键触发
     * @return
     */
    public static Map<String, Object> getScooterCapture(byte[] params){
        Map<String, Object> result = new HashMap<>();

        if (params != null && params.length == 3) {
            if (params[2] == 0x00) {
                result.put("mode", "normal");
            }else if (params[2] == 0x01) {
                result.put("mode", "continue");
            }else {
                result.put("mode", "video");
            }
        }
        return result;
    }

    /**
     *4.2.终止故障上报/拍照
     * @return
     */
    public static byte[] stopScooterNotify(){
        int len = 3;
        byte[] result = new byte[len];
        result[0] = command_A1;
        result[1] = (byte)len;

        return result;
    }

    /**
     *4.3.终止拍照
     * @return
     */
    public static byte[] stopCaptureNotify(){
        int len = 3;
        byte[] result = new byte[len];
        result[0] = command_A2;
        result[1] = (byte)len;

        return result;
    }

    /**
     *3.12.事件指示(0x0C)
     * @return
     */
    public static byte[] sendEventCommand(byte evenId, byte value){
        int len = 4;
        byte[] result = new byte[len];
        result[0] = command_0C;
        result[1] = (byte)len;
        result[2] = evenId;
        result[3] = value;
        return result;
    }

    public static byte[] setDateTimeCmd(){
        byte[] result = new byte[8];
        result[0] = command_2C;
        result[1] = 0x08;
        long current = System.currentTimeMillis();
        String date = DateTimeUtil.getDateString(current);
        int year = Integer.parseInt(date.substring(2,4));
        int month = Integer.parseInt(date.substring(5,7));
        int day = Integer.parseInt(date.substring(8));
        String time = DateTimeUtil.getTimeString(current);
        int hour = Integer.parseInt(time.substring(0,2));
        int minute = Integer.parseInt(time.substring(3,5));
        int second = 0;
        result[2] = (byte)year;
        result[3] = (byte)month;
        result[4] = (byte)day;
        result[5] = (byte)hour;
        result[6] = (byte)minute;
        result[7] = (byte)second;
        return result;
    }

    /****************************** for accessories device **********************/

    /**
    当APP收到Dock状态更新通知后，需要响应回Dock，否则Dock会一直发通知
     */
    public static byte[] getAccessoriesUpdate(){

        byte[] result = new byte[8];
        result[0] = 0x7e;
        result[1] = 0x00;
        result[2] = 0x00;
        result[3] = (byte) 0x80;
        result[4] = 0x00;
        result[5] = (byte) 0x80;
        result[6] = 0x00;
        result[7] = (byte) 0xFF;

        return result;
    }


    /**
     * 获取外设灯信息
     * @return
     */
    public static byte[] getLightStatus(){
        byte[] result = new byte[5];
        result[0] = 0x7e;
        result[1] = 0x02;
        result[2] = 0x01;
        result[3] = 0x03;
        result[4] = (byte) 0xff;
        return result;
    }

    /**
     * 设置外设灯状态
     * @return
     */
    public static byte[] setLightCommand(byte type, byte value){
        byte[] result = new byte[7];
        result[0] = 0x7e;
        result[1] = 0x04;
        result[2] = 0x02;
        result[3] = type;
        result[4] = value;
        result[5] = (byte) (result[1] + result[2] + result[3] + result[4]);
        result[6] = (byte) 0xff;
        return result;
    }

    /************************************************ parse response data *************************/





//    public static void main(String[] args) {
//        BLECommand command = new BLECommand();
//
//        System.out.println(command.authentication("1234567890123456"));
//    }

}
