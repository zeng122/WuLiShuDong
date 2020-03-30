package com.example.wulishudong.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wulishudong.R;
import com.example.wulishudong.netRequest.HttpRequest;
import com.example.wulishudong.util.GetIp;
import com.example.wulishudong.util.InfoUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class myInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView itemTitle;
    private TextView backIcon;
    private String  name, newSignature,newTelephone,newSex;
    private ImageView iv_head_icon;
    private TextView tv_user_name,tv_sex,tv_signature,tv_telephone;
    private RelativeLayout rl_head,rl_account,rl_sex,rl_signature,rl_telephone;

    private static final int CHANGE_SIGNATURE = 2;
    private static final int CHANGE_TELEPHONE = 1;
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        //设置此界面为竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();

        name = InfoUtils.readLoginUserName(this);
        new Thread(myInfoTask).start();


    }

    private void init(){
        iv_head_icon = findViewById(R.id.iv_head_icon);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_sex = findViewById(R.id.tv_sex);
        tv_signature = findViewById(R.id.tv_signature);
        tv_telephone = findViewById(R.id.tv_telephone);

        rl_head = findViewById(R.id.rl_head);
        rl_account = findViewById(R.id.rl_account);
        rl_sex = findViewById(R.id.rl_sex);
        rl_signature = findViewById(R.id.rl_signature);
        rl_telephone = findViewById(R.id.rl_telephone);

        rl_head.setOnClickListener(this);
        rl_account.setOnClickListener(this);
        rl_sex.setOnClickListener(this);
        rl_signature.setOnClickListener(this);
        rl_telephone.setOnClickListener(this);

        itemTitle = findViewById(R.id.item_title);
        itemTitle.setText("我的信息");
        backIcon = findViewById(R.id.back_to_page);
        backIcon.setOnClickListener(this);
    }


    Handler myInfoHandler = new Handler() {
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
                    Toast.makeText(myInfoActivity.this,"获取用户信息失败",Toast.LENGTH_SHORT).show();
                }else{
                    tv_user_name.setText(jsonObjectRes.optString("name"));
                    tv_sex.setText(jsonObjectRes.optString("sex"));
                    tv_signature.setText(jsonObjectRes.optString("signature"));
                    tv_telephone.setText(jsonObjectRes.optString("telephone"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };


    Runnable myInfoTask = new Runnable() {

        @Override
        public void run() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name",name);
                String ip = GetIp.getIpAddress();
                String url = "http://"+ip+":8080/user/getUserByName.do";
                String res = HttpRequest.requestByPost(url,jsonObject.toString());
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value", res);
                msg.setData(data);
                myInfoHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Handler changeSexHandler = new Handler() {
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
                    tv_sex.setText(newSex);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };


    Runnable changeSexTask = new Runnable() {

        @Override
        public void run() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name",name);
                jsonObject.put("sex",newSex);
                String ip = GetIp.getIpAddress();
                String url = "http://"+ip+":8080/user/changeSexByName.do";
                String res = HttpRequest.requestByPost(url,jsonObject.toString());
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value", res);
                msg.setData(data);
                changeSexHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 其实就是把需要传递的数值放置于bundle中，bundle作为附加到intent中
     * 获取回传数据是需要的跳转方法，第三个参数标示跳转是传递的数据
     **/
    public void enterActivityForResult(Class<?> to, int requestCode, Bundle b) {
        Intent intent = new Intent(this, to);
        intent.putExtras(b);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHANGE_TELEPHONE:
                if(data!=null){
                    newTelephone = data.getStringExtra("telephone");
                    tv_telephone.setText(newTelephone);
                }
                break;
            case CHANGE_SIGNATURE:
                if (data != null) {
                    newSignature = data.getStringExtra("signature");
                    tv_signature.setText(newSignature);
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.back_to_page:
               myInfoActivity.this.finish();
               break;
           case R.id.rl_signature:
               String signature = tv_signature.getText().toString();
               Bundle bdSignature = new Bundle();
               bdSignature.putString("content", signature); //传递界面上的签名数据
               bdSignature.putString("title", "签名");
               bdSignature.putInt("flag", 2);
               enterActivityForResult(changeUserInfoActivity.class, CHANGE_SIGNATURE, bdSignature);
               break;
           case R.id.rl_telephone:
               String telephone = tv_telephone.getText().toString();
               Bundle bdTelephone = new Bundle();
               bdTelephone.putString("content", telephone); //传递界面上的签名数据
               bdTelephone.putString("title", "电话");
               bdTelephone.putInt("flag", 1);
               enterActivityForResult(changeUserInfoActivity.class, CHANGE_TELEPHONE, bdTelephone);
               break;
           case R.id.rl_sex:
               String sex = tv_sex.getText().toString();
               sexDialog(sex);

       }
    }

    private void sexDialog(String sex) {
        int sexFlag = 0;
        if ("男".equals(sex)) {
            sexFlag = 0;
        } else if ("女".equals(sex)) {
            sexFlag = 1;
        }
        final String items[] = {"男", "女"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("性别");
        //sexFlag用来区分显示被选中项，如果sexFlag的值和在数组中的索引严格符合，下方which也可用sexFlag代替
        builder.setSingleChoiceItems(items, sexFlag, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                newSex = items[which];
                new Thread(changeSexTask).start();
            }
        });
        builder.create().show();
    }
}
