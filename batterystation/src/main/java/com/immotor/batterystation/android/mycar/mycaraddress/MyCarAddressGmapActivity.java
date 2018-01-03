package com.immotor.batterystation.android.mycar.mycaraddress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.CameraUpdateFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.ui.base.BaseActivity;


public class MyCarAddressGmapActivity extends BaseActivity implements
        GoogleApiClient.ConnectionCallbacks,OnMapReadyCallback,LocationListener,GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{

    private GoogleMap mMap;

    private String currentAddress;// scooter current address
    private String location;
    private LatLng mLatLng;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mGoogleMap;
    private Location mCurrentLocation;
    private Marker myLocationMarker;
    private LocationRequest mLocationRequest;
    private Marker carMarker;
    private boolean locationed=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mycar_address_gmap);
        location = getIntent().getStringExtra(AppConstant.KEY_ENTRY_ADDRESSACTIVITY_LOCATION);
        if (location != null && !location.equals("0,0")) {
            mLatLng = new LatLng(Double.parseDouble(location.substring(0, location.lastIndexOf(","))), Double.parseDouble(location.substring(location.lastIndexOf(",") + 1)));
        }
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
    private void initMap(){
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void initUIView() {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.car_btn_location:
                if (mGoogleMap != null && mLatLng != null && !location.equals("0,0")) {
                    mGoogleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(mLatLng, 15));
                } else {
                    Toast.makeText(this, R.string.cont_get_car_location, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.car_btn_my_location:
                if (mGoogleMap != null && mCurrentLocation != null) {
                    mGoogleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude()), 15));
                }
                break;
            case R.id.home_actionbar_menu:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mCurrentLocation == null){ return;}
            com.google.android.gms.maps.model.LatLng currentLatLng = new com.google.android.gms.maps.model.LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mGoogleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            if (myLocationMarker == null) {
                myLocationMarker = drawMark(currentLatLng, R.mipmap.map_position_circle);
            }
            if (mLatLng != null) {
                if (carMarker == null) {
                    carMarker = drawMark(mLatLng, R.mipmap.my_car_loc_icon);
                } else {
                    carMarker.setPosition(mLatLng);
                }
                if (locationed) {
                    mGoogleMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                    locationed = false;
                }
            }
            if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    private Marker drawMark(LatLng latLng, int resId) {
        //画指针
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.anchor(0.5f, 1.0f);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(getBitmap(resId)));
        return mGoogleMap.addMarker(markerOption);
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

    private Marker drawMark(LatLng latLng, Bitmap bmp) {
        //画指针
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.anchor(0.5f, 1.0f);
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(bmp));
        return mGoogleMap.addMarker(markerOption);
    }

    protected void startLocationUpdates() {
        try {
            createLocationRequest();
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest,  this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(10000);    // 10秒更新一次
        mLocationRequest.setFastestInterval(5000);  //最快5秒更新一次
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (myLocationMarker!=null) {
            myLocationMarker.setPosition(new LatLng(location.getLatitude(),location.getLongitude()));
        }
    }
}
