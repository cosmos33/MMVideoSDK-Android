package com.mm.recorduisdk.recorder.listener;

import android.support.annotation.IntRange;

import com.mm.recorduisdk.utils.ScreenOrientationManager;

/**
 * Created by XiongFangyu on 2017/4/5.
 *
 * 配合{@link ScreenOrientationManager}使用，通过角度判断是否应该转屏
 */
public abstract class OrientationSwitchListener implements ScreenOrientationManager.AngleChangedListener {
    private static final int LEFT_ANGLE = 270;
    private static final int RIGHT_ANGLE = 90;
    private static final int MIDDLE = 180;
    private static final int SWITCH_OFFSET = 20;
    private static final int LEFT_SWITCH = LEFT_ANGLE + SWITCH_OFFSET;              //315
    private static final int RIGHT_SWITCH = RIGHT_ANGLE - SWITCH_OFFSET;            //45
    private static final int RIGHT_TO_LEFT_SWITCH = LEFT_ANGLE - SWITCH_OFFSET;     //225
    private static final int LEFT_TO_RIGHT_SWITCH = RIGHT_ANGLE + SWITCH_OFFSET;    //135

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_LEFT = 1;
    public static final int STATUS_RIGHT = 2;

    private int status = STATUS_NORMAL;

    private long startTime = 0;

    @Override
    public void onAngleChanged(@IntRange(from = 0, to = 359) int angle) {
        if (System.currentTimeMillis() - startTime <= getDelayTime())
            return;
        switch (status) {
            case STATUS_NORMAL:
                handleNormalStatus(angle);
                break;
            case STATUS_LEFT:
                handleLeftStatus(angle);
                break;
            case STATUS_RIGHT:
                handleRightStatus(angle);
                break;
        }
    }

    private void handleNormalStatus(int angle) {
        if (angle <= LEFT_SWITCH && angle > MIDDLE) {
            status = STATUS_LEFT;
            startTime = System.currentTimeMillis();
            toLeft();
        } else if (angle >= RIGHT_SWITCH && angle < MIDDLE) {
            status = STATUS_RIGHT;
            startTime = System.currentTimeMillis();
            toRight();
        }
    }

    private void handleLeftStatus(int angle) {
        if (angle > LEFT_SWITCH) {
            status = STATUS_NORMAL;
            startTime = System.currentTimeMillis();
            toNormal();
        } else if (angle <= LEFT_TO_RIGHT_SWITCH) {
            status = STATUS_RIGHT;
            startTime = System.currentTimeMillis();
            fromLeftToRight();
        }
    }

    private void handleRightStatus(int angle) {
        if (angle < RIGHT_SWITCH) {
            status = STATUS_NORMAL;
            startTime = System.currentTimeMillis();
            toNormal();
        } else if (angle >= RIGHT_TO_LEFT_SWITCH) {
            status = STATUS_LEFT;
            startTime = System.currentTimeMillis();
            fromRightToLeft();
        }
    }

    protected abstract void toLeft();

    protected abstract void toRight();

    protected abstract void toNormal();

    protected abstract void fromLeftToRight();

    protected abstract void fromRightToLeft();

    protected long getDelayTime() {
        return 0;
    }
}
