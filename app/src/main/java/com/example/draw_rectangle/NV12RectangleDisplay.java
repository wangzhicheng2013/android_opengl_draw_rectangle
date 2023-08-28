package com.example.draw_rectangle;

import android.content.Context;
import android.graphics.Rect;
import android.opengl.GLES30;
import android.renderscript.Matrix4f;
import android.util.Log;

import java.nio.ByteBuffer;

public class NV12RectangleDisplay extends YUV420RectangleDisplay {
    private final static String TAG = "NV12RectangleDisplay";
    private Matrix4f mMatrix4f;
    public boolean init(Context context, int width, int height) {
        final float[] array = {
                0.f, 0.f,
                1.f, 0.f,
                0.f, 1.f,
                1.f, 1.f,
        };
        makeTexCoords(array);
        makeCoordBuffer();
        setSize(width, height);
        String vertexShaderCode = readShaderFromResource(context, R.raw.nv12_rectangle_vertex_shader);
        if (true == vertexShaderCode.isEmpty()) {
            Log.e(TAG, "get nv12 rectangle vertex shader string failed!");
            return false;
        }
        String fragmentShaderCode = readShaderFromResource(context, R.raw.nv12_rectangle_fragment_shader);
        if (true == fragmentShaderCode.isEmpty()) {
            Log.e(TAG, "get nv12 rectangle fragment shader string failed!");
            return false;
        }
        Log.d(TAG, "now to make program!");
        if (false == makeProgram(vertexShaderCode, fragmentShaderCode)) {
            return false;
        }
        Log.d(TAG, "make program ok!");
        if (false == loadHandle()) {
            Log.e(TAG, "load handle failed!");
            return false;
        }
        makeTexture();
        mMatrix4f = new Matrix4f();
        mMatrix4f.loadIdentity();
        Log.d(TAG, "NV12 Rectangle Display init OK!");
        return true;
    }
    public void bindImage(int imgWidth,
                          int imgHeight,
                          ByteBuffer byteBufferY,
                          ByteBuffer byteBufferUv) {
        bindLuminanceImage(mTexName[0], imgWidth, imgHeight, byteBufferY);
        bindLuminanceAlphaImage(mTexName[1], imgWidth, imgHeight, byteBufferUv);
    }
    public void displayImage(int arraySize, float[] array)  {
        display(mMatrix4f, mTexName[0], mTexName[1], new Rect(0, 0, mWindowHeight, mWindowHeight), arraySize, array);
    }
    public void setSize(int width, int height) {
        mWindowWidth = width;
        mWindowHeight = height;
    }
}
