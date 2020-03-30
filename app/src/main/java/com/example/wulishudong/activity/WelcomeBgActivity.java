package com.example.wulishudong.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wulishudong.R;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeBgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //跳转到主页
        toMainPage();
    }

    private void toMainPage(){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intend = new Intent(WelcomeBgActivity.this,MainActivity.class);
                startActivity(intend);
                //关闭欢迎页
                WelcomeBgActivity.this.finish();
            }
        };
        timer.schedule(timerTask,2000);
    }
}
