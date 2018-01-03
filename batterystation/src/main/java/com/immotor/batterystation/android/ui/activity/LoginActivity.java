package com.immotor.batterystation.android.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.immotor.batterystation.android.MyApplication;
import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.entity.LoginData;
import com.immotor.batterystation.android.entity.MybatteryListBean;
import com.immotor.batterystation.android.entity.UserInfo;
import com.immotor.batterystation.android.http.ApiException;
import com.immotor.batterystation.android.http.HttpFailMessage;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.ui.dialog.InputCodeDialog;
import com.immotor.batterystation.android.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Ashion on 2017/5/8.
 */

public class LoginActivity extends BaseActivity {

    private static final int HANDLER_TIMER_TICK = 1;
    private static final int HANDLER_CAPTCHA_RECEIVE = 2;

    @Bind(R.id.edit_phone)
    EditText editPhone;

    @Bind(R.id.edit_code)
    EditText editCode;

    @Bind(R.id.btn_get_code)
    TextView btnCode;

    @Bind(R.id.btn_login)
    Button btnLogin;

    @Bind(R.id.user_protocol)
    TextView protocol;

    private int timeCount = 0;  //计时时间，倒数60秒

    private String phoneSend;   //申请验证码的手机号

    private String mUUID;  //手机唯一标识符

