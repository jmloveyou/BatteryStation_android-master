package com.immotor.batterystation.android.mycar;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidkun.xtablayout.XTabLayout;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.entity.TripDayBean;
import com.immotor.batterystation.android.http.carhttp.CarHttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.util.Common;
import com.immotor.batterystation.android.util.DateTimeUtil;
import com.immotor.batterystation.android.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

public class TripHistoryActivity extends BaseActivity implements ViewPager.OnPageChangeListener{

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.tabLayout)
    XTabLayout mTabLayout;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.no_data_layout)
    LinearLayout noDataLayout;
    /*@Bind(R.id.trip_total_text)
    TextView tripTotalText;
    @Bind(R.id.trip_time_text)
    TextView tripTimeText;*/

    private ViewPagerAdapter mAdapter;
    private BottomSheetDialog bottomSheetDialog;
    private int lastSelect = -1;   //上次被选中的page
    private String sid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_trip_history);

    }

    @Override
    public void initUIView() {
        sid= getIntent().getStringExtra(AppConstant.KEY_ENTRY_BATTERY_CAR_TRIP_SID);
        mToolbar.setTitle(getString(R.string.drawer_trip));
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/haettenschweiler.ttf");
        tripTotalText.setTypeface(typeface);
        tripTimeText.setTypeface(typeface);*/

        if(mAdapter==null) {
            setupViewPager();
            mTabLayout.setupWithViewPager(mViewPager);
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
            initBottomSheet();
            httpRequestTrackWeek();
        }else{
            if(lastSelect!=-1){
                try {
                    Fragment fragment = mAdapter.getItem(lastSelect);
                    if (fragment instanceof TripHistoryFragment) {
                        ((TripHistoryFragment) fragment).startHeadAnimation();
                    }
                }catch (Exception e){
                    LogUtil.d(e.getMessage());
                }
            }
        }
    }

    private void setupViewPager(){
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(6);
        mViewPager.addOnPageChangeListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_trip_history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_search){
            if (bottomSheetDialog != null && !bottomSheetDialog.isShowing()){
                bottomSheetDialog.show();
            }
//        }else if (id == R.id.action_detail){
//            Intent intent =  new Intent(this, RankListActivity.class);
//            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        LogUtil.d("onPageSelected:"+position);

        //当前被选中的播放动画
        try {
            Fragment fragment = mAdapter.getItem(position);
            if (fragment instanceof TripHistoryFragment) {
                ((TripHistoryFragment) fragment).startHeadAnimation();
            }
        }catch (Exception e){
            LogUtil.d(e.getMessage());
        }

        //上次被选中的取消动画
        try{
            if(lastSelect!=-1){
                Fragment fragment = mAdapter.getItem(lastSelect);
                if (fragment instanceof TripHistoryFragment) {
                    ((TripHistoryFragment) fragment).stopHeadAnimation();
                }
            }

        }catch (Exception e){
            LogUtil.d(e.getMessage());
        }

        lastSelect = position;  //记录选中
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private FragmentManager fm;
        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            fm = manager;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void setFragments(Fragment fragment, String title) {
            if(mFragmentList.size() > 0){
                FragmentTransaction ft = fm.beginTransaction();
                for(Fragment f : mFragmentList){
                    ft.remove(f);
                }
                ft.commit();
                ft = null;
                fm.executePendingTransactions();
            }

            mFragmentTitleList.add(title);
            mFragmentList.add(fragment);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    private void httpRequestTrackWeek(){
        if (!isNetworkAvaliable()) return;
        final String token = mPreferences.getToken();
        Map<String ,Object> map = new HashMap<>();
        map.put("token", token);
        map.put("sID", sid);

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<List<TripDayBean>>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(e.getMessage());
            }
            @Override
            public void onNext(List<TripDayBean> result) {
                if (result != null) {
                    updateView(result);
                }else {
                    mTabLayout.setVisibility(View.GONE);
                    noDataLayout.setVisibility(View.VISIBLE);
                }
            }
        };
        CarHttpMethods.getInstance().getCarTripList(new ProgressSubscriber(subscriberOnNextListener, this), map);
    }

    private void httpRequestTrackDay(final String dateStr){
        if (!isNetworkAvaliable()) return;
        final String token = mPreferences.getToken();
        Map<String ,Object> map = new HashMap<>();
        map.put("token", token);
        map.put("sID", sid);
        map.put("date", dateStr);

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<TripDayBean>() {
            @Override
            public void onError(Throwable e) {
            }
            @Override
            public void onNext(TripDayBean result) {
                if (result != null) {
                    LogUtil.v("data str from server:"+dateStr);

                    /*String tmpData = result.getDate();
                    String dataStr = Common.formatData(tmpData);
                    mAdapter.mFragmentTitleList.clear();
                    mAdapter.mFragmentList.clear();
                    mAdapter.setFragments(TripHistoryFragment.newInstance(result), dataStr);
                    mAdapter.notifyDataSetChanged();*/

                    Intent intent = new Intent(TripHistoryActivity.this, TripHistoryDayActivity.class);
                    intent.putExtra("DATE_STR", dateStr);
                    intent.putExtra("TRIP_DAY_BEAN", result);
                    startActivity(intent);

                }else {
                    Toast.makeText(TripHistoryActivity.this, R.string.no_driver_track, Toast.LENGTH_SHORT).show();
                }
            }
        };
        CarHttpMethods.getInstance().getTrackDetailDay(new ProgressSubscriber(subscriberOnNextListener, this), map);
    }

    private void updateView(List<TripDayBean> beanList){
        if(mAdapter.mFragmentList.size()>0){
            return;
        }
        //for testing

        /*if (beanList == null ){
            beanList = new ArrayList<>();
            for (int x = 0; x<3; x++) {
                TripDayBean TripDayBean = new TripDayBean();
                ArrayList<TripBean> tripBeanList = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    TripBean tripBean = new TripBean();
                    tripBeanList.add(tripBean);
                }
                TripDayBean.setData(tripBeanList);
                beanList.add(TripDayBean);
            }
        }*/


        int size = beanList.size();
        if (size > 0){
            noDataLayout.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.VISIBLE);
        }else {
            noDataLayout.setVisibility(View.VISIBLE);
            mTabLayout.setVisibility(View.GONE);
        }
        for (int i=0; i<size; i++) {
            TripDayBean bean = beanList.get(i);
            String tmpData = bean.getDate();
            String dataStr = Common.formatData(tmpData);

            mAdapter.addFragment(TripHistoryFragment.newInstance(bean), dataStr);
        }
        mAdapter.notifyDataSetChanged();

        //默认选第一个
        if(size>0){
            onPageSelected(0);
        }
    }


    public void initBottomSheet() {

        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(this);
            View view = LayoutInflater.from(this).inflate(R.layout.datepicker_sheet, null, false);

            final DatePicker datePicker = (DatePicker)view.findViewById(R.id.date_picker);
            (view.findViewById(R.id.btn_cancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                }
            });
            (view.findViewById(R.id.btn_done)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                    String dateStr = DateTimeUtil.formatDate(datePicker.getYear(), datePicker.getMonth()+1, datePicker.getDayOfMonth());
                    LogUtil.v("dateStr:"+dateStr);
                    httpRequestTrackDay(dateStr);
                }
            });
            bottomSheetDialog.setContentView(view);
        }
    }

}
