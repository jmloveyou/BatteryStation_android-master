package com.immotor.batterystation.android.http.carhttp;

import com.immotor.batterystation.android.entity.BindCarEntry;
import com.immotor.batterystation.android.entity.CarBatteryEntry;
import com.immotor.batterystation.android.entity.CarHeartBeatEntry;
import com.immotor.batterystation.android.entity.CarListBean;
import com.immotor.batterystation.android.entity.FWMessageEntry;
import com.immotor.batterystation.android.entity.HttpResult;
import com.immotor.batterystation.android.entity.PMSMessageEntry;
import com.immotor.batterystation.android.entity.TripBean;
import com.immotor.batterystation.android.entity.TripDayBean;
import com.immotor.batterystation.android.entity.TripDetailBean;
import com.immotor.batterystation.android.entity.UpdateEntry;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by jm on 2017/9/15 0015.
 */

public interface  CarService {

    @POST("ec/list")
    Observable<HttpResult<List<CarListBean>>> carList(@Body Object body);

    @POST("ec/bind")
    Observable<HttpResult<BindCarEntry>> bindCar(@Body Object body);

    @POST("ec/heartbeat")
    Observable<HttpResult<List<CarHeartBeatEntry>>> heartBeat(@Body Object body);

    @POST("/ec/battery/list")
    Observable<HttpResult<CarBatteryEntry>> carBattery(@Body Object body);

    @POST("ec/track/seven")
    Observable<HttpResult<List<TripDayBean>>> sevenDayPath(@Body Object body);

    @POST("ec/track/day")
    Observable<HttpResult<TripDayBean>> TripDayPath(@Body Object body);

    @POST("ec/track/detail")
    Observable<HttpResult<List<TripDetailBean>>> TrackDetail(@Body Object body);

    @POST("ec/fw")
    Observable<HttpResult<FWMessageEntry>> fwMessage(@Body Object body);

    @POST("ec/pms")
    Observable<HttpResult<PMSMessageEntry>> pmsMessage(@Body Object body);

    @POST("ec/update")
    Observable<HttpResult<UpdateEntry>> update(@Body Object body);

    @POST("ec/unbind")
    Observable<HttpResult<Object>> unbindCar(@Body Object body);

}
