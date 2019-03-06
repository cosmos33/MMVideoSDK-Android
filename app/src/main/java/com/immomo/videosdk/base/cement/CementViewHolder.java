package com.immomo.videosdk.base.cement;

import android.view.View;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

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
