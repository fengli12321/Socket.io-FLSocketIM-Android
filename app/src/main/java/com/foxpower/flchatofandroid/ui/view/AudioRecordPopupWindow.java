package com.foxpower.flchatofandroid.ui.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.lang.reflect.MalformedParameterizedTypeException;

/**
 * Created by fengli on 2018/2/27.
 */

public class AudioRecordPopupWindow {

    private Context mContext;

    private PopupWindow mPop;

    public AudioRecordPopupWindow(Context mContext, View view) {
        this(mContext, view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public AudioRecordPopupWindow(Context mContext, View view, int width, int height) {

        init(mContext, view, width, height);
    }

    public void init(Context mContext, View view, int width, int height) {
        this.mContext = mContext;

        view.setFocusable(true);
        view.setFocusableInTouchMode(true);

        mPop = new PopupWindow(view, width, height, true);
        mPop.setFocusable(true);

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (i == KeyEvent.KEYCODE_BACK){
                    mPop.dismiss();
                    return true;
                }
                return false;
            }
        });

        // 点击其他地方消失
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mPop != null && mPop.isShowing()) {
                    mPop.dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    public PopupWindow getPopupWindow() {
        return mPop;
    }


    public void showAtLocation(View parent, int gravity, int x, int y) {
        if (mPop.isShowing()) {
            return;
        }
        mPop.showAtLocation(parent, gravity, x, y);
    }

    public void showAsDropDown(View anchor) {

        showAsDropDown(anchor, 0, 0);
    }

    public void showAsDropDown(View anchor, int xoff, int yoff) {

        if (mPop.isShowing()) {
            return;
        }
        mPop.showAsDropDown(anchor, xoff, yoff);
    }

    public void dismiss() {
        if (mPop.isShowing()) {
            mPop.dismiss();
        }
    }
}
