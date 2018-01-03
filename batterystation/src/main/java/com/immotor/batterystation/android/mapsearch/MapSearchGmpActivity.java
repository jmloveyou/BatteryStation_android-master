package com.immotor.batterystation.android.mapsearch;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.entity.SearchBean;
import com.immotor.batterystation.android.ui.activity.HomeActivity;
import com.immotor.batterystation.android.ui.base.BaseActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by jm on 2017/12/19 0019.
 */

public class MapSearchGmpActivity extends BaseActivity implements TextWatcher,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{
    private int DATA_TIMEOUT =  50000;
    private ImageView mClear;
    private ImageView mBack;
    private EditText mEditText;
    private RecyclerView mRecyView;
    private MapSearchGmpAdapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private final static int FLG_UPDATE_LIST=100;
    private final static int FLG_SEARCH_LATLNG=101;
    private List<SearchBean> addressList = new ArrayList<>();// poi数据

    static class MyHandler extends Handler {
        WeakReference<MapSearchGmpActivity> mActivity;


        MyHandler(MapSearchGmpActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MapSearchGmpActivity theActivity = mActivity.get();
            if(theActivity == null){
                return;
            }
            switch (msg.what) {
                case FLG_UPDATE_LIST:
                    if(!theActivity.isStop()) {  //不可搜索状态不显示搜索列表
                        if (msg.obj != null) {
                            theActivity.addressList.addAll((List<SearchBean>) (msg.obj));
                            if (theActivity.mAdapter!=null) {
                                theActivity.mAdapter.replaceData((List<SearchBean>) (msg.obj));
                                theActivity.mAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    break;
                case FLG_SEARCH_LATLNG:
                    LatLng latLng = (LatLng) msg.obj;
                    Intent intent = new Intent(theActivity, HomeActivity.class);
                    intent.putExtra(AppConstant.KEY_LATITUDE_TYPE, latLng.latitude);
                    intent.putExtra(AppConstant.KEY_LONGITUDE_TYPE, latLng.longitude);
                    theActivity.startActivity(intent);
                    theActivity.finish();
                    break;
                default:
                    break;
            }
        }
    }

    private MyHandler mHandler = new MyHandler(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_map_search_google);
    }

    @Override
    public void initUIView() {

        mClear = (ImageView) findViewById(R.id.search_clear_view);
        mClear.setOnClickListener(this);
        mBack = (ImageView) findViewById(R.id.search_action_bar_btn_return);
        mBack.setOnClickListener(this);
        mEditText = (EditText) findViewById(R.id.search_edit_view);
        mEditText.addTextChangedListener(this);
        mRecyView = (RecyclerView) findViewById(R.id.search_recyView);
        mRecyView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MapSearchGmpAdapter(R.layout.item_search_layout);
        mRecyView.setAdapter(mAdapter);
        initInputListener();
        initRecyListener();
        initMap();
    }

    private void initMap() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    private void initRecyListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mAdapter.getData().get(position).getPlaceId() == null) {
                    Toast.makeText(MapSearchGmpActivity.this, R.string.cont_get_point, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    getPlaceByPlaceId(mAdapter.getData().get(position).getPlaceId());
                }
            }
        });
    }
    private void getPlaceByPlaceId(String placeId){
        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            final Place myPlace = places.get(0);
                            Message message = new Message();
                            message.obj = myPlace.getLatLng();
                            message.what = FLG_SEARCH_LATLNG;
                            mHandler.sendMessage(message);
                        } else {
                            showSnackbar(R.string.cont_get_point);
                            //LogUtil.v("Place not found");
                        }
                        places.release();
                    }
                });

    }
    private void initInputListener() {
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (mEditText.getText().toString().trim().isEmpty()) {
                        Toast.makeText(MapSearchGmpActivity.this, R.string.import_search_address, Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    SearchTask  searchTask = new SearchTask();
                    searchTask.execute(mEditText.getText().toString().trim());
                    return true;
                } else {
                    return false;
                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        SearchTask searchTask = new SearchTask();
        searchTask.execute(s.toString());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class SearchTask extends AsyncTask<String, Integer, AutocompletePredictionBuffer> {

        @Override
        protected AutocompletePredictionBuffer doInBackground(String... params) {
            PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, params[0], null, null);
            return result.await(DATA_TIMEOUT, TimeUnit.MILLISECONDS);
        }

        @Override
        protected void onPostExecute(AutocompletePredictionBuffer result){
            if (result != null) {
                com.google.android.gms.common.api.Status status = result.getStatus();
                if (status.isSuccess()) {
                    Iterator<AutocompletePrediction> iterator = result.iterator();
                    List<SearchBean> tmpList = new ArrayList<>();
                    while (iterator.hasNext()) {
                        AutocompletePrediction prediction = iterator.next();
                        SearchBean bean = new SearchBean();
                        bean.setTitle(prediction.getFullText(null).toString());
                        bean.setPlaceId(prediction.getPlaceId());
                        tmpList.add(bean);
                    }
                    Message message = new Message();
                    message.what = FLG_UPDATE_LIST;
                    message.obj = tmpList;
                    mHandler.sendMessage(message);

                }
                result.release();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mEditText.getText().toString().isEmpty()) {
            mEditText.setHint(R.string.import_address);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.search_clear_view:
                mEditText.setText("");
                mEditText.setHint(R.string.import_address);
                break;

            case R.id.search_action_bar_btn_return:
                Intent intent = new Intent(MapSearchGmpActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MapSearchGmpActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEditText.removeTextChangedListener(this);
    }

}
