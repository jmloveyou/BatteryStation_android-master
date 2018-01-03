package com.immotor.batterystation.android.bluetooth;

import android.text.TextUtils;

/**
 * Created by Ashion on 2016/6/17.
 */
public class StringUtils {

    /**
     * format the string to expect len, if less then expect len, add 0 to the start
     *
     * @param str
     * @param len
     * @return
     */
    public static String formatStringToLength(String str, int len) {
        if (TextUtils.isEmpty(str) || len <= 0) {
            return str;
        }
        int strlen = str.length();
        if (strlen > len) {
            return str.substring(0, (len - 1));
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len - strlen; i++) {
                sb.append("0");
            }
            sb.append(str);
            return sb.toString();
        }
    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String bytesToHexStringData(byte[] data){
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            return stringBuilder.toString();
        }else {
            return null;
        }
    }

    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String reverseString2(String s) {
        int length = s.length();
        String reverse = "";
        if (length > 0) {
            reverse = s.substring(length - 2);
            String leftStr = s.substring(0, length - 2);
            reverse =  reverse + reverseString2(leftStr);
        }
        return reverse;
    }

    private boolean compareString(String str1, String str2){

        int result =  str1.compareTo(str2);
        return result > 0;
    }



    public static void main(String[] args){

        /*String str1 = "1.0.24";
        String str2 = "1.0.23";

        int result = str1.compareTo(str2);

        System.out.print(result);*/


    }

}
