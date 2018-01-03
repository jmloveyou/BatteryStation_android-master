package com.immotor.batterystation.android.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.widget.ImageView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.immotor.batterystation.android.MyConfiguration;
import com.immotor.batterystation.android.entity.ScooterBLEStatus;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * Created by Ashion on 2016/6/13.
 */
public class Common {


    public static String SERVICE_NOTIFICATION = "service_notification";
    public static String SERVICE_BORROW_NOTIFICATION = "service_borrow_notification";
    public static String SERVICE_ERROR_NOTIFICATION = "service_error_notification";

    public static int mapCircleStrokeColor = Color.argb(100, 233, 69, 11);
    public static int mapCircleFillColor = Color.argb(18, 233, 69, 11);

    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = new PackageInfo();
        try {
            PackageManager manager = context.getPackageManager();
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * create QR code with string
     * @param str string in QR Code
     * @return QR code bitmap
     */
    public static Bitmap createQRCodeBitmap(String str) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 300, 300);

            // 使用 ZXing Android Embedded 要写的代码
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bmp = barcodeEncoder.createBitmap(matrix);

            /*int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < width; ++y) {
                for (int x = 0; x < height; ++x) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000; // black pixel
                    } else {
                        pixels[y * width + x] = 0xffffffff; // white pixel
                    }
                }
            }
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmp.setPixels(pixels, 0, width, 0, 0, width, height);*/
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static double[] getLatAndLonDouble(String latAndLon) {
        double[] result = new double[2];
        if (!TextUtils.isEmpty(latAndLon)) {
            String[] location = latAndLon.split(",");    //longitude and latitude
            result[0] = Double.valueOf(location[1]);    // lat 维度
            result[1] = Double.valueOf(location[0]);    // lon 经度
        }
        return result;
    }


    /**
     * 根据字符串截取经纬度
     * @param latAndLon 字符串 "104.064855,30.679879"  // 经度 维度
     * @return
     */
    public static final LatLng getLatAndLon(String latAndLon) {
        double[] result = getLatAndLonDouble(latAndLon);
//        if (!TextUtils.isEmpty(latAndLon)) {
//            String[] location = latAndLon.split(",");    //longitude and latitude
//            result[0] = Double.valueOf(location[1]);    // lat 维度
//            result[1] = Double.valueOf(location[0]);    // lon 经度
//        }
        return new LatLng(result[0], result[1]);
    }

    /**
     * 根据字符串截取经纬度
     * @param latAndLon 字符串 "104.064855,30.679879"  // 经度 维度
     * @return
     */
    public static final com.google.android.gms.maps.model.LatLng getGoogleMapLatAndLon(String latAndLon) {
        double[] result = getLatAndLonDouble(latAndLon);
        if (!TextUtils.isEmpty(latAndLon)) {
            String[] location = latAndLon.split(",");    //longitude and latitude
            result[0] = Double.valueOf(location[1]);    // lat 维度
            result[1] = Double.valueOf(location[0]);    // lon 经度
        }
        return new com.google.android.gms.maps.model.LatLng(result[0], result[1]);
    }


