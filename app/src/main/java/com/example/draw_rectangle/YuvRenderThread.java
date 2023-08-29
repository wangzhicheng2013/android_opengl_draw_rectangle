package com.example.draw_rectangle;

import android.util.Log;

import java.nio.ByteBuffer;

public class YuvRenderThread extends Thread {
    private static final String TAG = "YuvRenderThread";
    private byte[] nv12_y;
    private byte[] nv12_uv;
    private int y_size = CameraCaptureThread.CAMERA_WIDTH * CameraCaptureThread.CAMERA_HEIGHT;
    private int uv_size = y_size / 2;
    private DisplaySurfaceView mDisplaySurfaceView;
    public YuvRenderThread(DisplaySurfaceView displaySurfaceView) {
        mDisplaySurfaceView = displaySurfaceView;
        mDisplaySurfaceView.mGLRender.mWidth = CameraCaptureThread.CAMERA_WIDTH;
        mDisplaySurfaceView.mGLRender.mHeight = CameraCaptureThread.CAMERA_HEIGHT;
    }
    public boolean init() {
        nv12_y = new byte[y_size];
        nv12_uv = new byte[uv_size];
        Log.d(TAG, "yuv render thread init ok!");
        return true;
    }
    @Override
    public void run() {
        while (true) {
            if (true == MainActivity.mQueue.isEmpty()) {
                continue;
            }
            byte[] data = MainActivity.mQueue.poll();
            System.arraycopy(data, 0, nv12_y, 0, y_size);
            System.arraycopy(data, y_size, nv12_uv, 0, uv_size);
            Log.d(TAG, "get data:" + data.length);
            float [] array = new float[4 * 2];
            array[0] = 0.41f;
            array[1] = 0.48f;
            array[2] = 0.52f;
            array[3] = 0.62f;

            array[4] = 0.63f;
            array[5] = 0.61f;
            array[6] = 0.71f;
            array[7] = 0.80f;
            mDisplaySurfaceView.mGLRender.setArray(4 * 2, array );
            ByteBuffer yBuffer = PublicTools.createByteBufferByImageData(nv12_y);
            ByteBuffer uvBuffer = PublicTools.createByteBufferByImageData(nv12_uv);
            mDisplaySurfaceView.mGLRender.setImageData(yBuffer, uvBuffer);
            mDisplaySurfaceView.requestRender();
        }
    }
}
