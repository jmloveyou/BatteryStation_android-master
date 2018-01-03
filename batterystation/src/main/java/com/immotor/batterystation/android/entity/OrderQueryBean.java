package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by jm on 2017/8/11 0011.
 */

public class OrderQueryBean implements Serializable {

        /**
         * jwt : 5676
         * expire : 1193
         */

        private int jwt;
        private int expire;

        public int getJwt() {
            return jwt;
        }

        public void setJwt(int jwt) {
            this.jwt = jwt;
        }

        public int getExpire() {
            return expire;
        }

        public void setExpire(int expire) {
            this.expire = expire;
        }
}
