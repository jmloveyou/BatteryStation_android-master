package com.immotor.batterystation.android.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
/**
 * Created by ${jm} on 2017/7/19 0018.
 */
public class CustomFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "CustomFragmentPagerAdapter";
    private OnFragmentChangeListener mFragmentChangeListener = null;
    private int mCount = 0;
    private final String[] CONTENT = new String[] {"消费明细","充值明细"};
    public CustomFragmentPagerAdapter(FragmentManager fm, OnFragmentChangeListener l) {
        super(fm);
        this.mFragmentChangeListener = l;
    }

    /**
     * 设置count
     *
     * @param count
     */
    public void setCount(int count) {
        this.mCount = count;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Fragment getItem(int arg0) {
        if (null == mFragmentChangeListener) {
            return null;
        }
        return mFragmentChangeListener.getFragment(arg0);
    }

    public interface OnFragmentChangeListener {
        Fragment getFragment(int position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length].toUpperCase();
    }

}
