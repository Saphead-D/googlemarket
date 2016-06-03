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
 * Created by shawn on 2016/5/14.
 */
public class AppHolder extends BaseHolder<AppInfo> {

    private TextView tv_name;
    private ImageView iv_icon;
    private BitmapUtils mBitmapUtils;
    private RatingBar rb_star;
    private TextView tv_size;
    private TextView tv_des;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.list_item_home);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        rb_star = (RatingBar) view.findViewById(R.id.rb_star);
        tv_size = (TextView) view.findViewById(R.id.tv_size);
        tv_des = (TextView) view.findViewById(R.id.tv_des);
        mBitmapUtils = BitmapHelper.getBitmapUtils();
        return view;
    }

    @Override
    public void refreshView(AppInfo data) {
        tv_name.setText(data.name);
        mBitmapUtils.display(iv_icon, HttpHelper.PATH + "image?name=" + data.iconUrl);
        rb_star.setRating((float) data.stars);
        tv_size.setText(Formatter.formatFileSize(UIUtils.getContext(),data.size));
        tv_des.setText(data.des);
    }
}
