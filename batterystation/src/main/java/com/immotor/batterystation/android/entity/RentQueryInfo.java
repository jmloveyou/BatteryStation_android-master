package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by Ashion on 2017/5/4.
 */

public class RentQueryInfo  implements Serializable {
    private int auth;
    private int expire;

    public int getAuth() {
        return auth;
    }

    public void setAuth(int auth) {
        this.auth = auth;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    @Override
    public String toString() {
        return "RentQueryInfo auth="+auth+", expire="+expire;
    }
}
