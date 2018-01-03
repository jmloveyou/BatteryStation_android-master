package com.immotor.batterystation.android.http.subscriber;

/**
 * Created by Ashion on 16/6/7.
 */
public interface SubscriberOnNextListener<T> {
    void onError(Throwable e);
    void onNext(T t);
}
