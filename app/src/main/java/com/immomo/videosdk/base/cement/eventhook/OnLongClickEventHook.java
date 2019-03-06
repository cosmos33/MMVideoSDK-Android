package com.immomo.videosdk.base.cement.eventhook;

import android.view.View;

import com.immomo.videosdk.base.cement.CementAdapter;
import com.immomo.videosdk.base.cement.CementModel;
import com.immomo.videosdk.base.cement.CementViewHolder;

import androidx.annotation.NonNull;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

/**
 * @author xudong
 * @since 2017/2/8
 */

public abstract class OnLongClickEventHook<VH extends CementViewHolder> extends
        EventHook<VH> {
    public OnLongClickEventHook(@NonNull Class<VH> clazz) {
        super(clazz);
    }

    public abstract boolean onLongClick(@NonNull View view, @NonNull VH viewHolder, int position,
                                        @NonNull CementModel rawModel);

    @Override
    public void onEvent(@NonNull View view, @NonNull final VH viewHolder,
                        @NonNull final CementAdapter adapter) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = viewHolder.getAdapterPosition();
                CementModel rawModel = adapter.getModel(position);
                return position != NO_POSITION && rawModel != null
                        && OnLongClickEventHook.this.onLongClick(v, viewHolder, position, rawModel);
            }
        });
    }
}
