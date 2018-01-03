package com.immotor.batterystation.android.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jm on 2017/10/31 0031.
 */

public class DataServer {


    public static List<SelectComboMultipleItem> getMultipleSelectData(List<SelectComboBean> datalist) {
        boolean HasTittleMonthe = true;
        boolean HasTittleTimes = true;
        //所有数据添加顺序不要动
        List<SelectComboMultipleItem> list = new ArrayList<>();
        for (int i = 0; i < datalist.size(); i++) {
            //0是月卡
            if (datalist.get(i).getType() == 0) {
                if (HasTittleMonthe) {
                    list.add(new SelectComboMultipleItem(SelectComboMultipleItem.TITTLE_MONTHE));
                    HasTittleMonthe = false;
                }
                list.add(new SelectComboMultipleItem(SelectComboMultipleItem.COMBO_MONTHE, datalist.get(i)));

                if (i == datalist.size()-1 || (i + 1 < datalist.size() && datalist.get(i + 1).getType() == 1)) {
                    list.add(new SelectComboMultipleItem(SelectComboMultipleItem.AUTO_RENEW));
                }
            }

            if (datalist.get(i).getType() == 1) {
                if (HasTittleTimes) {
                    list.add(new SelectComboMultipleItem(SelectComboMultipleItem.TITTLE_TIMES));
                    HasTittleTimes = false;
                }
                list.add(new SelectComboMultipleItem(SelectComboMultipleItem.COMBO_TIMES, datalist.get(i)));
            }
        }

        return list;
    }

    public static List<MyComboMultipleItem> getMultipleData(List<MyComboBean> datalist) {
        //所有数据添加顺序不要动
        boolean hasComboNotE = true;
        boolean hasComboE = true;
        boolean hasAutoRent=false;
        int a = 1;
        List<MyComboMultipleItem> list = new ArrayList<>();
        for (int i = 0; i < datalist.size(); i++) {
            //0是月卡
            if (datalist.get(i).getStatus() == 1) {
                if (hasComboE) {
                    list.add(new MyComboMultipleItem(MyComboMultipleItem.TITTLE_NOW));
                    hasComboE = false;
                }
                if (datalist.get(i).getType() == 0) {
                    list.add(a, new MyComboMultipleItem(MyComboMultipleItem.COMMON_COMBO, datalist.get(i)));
                    if (!hasAutoRent) {
                    list.add(a+1,new MyComboMultipleItem(MyComboMultipleItem.AUTO_RENEW));
                    hasAutoRent=true;
                    }
                    a = a + 1;
                } else {
                    list.add( new MyComboMultipleItem(MyComboMultipleItem.COMMON_COMBO, datalist.get(i)));
                }

            } else if (datalist.get(i).getStatus() == 2) {
                if (hasComboNotE) {
                    list.add(new MyComboMultipleItem(MyComboMultipleItem.TITTLE_NOT));
                    hasComboNotE = false;
                }
                list.add(new MyComboMultipleItem(MyComboMultipleItem.COMMON_COMBO_NOT, datalist.get(i)));
            }
        }
        return list;
    }
}
