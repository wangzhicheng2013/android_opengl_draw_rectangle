package com.example.draw_rectangle;

import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES30;
import android.opengl.GLES30;
import android.opengl.GLES30;
import android.renderscript.Matrix4f;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class YUV420RectangleDisplay extends YUV420Display {
    private final static String TAG = "YUV420RectangleDisplay";
    protected int mMatrixHandle;
    protected int mYuv;
    protected int mRectNumHandle;
    protected int mRectHandle;
    protected int mWindowWidth;
    protected int mWindowHeight;
    protected boolean loadHandle() {
        mMatrixHandle = GLES30.glGetUniformLocation(mProgramId, "mMatrix");
        if (true == checkGLError()) {
            return false;
        }
        mPositionHandle = GLES30.glGetAttribLocation(mProgramId, "vPos");
        if (true == checkGLError()) {
            return false;
        }
        mTextureHandle = GLES30.glGetAttribLocation(mProgramId, "vTex");
        if (true == checkGLError()) {
            return false;
        }
        mYHandle = GLES30.glGetUniformLocation(mProgramId, "sTexture");
        if (true == checkGLError()) {
            return false;
        }
        mUVHandle = GLES30.glGetUniformLocation(mProgramId, "sUvTexture");
        if (true == checkGLError()) {
            return false;
        }
        mYuv = GLES30.glGetUniformLocation(mProgramId, "sYuv");
        if (true == checkGLError()) {
            return false;
        }
        mRectNumHandle = GLES30.glGetUniformLocation(mProgramId, "iRectNum");
        if (true == checkGLError()) {
            return false;
        }
        mRectHandle = GLES30.glGetUniformLocation(mProgramId, "vRect");
        if (true == checkGLError()) {
            return false;
        }
        return true;
    }
    protected float[] getVertexPositions(Rect rect) {
        float halfWindowWidth = mWindowWidth/2.0f;
        float halfWindowHeight = mWindowHeight/2.0f;
        RectF rectf = new RectF();
        rectf.left = (rect.left - halfWindowWidth)/halfWindowWidth;
        rectf.right = (rect.right - halfWindowWidth)/halfWindowWidth;
        rectf.bottom = (rect.top - halfWindowHeight)/halfWindowHeight;
        rectf.top = (rect.bottom - halfWindowHeight)/halfWindowHeight;
        float[] pos = new float[12];
        pos[0] = rectf.left;
        pos[1] = rectf.top;
        pos[2] = 0;

        pos[3] = rectf.right;
        pos[4] = rectf.top;
        pos[5] = 0;

        pos[6] = rectf.left;
        pos[7] = rectf.bottom;
        pos[8] = 0;

        pos[9] = rectf.right;
        pos[10] = rectf.bottom;
        pos[11] = 0;
        return pos;
    }
    public void display(Matrix4f mat,
                        int textureY,
                        int textureUV,
                        Rect rect,
                        int array_size,
                        float[] array) {
        float[] positionCoords = getVertexPositions(rect);
        FloatBuffer vertexBuffer = getFloatBuffer(positionCoords);
        GLES30.glUniformMatrix4fv(mMatrixHandle, 1, false, mat.getArray(), 0);

        GLES30.glEnableVertexAttribArray(mPositionHandle);
        GLES30.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, 12, vertexBuffer);

        GLES30.glEnableVertexAttribArray(mTextureHandle);
        GLES30.glVertexAttribPointer(mTextureHandle, 2, GLES30.GL_FLOAT, false, 8, mCoordBuffer);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + textureY);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureY);
        GLES30.glUniform1i(mYHandle, textureY);

        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + textureUV);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureUV);
        GLES30.glUniform1i(mUVHandle, textureUV);

        GLES30.glUniform1i(mYuv, 1);
        if (0 == array_size) {
            GLES30.glUniform1i(mRectNumHandle, 0);
        }
        else {
            GLES30.glUniform1i(mRectNumHandle, array_size);
            FloatBuffer floatBuffer = ByteBuffer.allocateDirect(4 * 4 * array_size).order(ByteOrder.nativeOrder()).asFloatBuffer();
            floatBuffer.put(array).position(0);
            GLES30.glUniform4fv(mRectHandle, array_size, floatBuffer);
        }
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        GLES30.glDisableVertexAttribArray(mPositionHandle);
        GLES30.glDisableVertexAttribArray(mTextureHandle);
        vertexBuffer.clear();
    }
}