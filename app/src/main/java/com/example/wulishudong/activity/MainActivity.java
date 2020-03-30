package com.example.wulishudong.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wulishudong.R;
import com.example.wulishudong.fragment.DongtaiFragment;
import com.example.wulishudong.fragment.HoleFragment;
import com.example.wulishudong.fragment.MyIssueFragment;
import com.example.wulishudong.network.NetworkUtil;
import com.example.wulishudong.util.InfoUtils;
import com.google.android.material.navigation.NavigationView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends  AppCompatActivity implements  View.OnClickListener {
    private AppBarConfiguration mAppBarConfiguration;
    private long exitTime;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView itemBar1,itemBar2,itemBar3,tvUsername;
    private RelativeLayout itemBar1Bt,itemBar2Bt,itemBar3Bt;
    private Button footExitBt;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //初始化界面
        init();

        //判断是否有网络
        NetworkUtil networkUtil = new NetworkUtil(getApplicationContext());
        if(networkUtil.isNetConneted()){
            Toast.makeText(getApplicationContext(),"有网络",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"无网络,请稍后打开",Toast.LENGTH_SHORT).show();
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                      System.exit(0);
                }
            };
            timer.schedule(timerTask,2000);
        }
        //初始化界面
        initMainFragment();

        if(InfoUtils.readLoginUserName(this).equals("")){

        }

        if(InfoUtils.readLoginStatus(this)){
           String userName = InfoUtils.readLoginUserName(this);
           tvUsername.setText(userName);
        }else{
            tvUsername.setText("点击登录");
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intend;
                if(!InfoUtils.readLoginStatus(getApplicationContext())){
                    Toast.makeText(getApplicationContext(),"当前未登录",Toast.LENGTH_SHORT).show();
                  return false;
                }
                switch (menuItem.getItemId()){
                    case R.id.item1:
                        intend = new Intent(MainActivity.this,myInfoActivity.class);
                        startActivity(intend);
                        break;
                    case R.id.item2:

                        intend = new Intent(MainActivity.this,IssueActivity.class);
                        startActivity(intend);
                        break;
                    case R.id.item3:

                        intend = new Intent(MainActivity.this,MyLikeActivity.class);
                        startActivity(intend);
                        break;
                }
                return false;
            }
        });




    }

    @Override
    //给MainActivity加上退出清除登陆状态的方法。
    // 连续点击返回两次则退出，两次点击间隔超过2秒则提示再按一次退出。
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出武理树洞", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                this.finish();
                if (InfoUtils.readLoginStatus(this)) {
                    InfoUtils.cleanLoginStatus(this);
                }
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void init(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("树洞");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.setDrawerListener(toggle);//初始化状态
        toggle.syncState();

        itemBar1 = findViewById(R.id.main_bottom_bar_item1_text);
        itemBar2 = findViewById(R.id.main_bottom_bar_item2_text);
        itemBar3 = findViewById(R.id.main_bottom_bar_item3_text);
        itemBar1Bt = findViewById(R.id.main_bottom_bar_item1_bt);
        itemBar2Bt = findViewById(R.id.main_bottom_bar_item2_bt);
        itemBar3Bt = findViewById(R.id.main_bottom_bar_item3_bt);

        itemBar1Bt.setOnClickListener(this);
        itemBar2Bt.setOnClickListener(this);
        itemBar3Bt.setOnClickListener(this);

        View headView = navigationView.getHeaderView(0);
        tvUsername = headView.findViewById(R.id.user_name);
        tvUsername.setOnClickListener(this);
        footExitBt = findViewById(R.id.footer_item_out);
        footExitBt.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(InfoUtils.readLoginStatus(this)){
            String userName = InfoUtils.readLoginUserName(this);
            tvUsername.setText(userName);
        }
    }


    private  void initMainFragment(){
        this.getSupportFragmentManager().beginTransaction().add(R.id.main_body,new HoleFragment()).commit();
        setSelectStatus(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_bottom_bar_item1_bt:
                setSelectStatus(1);
                break;
            case R.id.main_bottom_bar_item2_bt:
                setSelectStatus(2);
                break;
            case R.id.main_bottom_bar_item3_bt:
                setSelectStatus(3);
                break;
            case R.id.footer_item_out:
                if(InfoUtils.readLoginStatus(this)){
                    InfoUtils.cleanLoginStatus(this);
                    tvUsername.setText("点击登录");

                }else{
                    Toast.makeText(getApplicationContext(),"当前没有登录",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.user_name:
                if(InfoUtils.readLoginStatus(this)){
                }else{
                    Intent intent = new Intent(MainActivity.this,loginActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    private  void setSelectStatus(int index){
        switch (index){
            case 1:
                itemBar1.setTextColor(getResources().getColor(R.color.itemBarSelected));
                itemBar2.setTextColor(getResources().getColor(R.color.itemBar));
                itemBar3.setTextColor(getResources().getColor(R.color.itemBar));
                this.getSupportFragmentManager().beginTransaction().replace(R.id.main_body,new HoleFragment()).commit();
                toolbar.setTitle("树洞");
                break;
            case 2:
                itemBar2.setTextColor(getResources().getColor(R.color.itemBarSelected));
                itemBar1.setTextColor(getResources().getColor(R.color.itemBar));
                itemBar3.setTextColor(getResources().getColor(R.color.itemBar));
                this.getSupportFragmentManager().beginTransaction().replace(R.id.main_body,new DongtaiFragment()).commit();
                toolbar.setTitle("动态");
                break;
            case 3:
                itemBar3.setTextColor(getResources().getColor(R.color.itemBarSelected));
                itemBar2.setTextColor(getResources().getColor(R.color.itemBar));
                itemBar1.setTextColor(getResources().getColor(R.color.itemBar));
                this.getSupportFragmentManager().beginTransaction().replace(R.id.main_body,new MyIssueFragment()).commit();
                toolbar.setTitle("我的发布");
                break;
        }
    }
}
