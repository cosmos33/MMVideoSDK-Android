package com.function.recordvideo;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.core.glcore.config.MRConfig;
import com.core.glcore.config.Size;
import com.immomo.moment.config.MRecorderActions;
import com.cosmos.mdlog.MDLog;
import com.immomo.moment.config.MRecorderActions;
import com.mm.mediasdk.IMultiRecorder;
import com.mm.mediasdk.MoMediaManager;
import com.mm.mediasdk.utils.CameraSizeUtil;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.recorder.activity.BaseFullScreenActivity;

import java.io.File;

public class VideoRecordTestActivity extends BaseFullScreenActivity {
    private static final String TAG = "VideoRecordTestActivity";
    private IMultiRecorder recorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record_test);
        recorder = MoMediaManager.createRecorder();
        recorder.prepare(this, getConfig());
        File file = new File(Environment.getExternalStorageDirectory(), "video_test.mp4");
        recorder.setMediaOutPath(file.getAbsolutePath());
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

    private MRConfig getConfig() {
        MRConfig mrConfig = MRConfig.obtain();
        mrConfig.setDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        //设置码率 8M
        mrConfig.setVideoEncodeBitRate(8 << 20);
        mrConfig.setEncodeSize(CameraSizeUtil.selectMatchSize(getApplicationContext(), new Size(640, 960), 0, 16.0f / 9.0f));

//        mrConfig.setEncodeSize(size);
        //设置音频声道数
        mrConfig.setAudioChannels(1);
//        mrConfig.setUseDefaultEncodeSize(true);
        //设置视频编码帧率
        mrConfig.setVideoFPS(20);
        // 设置camera 的采集分辨率
        mrConfig.setTargetVideoSize(new Size(720, 1280));
        return mrConfig;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_record:
                recorder.startRecording();
                break;

            case R.id.pause_record:
                recorder.pauseRecording();
                break;
            case R.id.finish_record:
                recorder.finishRecord(new MRecorderActions.OnRecordFinishedListener() {
                    @Override
                    public void onFinishingProgress(int progress) {
                        MDLog.e(TAG, "onFinishingProgress %d", progress);
                    }

                    @Override
                    public void onRecordFinished() {
                        MDLog.e(TAG, "onRecordFinished");
                    }

                    @Override
                    public void onFinishError(String errMsg) {
                        MDLog.e(TAG, "onFinishError %s", errMsg);
                    }
                });
                break;

            case R.id.switch_camera:
                recorder.switchCamera();
                break;
        }
    }
}
