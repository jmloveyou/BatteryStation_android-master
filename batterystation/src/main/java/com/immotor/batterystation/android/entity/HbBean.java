package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 *
 * Created by jm on 2017/8/17 0017.
 */

public class HbBean implements Serializable {

        /**
         * val : 21
         * desc : apns_order_failed
         */

        private int val;
        private String desc;

        public int getVal() {
            return val;
        }

        public void setVal(int val) {
            this.val = val;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
}
