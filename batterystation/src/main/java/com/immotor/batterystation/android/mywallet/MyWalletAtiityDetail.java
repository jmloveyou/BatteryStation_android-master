package com.immotor.batterystation.android.mywallet;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.mywallet.walletcharge.WalletChargeFragment;
import com.immotor.batterystation.android.mywallet.walletexpense.WalletExpenseFragment;
import com.immotor.batterystation.android.ui.adapter.CustomFragmentPagerAdapter;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.ui.base.BaseFragment;
import com.immotor.batterystation.android.ui.views.NoScrollViewPager;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public class MyWalletAtiityDetail extends BaseActivity implements View.OnClickListener {

    private final int TAB_CHARGE_INDEX = 1;
    private final int TAB_EXPENSE_INDEX = 0;
    private int TAB_COUNT = 2;
    private ImageView mTitleImg;
    private TextView mTitleText;
    private NoScrollViewPager mViewpager;
    private WalletChargeFragment mChargeFragment;
    private WalletExpenseFragment mExpenseFragment;
    private int mCurIndex;
    private List<String> mDataList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mywallet_activity);

        initView();
    }

    @Override
    public void initUIView() {
        final String[] CHANNELS = new String[]{getString(R.string.expense_detail), getString(R.string.charge_detail)};
        mDataList = Arrays.asList(CHANNELS);
    }

    private void initView() {
        mTitleImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mTitleText = (TextView) findViewById(R.id.home_actionbar_text);
        mTitleImg.setImageDrawable(getDrawable(R.mipmap.nav_back_icon_white));
        mTitleImg.setOnClickListener(this);
        mTitleText.setText(getString(R.string.my_wallet));
        mViewpager = (NoScrollViewPager) findViewById(R.id.mywallet_viewpager_content);
        intViewPager();
        initListener();
        initMagicIndicator();
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
                simplePagerTitleView.setNormalColor(Color.parseColor("#88000000"));
                simplePagerTitleView.setSelectedColor(Color.RED);
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
                indicator.setColors(Color.parseColor("#ec5218"));
                return indicator;
            }

        });
        magicIndicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setGravity(Gravity.CENTER);
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerPadding(UIUtil.dip2px(this, 15));
        titleContainer.setDividerDrawable(getResources().getDrawable(R.drawable.simple_splitter));
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
                /** 消费明细**/
                return getExpenseFragment();
            case TAB_CHARGE_INDEX:
                /** 充值明细**/
                return getChargeFragment();

            default:
                return null;
        }
    }

    private WalletExpenseFragment getExpenseFragment() {
        if (mExpenseFragment == null) {
            mExpenseFragment = new WalletExpenseFragment();
        }
        return mExpenseFragment;
    }

    private WalletChargeFragment getChargeFragment() {
        if (mChargeFragment == null) {
            mChargeFragment = new WalletChargeFragment();
        }
        return mChargeFragment;
    }

    private void initListener() {
/*
        mIndictor.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                mCurIndex = position;
                mViewpager.setCurrentItem(position, false);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_actionbar_menu:
                finish();
                break;
        }
    }
}
