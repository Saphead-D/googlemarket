package com.shawn.googlemarket.utils;

import com.lidroid.xutils.BitmapUtils;

/**
 * Created by shawn on 2016/5/17.
 */
public class BitmapHelper {

    /*private static BitmapUtils  mBitmapUtils = new BitmapUtils(UIUtils.getContext());

    private BitmapHelper() {
    }
    // 单例 饿汉式
    public static BitmapUtils getBitmapUtils() {
        return mBitmapUtils;
    }*/

    private static BitmapUtils  mBitmapUtils = null;
    private BitmapHelper() {
    }
    // 单例, 懒汉模式
    public static BitmapUtils getBitmapUtils() {
        if(mBitmapUtils == null) {
            synchronized (BitmapUtils.class){
                if(mBitmapUtils == null) {
                    mBitmapUtils = new BitmapUtils(UIUtils.getContext());
                }
            }
        }
        return mBitmapUtils;
    }
}
