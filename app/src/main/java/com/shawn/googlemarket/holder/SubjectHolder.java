package com.shawn.googlemarket.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.shawn.googlemarket.R;
import com.shawn.googlemarket.bean.SubjectInfo;
import com.shawn.googlemarket.http.HttpHelper;
import com.shawn.googlemarket.utils.BitmapHelper;
import com.shawn.googlemarket.utils.LogUtils;
import com.shawn.googlemarket.utils.UIUtils;

/**
 * Created by shawn on 2016/5/17.
 */
public class SubjectHolder extends BaseHolder<SubjectInfo> {

    private TextView tv_title;
    private ImageView iv_pic;
    private BitmapUtils mBitmapUtils;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.list_item_subject);
        iv_pic = (ImageView) view.findViewById(R.id.iv_pic);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        mBitmapUtils = BitmapHelper.getBitmapUtils();
        return view;
    }

    @Override
    public void refreshView(SubjectInfo data) {
        tv_title.setText(data.des);
        String imgpath = HttpHelper.PATH + "image?name=" + data.url;
        mBitmapUtils.display(iv_pic, imgpath);
    }
}
