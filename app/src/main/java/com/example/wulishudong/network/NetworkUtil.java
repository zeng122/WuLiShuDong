package com.example.wulishudong.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

public class NetworkUtil {
    ConnectivityManager connectivityManager = null;

    public NetworkUtil(Context context){
        if(context!=null){
            connectivityManager = (ConnectivityManager) context.
                    getSystemService(Context.CONNECTIVITY_SERVICE);
        }
    }


    /*判断是否有网络连接*/
    public  boolean isNetConneted(){
            if(connectivityManager == null){
                return  false;
            }
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null ){
                return  networkInfo.isAvailable();
            }
        return  false;
    }

    /* 获取当前网络连接的类型信息*/
    public  int getConnectedType(Context context){
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isAvailable()){
            return networkInfo.getType();
        }
        return  -1;
    }


}
