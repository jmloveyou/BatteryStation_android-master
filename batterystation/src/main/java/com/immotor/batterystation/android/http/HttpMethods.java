package com.immotor.batterystation.android.http;


import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.immotor.batterystation.android.MyConfiguration;
import com.immotor.batterystation.android.entity.AutoExpenseStatusBean;
import com.immotor.batterystation.android.entity.BatteryStationDetailInfo;
import com.immotor.batterystation.android.entity.BatteryStationInfo;
import com.immotor.batterystation.android.entity.HbBean;
import com.immotor.batterystation.android.entity.HttpResult;
import com.immotor.batterystation.android.entity.LoginData;
import com.immotor.batterystation.android.entity.MyBatteryCarBean;
import com.immotor.batterystation.android.entity.MyChargeRecord;
import com.immotor.batterystation.android.entity.MyComboBean;
import com.immotor.batterystation.android.entity.MyDepositInfo;
import com.immotor.batterystation.android.entity.MyExpenseRecord;
import com.immotor.batterystation.android.entity.MyRentInfo;
import com.immotor.batterystation.android.entity.MyWalletBean;
import com.immotor.batterystation.android.entity.MybatteryListBean;
import com.immotor.batterystation.android.entity.OrderQueryBean;
import com.immotor.batterystation.android.entity.RechargeGoodsInfo;
import com.immotor.batterystation.android.entity.RefundPayListBean;
import com.immotor.batterystation.android.entity.RentBatteryListBean;
import com.immotor.batterystation.android.entity.RentQueryInfo;
import com.immotor.batterystation.android.entity.SelectComboBean;
import com.immotor.batterystation.android.entity.VersionUpdateBean;
import com.immotor.batterystation.android.util.LogUtil;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

/**
 * Created by Ashion on 2016/6/7.
 */
public class HttpMethods {

    /**
     * 获取网络验证流后回调
     * 注意！！！！
     * 收到数据是在子线程中，回调函数不能直接操作UI
     */
    public interface IHttpStreamReceiver {
        /**
         * 收到网络数据，不能在这里直接操作UI
         *
         * @param data 可能为null，需要处理
         */
        void receiveData(byte[] data);
    }

    private Retrofit retrofit;
    private BatteryStationService batteryStationService;

    //构造方法私有
    private HttpMethods() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        try {
            builder.connectTimeout(MyConfiguration.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            builder.readTimeout(MyConfiguration.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }catch (Exception e) {
            LogUtil.e(e.toString());
        }

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
                .baseUrl(MyConfiguration.getBaseUrl())
                .build();

        batteryStationService = retrofit.create(BatteryStationService.class);
    }


    //在访问HttpMethods时创建单例
    private static class SingletonHolder {
        private static final HttpMethods INSTANCE = new HttpMethods();
    }

    //获取单例
    public static HttpMethods getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 用来统一处理Http的resultCode,并将HttpResult的Data部分剥离出来返回给subscriber
     *
     * @param <T> Subscriber真正需要的数据类型，也就是Data部分的数据类型
     */
    private class HttpResultFunc<T> implements Func1<HttpResult<T>, T> {
        @Override
        public T call(HttpResult<T> httpResult) {
            int code = httpResult.getCode();
            if (code != 200 && code != 600 ) {

                //协议定义不好，做强制处理
//                if(code == 700 || code ==701 || code ==702){
//                    String error = httpResult.getError();
//                    try{
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("code", code);
//                        if(code==702){
//                            String[] strings = error.split(",");
//                            map.put("version", strings[1]);
//                            map.put("url",strings[2]);
//                        }
//                        return (T) map;
//                    }catch (Exception e){
//                        throw new ApiException(0);
//                    }
//                }else if(code==300 || code==401){  //心跳处理的过期事件，如果心跳死了，先拉起来
//                    if(MyApplication.isLogin() && !BaseActivity.isExit){
//                        Intent intent = new Intent(MyApplication.context, HeartBeatService.class);
//                        intent.putExtra("token",Preferences.getInstance(MyApplication.context).getToken() );
//                        MyApplication.context.startService(intent);
//                    }
//                }

                throw new ApiException(code, httpResult.getError());
            }

            return httpResult.getResult();
        }
    }

    private class TestFunc implements Func1<List<BatteryStationInfo>, List<BatteryStationInfo>> {
        @Override
        public List<BatteryStationInfo> call(List<BatteryStationInfo> batteryStationInfos) {

            return batteryStationInfos;
        }
    }

    private <T> void toSubscribe(Observable<T> o, Subscriber<T> s) {
        o.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s);
    }

