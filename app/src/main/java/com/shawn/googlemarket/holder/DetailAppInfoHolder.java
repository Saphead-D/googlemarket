package com.shawn.googlemarket.holder;

import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.shawn.googlemarket.R;
import com.shawn.googlemarket.bean.AppInfo;
import com.shawn.googlemarket.http.HttpHelper;
import com.shawn.googlemarket.utils.BitmapHelper;
import com.shawn.googlemarket.utils.UIUtils;

/**
 * Created by shawn on 2016/5/20.
 */
public class DetailAppInfoHolder extends BaseHolder<AppInfo> {
    private ImageView iv_icon;
    private TextView tv_name;
    private RatingBar rb_star;
    private TextView tv_download_num;
    private TextView tv_version;
    private TextView tv_date;
    private TextView tv_size;

    private BitmapUtils mBitmapUtils;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.layout_detail_appinfo);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        rb_star = (RatingBar) view.findViewById(R.id.rb_star);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_download_num = (TextView) view.findViewById(R.id.tv_download_num);
        tv_version = (TextView) view.findViewById(R.id.tv_version);
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_size = (TextView) view.findViewById(R.id.tv_size);

        mBitmapUtils = BitmapHelper.getBitmapUtils();
        return view;
    }

    @Override
    public void refreshView(AppInfo data) {
        mBitmapUtils.display(iv_icon, HttpHelper.PATH + "image?name=" + data.iconUrl);
        rb_star.setRating((float) data.stars);
        tv_name.setText(data.name);
        tv_download_num.setText("下载量:" + data.downloadNum);
        tv_version.setText("版本号:" + data.version);
        tv_date.setText(data.date);
        tv_size.setText(Formatter.formatFileSize(UIUtils.getContext(),data.size));
    }
}
