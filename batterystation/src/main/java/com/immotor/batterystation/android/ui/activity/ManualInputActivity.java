package com.immotor.batterystation.android.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.Toast;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.ui.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Ashion on 2017/7/7.
 */

public class ManualInputActivity extends BaseActivity {

    @Bind(R.id.code)
    EditText inputCodeEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_input);
    }

    @Override
    public void initUIView() {

    }

    @OnClick(R.id.ok)
    public void actionOK(){
        String manua = inputCodeEdit.getText().toString();
        if (manua == null) {
            Toast.makeText(this, R.string.please_import_serial_num, Toast.LENGTH_SHORT).show();
        } else {
        initIntent();
        }
    }

    private void initIntent() {
        Intent intent = new Intent();
        intent.putExtra(QRCodeActivity.MANUAL_INPUT_TARGET, inputCodeEdit.getText().toString());
        setResult(QRCodeActivity.MANUAL_RESULT_CODE, intent);
        finish();
    }

}
