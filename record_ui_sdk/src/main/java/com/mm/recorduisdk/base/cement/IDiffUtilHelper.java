package com.mm.recorduisdk.base.cement;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;

/**
 * @author xudong
 * @since 2017/2/7
 */

public interface IDiffUtilHelper<T> {
    /**
     * see {@link DiffUtil.Callback#areItemsTheSame(int, int)}
     */
    boolean isItemTheSame(@NonNull T item);

    /**
     * see {@link DiffUtil.Callback#areContentsTheSame(int, int)}
     */
    boolean isContentTheSame(@NonNull T item);
}
