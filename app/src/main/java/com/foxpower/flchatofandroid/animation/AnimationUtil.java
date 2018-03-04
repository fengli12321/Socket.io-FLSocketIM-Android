package com.foxpower.flchatofandroid.animation;

import android.view.View;
import android.view.animation.ScaleAnimation;

/**
 * Created by fengli on 2018/2/6.
 */

public class AnimationUtil {

    /**
     * 是否开启动画
     *
     */
    public static boolean isAnimation = true;
    /**缩放
     * @param view
     * @param from 缩放开始比例0-1.0
     * @param to 缩放结束比例0-1.0
     * @param w
     * @param h
     */
    public static void setViewScale(View view, float from, float to, int w, int h) {
        if (isAnimation) {
            ScaleAnimation animation = new ScaleAnimation(from, to, from, to,
                    w/2, h/2);
            animation.setDuration(200);
            animation.setFillAfter(true);
            view.clearAnimation();
            view.setAnimation(animation);
            animation = null;
            System.gc();
        }
    }

}
