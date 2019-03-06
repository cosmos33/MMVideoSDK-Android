package com.immomo.videosdk.base.cement;

import java.util.Collection;

import androidx.annotation.NonNull;

/**
 * @author xudong
 * @since 2017/2/10
 */

public class ExpandableCementAdapter extends HeaderFooterCementAdapter<ExpandableList> {
    @Override
    protected boolean isDataListEmpty() {
        int size = 0;
        for (ExpandableList item : dataList) {
            size += item.size();
        }
        return size == 0;
    }

    @NonNull
    @Override
    Collection<? extends CementModel<?>> transData(@NonNull ExpandableList data) {
        return data.flatten();
    }

    @Override
    public void notifyDataChanged(@NonNull ExpandableList data) {
        replaceAllDataModels();
    }
}
