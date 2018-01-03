package com.immotor.batterystation.android.bluetooth;

import com.immotor.batterystation.android.MyConfiguration;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.util.LogUtil;

import java.io.IOException;

/**
 * Created by Ashion on 2017/6/19.
 */

public class SerialManager {

    /***
     *    命令构成：
     *    [0] 0x7e  开头
     *    [1] ctrl  0表示不分包，1表示分包第一包，2表示分包最后一包，3表示分包中间包
     *    [2] cmd   命令
     *    [3] length data长度，最大248
     *    [...] data 数据
     *    [length +4, length+5] crc 校验码
     *    [length + 6] 0xff 结束
     *    构成以上一帧数据后，再将除了开头和结尾外的0x7e,0xff,0x8c转义
     */


    private static final boolean IS_SERIAL_LOG = true;

    private static final int MAX_DATA_LENGTH  = 248;   //内容最大长度
    private static final int NO_DATA_LENGTH = 7;       //空命令长度，是一帧数据的最小长度
    private static final int PACKAGE_COMPLETE = 0x00;  //不分包
    private static final int PACKAGE_INCOMPLETE_START = 0x01; //分包开始
    private static final int PACKAGE_INCOMPLETE_END = 0x02;   //分包结束
    private static final int PACKAGE_INCOMPLETE_INTER = 0x03; //分包中间

    /**
     * 初始化，上报插槽自身状态
     */
    public static final byte CMD_INIT = 0x30;


    /**
     * 解锁，APP主动发送
     */
    public static final byte CMD_UNLOCK = 0x31;

    /**
     * 请求锁状态，APP主动发送请求，返回与{@link #CMD_LOCK_STATUS}一样
     */
    public static final byte CMD_LOCK_STATUS_POLL = 0x32;

    /**
     * 请求插槽状态，APP主动请求，返回与{@link #CMD_PORT_STATUS}一样，只包含插槽是否有电池
     */
    public static final byte CMD_PORT_STATUS_POLL = 0x33;

    /**
     * 请求给指定port充电
     */
    public static final byte CMD_CHARGE_REQUEST = 0x34;

    /**
     * 查询充电状态
     */
    public static final byte CMD_CHARGE_STATUS_REQUEST = 0x35;


    /**
     * 请求停止给指定port充电
     */
    public static final byte CMD_STOP_CHARGE_REQUEST = 0x36;


    /**
     * 查询电池状态
     */
    public static final byte CMD_BATTERY_STATUS_POLL = 0x37;

    /**
     * 心跳，上报电池详情
     */
    public static final byte CMD_HEARTBEAT = 0x38;


    /**
     * 主动上报锁状态，返回与{@link #CMD_LOCK_STATUS_POLL}一样
     */
    public static final byte CMD_LOCK_STATUS = 0x39;

    /**
     * 主动上报插槽状态，返回与{@link #CMD_PORT_STATUS_POLL}一样，只包含插槽是否有电池
     */
    public static final byte CMD_PORT_STATUS = 0x3A;

    /**
     * 主动上报充电状态
     */
    public static final byte CMD_CHARGE_STATUS = 0x3B;

    /**
     * 照明灯开关
     */
    public static final byte CMD_LIGHT_CTRL = 0x40;

    /**
     * 复位电源
     */
    public static final byte CMD_RESET_SYSTEM = 0x42;

    /**
     * 读取固件信息
     */
    public static final byte CMD_DEV_STA_POLL = 0x43;

    /**
     * 获取固件系统版本
     */
    public static final byte CMD_GET_DEV_VER = 0x20;

    /**
     * 请求开始升级
     */
    public static final byte CMD_FW_UPGRATE_START = 0x21;

    /**
     * 发送升级数据
     */
    public static final byte CMD_FW_UPGRADE = 0x22;

    /**
     * 请求结束升级
     */
    public static final byte CMD_FW_UPGRADE_DONE = 0x23;

    /**
     * 上报固件状态
     */
    public static final byte CMD_DEV_STA = 0x48;

    /**
     * 透传指令
     */
    public static final byte CMD_BYPASS = 0x27;
    private boolean IS_SERIAL_UTIL_LOG=true;


    public interface IOnReceiveSerialDataListener{
        /**
         * 这里不带数据，数据去LocalDataManager取
         * @param cmd
         */
        void onReceive(byte cmd);
    }

