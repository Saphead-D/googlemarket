package com.shawn.googlemarket.fragment;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shawn.googlemarket.http.protocol.RecommendProtocol;
import com.shawn.googlemarket.utils.UIUtils;
import com.shawn.googlemarket.view.LoadingPage;
import com.shawn.googlemarket.view.fly.ShakeListener;
import com.shawn.googlemarket.view.fly.StellarMap;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by shawn on 2016/5/13.
 */
public class RecommendFragment extends BaseFragment {

    private ArrayList<String> data;

    @Override
    public View onCreateSuccessView() {
        final StellarMap stellarMap = new StellarMap(UIUtils.getContext());
        stellarMap.setAdapter(new RecommendAdapter());
        // 随机方式, 将控件划分为9行6列的的格子, 然后在格子中随机展示
        stellarMap.setRegularity(6, 9);
        // 设置内边距10dp
        int padding  = UIUtils.dip2px(10);
        stellarMap.setInnerPadding(padding, padding, padding, padding);
        // 设置默认页面, 第一组数据
        stellarMap.setGroup(0,true);
        ShakeListener shake = new ShakeListener(UIUtils.getContext());
        shake.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                stellarMap.zoomIn();// 跳到下一页数据
            }
        });
        return stellarMap;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        RecommendProtocol protocol = new RecommendProtocol();
        data = protocol.getData(0);
        return check(data);
    }

    class RecommendAdapter implements StellarMap.Adapter {

        @Override
        public int getGroupCount() {// 返回组的个数
            return 2;
        }

        @Override
        public int getCount(int group) {// 返回某组的item个数
            int count = data.size() / getGroupCount();
            if(group == getGroupCount() - 1) {
                // 最后一页, 将除不尽,余下来的数量追加在最后一页, 保证数据完整不丢失
                count += data.size() % getGroupCount();
            }
            return count;
        }

        @Override// 初始化布局
        public View getView(int group, int position, View convertView) {
            // 因为position每组都会从0开始计数,
            // 所以需要将前面几组数据的个数加起来,才能确定当前组获取数据的角标位置
            position += group * getCount(group - 1);
            final String appName = data.get(position);
            TextView view = new TextView(UIUtils.getContext());
            view.setText(appName);
            Random random = new Random();
            // 随机大小, 16-25
            int size = 16 + random.nextInt(10);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);
            // 随机颜色
            // r g b, 0-255 -> 100-230, 颜色值不能太小或太大, 从而避免整体颜色过亮或者过暗
            int r = 100 + random.nextInt(100);
            int b = 100 + random.nextInt(100);
            int g = 100 + random.nextInt(100);
            view.setTextColor(Color.rgb(r,g,b));
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(), appName,
                            Toast.LENGTH_SHORT).show();
                }
            });
            return view;
        }

        @Override// 返回下一组的id
        public int getNextGroupOnZoom(int group, boolean isZoomIn) {
            if(isZoomIn) {// 往下滑加载上一页
                if(group > 0) {
                    group --;
                }else {
                    // 跳到最后一页
                    group = getGroupCount() - 1;
                }
            }else {// 往上滑加载下一页
                if(group < getGroupCount() - 1) {
                    group++;
                }else {
                    // 跳到第一页
                    group = 0;
                }
            }
            return group;
        }
    }
}
