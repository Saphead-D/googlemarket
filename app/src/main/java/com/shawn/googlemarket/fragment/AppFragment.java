package com.shawn.googlemarket.fragment;

import android.view.View;

import com.shawn.googlemarket.adapter.MyBaseAdapter;
import com.shawn.googlemarket.bean.AppInfo;
import com.shawn.googlemarket.holder.AppHolder;
import com.shawn.googlemarket.holder.BaseHolder;
import com.shawn.googlemarket.http.protocol.AppProtocol;
import com.shawn.googlemarket.utils.UIUtils;
import com.shawn.googlemarket.view.LoadingPage;
import com.shawn.googlemarket.view.MyListView;

import java.util.ArrayList;

/**
 * Created by shawn on 2016/5/13.
 */
public class AppFragment extends BaseFragment {

    private ArrayList<AppInfo> data;

    @Override
    public View onCreateSuccessView() {
       /* TextView view = new TextView(UIUtils.getContext());
        view.setText("应用");
        view.setGravity(Gravity.CENTER);*/
        MyListView view = new MyListView(UIUtils.getContext());
        view.setAdapter(new AppAdapter(data));
        return view;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        AppProtocol protocol = new AppProtocol();
        data = protocol.getData(0);
        return check(data);
    }

    class AppAdapter extends MyBaseAdapter<AppInfo>{

        public AppAdapter(ArrayList<AppInfo> data) {
            super(data);
        }

        @Override
        public BaseHolder<AppInfo> getHolder(int position) {
            return new AppHolder();
        }

        @Override
        public ArrayList<AppInfo> onLoadMore() {
            AppProtocol protocol = new AppProtocol();
            ArrayList<AppInfo> moreData = protocol.getData(getListSize());
            return moreData;
        }
    }
}
