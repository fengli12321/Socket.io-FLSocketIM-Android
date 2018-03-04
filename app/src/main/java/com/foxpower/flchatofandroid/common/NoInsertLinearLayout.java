package com.foxpower.flchatofandroid.common;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.LinearLayout;

/**
 * Created by fengli on 2018/2/21.
 */

public class NoInsertLinearLayout extends LinearLayout {

    private int[] mInsets = new int[4];
    public NoInsertLinearLayout(Context context) {
        super(context);
    }

    public NoInsertLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoInsertLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NoInsertLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected boolean fitSystemWindows(Rect insets) {

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            mInsets[0] = insets.left;
//            mInsets[1] = insets.top;
//            mInsets[2] = insets.right;
//
//            insets.left = 0;
//            insets.right = 0;
//            insets.top = 0;
//        }
        return super.fitSystemWindows(insets);
    }


    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {

            mInsets[0] = insets.getSystemWindowInsetLeft();
            mInsets[1] = insets.getSystemWindowInsetTop();
            mInsets[2] = insets.getSystemWindowInsetRight();

            return super.onApplyWindowInsets(insets.replaceSystemWindowInsets(0, 0, 0, insets.getSystemWindowInsetBottom()));
        }
        else {
            return super.onApplyWindowInsets(insets);
        }
    }
}
