package com.immomo.videosdk.base.cement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author xudong
 * @since 2017/2/10
 */

public class ExpandableList {
    @Nullable
    private final CementModel headerModel, emptyViewModel, footerModel;
    @NonNull
    private final List<CementModel<?>> childModels = new ArrayList<>();

    private boolean hideHeaderAndFooterWhenEmpty = false;

    public ExpandableList() {
        this(null, null, null);
    }

    public ExpandableList(@Nullable CementModel headerModel) {
        this(headerModel, null, null);
    }

    public ExpandableList(@Nullable CementModel headerModel,
                          @Nullable CementModel emptyViewModel,
                          @Nullable CementModel footerModel) {
        this.headerModel = headerModel;
        this.emptyViewModel = emptyViewModel;
        this.footerModel = footerModel;
    }

    public void setHideHeaderAndFooterWhenEmpty(boolean hideHeaderAndFooterWhenEmpty) {
        this.hideHeaderAndFooterWhenEmpty = hideHeaderAndFooterWhenEmpty;
    }

    @Nullable
    public CementModel getHeaderModel() {
        return headerModel;
    }

    @Nullable
    public CementModel getEmptyViewModel() {
        return emptyViewModel;
    }

    @Nullable
    public CementModel getFooterModel() {
        return footerModel;
    }

    @NonNull
    public List<CementModel<?>> getChildModels() {
        return childModels;
    }

    public int size() {
        boolean hideHeaderAndFooter = hideHeaderAndFooterWhenEmpty && childModels.isEmpty();

        int size = 0;
        if (headerModel != null && !hideHeaderAndFooter) {
            size++;
        }
        if (childModels.isEmpty()) {
            if (emptyViewModel != null) {
                size++;
            }
        } else {
            size += childModels.size();
        }
        if (footerModel != null && !hideHeaderAndFooter) {
            size++;
        }
        return size;
    }

    @NonNull
    public Collection<? extends CementModel<?>> flatten() {
        boolean hideHeaderAndFooter = hideHeaderAndFooterWhenEmpty && childModels.isEmpty();

        List<CementModel<?>> all = new ArrayList<>();
        if (headerModel != null && !hideHeaderAndFooter) {
            all.add(headerModel);
        }
        if (childModels.isEmpty()) {
            if (emptyViewModel != null) {
                all.add(emptyViewModel);
            }
        } else {
            all.addAll(childModels);
        }
        if (footerModel != null && !hideHeaderAndFooter) {
            all.add(footerModel);
        }
        return all;
    }
}