    public static final LatLng getLocationAMap(Context context) {
        LatLng latlng = null;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                latlng = new LatLng(latitude, longitude);
            }
        } else {
            LocationListener locationListener = new LocationListener() {
                // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                // Provider被enable时触发此函数，比如GPS被打开
                @Override
                public void onProviderEnabled(String provider) {
                }

                // Provider被disable时触发此函数，比如GPS被关闭
                @Override
                public void onProviderDisabled(String provider) {
                }

                //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        Log.e("Map", "Location changed : Lat: "
                                + location.getLatitude() + " Lng: "
                                + location.getLongitude());
                    }
                }
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                double latitude = location.getLatitude();//维度
                double longitude = location.getLongitude(); //经度
                latlng = new LatLng(latitude, longitude);
            }

        }
        /*Log.e("Map", "Location changed : Lat: "
                + latlng.latitude + " Lng: "
                + latlng.longitude);*/

        return latlng;
    }

    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }


    public static String formatTime(int second) {
        StringBuffer result = new StringBuffer();
        int min = second / 60;
        int sec = second % 60;
        if (min < 10) {
            result.append("0" + min);
        } else {
            result.append(min);
        }
        result.append(":");
        if (sec < 10) {
            result.append("0" + sec);
        } else {
            result.append(sec);
        }
        return result.toString();
    }

    public static String formatData(String dateStr) {
        StringBuffer result = new StringBuffer();
        result.append(dateStr.substring(4, 6));
        result.append("/");
        result.append(dateStr.substring(6, 8));
        return result.toString();
    }


    /**
     *
     * 函数名称: parseData
     * 函数描述: 将json字符串转换为map
     * @param data
     * @return
     */
    public static Map<String, String> parseJsonData(String data) {
        GsonBuilder gb = new GsonBuilder();
        Gson g = gb.create();
        Map<String, String> map = g.fromJson(data, new TypeToken<Map<String, String>>() {
        }.getType());
        return map;
    }

    /**
     * 从sd卡获取图片资源
     * @return
     */
    public static String getLatestImagePathFromSD() {
        // 图片列表
        //List<String> imagePathList = new ArrayList<String>();
        String imagePath = null;
        // 得到sd卡内image文件夹的路径   File.separator(/)
        String filePath = MyConfiguration.MEDIA_FILE_PATH;
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        if (files == null) {
            return imagePath;
        }
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return -1;
                else if (diff == 0)
                    return 0;
                else
                    return 1;
            }

            public boolean equals(Object obj) {
                return true;
            }
        });
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (checkIsImageFile(file.getPath())) {
                //imagePathList.add(file.getPath());
                imagePath = file.getPath();
                break;  // get the latest picture
            }
        }
        // 返回得到的图片列表
        return imagePath;
    }

    /**
     * 检查扩展名，得到图片格式的文件
     * @param fName  文件名
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg") || FileEnd.equals("bmp")|| FileEnd.equals("mp4")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

    public static void setSamplePhoto(ImageView imageView, Bitmap bitmap) {
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageBitmap(bitmap);
    }

    public static Location getLocation(Activity activity) {// 获取Location通过LocationManger获取！
        LocationManager locManger = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return null;
        }
        Location loc = locManger.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc == null) {
            loc = locManger.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        return loc;
    }




    public static String getPhotoLocation(String imagePath) {
        LogUtil.i("getPhotoLocation==" + imagePath);
        String latLngStr = null;
        double lat = 0;
        double lng = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(imagePath);
            String datetime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);// 拍摄时间
            String deviceName = exifInterface.getAttribute(ExifInterface.TAG_MAKE);// 设备品牌
            String deviceModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL); // 设备型号
            String latValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String lngValue = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            String latRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            String lngRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
            if (latValue != null && latRef != null && lngValue != null && lngRef != null) {
                try {
                    lat = convertRationalLatLonToFloat(latValue, latRef);
                    lng = convertRationalLatLonToFloat(lngValue, lngRef);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

            LogUtil.i(deviceName + ":" + deviceModel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtil.i(lat + ";" + lng);

        latLngStr = String.valueOf(lat)+","+String.valueOf(lng);

        return latLngStr;
    }

    private static float convertRationalLatLonToFloat(
            String rationalString, String ref) {

        String[] parts = rationalString.split(",");

        String[] pair;
        pair = parts[0].split("/");
        double degrees = Double.parseDouble(pair[0].trim())
                / Double.parseDouble(pair[1].trim());

        pair = parts[1].split("/");
        double minutes = Double.parseDouble(pair[0].trim())
                / Double.parseDouble(pair[1].trim());

        pair = parts[2].split("/");
        double seconds = Double.parseDouble(pair[0].trim())
                / Double.parseDouble(pair[1].trim());

        double result = degrees + (minutes / 60.0) + (seconds / 3600.0);
        if ((ref.equals("S") || ref.equals("W"))) {
            return (float) -result;
        }
        return (float) result;
    }

    public static void setLocationToExif(String path, double lat, double lng){
        try {
            //android写入图片EXIF信息
            LogUtil.v("lat="+lat+" lng="+lng);
            ExifInterface exifInterface = new ExifInterface(path);
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, String.valueOf(lat));
            exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, String.valueOf(lng));
            exifInterface.saveAttributes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setVideoTime(String path, String time){
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            exifInterface.setAttribute("video_time", time);
            exifInterface.saveAttributes();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getVideoTime(String path){
        String timeStr;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            timeStr = exifInterface.getAttribute("video_time");
            return timeStr;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getCustomPhotoLocation(String path){
        double latDouble = 0;
        double lngDouble = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            String latStr = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String lngStr = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);

            LogUtil.v("lat="+latStr+"  lng="+lngStr);
            if (!TextUtils.isEmpty(latStr)){
                String lat = latStr.split(",")[0];
                String[] latArr = lat.split("/");
                latDouble = Double.parseDouble(latArr[0])/Double.parseDouble(latArr[1]);
            }
            if (!TextUtils.isEmpty(lngStr)){
                String lng = lngStr.split(",")[0];
                String[] lngArr = lng.split("/");
                lngDouble = Double.parseDouble(lngArr[0])/Double.parseDouble(lngArr[1]);
            }
            LogUtil.v("latDouble:"+latDouble + " lngDouble:"+lngDouble);

            return latDouble+","+lngDouble;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getDisplayRotation(Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        switch (rotation) {
            case Surface.ROTATION_0: return 0;
            case Surface.ROTATION_90: return 90;
            case Surface.ROTATION_180: return 180;
            case Surface.ROTATION_270: return 270;
        }
        return 0;
    }
    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, Camera camera) {
        // See android.hardware.Camera.setCameraDisplayOrientation for
        // documentation.
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int degrees = getDisplayRotation(activity);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    public static Typeface getTypeFace(Context context){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/haettenschweiler.ttf");
        return typeface;
    }

    public static String formatFloat(float value){
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return decimalFormat.format(value);
    }

    public static String formatFloat(double value){
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        return decimalFormat.format(value);
    }


    //解析蓝牙返回的车辆信息 ,此为immotorGo解析方式
    public static void convertBLEStatus(ScooterBLEStatus scooterBLEStatus, byte[] data){
        if (scooterBLEStatus == null) {
            return;
        }
        int len = data.length;
        if (len < 11 || data[2] != 0x00) {
            return;
        }
        //速度
        int speed = NumberBytes.byteToInt(data[3]);
        float speedF = (float)speed / 10.0f;
        scooterBLEStatus.setSpeed(speedF);
        //Speed 当前助力模式速度
        int modeSpeed = NumberBytes.byteToInt(data[4]);
        float modeSpeedF = (float)modeSpeed / 10.0f;
        scooterBLEStatus.setModeSpeed(modeSpeedF);
        //Mode2 的最大限速
        int maxSpeed = NumberBytes.byteToInt(data[5]);
        float maxSpeedF = (float)maxSpeed/* / 10.0f*/;
        scooterBLEStatus.setMaxSpeed(maxSpeedF);
        //剩余电量
        int batteryLeft = NumberBytes.byteToInt(data[6]);
        scooterBLEStatus.setLeftBattery(batteryLeft);
        //剩余旅程
        byte[] mileageBytes = new byte[]{data[8], data[7]};
        int mileage = NumberBytes.bytesToInt(mileageBytes);
        scooterBLEStatus.setLeftMileage((float)mileage / 10.0f);

        String dataInfoStr = NumberBytes.byteToBit(data[9]);
            /** BIT[0]: 充电状态。 0: IDLE 1: charging
                BIT[1]: 锁状态。 0: Unlock 1: Lock
                BIT[2]: 智能钥匙状态。 0: Disable 1: Enable
                BIT[3]: 自动大灯开关。 0: Disable 1: Enable
                BIT[4]: 前灯开关。 0: Disable 1: Enable
                BIT[5]: LOGO 灯开关。 0: Off 1: On
                BIT[6]: 零速启动。 0: Off 1: On
                BIT[7]: Boost Mode State。 0: Disable 1: Enable 11 */
        scooterBLEStatus.setCharging(dataInfoStr.charAt(7) == '1');
        scooterBLEStatus.setLocked(dataInfoStr.charAt(6) == '1');
        scooterBLEStatus.setSmartKey(dataInfoStr.charAt(5) == '1');
        scooterBLEStatus.setLightAutoMode(dataInfoStr.charAt(4) == '1');
        scooterBLEStatus.setLightFront(dataInfoStr.charAt(3) == '1');
        scooterBLEStatus.setLightLogo(dataInfoStr.charAt(2) == '1');
        scooterBLEStatus.setLaunchZeroMode(dataInfoStr.charAt(1) == '0');
        scooterBLEStatus.setBoostMode(dataInfoStr.charAt(0) == '1');

        //电池是否在位 BIT 0：0 号槽位， 0: Not Present 1: Present。 BIT 1：1 号槽位， 0: Not Present 1: Present。
        String dataBatteryStr = NumberBytes.byteToBit(data[10]);
        scooterBLEStatus.setBatteryPresent0(dataBatteryStr.charAt(7) == '1');
        scooterBLEStatus.setBatteryPresent1(dataBatteryStr.charAt(6) == '1');

    }


    public static void main(String[] args){
        String tmp = "2016-08-04";
        /*String result = Common.formatData(tmp);

        System.out.print(result);*/
    }


}
