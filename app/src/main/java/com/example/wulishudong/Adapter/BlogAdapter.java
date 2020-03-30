package com.example.wulishudong.Adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wulishudong.R;
import com.example.wulishudong.bean.Blog;
import com.example.wulishudong.netRequest.HttpRequest;
import com.example.wulishudong.util.CommentDialog;
import com.example.wulishudong.util.GetIp;
import com.example.wulishudong.util.InfoUtils;
import com.example.wulishudong.util.JuBaoDialog;

import org.json.JSONObject;

import java.util.List;

public class BlogAdapter extends BaseAdapter {
    private Context context;
    private List<Blog> list;
    private CommentAdapter commentAdapter;
    public BlogAdapter(Context context, List<Blog> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null){
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        OnClick listener = null;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.dongtai_item,parent,false);
            vh = new ViewHolder();
            vh.dt_head_pic = (ImageView) convertView.findViewById(R.id.dt_head_pic);
            vh.dt_head_name = (TextView) convertView.findViewById(R.id.dt_head_name);
            vh.dt_head_date = (TextView) convertView.findViewById(R.id.dt_head_date);
            vh.dt_main_text =(TextView)  convertView.findViewById(R.id.dt_main_text);
            vh.dt_main_pic = (ImageView) convertView.findViewById(R.id.dt_main_pic);
            vh.do_comment = (TextView) convertView.findViewById(R.id.do_comment);
            vh.do_good    = (ImageView) convertView.findViewById(R.id.do_good);
            vh.do_star    = (ImageView) convertView.findViewById(R.id.do_star);

            vh.star_num   = (TextView) convertView.findViewById(R.id.star_num);
            vh.good_num   =(TextView) convertView.findViewById(R.id.good_num);
            vh.comment_list =(ListView)convertView.findViewById(R.id.comment_list);
            vh.tx_jubao  = (TextView)convertView.findViewById(R.id.tx_jubao);

            listener = new OnClick();

            vh.do_comment.setOnClickListener(listener);
            vh.do_good.setOnClickListener(listener);
            vh.do_star.setOnClickListener(listener);
            vh.tx_jubao.setOnClickListener(listener);

