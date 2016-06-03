package com.shawn.googlemarket.holder;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.shawn.googlemarket.R;
import com.shawn.googlemarket.bean.AppInfo;
import com.shawn.googlemarket.bean.DownloadInfo;
import com.shawn.googlemarket.manager.DownloadManager;
import com.shawn.googlemarket.utils.UIUtils;
import com.shawn.googlemarket.view.ProgressHorizontal;

import java.io.File;

/**
 * Created by shawn on 2016/5/21.
 */
public class DetailDownloadHolder extends BaseHolder<AppInfo> implements DownloadManager.DownloadObserver, View.OnClickListener {
    private Button btn_fav;
    private Button btn_share;
    private Button btn_download;
    private FrameLayout fl_progress;

    private ProgressHorizontal pbProgress;
    private DownloadManager mDM;
    private float mProgress;
    private int mCurrentState;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.layout_detail_download);
        btn_fav = (Button) view.findViewById(R.id.btn_fav);
        btn_share = (Button) view.findViewById(R.id.btn_share);
        btn_download = (Button) view.findViewById(R.id.btn_download);

        // 初始化自定义进度条
        fl_progress = (FrameLayout) view.findViewById(R.id.fl_progress);

        pbProgress = new ProgressHorizontal(UIUtils.getContext());
        pbProgress.setProgressBackgroundResource(R.drawable.progress_bg);// 进度条背景图片
        pbProgress.setProgressResource(R.drawable.progress_normal);// 进度条图片
        pbProgress.setProgressTextColor(Color.WHITE);// 进度文字颜色
        pbProgress.setProgressTextSize(UIUtils.dip2px(18));// 进度文字大小

        // 宽高填充父窗体
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // 给帧布局添加自定义进度条
        fl_progress.addView(pbProgress, params);

        btn_download.setOnClickListener(this);
        fl_progress.setOnClickListener(this);

        mDM = DownloadManager.getInstance();
        mDM.registerObserver(this);

        return view;
    }

    @Override
    public void refreshView(AppInfo data) {
        // 判断当前应用是否下载过
        DownloadInfo downloadInfo = mDM.getDownloadInfo(data);
        if(downloadInfo != null) {
            // 之前下载过
            mProgress = downloadInfo.getProgress();
            mCurrentState = downloadInfo.currentState;
        }else {
            // 没有下载过
            mCurrentState = DownloadManager.STATE_UNDO;
            mProgress = 0;
        }
        refreshUI(mCurrentState, mProgress);
    }

    private void refreshUI(int currentState, float progress) {
        mCurrentState = currentState;
        mProgress =progress;
        // 根据当前的下载进度和状态来更新界面
        switch (currentState) {
            case  DownloadManager.STATE_UNDO:
                fl_progress.setVisibility(View.GONE);
                btn_download.setVisibility(View.VISIBLE);
                btn_download.setText("下载");
                break;
            case DownloadManager.STATE_WAITING:
                fl_progress.setVisibility(View.GONE);
                btn_download.setVisibility(View.VISIBLE);
                btn_download.setText("等待下载");
                break;
            case DownloadManager.STATE_DOWNLOADING:
                fl_progress.setVisibility(View.VISIBLE);
                btn_download.setVisibility(View.GONE);
                pbProgress.setCenterText("");
                pbProgress.setProgress(progress);
                break;
            case DownloadManager.STATE_PAUSE:
                fl_progress.setVisibility(View.VISIBLE);
                btn_download.setVisibility(View.GONE);
                pbProgress.setCenterText("暂停");
                pbProgress.setProgress(progress);
                break;
            case DownloadManager.STATE_ERROR:
                fl_progress.setVisibility(View.GONE);
                btn_download.setVisibility(View.VISIBLE);
                btn_download.setText("下载失败");
                break;
            case DownloadManager.STATE_SUCCESS:
                fl_progress.setVisibility(View.GONE);
                btn_download.setVisibility(View.VISIBLE);
                btn_download.setText("安装");
                break;
        }
    }

    // 主线程更新ui
    private void refreshUIOnMainThread(final DownloadInfo downloadInfo){
        AppInfo data = getData();
        // 判断下载对象是否是当前应用
        if(data.id.equals(downloadInfo.id)) {
            UIUtils.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    refreshUI(downloadInfo.currentState,downloadInfo.getProgress());
                }
            });
        }
    }

    @Override
    public void onDownloadStateChanged(DownloadInfo downloadInfo) {
        refreshUIOnMainThread(downloadInfo);
    }

    @Override
    public void onDownloadProgressChanged(DownloadInfo downloadInfo) {
        refreshUIOnMainThread(downloadInfo);
    }

    @Override
    public void onClick(View v) {   
        switch (v.getId()) {
            case  R.id.btn_download:
            case  R.id.fl_progress:
                // 根据当前状态来决定下一步操作
                if(mCurrentState == DownloadManager.STATE_UNDO ||
                        mCurrentState == DownloadManager.STATE_ERROR ||
                        mCurrentState == DownloadManager.STATE_PAUSE) {
                    mDM.downLoad(getData());// 开始下载
                }else if(mCurrentState == DownloadManager.STATE_DOWNLOADING
                        || mCurrentState == DownloadManager.STATE_WAITING) {
                    mDM.pause(getData());;// 暂停下载
                }else if(mCurrentState == DownloadManager.STATE_SUCCESS) {
                    mDM.install(getData());// 开始安装
                }

                break;
            default:break;
        }
    }
}
