package com.immotor.batterystation.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.ui.views.SecurityCodeView;


/**
 * Created by Ashion on 2017/5/12.
 */

public class InputCodeDialog extends Dialog implements SecurityCodeView.InputCompleteListener {
    public interface IInputCodeReceiver {
        void inputCode(String code);
    }

    public interface resetCode {
        void reset();
    }

    private resetCode reset = null;
    private Bitmap bmp;
    private IInputCodeReceiver receiver = null;
    private Context context;
    private ImageView pic;
    private SecurityCodeView editText;

    /* public InputCodeDialog(@NonNull Context context,  int themeResId) {
         super(context, themeResId);
     }*/
    public InputCodeDialog(@NonNull Context context, int themeResId, Bitmap bmp, IInputCodeReceiver receiver, resetCode reset) {
        super(context, themeResId);
        this.context = context;
        this.bmp = bmp;
        this.receiver = receiver;
        this.reset = reset;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input_code, null);
        setContentView(view);
        setCanceledOnTouchOutside(false);
        // ((ImageView)findViewById(R.id.pic)).setImageBitmap(bmp);
        pic = (ImageView) findViewById(R.id.pic);
        pic.setImageBitmap(bmp);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);//设置对话框位置
       /* findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(receiver!=null){
                    receiver.inputCode(((EditText)findViewById(R.id.value)).getText().toString());
                }
                dismiss();
            }
        });*/
        editText = (SecurityCodeView) findViewById(R.id.scv_edittext);
        editText.setInputCompleteListener(this);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                dismiss();
            }
        });
        findViewById(R.id.reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (receiver != null) {
                    reset.reset();
                }
            }
        });

    }

    private void showSolftInput() {
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                InputMethodManager imm = (InputMethodManager)
                editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            }
        }, 100);
    }

    @Override
    public void inputComplete() {
        if (receiver != null) {
            receiver.inputCode(editText.getEditContent());
            editText.clearEditText();
            hideSoftKeyboard();
            dismiss();
        }

    }

    /**
     * 隐藏键盘
     */
    public void hideSoftKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    public void deleteContent(boolean isDelete) {
    }

    public void reSetBitMap(Bitmap bitmap) {
        this.bmp = bitmap;
        pic.setImageBitmap(bmp);
    }


    @Override
    public void show() {
        super.show();
        showSolftInput();
    }
}
