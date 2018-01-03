package com.immotor.batterystation.android.ui.fragment;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.animation.TranslateAnimation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RideStep;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.immotor.batterystation.android.MyApplication;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.app.TypeStatus;
import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.entity.BatteryStationInfo;
import com.immotor.batterystation.android.entity.DownLoadEnd;
import com.immotor.batterystation.android.entity.HbBean;
import com.immotor.batterystation.android.entity.MyRentInfo;
import com.immotor.batterystation.android.entity.OrderQueryBean;
import com.immotor.batterystation.android.entity.SelectComboBean;
import com.immotor.batterystation.android.http.ApiException;
import com.immotor.batterystation.android.http.HttpFailMessage;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.mybattery.MyBatteryActivity;
import com.immotor.batterystation.android.service.HeartBeatService;
import com.immotor.batterystation.android.ui.activity.FuscreenAtivity;
import com.immotor.batterystation.android.ui.activity.QRCodeActivity;
import com.immotor.batterystation.android.ui.base.BaseFragment;
import com.immotor.batterystation.android.ui.views.CommonDialog;
import com.immotor.batterystation.android.util.Common;
import com.immotor.batterystation.android.util.DateTimeUtil;
import com.immotor.batterystation.android.util.GlideCircleTransform;
import com.immotor.batterystation.android.util.LogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import cn.iwgang.countdownview.CountdownView;


/**
 * Created by ${jm} on 2017/7/19 0019.
 * 时间太紧，没空抽取，有时间再抽取出来
 */
public class MapFragment extends BaseFragment implements AMap.OnCameraChangeListener, LocationSource, AMapLocationListener, View.OnClickListener {
    private int mBatteryNumber = 1;
    private LinearLayout statusInfoPan;
    private List<BatteryStationInfo> mStationList = new ArrayList<>();
    private Map<String, Marker> markers = new HashMap<>();
    private AMap aMap;
    private MapView mAMapView;
    private AMapLocationClient mLocationClient;
    private LatLng currentLocation; // 手机当前的位置
    private LatLng centerLocation; //地图中心位置
    private RouteSearch routeSearch;  //导航到电池柜
    private Polyline polyline;        //绘制的导航线
    private Marker curPositionMarker;
    private BatteryStationInfo selectStationInfo;
    private LinearLayout mNavillyt;
    private LinearLayout mOrderPanel;
    private LinearLayout mFresh;
    private ImageView mLocation;
    private TextView mQrcode;
    private RadioButton mRadioBtnOne;
    private RadioButton mRadioBtnTwo;
    private LinearLayout mGoOrder;
    private LinearLayout mCancleOrderPanel;
    private LinearLayout mCancleOrder;
    private TextView mAddressText;
    private LinearLayout mBatteryMsg;
  //  private int mBatterynumber;
    private int mBatteryNotFetchnumber;
    private boolean HAVE_NOT_FETCH_BATTERY = false;
    private int mBatteryordernumber;
    private int mOrderType = 5;
    private String morderNum;
    private TextView mHaveBuyBattery;
    private Dialog mloadingDialog;
    private OrderQueryBean mOrderQuerydata;
    private TextView mFetch;
    private CountdownView mCuntDownTiem;
    private boolean isOrderQueryRequest = false;
    private boolean isCancleOrderQueryQuest = false;
    private String mPID;
    private boolean isScanResult = false;
    private TextView mDialogCancleBtn;
    private TextView mOneBatteryBtn;
    private TextView mTwoBatteryBtn;
    private boolean ishttpScanQueryQuest = false;
    private Typeface mTypeface;
    private TextView mCancleAddress;
    private boolean isOrderBatteryRequest = false;
    private LinearLayout mNoticeRightSign;
    private boolean misNoticeRequest = false;
    private double mLatitude;
    private double mLongitude;
    private Marker middleMarker;
    private double cameraChangeFinishlatitude;
    private double cameraChangeFinishlongitude;
    private boolean isorderedSucessStatus = false;
    private boolean isFirstLocation = true;
    private ImageView btnAnmion;
    private double middleMarkerNowLatitude;
    private double middleMarkerNowLongitude;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int getContentLayout() {
        Bundle bundle = getArguments();
        mLatitude = bundle.getDouble(AppConstant.KEY_HLATITUDE_TYPE);
        mLongitude = bundle.getDouble(AppConstant.KEY_HLONGITUDE_TYPE);
        return R.layout.fragment_map;
    }

    @Override
    public void initUIViews() {
        initView();
        initAMap();//为定位方法

    }

    private void initNotice() {
        if (!isNetworkAvaliable()) {
            return;
        }
        if (misNoticeRequest) {
            return;
        }
        misNoticeRequest = true;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {
                    int code = ((ApiException) e).getCode();
                    if (code == 633) {
                        mBatteryMsg.setClickable(true);
                        mBatteryMsg.setVisibility(View.VISIBLE);
                        mNoticeRightSign.setVisibility(View.VISIBLE);
                        String notice = getString(R.string.notice_msg_to_rent);
                        initShowBattery(notice, false);
                    } else if (code == 601){
                        mapNoticeDialog().show();
                    }else {
                        mBatteryMsg.setVisibility(View.GONE);
                    }
                } else {
                    mBatteryMsg.setVisibility(View.GONE);
                }
                misNoticeRequest = false;
            }

