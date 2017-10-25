package com.wangpos.soundrecordview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by qiyue on 2017/5/2.
 */

public class SoundRecordView extends View {

    private float mWidth;

    private float mHeight;
    /**
     * 竖线
     */
    private Paint mLinePaint;

    /**
     * 横线
     */
    private Paint mXLinePaint;

    private Paint timePaint;

    //private int lineColor = 0xff6da8ff;
    private int lineColor = 0xff6da8ff;

    private int XlineColor = 0xffe6e6e6;

    private float lineSize = 0.5f;

//    private int waveColor = 0xff6da8ff;

    private int waveColor = 0xffffffff;
//    private int waveColor = 0xffffffff;

    private float waveWidth = 1.5f;

    private float space = 0f;

    /**
     * line 左边的距离
     */
    private float x2 = 0;
    /**
     * circle 的左边距离
     */
    private float xx2 = 0;


    public ArrayList<Integer> getWaveLines() {
        return waveLines;
    }

    public void setWaveLines(ArrayList<Integer> waveLines2) {
        this.waveLines2 = waveLines2;
        this.startIndex = waveLines2.size();
    }

    /**
     * 线基本属性就是高度
     */
    private ArrayList<Integer> waveLines2 = new ArrayList<>();
    /**
     * 第二段波纹
     */
    private ArrayList<Integer> waveLines = new ArrayList<>();

    /**
     * 第一段波纹
     */
    private List<Integer> startWaveLines = new ArrayList<>();

    private List<Integer> topMarks = new ArrayList<>();

    private int markWidth;

    private Paint mMarkPaint;

    private float markSize;

    private int bottomLine;
    /**
     * 波纹画笔
     */
    private Paint mWavePaint;

    private float halfW;

    private float halfH;

    private Handler mHandler;

    private Paint mOutPaint;

    private Paint mInnerPaint;
    /**
     * 内圆半径
     */
    private float mInnerRadius;

    /**
     * 外圆半径
     */
    private float mOutRadius;

    /**
     * 当前分贝值
     */
    private int mDecibel;

    private float timeX = 0;

    private float timeHeight = 10;

    int waveIndex = 0;
    int startIndex = 0;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {


            startIndex = startIndex-1;
            if (startIndex>0) {
                ss = System.currentTimeMillis();
//                Log.i("qiyue","start^^^^^^^="+System.currentTimeMillis());
                int value = waveLines2.get(startIndex);
//                Log.i("waveLines2=" + waveLines2.size());


                if (startIndex-2>0) {
                    if (value == 0 && waveLines2.get(startIndex - 1) == 0 && waveLines2.get(startIndex - 2) == 0) {

                        if ((waveLines.size() - addZeroCount * 3) % 5 == 0) {//一次性添加
                            long start = System.currentTimeMillis();
                            waveLines.add(0, 0);
                            startWaveLines.add(0);
                            waveLines.add(0, 0);
                            startWaveLines.add(0);
                            waveLines.add(0, 0);
                            startWaveLines.add(0);
                            addZeroCount++;
                            long end = System.currentTimeMillis();

//                            Log.i("qiyue",">>>>>>>>>>>>>>>添加00000=");
//                    waveIndex = waveIndex +3;
                            startIndex = startIndex - 2;
                        }else{

                            waveLines.add(0, value);
                            startWaveLines.add(value);

                        }
                    }else{

                        waveLines.add(0, value);
                        startWaveLines.add(value);
                    }
                }else{
                    waveLines.add(0, value);
                    startWaveLines.add(value);
                }


                waveIndex++;
//                Log.i("qiyue","end1^^^^^^^^"+(System.currentTimeMillis()-ss));
                invalidate();
                if(!isPlayFinish) {
                    mHandler.postDelayed(runnable, 1);
                }
//                Log.i("sound","size="+waveLines.size());
            }else{
                invalidate();
            }

        }
    };

    private Runnable topMarkRunnable = new Runnable() {
        @Override
        public void run() {
            if (topMarks.size()%4 ==0) {
                topMarks.add(0,(int) dip2px(getContext(), 24));
            }else{
                topMarks.add(0,(int) dip2px(getContext(), 8));
            }
            mHandler.postDelayed(topMarkRunnable, 200);
        }
    };

    public SoundRecordView(Context context) {
        this(context, null);
    }


    public SoundRecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoundRecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(Color.BLACK);