    private IOnReceiveSerialDataListener serialListener;

    public void setSerialDataListener(IOnReceiveSerialDataListener listener){
        serialListener = listener;
    }

    public void removeSerialDataListener(IOnReceiveSerialDataListener listener){
        if(serialListener == listener){
            serialListener = null;
        }
    }

    private SerialManager(){}
    private static SerialManager _instance =  null;
    public static SerialManager getInstance(){
        if(_instance==null){
            _instance = new SerialManager();
        }
        return _instance;
    }


    public boolean sendUpdate(byte cmd, byte[] data){
        return sendCmdReal(cmd, data);
    }

    public boolean sendCmd(byte cmd, byte[] data){
        if(AppConstant.update_status == 0){
            //没有在升级，才可以传
            return sendCmdReal(cmd,data);
        }
        return false;
    }
    /**
     * 发送命令
     * @param cmd
     * @param data  原始数据，可以为null
     * @return
     */
    private synchronized boolean sendCmdReal(byte cmd, byte[] data){
        int length = 0;
        if(data!=null){
            length = data.length;
        }
        if(length > MAX_DATA_LENGTH){
            int packNum = (length+MAX_DATA_LENGTH-1)/MAX_DATA_LENGTH;
            for(int i = 0; i < packNum;i++){
                int status;
                byte[] packData;
                if(i == 0){
                    status = PACKAGE_INCOMPLETE_START;
                    packData = new byte[MAX_DATA_LENGTH];
                    System.arraycopy(data,0,packData,0,MAX_DATA_LENGTH);
                }else if(i == packNum-1){
                    status = PACKAGE_INCOMPLETE_END;
                    int lastLength = length%MAX_DATA_LENGTH;
                    lastLength = ((lastLength==0)?MAX_DATA_LENGTH:lastLength); //如果整个数据是MAX_DATA_LENGTH的倍数，则余数为0
                    packData = new byte[lastLength];
                    System.arraycopy(data,i*MAX_DATA_LENGTH,packData,0,lastLength);
                }else{
                    status = PACKAGE_INCOMPLETE_INTER;
                    packData = new byte[MAX_DATA_LENGTH];
                    System.arraycopy(data,i*MAX_DATA_LENGTH,packData,0,MAX_DATA_LENGTH);
                }
                if(!sendCmdInternal(cmd,status , packData)){  //有一帧出错，认为全部出错
                    return false;
                }
            }
            return true;
        }else{
            if(IS_SERIAL_LOG) {
                LogUtil.d("whj serial send cmd = " + cmd);
            }
            return sendCmdInternal(cmd, PACKAGE_COMPLETE, data);
        }

    }

    /**
     * 发送拆分好的命令
     * @param cmd
     * @param status
     * @param data  拆分好的数据
     * @return
     */
    private boolean sendCmdInternal(byte cmd, int status, byte[] data){
        int length = data==null?0:data.length;
        if(length > MAX_DATA_LENGTH){
            if(IS_SERIAL_LOG)
            LogUtil.d("serial send too much, need split");
            return false;
        }
        if(status!=PACKAGE_COMPLETE && status!=PACKAGE_INCOMPLETE_START && status!=PACKAGE_INCOMPLETE_END && status!=PACKAGE_INCOMPLETE_INTER){
            if(IS_SERIAL_LOG)
            LogUtil.d("serial send status is error");
        }
        byte[] sendData = new byte[length+NO_DATA_LENGTH];
        sendData[0] = 0x7e;
        sendData[1] = (byte)status;
        sendData[2] = cmd;
        sendData[3] = (byte)length;
        if(length > 0){
            System.arraycopy(data, 0, sendData, 4, length);
        }
        sendData[length + NO_DATA_LENGTH - 1] = (byte)0xff;
        byte[] crc = NumberBytes.shortToBytes(calculateCRC(sendData));
        System.arraycopy(crc,0, sendData, length + NO_DATA_LENGTH - 3,2);
        return sendOneFrame(sendData);
    }

