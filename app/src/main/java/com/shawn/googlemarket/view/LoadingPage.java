package com.shawn.googlemarket.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.shawn.googlemarket.R;
import com.shawn.googlemarket.utils.LogUtils;
import com.shawn.googlemarket.utils.UIUtils;

/**
 * Created by shawn on 2016/5/13.
 */
public abstract class LoadingPage extends FrameLayout{

    private static final int STATE_LOAD_UNDO = 1;// 未加载
    private static final int STATE_LOAD_LOADING = 2;// 正在加载
    private static final int STATE_LOAD_ERROR = 3;// 加载失败
    private static final int STATE_LOAD_EMPTY = 4;// 数据为空
    private static final int STATE_LOAD_SUCCESS = 5;// 加载成功

    private int mCurrentState = STATE_LOAD_UNDO;// 当前状态

    private View mLoadingPage;
    private View mErrorPage;
    private View mEmptyPage;
    private View mSuccessPage;


    public LoadingPage(Context context) {
        super(context);
        initView();
    }

    public LoadingPage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LoadingPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView(){
        if(mLoadingPage == null) {
            mLoadingPage = UIUtils.inflate(R.layout.page_loading);
            addView(mLoadingPage);
        }
        if(mErrorPage == null) {
            mErrorPage = UIUtils.inflate(R.layout.page_error);
            addView(mErrorPage);
        }
        if(mEmptyPage == null) {
            mEmptyPage = UIUtils.inflate(R.layout.page_empty);
            addView(mEmptyPage);
        }
        showRightPage();
    }

    // 根据当前状态,决定显示哪个布局
    private void showRightPage() {

        mLoadingPage.setVisibility(mCurrentState == STATE_LOAD_UNDO ||mCurrentState == STATE_LOAD_LOADING?VISIBLE:GONE);
        mErrorPage.setVisibility(mCurrentState == STATE_LOAD_ERROR?VISIBLE:GONE);
        mEmptyPage.setVisibility(mCurrentState == STATE_LOAD_EMPTY ? VISIBLE : GONE);
        if(mSuccessPage == null && mCurrentState == STATE_LOAD_SUCCESS) {
            mSuccessPage = onCreateSuccessView();
            if(mSuccessPage != null) {
                addView(mSuccessPage);
            }
        }
        if(mSuccessPage != null) {
            mSuccessPage.setVisibility(mCurrentState == STATE_LOAD_SUCCESS ? VISIBLE : GONE);
        }
    }

    public void loadData(){
        if(mCurrentState != STATE_LOAD_LOADING) {
            mCurrentState = STATE_LOAD_LOADING;
            new Thread(){
                @Override
                public void run() {
                    final ResultState resultState = onLoad();
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if(resultState != null) {
                                mCurrentState = resultState.getState();// 网络加载结束后,更新网络状态
                                // 根据最新的状态来刷新页面
                                showRightPage();
                            }
                        }
                    });
                }
            }.start();
        }
    }

    // 加载成功后显示的布局, 必须由调用者来实现
    public abstract View onCreateSuccessView();

    // 加载网络数据, 返回值表示请求网络结束后的状态
    public abstract ResultState onLoad();

    public enum ResultState{
        STATE_SUCCESS(STATE_LOAD_SUCCESS),STATE_EMPTY(STATE_LOAD_EMPTY),
        STATE_ERROR(STATE_LOAD_ERROR);

        private int state;
        private ResultState(int state){
            this.state = state;
        }

        public int getState(){
            return state;
        }
    }
}
