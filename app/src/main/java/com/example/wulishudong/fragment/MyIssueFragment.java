package com.example.wulishudong.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.wulishudong.Adapter.BlogAdapter;
import com.example.wulishudong.R;
import com.example.wulishudong.bean.Blog;
import com.example.wulishudong.facade.CommentFacade;
import com.example.wulishudong.netRequest.HttpRequest;
import com.example.wulishudong.util.GetIp;
import com.example.wulishudong.util.InfoUtils;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyIssueFragment extends Fragment {

    private PullToRefreshListView refresh_lv;
    private List<Blog> list;
    private BlogAdapter adapter;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, null);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(InfoUtils.readLoginStatus(getContext())){
        refresh_lv = (PullToRefreshListView) view.findViewById(R.id.my_fragment);
        list = new ArrayList<>();
        //设置可上拉刷新和下拉刷新
        refresh_lv.setMode(PullToRefreshBase.Mode.BOTH);

        //设置刷新时显示的文本
        ILoadingLayout startLayout = refresh_lv.getLoadingLayoutProxy(true,false);
        startLayout.setPullLabel("正在下拉刷新...");
        startLayout.setRefreshingLabel("正在玩命加载中...");
        startLayout.setReleaseLabel("放开以刷新");

        ILoadingLayout endLayout = refresh_lv.getLoadingLayoutProxy(false,true);
        endLayout.setPullLabel("正在上拉加载...");
        endLayout.setRefreshingLabel("正在玩命加载中...");
        endLayout.setReleaseLabel("放开以加载");

        refresh_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                new refrushAsyncTask(MyIssueFragment.this).execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                new LoadDataAsyncTask(MyIssueFragment.this).execute();
            }
        });


        //初始化界面
        new refrushAsyncTask(MyIssueFragment.this).execute();

        adapter = new BlogAdapter(getContext(),list);
        refresh_lv.setAdapter(adapter);
        }else {
            Toast.makeText(getContext(),"当前未登录",Toast.LENGTH_SHORT).show();
        }
    }


    //下拉刷新
    private void refrushBlog(){
        list.clear();
        JSONObject jsonObject = new JSONObject();
        try {
            String name = InfoUtils.readLoginUserName(getContext());
            jsonObject.put("name",name);
            String ip = GetIp.getIpAddress();
            String url = "http://"+ip+":8080/blog/refrushMyBlog.do";
            String res = HttpRequest.requestByPost(url,jsonObject.toString());

            JSONArray jsonArray = new JSONArray(res);
            for(int i = 0;i<jsonArray.length();i++){
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
                for(int j = 0 ; j <jsonArray1.length();j++){
                    CommentFacade commentFacade = new CommentFacade();
                    JSONObject jsonObject1 =jsonArray1.getJSONObject(j);
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

    }

    //上拉加载更多
    private void loadMoreData(){
        JSONObject jsonObject = new JSONObject();
        try {
            String name = InfoUtils.readLoginUserName(getContext());
            jsonObject.put("name",name);
            Blog blogIndex = (Blog)list.get(list.size()-1);
            jsonObject.put("id",blogIndex.getId());
            String ip = GetIp.getIpAddress();
            String url = "http://"+ip+":8080/blog/loadMoreMyBlog.do";
            String res = HttpRequest.requestByPost(url,jsonObject.toString());

            JSONArray jsonArray = new JSONArray(res);
            for(int i = 0;i<jsonArray.length();i++){
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
                for(int j = 0 ; j <jsonArray1.length();j++){
                    CommentFacade commentFacade = new CommentFacade();
                    JSONObject jsonObject1 =jsonArray1.getJSONObject(j);
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
    }

    private static class refrushAsyncTask extends AsyncTask<Void,Void,String> {
        private MyIssueFragment fragment;

        public refrushAsyncTask(MyIssueFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                fragment.refrushBlog();
                return "success";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("success")){
                fragment.adapter.notifyDataSetChanged();
                fragment.refresh_lv.onRefreshComplete();//刷新完成
            }
        }
    }



    /**
     * 异步下载任务
     */
    private static class LoadDataAsyncTask extends AsyncTask<Void,Void,String> {
        private MyIssueFragment fragment;

        public LoadDataAsyncTask(MyIssueFragment fragment) {
            this.fragment = fragment;
        }
        @Override
        protected String doInBackground(Void... params) {
            try {
                fragment.loadMoreData();
                return "success";
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 完成时的方法
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("success")){
                fragment.adapter.notifyDataSetChanged();
                fragment.refresh_lv.onRefreshComplete();//刷新完成
            }
        }
    }
}
