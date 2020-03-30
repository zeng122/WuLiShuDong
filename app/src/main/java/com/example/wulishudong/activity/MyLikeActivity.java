package com.example.wulishudong.activity;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wulishudong.Adapter.BlogAdapter;
import com.example.wulishudong.R;
import com.example.wulishudong.bean.Blog;
import com.example.wulishudong.facade.CommentFacade;
import com.example.wulishudong.netRequest.HttpRequest;
import com.example.wulishudong.util.GetIp;
import com.example.wulishudong.util.InfoUtils;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyLikeActivity extends AppCompatActivity {
    private TextView itemTitle;
    private TextView backIcon;
    private ListView myLike_list;
    private List<Blog> list;
    private BlogAdapter adapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylike);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        init();

        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLikeActivity.this.finish();
            }
        });

        new Thread(myLikeTask).start();

    }


    private  void init(){
        itemTitle = findViewById(R.id.item_title);
        backIcon = findViewById(R.id.back_to_page);
        itemTitle.setText("我的收藏");
        myLike_list = findViewById(R.id.myLike_list);
        list = new ArrayList<>();
    }

    Handler myLikeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果为-->" + val);
            // UI界面的更新等相关操作

            try {
                JSONArray jsonArray = new JSONArray(val);
                for(int i = 0;i<jsonArray.length();i++) {
                    JSONObject jsonObjectIndex = (JSONObject) jsonArray.get(i);
                    Blog blog = new Blog();
                    blog.setContend(jsonObjectIndex.optString("contend"));
                    blog.setName(jsonObjectIndex.optString("name"));
                    blog.setTime(jsonObjectIndex.optString("time"));
                    blog.setId(jsonObjectIndex.optInt("id"));
                    blog.setFavounum(jsonObjectIndex.optInt("favounum"));
                    blog.setLikenum(jsonObjectIndex.optInt("likenum"));


                    blog.setFavouState(jsonObjectIndex.optBoolean("favouState"));
                    blog.setLikeState(jsonObjectIndex.optBoolean("likeState"));


                    blog.setAnonymous(jsonObjectIndex.optBoolean("anonymous"));
                    JSONArray jsonArray1 = jsonObjectIndex.optJSONArray("commentFacades");

                    List<CommentFacade> commentFacades = new ArrayList<CommentFacade>();
                    for (int j = 0; j < jsonArray1.length(); j++) {
                        CommentFacade commentFacade = new CommentFacade();
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                        commentFacade.setName(jsonObject1.optString("name"));
                        commentFacade.setContend(jsonObject1.optString("contend"));
                        commentFacades.add(commentFacade);
                    }
                    blog.setCommentFacades(commentFacades);
                    list.add(blog);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter = new BlogAdapter(getApplicationContext(),list);
            myLike_list.setAdapter(adapter);
        }
    };

    Runnable myLikeTask = new Runnable() {
        @Override
        public void run() {
            // 在这里进行 http request.网络请求相关操作
              JSONObject jsonObject = new JSONObject();
                try {
                    String name  = InfoUtils.readLoginUserName(getApplicationContext());
                    jsonObject.put("name",name);
                    String ip = GetIp.getIpAddress();
                    String url = "http://"+ip+":8080/user/myLikeBlog.do";
                    String res = HttpRequest.requestByPost(url,jsonObject.toString());
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("value", res);
                    msg.setData(data);
                    myLikeHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
    };
}