//        setBackgroundColor(0xffF4F5F9);
        this.lineSize = dip2px(getContext(),0.5f);

        mLinePaint = new Paint();
        mLinePaint.setColor(lineColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(lineSize);


        mXLinePaint = new Paint();
        mXLinePaint.setColor(XlineColor);
        mXLinePaint.setAntiAlias(true);
        mXLinePaint.setStrokeWidth(lineSize);

        mWavePaint = new Paint();
        mWavePaint.setColor(waveColor);
        mWavePaint.setAntiAlias(true);
        mWavePaint.setStrokeWidth(waveWidth);
        mWavePaint.setStrokeCap(Paint.Cap.ROUND);
        mWavePaint.setTextSize(dip2px(getContext(),7.3f));
        mWavePaint.setTextAlign(Paint.Align.RIGHT);

        mHandler = new Handler();

        this.mInnerPaint = new Paint();
        this.mInnerPaint.setStyle(Paint.Style.FILL);
        this.mInnerPaint.setColor(Color.WHITE);
        this.mInnerPaint.setAntiAlias(true);

        this.mOutPaint = new Paint();
        this.mOutPaint.setStyle(Paint.Style.FILL);
        this.mOutPaint.setColor(0xff6DA8FF);
        this.mOutPaint.setAntiAlias(true);

        this.timePaint = new Paint();
        this.timePaint.setStyle(Paint.Style.STROKE);
        this.timePaint.setStrokeWidth(1);
        this.timePaint.setAntiAlias(true);
        this.timePaint.setColor(0xff333333);

     //   this.markSize = dip2px(getContext(),0.2f);

        this.mMarkPaint = new Paint();
        this.mMarkPaint.setStrokeWidth(0.5f);
        this.mMarkPaint.setAntiAlias(true);
        this.mMarkPaint.setColor(0xffdddddd);


        this.mInnerRadius = (int)dip2px(getContext(),2);

        this.mOutRadius = (int)dip2px(getContext(),4);

        this.xx2 = 0;

        this.ddx = (int)dip2px(getContext(),13f);
        this.rightMargin = (int)dip2px(getContext(),8);
        this.adapterSize = (int)dip2px(getContext(),8);

        topMarks.add((int)dip2px(getContext(),24));
        this.markWidth = (int)dip2px(getContext(),12.5f);

        this.bottomLine = (int)dip2px(getContext(),30);

        for (int i=0;i<5;i++) {
            topMarks.add((int) dip2px(getContext(), 8));
            topMarks.add((int) dip2px(getContext(), 8));
            topMarks.add((int) dip2px(getContext(), 8));
            topMarks.add((int) dip2px(getContext(), 24));
        }
        topMarks.add((int) dip2px(getContext(), 8));
        topMarks.add((int) dip2px(getContext(), 8));
        topMarks.add((int) dip2px(getContext(), 8));
        topMarks.add((int) dip2px(getContext(), 24));

    }

    private int ddx = 0;

    private int rightMargin = 0;

    private int adapterSize = 0;

    private int index = 0;

    int startSize = 0;

    private int addZeroCount = 0;

    private long ss = 0;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 绘制线边矩形
         */
        mMarkPaint.setColor(0xffffffff);
        canvas.drawRect(new RectF(0,mHeight-mOutRadius,mWidth,mHeight),mMarkPaint);



        float rightMarkHeight = mHeight/2+bottomLine/2;

//        canvas.drawText("-0",mWidth-rightMargin,mHeight/2-ddx*7+adapterSize,mWavePaint);
        canvas.drawText("-1",mWidth-rightMargin,rightMarkHeight-ddx*6+adapterSize,mWavePaint);
        canvas.drawText("-2",mWidth-rightMargin,rightMarkHeight-ddx*5+adapterSize,mWavePaint);
        canvas.drawText("-3",mWidth-rightMargin,rightMarkHeight-ddx*4+adapterSize,mWavePaint);
        canvas.drawText("-5",mWidth-rightMargin,rightMarkHeight-ddx*3+adapterSize,mWavePaint);
        canvas.drawText("-7",mWidth-rightMargin,rightMarkHeight-ddx*2+adapterSize,mWavePaint);
        canvas.drawText("-10",mWidth-rightMargin,rightMarkHeight-ddx*1+adapterSize,mWavePaint);

