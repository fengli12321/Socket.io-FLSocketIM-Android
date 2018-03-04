package com.foxpower.flchatofandroid.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.foxpower.flchatofandroid.R;
import com.foxpower.flchatofandroid.util.other.FLUtil;

import java.util.ArrayList;

import okhttp3.internal.Util;

/**
 * Created by fengli on 2018/3/1.
 */

public class IndicatorView extends LinearLayout {



    private Context mContext;
    private ArrayList<View> mImageViews;
    private int size = 6;
    private int marginSize = 15;
    private int pointSize;
    private int marginLeft;

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        pointSize = FLUtil.dip2px(context, size);
        marginLeft = FLUtil.dip2px(context, marginSize);
    }

    public void initIndicator(int count) {
        mImageViews = new ArrayList<>();
        this.removeAllViews();
        LayoutParams lp;

        for (int i = 0; i < count; i++) {
            View v = new View(mContext);
            lp = new LayoutParams(pointSize, pointSize);
            if (i != 0) {
                lp.leftMargin = marginLeft;
            }
            v.setLayoutParams(lp);
            if (i == 0){
                v.setBackgroundResource(R.drawable.bg_circle_white);
            } else {
                v.setBackgroundResource(R.drawable.bg_circle_gray);
            }
            mImageViews.add(v);
            this.addView(v);
        }
    }

    public void playByStartPointToNext(int startPosition, int nextPosition) {
        if (startPosition < 0 || nextPosition < 0 || nextPosition == startPosition) {
            startPosition = nextPosition = 0;
        }
        final View viewStart = mImageViews.get(startPosition);
        final View viewNext = mImageViews.get(nextPosition);
        viewNext.setBackgroundResource(R.drawable.bg_circle_white);
        viewStart.setBackgroundResource(R.drawable.bg_circle_gray);
    }
}
