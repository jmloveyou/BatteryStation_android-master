package com.immotor.batterystation.android.selectcombo.mvpmodel;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.AppConstant;
import com.immotor.batterystation.android.app.NetParamCount;
import com.immotor.batterystation.android.entity.AutoExpenseStatusBean;
import com.immotor.batterystation.android.entity.SelectComboBean;
import com.immotor.batterystation.android.http.ApiException;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.rentbattery.pay.RentBateryPayActivity;

import java.util.List;

/**
 * Created by jm on 2017/7/31 0031.
 */

public class SelectComboMode implements ISelectComboMode {

    private Context mContext;
    private ISelectComboListener mListener;
    /**
     * 是否正在请求网络数据
     */
    private boolean isRequesting = false;
    private boolean isUpdateRequesting = false;
    private boolean isLowerRequesting = false;
    private boolean isBuyTimesRequesting = false;
    private boolean isAutoExpenseRequesting = false;
    private boolean isAutoStatusRequesting = false;
    private IUpdateComboListener mUpdateListener;
    private IBuyTimesComboListener mBuyTimesListener;
    private ILowerComboListener mLowerListener;
    private IAutoExpenseListener mAutoExpenseListener;
    private IAutoStatusListener mAutoStatusListener;


    @Override
    public void requestSelectCombo(Context context, String token, ISelectComboListener listener) {
        mContext = context;
        mListener = listener;
        if (isRequesting) {
            return;
        }
        isRequesting = true;
        httpSelectCombo(token);
    }

    @Override
    public void requestUpdateCombo(Context context, String token, long id,int buycode, double Comboprice, IUpdateComboListener listener) {
        mUpdateListener = listener;
        if (isUpdateRequesting) {
            return;
        }
        isUpdateRequesting = true;
        httpUpdateCombo(token, id ,buycode,Comboprice);
    }

    private void httpUpdateCombo(String token, long id, final int buycode, final double Comboprice) {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {
                    int code = ((ApiException) e).getCode();
                    if (code == 631) {
                        Toast.makeText(mContext, R.string.please_use_combo_over_then_update, Toast.LENGTH_SHORT).show();
                    } else if (code == 626) {
                        Toast.makeText(mContext, R.string.not_enough_money_update_fail, Toast.LENGTH_SHORT).show();
                        startActivityToPay(buycode,Comboprice);
                    } else {
                        mUpdateListener.onGetUpdateDataFailure();
                    }
                }
                isUpdateRequesting = false;
            }

            @Override
            public void onNext(Object data) {
                if (data != null) {
                    mUpdateListener.onGetUpdateDataSuccess(data);
                }
                isUpdateRequesting = false;
            }
        };
        HttpMethods.getInstance().updateCombo(new ProgressSubscriber(subscriberOnNextListener, mContext), token, id);

    }

    @Override
    public void requestLowerCombo(Context context, String token, long id,  int buycode, double price, ILowerComboListener listener) {
        mLowerListener = listener;
        if (isLowerRequesting) {
            return;
        }
        isLowerRequesting = true;
        httpLowerCombo(context, token, id,buycode,price);
    }

    private void httpLowerCombo(final Context context, String token, long id, final int buycode, final double price) {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {
                    int code = ((ApiException) e).getCode();
                    if (code == 626) {
                        Toast.makeText(context, R.string.not_enough_money_combo_degrade_fail, Toast.LENGTH_SHORT).show();
                        startActivityToPay(buycode,price);
                    } else {
                        mLowerListener.onGetLowerDataFailure();
                    }
                }
                isLowerRequesting = false;
            }

            @Override
            public void onNext(Object data) {
                Toast.makeText(context, R.string.combo_degrade_scuess, Toast.LENGTH_SHORT).show();
                isLowerRequesting = false;
            }
        };
        HttpMethods.getInstance().lowerCombo(new ProgressSubscriber(subscriberOnNextListener, context), token, id);
    }

    @Override
    public void requestBuyCombo(Context context, String token, long id, int code, double price, IBuyTimesComboListener listener) {
        mBuyTimesListener = listener;
        if (isBuyTimesRequesting) {
            return;
        }
        isBuyTimesRequesting = true;
        httpBuyCombo(token, id,code,price);
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
                        Toast.makeText(context, R.string.buy_combo_first, Toast.LENGTH_SHORT).show();
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

    private void httpBuyCombo(String token, long id , final int buycode, final double price) {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                if (e instanceof ApiException) {
                    int code = ((ApiException) e).getCode();
                    if (code == 626) {
                   //     Toast.makeText(mContext, "余额不足，购买套餐失败", Toast.LENGTH_SHORT).show();
                        startActivityToPay(buycode,price);
                    } else {
                        mBuyTimesListener.onGetBuyTimesDataFailure();
                    }
                }
                isBuyTimesRequesting = false;
            }

            @Override
            public void onNext(Object data) {
                isBuyTimesRequesting = false;
                mBuyTimesListener.onGetBuyTimesDataSuccess(data);
            }
        };
        HttpMethods.getInstance().buyCombo(new ProgressSubscriber(subscriberOnNextListener, mContext), token, id);
    }
    private void startActivityToPay(int code,double price){
        Intent intent = new Intent(mContext,RentBateryPayActivity.class);
        intent.putExtra("rent_money",price);
        intent.putExtra("rent_code",code);
        intent.putExtra("entry_activity", 11);
        mContext.startActivity(intent);
    }
    private void httpSelectCombo(String token) {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<List<SelectComboBean>>() {
            @Override
            public void onError(Throwable e) {
                mListener.onGetDataFailure();
                isRequesting = false;
            }

            @Override
            public void onNext(List<SelectComboBean> data) {
                if (data != null) {
                    resultDataProcess(data);
                } else {
                    mListener.onGetDataEmpty();
                }
                isRequesting = false;
            }
        };
        HttpMethods.getInstance().SelectCombo(new ProgressSubscriber(subscriberOnNextListener, mContext), token);
    }

    /**
     * 结果数据处理
     */
    private void resultDataProcess(List<SelectComboBean> response) {
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

    public interface ISelectComboListener {

        void onGetDataSuccess(List<SelectComboBean> bean);

        void onGetDataEmpty();

        void onGetDataFailure();
    }

    public interface IUpdateComboListener {

        void onGetUpdateDataSuccess(Object bean);

        void onGetUpdateDataFailure();
    }

    public interface ILowerComboListener {

        void onGetLowerDataSuccess(Object bean);

        void onGetLowerDataFailure();
    }

    public interface IBuyTimesComboListener {

        void onGetBuyTimesDataSuccess(Object object);

        void onGetBuyTimesDataFailure();
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
