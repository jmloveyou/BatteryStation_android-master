package com.immotor.batterystation.android.mywallet.walletcharge;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.MyChargeRecord;
import com.immotor.batterystation.android.mywallet.walletcharge.mvppresent.ChargePresent;
import com.immotor.batterystation.android.mywallet.walletcharge.mvpview.IChargeView;
import com.immotor.batterystation.android.ui.activity.RechargeActivity;
import com.immotor.batterystation.android.ui.base.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public class WalletChargeFragment extends BaseFragment implements IChargeView, View.OnClickListener {

    private RelativeLayout mNoDataView;
    private RecyclerView mRecyView;
    private boolean mIsCanPullUp = true;
    private List<MyChargeRecord.ContentBean> mDataList = new ArrayList();
    private ChargePresent mChargePresent;
    private ChargeRecyViewAdapter mChargeAdapter;
    private String mToken;
    private SmartRefreshLayout mSmartRefreshLayout;
    private boolean isRefreshStatus = false;
    private LinearLayout mNoNetLayout;

    @Override
    public int getContentLayout() {
        return R.layout.fragment_wallet_charge;
    }

    @Override
    public void initUIViews() {
        mSmartRefreshLayout = (SmartRefreshLayout) getView().findViewById(R.id.swip_refresh_charge);
        mSmartRefreshLayout.setEnableAutoLoadmore(false);
        mRecyView = (RecyclerView) getView().findViewById(R.id.recycler_container);
        mRecyView.setLayoutManager(new LinearLayoutManager(getContext()));
        mNoDataView = (RelativeLayout) getView().findViewById(R.id.no_data_layout);
        TextView recharge = (TextView) getView().findViewById(R.id.recharge);
        recharge.setOnClickListener(this);
        mNoNetLayout = (LinearLayout) getView().findViewById(R.id.no_net_layout);
        TextView mNoNetTryBtn = (TextView) getView().findViewById(R.id.no_net_try_tv);
        mNoNetTryBtn.setOnClickListener(this);

        mToken = mPreferences.getToken();
        initData();
        initAdapter();
        initListener();
    }

    private void initListener() {

        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                isRefreshStatus = true;
                requestDataList(true);
                if (mDataList.size() > 0) {
                    refreshlayout.finishRefresh();
                } else {
                    refreshlayout.finishRefresh(false);
                }
                refreshlayout.setLoadmoreFinished(false);
            }
        });
        mSmartRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if (mIsCanPullUp) {
                    requestDataList(false);
                } else {
                    refreshlayout.setLoadmoreFinished(true);
                }
                if (mDataList.size() > 0) {
                    refreshlayout.finishLoadmore();
                } else {
                    refreshlayout.finishLoadmore(false);
                }
            }
        });

    }

    private void initAdapter() {
        mChargeAdapter = new ChargeRecyViewAdapter(R.layout.item_charge_recyview);
        mRecyView.setAdapter(mChargeAdapter);
    }

    private void initData() {
        mChargePresent = new ChargePresent(getContext(), this);
        if (isNetworkAvaliable()) {
            requestDataList(true);
        }
    }

    private void requestDataList(boolean isRefresh) {
        if (mDataList.size() > 0) {
            mChargePresent.requestChargeRecord(isRefresh, mToken, false);
        } else {
            mChargePresent.requestChargeRecord(isRefresh, mToken, true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recharge:
                initIntent();
                break;
            case R.id.no_net_try_tv:
                requestDataList(true);
                break;
            default:
                break;

        }
    }

    private void initIntent() {
        startActivity(new Intent(getContext(), RechargeActivity.class));
        getActivity().finish();

    }

    @Override
    public void showEmpty() {
        mNoNetLayout.setVisibility(View.GONE);
        mSmartRefreshLayout.setVisibility(View.GONE);
        mNoDataView.setVisibility(View.VISIBLE);
        isRefreshStatus = false;
    }

    @Override
    public void showNomal() {
        mNoNetLayout.setVisibility(View.GONE);
        mSmartRefreshLayout.setVisibility(View.VISIBLE);
        mNoDataView.setVisibility(View.GONE);
    }

    @Override
    public void showLoading() {
        isRefreshStatus = false;
    }

    @Override
    public void showFail() {
        if (mDataList.size() <= 0) {
            mSmartRefreshLayout.setVisibility(View.GONE);
            mNoDataView.setVisibility(View.GONE);
            mNoNetLayout.setVisibility(View.VISIBLE);
        }
        isRefreshStatus = false;
    }

    @Override
    public void addData(boolean isCanPullup, List<MyChargeRecord.ContentBean> data) {
        if (data.size() == 0) {
            mIsCanPullUp = false;
            mSmartRefreshLayout.setLoadmoreFinished(true);
        } else {
            mIsCanPullUp = isCanPullup;
        }
            mDataList.clear();
            mDataList.addAll(data);
        if (!isRefreshStatus) {
            mChargeAdapter.addData(mDataList);
        } else {
            isRefreshStatus = false;
            mChargeAdapter.replaceData(mDataList);
        }
    }
}
