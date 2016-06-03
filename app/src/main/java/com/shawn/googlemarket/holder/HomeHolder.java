package com.shawn.googlemarket.holder;

import android.text.format.Formatter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.shawn.googlemarket.R;
import com.shawn.googlemarket.bean.AppInfo;
import com.shawn.googlemarket.bean.DownloadInfo;
import com.shawn.googlemarket.http.HttpHelper;
import com.shawn.googlemarket.manager.DownloadManager;
import com.shawn.googlemarket.utils.BitmapHelper;
import com.shawn.googlemarket.utils.UIUtils;
import com.shawn.googlemarket.view.ProgressArc;

/**
 * Created by shawn on 2016/5/14.
 */
public class HomeHolder extends BaseHolder<AppInfo> implements DownloadManager.DownloadObserver, View.OnClickListener {

    private TextView tv_name;
    private ImageView iv_icon;
    private BitmapUtils mBitmapUtils;
    private RatingBar rb_star;
    private TextView tv_size;
    private TextView tv_des;

    private TextView tv_download;
    private DownloadManager mDM;
    private int mCurrentState;
    private float mProgress;
    private ProgressArc pbProgress;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.list_item_home);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        rb_star = (RatingBar) view.findViewById(R.id.rb_star);
        tv_size = (TextView) view.findViewById(R.id.tv_size);
        tv_des = (TextView) view.findViewById(R.id.tv_des);
        mBitmapUtils = BitmapHelper.getBitmapUtils();

        FrameLayout fl_progress = (FrameLayout) view.findViewById(R.id.fl_progress);
        tv_download = (TextView) view.findViewById(R.id.tv_download);

        pbProgress = new ProgressArc(UIUtils.getContext());
        // 设置圆形进度条直径
        pbProgress.setArcDiameter(UIUtils.dip2px(26));
        // 设置进度条颜色
        pbProgress.setProgressColor(UIUtils.getColor(R.color.progress));
        // 设置进度条宽高布局参数
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                UIUtils.dip2px(27), UIUtils.dip2px(27));

        fl_progress.addView(pbProgress, params);
        fl_progress.setOnClickListener(this);

        mDM = DownloadManager.getInstance();
        mDM.registerObserver(this);
        return view;
    }

    @Override
    public void refreshView(AppInfo data) {
        tv_name.setText(data.name);
        mBitmapUtils.display(iv_icon, HttpHelper.PATH + "image?name=" + data.iconUrl);
        rb_star.setRating((float) data.stars);
        tv_size.setText(Formatter.formatFileSize(UIUtils.getContext(),data.size));
        tv_des.setText(data.des);

        // 判断当前应用是否下载过
        DownloadInfo downloadInfo = mDM.getDownloadInfo(data);
        if (downloadInfo != null) {
            // 之前下载过
            mCurrentState = downloadInfo.currentState;
            mProgress = downloadInfo.getProgress();
        } else {
            // 没有下载过
            mCurrentState = DownloadManager.STATE_UNDO;
            mProgress = 0;
        }
        refreshUI(mCurrentState, mProgress, data.id);
    }

    /**
     * 刷新界面
     * @param currentState
     * @param progress
     * @param id
     */
    private void refreshUI(int currentState, float progress, String id) {

        mCurrentState = currentState;
        mProgress = progress;
        // 由于listview重用机制, 要确保刷新之前, 确实是同一个应用
        if(!getData().id.equals(id)) {
            return;
        }
        switch (currentState) {
            case  DownloadManager.STATE_UNDO:
                // 自定义进度条背景
                pbProgress.setBackgroundResource(R.drawable.ic_download);
                // 没有进度
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
                tv_download.setText("下载");
                break;
            case DownloadManager.STATE_WAITING:
                pbProgress.setBackgroundResource(R.drawable.ic_download);
                // 等待模式
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_WAITING);
                tv_download.setText("等待");
                break;
            case DownloadManager.STATE_DOWNLOADING:
                pbProgress.setBackgroundResource(R.drawable.ic_pause);
                // 下载中模式
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_DOWNLOADING);
                pbProgress.setProgress(progress, true);
                tv_download.setText((int) (progress * 100) + "%");
                break;
            case DownloadManager.STATE_PAUSE:
                pbProgress.setBackgroundResource(R.drawable.ic_resume);
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
                break;
            case DownloadManager.STATE_ERROR:
                pbProgress.setBackgroundResource(R.drawable.ic_redownload);
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
                tv_download.setText("下载失败");
                break;
            case DownloadManager.STATE_SUCCESS:
                pbProgress.setBackgroundResource(R.drawable.ic_install);
                pbProgress.setStyle(ProgressArc.PROGRESS_STYLE_NO_PROGRESS);
                tv_download.setText("安装");
                break;

            default:
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
                    refreshUI(downloadInfo.currentState,downloadInfo.getProgress(),downloadInfo.id);
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
            case R.id.fl_progress:
                // 根据当前状态来决定下一步操作
                if (mCurrentState == DownloadManager.STATE_UNDO
                        || mCurrentState == DownloadManager.STATE_ERROR
                        || mCurrentState == DownloadManager.STATE_PAUSE) {
                    mDM.downLoad(getData());// 开始下载
                } else if (mCurrentState == DownloadManager.STATE_DOWNLOADING
                        || mCurrentState == DownloadManager.STATE_WAITING) {
                    mDM.pause(getData());// 暂停下载
                } else if (mCurrentState == DownloadManager.STATE_SUCCESS) {
                    mDM.install(getData());// 开始安装
                }

                break;

            default:
                break;
        }
    }
}
