package com.example.wulishudong.bean;


import com.example.wulishudong.facade.CommentFacade;

import java.util.List;

public class Blog {
    private int id;
    private String name;
    private String contend;
    private String time;
    private int likenum;
    private int favounum;
    private boolean likeState;
    private boolean favouState;
    private List<CommentFacade> commentFacades = null;
    private boolean anonymous;

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public void setCommentFacades(List<CommentFacade> commentFacades) {
        this.commentFacades = commentFacades;
    }

    public List<CommentFacade> getCommentFacades() {
        return commentFacades;
    }

    public void setLikeState(boolean likeState) {
        this.likeState = likeState;
    }

    public void setFavouState(boolean favouState) {
        this.favouState = favouState;
    }

    public boolean isLikeState() {
        return likeState;
    }

    public boolean isFavouState() {
        return favouState;
    }

    public void setLikenum(int likenum) {
        this.likenum = likenum;
    }

    public void setFavounum(int favounum) {
        this.favounum = favounum;
    }

    public int getLikenum() {
        return likenum;
    }

    public int getFavounum() {
        return favounum;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContend() {
        return contend;
    }

    public String getTime() {
        return time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContend(String contend) {
        this.contend = contend;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

