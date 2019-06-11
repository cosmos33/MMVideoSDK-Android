package com.mm.sdkdemo.recorder.view;

import android.view.MotionEvent;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.recorder.presenter.IMomoRecorder;
import com.mm.sdkdemo.recorder.presenter.IRecorder;

/**
 * Created on 2019/5/23.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class CameraZoomChecker {

    private float oldDist;
    private float zoomUnit;
    private final IRecorder mRecorder;

    public CameraZoomChecker(IMomoRecorder recorder) {
        mRecorder = recorder;
    }

    public boolean feedEvent(MotionEvent event) {
        if (mRecorder == null) {
            return false;
        }
        if (event.getPointerCount() == 2) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = getFingerSpacing(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float newDist = getFingerSpacing(event);
                    int zoom = (int) ((newDist - oldDist) * getZoomUnit());
                    if (zoom == mRecorder.getCurrentZoomLevel()) {
                        return false;
                    }
                    // 取增量值进行缩放
                    int temp = (int) (zoom + mRecorder.getCurrentZoomLevel());
                    if (temp >= mRecorder.getMaxZoomLevel()) {
                        temp = mRecorder.getMaxZoomLevel();
                    }
                    if (temp <= 0) {
                        temp = 0;
                    }

                    mRecorder.setZoomLevel(temp);
                    break;
            }
            return true;
        }
        return false;
    }

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private float getZoomUnit() {
        if (mRecorder == null || zoomUnit > 0 || mRecorder.getMaxZoomLevel() == 0) {
            return zoomUnit;
        }
        //最大缩放值/屏幕对角线长度 为像素的缩放单位
        double temp = Math.sqrt(UIUtils.getRealScreenHeight() * UIUtils.getRealScreenHeight() + UIUtils.getScreenWidth() * UIUtils.getScreenWidth());
        if (temp == 0) {
            return zoomUnit;
        }
        zoomUnit = (float) (mRecorder.getMaxZoomLevel() / temp);
        return zoomUnit;
    }
}
