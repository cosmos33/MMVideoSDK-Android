package com.mm.recorduisdk.recorder.specialfilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.recorder.specialfilter.bean.FrameFilter;
import com.mm.recorduisdk.recorder.specialfilter.bean.TimeFilter;
import com.momo.mcamera.mask.MirrImageFrameFilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.filter.effect.TVArtifactFilter;
import project.android.imageprocessing.filter.processing.RainWindowFilter;
import project.android.imageprocessing.filter.processing.ShakeFilter;
import project.android.imageprocessing.filter.processing.SoulOutFilter;
import project.android.imageprocessing.filter.processing.fdk.FDKBlack3FilterGroup;
import project.android.imageprocessing.filter.processing.fdk.FDKDazzlingFilterGroup;
import project.android.imageprocessing.filter.processing.fdk.FDKHeartbeatFilter;
import project.android.imageprocessing.filter.processing.fdk.FDKShadowingFilter;
import project.android.imageprocessing.inter.IEffectFilterDataController;

public class SpecialDataControl implements ISpecialDataControl {

    @NonNull
    private final LinkedList<FrameFilter> usedFrameFiltersOfSort = new LinkedList<>();

    @NonNull
    private final List<FrameFilter> frameFilter = new ArrayList<>();

    @NonNull
    private final LinkedList<FrameFilter> usedFrameFilters = new LinkedList<>();
    @NonNull
    private final SpeicalFilterGroupWapper speicalFilterGroupWapper;
    private boolean reverseUsedFilters = false;

    public SpecialDataControl(Context context) {

        speicalFilterGroupWapper = new SpeicalFilterGroupWapper();

        registerFilter(R.drawable.ic_filter_shake, "抖动", UIUtils.getColor(R.color.filter_shake), new ShakeFilter(), "3");
        registerFilter(R.drawable.ic_filter_soul_out, "灵魂出窍", UIUtils.getColor(R.color.filter_soul_out), new SoulOutFilter(), "4");
        registerFilter(R.drawable.ic_filter_artifact, "故障", UIUtils.getColor(R.color.filter_artifact), new TVArtifactFilter(), "5");
        registerFilter(R.drawable.ic_filter_rainwindow, "雨滴", UIUtils.getColor(R.color.filter_rainwindow), new RainWindowFilter(), "2");
        registerFilter(R.drawable.ic_filter_mirror_image, "四格子", UIUtils.getColor(R.color.filter_mirror_image), new MirrImageFrameFilter(), "1");
        /*registerFilter(R.drawable.ic_filter_soul_out, "幽灵", UIUtils.getColor(R.color.filter_artifact), new FDKBGhostFilterGroup(), "6");
        registerFilter(R.drawable.ic_filter_shake, "坏电视", UIUtils.getColor(R.color.filter_shake), new FDKDistortedTVFilter(), "7");*/
        registerFilter(R.drawable.ic_filter_shake, "黑胶三格", UIUtils.getColor(R.color.filter_shake), new FDKBlack3FilterGroup(), "8");
        /*registerFilter(R.drawable.ic_filter_shake, "黑白粒子", UIUtils.getColor(R.color.filter_shake), new FDKBlackWhiteFilter(), "9");
        registerFilter(R.drawable.ic_filter_shake, "落雨", UIUtils.getColor(R.color.filter_shake), new FDKRaindropsOnWindowFilter(), "10");
        registerFilter(R.drawable.ic_filter_shake, "粒子模糊", UIUtils.getColor(R.color.filter_shake), new FDKParticleBlurFilter(), "11");
        registerFilter(R.drawable.ic_filter_shake, "噪音", UIUtils.getColor(R.color.filter_shake), new FDKGrainCamFilter(), "12");*/
        registerFilter(R.drawable.ic_filter_shake, "闪烁", UIUtils.getColor(R.color.filter_shake), new FDKDazzlingFilterGroup(), "13");
        registerFilter(R.drawable.ic_filter_shake, "心跳", UIUtils.getColor(R.color.filter_shake), new FDKHeartbeatFilter(), "14");
//        registerFilter(R.drawable.ic_filter_shake, "RGB胶片", UIUtils.getColor(R.color.filter_shake), new FDKRGBShift2Filter(), "15");
        registerFilter(R.drawable.ic_filter_shake, "VHS晃动", UIUtils.getColor(R.color.filter_shake), new FDKShadowingFilter(), "16");
//        registerFilter(R.drawable.ic_filter_shake, "变色", UIUtils.getColor(R.color.filter_shake), new FDKPartitionFilterGroup(), "17");

        /*Bitmap targetBitmap = createBitmap(context, "lookup_amatorka.png");
        if (targetBitmap != null) {
            registerFilter(R.drawable.ic_filter_shake, "闪现四格", UIUtils.getColor(R.color.filter_shake), new FDKHyperZoom4FilterGroup(targetBitmap), "18");
            targetBitmap = null;
        }*/

        /*Bitmap  targetBitmap = createBitmap(context, "film00104.jpg");
        if (targetBitmap != null) {
            registerFilter(R.drawable.ic_filter_shake, "胶片三格", UIUtils.getColor(R.color.filter_shake), new FDKFilm3FilterGroup(targetBitmap), "19");
            targetBitmap = null;
        }*/
       /* registerFilter(R.drawable.ic_filter_shake, "双面黑白", UIUtils.getColor(R.color.filter_shake), new FDKDoubleBWFilter(), "20");
        registerFilter(R.drawable.ic_filter_shake, "J抖", UIUtils.getColor(R.color.filter_shake), new FDKJitterFilter(), "21");
        registerFilter(R.drawable.ic_filter_shake, "重影", UIUtils.getColor(R.color.filter_shake), new FDKDizzyFilter(), "22");
        registerFilter(R.drawable.ic_filter_shake, "重彩四格", UIUtils.getColor(R.color.filter_shake), new FDKDuoColor4Filter(), "23");
        registerFilter(R.drawable.ic_filter_shake, "五彩电视", UIUtils.getColor(R.color.filter_shake), new FDKHueTVFilterGroup(), "24");

         targetBitmap = createBitmap(context, "film99.png");
        if (targetBitmap != null) {
            registerFilter(R.drawable.ic_filter_shake, "左滚胶片", UIUtils.getColor(R.color.filter_shake), new FDKTranslationFilterGroup(targetBitmap), "25");
            targetBitmap = null;
        }
        targetBitmap = createBitmap(context, "timg.jpg");
        if (targetBitmap != null) {
            registerFilter(R.drawable.ic_filter_shake, "彩烁", UIUtils.getColor(R.color.filter_shake), new FDKVHSStreakFilterGroup(targetBitmap), "26");
            targetBitmap = null;
        }*/
//        registerFilter(R.drawable.ic_filter_shake, "延迟宫格", UIUtils.getColor(R.color.filter_shake), new FDKGridFrameFilterGroup(), "27");

    }