            @Override
            public void onNext(Object object) {
                if (object != null) {
                    mBatteryMsg.setClickable(false);
                    mBatteryMsg.setVisibility(View.VISIBLE);
                    mNoticeRightSign.setVisibility(View.GONE);
                    String notice = getString(R.string.notice_msg_font) + " <font color='red'>" + (int) Float.parseFloat(String.valueOf(object)) + "</font>" + " "+getString(R.string.day);
                    initShowBattery(notice, true);
                } else {
                    mBatteryMsg.setVisibility(View.GONE);
                }
                misNoticeRequest = false;
            }
        };
        HttpMethods.getInstance().notice(new ProgressSubscriber(subscriberOnNextListener, getContext(), null), mPreferences.getToken());
    }

    private void initExpireTime() {
        httpGetMyRent();
    }

    private void initView() {
        statusInfoPan = (LinearLayout) getView().findViewById(R.id.status_info_pan);
        statusInfoPan.setVisibility(View.GONE);
        mCancleOrderPanel = (LinearLayout) statusInfoPan.findViewById(R.id.main_map_cancle_order);
        mOrderPanel = (LinearLayout) statusInfoPan.findViewById(R.id.main_map_go_order);
        mNavillyt = (LinearLayout) getView().findViewById(R.id.mian_map_navi_llyt);
        btnAnmion = (ImageView)getView().findViewById(R.id.btn_fresh_img);
        mHaveBuyBattery = (TextView) getView().findViewById(R.id.map_have_buy_battery_txt);
        mFetch = (TextView) mCancleOrderPanel.findViewById(R.id.rent_code);
        mCuntDownTiem = (CountdownView) mCancleOrderPanel.findViewById(R.id.rent_remain_time);
        mNoticeRightSign = (LinearLayout) getView().findViewById(R.id.notice_right_sign);
        mTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/haettenschweiler.ttf");
        initListener();
    }

    private void initListener() {
        mFresh = (LinearLayout) getView().findViewById(R.id.btn_fresh);
        mFresh.setOnClickListener(this);
        mLocation = (ImageView) getView().findViewById(R.id.btn_location);
        mLocation.setOnClickListener(this);
        mQrcode = (TextView) getView().findViewById(R.id.btn_qrcode);
        mQrcode.setText(R.string.scan_exchange_battery);
        mQrcode.setOnClickListener(this);
        mRadioBtnOne = (RadioButton) getView().findViewById(R.id.btn_one);
        mRadioBtnOne.setOnClickListener(this);
        mRadioBtnTwo = (RadioButton) getView().findViewById(R.id.btn_two);
        mRadioBtnTwo.setOnClickListener(this);
        mGoOrder = (LinearLayout) getView().findViewById(R.id.btn_order);
        mGoOrder.setOnClickListener(this);
        mCancleOrder = (LinearLayout) getView().findViewById(R.id.cancle_order);
        mCancleOrder.setOnClickListener(this);
        mBatteryMsg = (LinearLayout) getView().findViewById(R.id.map_battery_message);
        mBatteryMsg.setOnClickListener(this);
        mAddressText = (TextView) (getView().findViewById(R.id.text_info));
        mCancleAddress = (TextView) statusInfoPan.findViewById(R.id.text_info_cancle);
        mCuntDownTiem.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                mOrderQuerydata = null;
                statusInfoPan.setVisibility(View.GONE);
                isorderedSucessStatus = false;

                if (cameraChangeFinishlatitude != 0 && cameraChangeFinishlongitude != 0) {
                    httpGetStation(cameraChangeFinishlatitude, cameraChangeFinishlongitude, 20000);
                }
                //显示刷新按钮
                mQrcode.setText(R.string.scan_exchange_battery);
                mFresh.setVisibility(View.VISIBLE);
                mLocation.setVisibility(View.VISIBLE);
                mPreferences.setOrderStation(null);
                mPreferences.setOrderStationImgUrl(null);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAMapView != null) {
            mAMapView.onResume();
        }
        httpGetNotFetchBattery();
        initExpireTime();
        initNotice();
        if (centerLocation != null) {
            httpGetStation(centerLocation.latitude, centerLocation.longitude, 20000);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAMapView != null) {
            mAMapView.onPause();
        }
        if (mFresh != null) {
            mFresh.clearAnimation();
        }
        if (mOrderPanel != null && mOrderPanel.isShown()) {
            mOrderPanel.setVisibility(View.GONE);
        }

        if (mOrderQuerydata == null) {
            if (polyline != null) {
                polyline.remove();
                middleMarkerNowLatitude = 0;
                middleMarkerNowLongitude = 0;
            }
            isorderedSucessStatus = false;
        }
    }

    /**
     * 初始化AMap对象
     */
    private void initAMap() {
        mAMapView = (MapView) getView().findViewById(R.id.map);
        mAMapView.onCreate(null);
        if (aMap == null) {
            aMap = mAMapView.getMap();
            setUpMap();
        } else {
            if (getView().getParent() != null) {
                ((ViewGroup) getView().getParent()).removeView(getView());
            }
        }
        setMapCustomStyleFile(getContext());
    }

    private void setMapCustomStyleFile(Context context) {
        String styleName = "my_map_style.data";
        String filePath = context.getFilesDir().getAbsolutePath();
        File file = new File(filePath + "/" + styleName);
        if (file.exists()) {
            aMap.setCustomMapStylePath(filePath + "/" + styleName);
            aMap.setMapCustomEnable(true);
        } else {
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

    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setOnCameraChangeListener(this);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        aMap.setOnMarkerClickListener(markerClickListener);
        aMap.setOnMapClickListener(onMapClickListener);
        routeSearch = new RouteSearch(getContext());
        routeSearch.setRouteSearchListener(listener);
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if (aMapLocation != null
                && aMapLocation.getErrorCode() == 0) {
            currentLocation = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());//获取纬度
       //     LogUtil.d("whj on location changed " + needMoveTime + ", lat = " + currentLocation.latitude + ", lon = " + currentLocation.longitude);
            //  aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

            if (curPositionMarker == null) {
                curPositionMarker = drawMark(currentLocation, R.mipmap.map_position_circle);
            } else {
                curPositionMarker.setPosition(currentLocation);
            }
            if (isFirstLocation) {
                isFirstLocation = false;
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
            if (mLatitude != -1 && mLongitude != -1) {
                if (middleMarker == null) {
                    middleMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1.0f).position(new LatLng(mLatitude, mLongitude)).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.location_icon_middle))));

                } else {
                    middleMarker.setPosition(new LatLng(mLatitude, mLongitude));
                }
                middleMarker.setZIndex(2);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude, mLongitude), 15));
                mLatitude = -1;
                mLongitude = -1;
            } else {
                if (middleMarker == null) {
                    middleMarker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 1.0f).position(currentLocation).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.location_icon_middle))));
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                } /*else {
                    if (centerLocation != null) {
                        if (!isorderedSucessStatus) {
                            if (isFirstLocationMiddle) {
                                isFirstLocationMiddle = false;
                                middleMarker.setPosition(centerLocation);
                                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLocation, 15));
                            }
                        }
                    }
                }*/
                middleMarker.setZIndex(2);
            }
        }
    }
