package com.immotor.batterystation.android.selectcombo;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.MyComboMultipleItem;
import com.immotor.batterystation.android.entity.SelectComboBean;
import com.immotor.batterystation.android.entity.SelectComboMultipleItem;
import com.immotor.batterystation.android.view.WiperSwitchEx;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jm on 2017/11/2 0002.
 */

public class SelectComboMultiAdapter extends BaseMultiItemQuickAdapter<SelectComboMultipleItem, BaseViewHolder> {
    private boolean checkedStatus = true;
    private int mSelectItem = 1;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public SelectComboMultiAdapter(List<SelectComboMultipleItem> data) {
        super(data);

        addItemType(SelectComboMultipleItem.COMBO_MONTHE, R.layout.item_select_combo_layout);
        addItemType(SelectComboMultipleItem.AUTO_RENEW, R.layout.item_select_combo_switch);
        addItemType(SelectComboMultipleItem.COMBO_TIMES, R.layout.item_select_combo_layout);
        addItemType(SelectComboMultipleItem.TITTLE_MONTHE, R.layout.combo_header);
        addItemType(SelectComboMultipleItem.TITTLE_TIMES, R.layout.combo_header);

    }

    public void setSelectItem(int selectItem) {
        mSelectItem = selectItem;
    }

    public void setCheckedStatus(boolean checkedStatus) {
        this.checkedStatus = checkedStatus;
        notifyDataSetChanged();
    }

    public boolean getCheckedStatus() {
        return checkedStatus;
    }

