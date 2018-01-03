package com.immotor.batterystation.android.entity;


import java.io.Serializable;
import java.util.List;

/**
 * Created by jm on 2017/7/27 0027.
 */

    public  class MybatteryListBean implements Serializable{
        /**
         * content : [{"id":1,"status":0,"type":0,"discountCode":3,"discountName":"电池购买","amount":0.01,"createTime":1501139466000,"destroyTime":null,"uID":1}]
         * last : true
         * totalPages : 1
         * totalElements : 1
         * first : true
         * sort : [{"direction":"DESC","property":"createTime","ignoreCase":false,"nullHandling":"NATIVE","ascending":false,"descending":true}]
         * numberOfElements : 1
         * size : 10
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
             * id : 1
             * status : 0
             * type : 0
             * discountCode : 3
             * discountName : 电池购买
             * amount : 0.01
             * createTime : 1501139466000
             * destroyTime : null
             * uID : 1
             */

            private int id;
            private int status;
            private int type;
            private int discountCode;
            private String discountName;
            private double amount;
            private long createTime;
            private Object destroyTime;
            private int uID;

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

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public Object getDestroyTime() {
                return destroyTime;
            }

            public void setDestroyTime(Object destroyTime) {
                this.destroyTime = destroyTime;
            }

            public int getUID() {
                return uID;
            }

            public void setUID(int uID) {
                this.uID = uID;
            }
        }

        public static class SortBean {
            /**
             * direction : DESC
             * property : createTime
             * ignoreCase : false
             * nullHandling : NATIVE
             * ascending : false
             * descending : true
             */

            private String direction;
            private String property;
            private boolean ignoreCase;
            private String nullHandling;
            private boolean ascending;
            private boolean descending;

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

            public boolean isAscending() {
                return ascending;
            }

            public void setAscending(boolean ascending) {
                this.ascending = ascending;
            }

            public boolean isDescending() {
                return descending;
            }

            public void setDescending(boolean descending) {
                this.descending = descending;
            }
        }
    }
