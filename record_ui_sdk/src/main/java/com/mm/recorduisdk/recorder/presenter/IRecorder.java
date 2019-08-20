package com.mm.recorduisdk.recorder.presenter;

import android.app.Activity;

import com.core.glcore.config.MRConfig;
import com.core.glcore.config.Size;
import com.immomo.moment.config.MRecorderActions;
import com.mm.recorduisdk.recorder.view.IRecordView;
import com.momo.mcamera.mask.MaskModel;

/**
 * Created by wangduanqing on 2019/2/10.
 */
public interface IRecorder {

    int FLASH_MODE_OFF = 0;
    int FLAHS_MODE_ON = 1;
    int FLASH_MODE_AUTO = 2;

    void initWith(Activity activity, IRecordView view);

    boolean prepare();

    void startPreview();

    void takePhoto();

    void setVideoOutputPath(String outputPath);

    boolean isTakingPhoto();

    void startRecording();

    void setSpeed(float speed);

    void stopRecording();

    void cancelRecording();

    boolean finishRecord(MRecorderActions.OnRecordFinishedListener onRecordFinishedListener);

    void removeLast();

    void clearTempFiles();

    void release();

    void onResume();

    void onPause();

    boolean isFrontCamera();

    boolean supporFrontCamera();

    boolean supportFlash();

    MRConfig getMRConfig();

    boolean isStartRecorded();

    void setStartRecorded(boolean start);

    boolean isRecording();

    int getAvgBitrate();

    void focusOnTouch(double x, double y, int viewWidth, int viewHeight);

    void setFlashMode(int mode);

    Size getVisualSize();

    int getMaxZoomLevel();

    void setZoomLevel(int zoomLevel);

    int getCurrentZoomLevel();

    boolean addCustomTypeMaskModel(MaskModel maskModel);

    MaskModel getCurrentMaskModel();
}