    /**
     * 发送一帧数据
     * @param frame  一帧原始数据
     * @return
     */
    private boolean sendOneFrame(byte[] frame){
    /*    if(!SerialPortUtil.getInstance().isStart()){
            if(IS_SERIAL_LOG){
                LogUtil.d("serial port is not start");
            }
            return false;
        }*/
        //若不以0x7e开头，不已0xff结尾，则认为不是一则合格的数据，不发送
        if(frame==null || frame.length < NO_DATA_LENGTH || frame[0]!=0x7e || frame[frame.length-1]!=(byte)0xff){
            if(IS_SERIAL_LOG){
                LogUtil.d("serial data is not a correct frame");
            }
            return false;
        }
        //转义，需要将除了首尾的0x7e,0xff,0x8c转义
        byte[] convert = new byte[frame.length *2];
        convert[0] = frame[0];
        int i = 1, j = 1;

        for(i = 1; i < frame.length-1; i++){
            if(frame[i]==(byte)0x7e){
                convert[j] = (byte)0x8c;
                convert[j+1] = (byte)0x81;
                j+=2;
            }else if(frame[i]==(byte)0xff){
                convert[j] = (byte)0x8c;
                convert[j+1] = 0x00;
                j+=2;
            }else if(frame[i]==(byte)0x8c){
                convert[j] = (byte)0x8c;
                convert[j+1] = 0x73;
                j+=2;
            }else{
                convert[j] = frame[i];
                j++;
            }
        }
        convert[j] = frame[i];
        byte[] sendData = new byte[j+1];

        System.arraycopy(convert, 0, sendData, 0, j+1);

        return false;
    }

    /**
     * 计算校验码
     * @param onFrame
     * @return
     */
    private short calculateCRC(byte[] onFrame){
        short value = 0;
        for(int i = 1;i < onFrame.length-3;i++){
//            value += (onFrame[i] + (onFrame[i]<0?256:0));
            value += NumberBytes.byteToInt(onFrame[i]);
        }
        return value;
    }

    private byte[] tempData = new byte[1024];
    private int tempLength = 0;

    public synchronized void onReceiveData(byte[] originData, int size){
        if(tempLength+size> tempData.length){
            if(IS_SERIAL_LOG)
            LogUtil.d("serial data is wrong size");
            tempLength = 0;
            return;
        }
        System.arraycopy(originData,0, tempData, tempLength, size);
        tempLength+=size;
        analysis();
        //应该是出错了
        if(tempLength>512){
            tempLength = 0;
        }
    }
    private void analysis(){
        int start = 0;
        int end = 0;
        while(start < tempLength){
            //先找到头
            if(tempData[start]!=0x7e){
                start++;
            }else{
                //找最近的尾
                for(int i = start+1; i < tempLength;i++){
                    if(tempData[i]==(byte)0xff){
                        end = i;
                        break;  //跳出for循环
                    }
                }
                if(end > start){  //有头有尾，是一帧
//                    if(IS_SERIAL_LOG)
//                    LogUtil.d("serial find one frame start = "+start+", end = " + end);
                    byte[] oneFrame = new byte[end - start + 1];
                    System.arraycopy(tempData,start, oneFrame, 0, end - start + 1);
                    decodeOneFrame(oneFrame);
                    start = end+1;
                }else{  //最后一个有头没尾，不完全，也不用再找了
                    break;  //跳出while循环
                }
            }
        }
        if(end > 0){  //将处理过的移除
            System.arraycopy(tempData,end+1, tempData, 0, tempLength - end - 1);
            tempLength = tempLength - end - 1;
        }

    }

    /**
     * 解析一帧数据，主要是转码和校验
     * @param oneFrame  未转码之前的数据
     */
    private void decodeOneFrame(byte[] oneFrame){
        if(oneFrame == null || oneFrame.length < NO_DATA_LENGTH ||
                oneFrame[0]!=0x7e || oneFrame[oneFrame.length-1]!=(byte)0xff || //判断头尾
                oneFrame[oneFrame.length - 2]==(byte)0x8c){   //倒数第二个字符不能是转义
            if(IS_SERIAL_LOG)
            LogUtil.d("serial 解析帧失败，可能长度不够，首尾不对，或倒数第二帧byte是0x8c，不能转义");
            return;
        }
        byte[] convert = convertReceiveFrame(oneFrame);
        if(convert==null || convert.length < NO_DATA_LENGTH){
            if(IS_SERIAL_LOG)
            LogUtil.d("serial 转义失败");
            return;
        }
        byte[] crc = NumberBytes.shortToBytes(calculateCRC(convert));
        if(crc[0] == convert[convert.length - 3] && crc[1] == convert[convert.length - 2]){
            int length = NumberBytes.byteToInt(convert[3]);
            if(length == convert.length - NO_DATA_LENGTH){
                receiveOneIntactFrame(convert);
            }else{
                if(IS_SERIAL_LOG)
                LogUtil.d("serial dataLength = "+length+", 实际长度 = "+(convert.length-NO_DATA_LENGTH));
            }
        }else{
            if(IS_SERIAL_LOG) {
                LogUtil.d("serial 校验失败");
                LogUtil.d(NumberBytes.bytesToHexString(oneFrame));
            }
        }

    }

