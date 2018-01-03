package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by jm on 2017/8/18 0018.
 */

public class VersionUpdateBean implements Serializable {

        /**
         * id : 2
         * content : android第一个版本
         * status : 0
         * latest : 1.0.0
         * newest : 2.0.0
         * pVersion : P1
         * type : 0
         * url : http://baidu.com
         * createTime : 1504582774000
         */

        private String id;
        private String content;
        private int status;
        private String latest;
        private String newest;
        private String pVersion;
        private int type;
        private String url;
        private long createTime;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getLatest() {
            return latest;
        }

        public void setLatest(String latest) {
            this.latest = latest;
        }

        public String getNewest() {
            return newest;
        }

        public void setNewest(String newest) {
            this.newest = newest;
        }

        public String getPVersion() {
            return pVersion;
        }

        public void setPVersion(String pVersion) {
            this.pVersion = pVersion;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }
}
