package com.immotor.batterystation.android.mycar.mycaraddress;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jm on 2017/9/18 0018.
 */

public class MyCarAddressActivity extends BaseActivity implements LocationSource, AMapLocationListener, View.OnClickListener {

    private MapView mAMapView;
    private AMap aMap;
    private AMapLocationClient mLocationClient;
    private Marker carMarker;
    private Marker curPositionMarker;

    private LatLng currentLocation;
    private LatLonPoint latLonPoint;
    private OnLocationChangedListener mListener;
    private String location;
    private boolean locationed = false;
    private boolean isFirstLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_address);
        location = getIntent().getStringExtra(AppConstant.KEY_ENTRY_ADDRESSACTIVITY_LOCATION);
        if (location != null && !location.equals("0,0")) {
            latLonPoint = new LatLonPoint(Double.parseDouble(location.substring(0, location.lastIndexOf(","))), Double.parseDouble(location.substring(location.lastIndexOf(",") + 1)));
        }
        locationed = true;
        isFirstLocation = true;
    }

    @Override
    public void initUIView() {
        initAMap();
        ImageView mMenuButton = (ImageView) findViewById(R.id.home_actionbar_menu);
        mMenuButton.setImageResource(R.mipmap.nav_back_icon);
        mMenuButton.setOnClickListener(this);
        TextView mTittleText = (TextView) findViewById(R.id.home_actionbar_text);
        mTittleText.setText(R.string.car_map);
        ImageView myLocation = (ImageView) findViewById(R.id.car_btn_location);
        myLocation.setOnClickListener(this);
        ImageView carLoacation = (ImageView) findViewById(R.id.car_btn_my_location);
        carLoacation.setOnClickListener(this);
    }
    private void setMapCustomStyleFile(Context context) {
        String styleName = "my_map_style.data";
        String filePath = context.getFilesDir().getAbsolutePath();
        File file = new File(filePath + "/" + styleName);
        if(file.exists()){
            aMap.setCustomMapStylePath(filePath + "/" + styleName);
            aMap.setMapCustomEnable(true);
        }else{
            FileOutputStream outputStream = null;
            InputStream inputStream = null;
            try {
                inputStream = context.getAssets().open(styleName);
                byte[] b = new byte[inputStream.available()];
                inputStream.read(b);

                file.createNewFile();
                outputStream = new FileOutputStream(file);
                outputStream.write(b);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null)
                        inputStream.close();

                    if (outputStream != null)
                        outputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            aMap.setCustomMapStylePath(filePath + "/" + styleName);
            aMap.setMapCustomEnable(true);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mAMapView != null) {
            mAMapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAMapView != null) {
            mAMapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        if (mAMapView != null) {
            if (carMarker != null) {
                carMarker.destroy();
            }
            if (aMap != null) {
                aMap.clear();
            }
            mAMapView.onDestroy();
        }
        super.onDestroy();
    }

    /**
     * 初始化AMap对象
     */
    private void initAMap() {
        mAMapView = (MapView) findViewById(R.id.car_address_map);
        mAMapView.onCreate(null);
        if (aMap == null) {
            aMap = mAMapView.getMap();
            setUpMap();
        }
        setMapCustomStyleFile(this);
    }

    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null
                && aMapLocation.getErrorCode() == 0) {

            currentLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());//获取纬度
            if (curPositionMarker == null) {
                curPositionMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1f).position(new LatLng(currentLocation.latitude, currentLocation.longitude)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.map_position_circle))));
            } else {
                curPositionMarker.setPosition(currentLocation);
            }
            if (isFirstLocation) {
                isFirstLocation = false;
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
            if (latLonPoint != null) {
                if (carMarker == null) {
                    carMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1f).position(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude())).icon(BitmapDescriptorFactory.fromBitmap(getBitmap( R.mipmap.my_car_loc_icon))));
                } else {
                    carMarker.setPosition(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()));
                }
                if (locationed) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()), 15));
                    locationed = false;
                }
            }
        }
    }
    private Bitmap getBitmap(int resId) {
        Drawable drawable = ContextCompat.getDrawable(this, resId);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            AMapLocationClientOption locationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);
            //设置为高精度定位模式
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔,单位毫秒,默认为10000ms
            locationOption.setInterval(2000);
            locationOption.setNeedAddress(true);
            //设置定位参数
            mLocationClient.setLocationOption(locationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.car_btn_location:
                if (aMap != null && latLonPoint != null && !location.equals("0,0")) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()), 15));
                } else {
                    Toast.makeText(this, R.string.cont_get_car_location, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.car_btn_my_location:
                if (aMap != null && currentLocation != null) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                }
                break;
            case R.id.home_actionbar_menu:
                finish();
                break;
            default:
                break;
        }
    }
}
