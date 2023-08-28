package com.example.draw_rectangle;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class ImageDisplay {
    public static final int COORDS_PER_VERTEX = 3;
    private final static String TAG = "ImageDisplay";
    protected FloatBuffer mVertexBuffer = null;
    protected FloatBuffer mCoordBuffer = null;

    protected float mPositionCoords[] = {
            // in counterclockwise order:
            -1.f, -1.f, 0.0f,    // bottom left
            1.f, -1.f, 0.0f,     // bottom right
            -1.f, 1.f, 0.0f,     // top left
            1.f, 1.f, 0.0f       // top right
    };
    protected float mTexCoords[] = {
            1.f, 1.f,
            1.f, 0.f,
            0.f, 1.f,
            0.f, 0.f,
    };
    protected int mProgramId = -1;
    protected int[] mTexName;
    public void makePositionCoords(float array[]) {
        mPositionCoords = array;
    }
    public void makeTexCoords(float array[]) {
        mTexCoords = array;
    }
    public static FloatBuffer getFloatBuffer(float[] floatArr) {
        FloatBuffer fb = ByteBuffer.allocateDirect(floatArr.length * Float.BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        fb.put(floatArr);
        fb.position(0);
        return fb;
    }
    protected void makeVertexBuffer() {
        mVertexBuffer = getFloatBuffer(mPositionCoords);
    }
    protected void makeCoordBuffer() { mCoordBuffer = getFloatBuffer(mTexCoords); }
    // 通过代码片段编译着色器
    public static int compileShader(int type, String shaderCode) {
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }
    // 链接到着色器
    public static int linkProgram(int vertexShaderId, int fragmentShaderId) {
        final int programId = GLES30.glCreateProgram();
        // 将顶点着色器加入到程序
        GLES30.glAttachShader(programId, vertexShaderId);
        // 将片元着色器加入到程序
        GLES30.glAttachShader(programId, fragmentShaderId);
        // 链接着色器程序
        GLES30.glLinkProgram(programId);
        return programId;
    }
    public static boolean checkGLError() {
        return GLES30.glGetError() != GLES30.GL_NO_ERROR;
    }
    public boolean makeProgram(String vertexShaderCode, String fragmentShaderCode) {
        int vertexShaderId = compileShader(GLES30.GL_VERTEX_SHADER, vertexShaderCode);
        if (vertexShaderId < 0) {
            return false;
        }
        int fragmentShaderId = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderCode);
        if (fragmentShaderId < 0) {
            return false;
        }
        mProgramId = linkProgram(vertexShaderId, fragmentShaderId);
        if (mProgramId < 0) {
            return false;
        }
        GLES30.glUseProgram(mProgramId);
        if (true == checkGLError()) {
            Log.e(TAG, "make GL program error!");
            return false;
        }
        return true;
    }
    public static void setTextureParaments() {
        // GL_LINEAR: 线性插值过滤，获取坐标点附近4个像素的加权平均值
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);      // 对纹理的设置
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);      // 使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);   // GL_CLAMP_TO_EDGE：采样纹理边缘，即剩余部分显示纹理临近的边缘颜色值
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
    }
    public static String readShaderFromResource(Context context, int shaderId) {
        InputStream is = context.getResources().openRawResource(shaderId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        boolean readSuccess = true;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();
            is.close();
        } catch (Exception e) {
            readSuccess = false;
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
        if (false == readSuccess) {
            return "";
        }
        return sb.toString();
    }
}