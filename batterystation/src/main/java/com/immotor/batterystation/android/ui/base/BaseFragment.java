package com.immotor.batterystation.android.ui.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.test.suitebuilder.annotation.Suppress;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.util.NetworkUtils;

import butterknife.ButterKnife;

/**
 *
 * Created by Ashion on 2017/5/19.
 */

public abstract class BaseFragment extends Fragment  {
    public AppCompatActivity mActivity;
    public Preferences mPreferences;
    public View mRootView;
    protected  int  netState;

    public BaseFragment() {
        // Required empty public constructor
    }


    @LayoutRes
    public abstract int getContentLayout();

    public abstract void initUIViews();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getContentLayout(), container, false);
        ButterKnife.bind(this, view);
        mActivity = (AppCompatActivity) getActivity();
        mPreferences = Preferences.getInstance(getContext());
      //  mRootView = view;
        return view;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUIViews();
    }

    public void showSnackbar(int resId){
        if(mActivity!=null && mActivity instanceof BaseActivity){
            ((BaseActivity)mActivity).showSnackbar(resId);
        }
    }

    public void showSnackbar(String message){
        if(mActivity!=null && mActivity instanceof BaseActivity){
            ((BaseActivity)mActivity).showSnackbar(message);
        }
    }

    public boolean isNetworkAvaliable(){
        if (!NetworkUtils.isNetAvailable(getContext())){
        //    Toast.makeText(getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void setNetState(int netState) {
        this.netState = netState;
    }

    @Suppress
    public void onNetStateChanged(int netState) {
        this.netState = netState;
    }
}
