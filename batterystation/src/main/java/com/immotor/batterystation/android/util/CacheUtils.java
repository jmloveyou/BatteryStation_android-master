package com.immotor.batterystation.android.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Created by jm on 2017/7/21 0021.
 */

public class CacheUtils {

    public static String StringFilter( String str ) {
        // 只允许字母和数字
        // String regEx = "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile( regEx );
        Matcher m = p.matcher( str );
        return m.replaceAll( "" ).trim( );
    }
}
