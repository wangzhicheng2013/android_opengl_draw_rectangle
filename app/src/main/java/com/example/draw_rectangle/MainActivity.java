package com.example.draw_rectangle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.draw_rectangle.databinding.ActivityMainBinding;

import java.util.concurrent.ConcurrentLinkedDeque;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'draw_rectangle' library on application startup.
    static {
        System.loadLibrary("draw_rectangle");
    }

    private ActivityMainBinding binding;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String TAG = "MainActivity";
    private ImageView mQuitButton;
    private DisplaySurfaceView mDisplaySurfaceView;
    private CameraCaptureThread mCameraCaptureThread;
    private YuvRenderThread mYuvRenderThread;
    public static ConcurrentLinkedDeque<byte[]> mQueue = new ConcurrentLinkedDeque<byte[]>();
    public static final int QUEUE_SIZE = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
        if (false == checkPermissions()) {
            return;
        }
    }
    private void initView() {
        mQuitButton = findViewById(R.id.quit_btn);
        mQuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "main activity exit!");
                finish();
                System.exit(0);
            }
        });
        mDisplaySurfaceView = findViewById(R.id.surfaceView);
    }
    private boolean initThreads() {
        mCameraCaptureThread = new CameraCaptureThread(this);
        if (false == mCameraCaptureThread.init()) {
            return false;
        }
        mYuvRenderThread = new YuvRenderThread(mDisplaySurfaceView);
        if (false == mYuvRenderThread.init()) {
            return false;
        }
        mCameraCaptureThread.start();
        mYuvRenderThread.start();
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[]{ android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_CAMERA_PERMISSION);
    }
    // 权限请求结果处理 权限通过 打开相机
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "请授予相机权限！", Toast.LENGTH_SHORT).show();
            } else {
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (false == initThreads()) {
            Log.e(TAG, "init threads failed!");
        }
        else {
            Log.d(TAG, "init threads success!");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mDisplaySurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDisplaySurfaceView.onPause();
    }
    /**
     * A native method that is implemented by the 'draw_rectangle' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}