package com.immotor.batterystation.android.mycombo;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.MyComboBean;
import com.immotor.batterystation.android.entity.MybatteryListBean;
import com.immotor.batterystation.android.util.DateTimeUtil;

import java.util.List;

/**
 * Created by jm on 2017/7/27 0027.
 */

public class MyComboAdapter extends BaseQuickAdapter<MyComboBean, BaseViewHolder> {
    private boolean isTimesComboShowed=false;

    public MyComboAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder viewHolder,MyComboBean item) {
        String source = mContext.getString(R.string.resides)+" "+"<font color='white'>" + item.getResidue() + "</font>"+" "+mContext.getString(R.string.times);
        viewHolder.setText(R.id.item_my_combo_times_tv, item.getTimes() + mContext.getString(R.string.times_month))
                .setText(R.id.item_my_combo_current_price_tv, "¥" + item.getPrice())
                .setText(R.id.item_my_combo_residue_times, Html.fromHtml(source))
                .setText(R.id.item_my_combo_buy_tv, DateTimeUtil.getDateTimeString("yyyy.MM.dd", item.getCreateTime()))
                .setText(R.id.item_my_combo_finish_tv, " - " + DateTimeUtil.getDateTimeString("yyyy.MM.dd", item.getFinishTime()));
        viewHolder.getView(R.id.my_combo_llyt).setBackgroundResource(R.mipmap.my_combo_bg_nomal);
        //0是月卡，1是次卡 1是当前套餐，2是未生效套餐
        if (item.getType()==1) {
            viewHolder.getView(R.id.item_my_combo_buy_tv).setVisibility(View.GONE);
            viewHolder.getView(R.id.item_my_combo_finish_tv).setVisibility(View.GONE);
        }

        if (item.getStatus()==1 && viewHolder.getAdapterPosition()==0) {
            viewHolder.getView(R.id.item_my_combo_title).setVisibility(View.VISIBLE);
            viewHolder.setText(R.id.item_my_combo_title, R.string.now_combo);

        } else if (item.getStatus() == 2 && !isTimesComboShowed) {
            viewHolder.getView(R.id.item_my_combo_title).setVisibility(View.VISIBLE);
            viewHolder.setText(R.id.item_my_combo_title, R.string.not_effect_combo);
            viewHolder.getView(R.id.my_combo_llyt).setBackgroundResource(R.mipmap.my_combo_bg_times_);
            isTimesComboShowed = true;
        } else {
            viewHolder.getView(R.id.item_my_combo_title).setVisibility(View.GONE);
        }
    }


}