    @Override
    protected void convert(BaseViewHolder helper, SelectComboMultipleItem item) {
        switch (helper.getItemViewType()) {
            case SelectComboMultipleItem.TITTLE_MONTHE:
                helper.setText(R.id.item_my_combo_title, R.string.month_combo_list);
                break;
            case SelectComboMultipleItem.TITTLE_TIMES:
                helper.setText(R.id.item_my_combo_title, R.string.times_combo_list);
                break;
            case SelectComboMultipleItem.COMBO_MONTHE:
             /*   if (mmonthComboIndex<monthNum) {
                mmonthComboIndex=mmonthComboIndex +1;
                }*/
                SelectComboBean data = item.getData();

                helper.setText(R.id.item_select_combo_current_price_tv, mContext.getString(R.string.china_money_symbol) + (int)data.getPrice())
                        .setText(R.id.select_combo_name, data.getName())
                        .setText(R.id.item_select_combo_past_price_tv, mContext.getString(R.string.china_money_symbol) + (int)data.getOriginal_price())
                      ;
                if (data.getTimes() > 9999) {
                    String times = mContext.getString(R.string.not_limt);
                    ((TextView) helper.getView(R.id.select_combo_times_tv)).setText(times);
                } else {
                    ((TextView) helper.getView(R.id.select_combo_times_tv)).setText(data.getTimes() + mContext.getString(R.string.times_month));
                }
                if (data.getPrice() == data.getOriginal_price()) {
                    ((TextView) helper.getView(R.id.item_select_combo_past_price_tv)).setVisibility(View.GONE);
                } else {
                    ((TextView) helper.getView(R.id.item_select_combo_past_price_tv)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
                    ((TextView) helper.getView(R.id.item_select_combo_past_price_tv)).setVisibility(View.VISIBLE);
                }

                if (mSelectItem == helper.getAdapterPosition()) {
                    if (helper.getAdapterPosition()==1) {
                        helper.getView(R.id.item_recy_select_combo).setBackgroundResource(R.mipmap.select_combo_nomal_click);
                    }
                    if ( helper.getView(R.id.item_recy_select_combo).getTag() !=null) {
                        if ((int) helper.getView(R.id.item_recy_select_combo).getTag() == R.mipmap.select_combo_nomal) {
                            helper.getView(R.id.item_recy_select_combo).setBackgroundResource(R.mipmap.select_combo_nomal_click);
                        } else if ((int) helper.getView(R.id.item_recy_select_combo).getTag() == R.mipmap.select_combo_plus) {
                            helper.getView(R.id.item_recy_select_combo).setBackgroundResource(R.mipmap.select_combo_plus_click);
                        } else if ((int) helper.getView(R.id.item_recy_select_combo).getTag() == R.mipmap.select_combo_pro) {
                            helper.getView(R.id.item_recy_select_combo).setBackgroundResource(R.mipmap.select_combo_pro_click);
                        } else if ((int) helper.getView(R.id.item_recy_select_combo).getTag() == R.mipmap.select_combo_power) {
                            helper.getView(R.id.item_recy_select_combo).setBackgroundResource(R.mipmap.select_combo_power_click);
                        }
                    }
                } else {
                            if (helper.getAdapterPosition()%5==1){
                                helper.getView(R.id.item_recy_select_combo).setTag(R.mipmap.select_combo_nomal);
                                helper.getView(R.id.item_recy_select_combo).setBackgroundResource(R.mipmap.select_combo_nomal);
                            } else if (helper.getAdapterPosition()%5==2) {
                                helper.getView(R.id.item_recy_select_combo).setTag(R.mipmap.select_combo_plus);
                                helper.getView(R.id.item_recy_select_combo).setBackgroundResource(R.mipmap.select_combo_plus);
                            }else if (helper.getAdapterPosition()%5==3) {
                                helper.getView(R.id.item_recy_select_combo).setTag(R.mipmap.select_combo_pro);
                                helper.getView(R.id.item_recy_select_combo).setBackgroundResource(R.mipmap.select_combo_pro);
                            }else if (helper.getAdapterPosition()%5==4) {
                                helper.getView(R.id.item_recy_select_combo).setTag(R.mipmap.select_combo_power);
                                helper.getView(R.id.item_recy_select_combo).setBackgroundResource(R.mipmap.select_combo_power);
                            }

                }
                break;
            case SelectComboMultipleItem.COMBO_TIMES:
                SelectComboBean data1 = item.getData();
                helper.setText(R.id.item_select_combo_current_price_tv, mContext.getString(R.string.china_money_symbol) + (int)data1.getPrice())
                        .setText(R.id.item_select_combo_past_price_tv,mContext.getString(R.string.china_money_symbol) + (int)data1.getOriginal_price())
                        .setText(R.id.select_combo_name, data1.getName())
                        .setText(R.id.select_combo_times_tv, data1.getTimes() + mContext.getString(R.string.times));

                if (data1.getPrice() == data1.getOriginal_price()) {
                    ((TextView) helper.getView(R.id.item_select_combo_past_price_tv)).setVisibility(View.GONE);
                } else {
                    ((TextView) helper.getView(R.id.item_select_combo_past_price_tv)).getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);  // 设置中划线并加清晰
                    ((TextView) helper.getView(R.id.item_select_combo_past_price_tv)).setVisibility(View.VISIBLE);
                }
                if (mSelectItem == helper.getAdapterPosition()) {
                    helper.getView(R.id.item_recy_select_combo).setBackgroundResource(R.mipmap.select_combo_times_click);
                } else {
                    helper.getView(R.id.item_recy_select_combo).setBackgroundResource(R.mipmap.select_combo_times);
                }
                break;
            case SelectComboMultipleItem.AUTO_RENEW:
                ((WiperSwitchEx) helper.getView(R.id.slide_switch)).setChecked(checkedStatus);
                //  helper.addOnClickListener(R.id.slide_switch);
                ((WiperSwitchEx) helper.getView(R.id.slide_switch)).setOnChangedListener(new WiperSwitchEx.OnChangedListener() {
                    @Override
                    public void onChanged(WiperSwitchEx wiperSwitch, boolean checkState) {
                        checkedStatus = checkState;
                    }
                });
                break;
            default:
                break;
        }
    }
}
