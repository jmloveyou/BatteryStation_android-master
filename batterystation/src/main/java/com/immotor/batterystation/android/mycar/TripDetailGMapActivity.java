package com.immotor.batterystation.android.mycar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.entity.TripBean;
import com.immotor.batterystation.android.entity.TripDetailBean;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.carhttp.CarHttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.util.Common;
import com.immotor.batterystation.android.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

public class TripDetailGMapActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.chart)
    ComboLineColumnChartView chartView;
    @Bind(R.id.mileage_value)
    TextView mileageValue;
    @Bind(R.id.time_value)
    TextView timeValue;
    @Bind(R.id.battery_value)
    TextView batteryValue;
    @Bind(R.id.time_unit)
    TextView timeUnitText;
    @Bind(R.id.trip_unit)
    TextView tripUnit;

    private Bundle savedInstanceState;
    private LatLng mStartPoint;// = new LatLonPoint(39.925315, 116.439521);//起点，
    private LatLng mEndPoint;// = new LatLonPoint(39.926193, 116.491714);//终点，
    List<LatLng> pointList = new ArrayList<>();
    List<String> timeList = new ArrayList<>();
    List<Float> speedList = new ArrayList<>();

    List<Column> batteryList = new ArrayList<>();

    private ComboLineColumnChartData data;
    private int numberOfPoints;

    private TripBean tripBean;
    private long startTime;
    private long endTime;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mGoogleMap;
    private PolylineOptions lineOptions;
    private float maxSpeed = 0;   //最大速度
    private String sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_trip_detail_gmap);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
        }

        initMap();

    }
    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }
    @Override
    public void initUIView() {
        mToolbar.setTitle(getString(R.string.trip_detail));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();

        tripBean = (TripBean)intent.getSerializableExtra("TRIP_BEAN");
        sid = getIntent().getStringExtra(AppConstant.KEY_ENTRY_BATTERY_CAR_TRIP_SID);
        startTime = tripBean.getSTime();
        endTime = tripBean.getETime();

        float milesValue = tripBean.getMiles();
        mileageValue.setText(String.valueOf(milesValue));
        tripUnit.setText(getString(R.string.km));

        String timeValueStr = DateTimeUtil.secondToTimeString(tripBean.getCostTime());
        if (timeValueStr.contains("h")){
            timeUnitText.setText(getString(R.string.time_h));
            timeValueStr = timeValueStr.replace("h", "");
        }else {
            timeUnitText.setText(getString(R.string.time_m));
            timeValueStr = timeValueStr.replace("m", "");
        }
        timeValue.setText(timeValueStr);
        batteryValue.setText(String.valueOf(tripBean.getSoc())+"%");
        httpRequestTrackData();
    }

    private void initMap(){
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null){
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_trip_track_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_share){

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * get track data by day type = 0
     * sID	String	32位string,scooter id	true
     type	int	3:年度轨迹，2：一周的轨迹，1：一天的轨迹，0：轨迹详情	true
     token	string	32位string,用户调用接口凭证	true
     param	string	if type=3时，param为year,如“2016”，type 为 2, param可为空;type=1, param为日期，如“2016-02-26”；type=0, "param": "1465328465701,1465328492496" 即开始时间和结束时间的时间戳	false
     */
    private void httpRequestTrackData(){
        if (!isNetworkAvaliable()) return;
        final String token = mPreferences.getToken();
        Map<String ,Object> map = new HashMap<>();
        map.put("token", token);
        map.put("sID", sid);
        map.put("sTime", startTime);
        map.put("eTime", endTime);

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<List<TripDetailBean>>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(e.getMessage());
            }
            @Override
            public void onNext(List<TripDetailBean> result) {
                if (result != null) {
                    initPointList(result);
                    mStartPoint = pointList.get(0);
                    if (result.size() > 1) {
                        mEndPoint = pointList.get(result.size() - 1);
                        //requestWalkRoute(mStartPoint, pointList.get(1));
                    }
                    numberOfPoints = result.size();
                 //   generateData();
                    //画轨迹
                    drawTripLines();
                }else {
                    showSnackbar(R.string.no_data);
                }
            }
        };
        CarHttpMethods.getInstance().getTrackDetail(new ProgressSubscriber(subscriberOnNextListener, this), map);
    }


    private void initPointList(List<TripDetailBean> result){
        int len = result.size();

        for (int i=0; i<len; i++) {
            TripDetailBean tripDetailBean = result.get(i);

            double[] location = Common.getLatAndLonDouble(tripDetailBean.getLocation());
            LatLng point = new LatLng(location[0], location[1]);
            pointList.add(point);
           /* String time = DateTimeUtil.getTimeString(tripDetailBean.getTime());
            timeList.add(time);

            float speed = tripDetailBean.getSpeed();
            speed = speed * 3f;
            *//*if (speed < 1 ){
                speed = 1;
            }else {
                speed = speed * 3f;
            }*//*
            maxSpeed = maxSpeed > speed?maxSpeed:speed;
            speedList.add(speed);

            List<SubcolumnValue> values = new ArrayList<>();
            values.add(new SubcolumnValue(tripDetailBean.getSoc(), getResources().getColor(R.color.lightgreen_01)));    // for test data
            batteryList.add(new Column(values));*/
        }

    }

    private void drawTripLines(){
        if (mGoogleMap == null){
            return;
        }

        // Drawing polyline in the Google Map for the i-th route
        lineOptions = new PolylineOptions();
        lineOptions.addAll(pointList);
        lineOptions.width(15);
        lineOptions.color(getResources().getColor(R.color.colorAccent));
        mGoogleMap.addPolyline(lineOptions);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mStartPoint, 15));


        // draw point on line
        List<LatLng> middlePoints = new ArrayList<>();
        if (numberOfPoints > 10) {
            float next = numberOfPoints/10.0f;
            for (int i=0; i<10; i++){
                int index = (int)(i * next);
                LatLng point = pointList.get(index);
                middlePoints.add(point);
            }
        }else {
            for (int i=0; i<numberOfPoints; i++){
                LatLng point = pointList.get(i);
                middlePoints.add(point);
            }
        }
        for (LatLng latLng : middlePoints){
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(latLng).anchor(0.5f,0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_position_point)));
        }

        drawStartEndMarker();
    }



    private void generateData() {
        // Chart looks the best when line data and column data have similar maximum viewports.
        ColumnChartData columnChartData = generateColumnData();
        LineChartData lineChartData = generateLineData();
        Axis axisX = generateAxisData();
        data = new ComboLineColumnChartData(columnChartData, lineChartData);
        data.setAxisXBottom(axisX);
        chartView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        chartView.setComboLineColumnChartData(data);
    }

    private LineChartData generateLineData() {

        List<Line> lines = new ArrayList<>();

        List<PointValue> values = new ArrayList<>();

        if (numberOfPoints > 10) {
            float next = numberOfPoints/10.0f;
            for (int i=0; i<10; i++){
                int index = (int)(i * next);
                PointValue pointValue = new PointValue(i, speedList.get(index));
                values.add(pointValue);
            }
        }else {
            for (int j = 0; j < numberOfPoints; ++j) {
                float speed = speedList.get(j);
                values.add(new PointValue(j, speed));
            }
        }


        Line line = new Line(values);
        line.setColor(getResources().getColor(R.color.lightblue_01));
        line.setStrokeWidth(2);//画笔
        line.setPointRadius(3);//点半径
        line.setCubic(false);   // true 曲线  false 直线
        line.setHasLabels(false);
        line.setHasLines(true);
        line.setHasPoints(true);
        lines.add(line);

        //解决在部分情况线绘制到顶部变细
        List<PointValue> values2 = new ArrayList<>();
        values2.add(new PointValue(0, maxSpeed+5));
        Line line2 = new Line(values2);
        line2.setColor(0);
        line2.setHasLines(false);
        line2.setHasPoints(false);
        lines.add(line2);

        LineChartData lineChartData = new LineChartData(lines);
        return lineChartData;

    }

    private ColumnChartData generateColumnData() {
        List<Column> tmpList = new ArrayList<>();
        if (numberOfPoints > 10) {
            float next = numberOfPoints/10.0f;
            for (int i=0; i<10; i++){
                int index = (int)(i * next);
                tmpList.add(batteryList.get(index));
            }
        }else {
            tmpList.addAll(batteryList);
        }
        ColumnChartData columnChartData = new ColumnChartData(tmpList);
        columnChartData.setStacked(false);
        columnChartData.setFillRatio(0.1f);
        return columnChartData;
    }

    private Axis generateAxisData(){
        Axis axisX = new Axis().setHasLines(true);

        List<AxisValue> axisValues = new ArrayList<>();
        if (numberOfPoints > 10) {
            float next = numberOfPoints/10.0f;
            for (int i=0; i<10; i++){
                int index = (int)(i * next);
                axisValues.add(new AxisValue(i).setLabel(timeList.get(index)));
            }
        }else {
            for (int i = 0; i < numberOfPoints; i++) {
                axisValues.add(new AxisValue(i).setLabel(timeList.get(i)));
            }
        }
        axisX.setValues(axisValues);
        return axisX;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            super.onSaveInstanceState(outState);
        }catch (Exception e){}
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        //mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        try{
            if(mGoogleMap.getUiSettings()!=null) {
                mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
            }
        }catch (Exception e){}

    }


    private void drawStartEndMarker(){

        mGoogleMap.addMarker(new MarkerOptions()
                .position(mStartPoint).anchor(0.5f,0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_route_start)));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(mEndPoint).anchor(0.5f,0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_route_end)));

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
