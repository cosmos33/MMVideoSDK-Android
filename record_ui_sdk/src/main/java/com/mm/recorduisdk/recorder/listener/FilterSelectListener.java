package com.mm.recorduisdk.recorder.listener;

import com.mm.recorduisdk.widget.BeautyAdapterData;

/**
 * Created by liuhuan on 2017/6/12.
 */
public interface FilterSelectListener {
    void onFilterTabSelect(int selectPosition);

    void onBeautyTabSelect(int selectPosition, int type, BeautyAdapterData data);

    void onBeautyMoreChanged(float[] value, int type, BeautyAdapterData currentData);
}
