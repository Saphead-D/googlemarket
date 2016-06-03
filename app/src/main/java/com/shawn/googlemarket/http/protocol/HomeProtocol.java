package com.shawn.googlemarket.http.protocol;

import com.shawn.googlemarket.bean.AppInfo;
import com.shawn.googlemarket.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by shawn on 2016/5/14.
 */
public class HomeProtocol extends BaseProtocol<ArrayList<AppInfo>>{

    private ArrayList<String> pics;

    @Override
    public String getParams() {
        return "";// 如果没有参数,就传空串,不要传null
    }

    @Override
    public String getKey() {
        return "home";
    }

    @Override
    public ArrayList<AppInfo> parseData(String result) {
        // 使用JsonObject解析方式: 如果遇到{},就是JsonObject;如果遇到[], 就是JsonArray
        try {
            JSONObject jo = new JSONObject(result);
            // 解析应用列表数据
            JSONArray ja = jo.getJSONArray("list");
            ArrayList<AppInfo> appInfoList = new ArrayList<>();
            for (int i = 0; i < ja.length(); i++) {
                JSONObject object = ja.getJSONObject(i);
                AppInfo info = new AppInfo();
                info.des = object.getString("des");
                info.downloadUrl = object.getString("downloadUrl");
                info.iconUrl = object.getString("iconUrl");
                info.id = object.getString("id");
                info.name = object.getString("name");
                info.packageName = object.getString("packageName");
                info.size = object.getLong("size");
                info.stars = object.getDouble("stars");
                appInfoList.add(info);
            }

            // 初始化轮播条的数据
            JSONArray japic = jo.getJSONArray("picture");
            pics = new ArrayList<>();
            for (int i = 0; i < japic.length(); i++){
                String pic = japic.getString(i);
                pics.add(pic);
            }
            return appInfoList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getPictureList() {
        return pics;
    }
}
