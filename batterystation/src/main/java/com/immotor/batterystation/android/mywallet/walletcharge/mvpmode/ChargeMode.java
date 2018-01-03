package com.immotor.batterystation.android.mywallet.walletcharge.mvpmode;

import android.content.Context;
import android.widget.Toast;

import com.immotor.batterystation.android.app.NetParamCount;
import com.immotor.batterystation.android.entity.MyChargeRecord;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public class ChargeMode implements IChargeMode {

    private Context mContext;
    private IWalletChargeListener mListener;
    /**
     * true 刷新  false 加载更多
     */
    private boolean mIsRefresh = false;
    /**
     * 是否正在请求网络数据
     */
    private boolean isRequesting = false;
    /**
     * 是否有数据 可以加载更多
     */
    private boolean isCanPullup = true;
    /**
     * 当前页索引
     */
    private int pageIndex = 0;

    @Override
    public void requestChargeRecord(Context context,boolean isRefresh, String token,boolean progress, IWalletChargeListener listener) {
        mContext = context;
        mListener = listener;
        mIsRefresh = isRefresh;
        if (isRequesting) {
            return;
        }
        isRequesting = true;
        if (mIsRefresh) {
            pageIndex=0;
        }
        httpchargeRecords(token, pageIndex, NetParamCount.PARAMS_REQUEST_COUNT,progress);

    }

    private void httpchargeRecords(String token, int page, int size,boolean progree) {

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<MyChargeRecord>() {
            @Override
            public void onError(Throwable e) {
                mListener.onGetDataFailure();
                isRequesting = false;
                mIsRefresh = false;

            }

            @Override
            public void onNext(MyChargeRecord data) {
                if (data.getContent().size()>0) {
                    resultDataProcess(data);
                    pageIndex = pageIndex + 1;
                } else {
                    if (mIsRefresh) {
                        mListener.onGetDataEmpty();
                    } else {
                        mListener.onGetDataSuccess(false, new ArrayList<MyChargeRecord.ContentBean>());
                    }
                }
                isRequesting = false;
                mIsRefresh = false;
            }
        };
        if (progree) {
            HttpMethods.getInstance().chargeRecords(new ProgressSubscriber(subscriberOnNextListener, mContext), token, page, size);
        } else {
            HttpMethods.getInstance().chargeRecords(new ProgressSubscriber(subscriberOnNextListener, mContext,null), token, page, size);
        }
    }

    /**
     * 结果数据处理
     */
    private void resultDataProcess(MyChargeRecord response) {
        isCanPullup = isCanPullUp(response.getContent().size());
        mListener.onGetDataSuccess(isCanPullup, response.getContent());
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

    public interface IWalletChargeListener {

        void onGetDataSuccess(boolean isCanPullup, List<MyChargeRecord.ContentBean> bean);

        void onGetDataEmpty();

        void onGetDataFailure();
    }

}
