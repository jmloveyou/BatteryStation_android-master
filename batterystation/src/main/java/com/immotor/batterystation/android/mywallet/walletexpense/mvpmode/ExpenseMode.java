package com.immotor.batterystation.android.mywallet.walletexpense.mvpmode;

import android.content.Context;
import android.widget.Toast;

import com.immotor.batterystation.android.app.NetParamCount;
import com.immotor.batterystation.android.entity.MyChargeRecord;
import com.immotor.batterystation.android.entity.MyExpenseRecord;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public class ExpenseMode implements IExpenseMode {

    private IWalletExpenseListener mListener;
    private Context mContext;
    private boolean isRequesting=false;
    private boolean mIsRefresh;
    private int pageIndex = 0;
    private boolean isCanPullup=true;

    @Override
    public void requestExpenseRecord(Context context,boolean isRefresh, String token,boolean progress ,IWalletExpenseListener listener) {
        mContext = context;
        mListener = listener;
        mIsRefresh = isRefresh;
        if (isRequesting) {
            return;
        }
        if (mIsRefresh) {
            pageIndex = 0;
        }
        httpExpenseRecords(token,pageIndex, NetParamCount.PARAMS_REQUEST_COUNT,progress);
    }
    private void httpExpenseRecords(String token, int page, int size,boolean progress) {

            SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<MyExpenseRecord>() {
                @Override
                public void onError(Throwable e) {
                    mListener.onGetDataFailure();
                    isRequesting = false;
                    mIsRefresh = false;
                }

                @Override
                public void onNext(MyExpenseRecord data) {
                    if (data.getContent().size()>0) {
                        resultDataProcess(data);
                        pageIndex = pageIndex + 1;
                    } else {
                        if (mIsRefresh) {
                            mListener.onGetDataEmpty();
                        } else {
                            mListener.onGetDataSuccess(false, new ArrayList<MyExpenseRecord.ContentBean>());
                        }
                    }
                    isRequesting = false;
                    mIsRefresh = false;
                }
            };
        if (progress) {
            HttpMethods.getInstance().expenseRecords(new ProgressSubscriber(subscriberOnNextListener, mContext), token, page, size);
        } else {
            HttpMethods.getInstance().expenseRecords(new ProgressSubscriber(subscriberOnNextListener, mContext,null), token, page, size);
        }
        }

    /**
     * 结果数据处理
     */
    private void resultDataProcess(MyExpenseRecord response) {
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
    public interface IWalletExpenseListener {

        void onGetDataSuccess(boolean isCanPull, List<MyExpenseRecord.ContentBean> bean);

        void onGetDataEmpty();

        void onGetDataFailure();
    }
}
