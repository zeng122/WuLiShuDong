package com.example.wulishudong.netRequest;

import android.accounts.NetworkErrorException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {
    public static String requestByPost(String url ,String contend) {
        HttpURLConnection connection = null;
        try {
            URL url1 = new URL(url);
            connection = (HttpURLConnection) url1.openConnection();

            connection.setRequestMethod("POST");
            connection.setReadTimeout(5000);  //设置读取超时为5秒
            connection.setConnectTimeout(10000);  //设置连接网络超时为10秒
            connection.setDoOutput(true);     //设置此方法，允许向服务器输出内容
            connection.setRequestProperty("ser-Agent", "Fiddler");
            connection.setRequestProperty("Content-Type","application/json");
            //post请求参数

            //获得一个输出流,向服务器写数据,默认情况下,系统不允许向服务器输出内容
            OutputStream out = connection.getOutputStream();
            out.write(contend.getBytes());
            out.flush();
            out.close();

            int responseCode = connection.getResponseCode();
            if(responseCode==200){
                InputStream is = connection.getInputStream();
                String response = getStringFromInputStream(is);
                return response;
            }else {
                throw new NetworkErrorException("response status is "+responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();// 关闭连接
            }
        }
        return  null;
    }


    private static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        is.close();
        String state = os.toString();// 把流中的数据转换成字符串,采用的编码是utf-8(模拟器默认编码)
        os.close();
        return state;
    }

}
