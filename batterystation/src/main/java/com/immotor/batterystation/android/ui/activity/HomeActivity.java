package com.immotor.batterystation.android.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.immotor.batterystation.android.MyApplication;
import com.immotor.batterystation.android.MyConfiguration;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.bluetooth.DeviceDataService;
import com.immotor.batterystation.android.entity.MyComboBean;
import com.immotor.batterystation.android.entity.MybatteryListBean;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.mapsearch.MapSearchActivity;
import com.immotor.batterystation.android.mapsearch.MapSearchGmpActivity;
import com.immotor.batterystation.android.mycar.mycarmain.MyCarActivity;
import com.immotor.batterystation.android.mycombo.MyComboActivity;
import com.immotor.batterystation.android.mywallet.mywalletmian.MyWalletAtivity;
import com.immotor.batterystation.android.service.HeartBeatService;
import com.immotor.batterystation.android.ui.adapter.CustomFragmentPagerAdapter;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.ui.base.BaseFragment;
import com.immotor.batterystation.android.ui.fragment.GoogleMapFragment;
import com.immotor.batterystation.android.ui.fragment.MapFragment;
import com.immotor.batterystation.android.mybattery.MyBatteryActivity;
import com.immotor.batterystation.android.ui.views.CircleImageView;
import com.immotor.batterystation.android.ui.views.NoScrollViewPager;
import com.immotor.batterystation.android.util.DateTimeUtil;
import com.immotor.batterystation.android.util.FileUtil;
import com.immotor.batterystation.android.util.IMMLeaks;
import com.immotor.batterystation.android.util.LogUtil;
import com.immotor.batterystation.android.util.PermissionUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;


import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static com.immotor.batterystation.android.app.AppConstant.KEY_ENTRY_HOMEATY_TYPE;
import static com.immotor.batterystation.android.ui.activity.FirstGuideActivity.FIRST_TARGET;

/**
 * Created by ${jm} on 2017/7/18 0018.
 */

public class HomeActivity extends BaseActivity {
    //设置tab数量
    private final static int TAB_COUNT = 2;
    //默认加载页
    private final static int DEFULT_LOAD_INDEX = 0;
    private final static int DEFULT_GOOGLE_INDEX = 1;
    private int mCurIndex;
    public final static int TAB_MAP_INDEX = 0;
    public final static int TAB_GOOGLE_INDEX = 1;
    private int mType;
    private MapFragment mMapFragment;
    private GoogleMapFragment mGoogleMapFragment;
    private View navigationAvatar;
    private View navigationBattery;
    private View navigationWallet;
    private View navigationInclusive;
    private View navigationGuide;
    private View navigationAbout;
    private CircleImageView headPicture;
    private TextView mNameView;
    private TextView mCreditView;
    private DrawerLayout mDrawerLayout;
    private ImageView mMenuButton;
    private TextView mTittleText;
    private RadioGroup mIndicatorRadioGroup;
    private NavigationView mNavigation;
    private NoScrollViewPager mViewPager;
    private boolean isRequestPermission = false;
    private double mLatitude;
    private double mLongitude;
    private View navigationCar;
  //  private boolean isFirst = false;
  //  private int entryHome;
    private final int DOWNLOAD_COMPLETE=0;

    private android.os.Handler lbeHandler = new android.os.Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_COMPLETE:
                    updateAvatar(String.valueOf(msg.obj));
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        initData();
        initView();
        initHomeUIView();
//        if (entryHome==1) {
//            httpMyBatteryRequest();
//        }
    }

