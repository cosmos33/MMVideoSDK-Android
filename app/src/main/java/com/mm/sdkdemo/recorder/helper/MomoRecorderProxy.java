package com.mm.sdkdemo.recorder.helper;

import com.mm.sdkdemo.recorder.presenter.IMomoRecorder;

/**
 * Created by XiongFangyu on 2017/9/5.
 */

public class MomoRecorderProxy implements RecordTipManager.RecorderProxy {
    private IMomoRecorder recorder;

    public MomoRecorderProxy(IMomoRecorder recorder) {
        this.recorder = recorder;
    }

    @Override
    public boolean isFrontCamera() {
        return recorder != null && recorder.isFrontCamera();
    }

    @Override
    public boolean isDefaultFace() {
        return false;
    }
}
