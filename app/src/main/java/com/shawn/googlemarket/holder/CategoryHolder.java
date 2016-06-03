package com.shawn.googlemarket.holder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.shawn.googlemarket.R;
import com.shawn.googlemarket.bean.CategoryInfo;
import com.shawn.googlemarket.http.HttpHelper;
import com.shawn.googlemarket.utils.BitmapHelper;
import com.shawn.googlemarket.utils.LogUtils;
import com.shawn.googlemarket.utils.UIUtils;

/**
 * Created by shawn on 2016/5/19.
 */
public class CategoryHolder extends BaseHolder<CategoryInfo> implements View.OnClickListener {

    private TextView tv_name1,tv_name2,tv_name3;
    private ImageView iv_icon1,iv_icon2,iv_icon3;
    private LinearLayout ll_grid1,ll_grid2,ll_grid3;
    private BitmapUtils mBitmapUtils;
    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.list_item_category);
        tv_name1 = (TextView) view.findViewById(R.id.tv_name1);
        tv_name2 = (TextView) view.findViewById(R.id.tv_name2);
        tv_name3 = (TextView) view.findViewById(R.id.tv_name3);

        iv_icon1 = (ImageView) view.findViewById(R.id.iv_icon1);
        iv_icon2 = (ImageView) view.findViewById(R.id.iv_icon2);
        iv_icon3 = (ImageView) view.findViewById(R.id.iv_icon3);

        ll_grid1 = (LinearLayout) view.findViewById(R.id.ll_grid1);
        ll_grid2 = (LinearLayout) view.findViewById(R.id.ll_grid2);
        ll_grid3 = (LinearLayout) view.findViewById(R.id.ll_grid3);

        ll_grid1.setOnClickListener(this);
        ll_grid2.setOnClickListener(this);
        ll_grid3.setOnClickListener(this);

        mBitmapUtils = BitmapHelper.getBitmapUtils();
        return view;
    }

    @Override
    public void refreshView(CategoryInfo data) {
        tv_name1.setText(data.name1);
        tv_name2.setText(data.name2);
        tv_name3.setText(data.name3);

        mBitmapUtils.display(iv_icon1, HttpHelper.PATH + "image?name=" + data.url1);
        mBitmapUtils.display(iv_icon2, HttpHelper.PATH + "image?name=" + data.url2);
        if(!TextUtils.isEmpty( data.url3)) {
            mBitmapUtils.display(iv_icon3, HttpHelper.PATH + "image?name=" + data.url3);
        }
    }

    @Override
    public void onClick(View v) {
        CategoryInfo info = getData();
        switch (v.getId()) {
            case  R.id.ll_grid1:
                Toast.makeText(UIUtils.getContext(), info.name1, Toast.LENGTH_SHORT)
                        .show();
                break;
            case  R.id.ll_grid2:
                Toast.makeText(UIUtils.getContext(), info.name2, Toast.LENGTH_SHORT)
                        .show();
                break;
            case  R.id.ll_grid3:
                Toast.makeText(UIUtils.getContext(), info.name3, Toast.LENGTH_SHORT)
                        .show();
                break;
            default:break;
        }
    }
}
