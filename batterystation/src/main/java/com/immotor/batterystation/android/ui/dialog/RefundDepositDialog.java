package com.immotor.batterystation.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.immotor.batterystation.android.R;

/**
 * Created by Ashion on 2017/5/12.
 */

public class RefundDepositDialog extends Dialog {
    public interface IRefundReceiver{
        void refund(String code);
    }
    IRefundReceiver receiver;
    String id;
    public RefundDepositDialog(@NonNull Context context, String id, IRefundReceiver receiver) {
        super(context);
        this.receiver = receiver;
        this.id = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    private void init(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_refund_deposit, null);
        setContentView(view);
        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);//设置对话框位置
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(receiver!=null){
                    receiver.refund(id);
                }
                dismiss();
            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
