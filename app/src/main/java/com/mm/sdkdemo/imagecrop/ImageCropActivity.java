package com.mm.sdkdemo.imagecrop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;

import com.core.glcore.util.BitmapPrivateProtocolUtil;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.task.MomoTaskExecutor;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.config.Configs;
import com.mm.sdkdemo.recorder.activity.BaseFullScreenActivity;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.callback.BitmapCropCallback;
import com.yalantis.ucrop.view.CropImageView;
import com.yalantis.ucrop.view.GestureCropImageView;
import com.yalantis.ucrop.view.OverlayView;
import com.yalantis.ucrop.view.TransformImageView;
import com.yalantis.ucrop.view.UCropView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created on 2019/5/27.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class ImageCropActivity extends BaseFullScreenActivity {

    private UCropView mUCropView;
    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;
    public static final String SourceImagePathKey = "sourceImageUriKey";
    public static final String TargetImagePathKey = "targetImageUriKey";
    private ProgressDialog progressDialog;
    private String mSourceImagePath;
    private String mTargetImagePath;
    private View mTvReset;
    private float initScale;
    private File mTempSourceFile;

    public static void startImageCrop(Activity context, String sourceImagePath, String targetImagePath, int requestCode) {
        Intent intent = new Intent(context, ImageCropActivity.class);
        intent.putExtra(SourceImagePathKey, sourceImagePath);
        intent.putExtra(TargetImagePathKey, targetImagePath);
        context.startActivityForResult(intent, requestCode);
    }

    public static void startImageCrop(Fragment context, String sourceImagePath, String targetImagePath, int requestCode) {
        Intent intent = new Intent(context.getContext(), ImageCropActivity.class);
        intent.putExtra(SourceImagePathKey, sourceImagePath);
        intent.putExtra(TargetImagePathKey, targetImagePath);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        setImageData(getIntent());
        initState();
        initEvent();

    }

    private void initEvent() {
        mTvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGestureCropImageView.cancelAllAnimations();
                mGestureCropImageView.zoomOutImage(initScale);
                mGestureCropImageView.setMaxBitmapSize(CropImageView.DEFAULT_MAX_BITMAP_SIZE);
                mGestureCropImageView.setMaxScaleMultiplier(CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER);
                mGestureCropImageView.setImageToWrapCropBoundsAnimDuration(CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION);
                mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
                mGestureCropImageView.cancelAllAnimations();
                mGestureCropImageView.setImageToWrapCropBounds(false);

            }
        });
    }


    private void initView() {
        setContentView(R.layout.activirty_image_crop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("剪裁");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUCropView = (UCropView) findViewById(R.id.crop_view);
        mGestureCropImageView = mUCropView.getCropImageView();
        mOverlayView = mUCropView.getOverlayView();

        mGestureCropImageView.setTransformImageListener(mImageListener);

        mTvReset = findViewById(R.id.tv_reset);
    }

    private void setImageData(@NonNull final Intent intent) {
        processOptions(intent);

        showProgressDialog();
        MomoTaskExecutor.executeInnerTask(getClass(), new MomoTaskExecutor.Task() {
            @Override
            protected Object executeTask(Object[] objects) throws Exception {

                Uri inputUri = Uri.fromFile(new File(intent.getStringExtra(SourceImagePathKey)));
                Uri outputUri = Uri.fromFile(new File(intent.getStringExtra(TargetImagePathKey)));

                Bitmap bitmap = BitmapPrivateProtocolUtil.getBitmap(inputUri.getPath());

                if (bitmap != null) {
                    FileOutputStream fileOutputStream = null;
                    try {
                        mTempSourceFile = new File(Configs.getDir("ProcessImage"), "s_" + System.currentTimeMillis() + ".jpg");
                        fileOutputStream = new FileOutputStream(mTempSourceFile);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                        inputUri = Uri.fromFile(mTempSourceFile);
                        bitmap.recycle();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                if (inputUri != null && outputUri != null) {
                    try {
                        mGestureCropImageView.setImageUri(inputUri, outputUri);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                closeProgressDialog();
                            }
                        });
                    } catch (Exception e) {
                        finish();
                    }
                } else {
                    finish();
                }
                return null;
            }
        });


    }

    private void initState() {
        mGestureCropImageView.setRotateEnabled(false);
        mGestureCropImageView.setScaleEnabled(true);
    }

    private void processOptions(Intent intent) {
        // Crop image view options
        mGestureCropImageView.setMaxBitmapSize(intent.getIntExtra(UCrop.Options.EXTRA_MAX_BITMAP_SIZE, CropImageView.DEFAULT_MAX_BITMAP_SIZE));
        mGestureCropImageView.setMaxScaleMultiplier(intent.getFloatExtra(UCrop.Options.EXTRA_MAX_SCALE_MULTIPLIER, CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER));
        mGestureCropImageView.setImageToWrapCropBoundsAnimDuration(intent.getIntExtra(UCrop.Options.EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION));

        // Overlay view options
        mOverlayView.setFreestyleCropEnabled(true);

        mOverlayView.setDimmedColor(getResources().getColor(com.yalantis.ucrop.R.color.ucrop_color_default_dimmed));
        mOverlayView.setCircleDimmedLayer(OverlayView.DEFAULT_CIRCLE_DIMMED_LAYER);

        mOverlayView.setShowCropFrame(OverlayView.DEFAULT_SHOW_CROP_FRAME);
        mOverlayView.setCropFrameColor(getResources().getColor(com.yalantis.ucrop.R.color.ucrop_color_default_crop_frame));
        mOverlayView.setCropFrameStrokeWidth(getResources().getDimensionPixelSize(com.yalantis.ucrop.R.dimen.ucrop_default_crop_frame_stoke_width));

        mOverlayView.setShowCropGrid(OverlayView.DEFAULT_SHOW_CROP_GRID);
        mOverlayView.setCropGridRowCount(OverlayView.DEFAULT_CROP_GRID_ROW_COUNT);
        mOverlayView.setCropGridColumnCount(OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT);
        mOverlayView.setCropGridColor(getResources().getColor(com.yalantis.ucrop.R.color.ucrop_color_default_crop_grid));
        mOverlayView.setCropGridStrokeWidth(getResources().getDimensionPixelSize(com.yalantis.ucrop.R.dimen.ucrop_default_crop_grid_stoke_width));

        mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_crop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.done) {
            showProgressDialog();

            mGestureCropImageView.cropAndSaveImage(Bitmap.CompressFormat.PNG, 100, new BitmapCropCallback() {

                @Override
                public void onBitmapCropped(@NonNull Uri resultUri, int offsetX, int offsetY, int imageWidth, int imageHeight) {
//                    setResultUri(resultUri, mGestureCropImageView.getTargetAspectRatio(), offsetX, offsetY, imageWidth, imageHeight);

                    closeProgressDialog();
                    setResult(RESULT_OK, getIntent());
                    finish();
                }

                @Override
                public void onCropFailure(@NonNull Throwable t) {
//                    setResultError(t);
                    finish();
                }
            });


        } else {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setMessage("请稍候......");
        final Window window = progressDialog.getWindow();
        if (window != null) {
            window.setLayout(UIUtils.getPixels(170), UIUtils.getPixels(50));
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTempSourceFile != null && mTempSourceFile.exists()) {
            mTempSourceFile.delete();
        }
        MomoTaskExecutor.cancleAllTasksByTag(getClass());
        closeProgressDialog();
    }


    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
        }

        @Override
        public void onScale(float currentScale) {
            if (initScale <= 0) {
                initScale = currentScale;
            }
        }

        @Override
        public void onLoadComplete() {
            mUCropView.animate().alpha(1).setDuration(300).setInterpolator(new AccelerateInterpolator());
            mTvReset.setClickable(true);
        }

        @Override
        public void onLoadFailure(@NonNull Exception e) {
            finish();
        }

    };

}
