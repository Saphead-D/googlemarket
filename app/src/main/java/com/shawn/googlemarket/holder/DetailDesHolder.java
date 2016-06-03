package com.shawn.googlemarket.holder;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.shawn.googlemarket.R;
import com.shawn.googlemarket.bean.AppInfo;
import com.shawn.googlemarket.utils.UIUtils;

/**
 * Created by shawn on 2016/5/20.
 */
public class DetailDesHolder extends BaseHolder<AppInfo> {

    private TextView tv_detail_des;
    private TextView tv_detail_author;
    private ImageView iv_arrow;
    private RelativeLayout rl_detail_toggle;
    private LinearLayout.LayoutParams params;

    @Override
    public View initView() {
        View view = UIUtils.inflate(R.layout.layout_detail_desinfo);
        tv_detail_des = (TextView) view.findViewById(R.id.tv_detail_des);
        tv_detail_author = (TextView) view.findViewById(R.id.tv_detail_author);
        iv_arrow = (ImageView) view.findViewById(R.id.iv_arrow);
        rl_detail_toggle = (RelativeLayout) view.findViewById(R.id.rl_detail_toggle);
        rl_detail_toggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        return view;
    }

    private boolean isOpen = false;
    private void toggle() {
        int shortHeight = getShortHeight();
        int longHeight = getLongHeight();

        ValueAnimator animator = null;
        if (isOpen) {
            // 关闭
            isOpen = false;
            if (longHeight > shortHeight) {// 只有描述信息大于7行,才启动动画
                animator = ValueAnimator.ofInt(longHeight, shortHeight);
            }
        } else {
            // 打开
            isOpen = true;
            if (longHeight > shortHeight) {// 只有描述信息大于7行,才启动动画
                animator = ValueAnimator.ofInt(shortHeight, longHeight);
            }
        }
        if(animator != null) {
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    Integer height = (Integer) valueAnimator.getAnimatedValue();
                    params.height = height;
                    tv_detail_des.setLayoutParams(params);
                }
            });

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    // ScrollView要滑动到最底部
                    final ScrollView scrollView = getScrollView();
                    // 为了运行更加安全和稳定, 可以讲滑动到底部方法放在消息队列中执行
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(View.FOCUS_DOWN);
                        }
                    });
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

    }


    @Override
    public void refreshView(final AppInfo data) {

        tv_detail_des.post(new Runnable() {
            @Override
            public void run() {// 放在消息队列中运行, 解决当只有三行描述时也是7行高度的bug
                tv_detail_des.setText(data.des);
                tv_detail_author.setText(data.author);
                int shortHeight = getShortHeight();
                params = (LinearLayout.LayoutParams) tv_detail_des.getLayoutParams();
                params.height = shortHeight;
                tv_detail_des.setLayoutParams(params);
            }
        });
    }

    /**
     * 获取7行textview的高度
     */
    private int getShortHeight() {
        // 模拟一个textview,设置最大行数为7行, 计算该虚拟textview的高度,
        // 从而知道tv_detail_des在展示7行时应该多高
        int width = tv_detail_des.getMeasuredWidth();// 宽度
        TextView view = new TextView(UIUtils.getContext());
        view.setText(getData().des);// 设置文字
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);// 文字大小一致
        view.setMaxLines(7);// 最大行数为7行

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width,
                View.MeasureSpec.EXACTLY);// 宽不变, 确定值, match_parent
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(2000,
                View.MeasureSpec.AT_MOST);// 高度包裹内容, wrap_content;当包裹内容时,
        // 参1表示尺寸最大值,暂写2000, 也可以是屏幕高度

        // 开始测量
        view.measure(widthMeasureSpec, heightMeasureSpec);
        return view.getMeasuredHeight();// 返回测量后的高度

    }

    /**
     * 获取完整textview的高度
     */
    private int getLongHeight() {
        int width = tv_detail_des.getMeasuredWidth();// 宽度
        TextView view = new TextView(UIUtils.getContext());
        view.setText(getData().des);// 设置文字
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);// 文字大小一致
        //view.setMaxLines(7);// 最大行数为7行

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width,
                View.MeasureSpec.EXACTLY);// 宽不变, 确定值, match_parent
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(2000,
                View.MeasureSpec.AT_MOST);// 高度包裹内容, wrap_content;当包裹内容时,
        // 参1表示尺寸最大值,暂写2000, 也可以是屏幕高度

        // 开始测量
        view.measure(widthMeasureSpec,heightMeasureSpec);
        return view.getMeasuredHeight();// 返回测量后的高度
    }

    // 获取ScrollView, 一层一层往上找,
    // 知道找到ScrollView后才返回;注意:一定要保证父控件或祖宗控件有ScrollView,否则死循环
    private ScrollView getScrollView() {
        ViewParent parent = tv_detail_des.getParent();
        while (!(parent instanceof ScrollView)) {
            parent = parent.getParent();
        }

        return (ScrollView) parent;
    }
}
