package com.immotor.batterystation.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.http.HttpMethods;
import com.immotor.batterystation.android.http.subscriber.ProgressSubscriber;
import com.immotor.batterystation.android.http.subscriber.SubscriberOnNextListener;
import com.immotor.batterystation.android.ui.base.BaseActivity;
import com.immotor.batterystation.android.util.DateTimeUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by Ashion on 2017/5/25.
 */

public class ModifyProfileActivity extends BaseActivity {
    public static final String KEY_MODIFY_PROFILE = "modify_profile";
    public static final int TYPE_NAME = 1;
    public static final int TYPE_GENDER = 2;
    public static final int TYPE_BIRTHDAY = 3;
    private int modifyType;

    @Bind(R.id.edit_name)
    EditText nameEdit;

    @Bind(R.id.group_gender)
    RadioGroup genderGroup;
    @Bind(R.id.select_male)
    RadioButton selectMale;
    @Bind(R.id.select_female)
    RadioButton selectFemale;

    @Bind(R.id.date_picker)
    DatePicker datePicker;

    private String newValueString;
    private int newValueInt;
    private long newValueLong;
    private TextView mTittle;
    private TextView mSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_profile);
    }

    @Override
    public void initUIView() {
        modifyType = getIntent().getIntExtra(KEY_MODIFY_PROFILE, TYPE_NAME);
        mTittle = (TextView) findViewById(R.id.home_actionbar_text);
        ImageView mImg = (ImageView) findViewById(R.id.home_actionbar_menu);
        mImg.setImageDrawable(getDrawable(R.mipmap.nav_back_icon_white));
        mSave = (TextView) findViewById(R.id.home_actionbar_right);
        mSave.setText(R.string.save);
        mImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
// 保存的控件透明了，事件还存在

        if(modifyType==TYPE_NAME) {
            mTittle.setText(R.string.alter_nickname);
            mSave.setVisibility(View.VISIBLE);
            nameEdit.setVisibility(View.VISIBLE);
            genderGroup.setVisibility(View.GONE);
            datePicker.setVisibility(View.GONE);
            String name = mPreferences.getUserName();
            if(TextUtils.isEmpty(name)){
                nameEdit.setHint(R.string.import_nickname);
            }else{
                nameEdit.setText(name);
            }
        }else if(modifyType==TYPE_GENDER){
            mSave.setVisibility(View.VISIBLE);
            mTittle.setText(R.string.alter_sex);
            nameEdit.setVisibility(View.GONE);
            genderGroup.setVisibility(View.VISIBLE);
            datePicker.setVisibility(View.GONE);
            int gender = mPreferences.getSex();
            if(gender==2){
                selectFemale.setChecked(true);
            }else{
                selectMale.setChecked(true);
            }

        }else if(modifyType==TYPE_BIRTHDAY){
            mSave.setVisibility(View.VISIBLE);
            mTittle.setText(R.string.alter_birthday);
            nameEdit.setVisibility(View.GONE);
            genderGroup.setVisibility(View.GONE);
            datePicker.setVisibility(View.VISIBLE);
            datePicker.setMaxDate(System.currentTimeMillis());
        }
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modifyType==TYPE_NAME) {
                    String name = nameEdit.getText().toString();
                    if(name.length() > 0) {
                        httpUpdateProfile("name", name);
                        newValueString = name;
                    }else{
                        showSnackbar(getString(R.string.nickname_cont_null));
                    }
                }else if(modifyType==TYPE_GENDER){
                    if(selectMale.isChecked()){
                        httpUpdateProfile("sex", 1);
                        newValueInt = 1;
                    }else{
                        httpUpdateProfile("sex", 2);
                        newValueInt = 2;
                    }
                }else if(modifyType==TYPE_BIRTHDAY){
                    int year = datePicker.getYear();
                    int month = datePicker.getMonth()+1;
                    int day = datePicker.getDayOfMonth();
                    long mills = DateTimeUtil.stringToLong(year+"-" + (month<10?"0"+month:month) +"-"+(day<10?"0"+day:day), "yyyy-MM-dd");
                    httpUpdateProfile("birthday", mills);
                    newValueLong = mills;
                }
            }
        });
    }

    private void httpUpdateProfile(String key, Object value){
        if (!isNetworkAvaliable()){
            return;
        }

        SubscriberOnNextListener subscriberOnNextListener = new SubscriberOnNextListener<Object>() {
            @Override
            public void onError(Throwable e) {
                showSnackbar(getString(R.string.setting_fail));
            }

            @Override
            public void onNext(Object object) {
                if(modifyType==TYPE_NAME){
                    mPreferences.setUserName(newValueString);
                }else if(modifyType==TYPE_GENDER){
                    mPreferences.setSex(newValueInt);
                }else if(modifyType==TYPE_BIRTHDAY){
                    mPreferences.setBirthday(newValueLong);
                }
                finish();
            }
        };
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        HttpMethods.getInstance().updateUser(new ProgressSubscriber(subscriberOnNextListener, this, null), mPreferences.getToken(), mPreferences.getUserID()+"", map);
    }

}
