package com.function.takephoto;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.core.glcore.config.MRConfig;
import com.core.glcore.config.Size;
import com.mm.mdlog.MDLog;
import com.mm.mediasdk.IMultiRecorder;
import com.mm.mediasdk.MoMediaManager;
import com.immomo.moment.config.MRecorderActions;
import com.mm.sdkdemo.DemoApplication;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.log.LogTag;
import com.mm.sdkdemo.recorder.activity.BaseFullScreenActivity;

import java.io.File;

public class TakePhotoTestActivity extends BaseFullScreenActivity {

    private IMultiRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo_test);
        recorder = MoMediaManager.createRecorder();
        recorder.prepare(this, getConfig());
        SurfaceView surfaceView = findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                //view预览分辨率
                recorder.setVisualSize(width, height);
                recorder.setPreviewDisplay(holder);
                recorder.startPreview();
                //                //磨皮，美颜
                //                recorder.setSkinAndLightingLevel(1f);
                //                recorder.setSkinLightingScale(1f);
                //                //瘦脸
                //                recorder.setFaceThinScale(1f);
                //                //大眼
                //                recorder.setFaceEyeScale(1f);
                //                //瘦身
                //                recorder.setSlimmingScale(1f);
                //                //长腿
                //                recorder.setLongLegScale(1f);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        recorder.stopPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recorder.release();
    }

    private MRConfig getConfig() {
        MRConfig mrConfig = MRConfig.obtain();
        mrConfig.setDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        Size size = new Size(1280, 720);
        mrConfig.setEncodeSize(size);
        // 设置camera 的采集分辨率
        mrConfig.setTargetVideoSize(size);
        return mrConfig;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.take_photo:
                File file = new File(Environment.getExternalStorageDirectory(), "take_photo.jpg");
                recorder.takePhoto(file.getAbsolutePath(), new MRecorderActions.OnTakePhotoListener() {
                    @Override
                    public void onTakePhotoComplete(int status, Exception e) {
                        //0表示完成， -1表示失败
                        MDLog.e(LogTag.RECORDER.RECORD, "onTakePhotoComplete %d", status);
                    }
                });
                break;

            case R.id.switch_camera:
                recorder.switchCamera();
                break;
        }
    }
}
