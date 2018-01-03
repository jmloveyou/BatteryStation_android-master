package com.immotor.batterystation.android.mapsearch;


import android.support.annotation.LayoutRes;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.SearchBean;

/**
 * Created by jm on 2017/12/19 0019.
 */

public class MapSearchGmpAdapter extends BaseQuickAdapter<SearchBean, BaseViewHolder> {
    public MapSearchGmpAdapter(@LayoutRes int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, SearchBean item) {
        helper.setText(R.id.search_address, item.getTitle());
    }
}
