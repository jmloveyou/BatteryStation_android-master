package com.immotor.batterystation.android.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by jm on 2017/11/2 0002.
 */

public class SelectComboMultipleItem implements MultiItemEntity {
    private SelectComboBean data;
    private int itemType;
    public static final int COMBO_MONTHE = 1;
    public static final int COMBO_TIMES = 2;
    public static final int AUTO_RENEW = 3;
    public static final int TITTLE_MONTHE = 4;
    public static final int TITTLE_TIMES = 5;


    public SelectComboMultipleItem(int itemType, SelectComboBean data) {
        this.itemType = itemType;
        this.data = data;
    }

    public SelectComboMultipleItem(int itemType) {
        this.itemType = itemType;
    }

    public SelectComboBean getData() {
        return data;
    }

    public void setData(SelectComboBean data) {
        this.data = data;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
