package com.function.imageprocess;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.immomo.mdlog.MDLog;
import com.immomo.mediasdk.ImageProcess;
import com.immomo.mediasdk.MoMediaManager;
import com.immomo.mmutil.log.Log4Android;
import com.immomo.mmutil.task.MomoMainThreadExecutor;
import com.immomo.mmutil.toast.Toaster;
import com.immomo.videosdk.R;
import com.immomo.videosdk.config.Configs;
import com.immomo.videosdk.recorder.activity.BaseFullScreenActivity;
import com.immomo.videosdk.utils.DeviceUtils;
import com.immomo.videosdk.utils.filter.FiltersManager;

import java.io.File;

import project.android.imageprocessing.FastImageProcessingView;

public class ImageProcessTestActivity extends BaseFullScreenActivity {
    private ImageProcess imageProcess;
    private FastImageProcessingView processingView;
    private final String TAG = "ImageProcessTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process_test);
        processingView = findViewById(R.id.media_cover_image);

        imageProcess = MoMediaManager.createImageProcessor();
        imageProcess.initFilters(FiltersManager.getAllFilters());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.choose_image:
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 101);
                break;

            case R.id.addFilter:
                imageProcess.switchFilterPreview(2);
                break;
            case R.id.export_image:
                imageProcess.setImageProcessListener(new ImageProcess.ImageProcessListener() {
                    @Override
                    public void onProcessCompleted(final String path) {
                        MDLog.e(TAG, "onProcessCompleted %s", path);
                    }

                    @Override
                    public void onProcessFailed() {
                        MDLog.e(TAG, "onProcessFailed");
                    }
                });
                imageProcess.startImageProcess(null, null, 0, 0);
                break;
            case R.id.skin:
                imageProcess.updateSkinLevel(1f);
                break;
            case R.id.skin_light:
                imageProcess.updateSkinLightingLevel(1f);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageProcess.release();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && null != data) {
            Uri selectedVideo = data.getData();
            String processFilePath = DeviceUtils.uri2Path(getApplicationContext(), selectedVideo);
            //处理后保存的路径
            File file = new File(Configs.getDir("ProcessImage"), System.currentTimeMillis() + "_process.jpg");
            imageProcess.init(this, processFilePath, processingView, file.getAbsolutePath());
            int[] widthHeight = loadMediaInfo(processFilePath);
            processingView.getHolder().setFixedSize(widthHeight[0], widthHeight[1]);
        }
    }

    /**
     * 获取图片信息
     */
    private int[] loadMediaInfo(String originPath) {
        int[] widthHeight = new int[2];
        boolean needRotate = false;

        Bitmap mPreviewBitmap = com.core.glcore.util.BitmapPrivateProtocolUtil.getBitmap(originPath);
        if (mPreviewBitmap != null) {
            widthHeight[0] = mPreviewBitmap.getWidth();
            widthHeight[1] = mPreviewBitmap.getHeight();
            return widthHeight;
        }

        try {
            ExifInterface exifInfo = new ExifInterface(originPath);
            int width = exifInfo.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
            int height = exifInfo.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
            int orientation = exifInfo.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);

            if (width != 0 && height != 0) {

                needRotate = (orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270);
                if (needRotate) {
                    widthHeight[0] = height;
                    widthHeight[1] = width;
                } else {
                    widthHeight[0] = width;
                    widthHeight[1] = height;
                }
            }

        } catch (Exception ex) {
            Log4Android.getInstance().e(ex);
        }

        if (widthHeight[0] == 0 || widthHeight[1] == 0) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(originPath, options);
            if (needRotate) {
                widthHeight[1] = options.outWidth;
                widthHeight[0] = options.outHeight;
            } else {
                widthHeight[1] = options.outHeight;
                widthHeight[0] = options.outWidth;
            }

        }
        return widthHeight;
    }
}
