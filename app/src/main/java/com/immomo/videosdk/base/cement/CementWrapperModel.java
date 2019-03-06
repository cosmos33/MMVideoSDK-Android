package com.immomo.videosdk.base.cement;

import java.util.List;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class CementWrapperModel<VH extends CementWrapperViewHolder<MVH>,
        M extends CementModel<MVH>, MVH extends CementViewHolder> extends CementModel<VH> {
    @NonNull
    protected final M childModel;

    @NonNull
    public M getChildModel() {
        return childModel;
    }

    public CementWrapperModel(@NonNull M childModel) {
        super();
        this.childModel = childModel;
    }

    @Override
    int getViewType() {
        return 31 * super.getViewType() + childModel.getViewType();
    }

    @Override
    public boolean shouldSaveViewState() {
        return childModel.shouldSaveViewState();
    }

    @CallSuper
    @Override
    public void bindData(@NonNull VH holder) {
        childModel.bindData(holder.getChildViewHolder());
    }

    @CallSuper
    @Override
    public void bindData(@NonNull VH holder, @Nullable List<Object> payloads) {
        childModel.bindData(holder.getChildViewHolder(), payloads);
    }

    @CallSuper
    @Override
    public void unbind(@NonNull VH holder) {
        childModel.unbind(holder.getChildViewHolder());
    }

    @CallSuper
    @Override
    public void attachedToWindow(@NonNull VH holder) {
        childModel.attachedToWindow(holder.getChildViewHolder());
    }

    @CallSuper
    @Override
    public void detachedFromWindow(@NonNull VH holder) {
        childModel.detachedFromWindow(holder.getChildViewHolder());
    }

    @NonNull
    @Override
    public abstract CementAdapter.WrapperViewHolderCreator<VH, MVH> getViewHolderCreator();

    @Override
    public boolean isItemTheSame(@NonNull CementModel<?> item) {
        return super.isItemTheSame(item)
                && childModel.isItemTheSame(((CementWrapperModel) item).childModel);
    }

    @Override
    public boolean isContentTheSame(@NonNull CementModel<?> item) {
        return super.isContentTheSame(item)
                && childModel.isContentTheSame(((CementWrapperModel) item).childModel);
    }
}
