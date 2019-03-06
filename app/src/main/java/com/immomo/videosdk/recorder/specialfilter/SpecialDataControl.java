package com.immomo.videosdk.recorder.specialfilter;

import com.immomo.videosdk.R;
import com.immomo.videosdk.recorder.specialfilter.bean.FrameFilter;
import com.immomo.videosdk.recorder.specialfilter.bean.TimeFilter;
import com.immomo.videosdk.utils.UIUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.helper.FilterIdMarking;

public class SpecialDataControl implements ISpecialDataControl {

    @NonNull
    private final LinkedList<FrameFilter> usedFrameFiltersOfSort = new LinkedList<>();

    @NonNull
    private final List<FrameFilter> frameFilter = new ArrayList<>();

    @NonNull
    private final LinkedList<FrameFilter> usedFrameFilters = new LinkedList<>();
    @NonNull
    private final SpeicalFilterGroupWapper speicalFilterGroupWapper;

    public SpecialDataControl() {

        speicalFilterGroupWapper = new SpeicalFilterGroupWapper();

        frameFilter.add(new FrameFilter(R.drawable.ic_filter_shake, "抖动", UIUtils.getColor(R.color.filter_shake), speicalFilterGroupWapper.getFilterByType(FilterIdMarking.mShakeFilterId), "3"));
        frameFilter.add(new FrameFilter(R.drawable.ic_filter_soul_out, "灵魂出窍", UIUtils.getColor(R.color.filter_soul_out), speicalFilterGroupWapper.getFilterByType(FilterIdMarking.mSoulOutFilterId), "4"));
        frameFilter.add(new FrameFilter(R.drawable.ic_filter_artifact, "故障", UIUtils.getColor(R.color.filter_artifact), speicalFilterGroupWapper.getFilterByType(FilterIdMarking.mTVArtifactFilter), "5"));
        frameFilter.add(new FrameFilter(R.drawable.ic_filter_rainwindow, "雨滴", UIUtils.getColor(R.color.filter_rainwindow), speicalFilterGroupWapper.getFilterByType(FilterIdMarking.mRainWindowFilter), "2"));
        frameFilter.add(new FrameFilter(R.drawable.ic_filter_mirror_image, "四格子", UIUtils.getColor(R.color.filter_mirror_image), speicalFilterGroupWapper.getFilterByType(FilterIdMarking.mMirrImageFrameFilter), "1"));
    }

    @NonNull
    @Override
    public List<FrameFilter> getFrameFilters() {
        return frameFilter;
    }

    /**
     * 添加新的滤镜
     * <p>
     * 找到合适位置插入  保持滤镜链表一直都是按时间有序排列
     */
    @Override
    public void insertFrameFilter(@NonNull FrameFilter insterFrameFilter) {
        int index = 0;
        FrameFilter apart = null;
        for (Iterator<FrameFilter> iter = usedFrameFilters.iterator(); iter.hasNext(); ) {
            FrameFilter frameFilter = iter.next();
            if (frameFilter == insterFrameFilter) {
                break;
            }
            if (frameFilter.getStartTime() <= insterFrameFilter.getStartTime() && frameFilter.getEndTime() >= insterFrameFilter.getEndTime()) {
                if (frameFilter.getEndTime() > insterFrameFilter.getStartTime()) {
                    // 拆分滤镜
                    apart = FrameFilter.clone(frameFilter);
                    apart.setStartTime(insterFrameFilter.getEndTime());
                    apart.setEndTime(frameFilter.getEndTime());

                    frameFilter.setEndTime(insterFrameFilter.getStartTime());
                }
                // 重叠的情况  直接插在后面
                index++;
                break;
            }
            if ((frameFilter.getStartTime() > insterFrameFilter.getStartTime())) {
                // 没有重叠的情况  插在前面
                break;
            }
            index++;
        }
        usedFrameFilters.add(index, insterFrameFilter);
        if (apart != null) {
            // 添加被拆分的滤镜
            usedFrameFilters.add(index + 1, apart);
            usedFrameFiltersOfSort.add(apart);
        }
        usedFrameFiltersOfSort.add(insterFrameFilter);
    }

    /**
     * 更新正在使用的滤镜链表
     * <p>
     * 处理滤镜叠加情况  需要更新重叠滤镜的时间  保持滤镜列表顺序 无时间重叠的滤镜
     */
    @Override
    public void updateFilterList(FrameFilter updateFrameFilter) {
        if (updateFrameFilter == null) {
            return;
        }
        boolean needHandleStartEnd = false;
        for (Iterator<FrameFilter> iter = usedFrameFilters.iterator(); iter.hasNext(); ) {
            FrameFilter frameFilter = iter.next();
            if (frameFilter != updateFrameFilter && !frameFilter.isValid()) {
                iter.remove();
            }
            if (frameFilter == updateFrameFilter) {
                needHandleStartEnd = true;
                continue;
            }
            if (needHandleStartEnd) {
                if (updateFrameFilter.getEndTime() > frameFilter.getStartTime()) {
                    frameFilter.setStartTime(updateFrameFilter.getEndTime());
                }
                break;
            }
        }
    }

    /**
     * 更新单个filter  for SingleLineGroupFilterPlus
     *
     * @param start
     * @param end
     */
    @Override
    public void syncSingleFilter(int type, long start, long end) {
        speicalFilterGroupWapper.resetAll();
        speicalFilterGroupWapper.updateFilter(type, start, end);
    }

    /**
     * 同步给SingleLineGroupFilterPlus
     */
    @Override
    public void syncGroupFilter() {
        speicalFilterGroupWapper.resetAll();
        for (Iterator<FrameFilter> iter = usedFrameFilters.iterator(); iter.hasNext(); ) {
            FrameFilter frameFilter = iter.next();
            speicalFilterGroupWapper.updateFilter(frameFilter.getBasicFilter().mFilterId, frameFilter.getStartTime(), frameFilter.getEndTime());
        }
    }

    @NonNull
    @Override
    public LinkedList<FrameFilter> getUsedFrameFilters() {
        return usedFrameFilters;
    }

    @Override
    public FrameFilter doRollback() {
        if (usedFrameFiltersOfSort.size() <= 0) {
            return null;
        }
        FrameFilter frameFilter = usedFrameFiltersOfSort.removeLast();
        if (frameFilter != null) {
            for (Iterator<FrameFilter> iter = usedFrameFilters.iterator(); iter.hasNext(); ) {
                FrameFilter temp = iter.next();
                if (frameFilter == temp) {
                    iter.remove();
                    speicalFilterGroupWapper.resetFilter(frameFilter.getBasicFilter().mFilterId);
                    break;
                }
            }
        }
        return frameFilter;
    }

    @Override
    public int getUsedFrameFilterSize() {
        return usedFrameFilters.size();
    }

    @Override
    public void clearUsedFrameFilter() {
        usedFrameFilters.clear();
        speicalFilterGroupWapper.resetAll();
    }

    @NonNull
    @Override
    public List<BasicFilter> getSpecialFilter() {
        return speicalFilterGroupWapper.getFilters();
    }
}
