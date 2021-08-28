package com.mm.recorduisdk.base.cement.eventhook;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;

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
