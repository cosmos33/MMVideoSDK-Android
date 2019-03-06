package com.immomo.videosdk.recorder.specialfilter;

import com.momo.mcamera.mask.MirrImageFrameFilter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import project.android.imageprocessing.EffectTimeBean;
import project.android.imageprocessing.filter.BasicEffectFilter;
import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.filter.effect.TVArtifactFilter;
import project.android.imageprocessing.filter.processing.RainWindowFilter;
import project.android.imageprocessing.filter.processing.ShakeFilter;
import project.android.imageprocessing.filter.processing.SoulOutFilter;
import project.android.imageprocessing.helper.FilterIdMarking;

public class SpeicalFilterGroupWapper {

    @NonNull
    private final ShakeFilter shakeFilter = new ShakeFilter();
    @NonNull
    private final SoulOutFilter soulOutFilter = new SoulOutFilter();
    @NonNull
    private final TVArtifactFilter tvArtifactFilter = new TVArtifactFilter();
    @NonNull
    private final MirrImageFrameFilter mirrorImageFilter = new MirrImageFrameFilter();
    @NonNull
    private final RainWindowFilter rainWindowFilter = new RainWindowFilter();

    public SpeicalFilterGroupWapper() {
        resetAll();
    }


    public void resetAll() {
        shakeFilter.clearEffectTimeInfos();
        shakeFilter.setGlobalEffect(false);
        soulOutFilter.clearEffectTimeInfos();
        soulOutFilter.setGlobalEffect(false);
        tvArtifactFilter.clearEffectTimeInfos();
        tvArtifactFilter.setGlobalEffect(false);
        mirrorImageFilter.clearEffectTimeInfos();
        mirrorImageFilter.setGlobalEffect(false);
        rainWindowFilter.clearEffectTimeInfos();
        rainWindowFilter.setGlobalEffect(false);

    }

    public void resetFilter(int type) {
        BasicEffectFilter basicEffectFilter = getFilterByType(type);
        if (basicEffectFilter != null) {
            basicEffectFilter.clearEffectTimeInfos();
            basicEffectFilter.setGlobalEffect(false);
        }
    }

    @NonNull
    public List<BasicFilter> getFilters() {
        List<BasicFilter> filters = new ArrayList<>();
        filters.add(shakeFilter);
        filters.add(soulOutFilter);
        filters.add(tvArtifactFilter);
        filters.add(mirrorImageFilter);
        filters.add(rainWindowFilter);
        return filters;
    }

    /**
     * 得到一个深度复制的特效滤镜list
     * 防止不同process 使用同一个filter对象
     *
     * @return
     */
    @NonNull
    public List<BasicFilter> getMirrorFilters() {
        List<BasicFilter> filters = new ArrayList<>();
        filters.add(initMirrorFilter(shakeFilter, new ShakeFilter()));
        filters.add(initMirrorFilter(soulOutFilter, new SoulOutFilter()));
        filters.add(initMirrorFilter(tvArtifactFilter, new TVArtifactFilter()));
        filters.add(initMirrorFilter(mirrorImageFilter, new MirrImageFrameFilter()));
        filters.add(initMirrorFilter(rainWindowFilter, new RainWindowFilter()));
        return filters;
    }

    private BasicEffectFilter initMirrorFilter(@NonNull BasicEffectFilter src, @NonNull BasicEffectFilter desc) {
        List<EffectTimeBean> effectTimeBeans = src.mEffectTimeList;
        if (effectTimeBeans == null) {
            return desc;
        }
        int size = effectTimeBeans.size();
        for (int i = 0; i < size; i++) {
            EffectTimeBean temp = effectTimeBeans.get(i);
            EffectTimeBean effectTimeBean = new EffectTimeBean(temp.mStartTime, temp.mEndTime);
            desc.mEffectTimeList.add(effectTimeBean);
        }
        return desc;
    }

    public void updateFilter(@NonNull int type, long start, long end) {
        BasicEffectFilter temp = getFilterByType(type);
        if (temp != null) {
            EffectTimeBean effectTimeBean = new EffectTimeBean(start, end);
            temp.mEffectTimeList.add(effectTimeBean);
        }
    }

    @Nullable
    public BasicEffectFilter getFilterByType(int type) {
        switch (type) {
            case FilterIdMarking.mMirrImageFrameFilter:
                return mirrorImageFilter;
            case FilterIdMarking.mRainWindowFilter:
                return rainWindowFilter;
            case FilterIdMarking.mShakeFilterId:
                return shakeFilter;
            case FilterIdMarking.mSoulOutFilterId:
                return soulOutFilter;
            case FilterIdMarking.mTVArtifactFilter:
                return tvArtifactFilter;
            default:
                break;
        }
        return null;
    }
}
