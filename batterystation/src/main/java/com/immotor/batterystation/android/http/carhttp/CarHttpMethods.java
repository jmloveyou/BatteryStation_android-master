package com.immotor.batterystation.android.http.carhttp;

import com.google.gson.Gson;
import com.immotor.batterystation.android.MyConfiguration;
import com.immotor.batterystation.android.entity.BindCarEntry;
import com.immotor.batterystation.android.entity.CarBatteryEntry;
import com.immotor.batterystation.android.entity.CarHeartBeatEntry;
import com.immotor.batterystation.android.entity.CarListBean;
import com.immotor.batterystation.android.entity.FWMessageEntry;
import com.immotor.batterystation.android.entity.HttpResult;
import com.immotor.batterystation.android.entity.MyRentInfo;
import com.immotor.batterystation.android.entity.PMSMessageEntry;
import com.immotor.batterystation.android.entity.TripBean;
import com.immotor.batterystation.android.entity.TripDayBean;
import com.immotor.batterystation.android.entity.TripDetailBean;
import com.immotor.batterystation.android.entity.UpdateEntry;
import com.immotor.batterystation.android.http.ApiException;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jm on 2017/9/15 0015.
 */

public class CarHttpMethods {
    /**
     * 获取网络验证流后回调
     * 注意！！！！
     * 收到数据是在子线程中，回调函数不能直接操作UI
     */
    private Retrofit retrofit;
    private CarService carService;

    //构造方法私有
    private CarHttpMethods() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(MyConfiguration.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(MyConfiguration.DEFAULT_TIMEOUT,TimeUnit.SECONDS);

        //if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(logging);
        //}

        MyConfiguration.setServerURL();

        retrofit = new Retrofit.Builder()
//                .addConverterFactory(TestFactory.create())          //需要打印http原始数据时开这个，关上面
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(builder.build())
                .baseUrl(MyConfiguration.getCarBaseUrl())
                .build();

        carService = retrofit.create(CarService.class);
    }

    //在访问HttpMethods时创建单例
    private static class CarSingletonHolder {
        private static final CarHttpMethods INSTANCE = new CarHttpMethods();
    }

    //获取单例
    public static CarHttpMethods getInstance() {
        return CarSingletonHolder.INSTANCE;
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class HttpCarResultFunc<T> implements Func1<HttpResult<T>, T> {
        @Override
        public T call(HttpResult<T> httpResult) {
            int code = httpResult.getCode();
            if (code != 200) {
                throw new ApiException(code, httpResult.getError());
            }

            return httpResult.getResult();
        }
    }

    private <T> void toCarSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    /**
     * 获取车辆列表
     * @param subscriber  由调用者传过来的观察者对象
     *  sID	String	32位string,scooter id	true
    token	string	32位string,用户调用接口凭证	true
     */
    public void getMyCarList(Subscriber<List<CarListBean>> subscriber, Map<String, Object> map){
        Observable observable = carService.carList(map)
                .map(new CarHttpMethods.HttpCarResultFunc<List<CarListBean>>());
        toCarSubscribe(observable, subscriber);
    }
    //绑定车辆
    public void getBindCar(Subscriber<BindCarEntry> subscriber, Map<String, Object> map){
        Observable observable = carService.bindCar(map)
                .map(new CarHttpMethods.HttpCarResultFunc<BindCarEntry>());
        toCarSubscribe(observable, subscriber);
    }

    //心跳
    public void getHeartBeat(Subscriber<List<CarHeartBeatEntry>> subscriber, Map<String, Object> map) {
        Observable observable = carService.heartBeat(map)
                .map(new CarHttpMethods.HttpCarResultFunc<List<CarHeartBeatEntry>>());
        toCarSubscribe(observable, subscriber);
    }
    //车电池列表
    public void getCarBatteryList(Subscriber<CarBatteryEntry> subscriber, Map<String, Object> map) {
        Observable observable = carService.carBattery(map)
                .map(new CarHttpMethods.HttpCarResultFunc<CarBatteryEntry>());
        toCarSubscribe(observable, subscriber);
    }
    //车行驶轨迹列表
    public void getCarTripList(Subscriber<List<TripDayBean>> subscriber, Map<String, Object> map) {
        Observable observable = carService.sevenDayPath(map)
                .map(new CarHttpMethods.HttpCarResultFunc<List<TripDayBean>>());
        toCarSubscribe(observable, subscriber);
    }

    // 获取日轨迹
    public void getTrackDetailDay(Subscriber<TripDayBean> subscriber, Map<String, Object> map) {
        Observable observable = carService.TripDayPath(map)
                .map(new CarHttpMethods.HttpCarResultFunc<TripDayBean>());
        toCarSubscribe(observable, subscriber);
    }

    public void getTrackDetail(Subscriber<List<TripDetailBean>> subscriber, Map<String, Object> map){
        Observable observable = carService.TrackDetail(map)
                .map(new HttpCarResultFunc<List<TripDetailBean>>());
        toCarSubscribe(observable, subscriber);
    }

    public void getPMsMessage(Subscriber<PMSMessageEntry> subscriber, Map<String, Object> map){
        Observable observable = carService.pmsMessage(map)
                .map(new HttpCarResultFunc<PMSMessageEntry>());
        toCarSubscribe(observable, subscriber);
    }
    public void getfwMessage(Subscriber<FWMessageEntry> subscriber, Map<String, Object> map){
        Observable observable = carService.fwMessage(map)
                .map(new HttpCarResultFunc<FWMessageEntry>());
        toCarSubscribe(observable, subscriber);
    }
    public void getUpdate(Subscriber<UpdateEntry> subscriber, Map<String, Object> map){
        Observable observable = carService.update(map)
                .map(new HttpCarResultFunc<UpdateEntry>());
        toCarSubscribe(observable, subscriber);
    }
    public void getunbindCar(Subscriber<Object> subscriber, Map<String, Object> map){
        Observable observable = carService.unbindCar(map)
                .map(new HttpCarResultFunc<Object>());
        toCarSubscribe(observable, subscriber);
    }

}
