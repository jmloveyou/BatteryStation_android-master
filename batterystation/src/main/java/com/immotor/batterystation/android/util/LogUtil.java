package com.immotor.batterystation.android.util;

/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.util.Log;


import com.immotor.batterystation.android.MyConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Log工具，类似android.util.Log。
 * tag自动产生，格式: customTagPrefix:className.methodName(L:lineNumber),
 * customTagPrefix为空时只输出：className.methodName(L:lineNumber)。
 * Author: wyouflf
 * Date: 13-7-24
 * Time: 下午12:23
 */
public class LogUtil {

    public static final String customTagPrefix = "Scooter Log";

    private LogUtil() {
    }

    private static String generateTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        return callerClazzName;
    }

    private static String generatePrefix() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String prefix = "%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        prefix = String.format(prefix, caller.getMethodName(), caller.getLineNumber());
        return prefix;
    }

    public static void d() {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.d(tag, prefix);
    }

    public static void d(String content) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.d(tag, prefix + " " + content);
    }

    public static void d(String content, Throwable tr) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.d(tag, prefix + " " + content, tr);
    }

    public static void e(String content) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.e(tag, prefix + " " + content);
    }

    public static void e(String content, Throwable tr) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.e(tag, prefix + " " + content, tr);
    }

    public static void i(String content) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.i(tag, prefix + " " + content);
    }

    public static void i(String content, Throwable tr) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.i(tag, prefix + " " + content, tr);
    }

    public static void v(String content) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.v(tag, prefix + " " + content);
    }

    public static void v(String content, Throwable tr) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.v(tag, prefix + " " + content, tr);
    }

    public static void w(String content) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.w(tag, prefix + " " + content);
    }

    public static void w(String content, Throwable tr) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.w(tag, prefix + " " + content, tr);
    }

    public static void w(Throwable tr) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();

        Log.w(tag, tr);
    }


    public static void wtf(String content) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.wtf(tag, prefix + " " + content);
    }

    public static void wtf(String content, Throwable tr) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();
        String prefix = generatePrefix();

        Log.wtf(tag, prefix + " " + content, tr);
    }

    public static void wtf(Throwable tr) {
        if (!MyConfiguration.isDebug()) return;
        String tag = generateTag();

        Log.wtf(tag, tr);
    }
//
//    public static void f(String content){
//        if (!MyConfiguration.isDebug()) return;
//        if(logFile!=null){
//            try {
//                FileOutputStream stream = new FileOutputStream(logFile, true);
//                try {
//                    String s = DateTimeUtils.getTimeString(System.currentTimeMillis()) + ": " + content + "\r\n";
//                    stream.write(s.getBytes());
//                    stream.flush();
//                    stream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private static StringBuffer sCache = new StringBuffer();
//    private static int total = 0;
//    public static void fc(String content){
//        sCache.append(DateTimeUtils.getDateTimeString("MM-dd HH:mm:ss",System.currentTimeMillis())).append(": ")
//        .append(content).append("\r\n");
//        total++;
//        if(total > 1){
//            if(logFile!=null){
//                try {
//                    FileOutputStream stream = new FileOutputStream(logFile, true);
//                    try {
//                        stream.write(sCache.toString().getBytes());
//                        stream.flush();
//                        stream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//            sCache.setLength(0);
//            total = 0;
//        }
//    }
//
//    static File logFile;
//    static{
//        String fileName = MyConfiguration.APP_FILE_PATH+"log"+File.separator + "log.txt";
//        logFile = new File(fileName);
//        if(!logFile.exists()){
//            File path = new File(MyConfiguration.APP_FILE_PATH+"log"+File.separator);
//            path.mkdirs();
//            try {
//                logFile.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//                logFile = null;
//            }
//        }
//
//    }
}

