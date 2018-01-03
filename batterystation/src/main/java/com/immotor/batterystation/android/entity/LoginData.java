package com.immotor.batterystation.android.entity;

import java.io.Serializable;

/**
 * Created by Ashion on 2017/5/10.
 */

public class LoginData  implements Serializable {
//    "access_token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwaG9uZSI6IjE1OTg5NTQ4OTY5In0.5rb6pQHFz_khoVTQwK8yJjj9khTJSRjqLkFODaRtJK8",
//    "token_type": "bearer",
//    "expires_in": 90,
//    "userInfo":
    private String access_token;
    private String token_type;
    private int expires_in;
    private UserInfo userInfo;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
}
