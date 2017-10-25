package com.wangpos.soundrecordview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Handler mHandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);


            int max=60;
            int min=10;
            Random random = new Random();

            int s = random.nextInt(max)%(max-min+1) + min;

            srv.setDecibel(s);
            mHandle.sendEmptyMessageDelayed(0,10);

        }
    };
    SoundRecordView srv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         srv = (SoundRecordView)findViewById(R.id.srv);

         mHandle.sendEmptyMessageDelayed(0,10);


    }
}
