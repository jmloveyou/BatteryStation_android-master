package com.immotor.batterystation.android.mycombo;

import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.database.Preferences;
import com.immotor.batterystation.android.entity.MyComboBean;
import com.immotor.batterystation.android.entity.MyComboMultipleItem;
import com.immotor.batterystation.android.mycombo.mvppresent.MyComboPresent;
import com.immotor.batterystation.android.util.DateTimeUtil;
import com.immotor.batterystation.android.view.WiperSwitchEx;

import java.util.List;

/**
 * Created by jm on 2017/11/1 0001.
 */

public class MyComboMultiAdapter extends BaseMultiItemQuickAdapter<MyComboMultipleItem, BaseViewHolder> {
    private boolean checkedStatus = false;
    private MyComboPresent mPresent;
    private String mToken;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MyComboMultiAdapter(List<MyComboMultipleItem> data) {
        super(data);
        addItemType(MyComboMultipleItem.TITTLE_NOW, R.layout.combo_header);
        addItemType(MyComboMultipleItem.COMMON_COMBO, R.layout.item_my_combo_recy);
        addItemType(MyComboMultipleItem.TITTLE_NOT, R.layout.combo_header);
        addItemType(MyComboMultipleItem.AUTO_RENEW, R.layout.item_combo_auto_renew);
        addItemType(MyComboMultipleItem.COMMON_COMBO_NOT, R.layout.item_my_combo_recy);
    }

    public void setChecked(boolean checkedStatus) {
        this.checkedStatus = checkedStatus;
        notifyDataSetChanged();
    }

    public void setPresent(MyComboPresent present, String token) {
        this.mPresent = present;
        this.mToken = token;
    }

    @Override
    protected void convert(BaseViewHolder helper, MyComboMultipleItem item) {
        switch (helper.getItemViewType()) {
            case MyComboMultipleItem.TITTLE_NOW:
                helper.setText(R.id.item_my_combo_title, R.string.now_can_sue_combo);
                break;
            case MyComboMultipleItem.TITTLE_NOT:
                helper.setText(R.id.item_my_combo_title, R.string.not_effect_combo);
                break;
            case MyComboMultipleItem.COMMON_COMBO:
                switch (item.getData().getStatus()) {
                    case 1:
                        switch (item.getData().getType()) {
                            case 0:
                                MyComboBean data = item.getData();
                                String source = mContext.getString(R.string.resides)+" "+"<font color='white'>" + data.getResidue() + "</font>"+" "+mContext.getString(R.string.times);
                                helper.setText(R.id.item_my_combo_name,data.getPackageName())
                                        .setText(R.id.item_my_combo_current_price_tv, "¥" + (int)data.getPrice())
                                     //   .setText(R.id.item_my_combo_times_tv, data.getTimes() + "次/月")
                                        .setText(R.id.item_my_combo_times_effect, R.string.validity)
                                        .setText(R.id.item_my_combo_buy_tv, DateTimeUtil.getDateTimeString("yyyy.MM.dd", data.getCreateTime()))
                                        .setText(R.id.item_my_combo_finish_tv, " - " + DateTimeUtil.getDateTimeString("yyyy.MM.dd", data.getFinishTime()))
                                ;
                                if (data.getTimes() > 9999) {
                                    String times = mContext.getString(R.string.not_limit_times);
                                    ((TextView) helper.getView(R.id.item_my_combo_times_tv)).setVisibility(View.GONE);
                                    ((TextView) helper.getView(R.id.item_my_combo_residue_times)).setText(times);
                                } else {
                                    ((TextView) helper.getView(R.id.item_my_combo_times_tv)).setVisibility(View.VISIBLE);
                                    ((TextView) helper.getView(R.id.item_my_combo_residue_times)).setText(Html.fromHtml(source));
                                    ((TextView) helper.getView(R.id.item_my_combo_times_tv)).setText(data.getTimes() +mContext.getString(R.string.times_month));
                                }

                                helper.getView(R.id.item_my_combo_times_effect).setVisibility(View.VISIBLE);
                                helper.getView(R.id.my_combo_llyt).setBackgroundResource(R.mipmap.my_combo_bg_nomal);
                                break;
                            case 1:
                                MyComboBean data1 = item.getData();
                                String source1 = mContext.getString(R.string.resides)+" "+"<font color='white'>" + data1.getResidue() + "</font>"+" "+mContext.getString(R.string.times);
                                helper.setText(R.id.item_my_combo_residue_times, Html.fromHtml(source1))
                                        .setText(R.id.item_my_combo_current_price_tv, mContext.getString(R.string.china_money_symbol) + (int)data1.getPrice())
                                        .setText(R.id.item_my_combo_times_tv, data1.getTimes() + R.string.times)
                                        .setText(R.id.item_my_combo_name,data1.getPackageName())
                                ;
                                helper.getView(R.id.item_my_combo_times_effect).setVisibility(View.GONE);
                                helper.getView(R.id.my_combo_llyt).setBackgroundResource(R.mipmap.my_combo_bg_times_);
                                break;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
                break;
            case MyComboMultipleItem.COMMON_COMBO_NOT:
                //此处为未生效月套餐
                MyComboBean data = item.getData();
                String source = mContext.getString(R.string.resides)+" "+"<font color='white'>" + data.getResidue() + "</font>"+" "+mContext.getString(R.string.times);
                helper.setText(R.id.item_my_combo_residue_times, Html.fromHtml(source))
                        .setText(R.id.item_my_combo_current_price_tv, "¥" + (int)data.getPrice())
                       // .setText(R.id.item_my_combo_times_tv, data.getTimes() + "次/月")
                        .setText(R.id.item_my_combo_name,data.getPackageName())
                        .setText(R.id.item_my_combo_times_effect, R.string.effective_date)
                        .setText(R.id.item_my_combo_buy_tv, DateTimeUtil.getDateTimeString("yyyy.MM.dd", data.getCreateTime()))
                        .setText(R.id.item_my_combo_finish_tv, " - " + DateTimeUtil.getDateTimeString("yyyy.MM.dd", data.getFinishTime()));
                if (data.getTimes() > 9999) {
                    String times = mContext.getString(R.string.not_limit_times);
                    ((TextView) helper.getView(R.id.item_my_combo_times_tv)).setText(times);
                } else {
                    ((TextView) helper.getView(R.id.item_my_combo_times_tv)).setText(data.getTimes() + R.string.times_month);
                }
                helper.getView(R.id.my_combo_llyt).setBackgroundResource(R.mipmap.my_combo_not_effect_bg);
                break;

            case MyComboMultipleItem.AUTO_RENEW:
                ((WiperSwitchEx) helper.getView(R.id.combo_slide_switch)).setChecked(checkedStatus);
                ((WiperSwitchEx) helper.getView(R.id.combo_slide_switch)).setOnChangedListener(new WiperSwitchEx.OnChangedListener() {
                    @Override
                    public void onChanged(WiperSwitchEx wiperSwitch, boolean checkState) {
                        // 0关闭状态 ，1打开状态
                        if (checkState) {
                            mPresent.requestAutoExpense(mToken, true);
                        } else {
                            mPresent.requestAutoExpense(mToken, false);
                        }
                    }
                });
                //        helper.addOnClickListener(R.id.combo_slide_switch);
                break;
            default:
                break;
        }
    }

}
