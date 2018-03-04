package com.foxpower.flchatofandroid.util.other;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;

import org.w3c.dom.Text;

import java.util.UUID;

/**
 * Created by fengli on 2018/2/5.
 */

public class FLUtil {

    /*
    * 网络是否可用
    *
    * */
    public static boolean netIsConnect(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        }
        return true;
    }

    /*
    *  显示
    * */
    public static void showShortToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        if (context!=null) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }else{
            return 0;
        }
    }

    public static String createUUID() {

        return UUID.randomUUID().toString();
    }

    /*
    * 图片保存路径
    * */
    public static String imageSavePath() {

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/images/";
        FileUtils.createOrExistsDir(path);
        return path;
    }

    /*
    * 音频保存路径
    * */
    public static String audioSavePath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audios/";
        FileUtils.createOrExistsDir(path);
        return path;
    }

    public static String videoSavePath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videos/";
        FileUtils.createOrExistsDir(path);
        return path;
    }

    // 毫秒转秒
    public static String long2String(long time) {
        int sec = (int)time;
        int min = sec/60;
        sec = sec%60;
        if (min<10){
            if (sec<10) {
                return "0" + min + ":0" + sec;
            } else {
                return "0" + min + ":" + sec;
            }
        } else {
            if (sec < 10) {
                return min + ":0" + sec;
            } else {
                return min + ":" + sec;
            }
        }
    }
}




