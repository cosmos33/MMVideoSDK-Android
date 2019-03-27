package com.function.imageprocess;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mm.mdlog.MDLog;
import com.mm.mediasdk.IImageProcess;
import com.mm.mediasdk.MoMediaManager;
import com.mm.mmutil.log.Log4Android;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.config.Configs;
import com.mm.sdkdemo.recorder.activity.BaseFullScreenActivity;
import com.mm.sdkdemo.utils.DeviceUtils;
import com.mm.sdkdemo.utils.filter.FiltersManager;

import java.io.File;

import project.android.imageprocessing.FastImageProcessingView;

public class ImageProcessTestActivity extends BaseFullScreenActivity {
    private IImageProcess imageProcess;
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
                imageProcess.changeToFilter(2, false, 0);
                break;
            case R.id.export_image:
                imageProcess.setImageProcessListener(new IImageProcess.ImageProcessListener() {
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
                imageProcess.setSkinLevel(1f);
                break;
            case R.id.skin_light:
                imageProcess.setSkinLightingLevel(1f);
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
            handleImageDisplay(widthHeight[0], widthHeight[1]);
        }
    }

    /**
     * 处理图片显示变形问题
     * @param imageWidth
     * @param imageHeight
     */
    private void handleImageDisplay(int imageWidth, int imageHeight) {
        int displayWidth;
        int displayHeight;
        int bgWidth = UIUtils.getScreenWidth();
        int bgHeight = UIUtils.getScreenHeight();
        if (imageWidth / (float) imageHeight >= bgWidth / (float) bgHeight) {
            displayWidth = bgWidth;
            float scale = bgWidth / (float) imageWidth;
            displayHeight = (int) (imageHeight * scale);
        } else {
            displayHeight = bgHeight;
            float scale = bgHeight / (float) imageHeight;
            displayWidth = (int) (imageWidth * scale);
        }
        int stickerMarginTop = (bgHeight - displayHeight) / 2;
        int stickerMarginLeft = (bgWidth - displayWidth) / 2;

        ViewGroup.MarginLayoutParams imageParams = new ViewGroup.MarginLayoutParams(displayWidth, displayHeight);
        imageParams.setMargins(stickerMarginLeft, stickerMarginTop, 0, 0);

        processingView.setLayoutParams(new RelativeLayout.LayoutParams(imageParams));
        processingView.getHolder().setFixedSize(imageWidth, imageHeight);
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
