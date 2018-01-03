package com.immotor.batterystation.android.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jm on 2017/11/17 0017.
 */

public class RentBatteryListBean implements Serializable {

/*
    *//**
     * code : 600
     * msg : OK
     * result : [{"id":9,"code":9,"name":"租一颗电池","num":1,"type":4,"active":1,"deposit":0.01,"total":0.01},{"id":12,"code":12,"name":"租二颗电池","num":2,"type":4,"active":1,"deposit":0.01,"total":0.01}]
     *//*

    private List<ResultBean> result;

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {*/
        /**
         * id : 9
         * code : 9
         * name : 租一颗电池
         * num : 1
         * type : 4
         * active : 1
         * deposit : 0.01
         * total : 0.01
         */

        private int id;
        private int code;
        private String name;
        private int num;
        private int type;
        private int active;
        private double deposit;
        private double total;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getActive() {
            return active;
        }

        public void setActive(int active) {
            this.active = active;
        }

        public double getDeposit() {
            return deposit;
        }

        public void setDeposit(double deposit) {
            this.deposit = deposit;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }
    }

