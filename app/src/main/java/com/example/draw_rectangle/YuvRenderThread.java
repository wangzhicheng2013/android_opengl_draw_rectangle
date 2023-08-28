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
            float [] array = new float[4];
            array[0] = 0.4875f;
            array[1] = 0.38055557f;
            array[2] = 0.60520834f;
            array[3] = 0.6388889f;
            mDisplaySurfaceView.mGLRender.setArray(4, array );
            ByteBuffer yBuffer = PublicTools.createByteBufferByImageData(nv12_y);
            ByteBuffer uvBuffer = PublicTools.createByteBufferByImageData(nv12_uv);
            mDisplaySurfaceView.mGLRender.setImageData(yBuffer, uvBuffer);
            mDisplaySurfaceView.requestRender();
        }
    }
}
