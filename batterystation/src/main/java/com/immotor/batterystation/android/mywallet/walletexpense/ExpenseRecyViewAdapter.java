package com.immotor.batterystation.android.mywallet.walletexpense;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.MyChargeRecord;
import com.immotor.batterystation.android.entity.MyExpenseRecord;
import com.immotor.batterystation.android.util.DateTimeUtil;

import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public class ExpenseRecyViewAdapter extends BaseQuickAdapter<MyExpenseRecord.ContentBean, BaseViewHolder> {

    public ExpenseRecyViewAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, MyExpenseRecord.ContentBean item) {
        helper.setText(R.id.wallet_desc_text, item.getDiscount_name())
                .setText(R.id.wallet_date, DateTimeUtil.getDateTimeString(null, item.getCreateTime()))
                .setText(R.id.wallet_value,item.getAmount()+mContext.getString(R.string.chense_money));

    }

}
