package com.immotor.batterystation.android.mycar.mycarmain.mvpmodel;

import android.content.Context;

import com.immotor.batterystation.android.app.NetParamCount;
import com.immotor.batterystation.android.entity.BindCarEntry;
import com.immotor.batterystation.android.entity.CarHeartBeatEntry;
import com.immotor.batterystation.android.entity.CarListBean;
import com.immotor.batterystation.android.http.carhttp.CarHttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jm on 2017/7/31 0031.
 */

public class MyCarMode implements IMyCarMode {

    private Context mContext;
    private IMyCarListener mListener;
    /**
     * 是否正在请求网络数据
     */
    private boolean isRequesting = false;
    private int pageIndex = 0;
    private boolean isBindCarRequest=false;
    private IBindcarListener mBindCarListener;
    private boolean isHeartRequest=false;
    private IHeartBeatListener mHeartBeatListener;

    @Override
    public void requestBatteryCar(Context context, String token,IMyCarListener listener) {
        mContext = context;
        mListener = listener;
        if (isRequesting) {
            return;
        }
        isRequesting = true;
        httpcarList(token);
    }

    @Override
    public void requestBindCar(Context context, String token, String sId, int zone, IBindcarListener listener) {
        if (isBindCarRequest) {
            return;
        }
        mBindCarListener=listener;
        isBindCarRequest = true;
        httpBindCar(context,token,sId,zone);
    }

    @Override
    public void requestHeartBeat(Context context, String token, String sIds, IHeartBeatListener listener) {

        if (isHeartRequest) {
            return;
        }
        isHeartRequest = true;
        mHeartBeatListener=listener;
        httpHeartBeatReQuest(context,token,sIds);

    }

    private void httpHeartBeatReQuest(Context context, String token, String sIds) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("sIDs",sIds);
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<List<CarHeartBeatEntry>>() {
            @Override
            public void onError(Throwable e) {
                mHeartBeatListener.onGetHeartBeatDataFailure(e);
                isHeartRequest = false;
            }

            @Override
            public void onNext(List<CarHeartBeatEntry> data) {
                if (data !=null) {
                    if (data.size() > 0) {
                     mHeartBeatListener.onGetHeartBeatSuccess(data);
                    }else {
                        mHeartBeatListener.onGetHeartBeatDataEmpty();
                    }
                } else {
                    mHeartBeatListener.onGetHeartBeatDataEmpty();
                }
                isHeartRequest = false;
            }
        };
        CarHttpMethods.getInstance().getHeartBeat(new ProgressSubscriber(subscriberOnNextListener, context,null), map);


    }

    private void httpBindCar(Context context,String token, String sId, int zone) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("sID",sId);
        map.put("zone", zone);
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<BindCarEntry>() {
            @Override
            public void onError(Throwable e) {
                mBindCarListener.onGetonBindCarDataFailure(e);
                isBindCarRequest = false;
            }

            @Override
            public void onNext(BindCarEntry data) {
                if (data !=null) {
                    mBindCarListener.onGetonBindCarSuccess(data);
                } else {
                    mBindCarListener.onGetonBindCarDataEmpty();
                }
                isBindCarRequest = false;
            }
        };
        CarHttpMethods.getInstance().getBindCar(new ProgressSubscriber(subscriberOnNextListener, context), map);
    }

    private void httpcarList(String token) {

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<List<CarListBean>>() {
            @Override
            public void onError(Throwable e) {
                mListener.onGetDataFailure(e);
                isRequesting = false;
            }

            @Override
            public void onNext(List<CarListBean> data) {
                if (data.size() >0) {
                    mListener.onGetDataSuccess(data);
                } else {
                        mListener.onGetDataEmpty();
                }
                isRequesting = false;
            }
        };
        CarHttpMethods.getInstance().getMyCarList(new ProgressSubscriber(subscriberOnNextListener, mContext), map);
    }

    /**
     * 结果数据处理
     */
    private void resultDataProcess(CarListBean response) {
    //    isCanPullup = isCanPullUp(response.getContent().size());
     //   mListener.onGetDataSuccess(isCanPullup, response.getContent());
    }

    /**
     * 是否可以上拉加载
     */
    private boolean isCanPullUp(int resultDataSize) {
        boolean isCanPullUp = true;
        if (resultDataSize < NetParamCount.PARAMS_REQUEST_COUNT) {
            isCanPullUp = false;
        }
        return isCanPullUp;
    }

    public interface IMyCarListener {

        void onGetDataSuccess( List<CarListBean> bean);

        void onGetDataEmpty();

        void onGetDataFailure(Throwable e);
    }

    public interface IBindcarListener{
        void onGetonBindCarSuccess( BindCarEntry bean);

        void onGetonBindCarDataEmpty();

        void onGetonBindCarDataFailure(Throwable e);
    }

    public interface IHeartBeatListener{
        void onGetHeartBeatSuccess(List<CarHeartBeatEntry> bean);

        void onGetHeartBeatDataEmpty();

        void onGetHeartBeatDataFailure(Throwable e);
    }

}
