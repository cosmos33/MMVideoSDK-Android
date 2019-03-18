package com.mm.sdkdemo.widget.recyclerview.layoutmanager;

import android.support.annotation.NonNull;
import android.support.annotation.Size;

/**
 * Created by xudshen on 28/11/2017.
 */

public interface IItemVisibilityCalculator {
    @NonNull
    @Size(2)
    int[] getVisibleItemRange();

    @NonNull
    @Size(2)
    int[] getCompletelyVisibleItemRange();

    boolean isVisible(int position);

    boolean isCompletelyVisible(int position);
}
