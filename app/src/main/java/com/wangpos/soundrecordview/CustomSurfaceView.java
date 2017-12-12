package com.chinajesit.recordview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.lang.ref.WeakReference;

/**
 * Created by Allen on 2017/9/18.
 */

public abstract class CustomSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final Object lock = new Object();

    private SurfaceHolder mSurfaceHolder;
    private DrawThread mThread;

    public CustomSurfaceView(Context context) {
        this(context, null);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mThread = new DrawThread(this);
        mThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (lock) {
            mThread.destroyed = true;
            mThread.setRun(false);
            release();
        }
    }

    private static class DrawThread extends Thread {

        private WeakReference<CustomSurfaceView> customSurfaceView;
        public boolean destroyed = false;//是否被销毁
        public boolean isPause = false;//暂停
        private boolean isRun = false;//正在运行

        /**
         * 使用弱引用，防止内存泄露
         *
         * @param customSurfaceView
         */
        public DrawThread(CustomSurfaceView customSurfaceView) {
            this.customSurfaceView = new WeakReference<>(customSurfaceView);
        }

        private CustomSurfaceView getCustomSurfaceView() {
            return customSurfaceView.get();
        }

        private SurfaceHolder getSurfaceHolder() {
            if (getCustomSurfaceView() != null) {
                return getCustomSurfaceView().getHolder();
            }
            return null;
        }

        public void setRun(boolean isRun) {
            this.isRun = isRun;
        }

        @Override
        public void run() {
            long startAt = System.currentTimeMillis();
            while (!destroyed) {
                synchronized (lock) {
                    //暂停阶段
                    while (isPause) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //绘制
                    if (isRun) {
                        if (getSurfaceHolder() != null) {
                            Canvas canvas = getSurfaceHolder().lockCanvas();
                            getCustomSurfaceView().drawContent(canvas,System.currentTimeMillis() - startAt);
                            getSurfaceHolder().unlockCanvasAndPost(canvas);//提交
                        } else {
                            isRun = false;
                        }
                    }
                }
                try {
                    sleep(16);//16毫秒绘制
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void drawContent(Canvas canvas,long millisPassed) {

    }

    /**
     * 解锁暂停，继续执行绘制任务
     * 默认当Resume时不自动启动动画
     */
    public void onResume(){
        synchronized (lock){
            if (mThread != null) {
                mThread.isPause = false;
                lock.notifyAll();
            }
        }
    }


    //假暂停，并没有结束Thread
    public void onPause(){
        synchronized (lock){
            if (mThread != null) {
                mThread.isPause = true;
            }
        }
    }

    public void startAnim(){
        mThread.setRun(true);
    }

    public void stopAnim(){
        mThread.setRun(false);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (getHolder() != null && getHolder().getSurface() != null) {
            getHolder().getSurface().release();
            getHolder().removeCallback(this);
        }
    }
}
