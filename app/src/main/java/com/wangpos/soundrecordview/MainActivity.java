package com.wangpos.soundrecordview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Handler mHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int max=(int)dip2px(getApplicationContext(),40f);
            int min=0;
            Random random = new Random();
            int s = random.nextInt(max)%(max-min+1) + min;

            srv.setDecibel(s);
            mHandle.sendEmptyMessageDelayed(0,5);

        }
    };
    SoundRecordView srv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        srv = (SoundRecordView)findViewById(R.id.srv);
        Button start = (Button)findViewById(R.id.btn);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandle.sendEmptyMessageDelayed(0,50);
            }
        });



    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return  (dpValue * scale + 0.5f);
    }
}
