package com.example.wulishudong.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wulishudong.R;
import com.example.wulishudong.netRequest.HttpRequest;
import com.example.wulishudong.util.GetIp;
import com.example.wulishudong.util.InfoUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class changeUserInfoActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView itemTitle;
    private TextView backIcon;
    private TextView tv_save;
    private ImageView iv_delete;
    private EditText et_content;
    String ContendSignture ,ContendTelephone;
    /*
    flag = 1 ,表示修改电话
    flag = 2 ,表示修改签名*/
    private int flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_userinfo);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();

        contentListener();
    }

    private void init(){
        itemTitle = findViewById(R.id.item_title);
        backIcon = findViewById(R.id.back_to_page);
        tv_save = findViewById(R.id.tv_save);

        et_content = (EditText) findViewById(R.id.et_content);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);

        String title = getIntent().getStringExtra("title");
        String content = getIntent().getStringExtra("content");
        flag = getIntent().getIntExtra("flag", 0);


        itemTitle.setText(title);
        tv_save.setVisibility(View.VISIBLE);

        backIcon.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
        tv_save.setOnClickListener(this);

        if (!TextUtils.isEmpty(content)){
            et_content.setText(content);
            et_content.setSelection(content.length());
        }

    }

    private void contentListener() {
    et_content.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
         Editable editable = et_content.getText();
         int len = editable.length();
         if(len>0){
             iv_delete.setVisibility(View.VISIBLE);
         }else{
             iv_delete.setVisibility(View.GONE);
         }

         switch (flag){
             case 1:
                 if(len>15) {
                     int selEndIndex = Selection.getSelectionEnd(editable);
                     String str = editable.toString();
                     String newStr = str.substring(0, 15);
                     et_content.setText(newStr);
                     editable = et_content.getText();
                     int newLen = editable.length();
                     if (selEndIndex > newLen) {
                         selEndIndex = editable.length();
                     }
                     Selection.setSelection(editable, selEndIndex);
                 }
                 break;
             case 2:
                 if(len>20){
                 int selEndIndex = Selection.getSelectionEnd(editable);
                 String str = editable.toString();
                 String newStr = str.substring(0,20);
                 et_content.setText(newStr);
                 editable =et_content.getText();
                 int newLen = editable.length();
                 if (selEndIndex > newLen) {
                     selEndIndex = editable.length();
                 }
                 Selection.setSelection(editable, selEndIndex);
             }
                 break;
             case 3:;break;
         }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    });
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.back_to_page:
               this.finish();
               break;
           case R.id.iv_delete:
               et_content.setText("");
               break;
           case R.id.tv_save:
               String etContend = et_content.getText().toString().trim();
               switch (flag){
                   case 1:
                       if(!TextUtils.isEmpty(etContend)){
                           ContendTelephone= etContend;
                           new Thread(saveTelephoneTask).start();
                       }else{
                           Toast.makeText(getApplicationContext(),"电话不能为空",Toast.LENGTH_SHORT);
                       }
                       break;
                   case 2:
                       if(!TextUtils.isEmpty(etContend)){
                           ContendSignture = etContend;
                              new Thread(saveSignatureTask).start();
                       }else{
                           Toast.makeText(getApplicationContext(),"签名不能为空",Toast.LENGTH_SHORT);
                       }
               }

       }
    }

    Handler saveSignatureHandle = new Handler(){
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
                if(state.equals("true")){
                    Toast.makeText(changeUserInfoActivity.this,"修改签名成功",Toast.LENGTH_SHORT).show();
                    Intent intentData = new Intent();
                    intentData.putExtra("signature",ContendSignture);
                    setResult(RESULT_OK, intentData);
                    changeUserInfoActivity.this.finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    Runnable saveSignatureTask = new Runnable() {
        @Override
        public void run() {
            // 在这里进行 http request.网络请求相关操作
                JSONObject jsonObject = new JSONObject();
                String user = InfoUtils.readLoginUserName(getApplicationContext());
                try {
                    jsonObject.put("name",user);
                    jsonObject.put("signature",ContendSignture);
                    String ip = GetIp.getIpAddress();
                    String url = "http://"+ip+":8080/user/changeSignatureByName.do";
                    String res = HttpRequest.requestByPost(url,jsonObject.toString());
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value", res);
                    msg.setData(data);
                    saveSignatureHandle.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
    };


    Handler saveTelephoneHandle = new Handler(){
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
                if(state.equals("true")){
                    Toast.makeText(changeUserInfoActivity.this,"修改电话成功",Toast.LENGTH_SHORT).show();
                    Intent intentData = new Intent();
                    intentData.putExtra("telephone",ContendTelephone);
                    setResult(RESULT_OK, intentData);
                    changeUserInfoActivity.this.finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    Runnable saveTelephoneTask = new Runnable() {
        @Override
        public void run() {
            // 在这里进行 http request.网络请求相关操作
            JSONObject jsonObject = new JSONObject();
            String user = InfoUtils.readLoginUserName(getApplicationContext());
            try {
                jsonObject.put("name",user);
                jsonObject.put("telephone",ContendTelephone);
                String ip = GetIp.getIpAddress();
                String url = "http://"+ip+":8080/user/changeTelephoneByName.do";
                String res = HttpRequest.requestByPost(url,jsonObject.toString());
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value", res);
                msg.setData(data);
                saveTelephoneHandle.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

}
