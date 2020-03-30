package com.example.wulishudong.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.wulishudong.Adapter.CommentAdapter;
import com.example.wulishudong.R;
import com.example.wulishudong.bean.Blog;
import com.example.wulishudong.facade.CommentFacade;
import com.example.wulishudong.netRequest.HttpRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JuBaoDialog {
    private BottomSheetDialog dialog;
    private Blog blog;
    private Context context;
    private  String jubaoTx = null;
    public   JuBaoDialog(Context contextTran, Blog blogTran) {
        dialog = new BottomSheetDialog(contextTran, R.style.BottomSheetEdit);
        this.blog = blogTran;
        this.context = contextTran;
        View commentView = LayoutInflater.from(context).inflate(R.layout.jubao_dialog, null);
        final EditText jubaoText = (EditText) commentView.findViewById(R.id.dialog_jubao);
        final Button jubaoBt = (Button) commentView.findViewById(R.id.jubao_bt);
        dialog.setContentView(commentView);

        jubaoBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jubaoContend = jubaoText.getText().toString().trim();
                if (!TextUtils.isEmpty(jubaoContend)) {
                    dialog.dismiss();
                     jubaoTx = jubaoContend;
                    //进行网络传输
                    new Thread(jubaoTask).start();
                } else {
                    Toast.makeText(context, "举报内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });


        jubaoText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s) && s.length() > 1) {
                    jubaoBt.setBackgroundColor(Color.parseColor("#FFB568"));
                } else {
                    jubaoBt.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog.show();
    }

    Runnable jubaoTask = new Runnable() {
        @Override

        public void run() {
            // 在这里进行 http request.网络请求相关操作
            JSONObject jsonObject = new JSONObject();
            try {
                int blogId = blog.getId();
                String name  = InfoUtils.readLoginUserName(context);
                jsonObject.put("blogId",blogId);
                jsonObject.put("name",name);
                jsonObject.put("contend",jubaoTx);
                String ip = GetIp.getIpAddress();
                String url = "http://"+ip+":8080/blog/addJubao.do";
                HttpRequest.requestByPost(url,jsonObject.toString());
                Looper.prepare();
                Toast.makeText(context, "举报成功", Toast.LENGTH_SHORT).show();
                Looper.loop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
}
