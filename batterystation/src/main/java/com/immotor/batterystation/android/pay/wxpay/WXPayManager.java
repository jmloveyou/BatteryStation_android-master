package com.immotor.batterystation.android.pay.wxpay;

import android.content.Context;

import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Ashion on 2017/5/9.
 */

public class WXPayManager {
    public static final String WX_APPID = "wxe2744acb5c096056";
    private static WXPayManager instance = null;
    private boolean isRegister = false;
    IWXAPI msgApi;
    private WXPayManager(){}

    public static WXPayManager getInstance(Context context){
        if(instance == null){
            instance =new WXPayManager();
        }
        if(!instance.isRegister){
            instance.register(context);
        }
        return instance;
    }

    private void register(Context context){
        msgApi = WXAPIFactory.createWXAPI(context, WX_APPID, false);
//        msgApi.registerApp(WX_APPID);
        isRegister = true;
    }

    public IWXAPI getApi(){
        return msgApi;
    }

    public boolean isSupport(){
        if(msgApi.getWXAppSupportAPI()< Build.PAY_SUPPORTED_SDK_INT){
            return false;
        }
        return true;
    }

    public void requestPay(String partnerId, String prepayId, String packageValue, String nonceStr, String timeStamp, String sign){
        PayReq request = new PayReq();
        request.appId = WX_APPID;
        request.partnerId = partnerId;
        request.prepayId= prepayId;
        request.packageValue = packageValue;
        request.nonceStr= nonceStr;
        request.timeStamp= timeStamp;
        request.sign= sign;
        msgApi.sendReq(request);
    }

}
