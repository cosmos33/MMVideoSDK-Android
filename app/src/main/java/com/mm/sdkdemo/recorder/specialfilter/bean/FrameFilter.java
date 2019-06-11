package com.mm.sdkdemo.recorder.specialfilter.bean;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

import project.android.imageprocessing.inter.IEffectFilterDataController;

public class FrameFilter {
    @DrawableRes
    private int normalRes;
    @ColorRes
    private int color;

    private String name;
    @NonNull
    private IEffectFilterDataController dataController;


    private long startTime;
    private long endTime;

    // 上报用
    private String tag;


    public FrameFilter(@DrawableRes int normalRes, String name, @ColorRes int color, IEffectFilterDataController dataController, @NonNull String tag) {
        this.normalRes = normalRes;
        this.name = name;
        this.dataController = dataController;
        this.color = color;
        this.tag = tag;
    }

    public int getNormalRes() {
        return normalRes;
    }

    public void setNormalRes(int normalRes) {
        this.normalRes = normalRes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IEffectFilterDataController getEffectFilterDataController() {
        return dataController;
    }

    public void setEffectFilterDataController(IEffectFilterDataController dataController) {
        this.dataController = dataController;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getTotleTime() {
        return this.endTime - this.startTime;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "name=" + name +
                "dataController=" + dataController +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public boolean isValid() {
        return startTime < endTime;
    }

    public boolean canUse(long time) {
        if (!isValid()) {
            return false;
        }
        return time >= startTime && time <= endTime;
    }

    public static FrameFilter clone(FrameFilter frameFilter) {
        FrameFilter result = new FrameFilter(frameFilter.normalRes, frameFilter.name, frameFilter.getColor(), frameFilter.getEffectFilterDataController(), frameFilter.getTag());
        result.setStartTime(frameFilter.startTime);
        result.setEndTime(frameFilter.endTime);
        return result;
    }

}