/*
    public void addBoundscircle() {
        LatLngBounds.Builder build = new LatLngBounds.Builder();
        for (int i=0;i<mStationList.size();i++) {
            build.include(new LatLng(mStationList.get(i).getLatitude(),mStationList.get(i).getLongitude()));
        }
        build.include(currentLocation);
        LatLngBounds bounds = build.build();
        if (aMap!=null) {
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }

    }*/

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        centerLocation = cameraPosition.target;

        if (!isorderedSucessStatus) {
            if (polyline != null) {
                polyline.remove();
                middleMarkerNowLatitude = 0;
                middleMarkerNowLongitude = 0;
            }
            middleMarker.setPosition(centerLocation);
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
       /* if (middleMarker!=null) {
        startJumpAnimation();
        }*/
        float distance = 0;
        if (cameraChangeFinishlatitude != 0 && cameraChangeFinishlongitude != 0) {
            distance = AMapUtils.calculateLineDistance(new LatLng(cameraChangeFinishlatitude, cameraChangeFinishlongitude), cameraPosition.target);
        }
        cameraChangeFinishlatitude = cameraPosition.target.latitude;
        cameraChangeFinishlongitude = cameraPosition.target.longitude;
        if (!isorderedSucessStatus) {
            if (distance > 1000) {
                httpGetStation(cameraChangeFinishlatitude, cameraChangeFinishlongitude, 20000);
            }
        }
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener onLocationChangedListener) {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(getContext());
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
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    private Marker drawMark(LatLng latLng, int resId) {
        //画指针
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.anchor(0.5f, 1.0f);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(getBitmap(resId)));
        return aMap.addMarker(markerOption);
    }

    private Marker drawMark(LatLng latLng, Bitmap bmp) {
        //画指针
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.anchor(0.5f, 1.0f);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(bmp));
        return aMap.addMarker(markerOption);
    }

    private Bitmap getBitmap(int resId) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), resId);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void httpGetStation(double latitude, double longitude, double radius) {
        if (!isNetworkAvaliable()) {
            return;
        }
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<List<BatteryStationInfo>>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(e.getMessage());
            }

            @Override
            public void onNext(List<BatteryStationInfo> result) {
                if (result != null && result.size()!=0) {
                    mStationList.clear();
                    mStationList .addAll(result) ;
                   /* for (BatteryStationInfo info : result) {
                        boolean find = false;
                        for (int i = 0; i < mStationList.size(); i++) {
                            if (info.getpID().equals(mStationList.get(i).getpID())) {
                                find = true;
                                mStationList.get(i).clone(info);
                                break;
                            }
                        }
                        if (!find) {
                            mStationList.add(info);
                        }
                    }*/
                    MyApplication.setStationList(mStationList);
                    for (int i = 0; i < mStationList.size(); i++) {
                        BatteryStationInfo info = mStationList.get(i);
                        Marker marker = markers.get(info.getpID());
                        //可能信息比较老，需要更新
                        if (marker != null) {
                            markers.remove(info.getpID());
                            marker.remove();
                            marker.destroy();
                        }
                        View markerView = getView().findViewById(R.id.marker);
                  //      TextView remain = (TextView) markerView.findViewById(R.id.remain_valid);
                        TextView battery = (TextView) markerView.findViewById(R.id.battery_valid);
                        if (info.getpID().equals(mPreferences.getOrderStation()) || info.getValid() == 0) {
                            markerView.setBackgroundResource(R.mipmap.station_ordered_icon_big);
                            battery.setVisibility(View.INVISIBLE);
                        } else {
                            markerView.setBackgroundResource(R.mipmap.station_in_map_icon_big);
                            battery.setVisibility(View.VISIBLE);
                        }
                   //     remain.setText("" + info.getValid());
                        battery.setText("" + info.getValid());

                        markerView.setDrawingCacheEnabled(true);
                        marker = drawMark(new LatLng(info.getLatitude(), info.getLongitude()), markerView.getDrawingCache(true));
                        markerView.setDrawingCacheEnabled(false);
                        markers.put(info.getpID(), marker);
                        marker.setTitle(info.getpID());

                    }
                  /*  if (currentLocation!=null) {
                        addBoundscircle();
                    }
*/
                }else {
                    for (int i = 0; i < mStationList.size(); i++) {
                        BatteryStationInfo info = mStationList.get(i);
                        Marker marker = markers.get(info.getpID());
                        //可能信息比较老，需要更新
                        if (marker != null) {
                            markers.remove(info.getpID());
                            marker.remove();
                            marker.destroy();
                        }
                    }
                }
            }
        };
        HttpMethods.getInstance().getPowerStation(new ProgressSubscriber(subscriberOnNextListener, getContext(), null), latitude, longitude, radius);
    }

    AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        // marker 对象被点击时回调的接口
        // 返回 true 则表示接口已响应事件，否则返回false
        @Override
        public boolean onMarkerClick(Marker marker) {

            if (marker.getPosition()!=null && (marker.getPosition()==centerLocation ||marker.getPosition()==currentLocation) && marker.getTitle()==null) {
                return true;

            }
            String id = marker.getTitle();
            if (mOrderQuerydata != null) {
                if (id.equals(mPreferences.getOrderStation())) {
                    Intent intent = new Intent(getContext(), FuscreenAtivity.class);
                    startActivity(intent);
                    return true;
                } else {
                    Toast.makeText(getContext(), R.string.complete_current_order, Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
        /*    if (!isorderedSucessStatus) {
            middleMarker.setPosition(centerLocation);
            }*/
            isorderedSucessStatus = true;

            if (!TextUtils.isEmpty(id)) {
                for (int i = 0; i < mStationList.size(); i++) {
                    if (id.equals(mStationList.get(i).getpID())) {
                        selectStationInfo = mStationList.get(i);
                        if (selectStationInfo.getValid() <= 0) {
                            Toast.makeText(getContext(), R.string.no_can_use_battery, Toast.LENGTH_SHORT).show();
                            isorderedSucessStatus = false;
                            if (mOrderPanel.isShown()) {
                                mOrderPanel.setVisibility(View.GONE);
                            }
                            if (polyline!=null) {
                                polyline.remove();
                                middleMarkerNowLatitude = 0;
                                middleMarkerNowLongitude = 0;
                            }
                            return true;
                        }
                        if (polyline!=null) {
                            polyline.remove();
                        }
                        if (HAVE_NOT_FETCH_BATTERY) {
                            showOperateDialog().show();
                            statusInfoPan.setVisibility(View.GONE);
                        } else {
                            showOperateView(2);
                        }
                        if (middleMarkerNowLatitude != mStationList.get(i).getLatitude() && middleMarkerNowLongitude != mStationList.get(i).getLongitude()) {
                            middleMarkerNowLatitude = mStationList.get(i).getLatitude();
                            middleMarkerNowLongitude = mStationList.get(i).getLongitude();
                            navigateRide(mStationList.get(i).getLatitude(), mStationList.get(i).getLongitude());
                            middleMarker.setPosition(centerLocation);
                            return true;
                        }

                        return true;
                    }
                }
            }
            return false;
        }
    };
    AMap.OnMapClickListener onMapClickListener = new AMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if (mOrderPanel != null && mOrderPanel.isShown()) {
                mOrderPanel.setVisibility(View.GONE);
                isorderedSucessStatus = false;
                if (polyline != null) {
                    polyline.remove();
                    middleMarkerNowLatitude = 0;
                    middleMarkerNowLongitude = 0;
                }
            }
        }

    };

    private void showOperateView(int orderType) {
        statusInfoPan.setVisibility(View.VISIBLE);
        if (selectStationInfo != null) {
            mAddressText.setText(selectStationInfo.getName());
        }
        mOrderPanel.setVisibility(View.VISIBLE);
        mCancleOrderPanel.setVisibility(View.GONE);
        mOrderType = orderType;
        if ((mBatteryNotFetchnumber == 1 && orderType == TypeStatus.FETCH_TYPE) || selectStationInfo.getValid() == 1) {
            mRadioBtnOne.setVisibility(View.VISIBLE);
            mRadioBtnTwo.setVisibility(View.GONE);
        } else {
            mRadioBtnOne.setVisibility(View.VISIBLE);
            mRadioBtnTwo.setVisibility(View.VISIBLE);
        }
        //默认显示一颗电池
        mBatteryordernumber = 1;
        if (mRadioBtnTwo.isChecked()) {
            mRadioBtnTwo.setChecked(false);
        }
        mRadioBtnOne.setChecked(true);


     /*   statusInfoPan.clearAnimation();
        Animation translateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_translate_toast);
        statusInfoPan.startAnimation(translateAnimation);*/
    }

    private void navigateRide(double latitude, double longitude) {
        if (currentLocation != null) {
            LatLonPoint latLonPoint = null;
            if (cameraChangeFinishlatitude != 0 && cameraChangeFinishlongitude != 0) {
                latLonPoint = new LatLonPoint(cameraChangeFinishlatitude, cameraChangeFinishlongitude);
            } else {
                latLonPoint = new LatLonPoint(currentLocation.latitude, currentLocation.longitude);
            }
            RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(new RouteSearch.FromAndTo(latLonPoint, new LatLonPoint(latitude, longitude)));
            routeSearch.calculateRideRouteAsyn(query);
        }
    }

    RouteSearch.OnRouteSearchListener listener = new RouteSearch.OnRouteSearchListener() {
        @Override
        public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {
        }

        @Override
        public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        }

        @Override
        public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int code) {
        }

        @Override
        public void onRideRouteSearched(RideRouteResult rideRouteResult, int code) {
            LogUtil.d("onWalkRouteSearched " + code);
            if (code == 1000) {
                if (polyline != null) {
                    polyline.remove();
//                    polyline = null;
//                    return;
                }
                if (rideRouteResult.getPaths().size() > 0) {
                    RidePath ridePath = rideRouteResult.getPaths().get(0);
                    float distance = ridePath.getDistance();
                    long duration = ridePath.getDuration();
                    TextView distanceTv = (TextView) getView().findViewById(R.id.distance_value);
                    distanceTv.setTypeface(mTypeface);
                    distanceTv.setText((Math.round(distance / 100d) / 10d + ""));
                    TextView durationTV = (TextView) getView().findViewById(R.id.duration_value);
                    durationTV.setTypeface(mTypeface);
                    durationTV.setText(DateTimeUtil.secondToTimeString((int) duration, 0));

                    List<RideStep> list = ridePath.getSteps();
                    List<LatLng> latLngs = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        List<LatLonPoint> subList = list.get(i).getPolyline();
                        for (int j = 0; j < subList.size(); j++) {
                            latLngs.add(new LatLng(subList.get(j).getLatitude(), subList.get(j).getLongitude()));
                        }
                    }
                    PolylineOptions polylineOptions = new PolylineOptions().
                            addAll(latLngs).width(15).color(Color.argb(200, 1, 120, 1));
                    polylineOptions.useGradient(true);
                    polyline = aMap.addPolyline(polylineOptions);
                }
            }
        }
    };


    private void httpGetNotFetchBattery() {
        if (!isNetworkAvaliable()) {
            return;
        }
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Double>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(e.getMessage());
                httpGetNotFetchBattery();
            }

            @Override
            public void onNext(Double num) {
                if (num == null) {
                    return;
                }
                double doubleNum = num;
                int batteryNotFetchNum = (int) doubleNum;
                if (batteryNotFetchNum > 0) {
                    mBatteryNotFetchnumber = batteryNotFetchNum;
                    HAVE_NOT_FETCH_BATTERY = true;
                } else {
                    mBatteryNotFetchnumber = 0;
                    HAVE_NOT_FETCH_BATTERY = false;
                }
            }
        };
        HttpMethods.getInstance().myBatteryNotFetch(new ProgressSubscriber(subscriberOnNextListener, getContext(), null), mPreferences.getToken());
    }

    private void initShowBattery(String notice, boolean status) {
        mBatteryMsg.setVisibility(View.VISIBLE);
        if (status) {
            mHaveBuyBattery.setText(Html.fromHtml(notice));
        } else {
            mHaveBuyBattery.setText(notice + "");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
       /* boolean noticeStatus = Preferences.getInstance(getContext().getApplicationContext()).getNoticeMessageStatus();
        if (noticeStatus) {
            mapNoticeDialog().show();
            Preferences.getInstance(getContext().getApplicationContext()).setKeyNoticeMessageStatus(false);
        }*/
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        if (mAMapView != null) {
            if (curPositionMarker != null) {
                curPositionMarker.destroy();
            }
            if (middleMarker != null) {
                middleMarker.destroy();
            }
            if (aMap != null) {
                aMap.clear();
            }
            mAMapView.onDestroy();
        }
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHbEventE(HbBean hbBean) {
            doHbResult(hbBean.getVal());
    }

    private void doHbResult(int mVal) {
        if (mloadingDialog!=null &&mloadingDialog.isShowing()) {
            mloadingDialog.dismiss();
        }
        mCancleOrderPanel.setVisibility(View.GONE);
        httpGetStation(centerLocation.latitude, centerLocation.longitude, 20000);

        //显示刷新按钮
        mOrderQuerydata = null;
        isorderedSucessStatus = false;
        mQrcode.setText(R.string.scan_exchange_battery);
        mFresh.setVisibility(View.VISIBLE);
        mLocation.setVisibility(View.VISIBLE);
        mPreferences.setOrderStation(null);
        mPreferences.setOrderStationImgUrl(null);
        if (mVal == 10) {
            Toast.makeText(getContext(), R.string.fetch_battery_scuess, Toast.LENGTH_SHORT).show();
            //获取未取电池
            httpGetNotFetchBattery();
        } else if (mVal == 11) {
            Toast.makeText(getContext(), R.string.fetch_battery_fail, Toast.LENGTH_SHORT).show();
        } else if (mVal == 20) {
            Toast.makeText(getContext(), R.string.exchange_battery_scuess, Toast.LENGTH_SHORT).show();
        } else if (mVal == 21) {
            Toast.makeText(getContext(), R.string.exchange_battery_fail, Toast.LENGTH_SHORT).show();
        } else if (mVal == 40) {
            Toast.makeText(getContext(), R.string.refund_battery_scuess, Toast.LENGTH_SHORT).show();
            httpGetNotFetchBattery();
        }
        if (polyline != null) {
            polyline.remove();
            middleMarkerNowLatitude = 0;
            middleMarkerNowLongitude = 0;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fresh:
                if (centerLocation != null) {
                    httpGetStation(centerLocation.latitude, centerLocation.longitude, 20000);
                }
                if (mOrderPanel != null && mOrderPanel.isShown()) {
                    mOrderPanel.setVisibility(View.GONE);
                }
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_ro_refresh);
                btnAnmion.startAnimation(animation);//开始动画
                if (polyline != null) {
                    polyline.remove();
                    middleMarkerNowLatitude = 0;
                    middleMarkerNowLongitude = 0;
                }
                isorderedSucessStatus = false;
                break;
            case R.id.btn_location:
                if (aMap != null && currentLocation != null) {
                    aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                }
                break;
            case R.id.btn_qrcode:
                initializeScan();
                break;
            case R.id.btn_one:
                if (selectStationInfo != null) {
                    mBatteryordernumber = 1;
                    if (mRadioBtnTwo.isChecked()) {
                        mRadioBtnTwo.setChecked(false);
                    }
                }
                break;
            case R.id.btn_two:
                if (selectStationInfo != null) {
                    mBatteryordernumber = 2;
                    if (mRadioBtnOne.isChecked()) {
                        mRadioBtnOne.setChecked(false);
                    }
                }
                break;
            case R.id.btn_order:
                if (selectStationInfo == null) {
                    return;
                }
                orderHttpMethod();
                break;
            case R.id.cancle_order:
                showDialog();
                break;
            case R.id.map_battery_message:
                //   initializeScan();
                initIntentMybattery();
                break;
        }
    }

    //ToDO 导航
/* private static void navi() {
        if (!PackageManagerUtil.haveGaodeMap() && !PackageManagerUtil.haveBaiduMap()) {
            Toast.makeText(getContext(), "请安装高德地图或者百度地图", Toast.LENGTH_SHORT).show();
            return;
        }
        showNaviDialog();
    }

    private void showNaviDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setMessage("是否前往高德地图或者百度地图？");
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(PackageManagerUtil.haveGaodeMap()){
                    PackageManagerUtil.startGaodeApp(getContext(),middleMarkerNowLatitude,middleMarkerNowLongitude);
                } else if(PackageManagerUtil.haveBaiduMap()){
                    PackageManagerUtil.startBaiduApp(getContext(),currentLocation.latitude,currentLocation.longitude,middleMarkerNowLatitude,middleMarkerNowLongitude);
                }
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }*/
    private void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(R.string.cancle_order);
        dialog.setMessage(R.string.wether_cancle_order_battery);
        dialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                httpCancleOrder();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initIntentMybattery() {
        Intent intent = new Intent(getContext(), MyBatteryActivity.class);
     //   intent.putExtra("notice_entry", true);
        startActivity(intent);
       /* if (getContext() instanceof HomeActivity) {
            ((HomeActivity) getContext()).finish();
        }*/
    }

    private void httpCancleOrder() {
        if (mloadingDialog == null) {
            mloadingDialog = CommonDialog.createLoadingDialog(getContext(), null);
            mloadingDialog.setCancelable(true);
        }
        if (isNetworkAvaliable()) {
            if (!mloadingDialog.isShowing()) {
                mloadingDialog.show();
            }
            httpCancleBattery(selectStationInfo.getpID());
        }
    }

    private void httpGetMyRent() {
        if (!isNetworkAvaliable()) return;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<MyRentInfo>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(MyRentInfo result) {
                if (result != null) {
                    if (selectStationInfo == null) {
                        selectStationInfo = new BatteryStationInfo();
                    }
                    selectStationInfo.setpID(result.getPowerStation().getPID());
                    selectStationInfo.setName(result.getPowerStation().getName());
                    if (mOrderQuerydata == null) {
                        mOrderQuerydata = new OrderQueryBean();
                    }

                    mBatteryNumber = result.getNum();
                    mOrderType = result.getType();
                    mOrderQuerydata.setJwt(result.getCode());
                    onSucessGetMyRentData(result);
                    //TODo 新加代码，待测试
                 /*   if (isFirstPolyline) {
                        if (result.getPowerStation().getPID().equals( mPreferences.getOrderStation())) {
                            isorderedSucessStatus = true;
                            navigateRide(result.getPowerStation().getLatitude(),result.getPowerStation().getLongitude());
                        }
                        isFirstPolyline = false;
                    }*/

                } else {
                    mPreferences.setOrderStation(null);
                    mPreferences.setOrderStationImgUrl(null);
                }
            }
        };
        HttpMethods.getInstance().getMyRent(new ProgressSubscriber(subscriberOnNextListener, getContext(), null), mPreferences.getToken());
    }


    public void onSucessGetMyRentData(MyRentInfo data) {
        statusInfoPan.setVisibility(View.VISIBLE);
        mOrderPanel.setVisibility(View.GONE);
        mCancleOrderPanel.setVisibility(View.VISIBLE);
        //隐藏刷新按钮
        mQrcode.setText(R.string.scan_open_station);
        mFresh.setVisibility(View.GONE);
        mLocation.setVisibility(View.GONE);
        String address = data.getPowerStation().getName();
        mCancleAddress.setText(address + "");
        mFetch.setText(data.getCode() + "");
        mCuntDownTiem.start(data.getExpire() * 1000);
    }

    private void httpCancleBattery(final String pid) {
        if (!isNetworkAvaliable()) {
            return;
        }
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Integer>() {
            @Override
            public void onError(Throwable e) {
                if (mloadingDialog != null && mloadingDialog.isShowing()) {
                    mloadingDialog.dismiss();
                }
                if (e.getMessage() == null) {
                    Toast.makeText(getContext(), R.string.cancle_order_fail, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNext(Integer result) {
                if (mOrderQuerydata != null && mOrderQuerydata.getJwt() != 0) {
                    httpCancleOrderQuery(pid, mOrderQuerydata.getJwt());
                }
            }
        };
        HttpMethods.getInstance().cancleOrder(new ProgressSubscriber(subscriberOnNextListener, getContext(), null), mPreferences.getToken(), pid, mOrderQuerydata.getJwt());
    }

    private void httpCancleOrderQuery(final String pid, final int code) {
        if (!isNetworkAvaliable()) return;
        if (isCancleOrderQueryQuest) {
            return;
        }
        isCancleOrderQueryQuest = true;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {
                    int code = ((ApiException) e).getCode();
                    if (code == 604) {
                        isCancleOrderQueryQuest = false;
                        httpCancleOrderQuery(pid, code);
                    } else {
                        if (mloadingDialog != null && mloadingDialog.isShowing()) {
                            mloadingDialog.dismiss();
                        }
                        HttpFailMessage.showfailMessage(getContext(), null, e);
                    }
                } else {
                    if (mloadingDialog != null && mloadingDialog.isShowing()) {
                        mloadingDialog.dismiss();
                    }
                    HttpFailMessage.showfailMessage(getContext(), null, e);
                }
                isCancleOrderQueryQuest = false;
                if (cameraChangeFinishlatitude != 0 && cameraChangeFinishlongitude != 0) {
                    httpGetStation(cameraChangeFinishlatitude, cameraChangeFinishlongitude, 20000);
                }
            }

            @Override
            public void onNext(Object result) {
                if (mloadingDialog != null && mloadingDialog.isShowing()) {
                    mloadingDialog.dismiss();
                }
                mOrderQuerydata = null;
                Toast.makeText(getContext(), R.string.cancle_order_sucess, Toast.LENGTH_LONG).show();
                statusInfoPan.setVisibility(View.GONE);
                //显示刷新按钮
                mQrcode.setText(R.string.scan_exchange_battery);
                mFresh.setVisibility(View.VISIBLE);
                mLocation.setVisibility(View.VISIBLE);
                isCancleOrderQueryQuest = false;
                isorderedSucessStatus = false;
                mPreferences.setOrderStation(null);
                mPreferences.setOrderStationImgUrl(null);
                if (polyline != null) {
                    polyline.remove();
                    middleMarkerNowLatitude = 0;
                    middleMarkerNowLongitude = 0;
                }
            }
        };
        HttpMethods.getInstance().cancleOrderQuery(new ProgressSubscriber(subscriberOnNextListener, getContext(), null), mPreferences.getToken(), pid, code);
    }

    private void orderHttpMethod() {
        if (!mRadioBtnOne.isChecked() && !mRadioBtnTwo.isChecked()) {
            // showSnackbar("请选择电池数量"); 此处视图被覆盖
            Toast.makeText(getContext(), R.string.select_battery_num, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mloadingDialog == null) {
            mloadingDialog = CommonDialog.createLoadingDialog(getContext(), null);
            mloadingDialog.setCancelable(true);
        }
        if (isNetworkAvaliable()) {
            if (!mloadingDialog.isShowing()) {
                mloadingDialog.show();
            }
            if (isOrderBatteryRequest) {
                return;
            }
            isOrderBatteryRequest = true;
            isorderedSucessStatus = true;
            httpRentBattery(selectStationInfo.getpID(), mBatteryordernumber, mOrderType);
        }
    }

    private void httpRentBattery(final String pID, int num, final int type) {
        if (!isNetworkAvaliable()) return;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                if (mloadingDialog != null && mloadingDialog.isShowing()) {
                    mloadingDialog.dismiss();
                }
                HttpFailMessage.showfailMessage(getContext(), null, e);
                isOrderBatteryRequest = false;

            }

            @Override
            public void onNext(Object result) {
                //result 为生成的临时标记位
                if (result != null) {
                    morderNum = String.valueOf(result);
                    httpOrderBatteryQuery(pID, morderNum);
                }
                isOrderBatteryRequest = false;
            }
        };
        HttpMethods.getInstance().orderBattery(new ProgressSubscriber(subscriberOnNextListener, getContext()), mPreferences.getToken(), pID, num, type);
    }

    private void httpOrderBatteryQuery(final String pID, final String no) {
        if (!isNetworkAvaliable()) return;
        if (isOrderQueryRequest) {
            return;
        }
        isOrderQueryRequest = true;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<OrderQueryBean>() {
            @Override
            public void onError(Throwable e) {
             /*   if (System.currentTimeMillis() < mHttpCurrentTime + 10000) {
                    isOrderQueryRequest = false;
                    httpOrderBatteryQuery(pID, no);
                } else {*/
                if (e instanceof ApiException) {
                    int code = ((ApiException) e).getCode();
                    if (code == 604) {
                        isOrderQueryRequest = false;
                        httpOrderBatteryQuery(pID, no);
                    } else {
                        if (mloadingDialog != null && mloadingDialog.isShowing()) {
                            mloadingDialog.dismiss();
                        }
                        HttpFailMessage.showfailMessage(getContext(), String.valueOf(TypeStatus.ORDER_QUERY_TYPE), e);
                    }

                } else {
                    if (mloadingDialog != null && mloadingDialog.isShowing()) {
                        mloadingDialog.dismiss();
                    }
                    HttpFailMessage.showfailMessage(getContext(), null, e);
                }
                //    }
                isOrderQueryRequest = false;
            }

            @Override
            public void onNext(OrderQueryBean result) {
                if (mloadingDialog != null && mloadingDialog.isShowing()) {
                    mloadingDialog.dismiss();
                }
                if (result != null) {
                    LogUtil.d("httpOrderBatteryQuery result:" + result);
                    mOrderQuerydata = result;
                    mOrderPanel.setVisibility(View.GONE);
                    mCancleOrderPanel.setVisibility(View.VISIBLE);

                    //隐藏刷新按钮
                    mQrcode.setText(R.string.scan_open_station);
                    mFresh.setVisibility(View.GONE);
                    mLocation.setVisibility(View.GONE);

                    if (selectStationInfo != null) {
                        mCancleAddress.setText(selectStationInfo.getName());
                    }
                    int jwt = result.getJwt();
                    mFetch.setText(jwt + "");
                    mCuntDownTiem.start(result.getExpire() * 1000);
                }
                if (selectStationInfo != null) {
                    for (int i = 0; i < mStationList.size(); i++) {
                        if (selectStationInfo.getpID().equals(mStationList.get(i).getpID())) {
                            Marker marker = markers.get(selectStationInfo.getpID());
                            marker.remove();
                            loadImageSimpleTarget(selectStationInfo.getImg());
                            mPreferences.setOrderStation(selectStationInfo.getpID());
                            mPreferences.setOrderStationImgUrl(selectStationInfo.getImg());
                            break;
                        }
                    }
                }
                isorderedSucessStatus = true;
                isOrderQueryRequest = false;
                notShowOtherStation();
            }
        };

        HttpMethods.getInstance().orderBatteryQuery(new ProgressSubscriber(subscriberOnNextListener, getContext(), null), mPreferences.getToken(), pID, no);
    }

    public void notShowOtherStation() {
        for (int i = 0; i < mStationList.size(); i++) {
            BatteryStationInfo info = mStationList.get(i);
            Marker marker = markers.get(info.getpID());
            if (marker != null) {
                if (!selectStationInfo.getpID().equals(info.getpID())) {
                    markers.remove(info.getpID());
                    marker.remove();
                }
            }
        }
    }

    private void loadImageSimpleTarget(String url) {

        Glide.with(getContext())
                .load(url)
                .asBitmap()   //强制转换Bitmap
                .centerCrop()
                .transform(new GlideCircleTransform(getContext()))
                .into(target);

    }

    public void circleImg(ImageView img, Bitmap bitmap2) {
        Bitmap bitmap1 = ((BitmapDrawable) getResources().getDrawable(
                R.mipmap.battery_station_img_bg)).getBitmap();
        Drawable[] array = new Drawable[2];
        array[0] = new BitmapDrawable(bitmap1);
        array[1] = new BitmapDrawable(bitmap2);
        LayerDrawable la = new LayerDrawable(array);
        // 其中第一个参数为层的索引号，后面的四个参数分别为left、top、right和bottom
        la.setLayerInset(0, 0, 0, 0, 0);
        la.setLayerInset(1, 20, 20, 20, 70);
        img.setImageDrawable(la);
    }

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            ImageView imageView = (ImageView) getView().findViewById(R.id.battery_ordered_img);
            //   imageView.setImageBitmap(bitmap);
            circleImg(imageView, bitmap);
            imageView.setVisibility(View.VISIBLE);
            imageView.setDrawingCacheEnabled(true);
            Marker marker = drawMark(new LatLng(selectStationInfo.getLatitude(), selectStationInfo.getLongitude()), imageView.getDrawingCache(true));
            imageView.setDrawingCacheEnabled(false);
            markers.put(selectStationInfo.getpID(), marker);
            marker.setTitle(selectStationInfo.getpID());
        }
    };

    private void initializeScan() {
        IntentIntegrator.forSupportFragment(this)
                .setOrientationLocked(false)
                .addExtra(QRCodeActivity.QR_TYPE_TARGET, QRCodeActivity.QR_TYPE_STATION)
                .setCaptureActivity(QRCodeActivity.class)
                .initiateScan(); //  初始化扫描
    }

    public Dialog mapNoticeDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.notice_dialog_layout, null);// 得到加载view
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.notice_dialog);// 加载布局
        TextView tipTextView = (TextView) v.findViewById(R.id.notice_sure);// 提示文字
        final Dialog dialog = new Dialog(getContext(), R.style.dialog_style);// 创建自定义样式dialog
        dialog.setCancelable(true);

        dialog.setContentView(layout);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.65); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.8); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);

        tipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initNotice();
                dialog.dismiss();
            }
        });
        return dialog;
    }


    public Dialog showOperateDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.operate_dialog_layout, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.operate_dialog_view);// 加载布局
        TextView fetch = (TextView) v.findViewById(R.id.fetch_battery);
        TextView exchange = (TextView) v.findViewById(R.id.exchange_battery);
        TextView cancleDialog = (TextView) v.findViewById(R.id.dialog_cancle);
        final Dialog dialog = new Dialog(getContext(), R.style.dialog_style);// 创建自定义样式dialog
        dialog.setCancelable(false);
        dialog.setContentView(layout);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        v.measure(0, 0);
        lp.height = v.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanResult) {
                    createBatteryNUmDialog().show();
                    mOrderType = TypeStatus.FETCH_TYPE;
                } else {
                    showOperateView(TypeStatus.FETCH_TYPE);
                }
                isScanResult = false;

                dialog.dismiss();
            }
        });

        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isScanResult) {
                    mOrderType = TypeStatus.EXCHANGE_TYPE;
                    createBatteryNUmDialog().show();
                } else {
                    showOperateView(TypeStatus.EXCHANGE_TYPE);
                }
                isScanResult = false;
                dialog.dismiss();
            }
        });
        cancleDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isScanResult = false;
                if (polyline != null) {
                    polyline.remove();
                    middleMarkerNowLatitude = 0;
                    middleMarkerNowLongitude = 0;
                }
                isorderedSucessStatus = false;
            }
        });
        return dialog;
    }

    public Dialog createBatteryNUmDialog() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.battery_num_dialog_layout, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.battery_num_dialog);// 加载布局
        mDialogCancleBtn = (TextView) v.findViewById(R.id.dialog_num_cancle);
        mOneBatteryBtn = (TextView) v.findViewById(R.id.dialog_one_battery);
        mTwoBatteryBtn = (TextView) v.findViewById(R.id.dialog_two_battery);
        final Dialog numDialog = new Dialog(getContext(), R.style.dialog_style);// 创建自定义样式dialog
        numDialog.setContentView(layout);
        Window dialogWindow = numDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        v.measure(0, 0);
        lp.height = v.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);

        mOneBatteryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //111   SelectFetchBatteryNumber=1;
                mBatteryNumber = 1;
                scanResultHttp(numDialog);
            }
        });
        mTwoBatteryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //111   SelectFetchBatteryNumber=2;
                mBatteryNumber = 2;
                scanResultHttp(numDialog);
            }
        });
        mDialogCancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numDialog.dismiss();
                if (polyline != null) {
                    polyline.remove();
                    middleMarkerNowLatitude = 0;
                    middleMarkerNowLongitude = 0;
                }
                isorderedSucessStatus = false;
            }
        });

        return numDialog;
    }

    private void scanResultHttp(Dialog numDialog) {
        if (mloadingDialog == null) {
            mloadingDialog = CommonDialog.createLoadingDialog(getContext(), null);
            mloadingDialog.setCancelable(true);
        }
        numDialog.dismiss();
        if (isNetworkAvaliable()) {
            if (!mloadingDialog.isShowing()) {
                mloadingDialog.show();
            }
      /*

            if (mOrderQuerydata != null) {
                httpScan(mPID, mBatteryNumber, mOrderType, mOrderQuerydata.getJwt());
            } else {

            }*/
            httpScan(mPID, mBatteryNumber, mOrderType, -1);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            String pID = null;
            if (resultCode == QRCodeActivity.MANUAL_RESULT_CODE) {
                pID = data.getStringExtra(QRCodeActivity.MANUAL_INPUT_TARGET);
            } else {
                IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (intentResult != null) {
                    pID = intentResult.getContents();
                    LogUtil.v("scan qr code pID:" + pID);
                }
            }

            if (!TextUtils.isEmpty(pID)) {
                if (pID.length() > 12) {
                    int index = pID.indexOf("pid=");
                    if (index > 0) {
                        pID = pID.substring(index + 4);
                    }
                }
                mPID = pID;
                isScanResult = true;
                statusInfoPan.setVisibility(View.GONE);

                if (mOrderQuerydata != null) {
                    httpScan(pID, mBatteryNumber, mOrderType, mOrderQuerydata.getJwt());
                } else {
                    if (HAVE_NOT_FETCH_BATTERY) {
                        showOperateDialog().show();
                    } else {
                        mOrderType = TypeStatus.EXCHANGE_TYPE;
                        isScanResult = false;
                        createBatteryNUmDialog().show();
                    }
                }
            }
        }
    }

    private void httpScan(final String pID, int num, int type, int code) {
        isScanResult = false;
        if (!isNetworkAvaliable()) return;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                if (mloadingDialog != null && mloadingDialog.isShowing()) {
                    mloadingDialog.dismiss();
                }
                if (e instanceof ApiException) {
                    HttpFailMessage.showfailMessage(getContext(), null, e);
                } else {
                    Toast.makeText(getContext(), R.string.open_station_fail, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNext(Object result) {
                //result 为生成的临时标记位
                if (result != null) {
                    morderNum = String.valueOf(new DecimalFormat("#").format(result));
                    httpScanQuery(pID, morderNum);
                } else {
                    if (mloadingDialog != null && mloadingDialog.isShowing()) {
                        mloadingDialog.dismiss();
                    }
                    Toast.makeText(mActivity, R.string.cont_get_query_code, Toast.LENGTH_SHORT).show();
                }
            }
        };

        HttpMethods.getInstance().OrderScan(new ProgressSubscriber(subscriberOnNextListener, getContext()), mPreferences.getToken(), pID, num, type, code);
    }

    private void httpScanQuery(final String pID, final String no) {
        if (!isNetworkAvaliable()) return;
        if (ishttpScanQueryQuest) {
            return;
        }
        ishttpScanQueryQuest = true;
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<String>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {
                    int code = ((ApiException) e).getCode();
                    if (code == 604) {
                        ishttpScanQueryQuest = false;
                        httpScanQuery(pID, no);
                    } else {
                        if (mloadingDialog != null && mloadingDialog.isShowing()) {
                            mloadingDialog.dismiss();
                        }
                        HttpFailMessage.showfailMessage(getContext(), null, e);
                        mQrcode.setText(R.string.scan_exchange_battery);
                        mFresh.setVisibility(View.VISIBLE);
                        mLocation.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mloadingDialog != null && mloadingDialog.isShowing()) {
                        mloadingDialog.dismiss();
                    }
                    Toast.makeText(getContext(),R.string.open_station_fail, Toast.LENGTH_LONG).show();

                    mQrcode.setText(R.string.scan_exchange_battery);
                    mFresh.setVisibility(View.VISIBLE);
                    mLocation.setVisibility(View.VISIBLE);
                }

                ishttpScanQueryQuest = false;

            }

            @Override
            public void onNext(String result) {

                if (result != null) {
                    LogUtil.d("httpScanQuery result:" + result);
                }
                if (mloadingDialog != null && mloadingDialog.isShowing()) {
                    mloadingDialog.dismiss();
                }
                //      isorderedSucessStatus = false;
                //    Toast.makeText(getContext(), "开成功", Toast.LENGTH_SHORT).show();
                //  statusInfoPan.setVisibility(View.GONE);
                //隐藏刷新按钮
              /*  mQrcode.setText("扫码换电");
                mFresh.setVisibility(View.VISIBLE);
                mLocation.setVisibility(View.VISIBLE);*/
            }
        };

        HttpMethods.getInstance().OrderScanQuery(new ProgressSubscriber(subscriberOnNextListener, getContext(), null), mPreferences.getToken(), pID, no);
    }

}

