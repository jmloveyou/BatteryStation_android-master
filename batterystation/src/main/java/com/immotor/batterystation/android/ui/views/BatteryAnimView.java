package com.immotor.batterystation.android.ui.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.immotor.batterystation.android.R;
import com.immotor.batterystation.android.util.BitmapUtil;


/**
 * Created by Ashion on 2017/4/5.
 */

public class BatteryAnimView extends View {
    public BatteryAnimView(Context context) {
        this(context, null);
    }

    public BatteryAnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BatteryAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private final int HANDLER_FRESH = 1;
    private Bitmap innerCircle;
    private Paint paint;
    private int radius = 0;
    private int lineLength = 0;
    private int centerX = 0;
    private int centerY = 0;
    private Rect srcRect = new Rect();
    private Rect destRect = new Rect();
    private int percent = 0;
    private int curProgress = 0;
    private void init(){
        innerCircle = BitmapUtil.decodeSampledBitmapFromResource(getResources(), R.mipmap.battery_circle_inner,1);
        srcRect.left = 0;
        srcRect.top = 0;
        srcRect.right = innerCircle.getWidth();
        srcRect.bottom = innerCircle.getHeight();
        paint = new Paint();
        paint.setStrokeWidth(10);
        paint.setColor(getResources().getColor(R.color.black));
        handler = new Handler(){
            @Override
            public void dispatchMessage(Message msg) {
                if(msg.what == HANDLER_FRESH){
                    if(curProgress < percent){
                        curProgress++;
                        postInvalidate();
                        sendEmptyMessageDelayed(HANDLER_FRESH, 20);
                    }
                }
            }
        };
    }

    private Handler handler;

    public void setValue(int percent){
        if(percent>100){
            percent = 100;
        }else if(percent<0){
            percent = 0;
        }
        this.percent = percent;
        curProgress = 0;
//        startAnim();
    }

    public void startAnim(){
        if(!handler.hasMessages(HANDLER_FRESH)){
            curProgress = 0;
            handler.sendEmptyMessage(HANDLER_FRESH);
        }
    }

    public void stopAnim(){
        if(handler.hasMessages(HANDLER_FRESH)){
            handler.removeMessages(HANDLER_FRESH);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerX = getMeasuredWidth()/2;
        centerY = getMeasuredHeight()/2;
        radius = (centerX < centerY)?(centerX*3/4):(centerY*3/4);
        lineLength = radius / 8;
        destRect.left = centerX - radius;
        destRect.right = centerX + radius;
        destRect.top = centerY - radius;
        destRect.bottom = centerY + radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(innerCircle,srcRect,destRect,null);
        canvas.save();
        canvas.translate(centerX,centerY);
        canvas.rotate(60);
        int changeVal = curProgress/2;
        paint.setColor(getResources().getColor(R.color.black));
        for(int i = 0; i < changeVal;i++){
            canvas.drawLine(0, radius,0, radius +lineLength,paint);
            canvas.rotate(7.2f);
        }
        paint.setColor(getResources().getColor(R.color.light_grey2));
        for(int i  = changeVal; i < 50; i++){
            canvas.drawLine(0, radius,0, radius +lineLength,paint);
            canvas.rotate(7.2f);
        }
        canvas.rotate(-60);
        canvas.translate(-centerX, -centerY);
        canvas.restore();
    }
}
