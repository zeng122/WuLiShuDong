package com.example.wulishudong.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

public class DongtaiFragment extends Fragment{
    private PullToRefreshListView refresh_lv;
    private List<Blog> list;
    private BlogAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dongtai, null);
        return view;
    }


    @Override
    //onViewCreated在onCreateView执行完后立即执行。
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh_lv = (PullToRefreshListView) view.findViewById(R.id.dongtai_fragment);
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
                new refrushAsyncTask(DongtaiFragment.this).execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                new LoadDataAsyncTask(DongtaiFragment.this).execute();
            }
        });

        //初始化界面
        new refrushAsyncTask(DongtaiFragment.this).execute();

        adapter = new BlogAdapter(getContext(),list);
        refresh_lv.setAdapter(adapter);

    }


    //下拉刷新
    private void refrushBlog(){
        list.clear();
        JSONObject jsonObject = new JSONObject();
        try {
            if(InfoUtils.readLoginStatus(getContext())){
                String name = InfoUtils.readLoginUserName(getContext());
                jsonObject.put("name",name);
                jsonObject.put("isLogin",true);
            }else{
                jsonObject.put("isLogin",false);
            }
            jsonObject.put("anonymous",false);
            String ip = GetIp.getIpAddress();
            String url = "http://"+ip+":8080/blog/refrushBlog.do";
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

               //如果未登录，则不读取自己点赞、喜欢状态
               if(InfoUtils.readLoginStatus(getContext())){
                   blog.setFavouState(jsonObjectIndex.optBoolean("favouState"));
                   blog.setLikeState(jsonObjectIndex.optBoolean("likeState"));
               }

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
            if(InfoUtils.readLoginStatus(getContext())){
                String name = InfoUtils.readLoginUserName(getContext());
                jsonObject.put("name",name);
                jsonObject.put("isLogin",true);
            }else{
                jsonObject.put("isLogin",false);
            }
            jsonObject.put("anonymous",false);
            Blog blogIndex = (Blog)list.get(list.size()-1);
            jsonObject.put("id",blogIndex.getId());

            String ip = GetIp.getIpAddress();
            String url = "http://"+ip+":8080/blog/loadMoreBlog.do";
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

                if(InfoUtils.readLoginStatus(getContext())){
                    blog.setFavouState(jsonObjectIndex.optBoolean("favouState"));
                    blog.setLikeState(jsonObjectIndex.optBoolean("likeState"));
                }

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
        private DongtaiFragment fragment;

        public refrushAsyncTask(DongtaiFragment fragment) {
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
    /*
    * AsyncTask<Params,Progress,Result>
    * Params 启动任务执行的输入参数，比如下载URL
    * Progress 后台任务执行的百分比，比如下载进度
    * Result 后台执行任务最终返回的结果，比如下载结果
    */
    private static class LoadDataAsyncTask extends AsyncTask<Void,Void,String> {
        private DongtaiFragment fragment;

        public LoadDataAsyncTask(DongtaiFragment fragment) {
            this.fragment = fragment;
        }

        /*
        * 在此方法中处理比较耗时的操作，比如下载文件
        * */
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
        /*
        * 非必须方法，可以不用实现
        * 此函数代表任务在线程池中执行结束了，回调给UI主线程的结果*/
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("success")){
                /* notifyDataSetInvalidated()，数据改变的同时，自动滑动到顶部第0条的位置
                   notifyDataSetChanged() , 会记住你划到的位置，重新加载数据的时候不会改变位置，
                                            只是改变了数据。
                * */
                fragment.adapter.notifyDataSetChanged();
                fragment.refresh_lv.onRefreshComplete();//刷新完成
            }
        }
    }
}
