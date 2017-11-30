package com.tt.smoothlinechart;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final int MSG_ADD_VALUE = 0;

    private SmoothLineChartView mSmoothLineChartView;

    private Handler mHandler;
    private Handler.Callback mHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MSG_ADD_VALUE:
                    mSmoothLineChartView.addNewValue(new Random().nextInt(100));
                    break;
            }
            return false;
        }
    };

    private Timer mTimer;
    private TimerTask mTimerTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(MSG_ADD_VALUE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSmoothLineChartView = findViewById(R.id.weight_main_linechart);

        mHandler = new Handler(mHandlerCallback);
        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 1000, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }
}
