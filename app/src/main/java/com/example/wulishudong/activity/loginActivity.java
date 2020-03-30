package com.example.wulishudong.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wulishudong.R;
import com.example.wulishudong.facade.UserFacade;
import com.example.wulishudong.netRequest.HttpRequest;
import com.example.wulishudong.util.GetIp;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class loginActivity extends AppCompatActivity {
    private  EditText userText;
    private EditText passText;
    private Button loginBt;
    private TextView registerTextView;
    private TextView itemTitle;
    private TextView backIcon;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();

        //设置返回
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity.this.finish();
            }
        });

        //登陆
        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(loginTask).start();
            }
        });

        //跳转到注册
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private  void init(){
        registerTextView = findViewById(R.id.wl_register);
        loginBt = findViewById(R.id.login_bt);
        userText = findViewById(R.id.login_user_name);
        passText = findViewById(R.id.login_user_password);

        itemTitle = findViewById(R.id.item_title);
        itemTitle.setText("登陆");
        backIcon = findViewById(R.id.back_to_page);

    }


    Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果为-->" + val);
            // UI界面的更新等相关操作
            JSONObject jsonObjectRes = null;
            try {
                jsonObjectRes = new JSONObject(val);
                String  state = jsonObjectRes.optString("state");
                if(state.equals("false")){
                    Toast.makeText(loginActivity.this,"用户名或者密码输入错误",Toast.LENGTH_SHORT).show();
                }else{
                    String name = jsonObjectRes.optString("name");
                    saveLoginStatus(true,name);
                    //销毁登录界面
                    loginActivity.this.finish();
                    //startActivity(new Intent(loginActivity.this, MainActivity.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    Runnable loginTask = new Runnable() {
        @Override
        public void run() {
            // 在这里进行 http request.网络请求相关操作
            String user = userText.getText().toString().trim();
            String password = passText.getText().toString().trim();
            if(user.equals("") || password.equals("")){
                Looper.prepare();
                Toast.makeText(loginActivity.this,"用户名或者密码不能为空",Toast.LENGTH_SHORT).show();
                Looper.loop();
            }else{
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name",user);
                    jsonObject.put("password",password);
                    String ip = GetIp.getIpAddress();
                    String url = "http://"+ip+":8080/user/login.do";
                    String res = HttpRequest.requestByPost(url,jsonObject.toString());
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value", res);
                    msg.setData(data);
                    loginHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     *保存登录状态和登录用户名到SharedPreferences中
     */
    private void saveLoginStatus(boolean status,String userName) {
        SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        //获取编辑器
        SharedPreferences.Editor editor=sp.edit();
        //存入boolean类型的登录状态
        editor.putBoolean("isLogin", status);
        //存入登录状态时的用户名
        editor.putString("loginUserName", userName);
        //提交修改
        editor.commit();
    }
}
