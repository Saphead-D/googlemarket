package com.shawn.googlemarket.http;

import android.util.Log;

import com.shawn.googlemarket.utils.IOUtils;
import com.shawn.googlemarket.utils.StreamUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shawn on 2016/5/17.
 */
public class HttpHelper {

    public static final String PATH = "http://127.0.0.1:8090/";

    public String getJsonFromServer(String path){

        HttpURLConnection connection = null;
        String json = null;
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            if(connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                json = StreamUtils.parserStreamUtil(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return json;
    }

}
