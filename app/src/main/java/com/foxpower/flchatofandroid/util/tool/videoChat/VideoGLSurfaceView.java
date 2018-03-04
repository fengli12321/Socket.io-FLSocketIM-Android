package com.foxpower.flchatofandroid.util.tool.videoChat;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by fengli on 2018/3/3.
 */

public class VideoGLSurfaceView extends GLSurfaceView {

    FLVideoRenderer mRenderer;


    public VideoGLSurfaceView(Context context) {
        super(context);
        //为了可以激活log和错误检查，帮助调试3D应用，需要调用setDebugFlags()。
        this.setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
        mRenderer = new FLVideoRenderer();
        this.setRenderer(mRenderer);
    }

    public VideoGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //为了可以激活log和错误检查，帮助调试3D应用，需要调用setDebugFlags()。
        this.setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
        mRenderer = new FLVideoRenderer();
        this.setRenderer(mRenderer);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //由于DemoRenderer对象运行在另一个线程中，这里采用跨线程的机制进行处理。使用queueEvent方法
        //当然也可以使用其他像Synchronized来进行UI线程和渲染线程进行通信。
        this.queueEvent(new Runnable() {

            @Override
            public void run() {
            }
        });

        return true;
    }

    class FLVideoRenderer implements GLSurfaceView.Renderer {


        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

            //当surface的尺寸发生改变时，该方法被调用，。往往在这里设置ViewPort。或者Camara等。
            gl.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {

            //每帧都需要调用该方法进行绘制。绘制时通常先调用glClear来清空framebuffer。
            //然后调用OpenGL ES其他接口进行绘制
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        }
    }
}
