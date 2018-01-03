package com.immotor.batterystation.android.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public class MyChargeRecord implements Serializable {

    /**
         * content : [{"id":62,"status":1,"orderNumber":"2017072511050488","type":1,"discountCode":1,"discountName":"押金","amount":0.01,"currency_code":"CNY","payment_type":2,"userId":1,"createTime":1500951905000,"finishTime":1500951965000},{"id":23,"status":1,"orderNumber":"2017062810455864","type":1,"discountCode":1,"discountName":null,"amount":0.01,"currency_code":"CNY","payment_type":0,"userId":1,"createTime":1498617959000,"finishTime":null},{"id":20,"status":1,"orderNumber":"201706081346484","type":1,"discountCode":1,"discountName":null,"amount":0.01,"currency_code":"CNY","payment_type":0,"userId":1,"createTime":1496900808000,"finishTime":null},{"id":1,"status":1,"orderNumber":"201705241512583","type":1,"discountCode":1,"discountName":null,"amount":0.01,"currency_code":"CNY","payment_type":0,"userId":1,"createTime":1495609979000,"finishTime":1495609989000}]
         * last : true
         * totalPages : 1
         * totalElements : 4
         * first : true
         * sort : [{"direction":"DESC","property":"createTime","ignoreCase":false,"nullHandling":"NATIVE","descending":true,"ascending":false}]
         * numberOfElements : 4
         * size : 5
         * number : 0
         */

        private boolean last;
        private int totalPages;
        private int totalElements;
        private boolean first;
        private int numberOfElements;
        private int size;
        private int number;
        private List<ContentBean> content;
        private List<SortBean> sort;

        public boolean isLast() {
            return last;
        }

        public void setLast(boolean last) {
            this.last = last;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(int totalElements) {
            this.totalElements = totalElements;
        }

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        public int getNumberOfElements() {
            return numberOfElements;
        }

        public void setNumberOfElements(int numberOfElements) {
            this.numberOfElements = numberOfElements;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public List<ContentBean> getContent() {
            return content;
        }

        public void setContent(List<ContentBean> content) {
            this.content = content;
        }

        public List<SortBean> getSort() {
            return sort;
        }

        public void setSort(List<SortBean> sort) {
            this.sort = sort;
        }

        public static class ContentBean {
            /**
             * id : 62
             * status : 1
             * orderNumber : 2017072511050488
             * type : 1
             * discountCode : 1
             * discountName : 押金
             * amount : 0.01
             * currency_code : CNY
             * payment_type : 2
             * userId : 1
             * createTime : 1500951905000
             * finishTime : 1500951965000
             */

            private int id;
            private int status;
            private String orderNumber;
            private int type;
            private int discountCode;
            private String discountName;
            private double amount;
            private String currency_code;
            private int payment_type;
            private int userId;
            private long createTime;
            private long finishTime;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getOrderNumber() {
                return orderNumber;
            }

            public void setOrderNumber(String orderNumber) {
                this.orderNumber = orderNumber;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public int getDiscountCode() {
                return discountCode;
            }

            public void setDiscountCode(int discountCode) {
                this.discountCode = discountCode;
            }

            public String getDiscountName() {
                return discountName;
            }

            public void setDiscountName(String discountName) {
                this.discountName = discountName;
            }

            public double getAmount() {
                return amount;
            }

            public void setAmount(double amount) {
                this.amount = amount;
            }

            public String getCurrency_code() {
                return currency_code;
            }

            public void setCurrency_code(String currency_code) {
                this.currency_code = currency_code;
            }

            public int getPayment_type() {
                return payment_type;
            }

            public void setPayment_type(int payment_type) {
                this.payment_type = payment_type;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public long getFinishTime() {
                return finishTime;
            }

            public void setFinishTime(long finishTime) {
                this.finishTime = finishTime;
            }
        }

        public static class SortBean {
            /**
             * direction : DESC
             * property : createTime
             * ignoreCase : false
             * nullHandling : NATIVE
             * descending : true
             * ascending : false
             */

            private String direction;
            private String property;
            private boolean ignoreCase;
            private String nullHandling;
            private boolean descending;
            private boolean ascending;

            public String getDirection() {
                return direction;
            }

            public void setDirection(String direction) {
                this.direction = direction;
            }

            public String getProperty() {
                return property;
            }

            public void setProperty(String property) {
                this.property = property;
            }

            public boolean isIgnoreCase() {
                return ignoreCase;
            }

            public void setIgnoreCase(boolean ignoreCase) {
                this.ignoreCase = ignoreCase;
            }

            public String getNullHandling() {
                return nullHandling;
            }

            public void setNullHandling(String nullHandling) {
                this.nullHandling = nullHandling;
            }

            public boolean isDescending() {
                return descending;
            }

            public void setDescending(boolean descending) {
                this.descending = descending;
            }

            public boolean isAscending() {
                return ascending;
            }

            public void setAscending(boolean ascending) {
                this.ascending = ascending;
            }
        }
    }
