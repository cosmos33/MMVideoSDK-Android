package com.mm.sdkdemo.base.cement.eventhook;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.mm.sdkdemo.base.cement.CementAdapter;
import com.mm.sdkdemo.base.cement.CementModel;
import com.mm.sdkdemo.base.cement.CementViewHolder;

import java.util.List;

/**
 * @author xudong
 * @since 2017/2/6
 */

public abstract class EventHook<VH extends CementViewHolder> {
    protected final CementModel getRawModel(
            @NonNull VH viewHolder,
            @NonNull CementAdapter adapter) {
        int position = viewHolder.getAdapterPosition();
        return adapter.getModel(position);
    }

    @NonNull
    final Class<VH> clazz;

    public EventHook(@NonNull Class<VH> clazz) {
        this.clazz = clazz;
    }

    public abstract void onEvent(@NonNull View view, @NonNull VH viewHolder,
                                 @NonNull CementAdapter adapter);

    @Nullable
    public View onBind(@NonNull VH viewHolder) {
        return null;
    }

    @Nullable
    public List<? extends View> onBindMany(@NonNull VH viewHolder) {
        return null;
    }
}
