package com.mm.sdkdemo.recorder.specialfilter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import project.android.imageprocessing.EffectTimeBean;
import project.android.imageprocessing.filter.BasicFilter;
import project.android.imageprocessing.filter.SimpleAbsEffectFilter;
import project.android.imageprocessing.inter.IEffectFilterDataController;

public class SpeicalFilterGroupWapper {

    private List<IEffectFilterDataController> filters = new ArrayList<>();

    public SpeicalFilterGroupWapper() {
        resetAll();
    }

    public void registerFilter(IEffectFilterDataController effectFilterDataController){
        filters.add(effectFilterDataController);
    }

    public void resetAll() {
        for (IEffectFilterDataController dataController : filters) {
            dataController.clearEffectTimeInfos();
            dataController.setGlobalEffect(false);
        }
    }

    public void resetFilter(IEffectFilterDataController effectFilterDataController) {
        if (effectFilterDataController != null) {
            effectFilterDataController.clearEffectTimeInfos();
            effectFilterDataController.setGlobalEffect(false);
        }
    }

    @NonNull
    public List<BasicFilter> getFilters() {
        ArrayList<BasicFilter> basicFilters = new ArrayList<>();
        for (IEffectFilterDataController effectFilterDataController : filters) {
            basicFilters.add(effectFilterDataController.getBasicFilter());
        }
        return basicFilters;
    }


    private SimpleAbsEffectFilter initMirrorFilter(@NonNull SimpleAbsEffectFilter src, @NonNull SimpleAbsEffectFilter desc) {
        List<EffectTimeBean> effectTimeBeans = src.getEffectTimeList();
        if (effectTimeBeans == null) {
            return desc;
        }
        int size = effectTimeBeans.size();
        for (int i = 0; i < size; i++) {
            EffectTimeBean temp = effectTimeBeans.get(i);
            EffectTimeBean effectTimeBean = new EffectTimeBean(temp.mStartTime, temp.mEndTime);
            desc.addEffectTimeInfo(effectTimeBean);
        }
        return desc;
    }

    public void updateFilter(@NonNull IEffectFilterDataController effectFilterDataController, long start, long end) {
        IEffectFilterDataController temp = effectFilterDataController;
        if (temp != null) {
            EffectTimeBean effectTimeBean = new EffectTimeBean(start, end);
            temp.addEffectTimeInfo(effectTimeBean);
        }
    }

    @Nullable
    public IEffectFilterDataController getFilterByTypeClass(Class type) {
        for (IEffectFilterDataController effectFilterDataController : filters) {
            if (effectFilterDataController.getClass().equals(type)) {
                return effectFilterDataController;
            }
        }
        return null;
    }
}
