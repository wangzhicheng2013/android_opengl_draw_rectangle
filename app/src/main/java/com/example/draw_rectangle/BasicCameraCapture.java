package com.example.draw_rectangle;

public interface BasicCameraCapture {
    void setCameraId(int id);
    boolean initCamera();
    void setCameraFormat(int format);
    void setCameraScale(int width, int height);
    int getFrameLen();
    void setStreamCaptureCallback(StreamCaptureCallback cb);
}
