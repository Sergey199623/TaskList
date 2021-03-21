package com.sv.tasklist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sv.tasklist.R;

import static java.lang.Thread.sleep;

public class SplashActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    TextView tvPercent;
    int mProgress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mProgressBar = findViewById(R.id.progressBar1);
        tvPercent = findViewById(R.id.value123);

        new Thread(myThread).start();

//        new Handler().postDelayed(() -> {
//            startActivity(new Intent(SplashActivity.this,
//                    MainActivity.class));
//            finish(); }, 2000);
    }

    private final Runnable myThread = new Runnable() {
        @Override
        public void run() {
            while (mProgress < 100) {
                try {
                    myHandler.sendMessage(myHandler.obtainMessage());
                    sleep(5);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                new Handler().post(() -> tvPercent.setText(String.valueOf(mProgress)));
            }
            startActivity(new Intent(SplashActivity.this,
                    MainActivity.class));
            finish();
        }

        Handler myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mProgress++;
                mProgressBar.setProgress(mProgress);
                tvPercent.setText(String.valueOf(mProgress));
            }
        };
    };
}
