package com.shawn.googlemarket.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by shawn on 2016/5/19.
 */
public class DrawableUtils {

    //获取一个shape对象
    public static GradientDrawable getGradientDrawable(int color,int radius){
        // xml中定义的shape标签 对应此类
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setCornerRadius(radius);
        shape.setColor(color);
        return shape;
    }

    //获取状态选择器
    public static StateListDrawable getSelector(Drawable normal,Drawable press){
        StateListDrawable select = new StateListDrawable();
        select.addState(new int[]{android.R.attr.state_pressed}, press);// 按下图片
        select.addState(new int[]{}, normal);// 默认图片
        return select;
    }

    //获取状态选择器
    public static StateListDrawable getSelector(int press,int normal,int radius){
        GradientDrawable bgNormal  = getGradientDrawable(normal, radius);
        GradientDrawable bgPress = getGradientDrawable(press,radius);
        StateListDrawable selector = getSelector(bgNormal, bgPress);
        return selector;
    }
}
