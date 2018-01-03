package com.immotor.batterystation.android.http;


import android.app.Application;

/**
 * Created by Ashion on 2016/6/7.
 */
public class ApiException extends RuntimeException {

    public static final int USER_NOT_EXIST = 100;
    public static final int WRONG_PASSWORD = 101;

    private int resultCode = 0;

//    public ApiException(int resultCode) {
//        this(getApiExceptionMessage(resultCode));
//        this.resultCode = resultCode;
//    }


//    public ApiException(String detailMessage) {
//        super(detailMessage);
//    }

    public ApiException(int code, String detailMessage){
        super(detailMessage);
        resultCode = code;
    }

    public int getCode(){
        return resultCode;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     * @param code
     * @return
     */
    private static String getApiExceptionMessage(int code){
        String message = "";
        switch (code) {
//            case USER_NOT_EXIST:    //100
//                message = getError(R.string.error_101);
//                break;
//            case WRONG_PASSWORD:    //101
//                message = getError(R.string.error_101);
//                break;
//            case 200:    //200
//                message = getError(R.string.error_200);
//                break;
//            case 201:
//                message = getError(R.string.error_201);
//                break;
//            case 202:
//                message = getError(R.string.error_202);
//                break;
//            case 204:
//                message = getError(R.string.error_204);
//                break;
//            case 205:
//                message = getError(R.string.error_205);
//                break;
//            case 206:
//                message = getError(R.string.error_206);
//                break;
//            case 207:
//                message = getError(R.string.error_207);
//                break;
//            case 211:
//                message = getError(R.string.error_211);
//                break;
//            case 300:
//                message = getError(R.string.error_300);
//                break;
//            case 400:
//                message = getError(R.string.error_400);
//                break;
//            case 401:
//                message = getError(R.string.error_401);
//                break;
//            case 402:
//                message = getError(R.string.error_402);
//                break;
//            case 403:
//                message = getError(R.string.error_403);
//                break;
//            case 404:
//                message = getError(R.string.error_404);
//                break;
//            case 405:
//                message = getError(R.string.error_405);
//                break;
//            case 406:
//                message = getError(R.string.error_406);
//                break;
//            case 407:
//                message = getError(R.string.error_407);
//                break;
//            case 500:
//                message = getError(R.string.error_500);
//                break;
//            case 501:
//                message = getError(R.string.error_501);
//                break;
//            case 502:
//                message = getError(R.string.error_502);
//                break;
//            case 503:
//                message = getError(R.string.error_503);
//                break;
//            case 504:
//                message = getError(R.string.error_504);
//                break;
//            case 505:
//                message = getError(R.string.error_505);
//                break;
//            case 600:
//                message = getError(R.string.error_600);
//                break;
//            case 601:
//                message = getError(R.string.error_601);
//                break;
//            case 602:
//                message = getError(R.string.error_602);
//                break;
//            case 603:
//                message = getError(R.string.error_603);
//                break;
//            case 604:
//                message = getError(R.string.error_604);
//                break;
//            case 605:
//                message = getError(R.string.error_605);
//                break;
//            case 606:
//                message = getError(R.string.error_606);
//                break;
//            case 607:
//                message = getError(R.string.error_607);
//                break;
//            case 608:
//                message = getError(R.string.error_608);
//                break;
//            case 700:
//                message = getError(R.string.error_700);
//                break;
//            case 701:
//                message = getError(R.string.error_701);
//                break;
//            case 702:
//                message = getError(R.string.error_702);
//                break;
//            default:
//                message = getError(R.string.error_unknown);
        }
        return message;
    }

//    private static String getError(int resId){
//        return MyApplication.context.getString(resId);
//    }

}

