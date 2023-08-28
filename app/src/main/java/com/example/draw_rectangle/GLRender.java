package com.example.draw_rectangle;

import android.content.Context;
import android.graphics.Rect;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.renderscript.Matrix4f;
import android.util.Log;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRender implements GLSurfaceView.Renderer {
    private final static String TAG = "GLRender";
    public int mWidth = 0;
    public int mHeight = 0;
    private ByteBuffer mByteBufferY = null;
    private ByteBuffer mByteBufferUv = null;
    private int mSurfaceWidth, mSurfaceHeight;
    private int mShowWidth, mShowHeight;
    private NV12RectangleDisplay mNV12Display;
    private int mArraySize = 0;
    private float[] mArray;
    private Context mContext;
    public GLRender(Context context) {
        mNV12Display = new NV12RectangleDisplay();
        mContext = context;
    }
    public void setImageData(ByteBuffer byteBufferY,
                             ByteBuffer byteBufferUv) {
        mByteBufferY = byteBufferY;
        mByteBufferUv = byteBufferUv;
    }
    void setArray(int arraySize, float[] array) {
        mArraySize = arraySize;
        mArray = array;
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES30.glClearColor(0.0f, 0, 0, 0.0f);
        if (false == mNV12Display.init(mContext, mWidth, mHeight)) {         // must set here
            Log.e(TAG, "NV12Display init failed!");
            throw new RuntimeException("NV12Display init failed!");
        }
        mNV12Display.setSize(mWidth, mHeight);
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
        mSurfaceWidth = width;
        mSurfaceHeight = height;
    }
    @Override
    public void onDrawFrame(GL10 gl) {
        // 画背景颜色
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        if ((0 == mWidth) || (0 == mHeight)) {
            Log.d(TAG, "width or height is 0!");
            return;
        }
        if ((null == mByteBufferY) || (null == mByteBufferUv)) {
            Log.d(TAG, "Y or UV byte buffer is null!");
            return;
        }
        adjustShowScreen();
        mNV12Display.bindImage(mWidth, mHeight, mByteBufferY, mByteBufferUv);
        mNV12Display.displayImage(mArraySize, mArray);
    }
    public void adjustShowScreen() {
        // find the most suitable window size according to the aspect ratio
        if ((mWidth == mSurfaceWidth) && (mHeight == mSurfaceHeight)) {
            return;
        }
        if ((float)mWidth / (float)mHeight < (float)mSurfaceWidth / (float)mSurfaceHeight) {
            // Wider screen
            mShowWidth = (int)((float)mSurfaceHeight * (float)mWidth / (float)mHeight);
            mShowHeight = mSurfaceHeight;
        } else {
            // Higher screen
            mShowHeight = (int)((float)mSurfaceWidth * (float)mHeight / (float)mWidth);
            mShowWidth = mSurfaceWidth;
        }
        GLES30.glViewport(0, 0, mShowWidth, mShowHeight);
    }
}
