package com.example.draw_rectangle;

import static javax.microedition.khronos.opengles.GL10.GL_LUMINANCE;
import static javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_BYTE;

import android.opengl.GLES30;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class YUV420Display extends ImageDisplay {
    private final static String TAG = "YUV420Display";
    protected int mPositionHandle = 0;
    protected int mTextureHandle = 0;
    protected ByteBuffer mBuffer = null;
    protected int mYHandle = 0;
    protected int mUVHandle = 0;

    public void display(byte[] data, int width, int height) {
        // glGetUniformLocation返回一个整数，表示程序对象中特定统一变量的位置
        mYHandle = GLES30.glGetUniformLocation(mProgramId, "YTexture");
        if (true == checkGLError()) {
            Log.e(TAG, "glGetUniformLocation(mProgramId, \"YTexture\") error!");
            return;
        }
        mUVHandle = GLES30.glGetUniformLocation(mProgramId, "UVTexture");
        if (true == checkGLError()) {
            Log.e(TAG, "glGetUniformLocation(mProgramId, \"UVTexture\") error!");
            return;
        }
        mPositionHandle = GLES30.glGetAttribLocation(mProgramId, "vPosition");
        // 使能已生成的顶点属性数组的索引
        GLES30.glEnableVertexAttribArray(mPositionHandle);
        // 用于将当前的顶点属性与顶点缓冲对象（VBO）关联起来。
        GLES30.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 12, mVertexBuffer);

        mTextureHandle = GLES30.glGetAttribLocation(mProgramId, "aTexCoord");
        GLES30.glEnableVertexAttribArray(mTextureHandle);
        GLES30.glVertexAttribPointer(mTextureHandle, 2, GLES30.GL_FLOAT, false, 8, mCoordBuffer);

        if (null == mBuffer) {
            mBuffer = ByteBuffer.allocateDirect(width * height * 3 / 2).order(ByteOrder.nativeOrder());
        }
        mBuffer.put(data).position(0);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexName[0]);
        // 根据指定的参数,生成一个2D纹理 GL_UNSIGNED_BYTE 应该与格式 GL_RGBA 一起使用，为每个组件提供 8 位。
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GL_LUMINANCE, width, height, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, mBuffer);
        GLES30.glUniform1i(mYHandle, 0);    // 对接到着色器

        mBuffer.position(width * height);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexName[1]);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_LUMINANCE_ALPHA, width / 2, height / 2, 0, GLES30.GL_LUMINANCE_ALPHA, GL_UNSIGNED_BYTE, mBuffer);
        GLES30.glUniform1i(mUVHandle, 1);
        // Draw the square
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        mBuffer.position(0);
        // Disable vertex array
        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(mTextureHandle);
    }
    void makeTexture() {
        mTexName = new int[2];
        GLES30.glGenTextures(2, mTexName ,0);                       // 生成纹理

        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexName[0]);    // 将指定纹理绑定到目标上
        setTextureParaments();
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, mTexName[1]);
        setTextureParaments();
    }
    public void bindLuminanceImage(int textureId, int width, int height, ByteBuffer byteBuffer) {
        byteBuffer.position(0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,0,
                GLES30.GL_LUMINANCE, width, height,
                0, GLES30.GL_LUMINANCE, GLES30.GL_UNSIGNED_BYTE, byteBuffer);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }
    public void bindLuminanceAlphaImage(int textureId, int width, int height, ByteBuffer byteBuffer) {
        byteBuffer.position(0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D,0,
                GLES30.GL_LUMINANCE_ALPHA, width / 2, height / 2,
                0, GLES30.GL_LUMINANCE_ALPHA, GLES30.GL_UNSIGNED_BYTE, byteBuffer);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }
}
