package com.immotor.batterystation.android.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;


/**
 * Created by jm on 2017/11/1 0001.
 */

public class MyComboMultipleItem implements MultiItemEntity {
    private  MyComboBean data;
    private int itemType;
    public static final int TITTLE_NOW = 1;
    public static final int COMMON_COMBO = 2;
    public static final int AUTO_RENEW = 3;
    public static final int TITTLE_NOT = 4;
    public static final int COMMON_COMBO_NOT = 5;

    public MyComboMultipleItem(int itemType,MyComboBean data) {
        this.itemType = itemType;
        this.data = data;
    }
    public MyComboMultipleItem(int itemType) {
        this.itemType = itemType;
    }
    public MyComboBean getData() {
        return data;
    }

    public void setData(MyComboBean data) {
        this.data = data;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
