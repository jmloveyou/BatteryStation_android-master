package com.immotor.batterystation.android.util;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Ashion on 2017/5/16.
 */

public class FileUtil {
    public static String getServerFileName(String serverPath){
        String result = serverPath.substring(serverPath.lastIndexOf("/")+1);
        return result;
    }


    public static boolean saveBitmapFile(Bitmap bitmap, String filePath){
        boolean result = false;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            File file = new File(filePath);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
                result = true;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

}
