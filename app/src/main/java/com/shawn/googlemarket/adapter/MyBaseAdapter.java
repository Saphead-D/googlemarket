package com.shawn.googlemarket.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.shawn.googlemarket.holder.BaseHolder;
import com.shawn.googlemarket.holder.MoreHolder;
import com.shawn.googlemarket.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by shawn on 2016/5/14.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {

    private static int TYPE_MORE = 0;
    private static int TYPE_NORMAL = 1;
    private ArrayList<T> data;
    public MyBaseAdapter(ArrayList<T> data){
        this.data = data;
    }
    @Override
    public int getCount() {
        return data.size() + 1;
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == getCount() - 1) {
            return TYPE_MORE;
        }else {
            return getInnerType(position);
        }
    }

    //子类可以重写更改返回类型
    public int getInnerType(int position){
        return TYPE_NORMAL;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder holder;
        if(convertView == null) {
            if(getItemViewType(position) == TYPE_MORE) {
                holder = new MoreHolder(hasMore());
            }else {
                holder = getHolder(position);
            }
        }else {
            holder = (BaseHolder) convertView.getTag();
        }

        if(getItemViewType(position) != TYPE_MORE) {
            holder.setData(getItem(position));
        }else {
            //加载更多数据

            // 一旦加载更多布局展示出来, 就开始加载更多
            // 只有在有更多数据的状态下才加载更多
            MoreHolder moreHolder = (MoreHolder) holder;
            if(moreHolder.getData() == MoreHolder.STATE_MORE_MORE) {

                loadMore(moreHolder);
            }
        }
        return holder.getmRootView();
    }

    public boolean hasMore(){
        return true;
    }

    // 返回当前页面的holder对象, 必须子类实现
    public abstract BaseHolder<T> getHolder(int position);

    public boolean isLoadMore = false;// 标记是否正在加载更多

    public void loadMore(final MoreHolder m){
        if(!isLoadMore) {
            isLoadMore = true;
            new Thread(){
                @Override
                public void run() {
                    final ArrayList<T> more = onLoadMore();
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if(more != null) {
                                if(more.size() < 20) {
                                    m.setData(MoreHolder.STATE_MORE_NONE);
                                    Toast.makeText(UIUtils.getContext(),
                                            "没有更多数据了", Toast.LENGTH_SHORT)
                                            .show();
                                }else {
                                    m.setData(MoreHolder.STATE_MORE_MORE);
                                }
                                // 将更多数据追加到当前集合中
                                data.addAll(more);
                                // 刷新界面
                                MyBaseAdapter.this.notifyDataSetChanged();

                            }else {
                                m.setData(MoreHolder.STATE_MORE_ERROR);
                            }
                            isLoadMore = false;
                        }
                    });
                }
            }.start();
        }
    }
    public abstract ArrayList<T> onLoadMore();

    public int getListSize(){
        return data.size();
    }
}
