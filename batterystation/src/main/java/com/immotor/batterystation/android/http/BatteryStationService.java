package com.immotor.batterystation.android.http;


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

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Ashion on 2016/6/7.
 */
public interface BatteryStationService {

    //@GET("data")
    //Observable<HttpResult<List<Object>>> getData(@Query("start") int start, @Query("count") int count);
    /******************************* User API ***********************/

    @POST("user/passcode")
    Observable<HttpResult<Object>> passCode(@Body Object body);

    @Headers("Content-Type: application/json")
    @GET("power")
    Observable<HttpResult<List<BatteryStationInfo>>> getPowerStation(@Query("latitude") double latitude, @Query("longitude") double longitude, @Query("radius") double radius);

    @Headers("Content-Type: application/json")
    @GET("power/rent")
    Observable<HttpResult<MyRentInfo>> getMyRent(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("power/rent/{pID}")
    Observable<HttpResult<Integer>> rentBattery(@Header("Authorization") String token, @Path("pID") String pID, @Query("uID") String uID,@Query("code") int code, @Body Object ports);
    @Headers("Content-Type: application/json")
    @POST("power/rent/{pID}")
    Observable<HttpResult<Integer>> orderBattery(@Header("Authorization") String token, @Path("pID") String pID, @Query("num") int num,@Query("type") int code);

    @Headers("Content-Type: application/json")
    @GET("power/rent/{pID}")
    Observable<HttpResult<RentQueryInfo>> queryForRent(@Header("Authorization") String token, @Path("pID") String pID, @Query("no") String no, @Query("uID") String uID);

    @Headers("Content-Type: application/json")
    @PUT("power/rent/{pID}")
    Observable<HttpResult<Integer>> updateBattery(@Header("Authorization") String token, @Path("pID") String pID, @Query("code") int code, @Query("uID") String uID, @Query("ports") List<String> ports);

    @Headers("Content-Type: application/json")
    @DELETE("power/rent/{pID}")
    Observable<HttpResult<Integer>> cancelRent(@Header("Authorization") String token, @Path("pID") String pID, @Query("code") int code, @Query("uID") String uID);

    @Headers("Content-Type: application/json")
    @GET("power/{pID}")
    Observable<HttpResult<BatteryStationDetailInfo>> getPowerStationDetail(@Path("pID") String pID);

    @Headers("Content-Type: application/json")
    @POST("oauth/send")
    Observable<HttpResult<Object>> getLoginCode(@Body Object body);

    @Headers("Content-Type: application/json")
    @POST("oauth/token")
    Observable<HttpResult<LoginData>> login(@Query("phone") String phone, @Query("cCode") String cCode, @Query("deviceToken") String deviceToken);

    @Headers("Content-Type: application/json")
    @GET("power/user/canceling")
    Observable<HttpResult<Object>> logout(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @PUT("power/user/{uID}")
    Observable<HttpResult<Object>> updateUser(@Header("Authorization") String token, @Path("uID") String uID, @Body Object body);


    @Multipart
    @POST("power/user/upload")
    Observable<HttpResult<String>> updateAvatar(@Header("Authorization") String token, @Part MultipartBody.Part file);


    @Headers("Content-Type: application/json")
    @POST("power/wxPay/preOrder")
    Observable<HttpResult<Map<String, String>>> wxPayPreOrder(@Header("Authorization") String token, @Query("code") int code);

    @Headers("Content-Type: application/json")
    @GET("power/wxPay/orderQuery")
    Observable<HttpResult<Object>> wxPayOrderQuery(@Header("Authorization") String token,@Query("out_trade_no") String tradeNo);

    @Headers("Content-Type: application/json")
    @POST("/power/user/refresh")
    Observable<HttpResult<Object>> updateToken(@Header("Authorization") String value);

    //心跳
    @Headers("Content-Type: application/json")
    @POST("/power/hb")
    Observable<HttpResult<HbBean>> powerHb(@Header("Authorization") String token);


    @Headers("Content-Type: application/json")
    @GET("power/user/charge/available")
    Observable<HttpResult<List<RechargeGoodsInfo>>> chargeAvailable(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("power/user/deposit/list")
    Observable<HttpResult<RefundPayListBean>> getMyDepositList(@Header("Authorization") String token, @Query("page") int page, @Query("size") int size);

    @Headers("Content-Type: application/json")
    @POST("/power/user/deposit/refund")
    Observable<HttpResult<Object>> refundRentPay(@Header("Authorization") String token,@Query("id")String id);

    @Headers("Content-Type: application/json")
    @GET("/power/user/deposit/available")
    Observable<HttpResult<List<RentBatteryListBean>>> getMyRentBatteryList(@Header("Authorization") String token);


    @Headers("Content-Type: application/json")
    @GET("power/user/amount")
    Observable<HttpResult<MyWalletBean>> getMyAmount(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("/power/user/battery/not")
    Observable<HttpResult<Object>> getMyBatteryNotFetch(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @DELETE("/power/rent/cancel/{pID}")
    Observable<HttpResult<Object>> getCancleOrder(@Header("Authorization") String token, @Path("pID") String pID,@Query("code") int code);

    @Headers("Content-Type: application/json")
    @GET("/power/rent/cancel/query")
    Observable<HttpResult<Object>> getCancleOrderQuery(@Header("Authorization") String token,@Query("pID") String pID,@Query("code") int code);

    @Headers("Content-Type: application/json")
    @POST("/power/user/battery/buy")
    Observable<HttpResult<Object>> getBuyBattery(@Header("Authorization") String token,@Query("quantity") int number);

    //此处为服务器和station之间通讯结果查询
    @Headers("Content-Type: application/json")
    @GET("/power/rent/query")
    Observable<HttpResult<OrderQueryBean>> getOrderBatteryQuery(@Header("Authorization") String token, @Query("pID") String pID, @Query("no") String no);

    //获取我的电池列表
    @Headers("Content-Type: application/json")
    @GET("/power/user/battery/list")
    Observable<HttpResult<MybatteryListBean>> getMyBatteryList(@Header("Authorization") String token);

    //绑定，添加电池
    @Headers("Content-Type: application/json")
    @POST("/power/user/battery/bind")
    Observable<HttpResult<String>> getAddMyBattery(@Header("Authorization") String token,@Query("sn") String number);

    //获取消费记录
    @Headers("Content-Type: application/json")
    @GET("/power/user/expense/records")
    Observable<HttpResult<MyExpenseRecord>> getExpenseRecords(@Header("Authorization") String token, @Query("page") int page, @Query("size") int size);

    //获取充值记录
    @Headers("Content-Type: application/json")
    @GET("/power/user/charge/records")
    Observable<HttpResult<MyChargeRecord>> getChargeRecords(@Header("Authorization") String token, @Query("page") int page, @Query("size") int size);

    //获取我的车
    @Headers("Content-Type: application/json")
    @GET(" http://120.76.157.58:8080/ec/list")
    Observable<HttpResult<MyBatteryCarBean>> getmyBatteryCar(@Header("Authorization") String token);

    //获取我的套餐
    @Headers("Content-Type: application/json")
    @GET("/power/user/package/list")
    Observable<HttpResult<List<MyComboBean>>> getMyCombo(@Header("Authorization") String token);

    //获取可购买套餐
    @Headers("Content-Type: application/json")
    @GET("/power/user/package/available")
    Observable<HttpResult<List<SelectComboBean>>> getSelectCombo(@Header("Authorization") String token);

    //购买套餐
    @Headers("Content-Type: application/json")
    @POST("/power/user/package/{id}")
    Observable<HttpResult<Object>> getBuyCombo(@Header("Authorization") String token, @Path("id") long id);

    //升级套餐
    @Headers("Content-Type: application/json")
    @POST("/power/user/package/upgrade")
    Observable<HttpResult<Object>> getUpdateCombo(@Header("Authorization") String token, @Query("id") long id);

    //降级套餐
    @Headers("Content-Type: application/json")
    @POST("/power/user/package/degrade")
    Observable<HttpResult<Object>> getLowerCombo(@Header("Authorization") String token, @Query("id") long id);

    //取消降级套餐
    @Headers("Content-Type: application/json")
    @POST("/power/user/package/del")
    Observable<HttpResult<Object>> getcancleLowerCombo(@Header("Authorization") String token);

    //扫描二维码，若已有订单则绑定该订单内容
    @Headers("Content-Type: application/json")
    @POST("/power/rent/scan")
    Observable<HttpResult<Object>> getOrderScan(@Header("Authorization") String token,@Query("pID") String pID,@Query("num") int num,@Query("type") int type,@Query("code") int code);

    //扫描二维码，若已有订单则绑定该订单内容
    @Headers("Content-Type: application/json")
    @POST("/power/rent/scan")
    Observable<HttpResult<Object>> getOrderScanNoCode(@Header("Authorization") String token,@Query("pID") String pID,@Query("num") int num,@Query("type") int type);

    //扫描查询
    @Headers("Content-Type: application/json")
    @GET("/power/rent/scan/query")
    Observable<HttpResult<String>> getOrderScanQuery(@Header("Authorization") String token,@Query("pID") String pID,@Query("no") String num);

    //版本升级
    @Headers("Content-Type: application/json")
    @POST("/power/upgrade")
    Observable<HttpResult<VersionUpdateBean>> getVersionUpdata(@Query("type") int type, @Query("pVersion") String pversion);

    //自动续约
    @Headers("Content-Type: application/json")
    @POST("/power/user/autoExpense")
    Observable<HttpResult<Boolean>> getAutoContractCombo(@Header("Authorization") String token, @Query("auto") boolean auto);

    //获取自动续约状态
    @Headers("Content-Type: application/json")
    @GET("/power/user/getAuto")
    Observable<HttpResult<AutoExpenseStatusBean>> getAutoContractComboStatus(@Header("Authorization") String token);

    //获取活动通知
    @Headers("Content-Type: application/json")
    @GET("/power/user/getNotice")
    Observable<HttpResult<Object>> getNotice(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @POST("power/aliPay/preOrder")
    Observable<HttpResult<Map<String, String>>> zFBPayPreOrder(@Header("Authorization") String token, @Query("code") int code);

    @Headers("Content-Type: application/json")
    @GET("power/aliPay/orderQuery")
    Observable<HttpResult<Object>> zFXPayOrderQuery(@Header("Authorization") String token,@Query("out_trade_no") String outTradeNo,@Query("trade_no") String tradeNo);

    /**************   如何在URL中带上参数    *******************/
    @GET("aaa/{user}")
    Observable<HttpResult<Object>> getXXX(@Path("user") String user);
}
