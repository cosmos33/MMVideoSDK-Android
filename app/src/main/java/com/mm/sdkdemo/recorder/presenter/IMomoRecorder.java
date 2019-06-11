package com.mm.sdkdemo.recorder.presenter;

import android.content.res.Configuration;
import android.support.annotation.FloatRange;
import android.view.MotionEvent;

import com.mm.sdkdemo.recorder.model.MusicContent;
import com.momo.mcamera.filtermanager.MMPresetFilter;
import com.momo.mcamera.mask.MaskModel;

import java.util.List;

/**
 * Created by XiongFangyu on 2017/11/29.
 */
public interface IMomoRecorder extends IRecorder {

    void switchCamera();

    boolean addMaskModel(MaskModel maskModel);

    void clearFace();

    MusicContent getPlayMusic();

    boolean setPlayMusic(MusicContent music);

    void changeToFilter(int index, boolean up, float offset);

    void setFilterIntensity(@FloatRange(from = 0, to = 1.0f) float intensity);

    void setItemSelectSkinLevel(float[] value);

    void setFaceEyeScale(float eyeScale);

    void setFaceThinScale(float thinFaceScale);

    void setSlimmingScale(float value);

    void setLongLegScale(float value);

    void onConfigurationChanged(Configuration newConfig);

    void initFilter(List<MMPresetFilter> filters);

    void feedCameraZoomEvent(MotionEvent motionEvent);

}