    /**
     * 转义接收的数据，若转义失败，则返回null
     * @param receive
     * @return
     */
    private byte[] convertReceiveFrame(byte[] receive){
        byte[] data = new byte[receive.length];
        int j = 1;
        data[0] = receive[0];
        for(int i = 1; i < receive.length-1; i++){
            if(receive[i] == (byte)0x8c){
                if(receive[i+1]==(byte)0x81){
                    i++;
                    data[j] = 0x7e;
                    j++;
                }else if(receive[i+1] == (byte)0x00){
                    i++;
                    data[j] = (byte)0xff;
                    j++;
                }else if(receive[i+1]==(byte)0x73){
                    i++;
                    data[j] = (byte)0x8c;
                    j++;
                }else{
                    return null;  //转义失败
                }
            }else{
                data[j] = receive[i];
                j++;
            }
        }
        data[j] = receive[receive.length-1];
        if(j == receive.length - 1) {  //没转义
            return data;
        }else{
            byte[] convert = new byte[j+1];
            System.arraycopy(data,0,convert,0,j+1);
            return convert;
        }
    }

    private byte lastIncompleteCmd;    //最近分包命令
    private int incompleteLength = 0;  //分包数据已接受长度
    private byte[] incompleteBuffer = new byte[10240];  //分包数据缓存
    /**
     * 接收到一帧
     * @param oneFrame 一帧转义后校验过的正确数据
     */
    private void receiveOneIntactFrame(byte[] oneFrame){

        if(oneFrame == null || oneFrame.length <NO_DATA_LENGTH){
            return;
        }
        int status = NumberBytes.byteToInt(oneFrame[1]);
        byte cmd = oneFrame[2];
        int dataLength = NumberBytes.byteToInt(oneFrame[3]);
        byte[] data = null;
        if(IS_SERIAL_LOG)
        LogUtil.d("serial receive one Intact frame cmd = " + cmd+", status = "+status + ", dataLength = " + dataLength);

        if(status == PACKAGE_INCOMPLETE_START){  //分包数据第一包，缓存下数据就返回
            lastIncompleteCmd = cmd;
            System.arraycopy(oneFrame, 4, incompleteBuffer, 0, dataLength);
            incompleteLength = dataLength;
            return;
        }else if(status == PACKAGE_INCOMPLETE_INTER){  //分包数据中间包，缓存下数据就返回
            if(lastIncompleteCmd!=cmd){
                return;
            }
            System.arraycopy(oneFrame,4,incompleteBuffer,incompleteLength,dataLength);
            incompleteLength += dataLength;
            return;
        }else if(status == PACKAGE_INCOMPLETE_END){   //分包数据最后一包，需要处理数据
            if(lastIncompleteCmd!=cmd){
                return;
            }
            System.arraycopy(oneFrame,4,incompleteBuffer,incompleteLength,dataLength);
            incompleteLength+=dataLength;
            data = new byte[incompleteLength];
            System.arraycopy(incompleteBuffer,0,data,0, incompleteLength);
            lastIncompleteCmd = 0;
            incompleteLength = 0;
        }else if(status == PACKAGE_COMPLETE){  //完整数据，需要处理数据
            if(dataLength > 0){
                data = new byte[dataLength];
                System.arraycopy(oneFrame,4,data,0,dataLength);
            }
        }else{
            if(IS_SERIAL_LOG)
            LogUtil.d("serial receive an unknown status "+status);
            return;
        }


        if(serialListener!=null){
            serialListener.onReceive(cmd);
        }
    }
}
