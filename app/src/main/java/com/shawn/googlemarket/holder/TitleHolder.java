package com.shawn.googlemarket.holder;

import android.view.View;
import android.widget.TextView;

import com.shawn.googlemarket.R;
import com.shawn.googlemarket.bean.CategoryInfo;
import com.shawn.googlemarket.utils.UIUtils;

/**
 * Created by shawn on 2016/5/19.
 */
public class TitleHolder extends BaseHolder<CategoryInfo>{
    private TextView tv_title;
    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.list_item_title);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        return view;
    }

    @Override
    public void refreshView(CategoryInfo data) {
        tv_title.setText(data.title);
    }
}
