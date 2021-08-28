package com.mm.recorduisdk.widget.seekbar;

import androidx.annotation.ColorInt;

/**
 * created by zhuangguangquan on 2018/6/6
 * <p>
 * for collecting each section track color
 */
public interface ColorCollector {
    boolean collectSectionTrackColor(@ColorInt int[] colorIntArr);
}