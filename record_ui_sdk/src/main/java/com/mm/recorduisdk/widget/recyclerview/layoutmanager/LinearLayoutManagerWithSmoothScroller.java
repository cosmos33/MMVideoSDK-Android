package com.mm.recorduisdk.widget.recyclerview.layoutmanager;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;

/**
 * Created by xudong on 16/9/9.
 */
public class LinearLayoutManagerWithSmoothScroller extends LinearLayoutManager
        implements IItemVisibilityCalculator {
    private int offset;
    private int verticalSnapPreference = LinearSmoothScroller.SNAP_TO_START;
    private int horizontalSnapPreference = LinearSmoothScroller.SNAP_TO_START;

    public LinearLayoutManagerWithSmoothScroller(Context context) {
        super(context, VERTICAL, false);
    }

    public LinearLayoutManagerWithSmoothScroller(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
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

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state,
                                       int position) {
        RecyclerView.SmoothScroller smoothScroller = new TopSnappedSmoothScroller(recyclerView.getContext());
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    private class TopSnappedSmoothScroller extends LinearSmoothScroller {
        public TopSnappedSmoothScroller(Context context) {
            super(context);
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return LinearLayoutManagerWithSmoothScroller.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            return super.calculateDtToFit(viewStart, viewEnd, boxStart, boxEnd, snapPreference) + offset;
        }

        @Override
        protected int getVerticalSnapPreference() {
            return verticalSnapPreference;
        }

        @Override
        protected int getHorizontalSnapPreference() {
            return horizontalSnapPreference;
        }
    }
}

