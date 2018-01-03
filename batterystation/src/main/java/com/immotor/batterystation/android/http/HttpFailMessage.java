package com.immotor.batterystation.android.http;

import android.content.Context;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.app.TypeStatus;

/**
 * Created by jm on 2017/9/1 0001.
 */

public class HttpFailMessage {
    /*
    * {

    APISuccessCode  = 200,      // successful
    APISuccessResultCode        = 600,      // successful

    APIErrorCodeSIDError        = 204, //sID格式不合法，只能包含数字和字母
    APIErrorCodeTooLong         = 208,  //昵称长度不超过20
    APIErrorCodeAlreadyBind     = 601,  //该车已经被绑定了

    APIErrorCodeOccupied        = 603,  // 已被占用，不能操作
    APIErrorCodeInProgress      = 604,  // 请求处理中
    APIErrorCodeNoExist         = 605,  // 不存在
    APIErrorCodeOrderError      = 606,  // 预定电池处理失败 || 没有该类型的车
    APIErrorCodeSendFailed      = 607,  // 发送验证码失败 || 您已经绑定了该车
    APIErrorCodeHourLimitation  = 608,  // 1小时内同一手机号发送次数超过限制 || 该车已经解绑了
    APIErrorCodeDayLimitation   = 609,  // 1天内同一手机号发送次数超过限制

    APIErrorCodeInvalidVerification = 615,  // 无效的验证码or已过期
    APIErrorCodeInvalidToken    = 616,  // 无效的Token

    APIErrorCodePaymentFailed   = 617,  // 支付失败
    APIErrorCodePaymentInprogress   = 618, // 支付中
    APIErrorCodeUnPayment       = 619,  // 未支付
    APIErrorCodePaymentClosed   = 620,  // 支付已关闭
    APIErrorCodeRefundInprogress    = 621,  // 退款处理中
    APIErrorCodeRefundNotSure   = 622,  // 未确定，需要重新发起退款申请
    APIErrorCodeRefundException = 623,  // 退款异常
    APIErrorCodeRefundSuccess   = 624,  // 退款成功
    APIErrorCodeRefundClose     = 625,  // 退款关闭

    APIErrorCodeNoMoney         = 626,  // 钱包余额不足
    APIErrorCodeNoBattery       = 627,  // 没电池，请先购买电池
    APIErrorCodeNoPackage       = 628,  // 没套餐，请先购买套餐
    APIErrorCodePackgeNotEnough = 629,  // 套餐内次数不够

    APIErrorCodeReservationExist    = 630,  // 您已经有订单了
RELEASE_EXIST(631, "套餐尚未用完"),
    TIMES_LESS(632, "升级套餐必须选择次数更多的套餐"),
    COUPON_EXIST(633, "有优惠未领取"),
    REFUND_LIMIT(634, "每颗电池每月只允许退一次押金"),
    DEPOSIT_LIMIT(635, "每个用户只允许租2颗电池"),
    BACK_BATTERY(636, "请先还电池");
    APIReservationNotifyCode    = 6000,     // 预订单完成的通知

    APIErrorUnknownCode = 9000,
}

BATTERY_LESS(627, "请先购买电池"),
    PACKAGE_NULL(628, "请先购买套餐"),
    PACKAGE_LESS(629, "套餐内次数不足"),
    EXIST_ALREADY(630, "您已经有订单了"),
    RELEASE_EXIST(631, "套餐尚未用完"),
    TIMES_LESS(632, "升级套餐必须选择次数更多的套餐"),
    COUPON_EXIST(633, "有优惠未领取"),
    REFUND_LIMIT(634, "每颗电池每月只允许退一次押金"),
    DEPOSIT_LIMIT(635, "每个用户只允许租2颗电池"),
    BACK_BATTERY(636, "请先还电池");
    * */

    public static void showfailMessage(Context context, String type, Throwable e) {
        String message = "";
        int code = 0;
        if (e == null) {
            Toast.makeText(context, R.string.connection_timeout, Toast.LENGTH_SHORT).show();
            return;
        }

        if (e instanceof ApiException) {
            code = ((ApiException) e).getCode();
        } else {
            return;
        }
        switch (code) {
            case 204:
                message = context.getString(R.string.sid_not_legal);
                break;
            case 208:
                message = context.getString(R.string.nickname_more_long);
                break;
            case 601:
                message =context.getString(R.string.car_have_binded);
                break;
            case 603:
                message = context.getString(R.string.cont_use);
                break;
            case 604:
                message = context.getString(R.string.http_connect_now);
                break;
            case 605:
                if (type!=null && Integer.parseInt(type) == TypeStatus.ORDER_QUERY_TYPE) {
                    message = context.getString(R.string.no_battery_use);
                } else {
                    message = context.getString(R.string.station_http_timeout);
                }
                break;
            case 606:
                message = context.getString(R.string.system_deal_fail);
                break;
            case 607:
                message = context.getString(R.string.send_authcode_fail);
                break;
            case 608:
                message = context.getString(R.string.send_out_limt_hour);
                break;
            case 609:
                message = context.getString(R.string.send_out_limt_day);
                break;
            case 615:
                message = context.getString(R.string.authcode_outtime_code);
                break;
            case 616:
                message =  context.getString(R.string.not_used_token);
                break;
            case 617:
                message = context.getString(R.string.pay_fail);
                break;
            case 618:
                message = context.getString(R.string.pay_connect_now);
                break;
            case 620:
                message = context.getString(R.string.pay_closed);
                break;
            case 621:
                message = context.getString(R.string.refund_deal_now);
                break;
            case 622:
                message = context.getString(R.string.not_sue_resend_refund);
                break;
            case 623:
                message = context.getString(R.string.refund_error);
                break;
            case 624:
                message = context.getString(R.string.refund_sucess);
                break;
            case 625:
                message = context.getString(R.string.refund_closed);
                break;
            case 626:
                message = context.getString(R.string.wallet_not_enough);
                break;
            case 627:
                message = context.getString(R.string.not_have_use_batttery);
                break;
            case 628:
                message = context.getString(R.string.not_have_combo);
                break;
            case 629:
                message = context.getString(R.string.not_enough_combo);
                break;
            case 630:
                message = context.getString(R.string.have_ordered);
                break;
            case 631:
                message = context.getString(R.string.combo_not_used_finish);
                break;
            case 632:
                message = context.getString(R.string.more_times_combo_select);
                break;
            case 633:
                message = context.getString(R.string.have_favorable_to_use);
                break;
            case 634:
                message = context.getString(R.string.one_time_to_refund_month);
                break;
            case 635:
                message = context.getString(R.string.two_battery_limt);
                break;
            case 636:
                message = context.getString(R.string.need_return_battery);
                break;
            case 6000:
                message = context.getString(R.string.ordered_finished_noticed);
                break;
            default:
                message = e.getMessage();
                break;

        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