//        canvas.drawText("-0",mWidth-rightMargin,mHeight/2+ddx*7,mWavePaint);
        canvas.drawText("-1",mWidth-rightMargin,rightMarkHeight+ddx*6,mWavePaint);
        canvas.drawText("-2",mWidth-rightMargin,rightMarkHeight+ddx*5,mWavePaint);
        canvas.drawText("-3",mWidth-rightMargin,rightMarkHeight+ddx*4,mWavePaint);
        canvas.drawText("-5",mWidth-rightMargin,rightMarkHeight+ddx*3,mWavePaint);
        canvas.drawText("-7",mWidth-rightMargin,rightMarkHeight+ddx*2,mWavePaint);
        canvas.drawText("-10",mWidth-rightMargin,rightMarkHeight+ddx*1,mWavePaint);


        /**
         * 绘制横线
         */
        canvas.drawLine(0f, mHeight / 2+bottomLine/2, mWidth, mHeight / 2+bottomLine/2, mXLinePaint);

        int size = waveLines.size();
//        int startSize = startWaveLines.size();

        Log.i("sound","size="+size);
        Log.i("sound","startSize="+size);
        if (this.x2 >= this.halfW) {
            this.x2 = this.halfW;
            if (startSize ==0){
                startSize = startWaveLines.size();
            }
            drawTopMark2(canvas);
            startTopMarksScroll();
            /**
             *  绘制竖线
             */
            canvas.drawLine(x2, bottomLine, x2, mHeight, mLinePaint);
            /**
             * 上边圆
             */
            canvas.drawCircle(halfW,bottomLine,mOutRadius,mOutPaint);
            canvas.drawCircle(halfW,bottomLine,mInnerRadius,mInnerPaint);
            /**
             * 下边圆
             */
            canvas.drawCircle(halfW,mHeight-mOutRadius,mOutRadius,mOutPaint);
            canvas.drawCircle(halfW,mHeight-mOutRadius,mInnerRadius,mInnerPaint);
            long start = System.currentTimeMillis();

            if (size>startSize){// 0 1 2 3
                index =  size- startSize;
//                Log.i("qiyue","index="+index);
            }

            for (int i=0; i < startSize; i++) {
                float x = (halfW -  (i * (waveWidth + space)));
                int y = waveLines.get(i);
                canvas.drawLine(x, halfH, x, halfH - y, mWavePaint);

                canvas.drawLine(x, halfH, x, halfH + y, mWavePaint);
            }


            long end = System.currentTimeMillis();
//            Log.i("qiyue","draw length="+(size-index)+"endSize="+size+"time-three="+(end-start));
            startWaveLines.clear();

        } else {
            drawTopMark(canvas);
            canvas.drawLine(x2, bottomLine, x2, mHeight, mLinePaint);

            for (int i = 0; i < size; i++) {
                float x = (i * (waveWidth + space));
                int y = startWaveLines.get(i);
                canvas.drawLine(x, halfH, x, halfH - y, mWavePaint);

                canvas.drawLine(x, halfH, x, halfH + y, mWavePaint);
            }
            /**
             * 上圆
             */
            canvas.drawCircle(xx2,bottomLine,mOutRadius,mOutPaint);
            canvas.drawCircle(xx2,bottomLine,mInnerRadius,mInnerPaint);
            /**
             * 下圆
             */
            canvas.drawCircle(xx2,mHeight-mOutRadius,mOutRadius,mOutPaint);
            canvas.drawCircle(xx2,mHeight-mOutRadius,mInnerRadius,mInnerPaint);
            this.x2 = (size) * (waveWidth + space);
            this.xx2 = (size) * (waveWidth + space);
//            Log.i("qiyue","size="+size);
        }

