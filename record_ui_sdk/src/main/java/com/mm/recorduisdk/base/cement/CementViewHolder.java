package com.mm.recorduisdk.base.cement;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

public class CementViewHolder extends RecyclerView.ViewHolder {
    @Nullable
    CementModel model;

    public CementViewHolder(View itemView) {
        super(itemView);
    }

    void bind(@NonNull CementModel model, @Nullable List<Object> payloads) {
        if (payloads != null && !payloads.isEmpty()) {
            // noinspection unchecked
            model.bindData(this, payloads);
        } else {
            // noinspection unchecked
            model.bindData(this);
        }

        this.model = model;
    }

    void unbind() {
        if (model == null) return;
        // noinspection unchecked
        model.unbind(this);
        model = null;
    }

    boolean shouldSaveViewState() {
        return model != null && model.shouldSaveViewState();
    }
}
