package com.immotor.batterystation.android.mycombo;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.entity.DataServer;
import com.immotor.batterystation.android.entity.MyComboBean;
import com.immotor.batterystation.android.entity.MyComboMultipleItem;
import com.immotor.batterystation.android.mycombo.mvppresent.MyComboPresent;
import com.immotor.batterystation.android.mycombo.mvpview.IMyComboView;
import com.immotor.batterystation.android.selectcombo.SelectComboActivity;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.view.WiperSwitchEx;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jm on 2017/8/3 0003.
 */

public class MyComboActivity extends BaseActivity implements IMyComboView, View.OnClickListener {


    private TextView mBuyTv;
    private TextView mTittle;
    private ImageView mBackImg;
    private MyComboPresent mMyComboPresent;
    private LinearLayout mNomalView;
    private LinearLayout mNoDataView;
    private RecyclerView mRecyView;
    private String mToken;
    private List<MyComboBean> mData = new ArrayList<>();
    private String mComboTag = "combo_tag";
    private String mComboResidueTag = "combo_residue_times_tag";
    private String mComboStatus = "combo_status";
    private String mHaveCombo = "wether_hava_combo";
    private MyComboMultiAdapter mMyComboAdapter;
  //  private WiperSwitchEx mSlideSwitch;
    private int mSlideSwitchStatus;
    private boolean mHaveMonthCombo = false;
    private LinearLayout mNoNetLayout;
    private TextView mNoNetTryBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_combo);
        mToken = mPreferences.getToken();
        mMyComboPresent = new MyComboPresent(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void initUIView() {

        mBackImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mBackImg.setOnClickListener(this);
        mBackImg.setImageDrawable(getResources().getDrawable(R.mipmap.nav_back_icon_white));
        mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.my_combo);
        mTittle.setGravity(Gravity.CENTER);
     //   mSlideSwitch = (WiperSwitchEx) findViewById(R.id.combo_slide_switch);
        //initSliseListener();
        mNomalView = (LinearLayout) findViewById(R.id.my_combo_data_layout);
        mNoDataView = (LinearLayout) findViewById(R.id.my_combo_no_data_layout);
        mRecyView = (RecyclerView) findViewById(R.id.my_combo_recyView);
        mRecyView.setLayoutManager(new LinearLayoutManager(this));

        mBuyTv = (TextView) findViewById(R.id.my_combo_buy_tv);
        mBuyTv.setOnClickListener(this);
        mNoNetLayout = (LinearLayout) findViewById(R.id.no_net_layout);
        mNoNetTryBtn = (TextView) findViewById(R.id.no_net_try_tv);
        mNoNetTryBtn.setOnClickListener(this);
       // initListener();
    }

    private void initListener() {
        mMyComboAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                List<MyComboMultipleItem> dataList= DataServer.getMultipleData(mData);
                if (dataList.get(position).getItemType() == MyComboMultipleItem.COMMON_COMBO_NOT) {
                    showLowerDialog();
                }
            }
        });
    }

    private void showLowerDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.combo_cancle_degrade);
        dialog.setMessage(R.string.wether_combo_cancle_degrade);
        dialog.setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mMyComboPresent.requestCancleLowerCombo(mToken);
            }
        });
        dialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initData() {
        mMyComboPresent.requestMyCombo(mToken);
    }

    @Override
    public void showEmpty() {
        mBuyTv.setVisibility(View.VISIBLE);
        mNomalView.setVisibility(View.GONE);
        mNoNetLayout.setVisibility(View.GONE);
        mNoDataView.setVisibility(View.VISIBLE);
        mBuyTv.setText(R.string.go_buy);
    }

    @Override
    public void showNomal() {
        mBuyTv.setVisibility(View.VISIBLE);
        mNomalView.setVisibility(View.VISIBLE);
        mNoDataView.setVisibility(View.GONE);
        mNoNetLayout.setVisibility(View.GONE);
        mBuyTv.setText(R.string.combo_updata);

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showFail() {
        mNomalView.setVisibility(View.GONE);
        mNoDataView.setVisibility(View.GONE);
        mBuyTv.setVisibility(View.GONE);
        mNoNetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void addData(List<MyComboBean> data) {
        mMyComboPresent.requestgetAutoStatus(mToken);
        mData = data;
        if (mMyComboAdapter!=null) {
            mMyComboAdapter = null;
        }
        mMyComboAdapter = new MyComboMultiAdapter(DataServer.getMultipleData(data));
        mMyComboAdapter.setPresent(mMyComboPresent, mToken);
        mRecyView.setAdapter(mMyComboAdapter);
        initListener();
    //    mMyComboAdapter.setDataList(data);
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getType() == 0) {
                mHaveMonthCombo = true;
                break;
            }
        }
    }

    @Override
    public void RefreshView() {
        initData();
        mMyComboAdapter.notifyDataSetChanged();
    }

    @Override
    public void getAutoStatus(int status) {
        mSlideSwitchStatus = status;
     //   mMyComboAdapter.setChecked()
        if (status == AppConstant.AUTO_EXPENSE_STATUS_OPEN_COD) {
          //  mSlideSwitch.setChecked(true);
            mMyComboAdapter.setChecked(true);
        } else {
           // mSlideSwitch.setChecked(false);
            mMyComboAdapter.setChecked(false);
        }
    //    mSlideSwitch.invalidate();
    }

    @Override
    public void autoExpenseResult(int status) {
        if (status == AppConstant.AUTO_EXPENSE_STATUS_CLOSE_SUCCESS_COD) {
          //  mSlideSwitch.setChecked(false);
            mMyComboAdapter.setChecked(false);
        } else {
          //  mSlideSwitch.setChecked(true);
            mMyComboAdapter.setChecked(true);
        }
    }

    @Override
    public void getAutoStatusFail() {
        Toast.makeText(this, R.string.expense_query_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void autoExpenseResultFail(int status) {
        if (status == AppConstant.AUTO_EXPENSE_STATUS_CLOSE_FAIL_COD) {
            Toast.makeText(this, R.string.expense_closed_fail, Toast.LENGTH_SHORT).show();
          //  mSlideSwitch.setChecked(true);
            mMyComboAdapter.setChecked(true);
        } else {
            Toast.makeText(this, R.string.expense_open_fail, Toast.LENGTH_SHORT).show();
          //  mSlideSwitch.setChecked(false);
            mMyComboAdapter.setChecked(false);
        }
      //  mSlideSwitch.invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_actionbar_menu:
                finish();
                break;
            case R.id.my_combo_buy_tv:
                // 没有月套餐,没有数据或者有数据但是有次卡，没有月卡
           /*     boolean haveCombo = false;
                if (mData.size() > 0) {
                    for (int i=0;i<mData.size();i++) {
                        if (mData.get(i).getType()==0) {
                            //有月套餐
                            haveCombo = true;
                            break;
                        }
                    }
                }*/

                if (mHaveMonthCombo) {
                    initUpdateIntent();
                } else {
                    initBuyIntent();
                }
               /* //没有数据
                if (mData.size() == 0) {
                    initBuyIntent();
                }*/
                break;
            case R.id.no_net_try_tv:
                mMyComboPresent.requestMyCombo(mToken);
                break;
            default:
                break;
        }
    }

    private void initBuyIntent() {
        // 没有套餐，购买套餐,1 没有套餐
        Intent intent = new Intent(this, SelectComboActivity.class);
        intent.putExtra(mHaveCombo, 1);
        startActivity(intent);
    }

    private void initUpdateIntent() {
        // 升级降级判断，使用完才可升级，未使用完，可降级
        Intent intent = new Intent(this, SelectComboActivity.class);
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getType() == 0) {
                if (mData.get(i).getStatus() == 2) {
                    intent.putExtra(mComboStatus, true);
                } else {
                    intent.putExtra(mComboTag, mData.get(i).getTimes());
                    intent.putExtra(mComboResidueTag, mData.get(i).getResidue());
                }
            }
        }
        startActivity(intent);
    }

}
