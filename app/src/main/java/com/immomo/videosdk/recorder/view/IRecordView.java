package com.immomo.videosdk.recorder.view;

import android.view.SurfaceHolder;

import com.momo.mcamera.mask.MaskModel;

/**
 * Created by wangduanqing on 2019/2/10.
 */

public interface IRecordView {

    SurfaceHolder getHolder();

    void onCameraSet();

    long getMinDuration();

    void removeLast();

    int getCount();

    void refreshView(boolean isRecording);

    void resetRecordButton(boolean needAnim);

    void onStartFinish();

    void finish();

    void onTakePhoto(String path, Exception e);

    void faceDetected(boolean hasFace);

    void playStateChanged(int soundId, boolean play);

    void hidePreImageView();

    boolean isVideoDurationValid();

    void onMaskModelSet(MaskModel model);
}