//    private void initFirstGuid() {
//        //    boolean comboStatus = mPreferences.getComboStatus();
//        //   boolean mybatteryStatus = mPreferences.getMybatteryStatus();
//        //   if (!comboStatus && !mybatteryStatus) {
//        if (isFirst) {
//            return;
//        }
//        startActivity(new Intent(this, FirstGuideActivity.class));
//        finish();
//        //   }
//    }

    private void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMenuButton = (ImageView) findViewById(R.id.home_actionbar_menu);
        mTittleText = (TextView) findViewById(R.id.home_actionbar_text);
        mIndicatorRadioGroup = (RadioGroup) findViewById(R.id.home_indicator_radioGroup);
        mNavigation = (NavigationView) findViewById(R.id.navigation_view);
        mViewPager = (NoScrollViewPager) findViewById(R.id.home_viewpager_content);
        mMenuButton.setImageDrawable(getResources().getDrawable(R.mipmap.home_new_actionbar_menu));
       /* RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mMenuButton.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.setMargins(11, 30, 0, 0);
        mMenuButton.setLayoutParams(layoutParams);*/
        mTittleText.setText(getResources().getString(R.string.home_tittle));
        ImageView search = (ImageView) findViewById(R.id.home_actionbar_search);
        search.setVisibility(View.VISIBLE);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DateTimeUtil.isInChina()) {
                    startActivity(new Intent(HomeActivity.this, MapSearchActivity.class));
                } else {
                    startActivity(new Intent(HomeActivity.this, MapSearchGmpActivity.class));
                }
                finish();
            }
        });
    }

    private void initData() {

  //      entryHome = getIntent().getIntExtra(KEY_ENTRY_HOMEATY_TYPE, -1);
   //     isFirst = getIntent().getBooleanExtra(FIRST_TARGET, false);
        mLatitude = getIntent().getDoubleExtra(AppConstant.KEY_LATITUDE_TYPE, -1);
        mLongitude = getIntent().getDoubleExtra(AppConstant.KEY_LONGITUDE_TYPE, -1);
        MyApplication.setWxpayStatus(false);
    }

    public void initHomeUIView() {
        initToolbar();
        initViewPager();
        initListener();
        initNavigation();
    }

    private void initNavigation() {
        View view = mNavigation.getHeaderView(0);
        navigationAvatar = view.findViewById(R.id.head);
        navigationBattery = view.findViewById(R.id.menu_battery);
        navigationWallet = view.findViewById(R.id.menu_wallet);
        navigationInclusive = view.findViewById(R.id.menu_inclusive);
        navigationGuide = view.findViewById(R.id.menu_guide);
        navigationAbout = view.findViewById(R.id.menu_about);
        navigationCar = view.findViewById(R.id.menu_car);
        headPicture = (CircleImageView) (view.findViewById(R.id.avatar_view));
//        walletMoney = (TextView)(view.findViewById(R.id.wallet_value));
        mNameView = (TextView) (view.findViewById(R.id.nickname_view));
        mCreditView = (TextView) (view.findViewById(R.id.credit_view));
        navigationAvatar.setOnClickListener(menuClickListener);
        navigationBattery.setOnClickListener(menuClickListener);
        navigationInclusive.setOnClickListener(menuClickListener);
        navigationWallet.setOnClickListener(menuClickListener);
        navigationGuide.setOnClickListener(menuClickListener);
        navigationAbout.setOnClickListener(menuClickListener);
        navigationCar.setOnClickListener(menuClickListener);
    }

    View.OnClickListener menuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.head:
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    break;
                case R.id.menu_battery:
                    startActivity(new Intent(HomeActivity.this, MyBatteryActivity.class));
                    break;
                case R.id.menu_wallet:
                    startActivity(new Intent(HomeActivity.this, MyWalletAtivity.class));
                    break;
                case R.id.menu_car:
                    startActivity(new Intent(HomeActivity.this, MyCarActivity.class));
                    break;
                case R.id.menu_inclusive:
                    startActivity(new Intent(HomeActivity.this, MyComboActivity.class));
                    break;
                case R.id.menu_guide:
                  //  Toast.makeText(HomeActivity.this, "暂未开放", Toast.LENGTH_SHORT).show();
                    // startActivity(new Intent(HomeActivity.this,GuideActivity.class));
                    break;
                case R.id.menu_about:
                    startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                    break;
            }
            mDrawerLayout.closeDrawers();
        }
    };

    private void initViewPager() {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        CustomFragmentPagerAdapter mPagerAdapter = new CustomFragmentPagerAdapter(mFragmentManager, mOnFragmentChangeListener);
        mPagerAdapter.setCount(TAB_COUNT);
        mViewPager.setOffscreenPageLimit(TAB_COUNT);
        mViewPager.setNoScroll(true);
        mViewPager.setAdapter(mPagerAdapter);
        if (DateTimeUtil.isInChina()) {
            mCurIndex = DEFULT_LOAD_INDEX;
        } else {
            mCurIndex = DEFULT_GOOGLE_INDEX;
        }
        mViewPager.setCurrentItem(mCurIndex);
        mViewPager.setScrollContainer(false);

    }

    private void updateMyProfile() {
        String name = mPreferences.getUserName();
        if (TextUtils.isEmpty(name)) {
            name = mPreferences.getPhone();
            if (name.length() > 7) {
                name = name.substring(0, 3) + "****" + name.substring(7, name.length());
            }
        }
        mNameView.setText(name);
        //  mCreditView.setText("账户 "+mPreferences.getPhone());
        headPicture.setBackgroundResource(R.mipmap.ic_avatar_default);
        String avatar = mPreferences.getAvatar();
        if (!TextUtils.isEmpty(avatar)) {
            final String originalPath = MyConfiguration.AVATAR_PATH + FileUtil.getServerFileName(avatar);
            File file = new File(originalPath);
            if (!file.exists()) {
                FileDownloader.getImpl().create(avatar).setPath(originalPath).setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtil.d("filedownload pending " + soFarBytes + ", " + totalBytes);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtil.d("filedownload progress " + soFarBytes + ", " + totalBytes);
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        LogUtil.v("filedownload completed");
                      //  updateAvatar(originalPath);
                        Message message = lbeHandler.obtainMessage(DOWNLOAD_COMPLETE);
                        message.obj = originalPath;
                        lbeHandler.sendMessage(message);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtil.d("filedownload paused " + soFarBytes + ", " + totalBytes);
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        LogUtil.d("filedownload error " + e.getMessage());
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        LogUtil.d("filedownload warn");
                    }
                }).start();
            } else {
                updateAvatar(originalPath);
            }
        }
    }

    /**
     * 更新头像
     *
     * @param originalPath 头像地址
     */
    private void updateAvatar(String originalPath) {
        Glide.with(this).load(originalPath).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.IMMEDIATE).crossFade().error(R.mipmap.ic_avatar_default).into(headPicture);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(mPreferences.getToken())) {
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        if (!isRequestPermission) {
            isRequestPermission = true;
            if (requestLocationPermission()) {
                return;
            }
        }
        updateMyProfile();

    }

  /*  private void httpMyComboRequest() {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<List<MyComboBean>>() {
            @Override
            public void onError(Throwable e) {


            }

            @Override
            public void onNext(List<MyComboBean> data) {
                if (data.size() <= 0) {
                    initFirstGuid();
                }
            }
        };
        HttpMethods.getInstance().MyCombo(new ProgressSubscriber(subscriberOnNextListener, this, null), mPreferences.getToken());
    }
*/
    /*private void httpMyBatteryRequest() {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<MybatteryListBean>() {
            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(MybatteryListBean bean) {
                if (bean.getContent().size() <= 0) {
                    initFirstGuid();
                  //  httpMyComboRequest();
                }
            }
        };
        HttpMethods.getInstance().myBatteryList(new ProgressSubscriber(subscriberOnNextListener, this, null), mPreferences.getToken());
    }*/

    /**
     * @return true if need display request dialog ,false otherwise
     */
    private boolean requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_required_toast),
                    PermissionUtils.PERMISSION_REQUEST_CODE_LOCATION, perms);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            MyApplication myApplication = null;
            try {
                myApplication = (MyApplication) getApplicationContext();
            } catch (Exception e) {
                LogUtil.e( e.toString() );
                myApplication = null;
            }

            if (null == myApplication) {
                return;
            }
            myApplication.exitAllActivity();
        }
    }

    private CustomFragmentPagerAdapter.OnFragmentChangeListener mOnFragmentChangeListener = new CustomFragmentPagerAdapter.OnFragmentChangeListener() {
        @Override
        public Fragment getFragment(int position) {
            return getFragmentByIndex(position);
        }
    };

    private BaseFragment getFragmentByIndex(int index) {
        switch (index) {
            case TAB_MAP_INDEX:
                /** 地图**/
                return getMapFragment();
            case TAB_GOOGLE_INDEX:
                /** google**/
                return getGoogleMapFragment();

            default:
                return null;
        }
    }


    private MapFragment getMapFragment() {
        if (mMapFragment == null) {
            mMapFragment = new MapFragment();
            Bundle bundle = new Bundle();
            bundle.putDouble(AppConstant.KEY_HLATITUDE_TYPE, mLatitude);//这里的values就是我们要传的值
            bundle.putDouble(AppConstant.KEY_HLONGITUDE_TYPE, mLongitude);
            mMapFragment.setArguments(bundle);
        }
        //  mMapFragment.setNetState(netState);
        return mMapFragment;
    }

    private GoogleMapFragment getGoogleMapFragment() {
        if (mGoogleMapFragment == null) {
            mGoogleMapFragment = new GoogleMapFragment();
            Bundle bundle = new Bundle();
            bundle.putDouble(AppConstant.KEY_HLATITUDE_TYPE, mLatitude);//这里的values就是我们要传的值
            bundle.putDouble(AppConstant.KEY_HLONGITUDE_TYPE, mLongitude);
            mGoogleMapFragment.setArguments(bundle);
        }
        //    mBatteryFragment.setNetState(netState);
        return mGoogleMapFragment;
    }

    private void initListener() {
        mIndicatorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int position = mIndicatorRadioGroup.indexOfChild(mIndicatorRadioGroup.findViewById(checkedId));
                mCurIndex = position;
                mType = getTypeByIndex(mCurIndex);
                mViewPager.setCurrentItem(position, false);

            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                RadioButton radioButton = (RadioButton) mIndicatorRadioGroup.getChildAt(position);
                if (!radioButton.isChecked()) {
                    radioButton.setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }

    private int getTypeByIndex(int curIndex) {
        switch (curIndex) {
            case HomeActivity.TAB_MAP_INDEX:
                return AppConstant.TAG_MAP_TYPE;
            case HomeActivity.TAB_GOOGLE_INDEX:
                return AppConstant.TAG_GOOGLE_TYPE;
            default:
                return AppConstant.TAG_MAP_TYPE;
        }
    }

    private void initToolbar() {
        mMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    protected void onDestroy() {
        IMMLeaks.fixFocusedViewLeak(getApplication());
        super.onDestroy();
    }

    @Override
    public void initUIView() {
        Intent intent = new Intent(this, HeartBeatService.class);
        startService(intent);
    }
}
