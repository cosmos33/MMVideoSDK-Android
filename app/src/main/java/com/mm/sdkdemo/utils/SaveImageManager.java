package com.mm.sdkdemo.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.LinkedList;

/**
 * Created by XiongFangyu on 2017/4/17.
 */
public class SaveImageManager extends HandlerThread implements Handler.Callback {
    private static final int SAVE_BITMAP = 0X11;
    private boolean autoRecycleBitmap = false;

    private LinkedList<BA> bas;

    private String formatFilePath;

    private int quality = 100;

    private Handler handler;

    private Callback callback;

    private boolean released = false;

    private Matrix matrix;

    public SaveImageManager() {
        super("SaveImageManager");
        matrix = new Matrix();
        bas = new LinkedList<>();
        start();
    }

    public void setAutoRecycleBitmap(boolean autoRecycleBitmap) {
        this.autoRecycleBitmap = autoRecycleBitmap;
    }

    public void setFormatFilePath(String formatFilePath) {
        this.formatFilePath = formatFilePath;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setRotate(int rotate) {
        matrix.setRotate(rotate);
    }

    public void release() {
        released = true;
        if (handler != null)
            handler.removeMessages(SAVE_BITMAP);
        quit();
        if (bas != null)
            bas.clear();
        bas = null;
        callback = null;
        matrix = null;
    }

    @Override
    protected void onLooperPrepared() {
        handler = new Handler(this);
        if (bas != null && !bas.isEmpty())
            handler.obtainMessage(SAVE_BITMAP, bas.pop()).sendToTarget();
    }

    public void putBitmap(Bitmap bmp, Object... args) {
        if (released)
            return;
        BA ba = new BA(bmp, args);
        bas.push(ba);
        if (handler != null)
            handler.obtainMessage(SAVE_BITMAP, bas.pop()).sendToTarget();
    }

    private void saveBitmap(BA ba) {
        if (released)
            return;
        final Bitmap bmp = Bitmap.createBitmap(ba.bitmap, 0, 0, ba.bitmap.getWidth(), ba.bitmap.getHeight(), matrix, true);
        final Object[] args = ba.args;
        final String path = String.format(formatFilePath, args);
        final File file = new File(path);

        if (!file.exists() || file.length() <= 0) {
            try {
                if (bmp.compress(Bitmap.CompressFormat.JPEG, quality, new FileOutputStream(file))) {
                    if (callback != null) {
                        callback.onSaveSucess(file, ba.args);
                    }
                }
            } catch (FileNotFoundException e) {
                if (callback != null) {
                    callback.onSaveError(e, ba.args);
                }
            }
        } else {
            if (callback != null) {
                callback.onSaveError(new Exception("file exits and size > 0."), ba.args);
            }
        }
        recycleBitmap(ba.bitmap);
        recycleBitmap(bmp);
    }

    private void recycleBitmap(Bitmap bitmap) {
        if (autoRecycleBitmap && bitmap != null && !bitmap.isRecycled())
            bitmap.recycle();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SAVE_BITMAP:
                saveBitmap((BA) msg.obj);
                break;
        }
        return true;
    }

    public static final class BA {
        Bitmap bitmap;
        Object[] args;

        BA() {

        }

        BA(Bitmap b, Object... args) {
            this.bitmap = b;
            this.args = args;
        }
    }

    public interface Callback {
        void onSaveSucess(File file, Object... args);

        void onSaveError(Throwable t, Object... args);
    }
}
