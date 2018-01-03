package com.immotor.batterystation.android.mycar;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.entity.TripBean;
import com.immotor.batterystation.android.entity.TripDetailBean;
import com.immotor.batterystation.android.http.carhttp.CarHttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.util.Common;
import com.immotor.batterystation.android.util.DateTimeUtil;
import com.immotor.batterystation.android.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

public class TripDetailActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
//    @Bind(R.id.viewMap)

    MapView mapView;
    // for chartView
    @Bind(R.id.chart)
    ComboLineColumnChartView chartView;
    @Bind(R.id.mileage_value)
    TextView mileageValue;
    @Bind(R.id.time_value)
    TextView timeValue;
    @Bind(R.id.time_unit)
    TextView timeUnitText;
    @Bind(R.id.battery_value)
    TextView batteryValue;
    @Bind(R.id.trip_unit)
    TextView tripUnit;

    private Bundle savedInstanceState;
    private AMap aMap;

    private LatLng mStartPoint;// = new LatLonPoint(39.925315, 116.439521);//起点，
    private LatLng mEndPoint;// = new LatLonPoint(39.926193, 116.491714);//终点，
    List<LatLng> pointList = new ArrayList<>();
    //List<TrackResult> trackList = new ArrayList<>();
    List<String> timeList = new ArrayList<>();
    List<Float> speedList = new ArrayList<>();

    List<Column> batteryList = new ArrayList<>();


    private ComboLineColumnChartData data;
    private int numberOfPoints;

    private TripBean tripBean;
    private long startTime;
    private long endTime;

    private float maxSpeed = 0;   //最大速度
    private String sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(R.layout.activity_trip_detail);

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
        tripBean = (TripBean) intent.getSerializableExtra("TRIP_BEAN");
        sid = getIntent().getStringExtra(AppConstant.KEY_ENTRY_BATTERY_CAR_TRIP_SID);
        startTime = tripBean.getSTime();
        endTime = tripBean.getETime();
        mapView = (MapView) findViewById(R.id.viewMap);
        initMap();

        float milesValue = tripBean.getMiles();
        mileageValue.setText(String.valueOf(milesValue));
        tripUnit.setText(getString(R.string.km));

        String timeValueStr = DateTimeUtil.secondToTimeString(tripBean.getCostTime());
        if (timeValueStr.contains("h")) {
            timeUnitText.setText(getString(R.string.time_h));
            timeValueStr = timeValueStr.replace("h", "");
        } else {
            timeUnitText.setText(getString(R.string.time_m));
            timeValueStr = timeValueStr.replace("m", "");
        }
        timeValue.setText(timeValueStr);

        batteryValue.setText(String.valueOf(tripBean.getSoc()) + "%");
        httpRequestTrackData();

    }

    private void initMap() {
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        setMapCustomStyleFile(this);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                LogUtil.v(latLng.toString());
            }
        });

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
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mapView != null) {
            aMap.clear();
            aMap.removecache();
            aMap.stopAnimation();
            aMap = null;
            mapView.onDestroy();
            mapView = null;
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_trip_track_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * get track data by day type = 0
     * sID	String	32位string,scooter id	true
     * type	int	3:年度轨迹，2：一周的轨迹，1：一天的轨迹，0：轨迹详情	true
     * token	string	32位string,用户调用接口凭证	true
     * param	string	if type=3时，param为year,如“2016”，type 为 2, param可为空;type=1, param为日期，如“2016-02-26”；type=0, "param": "1465328465701,1465328492496" 即开始时间和结束时间的时间戳	false
     */
    private void httpRequestTrackData() {
        if (!isNetworkAvaliable()) return;
        final String token = mPreferences.getToken();
        Map<String, Object> map = new HashMap<>();
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
                    }
                    numberOfPoints = result.size();
                //    generateData();
                    //画轨迹
                    drawTripLines();
                } else {
                    showSnackbar(R.string.no_data);
                }
            }
        };
        CarHttpMethods.getInstance().getTrackDetail(new ProgressSubscriber(subscriberOnNextListener, this), map);
    }

    private void initPointList(List<TripDetailBean> result) {
        int len = result.size();

        for (int i = 0; i < len; i++) {
            TripDetailBean tripDetailBean = result.get(i);

            double[] location = Common.getLatAndLonDouble(tripDetailBean.getLocation());
            LatLng point = new LatLng(location[0], location[1]);
            pointList.add(point);
          /*  String time = DateTimeUtil.getTimeString(tripDetailBean.getTime());
            timeList.add(time);

            float speed = tripDetailBean.getSpeed();
            if (speed < 1) {
                speed = 1;
            } else {
                speed = speed * 3f;
                //}
                maxSpeed = maxSpeed > speed ? maxSpeed : speed;
                speedList.add(speed);

                List<SubcolumnValue> values = new ArrayList<>();
                values.add(new SubcolumnValue(tripDetailBean.getSoc(), getResources().getColor(R.color.lightgreen_01)));    // for test data
                batteryList.add(new Column(values));
            }*/
        }
    }

    private void drawTripLines() {
        if (aMap == null) {
            return;
        }
        List<LatLng> latLngs = new ArrayList<>();

        for (LatLng latLonPoint : pointList) {
            latLngs.add(latLonPoint);
        }

        aMap.addPolyline(new PolylineOptions()
                .addAll(latLngs)
                .width(13)
                .color(getResources()
                        .getColor(R.color.ripple_orange)));

        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mStartPoint, 17));


        // draw point on line
        List<LatLng> middlePoints = new ArrayList<>();
        if (numberOfPoints > 10) {
            float next = numberOfPoints / 10.0f;
            for (int i = 0; i < 10; i++) {
                int index = (int) (i * next);
                LatLng point = pointList.get(index);
                middlePoints.add(point);
            }
        } else {
            for (int i = 0; i < numberOfPoints; i++) {
                LatLng point = pointList.get(i);
                middlePoints.add(point);
            }
        }
        for (LatLng latLng : middlePoints) {
            aMap.addMarker(new MarkerOptions().
                    position(latLng).
                    anchor(0.5f, 0.5f).
                    icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(),
                                    R.mipmap.map_position_point))));
        }

        // start point;
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(mStartPoint);
        markerOption.anchor(0.5f, 0.5f);
        markerOption.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_route_start));
        aMap.addMarker(markerOption);
        // end point
        MarkerOptions markerOptionEnd = new MarkerOptions();
        markerOptionEnd.position(mEndPoint);
        markerOptionEnd.anchor(0.5f, 0.5f);
        markerOptionEnd.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_route_end));
        aMap.addMarker(markerOptionEnd);
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
                LogUtil.v("tttouch ");
                return true;
            }
        });
        chartView.setComboLineColumnChartData(data);
    }

    private LineChartData generateLineData() {

        List<Line> lines = new ArrayList<>();

        List<PointValue> values = new ArrayList<>();

        if (numberOfPoints > 10) {
            float next = numberOfPoints / 10.0f;
            for (int i = 0; i < 10; i++) {
                int index = (int) (i * next);
                PointValue pointValue = new PointValue(i, speedList.get(index));
                values.add(pointValue);
            }
        } else {
            for (int j = 0; j < numberOfPoints; ++j) {
                float speed = speedList.get(j);
                values.add(new PointValue(j, speed));
            }
        }


        Line line = new Line(values);
        line.setColor(getResources().getColor(R.color.lightblue_01));
        line.setStrokeWidth(2);//画笔
        line.setPointRadius(3);//点半径
        line.setCubic(false);    // true 曲线  false 直线
        line.setHasLabels(false);
        line.setHasLines(true);
        line.setHasPoints(true);
        lines.add(line);

        //解决在部分情况线绘制到顶部变细
        List<PointValue> values2 = new ArrayList<>();
        values2.add(new PointValue(0, maxSpeed + 5));
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
            float next = numberOfPoints / 10.0f;
            for (int i = 0; i < 10; i++) {
                int index = (int) (i * next);
                tmpList.add(batteryList.get(index));
            }
        } else {
            tmpList.addAll(batteryList);
        }
        ColumnChartData columnChartData = new ColumnChartData(tmpList);
        columnChartData.setStacked(false);
        columnChartData.setFillRatio(0.1f);
        return columnChartData;
    }

    private Axis generateAxisData() {
        Axis axisX = new Axis().setHasLines(true);

        List<AxisValue> axisValues = new ArrayList<>();
        if (numberOfPoints > 10) {
            float next = numberOfPoints / 10.0f;
            for (int i = 0; i < 10; i++) {
                int index = (int) (i * next);
                axisValues.add(new AxisValue(i).setLabel(timeList.get(index)));
            }
        } else {
            for (int i = 0; i < numberOfPoints; i++) {
                axisValues.add(new AxisValue(i).setLabel(timeList.get(i)));
            }
        }
        axisX.setValues(axisValues);
        return axisX;
    }

}
