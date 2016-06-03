package com.shawn.googlemarket.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.shawn.googlemarket.HomeDetailActivity;
import com.shawn.googlemarket.adapter.MyBaseAdapter;
import com.shawn.googlemarket.bean.AppInfo;
import com.shawn.googlemarket.holder.BaseHolder;
import com.shawn.googlemarket.holder.HomeHeadHolder;
import com.shawn.googlemarket.holder.HomeHolder;
import com.shawn.googlemarket.http.protocol.HomeProtocol;
import com.shawn.googlemarket.utils.UIUtils;
import com.shawn.googlemarket.view.LoadingPage;
import com.shawn.googlemarket.view.MyListView;

import java.util.ArrayList;

/**
 * Created by shawn on 2016/5/13.
 */
public class HomeFragment extends BaseFragment{

    private ArrayList<AppInfo> data;
    private HomeAdapter homeAdapter;
    private ArrayList<String> pictureList;

    @Override
    public View onCreateSuccessView() {
        MyListView view = new MyListView(UIUtils.getContext());
        homeAdapter = new HomeAdapter(data);

        HomeHeadHolder headHolder = new HomeHeadHolder();
        view.addHeaderView(headHolder.getmRootView());

        if(pictureList != null) {
            headHolder.setData(pictureList);// 设置轮播条数据
        }
        view.setAdapter(homeAdapter);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo appInfo = data.get(position - 1);// 去掉头布局
                if(appInfo != null) {
                    Intent intent = new Intent(UIUtils.getContext(), HomeDetailActivity.class);
                    intent.putExtra("packageName",appInfo.packageName);
                    startActivity(intent);
                }
            }
        });
        return view;
    }


    @Override
    public LoadingPage.ResultState onLoad() {
        HomeProtocol protocol = new HomeProtocol();
        data = protocol.getData(0);
        pictureList = protocol.getPictureList();
        return check(this.data);// 校验数据并返回
    }


    class HomeAdapter extends MyBaseAdapter<AppInfo>{

        public HomeAdapter(ArrayList<AppInfo> data) {
            super(data);
        }

        @Override
        public BaseHolder<AppInfo> getHolder(int position) {

            return new HomeHolder();
        }

        @Override
        public ArrayList<AppInfo> onLoadMore() {
            HomeProtocol protocol = new HomeProtocol();
            ArrayList<AppInfo> moreData = protocol.getData(getListSize());
            return moreData;
        }
    }
}
