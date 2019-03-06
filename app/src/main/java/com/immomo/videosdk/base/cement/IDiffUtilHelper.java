package com.immomo.videosdk.base.cement;

import androidx.annotation.NonNull;

/**
 * @author xudong
 * @since 2017/2/7
 */

public interface IDiffUtilHelper<T> {
    /**
     * see {@link androidx.recyclerview.widget.DiffUtil.Callback#areItemsTheSame(int, int)}
     */
    boolean isItemTheSame(@NonNull T item);

    /**
     * see {@link androidx.recyclerview.widget.DiffUtil.Callback#areContentsTheSame(int, int)}
     */
    boolean isContentTheSame(@NonNull T item);
}
