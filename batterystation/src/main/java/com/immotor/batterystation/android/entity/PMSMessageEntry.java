package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by jm on 2017/9/22 0022.
 */

public class PMSMessageEntry implements Serializable {
        /**
         * s_version : D1
         * content : pms 升级
         * url : ec/app/fw_version/808c72b58c974fba8bdea95898ad05d2.bin
         * version : 1.0.0.12
         * hw_version : 1.2
         * type : 1
         */

        private String s_version;
        private String content;
        private String url;
        private String version;
        private String hw_version;
        private int type;

        public String getS_version() {
            return s_version;
        }

        public void setS_version(String s_version) {
            this.s_version = s_version;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getHw_version() {
            return hw_version;
        }

        public void setHw_version(String hw_version) {
            this.hw_version = hw_version;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
}
