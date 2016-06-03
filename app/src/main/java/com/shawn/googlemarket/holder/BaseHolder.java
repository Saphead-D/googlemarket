package com.shawn.googlemarket.holder;

import android.view.View;

/**
 * Created by shawn on 2016/5/14.
 */
public abstract class BaseHolder<T> {

    private final View mRootView;

    private T data;

    public BaseHolder(){
        //1.加载布局2.初始化控件
        mRootView = initView();
        //3.打标记
        mRootView.setTag(this);
    }

    public abstract View initView();

    public View getmRootView(){
        return mRootView;
    }
    //设置当前item的数据
    public void setData(T data){
        this.data = data;
        refreshView(data);
    }

    //获取当前item的数据
    public T getData(){
        return data;
    }
    //4.根据数据刷新界面
    public abstract void refreshView(T data);
}
