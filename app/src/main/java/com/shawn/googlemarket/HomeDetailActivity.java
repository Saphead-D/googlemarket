package com.shawn.googlemarket;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.shawn.googlemarket.bean.AppInfo;
import com.shawn.googlemarket.holder.DetailAppInfoHolder;
import com.shawn.googlemarket.holder.DetailDesHolder;
import com.shawn.googlemarket.holder.DetailDownloadHolder;
import com.shawn.googlemarket.holder.DetailPicsHolder;
import com.shawn.googlemarket.holder.DetailSafeHolder;
import com.shawn.googlemarket.http.protocol.HomeDetailProtocol;
import com.shawn.googlemarket.utils.UIUtils;
import com.shawn.googlemarket.view.LoadingPage;

public class HomeDetailActivity extends AppCompatActivity {

    private FrameLayout fl_detail_appinfo;
    private String packageName;
    private AppInfo data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home_detail);

        LoadingPage mLoadingPage = new LoadingPage(UIUtils.getContext()) {
            @Override
            public View onCreateSuccessView() {
                return HomeDetailActivity.this.onCreateSuccessView();
            }

            @Override
            public ResultState onLoad() {
                return HomeDetailActivity.this.onLoad();
            }
        };

        setContentView(mLoadingPage);

        Intent intent = getIntent();
        packageName = intent.getStringExtra("packageName");

        // 开始加载网络数据
        mLoadingPage.loadData();

        initActionbar();
    }

    // 初始化actionbar
    private void initActionbar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setLogo(R.drawable.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public View onCreateSuccessView() {
        View view = UIUtils.inflate(R.layout.activity_home_detail);
        // 初始化应用信息模块
        fl_detail_appinfo = (FrameLayout)view.findViewById(R.id.fl_detail_appinfo);
        // 动态给帧布局填充页面
        DetailAppInfoHolder appInfoHolder = new DetailAppInfoHolder();
        fl_detail_appinfo.addView(appInfoHolder.getmRootView());
        appInfoHolder.setData(data);

        // 初始化安全描述模块
        FrameLayout fl_detail_safe = (FrameLayout) view.findViewById(R.id.fl_detail_safe);
        DetailSafeHolder safeHolder = new DetailSafeHolder();
        fl_detail_safe.addView(safeHolder.getmRootView());
        safeHolder.setData(data);

        // 初始化截图模块
        HorizontalScrollView hsv_detail_pics = (HorizontalScrollView) view.findViewById(R.id.hsv_detail_pics);
        DetailPicsHolder picsHolder = new DetailPicsHolder();
        hsv_detail_pics.addView(picsHolder.getmRootView());
        picsHolder.setData(data);

        // 初始化描述模块
        FrameLayout fl_detail_des = (FrameLayout) view.findViewById(R.id.fl_detail_des);
        DetailDesHolder desHolder = new DetailDesHolder();
        fl_detail_des.addView(desHolder.getmRootView());
        desHolder.setData(data);

        // 初始化下载模块
        FrameLayout fl_detail_download = (FrameLayout) view.findViewById(R.id.fl_detail_download);
        DetailDownloadHolder downloadHolder = new DetailDownloadHolder();
        fl_detail_download.addView(downloadHolder.getmRootView());
        downloadHolder.setData(data);

        return view;
    }

    public LoadingPage.ResultState onLoad() {
        HomeDetailProtocol protocol = new HomeDetailProtocol(packageName);
        data = protocol.getData(0);
        if (data != null) {
            return LoadingPage.ResultState.STATE_SUCCESS;
        } else {
            return LoadingPage.ResultState.STATE_ERROR;
        }
    }
}
