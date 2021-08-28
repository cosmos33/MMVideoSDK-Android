package com.mm.recorduisdk.base.cement;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;

/**
 * @author xudong
 * @since 2017/2/10
 */

public class SimpleCementAdapter extends HeaderFooterCementAdapter<CementModel<?>> {
    @NonNull
    @Override
    Collection<? extends CementModel<?>> transData(@NonNull CementModel<?> data) {
        return Collections.singletonList(data);
    }

    @NonNull
    @Override
    protected Collection<CementModel<?>> transDataList(
            @NonNull Collection<CementModel<?>> dataList) {
        return dataList;
    }

    @Override
    public void notifyDataChanged(@NonNull CementModel<?> data) {
        notifyModelChanged(data);
    }

    @Override
    public void removeData(@NonNull CementModel<?> data) {
        if (dataList.remove(data)) {
            removeModel(data);
        }
        checkEmptyView();
    }
}
