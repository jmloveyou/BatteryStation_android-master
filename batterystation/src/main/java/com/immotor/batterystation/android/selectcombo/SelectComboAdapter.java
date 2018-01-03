package com.immotor.batterystation.android.selectcombo;

import android.graphics.Paint;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.SelectComboBean;


/**
 * Created by jm on 2017/8/7 0007.
 */

public class SelectComboAdapter extends BaseQuickAdapter<SelectComboBean, SelectComboAdapter.SelectComboHodler> {
    private int mSelectItem=0;

    public SelectComboAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(SelectComboHodler holder, SelectComboBean item) {
        holder.mComboTimes.setText(item.getTimes() + "次/月");
        holder.mCurrentPrice.setText("¥" + item.getPrice());
        holder.mPastPrice.setText("¥" +item.getOriginal_price());
        if (mSelectItem == holder.getAdapterPosition()&& item.getType()==0) {
            holder.mContentView.setBackgroundResource(R.mipmap.select_combo_nomal_click);
        }else if (mSelectItem == holder.getAdapterPosition()&& item.getType()==1) {
            holder.mContentView.setBackgroundResource(R.mipmap.select_combo_plus_click);
        } else if (item.getType()==0){
            holder.mContentView.setBackgroundResource(R.mipmap.select_combo_nomal);
        } else if (item.getType()==1) {
            holder.mContentView.setBackgroundResource(R.mipmap.select_combo_plus);
        }
    }

    public void setSelectItem(int selectItem) {
        mSelectItem = selectItem;
    }
    public int getSelectItem() {
        return mSelectItem;
    }

    public static class SelectComboHodler extends BaseViewHolder {

        View mContentView;
        TextView mCurrentPrice;
        TextView mComboTimes;
        TextView mPastPrice;
        public SelectComboHodler(View view) {
            super(view);
            mContentView = view;
            mComboTimes = (TextView)itemView.findViewById(R.id.select_combo_times_tv);
            mCurrentPrice = (TextView)itemView.findViewById(R.id.item_select_combo_current_price_tv);
            mPastPrice = (TextView) itemView.findViewById(R.id.item_select_combo_past_price_tv);
            mPastPrice.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰 ;
        }
    }
}
