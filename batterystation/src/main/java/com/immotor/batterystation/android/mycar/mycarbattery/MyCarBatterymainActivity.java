package com.immotor.batterystation.android.mycar.mycarbattery;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.entity.CarBatteryEntry;
import com.immotor.batterystation.android.http.carhttp.CarHttpFailMessage;
import com.immotor.batterystation.android.http.carhttp.CarHttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.intentactivity.IntentActivityMethod;
import com.immotor.batterystation.android.mycar.mycarbattery.battery1.BatteryOneFragment;
import com.immotor.batterystation.android.mycar.mycarbattery.battery2.BatteryTwoFragment;
import com.immotor.batterystation.android.ui.adapter.CustomFragmentPagerAdapter;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.ui.base.BaseFragment;
import com.immotor.batterystation.android.ui.views.NoScrollViewPager;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jm on 2017/9/18 0018.
 */

public class MyCarBatterymainActivity extends BaseActivity implements View.OnClickListener {
    private final int TAB_CHARGE_INDEX = 1;
    private final int TAB_EXPENSE_INDEX = 0;
    private int TAB_COUNT = 2;
    private ImageView mTitleImg;
    private TextView mTitleText;
    private NoScrollViewPager mViewpager;
    private int mCurIndex;
    private int currentIndex;
    private BatteryTwoFragment mBatteryTWoFragment;
    private BatteryOneFragment mBatteryOneFragment;
    private String mToken;
    private String sID;
    private boolean isRequest = false;
    private CarBatteryEntry mDataL;
    private int noBattery = -1;
    private int requestFail = -1;
    private ImageView rightSetting;
    private ImageView mTittleImg1;
    private ImageView mTittleImg2;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private List<String> mDataList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_battery_activity);
        mToken = Preferences.getInstance(this).getToken();
        sID = getIntent().getStringExtra(AppConstant.KEY_ENTRY_BATTERY_CAR_SID_IN);
        initView();
        initData();
        initMagicIndicator();
    }

    private void initData() {
        if (isNetworkAvaliable()) {
            httpBatteryrequest();
        }
    }

    private void httpBatteryrequest() {
        if (isRequest) {
            return;
        }
        isRequest = true;
        Map<String, Object> map = new HashMap<>();
        map.put("token", mToken);
        map.put("sID", sID);
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<CarBatteryEntry>() {
            @Override
            public void onError(Throwable e) {
                CarHttpFailMessage.carfailMessageShow(MyCarBatterymainActivity.this, null, e);
                isRequest = false;
                //传递请求失败的消息
                requestFail = 1;
                intViewPager();
            }

            @Override
            public void onNext(CarBatteryEntry bean) {
                if (bean != null) {
                    mDataL = bean;
                    for (int i = 0; i < mDataL.getBats().size(); i++) {
                        int portNumber = mDataL.getBats().get(i).getPortNumber();
                        if (portNumber==0) {
                            mCurIndex = 0;
                            rightSetting.setVisibility(View.VISIBLE);
                            mTittleImg1.setImageResource(R.mipmap.battery_h_icon_left);
                        } else if (portNumber==1) {
                            mCurIndex = 1;
                            mTittleImg2.setImageResource(R.mipmap.battery_h_icon_right);
                        }
                    }
                } else {
                    //  传递没有装电池的消息
                    noBattery = 1;
                }
                if (mDataL.getBats().size()>1) {
                    mCurIndex = 0;
                }
                isRequest = false;
                intViewPager();
            }
        };
        CarHttpMethods.getInstance().getCarBatteryList(new ProgressSubscriber(subscriberOnNextListener, this), map);
    }

    @Override
    public void initUIView() {
        final String[] CHANNELS = new String[]{getString(R.string.battery_one), getString(R.string.battery_two)};
        mDataList = Arrays.asList(CHANNELS);
    }

    private void initView() {
        mTitleImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mTitleText = (TextView) findViewById(R.id.home_actionbar_text);
        rightSetting = (ImageView) findViewById(R.id.home_actionbar_search);
        rightSetting.setImageResource(R.mipmap.battery_rigth_detail);
        rightSetting.setOnClickListener(this);
        mTitleImg.setImageDrawable(getDrawable(R.mipmap.nav_back_icon_white));
        mTitleImg.setOnClickListener(this);
        mTitleText.setText(getString(R.string.battery_detail));
        mTittleImg1 = (ImageView) findViewById(R.id.battery_tittle_img1);
        mTittleImg2 = (ImageView) findViewById(R.id.battery_tittle_img2);
        mViewpager = (NoScrollViewPager) findViewById(R.id.battery_viewpager_content);
        onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
              currentIndex= position;
                if (mDataL!=null) {
                    for (int i = 0; i < mDataL.getBats().size(); i++) {
                        int portNumber = mDataL.getBats().get(i).getPortNumber();
                        if (portNumber == currentIndex) {
                            rightSetting.setVisibility(View.VISIBLE);
                            break;
                        } else {
                            rightSetting.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mViewpager.addOnPageChangeListener(onPageChangeListener);
    }

    private void initMagicIndicator() {
        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList == null ? 0 : mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setNormalColor(Color.parseColor("#ffffff"));
                simplePagerTitleView.setSelectedColor(Color.WHITE);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewpager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setColors(Color.parseColor("#E6442B"));

                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setGravity(Gravity.CENTER);
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerPadding(UIUtil.dip2px(this, 15));
        //    titleContainer.setDividerDrawable(getResources().getDrawable(R.drawable.simple_splitter));
        ViewPagerHelper.bind(magicIndicator, mViewpager);

    }

    private void intViewPager() {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        CustomFragmentPagerAdapter mPagerAdapter = new CustomFragmentPagerAdapter(mFragmentManager, mOnFragmentChangeListener);
        mPagerAdapter.setCount(TAB_COUNT);
        mViewpager.setNoScroll(false);
        mViewpager.setOffscreenPageLimit(TAB_COUNT);
        mViewpager.setAdapter(mPagerAdapter);
        mViewpager.setCurrentItem(mCurIndex);
        mViewpager.setScrollContainer(false);

    }

    private CustomFragmentPagerAdapter.OnFragmentChangeListener mOnFragmentChangeListener = new CustomFragmentPagerAdapter.OnFragmentChangeListener() {
        @Override
        public Fragment getFragment(int position) {
            return getFragmentByIndex(position);
        }
    };

    private BaseFragment getFragmentByIndex(int index) {
        switch (index) {
            case TAB_EXPENSE_INDEX:
                /** 电池1**/
                return getBatteryOneFragment();
            case TAB_CHARGE_INDEX:
                /** 电池2**/
                return getBatteryTwoFragment();

            default:
                return null;
        }
    }

    private BatteryOneFragment getBatteryOneFragment() {
        if (mBatteryOneFragment == null) {
            mBatteryOneFragment = new BatteryOneFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(AppConstant.KEY_ENTRY_BATTERY_REQUEST_FAIL, requestFail);
            boolean havedata = true;
            if (mDataL!=null) {
                for (int i = 0; i < mDataL.getBats().size(); i++) {
                    int portNumber = mDataL.getBats().get(i).getPortNumber();
                    if (portNumber==0) {
                        bundle.putSerializable(AppConstant.KEY_ENTRY_BATTERY_REQUEST_ONE_DATA, mDataL.getBats().get(i));
                        havedata = false;
                        break;
                    }
                }
            }
            if (mDataL==null || havedata) {
                bundle.putInt(AppConstant.KEY_ENTRY_BATTERY_REQUEST_EMPTY, noBattery);
            }
            mBatteryOneFragment.setArguments(bundle);
        }
        return mBatteryOneFragment;
    }

    private BatteryTwoFragment getBatteryTwoFragment() {
        if (mBatteryTWoFragment == null) {
            mBatteryTWoFragment = new BatteryTwoFragment();
            Bundle bundle = new Bundle();
            boolean havedata = true;
            bundle.putInt(AppConstant.KEY_ENTRY_BATTERY_REQUEST_FAIL, requestFail);//这里的values就是我们要传的值
            if (mDataL!=null) {
                for (int i = 0; i < mDataL.getBats().size(); i++) {
                    int portNumber = mDataL.getBats().get(i).getPortNumber();
                    if (portNumber == 1) {
                        bundle.putSerializable(AppConstant.KEY_ENTRY_BATTERY_REQUEST_TWO_DATA, mDataL.getBats().get(i));
                        havedata = false;
                        break;
                    }
                }
            }

            if (mDataL==null || havedata) {
                bundle.putInt(AppConstant.KEY_ENTRY_BATTERY_REQUEST_EMPTY, noBattery);
            }
            mBatteryTWoFragment.setArguments(bundle);
        }
        return mBatteryTWoFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_actionbar_menu:
                finish();
                break;
            case R.id.home_actionbar_search:
                IntentActivityMethod.CarBatterytoBatterySettingDetail(this, mDataL, currentIndex);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewpager.removeOnPageChangeListener(onPageChangeListener);
    }
}
