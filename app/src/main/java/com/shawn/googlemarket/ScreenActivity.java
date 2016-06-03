package com.shawn.googlemarket;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.shawn.googlemarket.http.HttpHelper;
import com.shawn.googlemarket.utils.BitmapHelper;
import com.shawn.googlemarket.utils.UIUtils;

import java.util.ArrayList;

public class ScreenActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private BitmapUtils mBitmapUtils;
    private ArrayList<String> screen;
    private ArrayList<ImageView> viewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);
        mViewPager = (ViewPager)findViewById(R.id.vp_screen);

        Intent intent = getIntent();
        int index = intent.getIntExtra("index", -1);
        screen = intent.getStringArrayListExtra("picList");
        mBitmapUtils = BitmapHelper.getBitmapUtils();
        for (int i = 0; i < screen.size(); i++) {
            ImageView view = new ImageView(UIUtils.getContext());
            mBitmapUtils.display(view, HttpHelper.PATH + "image?name=" + screen.get(i));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            viewList.add(view);
        }
        mViewPager.setAdapter(new ScreenAdapter());
        mViewPager.setCurrentItem(index);
    }

    class ScreenAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = viewList.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
