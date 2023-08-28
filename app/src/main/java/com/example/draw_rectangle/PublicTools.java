package com.example.draw_rectangle;

import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class PublicTools {
    private static final String TAG = "PublicTools";
    public static boolean dumpImage(String imagePath, byte[] data) {
        FileOutputStream output = null;
        boolean success = false;
        try {
            output = new FileOutputStream(imagePath, true);
            output.write(data);
            Log.i(TAG, "file:" + imagePath + " write success!");
            success = true;
        } catch (IOException e) {
            Log.e(TAG, "write file error:" + e.toString());
        }
        finally {
            try {
                output.close();
            } catch (IOException e) {
                Log.e(TAG, "file close error:" + e.toString());
            }
        }
        return success;
    }
    public static ByteBuffer createByteBufferByImageData(byte[] imageData) {
        ByteBuffer imageByteBuffer = ByteBuffer.allocateDirect(imageData.length);
        if (imageByteBuffer != null) {
            imageByteBuffer.position(0);
            imageByteBuffer.put(imageData, 0, imageData.length);
            imageByteBuffer.position(0);
        }
        return imageByteBuffer;
    }
}
