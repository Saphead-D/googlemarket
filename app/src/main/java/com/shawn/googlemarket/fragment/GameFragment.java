package com.shawn.googlemarket.fragment;

import android.view.View;

import com.shawn.googlemarket.view.LoadingPage;

/**
 * Created by shawn on 2016/5/13.
 */
public class GameFragment extends BaseFragment {
    @Override
    public View onCreateSuccessView() {
        return null;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        return LoadingPage.ResultState.STATE_EMPTY;
    }
}
