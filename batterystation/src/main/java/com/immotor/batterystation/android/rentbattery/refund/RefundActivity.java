package com.immotor.batterystation.android.rentbattery.refund;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.MybatteryListBean;
import com.immotor.batterystation.android.entity.RefundPayListBean;
import com.immotor.batterystation.android.rentbattery.refund.mvpView.IRefundView;
import com.immotor.batterystation.android.rentbattery.refund.mvppresent.RefundPresent;
import com.immotor.batterystation.android.ui.base.BaseActivity;

import static com.chad.library.adapter.base.BaseQuickAdapter.ALPHAIN;

/**
 * Created by jm on 2017/7/27.
 */

public class RefundActivity extends BaseActivity implements View.OnClickListener, IRefundView {
    private TextView mNoRefundView;
    private RecyclerView mRecyView;
    private String token;
    private RefundPresent refundPresent;
    private RefundPayListBean mDataBean;
    private LinearLayout mNoNetLayout;
    private RefundAdapter myBatteryAdapter;
    private TextView mListTittle;
    private TextView mListmsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund);
        token = mPreferences.getToken();
        refundPresent = new RefundPresent(this, this, token);
        initData();
    }

    private void initData() {
        refundPresent.requestRefundList();
    }

    @Override
    public void initUIView() {
        TextView mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        mTittle.setText(R.string.refund);
        ImageView mImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mImg.setOnClickListener(this);
        mImg.setImageDrawable(getDrawable(R.mipmap.nav_back_icon_white));
        mNoNetLayout = (LinearLayout) findViewById(R.id.no_net_layout);
        mRecyView = (RecyclerView) findViewById(R.id.refund_recy);
        mRecyView.setLayoutManager(new GridLayoutManager(this, 1));
        mNoRefundView = (TextView) findViewById(R.id.no_data_layout);
        myBatteryAdapter = new RefundAdapter(R.layout.item_refund_recy);
        mListTittle = (TextView) findViewById(R.id.refund_list_tittle);
        mListmsg = (TextView) findViewById(R.id.refund_list_msg);
        myBatteryAdapter.openLoadAnimation(ALPHAIN);
        mRecyView.setAdapter(myBatteryAdapter);
        ititListener();
    }

    private void ititListener() {
        findViewById(R.id.no_net_try_tv).setOnClickListener(this);
        myBatteryAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.item_refund_btn:
                        showdialog(myBatteryAdapter.getData().get(position));
                    break;
                    default:
                        break;

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_actionbar_menu:
                finish();
                break;
            case R.id.no_net_try_tv:
                refundPresent.requestRefundList();
                break;
            default:
                break;
        }
    }

    private void showdialog(final RefundPayListBean.ContentBean data) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.refund);
        dialog.setMessage(R.string.refund_dialog_msg);
        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                refundPresent.requestRefund(String.valueOf(data.getId()));
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void addData(RefundPayListBean bean) {
        this.mDataBean = bean;
        myBatteryAdapter.replaceData(mDataBean.getContent());
    }

    @Override
    public void onBatteryShow() {
        mNoNetLayout.setVisibility(View.GONE);
        mRecyView.setVisibility(View.GONE);
        mNoRefundView.setVisibility(View.VISIBLE);
        mListTittle.setVisibility(View.GONE);
        mListmsg.setVisibility(View.GONE);
    }

    @Override
    public void BatteryListShow() {
        mNoNetLayout.setVisibility(View.GONE);
        mRecyView.setVisibility(View.VISIBLE);
        mNoRefundView.setVisibility(View.GONE);
        mListTittle.setVisibility(View.VISIBLE);
        mListmsg.setVisibility(View.VISIBLE);
    }

    @Override
    public void getBatteryFail() {
        mRecyView.setVisibility(View.GONE);
        mNoRefundView.setVisibility(View.GONE);
        mNoNetLayout.setVisibility(View.VISIBLE);
        mListTittle.setVisibility(View.GONE);
        mListmsg.setVisibility(View.GONE);
    }
}
