package com.immotor.batterystation.android.mycombo.mvpmodel;

import android.content.Context;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.app.NetParamCount;
import com.immotor.batterystation.android.entity.AutoExpenseStatusBean;
import com.immotor.batterystation.android.entity.MyComboBean;
import com.immotor.batterystation.android.http.ApiException;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.selectcombo.mvpmodel.SelectComboMode;

import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public class MyComboMode implements IMyComboMode {

    private Context mContext;
    private IMyComboListener mListener;
    /**
     * 是否正在请求网络数据
     */
    private boolean isRequesting = false;
    private boolean isCancleRequesting = false;
    private boolean isAutoExpenseRequesting = false;
    private boolean isAutoStatusRequesting = false;

    private ICancleLowerComboListener mCanclerLowerListener;
    private IAutoExpenseListener mAutoExpenseListener;
    private IAutoStatusListener mAutoStatusListener;
    @Override
    public void requestMyCombo(Context context, String token ,IMyComboListener listener) {
        mContext = context;
        mListener = listener;
        if (isRequesting) {
            return;
        }
        isRequesting = true;
        httpMyCombo(token);
    }

    private void httpMyCombo(String token) {

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<List<MyComboBean>>() {
            @Override
            public void onError(Throwable e) {
                mListener.onGetDataFailure();
                isRequesting = false;
            }

            @Override
            public void onNext(List<MyComboBean> data) {
                if (data.size()>0) {
                    resultDataProcess(data);
                } else {
                    mListener.onGetDataEmpty();
                }
                isRequesting = false;
            }
        };
            HttpMethods.getInstance().MyCombo(new ProgressSubscriber(subscriberOnNextListener, mContext), token);
    }
    @Override
    public void requestcancleLowerCombo(Context context, String token,ICancleLowerComboListener listener) {
        mCanclerLowerListener = listener;
        if (isCancleRequesting) {
            return;
        }
        isCancleRequesting = true;
        httpCancleLowerCombo(context,token);
    }

    private void httpCancleLowerCombo(final Context context, String token) {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                mCanclerLowerListener.onCanclerLowerComboFailure();
                isCancleRequesting = false;
            }

            @Override
            public void onNext(Object data) {
                mCanclerLowerListener.onCanclerLowerComboSuccess(data);
                isCancleRequesting = false;
            }
        };
        HttpMethods.getInstance().cancleLowerCombo(new ProgressSubscriber(subscriberOnNextListener, context), token);

    }

    @Override
    public void requestAutoExpense(Context context, String token, boolean status, IAutoExpenseListener listener) {
        mAutoExpenseListener = listener;
        if (isAutoExpenseRequesting) {
            return;
        }
        isAutoExpenseRequesting = true;
        httpAutoExpense(context, token, status);
    }

    private void httpAutoExpense(final Context context, String token, final boolean status) {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Boolean>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {
                    int code = ((ApiException) e).getCode();
                    if (code==628) {
                        Toast.makeText(context, R.string.plase_buy_combo_first, Toast.LENGTH_SHORT).show();
                    }
                    if (status==false) {
                        //传入关闭失败的状态
                        mAutoExpenseListener.onAutoExpenseDataFailure(AppConstant.AUTO_EXPENSE_STATUS_CLOSE_FAIL_COD);
                    }else {
                        //传入打开失败的状态
                        mAutoExpenseListener.onAutoExpenseDataFailure(AppConstant.AUTO_EXPENSE_STATUS_OPEN_FAIL_COD);
                    }
                }
                isAutoExpenseRequesting = false;
            }

            @Override
            public void onNext(Boolean data) {
                if (data) {
                    mAutoExpenseListener.onAutoExpenseDataSuccess(AppConstant.AUTO_EXPENSE_STATUS_OPEN_SUCCESS_COD);
                } else {
                    mAutoExpenseListener.onAutoExpenseDataSuccess(AppConstant.AUTO_EXPENSE_STATUS_CLOSE_SUCCESS_COD);
                }
                isAutoExpenseRequesting = false;
            }
        };
        HttpMethods.getInstance().autoContractCombo(new ProgressSubscriber(subscriberOnNextListener, context), token, status);
    }

    @Override
    public void requestgetAutoStatus(Context context, String token, IAutoStatusListener listener) {
        mAutoStatusListener = listener;
        if (isAutoStatusRequesting) {
            return;
        }
        isAutoStatusRequesting = true;
        httpAutoStatus(context,token);
    }
    private void httpAutoStatus(final Context context, String token) {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<AutoExpenseStatusBean>() {
            @Override
            public void onError(Throwable e) {
                mAutoStatusListener.onGetAutoStatusDataFailure();
                isAutoStatusRequesting = false;
            }

            @Override
            public void onNext(AutoExpenseStatusBean data) {
                if (data != null) {
                    mAutoStatusListener.onGetAutoStatusSuccess(AppConstant.AUTO_EXPENSE_STATUS_OPEN_COD);
                } else {
                    mAutoStatusListener.onGetAutoStatusSuccess(AppConstant.AUTO_EXPENSE_STATUS_CLOSE_COD);
                }
                isAutoStatusRequesting = false;
            }
        };
        HttpMethods.getInstance().autoContractComboStatus(new ProgressSubscriber(subscriberOnNextListener, context), token);
    }

    /**
     * 结果数据处理
     */
    private void resultDataProcess(List<MyComboBean> response) {
    //    isCanPullup = isCanPullUp(response.getContent().size());
        mListener.onGetDataSuccess(response);
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

    public interface IMyComboListener {

        void onGetDataSuccess(List<MyComboBean> bean);

        void onGetDataEmpty();

        void onGetDataFailure();
    }

    public interface ICancleLowerComboListener {

        void onCanclerLowerComboSuccess(Object bean);

        void onCanclerLowerComboFailure();
    }

    public interface IAutoExpenseListener {

        void onAutoExpenseDataSuccess(int status);

        void onAutoExpenseDataFailure(int status);
    }

    public interface IAutoStatusListener {

        void onGetAutoStatusSuccess(int  status);

        void onGetAutoStatusDataFailure();
    }

}