//        Log.i("qiyue","*******************************endtime"+(System.currentTimeMillis()-ss));

    }

    private boolean markismark = false;
    private void startTopMarksScroll() {
        if (!markismark) {
            markismark = true;
            mHandler.postDelayed(topMarkRunnable, 200);
        }
    }

    /**
     * 绘制第二段mark
     * @param canvas
     */
    private void drawTopMark(Canvas canvas) {
        mMarkPaint.setColor(0xffffffff);
        canvas.drawRect(new RectF(0,0,mWidth,bottomLine),mMarkPaint);
        canvas.drawLine(0,bottomLine,mWidth,bottomLine,mMarkPaint);
        mMarkPaint.setColor(0xffdddddd);
        for (int i=0;i<topMarks.size();i++) {
            canvas.drawLine(i*markWidth,bottomLine, i*markWidth,bottomLine-topMarks.get(i),mMarkPaint);

        }
    }

    /**
     * 绘制第二段mark
     * @param canvas
     */
    private void drawTopMark2(Canvas canvas) {
        mMarkPaint.setColor(0xffffffff);
        canvas.drawRect(new RectF(0,0,mWidth,bottomLine),mMarkPaint);
        canvas.drawLine(0,bottomLine,mWidth,bottomLine,mMarkPaint);
        mMarkPaint.setColor(0xffdddddd);
        for (int i=0;i<topMarks.size();i++) {

            float x = mWidth - i * (markWidth);

            canvas.drawLine(x,bottomLine, x,bottomLine-topMarks.get(i),mMarkPaint);

        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        this.halfW = mWidth / 2;
        this.halfH = mHeight / 2+bottomLine/2;
        //   start();
    }

    /**
     * 随机开始波纹
     */
    public void start(){
        ss = System.currentTimeMillis();
        Log.i("sound","start-size="+waveLines.size());
        this.waveIndex = 0;
        this.startIndex = waveLines2.size();
        this.x2 = 0;
        this.xx2 = 0;
        waveLines.clear();
        startWaveLines.clear();
        reset();
        this.mHandler.post(runnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        waveLines.clear();
        startWaveLines.clear();
        reset();
        stop();
    }

    public void reset(){
        Log.i("sound","reset-size="+waveLines.size());
        markismark = false;
        topMarks.clear();
        topMarks.add((int)dip2px(getContext(),24));

        for (int i=0;i<6;i++) {
            topMarks.add((int) dip2px(getContext(), 8));
            topMarks.add((int) dip2px(getContext(), 8));
            topMarks.add((int) dip2px(getContext(), 8));
            topMarks.add((int) dip2px(getContext(), 24));
        }
    }

    public void stop(){
        markismark = false;
        this.mHandler.removeCallbacksAndMessages(null);
    }




    /**
     * 设置分贝
     * @param decibel
     */
    public void setDecibel(double decibel) {
//        this.mDecibel = (int)decibel;

        this.mDecibel = (int)getValue(decibel);
        ss = System.currentTimeMillis();
        int max=20;
        int min=3;
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;

        if ((waveLines.size()- addZeroCount*3)%s == 0){//一次性添加
            long start = System.currentTimeMillis();
            waveLines.add(0, 0);
            startWaveLines.add(0);
            waveLines.add(0, 0);
            startWaveLines.add(0);
            waveLines.add(0, 0);
            startWaveLines.add(0);
            addZeroCount ++;
            long end = System.currentTimeMillis();

//            Log.i("qiyue","time_one="+(start-end));

        }
        long start = System.currentTimeMillis();
        waveLines.add(0, this.mDecibel);
        startWaveLines.add(this.mDecibel);
        long end = System.currentTimeMillis();
//        Log.i("qiyue","time_two="+(start-end)+"====size="+waveLines.size());

        invalidate();
    }


    /**
     * 波纹算法
     * @param decibel
     * @return
     */
    public double getValue(double decibel){
        double y;
        Log.i("deb","x>>>>>>="+decibel);
        if (decibel<=-15){
            y = new Random().nextInt(2);

        }else {
            decibel = (decibel) / 10;
            y = 3.5 * decibel * decibel - 32;
        }

        if (y < 1) {
            y = 1;
        }
        Log.i("deb","y="+y);
        /**
         * 这个高度换机器要做适配
         */
        if (y>dip2px(this.getContext(),130)){
            y = dip2px(this.getContext(),130);
        }
        return y;
    }

    public void destroy() {
        Log.i("sound","reset-size="+waveLines.size());
        this.x2 = 0;
        this.xx2 = 0;
        waveLines.clear();
        startWaveLines.clear();
        reset();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return  (dpValue * scale + 0.5f);
    }

    public void setPlayFinish(boolean playFinish) {
        isPlayFinish = playFinish;
    }

    private boolean isPlayFinish = false;

    public void continuePlay() {
        if (isPlayFinish){
            this.startIndex = waveLines2.size();
            this.waveIndex = 0;
            this.x2 = 0;
            this.xx2 = 0;
            waveLines.clear();
            startWaveLines.clear();
            reset();
            isPlayFinish = false;
        }
        this.mHandler.post(runnable);
    }

    public void pause() {
        markismark = false;
        this.mHandler.removeCallbacksAndMessages(null);
    }
}
