package com.wangpos.soundrecordview.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;

import com.wangpos.soundrecordview.CustomSurfaceView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private int mLineColor = Color.parseColor("#FF0000");
    //主要线宽
    private int mMainWidth = 7;


    private int mHeight;

    private int mWidth;

    private float mCenterY;

    /**
     * 点数,当然越多越细腻
     */
    private int pointSize = 200;
    /**
     * 声音分贝数
     */
    private float volume = 1;


    /**
     * 速度
     */
    private float velocity = 1;

    /**
     * 默认线的初始化振幅
     */
    private float[] shakeRatioArray = {1.5f,2f,2.5f};

    /**
     * 默认线的偏移量
     */
    private float[] lineOffset = {0f,1.5f,3f};

    /**
     * 默认颜色
     */
//    private int[] lineColor = {0xa060ffff,0x2238f5c3,0xABF3FF00};

    private int[] lineColor = {0xffff0000,0xff00ff00,0xff0000ff};
    /**
     * 线的集合
     */
    Map<Integer, List<Point>> lines = new HashMap<>();

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
        mMainPaint.setStrokeCap(Paint.Cap.ROUND);
        mMainPaint.setStrokeJoin(Paint.Join.ROUND);


    }

    @Override
    public void drawContent(Canvas canvas, long millisPassed) {
        initDraw(canvas, millisPassed);
        drawLine(canvas);
    }


    /**
     * 初始化参数以及计算出点的位置
     *
     * @param canvas
     * @param millisPassed
     */
    private void initDraw(Canvas canvas, long millisPassed) {
        lines.clear();
        //根据时间偏移
        float offset = millisPassed / 100f * velocity;
        mWidth = canvas.getWidth();
        mHeight = canvas.getHeight();
        mCenterY = mHeight / 2;
        float dx = (float) mWidth / (pointSize - 1);// 必须为float，否则丢失
        Log.i("qiyue", "dx = " + dx + "offset=" + offset);

        for (int j = 0; j < shakeRatioArray.length; j++) {
            List<Point>points = new ArrayList<>();
            initLine(offset, dx, shakeRatioArray[j], points,lineOffset[j]);
            lines.put(j,points);
        }


    }

    /**
     * 初始化一条线
     * @param offset
     * @param dx
     * @param shakeRatio
     * @param points
     */
    private void initLine(float offset, float dx, float shakeRatio, List<Point> points,float lOffset) {
        for (int i = 0; i < pointSize; i++) {
            float y;
            float x = dx * i;
            float adapterShakeParam;
            adapterShakeParam = convergenFunction(i);
            Log.i("qiyue", "adapterShakeParam=" + adapterShakeParam);
            y = calculateY(x, offset, adapterShakeParam, shakeRatio,lOffset);
            points.add(new Point(x, y));
        }
    }

    /**
     * 收敛函数  这个函数可以用一个二次函数，效果更好
     *
     * @param i
     * @return
     */
    private float convergenFunction(int i) {
        float adapterShakeParam;
        if (i < pointSize / 2) {
            // 增加 0
            adapterShakeParam = i;
        } else {
            // 减少 1 0
            adapterShakeParam = pointSize - i;
        }
        return adapterShakeParam;
    }

    /**
     * 画线
     *
     * @param canvas
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void drawLine(Canvas canvas) {
        canvas.drawColor(mBgColor);
        Log.i("qiyue", "drawLine");

        List<Path>paths = new ArrayList<>();

        List<Path>paths2 = new ArrayList<>();

        for (Integer key:lines.keySet()){
            mMainPaint.setColor(lineColor[key]);
            List<Point>points = lines.get(key);

            Path path = new Path();
            path.moveTo(0,mHeight);
            for (int i = 1;i<pointSize;i++) {
                Point p = points.get(i);
                path.lineTo(p.x,p.y);
            }
            path.lineTo(mWidth,mHeight);
            path.close();

            paths.add(path);


            Path path2 = new Path();
            path2.moveTo(0,mHeight);
            for (int i = 1;i<pointSize;i++) {
                Point p = points.get(i);
                path2.lineTo(p.x,p.y);
            }
            path2.lineTo(mWidth,mHeight);
            path2.close();

            paths2.add(path2);


//            for (int i = 1; i < pointSize; i++) {
//                Point p1 = points.get(i - 1);
//                Point p2 = points.get(i);
//                Log.i("qiyue", "p1=" + p1);
//
//                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, mMainPaint);
//            }
        }
//
//        Path topPath = new Path();
//
//        topPath.moveTo(0,0);
//        topPath.lineTo(mWidth,0);
//        topPath.lineTo(mWidth,mHeight);
//        topPath.lineTo(0,mHeight);
//        topPath.close();
//
//        mMainPaint.setColor(0xff4FAEC1);
//        canvas.drawPath(topPath,mMainPaint);



        Path path = paths.get(0);
        Path path1 = paths.get(1);
        Path path2 = paths.get(2);



        path.op(paths2.get(1), Path.Op.XOR);
        mMainPaint.setColor(lineColor[0]);
        canvas.drawPath(path,mMainPaint);

        path1.op(paths2.get(2),Path.Op.XOR);
        mMainPaint.setColor(lineColor[1]);
        canvas.drawPath(path1,mMainPaint);


        path2.op(paths2.get(0),Path.Op.XOR);
        mMainPaint.setColor(lineColor[2]);
        canvas.drawPath(path2,mMainPaint);





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
     * @param offset 偏移量
     * @return
     */
    private float calculateY(float x, float offset, float adapterShakeParam, float shakeRatio,float lOffset) {

        /**
         * 弧度取值范围 0 2π
         */
        double rad = Math.toRadians(x) + lOffset;

        double fx = Math.sin(rad + offset);

        float dy = (float) (fx);

        float calculateVolume = getCalculateVolume();

        float finalDy = dy * adapterShakeParam * shakeRatio * calculateVolume;

        return (float) (mCenterY - finalDy);

    }

    /**
     * 声音分贝计算
     *
     * @return
     */
    private float getCalculateVolume() {
        return (volume +10) / 20f;
    }


    /**
     * 绘制点Bean
     */
    class Point {
        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }

        public float x;
        public float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

    }


}
