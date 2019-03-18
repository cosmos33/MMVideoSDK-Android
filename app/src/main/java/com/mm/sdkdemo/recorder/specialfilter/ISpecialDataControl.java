package com.mm.sdkdemo.recorder.specialfilter;

import android.support.annotation.NonNull;

import com.mm.sdkdemo.recorder.specialfilter.bean.FrameFilter;

import java.util.LinkedList;
import java.util.List;

import project.android.imageprocessing.filter.BasicFilter;

public interface ISpecialDataControl {


    @NonNull
    List<FrameFilter> getFrameFilters();

    void insertFrameFilter(@NonNull FrameFilter frameFilter);

    void updateFilterList(FrameFilter frameFilter);

    void syncSingleFilter(int type, long start, long end);

    void syncGroupFilter();

    @NonNull
    LinkedList<FrameFilter> getUsedFrameFilters();

    FrameFilter doRollback();

    int getUsedFrameFilterSize();

    void clearUsedFrameFilter();

    @NonNull
    List<BasicFilter> getSpecialFilter();
}
