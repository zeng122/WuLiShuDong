package com.example.wulishudong.netRequest;

import android.accounts.NetworkErrorException;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class HttpFileSend {
    public static String uploadFile(String url ,String[] picPaths,int num,int blogId){
        HttpURLConnection connection = null;
        String boundary  = UUID.randomUUID().toString();
        String prefix = "--";
        String lineEnd = "\r\n";
        URL url1 = null;
        try {
            url1 = new URL(url);
            connection = (HttpURLConnection) url1.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);  //设置连接网络超时为10秒
            connection.setDoOutput(true);         //允许输出流
            connection.setDoInput(true);          //允许输入流
            connection.setUseCaches(false);       //不允许使用缓存
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            // "multipart/form-data"这个参数来说明我们这传的是文件不是字符串了
            connection.setRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);

            DataOutputStream ds =
                    new DataOutputStream(connection.getOutputStream());
            JSONObject jsonObject = new JSONObject();


            //发送blogId
            StringBuffer sb1 = new StringBuffer();
            sb1.append(prefix);
            sb1.append(boundary);
            sb1.append(lineEnd);
            String blogIdStr = String.valueOf(blogId);
            sb1.append("Content-Disposition:form-data;name=\"blogId\";" + lineEnd);
            sb1.append("Content-Type:text/plain;charset=UTF-8"+lineEnd);
            sb1.append(lineEnd);
            ds.write(sb1.toString().getBytes());
            ds.write(blogIdStr.getBytes("utf-8"));
            ds.write(lineEnd.getBytes());

            //发送图片
            for(int i = 0 ; i<num; i++){
                File file = new File(picPaths[i]);
                StringBuffer sb = new StringBuffer();
                sb.append(prefix);
                sb.append(boundary);
                sb.append(lineEnd);
                sb.append("Content-Disposition:form-data;name=\"img\";" +
                        "filename=\""+file.getName()+"\""+lineEnd);
                sb.append("Content-Type:application/octet-stream;charset=UTF-8"+lineEnd);
                sb.append(lineEnd);
               // Log.i("-----------","---Header"+sb.toString());
                ds.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0 ;
                while((len = is.read(bytes))!= -1){
                    ds.write(bytes,0,len);
                }
                is.close();
                ds.write(lineEnd.getBytes());
            }
            byte[] end_data = (prefix+boundary+prefix+lineEnd).getBytes();
            ds.write(end_data);
            ds.flush();
            int responseCode = connection.getResponseCode();
            if(responseCode==200){
                return  "true";
            }else{
                throw new NetworkErrorException("response status is "+responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (connection != null) {
                connection.disconnect();// 关闭连接
            }
        }
        return  "false";
    }
}
