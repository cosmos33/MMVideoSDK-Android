package com.mm.sdkdemo.widget.recyclerview.layoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by xudong on 2016/11/21.
 */

public class GridLayoutManagerWithSmoothScroller extends GridLayoutManager
        implements IItemVisibilityCalculator {
    private int verticalSnapPreference = LinearSmoothScroller.SNAP_TO_START;
    private int horizontalSnapPreference = LinearSmoothScroller.SNAP_TO_START;
    private float scrollTimeFactor = 1f;

    public GridLayoutManagerWithSmoothScroller(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public GridLayoutManagerWithSmoothScroller(Context context, int spanCount) {
        super(context, spanCount);
    }

    public GridLayoutManagerWithSmoothScroller(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    @NonNull
    @Size(2)
    public int[] getVisibleItemRange() {
        return new int[]{findFirstVisibleItemPosition(),
                findLastVisibleItemPosition()};
    }

    @Override
    @NonNull
    @Size(2)
    public int[] getCompletelyVisibleItemRange() {
        return new int[]{findFirstCompletelyVisibleItemPosition(),
                findLastCompletelyVisibleItemPosition()};
    }

    @Override
    public boolean isVisible(int position) {
        int[] range = getVisibleItemRange();
        return range[0] <= position && position <= range[1];
    }

    @Override
    public boolean isCompletelyVisible(int position) {
        int[] range = getCompletelyVisibleItemRange();
        return range[0] <= position && position <= range[1];
    }

    public void setVerticalSnapPreference(int verticalSnapPreference) {
        this.verticalSnapPreference = verticalSnapPreference;
    }

    public void setHorizontalSnapPreference(int horizontalSnapPreference) {
        this.horizontalSnapPreference = horizontalSnapPreference;
    }

    public void setScrollTimeFactor(float scrollTimeFactor) {
        this.scrollTimeFactor = scrollTimeFactor;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                       int position) {
        RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    private class TopSnappedSmoothScroller extends LinearSmoothScroller {
        public TopSnappedSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return GridLayoutManagerWithSmoothScroller.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int getVerticalSnapPreference() {
            return verticalSnapPreference;
        }

        @Override
        protected int getHorizontalSnapPreference() {
            return horizontalSnapPreference;
        }

        @Override
        protected int calculateTimeForDeceleration(int dx) {
            return (int) (super.calculateTimeForDeceleration(dx) * scrollTimeFactor);
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            return (int) (super.calculateTimeForScrolling(dx) * scrollTimeFactor);
        }
    }
}