    private Handler myHandler = new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what){
                case HANDLER_TIMER_TICK:
                    if(timeCount > 0){
                        if(btnCode!=null) {
                            btnCode.setText(timeCount + getString(R.string.second));
                        }
                        timeCount--;
                        sendEmptyMessageDelayed(HANDLER_TIMER_TICK,1000);
                    }else{
                        if(btnCode!=null) {
                            btnCode.setText(R.string.get_auth_code);
                            btnCode.setEnabled(true);
                            editPhone.setEnabled(true);
                        }
                    }
                    break;
                case HANDLER_CAPTCHA_RECEIVE:
                    receiveCapthaData((byte[])msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUUID = mPreferences.getUUID();
        if(TextUtils.isEmpty(mUUID)){
            mUUID = ((MyApplication)getApplication()).calculateUUID();
            mPreferences.setUUID(mUUID);
        }
    }

    @Override
    public void initUIView() {
        btnCode.setEnabled(false);
        btnLogin.setEnabled(false);
        if(!myHandler.hasMessages(HANDLER_TIMER_TICK)) {
            btnCode.setText(R.string.get_auth_code);
            editPhone.setEnabled(true);
            setButtonStatus();
        }
        editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setButtonStatus();
            }
        });
        editCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setButtonStatus();
            }
        });
    }

    @OnClick(R.id.btn_get_code)
    public void ActionGetCode(){
        getCaptcha();
//        getCode();
    }

    @OnClick(R.id.btn_login)
    public void ActionLogin(){
        checkLogin();
    }

    @OnClick(R.id.user_protocol)
    public void ActionUserProtocol(){
        Intent intent = new Intent(this,WebActivity.class);
        intent.putExtra(WebActivity.TYPE_KEY, WebActivity.TYPE_VALUE_USER_PROTOCOL);
        startActivity(intent);
    }

    /**
     * 设置按钮可编辑属性
     */
    private void setButtonStatus(){
        if(editPhone.getText().length()>0){
            if(myHandler.hasMessages(HANDLER_TIMER_TICK)) {
                btnCode.setEnabled(false);
            }else{
                btnCode.setEnabled(true);
            }
//            editCode.setEnabled(true);
            if(editCode.getText().length()==6){
                btnLogin.setEnabled(true);
            }else{
                btnLogin.setEnabled(false);
            }
        }else{
            btnCode.setEnabled(false);
            btnLogin.setEnabled(false);
            editCode.setEnabled(false);
        }
    }

    /**
     * 获取验证码
     */
    private void getCode(){
        if(httpGetCode()){
            btnCode.setEnabled(false);
            editCode.setEnabled(true);
            editPhone.setEnabled(false);
            timeCount = 60;
            myHandler.sendEmptyMessage(HANDLER_TIMER_TICK);
        }else{
            btnCode.setEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean httpGetCode(){
        if (!isNetworkAvaliable()){
            return false;
        }
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                if(myHandler.hasMessages(HANDLER_TIMER_TICK)){
                    myHandler.removeMessages(HANDLER_TIMER_TICK);
                    btnCode.setText(R.string.get_auth_code);
                    editPhone.setEnabled(true);
                    setButtonStatus();
                }
                if(e instanceof ApiException){
                    int  code = ((ApiException)e).getCode();
                    if(code == 615){
                        getCaptcha();
                        showSnackbar(getString(R.string.auth_code_import_error));
                    } else if (code == 607) {
                        showSnackbar(getString(R.string.phonenum_import_error));
                    } else {
                        HttpFailMessage.showfailMessage(LoginActivity.this,null,e);
                    }
                }else {
                    showSnackbar(e.getMessage());
                }
            }

            @Override
            public void onNext(Object object) {

            }
        };
        phoneSend = editPhone.getText().toString();
        Map<String, Object> map = new HashMap<>();
        map.put("captchaCode", mUUID);
        map.put("captchaValue", captcha);
        map.put("phone", phoneSend);
        HttpMethods.getInstance().getLoginCode(new ProgressSubscriber(subscriberOnNextListener, this, null), map);
        return true;
    }

    private String captcha;

    /**
     * @return 获得图片验证码
     */
    private boolean getCaptcha(){
        captcha = null;
        if (!isNetworkAvaliable()){
            return false;
        }
        btnCode.setEnabled(false);
        HttpMethods.getInstance().getCaptcha(new HttpMethods.IHttpStreamReceiver() {
            @Override
            public void receiveData(byte[] data) {
                myHandler.sendMessage(myHandler.obtainMessage(HANDLER_CAPTCHA_RECEIVE, data));
                return;
            }
        }, mUUID);
        return true;
    }

 /*   private void receiveCapthaData(byte[] data){
        if(data!=null){
            InputCodeDialog dlg = new InputCodeDialog(LoginActivity.this,R.style.loading_dialog ,BitmapFactory.decodeByteArray(data, 0, data.length), new InputCodeDialog.IInputCodeReceiver(){
                @Override
                public void inputCode(String code) {
                    captcha = code;
                }
            });
            dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(TextUtils.isEmpty(captcha)) {
                        btnCode.setEnabled(true);
                    }else{
                        getCode();
                    }
                }
            });
            dlg.show();
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT; //设置宽度
            getWindow().setAttributes(lp);
        }else{
            btnCode.setEnabled(true);
        }
    }*/
    InputCodeDialog dlg;
    private void receiveCapthaData(byte[] data){
        if(data!=null){
            if(dlg == null){
                dlg = new InputCodeDialog(LoginActivity.this,R.style.loading_dialog, BitmapFactory.decodeByteArray(data, 0, data.length)
                        , new InputCodeDialog.IInputCodeReceiver(){
                    @Override
                    public void inputCode(String code) {
                        captcha = code;
                    }
                },new InputCodeDialog.resetCode(){
                    @Override
                    public void reset() {
                        getCaptcha();
                    }
                });

                dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if(TextUtils.isEmpty(captcha)) {
                            btnCode.setEnabled(true);
                        }else{
                            getCode();
                        }
                    }
                });
                dlg.show();
            }else{
                if(dlg.isShowing()){
                    dlg.reSetBitMap(BitmapFactory.decodeByteArray(data, 0, data.length));
                }else{
                    dlg.reSetBitMap(BitmapFactory.decodeByteArray(data, 0, data.length));
                    dlg.show();
                }
            }
        }else{
            if (btnCode!=null) {
            btnCode.setEnabled(true);
            }
        }
    }
    private void checkLogin(){
        if (!isNetworkAvaliable()){
            return;
        }

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<LoginData>() {
            @Override
            public void onError(Throwable e) {
                if(e instanceof ApiException){
                    int code = ((ApiException)e).getCode();
                    if(code==615){
                        showSnackbar(getString(R.string.authcode_outtime_code));
                    }else{
                        showSnackbar(e.getMessage());
                    }
                }else {
                    showSnackbar(e.getMessage());
                }
            }

            @Override
            public void onNext(LoginData result) {
                if(result!=null) {
                    mPreferences.setToken(result.getAccess_token());

                    UserInfo user = result.getUserInfo();
                    if(user.getDeposit()==null) {
                        mPreferences.setDeposit(true);
                    }else{
                        mPreferences.setDeposit(false);
                    }
                    mPreferences.setUserID(user.getId());
                    mPreferences.setPhone(user.getPhone());
                    mPreferences.setBirthday(user.getBirthday());
                    mPreferences.setAvatar(user.getAvatar());
                    mPreferences.setSex(user.getSex());
                    mPreferences.setCreateTime(user.getCreateTime());
                    mPreferences.setUserName(user.getName());
                    //deposit 和 amount设置还没做
//                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                    intent.putExtra(KEY_ENTRY_HOMEATY_TYPE,1);
//                    startActivity(intent);
//                    finish();
                    httpMyBatteryRequest();
                }
            }
        };
        HttpMethods.getInstance().login(new ProgressSubscriber(subscriberOnNextListener, this),phoneSend,editCode.getText().toString(),mUUID);
    }
    private void httpMyBatteryRequest() {
        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<MybatteryListBean>() {
            @Override
            public void onError(Throwable e) {
                initHome();
            }

            @Override
            public void onNext(MybatteryListBean bean) {
                if (bean != null && bean.getContent() != null) {
                    if (bean.getContent().size() <= 0) {
                        initFirstGuid();
                    } else {
                        initHome();
                    }
                } else {
                    initHome();
                }

            }
        };
        HttpMethods.getInstance().myBatteryList(new ProgressSubscriber(subscriberOnNextListener, this, null), mPreferences.getToken());
    }

    private void initHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void initFirstGuid() {
        startActivity(new Intent(this, FirstGuideActivity.class));
        finish();
    }
    /*
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSoftButtonsBarHeight() != 0) {
            hideSoftKeyboard();
        } else {

            MyApplication myApplication = null;
            try {
                myApplication = (MyApplication) getApplicationContext();
            } catch (Exception e) {
                LogUtil.e( e.toString() );
                myApplication = null;
            }

            if (null == myApplication) {
                return;
            }
            myApplication.exitAllActivity();
        }
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            MyApplication myApplication = null;
            try {
                myApplication = (MyApplication) getApplicationContext();
            } catch (Exception e) {
                LogUtil.e( e.toString() );
                myApplication = null;
            }

            if (null == myApplication) {
                return true;
            }
            myApplication.exitAllActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
