package com.immotor.batterystation.android.selectcombo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.immotor.batterystation.android.MyApplication;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.entity.DataServer;
import com.immotor.batterystation.android.entity.SelectComboBean;
import com.immotor.batterystation.android.entity.SelectComboMultipleItem;
import com.immotor.batterystation.android.selectcombo.mvppresent.SelectComboPresent;
import com.immotor.batterystation.android.selectcombo.mvpview.ISelectComboView;
import com.immotor.batterystation.android.ui.activity.FirstGuideActivity;
import com.immotor.batterystation.android.ui.activity.HomeActivity;
import com.immotor.batterystation.android.ui.base.BaseActivity;

import java.util.List;

import static com.immotor.batterystation.android.ui.activity.FirstGuideActivity.FIRST_TARGET;

/**
 * Created by jm on 2017/8/4 0004.
 */

public class SelectComboActivity extends BaseActivity implements ISelectComboView, View.OnClickListener {

    private ImageView mBackImg;
    private LinearLayout mNomalView;
    private LinearLayout mNoDataView;
    private RecyclerView mRecyView;
    private TextView mBuyTv;
    private LinearLayout mBottomView;
    private TextView mTittle;
    private String mToken;
    private SelectComboPresent mSelectComboPresent;
    private SelectComboMultiAdapter mAdapter;
    private int mTimes;
    private SelectComboBean mselectComboBean;
    private int mResidueTimes;
    private boolean mComboStatus;
    private boolean isFirst;
    private List<SelectComboBean> mData;
    //   private Switch mSlideSwitch;
    private int mWetherHaveCombo;
 //   private int mSlideSwitchStatus = 1;
    private LinearLayout mNoNetLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_layout);
        mToken = mPreferences.getToken();
        mTimes = getIntent().getIntExtra("combo_tag", -1);
        mResidueTimes = getIntent().getIntExtra("combo_residue_times_tag", -1);
        mComboStatus = getIntent().getBooleanExtra("combo_status", false);
        mWetherHaveCombo = getIntent().getIntExtra("wether_hava_combo", -1);
        mSelectComboPresent = new SelectComboPresent(this, this);

        initData();
    }

    private void initData() {
        mSelectComboPresent.requestSelectCombo(mToken);
        //    mSelectComboPresent.requestgetAutoStatus(mToken);
    }

    @Override
    public void initUIView() {
        isFirst = getIntent().getBooleanExtra(FirstGuideActivity.FIRST_TARGET_TWO_STEP, false);
        if (isFirst) {
            findViewById(R.id.combo_stub).setVisibility(View.VISIBLE);
            findViewById(R.id.second_item).setBackgroundResource(R.mipmap.corner_bg);
            findViewById(R.id.third_item).setBackgroundResource(R.mipmap.corner_bg);
         //   findViewById(R.id.fourth_item).setBackgroundResource(R.mipmap.step_white_circle);
        }
       /* mSlideSwitch = (Switch) findViewById(R.id.slide_switch);
        mSlideSwitch.setChecked(true);
        initSliseListener();*/
        mBackImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mBackImg.setOnClickListener(this);
        mBackImg.setImageDrawable(getResources().getDrawable(R.mipmap.nav_back_icon_white));
        mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.select_combo);
        mTittle.setGravity(Gravity.CENTER);

        mNomalView = (LinearLayout) findViewById(R.id.select_combo_data_layout);
        mNoDataView = (LinearLayout) findViewById(R.id.select_combo_no_update_data_layout);
        mRecyView = (RecyclerView) findViewById(R.id.select_combo_recyView);
        mRecyView.setLayoutManager(new GridLayoutManager(this, 2));
        mBottomView = (LinearLayout) findViewById(R.id.select_combo_update_bottom_llyt);
        mBuyTv = (TextView) findViewById(R.id.select_combo_sure);
        mBuyTv.setOnClickListener(this);

        TextView mNoNetbtn = (TextView) findViewById(R.id.no_net_try_tv);
        mNoNetbtn.setOnClickListener(this);
        mNoNetLayout = (LinearLayout) findViewById(R.id.no_net_layout);

    }

    @Override
    public void showEmpty() {
        mBottomView.setVisibility(View.GONE);
        mNomalView.setVisibility(View.GONE);
        mNoNetLayout.setVisibility(View.GONE);
        mNoDataView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNomal() {
        mNoDataView.setVisibility(View.GONE);
        mNoNetLayout.setVisibility(View.GONE);
        mBottomView.setVisibility(View.VISIBLE);
        mNomalView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading() {
    }

    @Override
    public void showFail() {
        mNoDataView.setVisibility(View.GONE);
        mBottomView.setVisibility(View.GONE);
        mNomalView.setVisibility(View.GONE);
        mNoNetLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void addData(final List<SelectComboBean> data) {
        mData = data;
        if (mAdapter != null) {
            mAdapter = null;
        }

        mAdapter = new SelectComboMultiAdapter(DataServer.getMultipleSelectData(data));
        mAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
            @Override
            public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
                if (DataServer.getMultipleSelectData(data).get(position).getItemType() == SelectComboMultipleItem.TITTLE_MONTHE) {
                    return 2;
                } else if (DataServer.getMultipleSelectData(data).get(position).getItemType() == SelectComboMultipleItem.AUTO_RENEW) {
                        return 2;
                } else if (DataServer.getMultipleSelectData(data).get(position).getItemType() == SelectComboMultipleItem.TITTLE_TIMES) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
        mRecyView.setAdapter(mAdapter);
        initItemClickListener();
        //   mAdapter.replaceData(data);
    }

    @Override
    public void BuyComboSuccess() {
        //如果是开关打开，并且是月套餐
        if (mselectComboBean.getType() == 0) {
          /*  if (mSlideSwitchStatus==AppConstant.AUTO_EXPENSE_STATUS_OPEN_COD ||mSlideSwitchStatus==1 ||mSlideSwitchStatus==AppConstant.AUTO_EXPENSE_STATUS_OPEN_SUCCESS_COD)
            {
                mSelectComboPresent.requestAutoExpense(mToken,true);
            }*/
            if (mAdapter.getCheckedStatus()) {
                mSelectComboPresent.requestAutoExpense(mToken, true);
            }
        }
        finish();
    }

    @Override
    public void getAutoStatusSucess(int status) {
      /*  mSlideSwitchStatus = status;
        if (status == AppConstant.AUTO_EXPENSE_STATUS_OPEN_COD ||status==1) {
            mSlideSwitch.setChecked(true);
        } else {
            mSlideSwitch.setChecked(false);
        }
        mSlideSwitch.invalidate();*/
    }

    @Override
    public void autoExpenseResultSucess(int status) {
    //    mSlideSwitchStatus = status;
        if (status == AppConstant.AUTO_EXPENSE_STATUS_OPEN_SUCCESS_COD) {
            // mSlideSwitch.setChecked(true);
            mAdapter.setCheckedStatus(true);
        } else {
            //   mSlideSwitch.setChecked(false);
            mAdapter.setCheckedStatus(false);
        }
        //    mSlideSwitch.invalidate();
    }

    @Override
    public void getAutoStatusFail() {
        Toast.makeText(this, R.string.expense_query_fail, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void autoExpenseResultFail(int status) {
        if (status == AppConstant.AUTO_EXPENSE_STATUS_CLOSE_FAIL_COD) {
            Toast.makeText(this, R.string.expense_closed_fail, Toast.LENGTH_SHORT).show();
            mAdapter.setCheckedStatus(true);
            //   mSlideSwitch.setChecked(true);
        } else {
            Toast.makeText(this, R.string.expense_open_fail, Toast.LENGTH_SHORT).show();
            mAdapter.setCheckedStatus(false);
            mSelectComboPresent.requestAutoExpense(mToken, true);
            //     mSlideSwitch.setChecked(false);
        }
        //   mSlideSwitch.invalidate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_actionbar_menu:
                if (isFirst) {
                    setReult();
                }
                finish();
                break;
            case R.id.select_combo_sure:
                if (mselectComboBean == null) {
                    mselectComboBean = mAdapter.getData().get(1).getData();
                }
                if (isFirst) {
                    setReult();
                } else {
                    requestComboMethod();
                }
                break;

            case R.id.no_net_try_tv:
                initData();
                break;
        }
    }

    private void setReult() {
        Intent intent = new Intent();
        if (mselectComboBean == null) {
            intent.putExtra(AppConstant.SELECT_MYCOMBO_RESULT_TAG, mAdapter.getData().get(1).getData().getPrice());
        }
        setResult(AppConstant.SELECT_MYCOMBO_RESULT_COD, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MyApplication myApplication = null;
        /*if (isFirst) {
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
        }else{
            finish();}*/
        if (isFirst) {
            Intent intent = new Intent(SelectComboActivity.this, HomeActivity.class);
            intent.putExtra(FIRST_TARGET, true);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }

    //TODO 暂时先在此处处理，之后会移到p层
    private void requestComboMethod() {
        //套餐已经使用完
        if (mResidueTimes <= 0) {
            //可以直接购买套餐
            if (mselectComboBean.getType() == 0) {
                if (mWetherHaveCombo == 1) {
                    mSelectComboPresent.requestBuyCombo(mToken, mselectComboBean.getId(),mselectComboBean.getCode(),mselectComboBean.getPrice());
                }
                // 套餐 升级
                else if (mselectComboBean.getTimes() > mTimes) {
                    mSelectComboPresent.requestUpdateCombo(mToken, mselectComboBean.getId(),mselectComboBean.getCode(),mselectComboBean.getPrice());
                    // 套餐 降级
                } else if (mselectComboBean.getTimes() < mTimes) {
                    mSelectComboPresent.requestLowerCombo(mToken, mselectComboBean.getId(),mselectComboBean.getCode(),mselectComboBean.getPrice());
                }else if (mselectComboBean.getTimes() == mTimes){
                    Toast.makeText(this, R.string.combo_is_exist, Toast.LENGTH_SHORT).show();
                }
            } else {
                mSelectComboPresent.requestBuyCombo(mToken, mselectComboBean.getId(),mselectComboBean.getCode(),mselectComboBean.getPrice());
            }
            //套餐未使用完
        } else {
            //次卡
            if (mselectComboBean.getType() == 1) {
                mSelectComboPresent.requestBuyCombo(mToken, mselectComboBean.getId(),mselectComboBean.getCode(),mselectComboBean.getPrice());
                //月卡
            } else {
                // 套餐 升级
                if (mselectComboBean.getTimes() > mTimes) {
                    Toast.makeText(this, R.string.please_use_combo_over_then_update, Toast.LENGTH_SHORT).show();
                    // 套餐 降级
                } else if (mselectComboBean.getTimes() < mTimes) {
                    if (mComboStatus) {
                        Toast.makeText(this, R.string.have_uneffictive_combo, Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        mSelectComboPresent.requestLowerCombo(mToken, mselectComboBean.getId(),mselectComboBean.getCode(),mselectComboBean.getPrice());
                    }
                } else {
                    Toast.makeText(this, R.string.combo_is_exist, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void initItemClickListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (mAdapter.getData().get(position).getItemType() != SelectComboMultipleItem.AUTO_RENEW &&
                        mAdapter.getData().get(position).getItemType() != SelectComboMultipleItem.TITTLE_MONTHE
                        && mAdapter.getData().get(position).getItemType() != SelectComboMultipleItem.TITTLE_TIMES) {
                    mAdapter.setSelectItem(position);
                    mselectComboBean = mAdapter.getData().get(position).getData();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        /*mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
              *//*  mAdapter.setSelectItem(position);
                mselectComboBean = mAdapter.getData().get(position).getData();
                mAdapter.notifyDataSetChanged();*//*

            }
        });*/
    }

}
