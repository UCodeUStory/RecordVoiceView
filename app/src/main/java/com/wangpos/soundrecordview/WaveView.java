package com.wangpos.soundrecordview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by qiyue on 2016/11/7.
 */
public class WaveView extends View {

    private int MAX_LENGTH = 0;

    private Paint paint;
    private Path path;


    private Paint bgPaint;

    private int MAX_WAVE_HEIGHT = 0;


    private int v = 6;

    private Path path2;


    /**
     * 波纹
     */
    private List<WavePoint> waveList = new ArrayList<>();

    private int color;

    public WaveView(Context context) {
        this(context,null);
        init(context);
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
        init(context);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);



        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaveView);

        MAX_LENGTH = (int)ta.getDimension(R.styleable.WaveView_wavelength,200);

        color = ta.getColor(R.styleable.WaveView_color,Color.argb(255,78,143,136));

        MAX_WAVE_HEIGHT = MAX_LENGTH/2;

        Log.i("tt","MAX_LENGTH="+MAX_LENGTH);

        ta.recycle();





        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
//        this.color = Color.BLACK;
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);


        bgPaint = new Paint();
//        this.color = Color.BLACK;
        bgPaint.setColor(Color.BLACK);
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setStrokeWidth(10);
        bgPaint.setAntiAlias(true);
//        setBackgroundColor(Color.GRAY);

        path = new Path();


//        path2 = new Path();



    }




    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        Log.i("tt","onDraw");
//
//        bgPaint.setColor(Color.argb(255,71,155,172));
//        canvas.drawRect(new RectF(0,0,getWidth(),getHeight()/2),bgPaint);


