package com.shawn.googlemarket.fragment;

import android.view.View;

import com.shawn.googlemarket.adapter.MyBaseAdapter;
import com.shawn.googlemarket.bean.CategoryInfo;
import com.shawn.googlemarket.holder.BaseHolder;
import com.shawn.googlemarket.holder.CategoryHolder;
import com.shawn.googlemarket.holder.TitleHolder;
import com.shawn.googlemarket.http.protocol.CategoryProtocol;
import com.shawn.googlemarket.utils.UIUtils;
import com.shawn.googlemarket.view.LoadingPage;
import com.shawn.googlemarket.view.MyListView;

import java.util.ArrayList;

/**
 * Created by shawn on 2016/5/13.
 */
public class CategoryFragment extends BaseFragment {

    private ArrayList<CategoryInfo> data;

    @Override
    public View onCreateSuccessView() {
        MyListView view = new MyListView(UIUtils.getContext());
        view.setAdapter(new CategoryAdapter(data));
        return view;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        CategoryProtocol protocol = new CategoryProtocol();
        data = protocol.getData(0);
        return check(data);
    }

    class CategoryAdapter extends MyBaseAdapter<CategoryInfo>{

        public CategoryAdapter(ArrayList<CategoryInfo> data) {
            super(data);
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;// 在原来基础上增加一种标题类型
        }

        @Override
        public int getInnerType(int position) {
            // 判断是标题类型还是普通分类类型
            CategoryInfo info = data.get(position);
            if(info.isTitle) {
                return super.getInnerType(position) + 1;
            }else {
                return super.getInnerType(position);
            }
        }

        @Override
        public BaseHolder<CategoryInfo> getHolder(int position) {
            // 判断是标题类型还是普通分类类型, 来返回不同的holder
            CategoryInfo info = data.get(position);
            if(info.isTitle) {
                return new TitleHolder();
            }else {
                return new CategoryHolder();
            }
        }

        @Override
        public boolean hasMore() {
            return false;
        }

        @Override
        public ArrayList<CategoryInfo> onLoadMore() {
            return null;
        }
    }
}
