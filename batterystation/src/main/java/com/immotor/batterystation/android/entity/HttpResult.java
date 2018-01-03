package com.immotor.batterystation.android.entity;

/**
 * Created by Ashion on 2017/5/2.
 */

public class HttpResult<T> {

    private int code;

    private String status;

    private String error;
    // data
    private T result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
