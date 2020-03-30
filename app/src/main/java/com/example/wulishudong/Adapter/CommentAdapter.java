package com.example.wulishudong.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wulishudong.R;

import com.example.wulishudong.facade.CommentFacade;

import java.util.List;

public class CommentAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<CommentFacade> commentFacades ;

    public  CommentAdapter(Context context ,List<CommentFacade> commentFacades){
        this.context = context;
        this.commentFacades= commentFacades;
        this.layoutInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return commentFacades == null? 0 :commentFacades.size();
    }

    @Override
    public Object getItem(int position) {
        return  commentFacades.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = layoutInflater.inflate(R.layout.comment_list_item,null);
            convertView.setTag(new ViewHolder(convertView));
        }

        initializeViews((CommentFacade)getItem(position),(ViewHolder)convertView.getTag(),
                position,convertView);
        return  convertView;
    }

    private void initializeViews(CommentFacade commentFacade, ViewHolder holder,
                                 int position, View convertView) {
       final CommentFacade comment = (CommentFacade) getItem(position);
       if(comment != null){
           holder.comment_name.setText(comment.getName());
           holder.comment_contend.setText(comment.getContend());
       }

    }

    class ViewHolder{
        private ImageView comment_head_pic;
        private TextView comment_name;
        private TextView comment_contend;

        public ViewHolder(View view) {
            comment_head_pic = (ImageView)view.findViewById(R.id.comment_head_pic);
            comment_name  = (TextView)view.findViewById(R.id.comment_name);
            comment_contend = (TextView)view.findViewById(R.id.comment_contend);
        }
    }
}
