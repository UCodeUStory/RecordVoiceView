package com.chinajesit.recordview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Allen on 2017/9/18.
 */

public class RecordView extends CustomSurfaceView {

    private static final String TAG = "RecordView";

    private Paint mMainPaint;

    //背景色
    private int mBgColor = Color.WHITE;
    //波浪颜色
    private int mLineColor = Color.parseColor("#00CED1");
    //主要线宽
    private int mMainWidth = 7;
    //辅助线宽
    private int mSubWidth = 2;

    //内容区域的Size
    private int mWidth;
    private int mHeight;
    private int mCenterY;

    //四条线的高度系数
    private float[] linesFunc = new float[]{0.5f};

    public RecordView(Context context) {
        this(context, null);
    }

    public RecordView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initPaint();
    }

    /**
     * 自定义属性
     *
     * @param attrs
     */
    private void initAttrs(AttributeSet attrs) {
        // TODO: 2017/9/18
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mMainPaint = new Paint();
        mMainPaint.setColor(mLineColor);
        mMainPaint.setStrokeWidth(mMainWidth);
        mMainPaint.setStyle(Paint.Style.FILL);
        mMainPaint.setAntiAlias(true);
    }

    @Override
    public void drawContent(Canvas canvas, long millisPassed) {
        initDraw(canvas, millisPassed);
        drawLine(canvas);
    }

    //取样点
    private int samplingSize = 2;
    private float[] samplingX;
    private float[] samplingY;
    private float shakeFunc;//衰退系数

    private int volume = 0;//音量
    //保存4条线的Y坐标
    Map<Float, float[]> linesY = new HashMap<>();

    /**
     * 初始化参数以及计算出点的位置
     *
     * @param canvas
     * @param millisPassed
     */
    private void initDraw(Canvas canvas, long millisPassed) {

        //根据时间偏移
        float offset = millisPassed / 100f;
        Log.i("qiyue", "offset=" + offset);
        linesY.clear();

        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();
        mCenterY = mHeight / 2;

        samplingX = new float[samplingSize + 1];

        //点之间距离
        float dx = mWidth / (float) samplingSize;

        Log.i("qiyue", "dx=" + dx);

        for (float lineFunc : linesFunc) {
            samplingY = new float[samplingSize + 1];
            for (int i = 0; i <= samplingSize; i++) {
                float x = i * dx;
                samplingX[i] = x;
                if (i < samplingSize / 2) {
                    // 增加 0
                    shakeFunc = i;
                } else {
                    // 减少 1 0
                    shakeFunc = samplingSize - i;
                }

                samplingY[i] = calculateY(x, offset, shakeFunc * lineFunc);

                Log.i("qiyue", "samplingY=" + samplingY);
            }
            linesY.put(lineFunc, samplingY);
        }
    }

    /**
     * 画线
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        canvas.drawColor(mBgColor);
        for (int j = 0; j < linesFunc.length; j++) {
            float lineFunc = linesFunc[j];
            float[] yAxis = linesY.get(lineFunc);
            for (int i = 1; i <= samplingSize; i++) {
                if (j == 0) {
                    mMainPaint.setStrokeWidth(mMainWidth);
                } else {
                    mMainPaint.setStrokeWidth(mSubWidth);
                }
                canvas.drawLine(samplingX[i - 1], yAxis[i - 1], samplingX[i], yAxis[i], mMainPaint);
            }
        }
    }

    @Override
    public void stopAnim() {
        super.stopAnim();
        clearDraw();
    }

    /**
     * 设置音量大小
     *
     * @param volume
     */
    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void clearDraw() {
        Canvas canvas = null;
        try {
            canvas = getHolder().lockCanvas(null);
            canvas.drawColor(mBgColor);
        } catch (Exception e) {
        } finally {
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * 计算Y轴坐标
     *
     * @param x
     * @param offset    偏移量
     * @param shakeFunc 衰减系数
     * @return
     */
    private float calculateY(float x, float offset, float shakeFunc) {
        double rad = degreeToRad(x);
        Log.i("rad", "rad=" + rad + "x="+x);
        float volumeFunc = (volume + 10) / 30f;

        //Math.sin(x)    -1   0   1
        double h = Math.sin(rad + offset) * 300;

        Log.i("qiyue", "h=" + h +"********"+Math.sin(rad + offset));

        float shake = shakeFunc / (float) samplingSize;

        // 0 0.25 0
        Log.i("shake", "shake=" + shake);

        return (float) (mCenterY - Math.sin(x + offset) * 300 * volumeFunc * shake);

    }

    /**
     * 角度换弧度
     *
     * @param degree
     * @return
     */
    private double degreeToRad(double degree) {
        return degree * Math.PI / 180;
    }
}
