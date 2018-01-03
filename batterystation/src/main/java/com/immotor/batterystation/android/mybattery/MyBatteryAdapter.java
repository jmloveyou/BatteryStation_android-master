package com.immotor.batterystation.android.mybattery;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.MybatteryListBean;
import com.immotor.batterystation.android.util.DateTimeUtil;

import java.util.List;

/**
 * Created by jm on 2017/7/27 0027.
 */

public class MyBatteryAdapter extends BaseQuickAdapter<MybatteryListBean.ContentBean, BaseViewHolder> {

    public MyBatteryAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, MybatteryListBean.ContentBean item) {
        viewHolder.setText(R.id.battery_buy_date, mContext.getString(R.string.rent_time)+"：" + DateTimeUtil.getDateTimeString(DateTimeUtil.dateFormat, item.getCreateTime()))
        .setText(R.id.battery_buy_price,item.getAmount()+mContext.getString(R.string.chense_money));
        if (item.getStatus() == 0) {
            viewHolder.setText(R.id.not_fetch_mark, R.string.not_fetch_battery);
         //   ((TextView)viewHolder.getView(R.id.not_fetch_mark)).setTextColor(0xffa5a5a5);
        //    viewHolder.getView(R.id.not_fetch_mark).setBackground(null);
        } else {
            viewHolder.setText(R.id.not_fetch_mark, R.string.have_fetch_battery);
          //  ((TextView)viewHolder.getView(R.id.not_fetch_mark)).setTextColor(0xffa5a5a5);
        //    viewHolder.getView(R.id.not_fetch_mark_llyt).setBackgroundResource(R.mipmap.marker_bg_batery);
        }
    }

}
