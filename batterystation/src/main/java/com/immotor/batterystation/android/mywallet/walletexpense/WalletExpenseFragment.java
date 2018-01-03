package com.immotor.batterystation.android.mywallet.walletexpense;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.MyChargeRecord;
import com.immotor.batterystation.android.entity.MyExpenseRecord;
import com.immotor.batterystation.android.mywallet.walletcharge.ChargeRecyViewAdapter;
import com.immotor.batterystation.android.mywallet.walletcharge.mvppresent.ChargePresent;
import com.immotor.batterystation.android.mywallet.walletexpense.mvppresent.ExpensePresent;
import com.immotor.batterystation.android.mywallet.walletexpense.mvpview.IExpenseView;
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

public class WalletExpenseFragment extends BaseFragment implements IExpenseView, View.OnClickListener{

    private RelativeLayout mNoDataView;
    private RecyclerView mRecyView;
    private boolean mIsCanPullUp = true;
    private List<MyExpenseRecord.ContentBean> mDataList = new ArrayList();
    private ExpensePresent mExpensePresent;
    private ExpenseRecyViewAdapter mExpenseAdapter;
    private String mToken;
    private SmartRefreshLayout mSmartRefreshLayout;
    private boolean isRefreshStatus = false;
    private LinearLayout mNoNetLayout;

    @Override
    public int getContentLayout() {
        return R.layout.fragment_wallet_expense;
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
        mExpenseAdapter = new ExpenseRecyViewAdapter(R.layout.item_charge_recyview);
        mRecyView.setAdapter(mExpenseAdapter);
    }

    private void initData() {
        mExpensePresent= new ExpensePresent(getContext(),mToken, this);
        if (isNetworkAvaliable()) {
            requestDataList(true);
        }
    }

    private void requestDataList(boolean isRefresh) {
        if (mDataList.size() > 0) {
            mExpensePresent.requestExpenseRecord(isRefresh, false);
        } else {
            mExpensePresent.requestExpenseRecord(isRefresh, true);
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
    public void addData(boolean isCanPullup, List<MyExpenseRecord.ContentBean> data) {
        if (data.size() == 0) {
            mIsCanPullUp = false;
            mSmartRefreshLayout.setLoadmoreFinished(true);
        } else {
            mIsCanPullUp = isCanPullup;
        }
        mDataList.clear();
        mDataList.addAll(data);
        if (!isRefreshStatus) {
            mExpenseAdapter.addData(mDataList);
        } else {
            isRefreshStatus = false;
            mExpenseAdapter.replaceData(mDataList);
        }
    }
}
