package com.mm.sdkdemo.recorder.listener;

/**
 * Created by liuhuan on 2017/6/12.
 */
public interface FilterSelectListener {
    void onFilterTabSelect(int selectPosition);

    void onBeautyTabSelect(int selectPosition, int type);

    void onBeautyMoreChanged(float[] value, int type);
}
