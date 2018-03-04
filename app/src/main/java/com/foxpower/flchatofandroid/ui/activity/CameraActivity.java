package com.foxpower.flchatofandroid.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ClickListener;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.foxpower.flchatofandroid.R;
import com.foxpower.flchatofandroid.common.BaseActivity;
import com.foxpower.flchatofandroid.util.other.FLLog;
import com.foxpower.flchatofandroid.util.other.FLUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by fengli on 2018/3/2.
 */

public class CameraActivity extends BaseActivity {


    private JCameraView jCameraView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }


    @Override
    protected void initView() {
        super.initView();

        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }

        jCameraView = findViewById(R.id.jcameraview);
        jCameraView.setSaveVideoPath(FLUtil.videoSavePath());
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);

        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {

                FLLog.i("打开camera失败");
            }

            @Override
            public void AudioPermissionError() {

                FLLog.i("没有权限");
            }
        });

        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {

                sendImage(bitmap);
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {

                FLLog.i("获取到视频");
            }
        });

        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {

                goBack();
            }
        });

        jCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {

                FLLog.i("保存图片");
            }
        });
    }


    private void sendImage(Bitmap bitmap) {

        // 保存图片到本地
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] buffer = stream.toByteArray();

        String fileName = FLUtil.createUUID() + ".jpg";

        String savePath = FLUtil.imageSavePath() + fileName;

        File file = new File(savePath);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(buffer);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent();
        intent.putExtra("imageName", fileName);
        intent.putExtra("imageWidth", bitmap.getWidth());
        intent.putExtra("imageHeight", bitmap.getHeight());
        setResult(102, intent);
        goBack();
    }

}