    /**************************************************************************************/

    /**
     * 获取电池桩列表
     *
     * @param subscriber
     * @param latitude   纬度
     * @param longitude  经度
     * @param radius     半径，单位米
     */
    public void getPowerStation(Subscriber<List<BatteryStationInfo>> subscriber, double latitude, double longitude, double radius) {
        Observable observable = batteryStationService.getPowerStation(latitude, longitude, radius).map(new HttpResultFunc<List<BatteryStationInfo>>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 获取我的订单
     *
     * @param subscriber
     * @param
     */
    public void getMyRent(Subscriber<MyRentInfo> subscriber,String token) {
        Observable observable = batteryStationService.getMyRent("bearer " + token).map(new HttpResultFunc<MyRentInfo>());
        toSubscribe(observable, subscriber);
    }

    /**
     * @param subscriber
     * @param pID        电池桩id
     * @param uID        用户id
     * @param code       没用
     * @param ports      电池端口
     */
    public void rentBattery(Subscriber<Object> subscriber,String token, String pID, String uID, int code, List<String> ports) {
        Observable observable = batteryStationService.rentBattery("bearer " + token, pID, uID, code, ports).map(new HttpResultFunc<Integer>());
        toSubscribe(observable, subscriber);
    }
    /**
     * 预订
     * @param subscriber
     * @param token
     */

    public void orderBattery(Subscriber<Integer> subscriber,String token, String pID, int num, int code) {
        Observable observable = batteryStationService.orderBattery("bearer " + token, pID, num, code).map(new HttpResultFunc<Integer>());
        toSubscribe(observable, subscriber);
    }

    /**
     * @param subscriber
     * @param pID        电池桩id
     * @param no         订单号，由rent，update和delete返回
     * @param uID        用户id
     */
    public void queryForRent(Subscriber<RentQueryInfo> subscriber, String token, String pID, String no, String uID) {
        Observable observable = batteryStationService.queryForRent("bearer " + token, pID, no, uID).map(new HttpResultFunc<RentQueryInfo>());
        toSubscribe(observable, subscriber);
    }

    public void orderBatteryQuery(Subscriber<OrderQueryBean> subscriber, String token, String pID, String no ) {
        Observable observable = batteryStationService.getOrderBatteryQuery("bearer " + token, pID, no).map(new HttpResultFunc<OrderQueryBean>());
        toSubscribe(observable, subscriber);
    }

    /**
     * @param subscriber
     * @param pID
     * @param uID
     * @param code
     * @param ports
     */
    public void updateBattery(Subscriber<Integer> subscriber, String token, String pID, String uID, int code, List<String> ports) {
        Observable observable = batteryStationService.updateBattery("bearer " + token, pID, code, uID, ports).map(new HttpResultFunc<Integer>());
        toSubscribe(observable, subscriber);
    }

    /**
     * @param subscriber
     * @param pID        电池桩id
     * @param uID        用户id
     * @param code       开箱密码，rentInfo里的auth
     */
    public void cancelRent(Subscriber<Integer> subscriber, String token, String pID, String uID, int code) {
        Observable observable = batteryStationService.cancelRent("bearer " + token, pID, code, uID).map(new HttpResultFunc<Integer>());
        toSubscribe(observable, subscriber);
    }


    /**
     * 获取单个电池桩的详情
     *
     * @param subscriber
     * @param pID        电池桩id
     */
    public void getPowerStationDetail(Subscriber<BatteryStationDetailInfo> subscriber, String pID) {
        Observable observable = batteryStationService.getPowerStationDetail(pID).map(new HttpResultFunc<BatteryStationDetailInfo>());
        toSubscribe(observable, subscriber);
    }


    /**
     * 获取验证码
     *
     * @param subscriber
     * @param map        ,包括
     *                   "captchaCode": "string",    身份字串，与获得图片验证码一致
     *                   "captchaValue": "string",   用户根据图片验证码输入的
     *                   "phone": "string"           手机号
     */
    public void getLoginCode(Subscriber<Object> subscriber, Map<String, Object> map) {
        Observable observable = batteryStationService.getLoginCode(map).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 登录
     *
     * @param subscriber
     * @param phone
     * @param code
     */
    public void login(Subscriber<LoginData> subscriber, String phone, String code, String uuid) {
        Observable observable = batteryStationService.login(phone, code, uuid).map(new HttpResultFunc<LoginData>());
        toSubscribe(observable, subscriber);
    }

    public void logout(Subscriber<Object> subscriber, String token){
        Observable observable = batteryStationService.logout("bearer " + token).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 更新TOKEN
     * @param subscriber
     * @param token
     */
    public void updateToken(Subscriber<Object> subscriber, String token) {
        Observable observable = batteryStationService.updateToken("bearer " + token).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 更新用户信息
     * @param subscriber
     * @param uID
     * @param data
     */
    public void updateUser(Subscriber<Object> subscriber,String token, String uID, Map<String, Object> data) {
        Observable observable = batteryStationService.updateUser("bearer " + token, uID, data).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 上传头像
     * @param subscriber
     * @param token
     * @param file
     */
    public void updateAvatar(Subscriber<String> subscriber, String token, MultipartBody.Part file){
        Observable observable = batteryStationService.updateAvatar("bearer " + token,file).map(new HttpResultFunc<String>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 微信支付请求订单
     * @param subscriber
     * @param token
     * @param code 支付代码
     */
    public void wxPayPreOrder(Subscriber<Map<String, String>> subscriber, String token, int code){
        Observable observable = batteryStationService.wxPayPreOrder("bearer " + token,code).map(new HttpResultFunc<Map<String, String>>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 查询微信支付结果
     * @param subscriber
     * @param token
     * @param tradeNo
     */
    public void wxPayOrderQuery(Subscriber<Object> subscriber, String token, String tradeNo){
        Observable observable = batteryStationService.wxPayOrderQuery("bearer " + token,tradeNo).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 获取可充值列表
     * @param subscriber
     * @param token
     */
    public void chargeAvailable(Subscriber<List<RechargeGoodsInfo>> subscriber, String token){
        Observable observable = batteryStationService.chargeAvailable("bearer " + token).map(new HttpResultFunc<List<RechargeGoodsInfo>>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 获取我的押金列表
     * @param subscriber
     * @param token
     */
    public void getMyDepositList(Subscriber<RefundPayListBean> subscriber, String token, int page, int size){
        Observable observable = batteryStationService.getMyDepositList("bearer " + token,page,size).map(new HttpResultFunc<RefundPayListBean>());
        toSubscribe(observable, subscriber);
    }
    public void getrefundRentPay(Subscriber<Object> subscriber, String token,String id){
        Observable observable = batteryStationService.refundRentPay("bearer " + token,id).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }

    public void getMyRentBatteryList(Subscriber<List<RentBatteryListBean>> subscriber, String token){
        Observable observable = batteryStationService.getMyRentBatteryList("bearer " + token).map(new HttpResultFunc<List<RentBatteryListBean>>());
        toSubscribe(observable, subscriber);
    }

    public void getMyAmount(Subscriber<MyWalletBean> subscriber, String token){
        Observable observable = batteryStationService.getMyAmount("bearer " + token).map(new HttpResultFunc<MyWalletBean>());
        toSubscribe(observable, subscriber);
    }

    /**
     *
     * 心跳
     */
    public void getPowerHb(Subscriber<HbBean> subscriber, String token){
        Observable observable = batteryStationService.powerHb("bearer " + token).map(new HttpResultFunc<HbBean>());
        toSubscribe(observable, subscriber);
    }
    /**
     * 获取未取的电池数量
     * @param subscriber
     * @param token
     */
    public void myBatteryNotFetch(Subscriber<Object> subscriber, String token){
        Observable observable = batteryStationService.getMyBatteryNotFetch("bearer " + token).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }
    /**
     * 购买
     * @param subscriber
     * @param token
     */
    public void buyBattery(Subscriber<Object> subscriber, String token,int number){
        Observable observable = batteryStationService.getBuyBattery("bearer " + token,number).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }
    /**
     * 添加,绑定电池
     * @param subscriber
     * @param token
     */
    public void addBattery(Subscriber<String> subscriber, String token,String number){
        Observable observable = batteryStationService.getAddMyBattery("bearer " + token,number).map(new HttpResultFunc<String>());
        toSubscribe(observable, subscriber);
    }
    /**
     * 扫描二维码，若已有订单则绑定该订单内容
     * @param subscriber
     * @param token
     */
    public void OrderScan(Subscriber<Object> subscriber, String token,String pID,int num,int type,int code){
        Observable observable=null;
        if (code == -1) {
            observable = batteryStationService.getOrderScanNoCode("bearer " + token, pID, num, type).map(new HttpResultFunc<Object>());
        } else {
            observable = batteryStationService.getOrderScan("bearer " + token, pID, num, type, code).map(new HttpResultFunc<Object>());
        }
        toSubscribe(observable, subscriber);
    }

    /**
     * 扫描查询
     * @param subscriber
     * @param token
     */
    public void OrderScanQuery(Subscriber<String> subscriber, String token,String pID,String no){
        Observable observable = batteryStationService.getOrderScanQuery("bearer " + token,pID,no).map(new HttpResultFunc<String>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 取消预订
     * @param subscriber
     * @param token
     */
    public void cancleOrder(Subscriber<Object> subscriber, String token,String pid,int code){
        Observable observable = batteryStationService.getCancleOrder("bearer " + token,pid,code).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }
    /**
     * 取消预订查询
     * @param subscriber
     * @param token
     */
    public void cancleOrderQuery(Subscriber<Object> subscriber, String token,String pid,int code){
        Observable observable = batteryStationService.getCancleOrderQuery("bearer " + token,pid,code).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 获取我的电池列表
     * @param subscriber
     * @param token
     */
    public void myBatteryList(Subscriber<MybatteryListBean> subscriber, String token){
        Observable observable = batteryStationService.getMyBatteryList("bearer " + token).map(new HttpResultFunc<MybatteryListBean>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 获取消费记录列表
     * @param subscriber
     * @param token
     */
    public void expenseRecords(Subscriber<MyExpenseRecord> subscriber, String token,int page,int size){
        Observable observable = batteryStationService.getExpenseRecords("bearer " + token,page,size).map(new HttpResultFunc<MyExpenseRecord>());
        toSubscribe(observable, subscriber);
    }

    //获取充值记录
    public void chargeRecords(Subscriber<MyChargeRecord> subscriber, String token, int page, int size){
        Observable observable = batteryStationService.getChargeRecords("bearer " + token,page,size).map(new HttpResultFunc<MyChargeRecord>());
        toSubscribe(observable, subscriber);
    }


    //获取我的电车
    public void myBatteryCar(Subscriber<MyBatteryCarBean> subscriber, String token){
        Observable observable = batteryStationService.getmyBatteryCar("bearer " + token).map(new HttpResultFunc<MyBatteryCarBean>());
        toSubscribe(observable, subscriber);
    }
    //获取我的套餐
    public void MyCombo(Subscriber<List<MyComboBean>> subscriber, String token){
        Observable observable = batteryStationService.getMyCombo("bearer " + token).map(new HttpResultFunc<List<MyComboBean>>());
        toSubscribe(observable, subscriber);
    }
    //选择可升级套餐
    public void SelectCombo(Subscriber<List<SelectComboBean>> subscriber, String token){
        Observable observable = batteryStationService.getSelectCombo("bearer " + token).map(new HttpResultFunc<List<SelectComboBean>>());
        toSubscribe(observable, subscriber);
    }

    //购买套餐
    public void buyCombo(Subscriber<Object> subscriber, String token,long id){
        Observable observable = batteryStationService.getBuyCombo("bearer " + token,id).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }
    //升级套餐
    public void updateCombo(Subscriber<Object> subscriber, String token,long id){
        Observable observable = batteryStationService.getUpdateCombo("bearer " + token,id).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }
    //降级套餐
    public void lowerCombo(Subscriber<Object> subscriber, String token,long id){
        Observable observable = batteryStationService.getLowerCombo("bearer " + token,id).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }
    //取消降级套餐
    public void cancleLowerCombo(Subscriber<Object> subscriber, String token){
        Observable observable = batteryStationService.getcancleLowerCombo("bearer " + token).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }

    //版本升级
    public void VersionUpdata(Subscriber<VersionUpdateBean> subscriber,int type,String pVersion){
        Observable observable = batteryStationService.getVersionUpdata(type,pVersion).map(new HttpResultFunc<VersionUpdateBean>());
        toSubscribe(observable, subscriber);
    }

    // 自动续约
    public void autoContractCombo(Subscriber<Boolean> subscriber, String token, boolean auto){
        Observable observable = batteryStationService.getAutoContractCombo("bearer " + token,auto).map(new HttpResultFunc<Boolean>());
        toSubscribe(observable, subscriber);
    }

    // 获取自动续约状态
    public void autoContractComboStatus(Subscriber<AutoExpenseStatusBean> subscriber, String token){
        Observable observable = batteryStationService.getAutoContractComboStatus("bearer " + token).map(new HttpResultFunc<AutoExpenseStatusBean>());
        toSubscribe(observable, subscriber);
    }

    // 获取自动续约状态
    public void notice(Subscriber<Object> subscriber, String token){
        Observable observable = batteryStationService.getNotice("bearer " + token).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }

    public void zfbPayOrder(Subscriber<Map<String, String>> subscriber, String token,int code){
        Observable observable = batteryStationService.zFBPayPreOrder("bearer " + token,code).map(new HttpResultFunc<Map<String, String>>());
        toSubscribe(observable, subscriber);
    }

    public void zfbPayOrderQuery(Subscriber<Object> subscriber, String token,String  outTradeNo,String tradeNo){
        Observable observable = batteryStationService.zFXPayOrderQuery("bearer " + token,outTradeNo,tradeNo).map(new HttpResultFunc<Object>());
        toSubscribe(observable, subscriber);
    }

    /**
     * 获取图片验证码
     *
     * @param receiver    数据接收器
     * @param captchaCode 身份字串，如uuid
     */
    public void getCaptcha(final IHttpStreamReceiver receiver, final String captchaCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(MyConfiguration.getBaseUrl() + "getcaptcha?captchaCode=" + captchaCode);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(10000);
                    if (conn.getResponseCode() == 200) {
                        byte[] picByte;
                        InputStream fis = conn.getInputStream();
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        byte[] bytes = new byte[1024];
                        int length = -1;
                        while ((length = fis.read(bytes)) != -1) {
                            bos.write(bytes, 0, length);
                        }
                        picByte = bos.toByteArray();
                        bos.close();
                        fis.close();
                        if (receiver != null) {
                            receiver.receiveData(picByte);
                        }
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (receiver != null) {
                    receiver.receiveData(null);
                }
            }
        }).start();
    }


    /******************以下为测试类，用于打印出http返回原始数据  ******************************************************************************************/

    static class TestFactory extends Converter.Factory {
        public static TestFactory create() {
            return new TestFactory();
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                                Retrofit retrofit) {
//            if(annotations!=null && annotations.length == 1){
//                LogUtil.d("whj "+annotations[0].toString());
//                if(annotations[0].toString().contains("/power/user/refresh")){
            return new TestResponseConvert<>(type);
//                }
//            }
//            return null;
        }

        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                              Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {

            return new TestRequestConvert<>();
        }
    }

    static class TestRequestConvert<T extends Object> implements Converter<T, RequestBody> {
        @Override
        public RequestBody convert(T value) throws IOException {
            final MediaType MEDIA_TYPE = MediaType.parse("application/json");
            byte[] bytes = value.toString().getBytes();
            return RequestBody.create(MEDIA_TYPE, bytes);
        }
    }

    static class TestResponseConvert<T extends Object>
            implements Converter<ResponseBody, T> {
        Class<? super T> rawType;

        public TestResponseConvert(Type type) {
            rawType = (Class<? super T>) getRawType(type);
        }

        public T convert(ResponseBody value) throws IOException {
            LogUtil.d("http data: \n" + value.string());
            if (value.source().buffer().size() > 0) {
                return null;
            } else {

            }
            try {
                final Constructor<? super T> constructor = rawType.getDeclaredConstructor();
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                Object[] args = null;
                return (T) constructor.newInstance(args);
            } catch (Exception e) {

            }
            return null;
        }


        public static Class<?> getRawType(Type type) {
            if (type instanceof Class<?>) {
                // type is a normal class.
                return (Class<?>) type;

            } else if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;

                // I'm not exactly sure why getRawType() returns Type instead of Class.
                // Neal isn't either but suspects some pathological case related
                // to nested classes exists.
                Type rawType = parameterizedType.getRawType();
                checkArgument(rawType instanceof Class);
                return (Class<?>) rawType;

            } else if (type instanceof GenericArrayType) {
                Type componentType = ((GenericArrayType) type).getGenericComponentType();
                return Array.newInstance(getRawType(componentType), 0).getClass();

            } else if (type instanceof TypeVariable) {
                // we could use the variable's bounds, but that won't work if there are multiple.
                // having a raw type that's more general than necessary is okay
                return Object.class;

            } else if (type instanceof WildcardType) {
                return getRawType(((WildcardType) type).getUpperBounds()[0]);

            } else {
                String className = type == null ? "null" : type.getClass().getName();
                throw new IllegalArgumentException("Expected a Class, ParameterizedType, or "
                        + "GenericArrayType, but <" + type + "> is of type " + className);
            }
        }

    }


}
