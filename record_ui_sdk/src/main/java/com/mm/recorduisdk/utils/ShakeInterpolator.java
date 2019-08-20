package com.mm.recorduisdk.utils;

import android.view.animation.Interpolator;

/**
 * Created by XiongFangyu on 2018/7/18.
 */
public class ShakeInterpolator implements Interpolator {
    private int tempIntervalTimes;
    private float lastOut = 1;
    private float offset = 1;
    private boolean offsetChanged = false;

    private int intervalTimes = 10;

    public ShakeInterpolator() {
        this(10);
    }

    public ShakeInterpolator(int intervalTimes) {
        this(intervalTimes, 1);
    }

    public ShakeInterpolator(int intervalTimes, float offset) {
        setIntervalTimes(intervalTimes);
        setOffset(offset);
    }

    public void setIntervalTimes(int intervalTimes) {
        this.intervalTimes = intervalTimes;
    }

    public void addIntervalTimes() {
        this.intervalTimes++;
    }

    public void minusIntervalTimes() {
        this.intervalTimes--;
        if (intervalTimes <= 0)
            intervalTimes = 0;
    }

    public void setOffset(float offset) {
        offsetChanged = this.offset != offset;
        this.offset = offset;
    }

    @Override
    public float getInterpolation(float input) {
        if (tempIntervalTimes >= intervalTimes) {
            if (offsetChanged) {
                float out = offset;
                if (lastOut < 0) {
                    lastOut = -out;
                } else {
                    lastOut = out;
                }
            }
            tempIntervalTimes = 0;
            lastOut = -lastOut;
            return lastOut;
        }
        tempIntervalTimes++;
        return lastOut;
    }
}