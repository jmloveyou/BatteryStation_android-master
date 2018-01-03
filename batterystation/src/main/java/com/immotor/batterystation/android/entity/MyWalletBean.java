package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by jm on 2017/8/3 0003.
 */

public class MyWalletBean implements Serializable {

    /**
     * amount : 0.01
     */

    private double amount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
