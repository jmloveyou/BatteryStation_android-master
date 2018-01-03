package com.immotor.batterystation.android.mywallet.walletcharge;

import android.support.annotation.LayoutRes;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.MyChargeRecord;
import com.immotor.batterystation.android.util.DateTimeUtil;


/**
 * Created by jm on 2017/7/31 0031.
 */

public class ChargeRecyViewAdapter extends BaseQuickAdapter<MyChargeRecord.ContentBean, BaseViewHolder> {
    public ChargeRecyViewAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyChargeRecord.ContentBean item) {
        helper.setText(R.id.wallet_desc_text, item.getDiscountName())
                .setText(R.id.wallet_date, DateTimeUtil.getDateTimeString(null, item.getCreateTime()))
                .setText(R.id.wallet_value,"+"+item.getAmount()+mContext.getString(R.string.chense_money));

    }

}
