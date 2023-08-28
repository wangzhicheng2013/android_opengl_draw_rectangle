package com.example.draw_rectangle;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class DisplaySurfaceView extends GLSurfaceView {
    private final static String TAG = "DisplaySurfaceView";
    private final static int OPENGL_ES_VERSION = 3;
    public GLRender mGLRender;
    public DisplaySurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(OPENGL_ES_VERSION);             // 设置opengl版本
        mGLRender = new GLRender(context);
        setRenderer(mGLRender);                                    // 设置渲染器
        setRenderMode(RENDERMODE_WHEN_DIRTY);                      // 设置渲染模式 只有在创建和调用requestRender()时才会刷新
        Log.d(TAG,"DisplaySurfaceView(Context context) init");
    }
    public DisplaySurfaceView(Context context, AttributeSet attrs) {
        super(context,attrs);
        setEGLContextClientVersion(OPENGL_ES_VERSION);             // 必须开始就设置
        mGLRender = new GLRender(context);
        setRenderer(mGLRender);                                    // 设置渲染器
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        Log.d(TAG,"DisplaySurfaceView(Context context, AttributeSet attrs) init");
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        Log.d(TAG,"surfaceCreated(SurfaceHolder holder)");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        super.surfaceChanged(holder, format, w, h);
        Log.d(TAG,"surfaceChanged(SurfaceHolder holder, int format, int w, int h)");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        Log.d(TAG,"surfaceDestroyed");
    }
}
