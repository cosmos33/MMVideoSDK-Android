package com.mm.sdkdemo.recorder.specialfilter;

import android.support.annotation.NonNull;

import com.mm.sdkdemo.recorder.specialfilter.bean.FrameFilter;
import com.mm.sdkdemo.recorder.specialfilter.bean.TimeFilter;

import java.util.LinkedList;
import java.util.List;

import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.inter.IEffectFilterDataController;

public interface ISpecialDataControl {


    @NonNull
    List<FrameFilter> getFrameFilters();

    void insertFrameFilter(@NonNull FrameFilter frameFilter);

    void updateFilterList(FrameFilter frameFilter);

    void syncSingleFilter(IEffectFilterDataController effectFilterDataController, long start, long end);

    void syncGroupFilter();

    @NonNull
    LinkedList<FrameFilter> getUsedFrameFilters();

    FrameFilter doRollback();

    int getUsedFrameFilterSize();

    void clearUsedFrameFilter();

    @NonNull
    List<BasicFilter> getSpecialFilter();

    void restoreUsedFilters(long videoDurtion);

    List<TimeFilter> getTimeFilter();

    void reverseUsedFilters(long length);
}
