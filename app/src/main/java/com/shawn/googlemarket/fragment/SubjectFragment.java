package com.shawn.googlemarket.fragment;

import android.view.View;

import com.shawn.googlemarket.adapter.MyBaseAdapter;
import com.shawn.googlemarket.bean.SubjectInfo;
import com.shawn.googlemarket.holder.BaseHolder;
import com.shawn.googlemarket.holder.SubjectHolder;
import com.shawn.googlemarket.http.protocol.SubjectProtocol;
import com.shawn.googlemarket.utils.UIUtils;
import com.shawn.googlemarket.view.LoadingPage;
import com.shawn.googlemarket.view.MyListView;

import java.util.ArrayList;

/**
 * Created by shawn on 2016/5/13.
 */
public class SubjectFragment extends BaseFragment {

    private ArrayList<SubjectInfo> data;

    @Override
    public View onCreateSuccessView() {
        MyListView view = new MyListView(UIUtils.getContext());
        view.setAdapter(new SubjectAdapter(data));
        return view;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        SubjectProtocol protocol = new SubjectProtocol();
        data = protocol.getData(0);
        return check(data);
    }

    class SubjectAdapter extends MyBaseAdapter<SubjectInfo>{

        public SubjectAdapter(ArrayList<SubjectInfo> data) {
            super(data);
        }

        @Override
        public BaseHolder<SubjectInfo> getHolder(int position) {
            return new SubjectHolder();
        }

        @Override
        public ArrayList<SubjectInfo> onLoadMore() {
            SubjectProtocol protocol = new SubjectProtocol();
            ArrayList<SubjectInfo> moreData = protocol.getData(getListSize());
            return moreData;
        }

    }
}