            convertView.setTag(vh);
            convertView.setTag(vh.do_comment.getId(),listener); //保存监听对象
        }else{
            vh = (ViewHolder) convertView.getTag();
            listener = (OnClick) convertView.getTag(vh.do_comment.getId());  //重新获得监听
        }
        Blog blog = (Blog) getItem(position);
        vh.dt_head_pic.setImageResource(R.drawable.ic_head);

        if(blog.isAnonymous()){
            vh.dt_head_name.setText("匿名用户");
        }else{
            vh.dt_head_name.setText(blog.getName());
        }

        vh.dt_head_date.setText(blog.getTime());
        vh.dt_main_text.setText(blog.getContend());
        if(blog.getCommentFacades()!=null) {
            commentAdapter = new CommentAdapter(context, blog.getCommentFacades());
            /*改变listView的高度*/
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int w_sreen = displayMetrics.widthPixels;

            int totalHeight = 0;
            int listViewWidth = w_sreen-dip2px(context,16);
            //父容器指定了一个可用大小即SpecSize,View的大小不能大于这个值，具体是什么值要看不同View的具体实现
            int widthSpec = View.MeasureSpec.makeMeasureSpec(listViewWidth,View.MeasureSpec.AT_MOST);
            for(int i =0,len = commentAdapter.getCount();i<len;i++){
                View listItem = commentAdapter.getView(i,null,null);
                listItem.measure(widthSpec,0);

                totalHeight = totalHeight+listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = vh.comment_list.getLayoutParams();
            params.height = totalHeight +(vh.comment_list.getDividerHeight() *(vh.comment_list.getCount()-1));
            vh.comment_list.setLayoutParams(params);
            vh.comment_list.setAdapter(commentAdapter);
        }
        vh.good_num.setText(Integer.toString(blog.getFavounum()));
        vh.star_num.setText(Integer.toString(blog.getLikenum()));

        if(InfoUtils.readLoginStatus(context)) {
            if (blog.isFavouState()) {
                vh.do_good.setImageResource(R.drawable.good_checked);
            } else {
                vh.do_good.setImageResource(R.drawable.good);
            }

            if (blog.isLikeState()) {
                vh.do_star.setImageResource(R.drawable.collection_checked);
            } else {
                vh.do_star.setImageResource(R.drawable.collection);
            }
        }
        listener.setPosition(position);
        listener.setVH(vh);
        return convertView;
    }

    class OnClick implements View.OnClickListener{

        int position;
        ViewHolder vh;
        public  void setVH(ViewHolder vh){
            this.vh = vh;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tx_jubao:
                    if(InfoUtils.readLoginStatus(context)){
                        Blog blog = list.get(position);
                        new JuBaoDialog(context,blog);
                    }else{
                        Toast.makeText(context,"当前未登录",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.do_comment:
                    if(InfoUtils.readLoginStatus(context)) {
                        Blog blog = list.get(position);
                        new CommentDialog(context, blog, vh.comment_list);
                    }else{
                        Toast.makeText(context,"当前未登录",Toast.LENGTH_SHORT).show();
                    }
                    //进行网络传输
                    break;
                case R.id.do_good:
                    if(InfoUtils.readLoginStatus(context)) {
                        if (list.get(position).isFavouState()) {
                            vh.do_good.setImageResource(R.drawable.good);
                            list.get(position).setFavouState(false);
                            vh.good_num.setText(Integer.toString(Integer.parseInt(vh.good_num.getText().toString()) - 1));
                            new Thread(nofavouTask).start();
                        } else {
                            vh.do_good.setImageResource(R.drawable.good_checked);
                            list.get(position).setFavouState(true);
                            vh.good_num.setText(Integer.toString(Integer.parseInt(vh.good_num.getText().toString()) + 1));
                            new Thread(favouTask).start();
                        }
                    }else{
                        Toast.makeText(context,"当前未登录",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.do_star:
                    if(InfoUtils.readLoginStatus(context)) {
                        if (list.get(position).isLikeState()) {
                            vh.do_star.setImageResource(R.drawable.collection);
                            list.get(position).setLikeState(false);
                            vh.star_num.setText(Integer.toString(Integer.parseInt(vh.star_num.getText().toString()) - 1));
                            new Thread(nolikeTask).start();
                        } else {
                            vh.do_star.setImageResource(R.drawable.collection_checked);
                            list.get(position).setLikeState(true);
                            vh.star_num.setText(Integer.toString(Integer.parseInt(vh.star_num.getText().toString()) + 1));
                            new Thread(likeTask).start();
                        }
                    }else{
                        Toast.makeText(context,"当前未登录",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

        Runnable likeTask = new Runnable() {
            @Override

            public void run() {
                // 在这里进行 http request.网络请求相关操作
                JSONObject jsonObject = new JSONObject();
                try {
                    int id = list.get(position).getId();
                    String name  = InfoUtils.readLoginUserName(context);
                    jsonObject.put("id",id);
                    jsonObject.put("name",name);
                    jsonObject.put("isLike",true);

                    String ip = GetIp.getIpAddress();
                    String url = "http://"+ip+":8080/blog/likeBlog.do";
                    HttpRequest.requestByPost(url,jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        Runnable nolikeTask = new Runnable() {
            @Override

            public void run() {
                // 在这里进行 http request.网络请求相关操作
                JSONObject jsonObject = new JSONObject();
                try {
                    int id = list.get(position).getId();
                    String name  = InfoUtils.readLoginUserName(context);
                    jsonObject.put("id",id);
                    jsonObject.put("name",name);
                    jsonObject.put("isLike",false);

                    String ip = GetIp.getIpAddress();
                    String url = "http://"+ip+":8080/blog/likeBlog.do";
                    HttpRequest.requestByPost(url,jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable favouTask = new Runnable() {
            @Override

            public void run() {
                // 在这里进行 http request.网络请求相关操作
                JSONObject jsonObject = new JSONObject();
                try {
                    int id = list.get(position).getId();
                    String name  = InfoUtils.readLoginUserName(context);
                    jsonObject.put("id",id);
                    jsonObject.put("name",name);
                    jsonObject.put("isFavou",true);

                    String ip = GetIp.getIpAddress();
                    String url = "http://"+ip+":8080/blog/favouBlog.do";
                    HttpRequest.requestByPost(url,jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Runnable nofavouTask = new Runnable() {
            @Override

            public void run() {
                // 在这里进行 http request.网络请求相关操作
                JSONObject jsonObject = new JSONObject();
                try {
                    int id = list.get(position).getId();
                    String name  = InfoUtils.readLoginUserName(context);
                    jsonObject.put("id",id);
                    jsonObject.put("name",name);
                    jsonObject.put("isFavou",false);

                    String ip = GetIp.getIpAddress();
                    String url = "http://"+ip+":8080/blog/favouBlog.do";
                    HttpRequest.requestByPost(url,jsonObject.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

    }



    class ViewHolder{
        ImageView dt_head_pic;
        TextView dt_head_name;
        TextView dt_head_date;
        TextView dt_main_text;
        ImageView dt_main_pic;
        TextView do_comment;
        ImageView do_good;
        ImageView do_star;
        TextView star_num;
        TextView good_num;
        ListView comment_list;
        TextView tx_jubao;
    }


    public static  int dip2px(Context context,float dipValue){
        final  float scale = context.getResources().getDisplayMetrics().density;
        return  (int)(dipValue*scale +0.5f);
    }
}
