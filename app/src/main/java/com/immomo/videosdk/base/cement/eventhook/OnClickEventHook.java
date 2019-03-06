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

public abstract class OnClickEventHook<VH extends CementViewHolder> extends EventHook<VH> {
    public OnClickEventHook(@NonNull Class<VH> clazz) {
        super(clazz);
    }

    public abstract void onClick(@NonNull View view, @NonNull VH viewHolder, int position,
                                 @NonNull CementModel rawModel);

    @Override
    public void onEvent(@NonNull View view, @NonNull final VH viewHolder,
                        @NonNull final CementAdapter adapter) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                CementModel rawModel = adapter.getModel(position);
                if (position != NO_POSITION && rawModel != null) {
                    OnClickEventHook.this.onClick(v, viewHolder, position, rawModel);
                }
            }
        });
    }
}
