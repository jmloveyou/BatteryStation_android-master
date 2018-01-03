package com.immotor.batterystation.android.rentbattery.refund;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.RefundPayListBean;
import com.immotor.batterystation.android.util.DateTimeUtil;

import java.util.List;

/**
 * Created by jm on 2017/7/27 0027.
 */

public class RefundAdapter extends BaseQuickAdapter<RefundPayListBean.ContentBean, BaseViewHolder> {

    public RefundAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, RefundPayListBean.ContentBean item) {
        viewHolder.setText(R.id.item_refund_time, mContext.getString(R.string.rent_time_xml) + DateTimeUtil.getDateTimeString(DateTimeUtil.dateFormat, item.getCreateTime()))
        .setText(R.id.item_refund_price,(int)item.getAmount()+mContext.getString(R.string.chense_money))
        .setText(R.id.item_refund_name,mContext.getString(R.string.superbattery)+"（"+item.getNum()+mContext.getString(R.string.cap)+")")
        .addOnClickListener(R.id.item_refund_btn);
    }
}
