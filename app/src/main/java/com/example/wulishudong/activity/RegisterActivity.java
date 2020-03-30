package com.example.wulishudong.activity;

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

import androidx.appcompat.app.AppCompatActivity;

import com.example.wulishudong.R;
import com.example.wulishudong.netRequest.HttpRequest;
import com.example.wulishudong.util.GetIp;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private TextView itemTitle;
    private TextView backIcon;
    private EditText registerName,registerPass,registerConPass;
    private Button registerBt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();

        //返回
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });


        //点击注册
        registerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 new Thread(registerTask).start();
            }
        });

    }

    private  void init(){
        registerName =findViewById(R.id.register_user_name);
        registerPass = findViewById(R.id.register_user_password);
        registerConPass =findViewById(R.id.confirm_register_user_password);
        registerBt = findViewById(R.id.register_bt);

        itemTitle = findViewById(R.id.item_title);
        itemTitle.setText("注册");
        backIcon = findViewById(R.id.back_to_page);

    }

    Runnable registerTask = new Runnable() {
        @Override
        public void run() {
           String regName = registerName.getText().toString().trim();
           String regPass = registerPass.getText().toString().trim();
           String regConPass = registerConPass.getText().toString().trim();

           if(regName.equals("")){
               Looper.prepare();
               Toast.makeText(RegisterActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
               Looper.loop();
           }else if(regPass.equals("")){
               Looper.prepare();
               Toast.makeText(RegisterActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
               Looper.loop();
           }else if(regConPass.equals("")){
               Looper.prepare();
               Toast.makeText(RegisterActivity.this,"确认密码框不能为空",Toast.LENGTH_SHORT).show();
               Looper.loop();
           }else if(!regPass.equals(regConPass)){
               Looper.prepare();
               Toast.makeText(RegisterActivity.this,"密码和确认密码输入的不一样",Toast.LENGTH_SHORT).show();
               Looper.loop();
           }else{
               JSONObject jsonObject = new JSONObject();
               try {
                   jsonObject.put("name",regName);
                   jsonObject.put("password",regPass);
                   String ip = GetIp.getIpAddress();
                   String url = "http://"+ip+":8080/user/register.do";
                   String res = HttpRequest.requestByPost(url,jsonObject.toString());
                   Message msg = new Message();
                   Bundle data = new Bundle();
                   data.putString("value", res);
                   msg.setData(data);
                   registerHandle.sendMessage(msg);
               } catch (JSONException e) {
                   e.printStackTrace();
               }
           }

        }
    };

    Handler registerHandle = new Handler(){
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
                    Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                    RegisterActivity.this.finish();
                }
            } catch (Exception e) {
                    e.printStackTrace();
            }

            }
    };

}
