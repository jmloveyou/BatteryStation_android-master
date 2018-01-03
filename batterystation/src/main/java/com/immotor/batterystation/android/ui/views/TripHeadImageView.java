package com.immotor.batterystation.android.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import java.util.Random;

public class TripHeadImageView extends AppCompatImageView {
    public TripHeadImageView(Context context){
        super(context);
    }
    public TripHeadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TripHeadImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
    }
    private final int PIC_WIDTH = 1080;   //图片实际宽
    private final int PIC_HEIGHT = 636;   //图片实际高度


    //点数据，x1,y1,x2,y2,x3,y3...的存储格式
    private int[][] datas = new int[][]{
            new int[]{
                    358, 3,
                    299,171,
                    146,337,
                    565,575,
                    751,516,
                    752,487,
                    861,532,
                    1077,462
            },
            new int[]{
                    2,388,
                    92,395,
                    298,168,
                    520,133,
                    622,179,
                    1080,141

            },
            new int[]{
                    826,0,
                    813,87,
                    526,98,
                    250,133,
                    298,174,
                    0,490
            },
            new int[]{
                    0,388,
                    89,395,
                    146,338,
                    383,469,
                    465,275,
                    599,378,
                    842,314,
                    807,163,
                    1080,141
            },
            new int[]{
                    1080,464,
                    862,530,
                    623,178,
                    520,130,
                    299,174,
                    186,293,
                    97,269,
                    235,0
            }
    };
    private boolean isStart = false;  //是否开始动画
    private float scaleW = 1;   //宽缩放比例，实际屏幕显示像素与PIC_WIDTH比
    private float scaleH = 1;   //高缩放比例，实际屏幕显示像素与PIC_HEIGHT比
    private long startTime = 0;  //动画开始时间
    private int index = 0;       //动画选择数据索引
    private int pointCount = 0;  //数据中的点数
    private int[] distance;      //前面n段的距离，distance[2]表示前面3段的距离
    private Paint paint = new Paint();


    /**
     * 开始动画
     */
    public void start(){
        if(!isStart) {
            isStart = true;
            startTime = System.currentTimeMillis();
            index = new Random().nextInt(datas.length);
            pointCount = datas[index].length/2;
            distance = new int[pointCount - 1];
            //计算每段距离
            for(int i = 0; i < distance.length;i++){
                distance[i] = (int)Math.sqrt(Math.pow(datas[index][i*2+2] - datas[index][i*2],2) + Math.pow(datas[index][i*2+3] - datas[index][i*2+1],2));
            }
            //叠加前面距离，算出从起点到当前位置的距离
            for(int i = 1; i < distance.length;i++){
                distance[i] += distance[i-1];
            }
            paint.setColor(Color.RED);
            paint.setStrokeWidth(3);
            postInvalidate();
        }

    }

    /**
     * 停止动画
     */
    public void stop(){
        isStart = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //算缩放比例
        scaleW = 1.0f*getMeasuredWidth()/PIC_WIDTH;
        scaleH = 1.0f*getMeasuredHeight()/PIC_HEIGHT;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isStart){
            long cur = System.currentTimeMillis();
            int hasGone =(int) ((cur - startTime)*0.8);  //已经走过的距离
            int atLine = distance.length-2;   //当前所在第几段路上，从倒数第2段算起，最后一段不参与

            //算出已经完全走完的
            while(atLine>=0 && hasGone < distance[atLine]){
                atLine--;
            }

            for(int i = 0; i <= atLine;i++){
                //绘制前面的线和点

                canvas.drawLine(datas[index][2*i]*scaleW,datas[index][2*i+1]*scaleH,datas[index][2*i+2]*scaleW,datas[index][2*i+3]*scaleH,paint);
                canvas.drawCircle(datas[index][2*i+2]*scaleW,datas[index][2*i+3]*scaleH,20, paint);
            }
            //还没走完的
            atLine++;
            float goneThisLine = (hasGone - (atLine>0?distance[atLine - 1]:0))*1f/(distance[atLine]-(atLine>0?distance[atLine-1]:0)); //此段已经走完的比例
            canvas.drawLine(datas[index][2*atLine]*scaleW,datas[index][2*atLine+1]*scaleH,
                    (datas[index][2*atLine] + (datas[index][2*atLine+2] - datas[index][2*atLine])*goneThisLine)*scaleW,
                    (datas[index][2*atLine+1]+ (datas[index][2*atLine+3]-datas[index][2*atLine+1])*goneThisLine)*scaleH,paint);
            if(hasGone <= distance[distance.length-1]){  //是否已经走完
                postInvalidate();
            }else{
                isStart = false;
            }
        }
    }
}