    private Bitmap createBitmap(Context context, String fileName) {
        InputStream imageStream = null;
        try {
            imageStream = context.getAssets().open(fileName);
            Bitmap targetBitmap = BitmapFactory.decodeStream(imageStream);
            return targetBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (imageStream != null) {
                try {
                    imageStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageStream = null;
            }
        }
        return null;
    }

    private void registerFilter(@DrawableRes int res, String name, @ColorRes int color, IEffectFilterDataController dataController, @NonNull String tag) {
        frameFilter.add(new FrameFilter(res, name, color, dataController, tag));
        speicalFilterGroupWapper.registerFilter(dataController);
    }

    @NonNull
    @Override
    public List<FrameFilter> getFrameFilters() {
        return frameFilter;
    }

    @NonNull
    @Override
    public List<TimeFilter> getTimeFilter() {
        List<TimeFilter> list = new ArrayList<>();
        list.add(new TimeFilter(R.drawable.ic_filter_no, "无", TimeFilter.TYPE_NO, "100"));
        list.add(new TimeFilter(R.drawable.ic_filter_slow, "慢动作", TimeFilter.TYPE_SLOW, "101"));
        list.add(new TimeFilter(R.drawable.ic_filter_fast, "快动作", TimeFilter.TYPE_FAST, "102"));
        list.add(new TimeFilter(R.drawable.ic_filter_repeat, "反复", TimeFilter.TYPE_REPEAT, "103"));
        list.add(new TimeFilter(R.drawable.ic_filter_back, "倒放", TimeFilter.TYPE__BACK, "104"));
        return list;
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
        if (apart != null && apart.getEndTime() > apart.getStartTime()) {

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
    public void syncSingleFilter(IEffectFilterDataController effectFilterDataController, long start, long end) {
        speicalFilterGroupWapper.resetAll();
        speicalFilterGroupWapper.updateFilter(effectFilterDataController, start, end);
    }

    /**
     * 同步给SingleLineGroupFilterPlus
     */
    @Override
    public void syncGroupFilter() {
        speicalFilterGroupWapper.resetAll();
        for (Iterator<FrameFilter> iter = usedFrameFilters.iterator(); iter.hasNext(); ) {
            FrameFilter frameFilter = iter.next();
            speicalFilterGroupWapper.updateFilter(frameFilter.getEffectFilterDataController(), frameFilter.getStartTime(), frameFilter.getEndTime());
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
                    speicalFilterGroupWapper.resetFilter(frameFilter.getEffectFilterDataController());
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


    /**
     * 反转滤镜列表
     *
     * @param videoDurtion
     */
    @Override
    public void reverseUsedFilters(long videoDurtion) {
        if (reverseUsedFilters) {
            return;
        }
        realReverseFilters(videoDurtion);
        reverseUsedFilters = true;
    }

    /**
     * 还原反转的滤镜链表
     *
     * @param videoDurtion
     */
    @Override
    public void restoreUsedFilters(long videoDurtion) {
        if (!reverseUsedFilters) {
            return;
        }
        realReverseFilters(videoDurtion);
        reverseUsedFilters = false;
    }

    private void realReverseFilters(long videoDurtion) {
        // 反转列表 重设时间
        long tempStart;
        long tempEnd;
        speicalFilterGroupWapper.resetAll();
        for (Iterator<FrameFilter> iter = usedFrameFilters.iterator(); iter.hasNext(); ) {
            FrameFilter frameFilter = iter.next();
            tempStart = frameFilter.getStartTime();
            tempEnd = frameFilter.getEndTime();
            frameFilter.setStartTime(videoDurtion - tempEnd);
            frameFilter.setEndTime(videoDurtion - tempStart);
            speicalFilterGroupWapper.updateFilter(frameFilter.getEffectFilterDataController(), frameFilter.getStartTime(), frameFilter.getEndTime());
        }
        Collections.reverse(usedFrameFilters);
    }
}
