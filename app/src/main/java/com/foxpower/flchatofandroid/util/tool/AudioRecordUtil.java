package com.foxpower.flchatofandroid.util.tool;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.foxpower.flchatofandroid.util.other.FLLog;
import com.foxpower.flchatofandroid.util.other.FLUtil;

import java.io.File;
import java.io.Flushable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.logging.LogRecord;

/**
 * Created by fengli on 2018/2/27.
 */

public class AudioRecordUtil {

    // 文件路径
    private String filePath;
    // 文件夹路径
    private String folderPath;
    // 文件名称
    private String audioName;

    private MediaRecorder mMediaRecorder;
    private final String TAG = "fan";
    private static final int MAX_LENGTH = 1000 * 60 * 10;

    private OnAudioStatusUpdateListener audioStatusUpdateListener;
    public AudioRecordUtil() {

        this(FLUtil.audioSavePath());
    }

    public AudioRecordUtil(String filePath) {

        File path = new File(filePath);
        this.folderPath = filePath;

    }


    private long startTime;
    private long endTime;


    /*
    * 开始录音
    *
    * */
    public void startRecord(Context context) {

        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }

        try{
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            audioName = FLUtil.createUUID() + ".amr";
            filePath = folderPath + audioName;

            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();

            mMediaRecorder.start();

            startTime = System.currentTimeMillis();
            updateMicStatus();
        }catch (IllegalStateException e){

            audioStatusUpdateListener.onError();
        }catch (IOException e) {

            audioStatusUpdateListener.onError();
        }
    }

    /*
    * 停止录音
    * */
    public long stopRecord() {

        if (mMediaRecorder == null){
            return 0L;
        }
        endTime = System.currentTimeMillis();

        mMediaRecorder.setOnErrorListener(null);
        mMediaRecorder.setPreviewDisplay(null);

        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;

        long time = endTime - startTime;
        audioStatusUpdateListener.onStop(time/1000, filePath, audioName);
        filePath = "";
        return  endTime - startTime;
    }

    /*
    * 取消录音
    * */
    public void cancelRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        File file = new File(filePath);
        if (file.exists())
            file.delete();
        filePath = null;
    }

    private final Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = new Runnable() {
        @Override
        public void run() {
            updateMicStatus();
        }
    };

    private int BASE = 1;
    private int SPACE = 100;

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener audioStatusUpdateListener) {
        this.audioStatusUpdateListener = audioStatusUpdateListener;
    }

    /*
    * 更新麦克风状态
    * */
    private void updateMicStatus () {

        if (mMediaRecorder != null) {
            double ratio = (double)mMediaRecorder.getMaxAmplitude()/BASE;
            double db = 0;
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);

                FLLog.i("db======" + db);
                if (null != audioStatusUpdateListener) {
                    audioStatusUpdateListener.onUpdate(db, (System.currentTimeMillis() - startTime)/1000);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, SPACE);
        }
    }

    public interface OnAudioStatusUpdateListener {

        public void onUpdate(double db, long time);

        public void onStop(long time, String filePath, String audioName);

        public void onError();
    }
}
