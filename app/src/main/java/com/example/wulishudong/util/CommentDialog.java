package com.example.wulishudong.util;

import android.content.Context;
import android.graphics.Color;
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

public class CommentDialog {
    private  BottomSheetDialog dialog;
    private  Blog blog;
    private  ListView listView;
    private  Context context;
    private  String commentTx = null;
    public   CommentDialog(Context contextTran, Blog blogTran, ListView listViewTran){
        dialog = new BottomSheetDialog(contextTran,R.style.BottomSheetEdit);
        this.blog = blogTran;
        this.listView = listViewTran;
        this.context = contextTran;
        View commentView = LayoutInflater.from(context).inflate(R.layout.comment_dialog,null);
        final EditText commentText = (EditText)commentView.findViewById(R.id.dialog_comment);
        final Button commentBt = (Button)commentView.findViewById(R.id.comment_bt);
        dialog.setContentView(commentView);

        commentBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentContend = commentText.getText().toString().trim();
                if(!TextUtils.isEmpty(commentContend)){
                    dialog.dismiss();
                    commentTx = commentContend;
                    String name = InfoUtils.readLoginUserName(context);
                    CommentFacade commentFacade = new CommentFacade();
                    commentFacade.setContend(commentContend);
                    commentFacade.setName(name);
                    if(blog.getCommentFacades()==null) {
                        List<CommentFacade> commentFacades = new ArrayList<CommentFacade>();
                        commentFacades.add(commentFacade);
                        blog.setCommentFacades(commentFacades);
                    }else{
                        List<CommentFacade> commentFacades = blog.getCommentFacades();
                        commentFacades.add(commentFacade);
                        blog.setCommentFacades(commentFacades);
                    }

                    CommentAdapter commentAdapter = new CommentAdapter(context, blog.getCommentFacades());
                    /*改变listView的高度*/
                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                    int w_sreen = displayMetrics.widthPixels;

                    int totalHeight = 0;
                    int listViewWidth = w_sreen-dip2px(context,16);
                    int widthSpec = View.MeasureSpec.makeMeasureSpec(listViewWidth,View.MeasureSpec.AT_MOST);
                    for(int i =0,len = commentAdapter.getCount();i<len;i++){
                        View listItem = commentAdapter.getView(i,null,null);
                        listItem.measure(widthSpec,0);
                        totalHeight = totalHeight+listItem.getMeasuredHeight();
                    }
                    ViewGroup.LayoutParams params = listView.getLayoutParams();
                    params.height = totalHeight +(listView.getDividerHeight() *(listView.getCount()-1));
                    listView.setLayoutParams(params);
                    listView.setAdapter(commentAdapter);

                    //进行网络传输
                     new Thread(issueCommentTask).start();
                }else{
                    Toast.makeText(context,"评论内容不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });


        commentText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s) && s.length()>2){
                    commentBt.setBackgroundColor(Color.parseColor("#FFB568"));
                }else {
                    commentBt.setBackgroundColor(Color.parseColor("#D8D8D8"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialog.show();
    }


    Runnable issueCommentTask = new Runnable() {
        @Override

        public void run() {
            // 在这里进行 http request.网络请求相关操作
            JSONObject jsonObject = new JSONObject();
            try {
                int blogId = blog.getId();
                String name  = InfoUtils.readLoginUserName(context);
                jsonObject.put("blogId",blogId);
                jsonObject.put("name",name);
                jsonObject.put("contend",commentTx);
                String ip = GetIp.getIpAddress();
                String url = "http://"+ip+":8080/blog/addComment.do";
                HttpRequest.requestByPost(url,jsonObject.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public int dip2px(Context context,float dipValue){
        final  float scale = context.getResources().getDisplayMetrics().density;
        return  (int)(dipValue*scale +0.5f);
    }

}
