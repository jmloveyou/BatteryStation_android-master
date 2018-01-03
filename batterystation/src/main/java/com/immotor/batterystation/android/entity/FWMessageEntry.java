package com.immotor.batterystation.android.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jm on 2017/9/22 0022.
 */

public class FWMessageEntry implements Serializable {

        /**
         * list : [{"s_version":"T1","content":"aaa","url":"https://immotor-china.oss-cn-shenzhen.aliyuncs.com/aaa","version":"1.0.0.3","hw_version":"1.0","type":0},{"s_version":"T1","content":"bbb\r\n","url":"https://immotor-china.oss-cn-shenzhen.aliyuncs.com/bbb","version":"1.0.0.2","hw_version":"1.0","type":1},{"s_version":"T1","content":"ccc","url":"https://immotor-china.oss-cn-shenzhen.aliyuncs.com/ccc","version":"1.0.0.1","hw_version":"1.0","type":2}]
         * status : 1
         */

        private Integer status;
        private List<ListBean> list;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * s_version : T1
             * content : aaa
             * url : https://immotor-china.oss-cn-shenzhen.aliyuncs.com/aaa
             * version : 1.0.0.3
             * hw_version : 1.0
             * type : 0
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
}
