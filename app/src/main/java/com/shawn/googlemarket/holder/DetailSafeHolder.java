package com.shawn.googlemarket.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.shawn.googlemarket.R;
import com.shawn.googlemarket.bean.AppInfo;
import com.shawn.googlemarket.http.HttpHelper;
import com.shawn.googlemarket.utils.BitmapHelper;
import com.shawn.googlemarket.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by shawn on 2016/5/20.
 */
public class DetailSafeHolder extends BaseHolder<AppInfo> {

    private ImageView[] mSafeIcons;// 安全标识图片
    private ImageView[] mDesIcons;// 安全描述图片
    private TextView[] mSafeDes;// 安全描述文字
    private LinearLayout[] mSafeDesBar;// 安全描述条目(图片+文字)

    private BitmapUtils mBitmapUtils;

    private RelativeLayout rl_des_root;
    private ImageView iv_arrow;
    private LinearLayout ll_des_root;
    private int measuredHeight;
    private LinearLayout.LayoutParams params;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.layout_detail_safeinfo);

        mSafeIcons = new ImageView[4];
        mSafeIcons[0] = (ImageView) view.findViewById(R.id.iv_safe1);
        mSafeIcons[1] = (ImageView) view.findViewById(R.id.iv_safe2);
        mSafeIcons[2] = (ImageView) view.findViewById(R.id.iv_safe3);
        mSafeIcons[3] = (ImageView) view.findViewById(R.id.iv_safe4);

        mDesIcons = new ImageView[4];
        mDesIcons[0] = (ImageView) view.findViewById(R.id.iv_des1);
        mDesIcons[1] = (ImageView) view.findViewById(R.id.iv_des2);
        mDesIcons[2] = (ImageView) view.findViewById(R.id.iv_des3);
        mDesIcons[3] = (ImageView) view.findViewById(R.id.iv_des4);

        mSafeDes = new TextView[4];
        mSafeDes[0] = (TextView) view.findViewById(R.id.tv_des1);
        mSafeDes[1] = (TextView) view.findViewById(R.id.tv_des2);
        mSafeDes[2] = (TextView) view.findViewById(R.id.tv_des3);
        mSafeDes[3] = (TextView) view.findViewById(R.id.tv_des4);

        mSafeDesBar = new LinearLayout[4];
        mSafeDesBar[0] = (LinearLayout) view.findViewById(R.id.ll_des1);
        mSafeDesBar[1] = (LinearLayout) view.findViewById(R.id.ll_des2);
        mSafeDesBar[2] = (LinearLayout) view.findViewById(R.id.ll_des3);
        mSafeDesBar[3] = (LinearLayout) view.findViewById(R.id.ll_des4);

        mBitmapUtils = BitmapHelper.getBitmapUtils();
        rl_des_root = (RelativeLayout) view.findViewById(R.id.rl_des_root);
        ll_des_root = (LinearLayout) view.findViewById(R.id.ll_des_root);
        iv_arrow = (ImageView) view.findViewById(R.id.iv_arrow);

        rl_des_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        return view;
    }

    private boolean isOpen = false;// 标记安全描述开关状态,默认关
    private void toggle() {
        ValueAnimator animator = null;
        if(isOpen) {
            // 关闭
            isOpen = false;
            animator = ValueAnimator.ofInt(measuredHeight,0);
        }else {
            // 开启
            isOpen = true;
            animator = ValueAnimator.ofInt(0,measuredHeight);
        }

        // 动画更新的监听
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Integer height = (Integer) valueAnimator.getAnimatedValue();
                // 重新修改布局高度
                params.height = height;
                ll_des_root.setLayoutParams(params);
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // 动画结束的事件
                // 更新小箭头的方向
                if (isOpen) {
                    iv_arrow.setImageResource(R.drawable.arrow_up);
                } else {
                    iv_arrow.setImageResource(R.drawable.arrow_down);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        animator.setDuration(200);
        animator.start();
    }

    @Override
    public void refreshView(AppInfo data) {
        ArrayList<AppInfo.SafeInfo> safe = data.safe;
        for (int i = 0; i < 4; i++) {
            if(i < safe.size()) {
                // 安全标识图片
                AppInfo.SafeInfo safeInfo = safe.get(i);
                mBitmapUtils.display(mSafeIcons[i], HttpHelper.PATH + "image?name=" + safeInfo.safeUrl);
                mSafeDes[i].setText(safeInfo.safeDes);
                mBitmapUtils.display(mDesIcons[i],HttpHelper.PATH + "image?name=" + safeInfo.safeDesUrl);
            }else {
                // 剩下不应该显示的图片
                mSafeIcons[i].setVisibility(View.GONE);

                // 隐藏多余的描述条目
                mSafeDesBar[i].setVisibility(View.GONE);
            }
        }

        // 获取安全描述的完整高度
        ll_des_root.measure(0, 0);
        measuredHeight = ll_des_root.getMeasuredHeight();
        // 修改安全描述布局高度为0,达到隐藏效果
        params = (LinearLayout.LayoutParams) ll_des_root.getLayoutParams();
        params.height = 0;
        ll_des_root.setLayoutParams(params);
    }
}