//        paint.setColor(Color.argb(255,78,143,136));
        path.reset();

        path.moveTo(0, getHeight() / 2);

        if (waveList.size()==0){
//            waveList.add(new WavePoint(0,0,100,100,200,0));//水平速度是垂直的2倍
            waveList.add(new WavePoint(0,0,1,1,2,0));
        }
        int size = waveList.size();
        for (int i = 0; i <size ; i++) {
            WavePoint point = waveList.get(i);
//            Log.i("point","point="+point.toString());
            path.rQuadTo(point.bX, point.bY, point.cX, point.cY);

            /** 处理第一个 */
            if (i==0) {
                if (!point.isMaxLength()) {
                    point.setbX(point.bX + v/2);
//                    Log.i("point","firstPoint111 =" + point.getDirection());

                    if (point.getDirection() == Direction.DOWN) {
                        point.setbY(point.bY + v/2);
                    }else{
                        point.setbY(point.bY - v/2);
                    }
                    point.setcX(point.cX + v);
                }
            }



        }




        /**
         *
         * 处理最后一个
         * */

        float firstcX = waveList.get(0).cX;

        if (size>2) {
            WavePoint endPoint = waveList.get(size-1);


            float totalcX = MAX_LENGTH * (size-2)+ firstcX + endPoint.cX;
//            Log.i("point", "totalcX cX=" + totalcX+"firstcX="+firstcX+"endPoint.cX="+endPoint.cX+"getWidth()="+getWidth());

            if (totalcX >getWidth()) {

                float p = (totalcX-getWidth())*1f/MAX_LENGTH ;


                if (endPoint.getcX()>0) {
                    if (endPoint.getDirection() == Direction.DOWN) {
                        endPoint.setbY(endPoint.bY - v/2);

                    } else {
                        endPoint.setbY(endPoint.bY + v/2);
                    }
                    endPoint.setbX(endPoint.bX - p * MAX_WAVE_HEIGHT);

//                    if ((totalcX-getWidth())<=endPoint.cX) {

                        Log.i("point","totalcX ="+totalcX);
                        Log.i("point","getWidth() ="+getWidth());


                        endPoint.setcX(endPoint.cX - (totalcX - getWidth()));



//                    }else{
//                        endPoint.setcX(0);
//                    }
                    /**
                     * 突然出来个4.0导致闪动
                     */
                    Log.i("point", "endPoint =" + (totalcX-getWidth()));
                }else{
                    Log.i("point", "endPoint =" + endPoint.toString());
                    waveList.remove(size-1);

                    WavePoint point = waveList.get(0);

                    if (!point.isMaxLength()) {
                        point.setbX(point.bX - v/2);
//                    Log.i("point","firstPoint111 =" + point.getDirection());

                        if (point.getDirection() == Direction.DOWN) {
                            point.setbY(point.bY - v/2);
                        }else{
                            point.setbY(point.bY - v/2);
                        }
                        point.setcX(point.cX - v);
                    }


                }
            }

        }

        /**添加新的*/
        WavePoint firstPoint = waveList.get(0);

        if (firstPoint.isMaxLength()){
//            Log.i("point","firstPoint =" + firstPoint.getDirection());
            if(firstPoint.getDirection() == Direction.DOWN){
                waveList.add(0,new WavePoint(0,0,1,-1,2,0));
            }else{
                waveList.add(0,new WavePoint(0,0,1,1,2,0));
            }

        }

        path.lineTo(getWidth(),getHeight());
        path.lineTo(0,getHeight());
        path.close();

        canvas.drawPath(path, paint);





        invalidate();




    }

    private int width;
    private int height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;

        Log.i("tt","onSizeChanged");

        int count = w/MAX_LENGTH;

        int least = w%MAX_LENGTH;

        for (int i=0;i<count;i++){

            if (i%2==0) {
                waveList.add(new WavePoint(0, 0, MAX_WAVE_HEIGHT, MAX_WAVE_HEIGHT, MAX_LENGTH, 0));
            }else{
                waveList.add(new WavePoint(0, 0, MAX_WAVE_HEIGHT, -MAX_WAVE_HEIGHT, MAX_LENGTH, 0));
            }
        }
        float bx = (least*1f/MAX_LENGTH)*MAX_WAVE_HEIGHT;
        float by = (least*1f/MAX_LENGTH)*MAX_WAVE_HEIGHT;
        WavePoint firstPoint = waveList.get(0);

        if (firstPoint.getDirection() == Direction.DOWN) {
            waveList.add(0, new WavePoint(0, 0, bx, -by, least, 0));
        }else{
            waveList.add(0, new WavePoint(0, 0, bx, by, least, 0));
        }
    }

    /**
***************************** 实体类 ********************************
*/



    enum Direction{
        Up,DOWN
    }

    class WavePoint{

        Direction direction;

        public float getaX() {
            return aX;
        }

        public void setaX(float aX) {
            this.aX = aX;
        }

        public float getaY() {
            return aY;
        }

        public void setaY(float aY) {
            this.aY = aY;
        }

        public float getbX() {
            return bX;
        }

        public void setbX(float bX) {
            this.bX = bX;
        }

        public float getbY() {
            return bY;
        }

        public void setbY(float bY) {
            this.bY = bY;
        }

        public float getcX() {
            return cX;
        }

        public void setcX(float cX) {
            this.cX = cX;
        }

        public float getcY() {
            return cY;
        }

        public void setcY(float cY) {
            this.cY = cY;
        }


        public Direction getDirection(){
            if (bY>0){
                return Direction.DOWN;
            }else{
                return Direction.Up;
            }
        }

        /**起始点x轴坐标*/
        private float aX;
        /**起始点y轴坐标*/
        private float aY;

        /**拐点X轴坐标*/
        private float bX;
        /**拐点Y轴坐标*/
        private float bY;

        /**终点X轴坐标*/
        private float cX;
        /**终点Y轴坐标*/
        private float cY;



        public WavePoint(float aX, float aY, float bX, float bY, float cX, float cY) {
            this.aX = aX;
            this.aY = aY;
            this.bX = bX;
            this.bY = bY;
            this.cX = cX;
            this.cY = cY;
        }

        public boolean isMaxLength(){
            if (cX-aX>=MAX_LENGTH){
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return " ax="+aX+ " aY="+aY+" bX="+bX+" bY="+bY+" cX="+cX+" cY="+cY;
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return  (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
