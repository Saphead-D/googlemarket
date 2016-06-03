package com.shawn.googlemarket.fragment;

import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.shawn.googlemarket.http.protocol.HotProtocol;
import com.shawn.googlemarket.utils.DrawableUtils;
import com.shawn.googlemarket.utils.UIUtils;
import com.shawn.googlemarket.view.FlowLayout;
import com.shawn.googlemarket.view.LoadingPage;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by shawn on 2016/5/13.
 */
public class HotFragment extends BaseFragment {

    private ArrayList<String> data;

    @Override
    public View onCreateSuccessView() {
        ScrollView scrollView = new ScrollView(UIUtils.getContext());
        FlowLayout flowLayout = new FlowLayout(UIUtils.getContext());
        scrollView.addView(flowLayout);
        int padding = UIUtils.dip2px(10);
        flowLayout.setPadding(padding,padding,padding,padding);// 设置内边距
        flowLayout.setHorizontalSpacing(UIUtils.dip2px(6));// 水平间距
        flowLayout.setVerticalSpacing(UIUtils.dip2px(8));// 竖直间距


        for (int i = 0; i < data.size(); i++) {
            final String keyword = data.get(i);
            TextView view = new TextView(UIUtils.getContext());
            view.setText(data.get(i));

            view.setTextColor(Color.WHITE);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            view.setPadding(padding, padding, padding, padding);
            view.setGravity(Gravity.CENTER);

            // 随机颜色
            Random random = new Random();
            int r = 100 + random.nextInt(100);
            int b = 100 + random.nextInt(100);
            int g = 100 + random.nextInt(100);
            int color = 0xffcecece;// 按下后偏白的背景色
            StateListDrawable selector = DrawableUtils.getSelector(color, Color.rgb(r, g, b), UIUtils.dip2px(6));
            if(Build.VERSION.SDK_INT >= 16) {
                view.setBackground(selector);
            }else {
                view.setBackgroundDrawable(selector);
            }

            flowLayout.addView(view);

            // 只有设置点击事件, 状态选择器才起作用
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(),keyword, Toast.LENGTH_SHORT).show();
                }
            });
        }
        return scrollView;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        HotProtocol protocol = new HotProtocol();
        data = protocol.getData(0);
        return check(data);
    }
}
