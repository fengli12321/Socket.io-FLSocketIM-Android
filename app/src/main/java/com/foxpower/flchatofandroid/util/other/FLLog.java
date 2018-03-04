package com.foxpower.flchatofandroid.util.other;

import android.util.Log;

/**
 * Created by fengli on 2018/2/5.
 */

public class FLLog {

    private static final boolean ISDEBUG = true;
    private static final String TAG = "log:";




    public static void d(String tag, String msg) {
        if (ISDEBUG) Log.d(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (ISDEBUG) Log.v(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (ISDEBUG) Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (ISDEBUG) Log.e(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (ISDEBUG) Log.w(tag, msg);
    }

    public static void wtf(String tag, String msg) {
        if (ISDEBUG) Log.wtf(tag, msg);
    }




    public static void d(String msg) {
        if (ISDEBUG) Log.d(TAG, msg);
    }

    public static void v(String msg) {
        if (ISDEBUG) Log.v(TAG, msg);
    }

    public static void i(String msg) {
        if (ISDEBUG) Log.i(TAG, msg);
    }

    public static void e(String msg) {
        if (ISDEBUG) Log.e(TAG, msg);
    }

    public static void w(String msg) {
        if (ISDEBUG) Log.w(TAG, msg);
    }

    public static void wtf(String msg) {
        if (ISDEBUG) Log.wtf(TAG, msg);
    }
}
