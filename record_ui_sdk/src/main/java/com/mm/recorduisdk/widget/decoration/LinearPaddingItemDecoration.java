package com.mm.recorduisdk.widget.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.mm.recorduisdk.R;

/**
 * Created by xudong on 16/09/28.
 * PaddingItemDecoration for LinearLayoutManager
 */
public class LinearPaddingItemDecoration extends RecyclerView.ItemDecoration {
    public static final int NO_LEFT = 0x0001;
    public static final int NO_TOP = 0x0010;
    public static final int NO_RIGHT = 0x0100;
    public static final int NO_BOTTOM = 0x1000;

    /**
     * {@link #orientation} = {@link OrientationHelper#VERTICAL}:
     * padding along the horizontal adjacent items
     * {@link #orientation} = {@link OrientationHelper#HORIZONTAL}:
     * padding along the vertical adjacent items
     */
    private int headerPadding, footerPadding, itemPadding;

    private int orientation = OrientationHelper.VERTICAL;
    private LinearLayoutManager linearLayoutManager;

    private Drawable dividerDrawable;

    public LinearPaddingItemDecoration(int headerPadding, int footerPadding, int itemPadding) {
        this.headerPadding = headerPadding;
        this.footerPadding = footerPadding;
        this.itemPadding = itemPadding;
    }

    public void setFooterPadding(int footerPadding) {
        this.footerPadding = footerPadding;
    }

    public void setDivider(Drawable drawable) {
        dividerDrawable = drawable;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (linearLayoutManager == null && parent.getLayoutManager() instanceof LinearLayoutManager) {
            linearLayoutManager = (LinearLayoutManager) parent.getLayoutManager();
            orientation = linearLayoutManager.getOrientation();
        }
        if (linearLayoutManager == null) return;

        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == RecyclerView.NO_POSITION) return;

        final int itemCount = state.getItemCount();
        switch (orientation) {
            case OrientationHelper.VERTICAL: {
                /** first position */
                if (itemPosition == 0) {
                    outRect.set(outRect.left, headerPadding, outRect.right, itemPadding);
                }
                /** write bottomPadding of positions after first */
                if (itemPosition > 0) {
                    outRect.set(outRect.left, outRect.top, outRect.right, itemPadding);
                }
                /** write bottomPadding of last position */
                if (itemPosition == itemCount - 1) {
                    outRect.set(outRect.left, outRect.top, outRect.right, footerPadding);
                }

                /** only hide itemPadding */
                Object paddingInfoObj = view.getTag(R.id.padding_info);
                if (paddingInfoObj instanceof Integer) {
                    int paddingInfo = (int) paddingInfoObj;
                    outRect.set(
                            outRect.left,
                            (hasMark(paddingInfo, NO_TOP) && itemPosition > 0) ?
                                    -itemPadding : outRect.top,
                            outRect.right,
                            (hasMark(paddingInfo, NO_BOTTOM) && itemPosition < itemCount - 1) ?
                                    0 : outRect.bottom);
                }
                break;
            }
            case OrientationHelper.HORIZONTAL: {
                /** first position */
                if (itemPosition == 0) {
                    outRect.set(headerPadding, outRect.top, itemPadding, outRect.bottom);
                }
                /** write rightPadding of positions after first */
                if (itemPosition > 0) {
                    outRect.set(outRect.left, outRect.top, itemPadding, outRect.bottom);
                }
                /** write rightPadding of last position */
                if (itemPosition == itemCount - 1) {
                    outRect.set(outRect.left, outRect.top, footerPadding, outRect.bottom);
                }

                /** only hide itemPadding */
                Object paddingInfoObj = view.getTag(R.id.padding_info);
                if (paddingInfoObj instanceof Integer) {
                    int paddingInfo = (int) paddingInfoObj;
                    outRect.set(
                            (hasMark(paddingInfo, NO_LEFT) && itemPosition > 0) ?
                                    -itemPadding : outRect.left,
                            outRect.top,
                            (hasMark(paddingInfo, NO_RIGHT) && itemPosition < itemCount - 1) ?
                                    0 : outRect.right,
                            outRect.bottom);
                }
                break;
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);

        if (dividerDrawable == null) return;
        if (orientation == OrientationHelper.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        int left = parent.getLeft() + parent.getPaddingLeft();
        int right = parent.getRight() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int pos = 0; pos < childCount; pos++) {
            View child = parent.getChildAt(pos);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            if (pos == 0) {
                int topBottom = child.getTop() - params.topMargin;
                dividerDrawable.setBounds(left, topBottom - headerPadding,
                                          right, topBottom);
                dividerDrawable.draw(c);
            }

            int bottomTop = child.getBottom() + params.bottomMargin;
            dividerDrawable.setBounds(left, bottomTop,
                                      right, bottomTop + ((pos < childCount - 1) ? itemPadding : footerPadding));
            dividerDrawable.draw(c);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int top = parent.getTop() + parent.getPaddingTop();
        int bottom = parent.getBottom() - parent.getPaddingBottom();

        int childCount = parent.getChildCount();
        for (int pos = 0; pos < childCount; pos++) {
            View child = parent.getChildAt(pos);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            if (pos == 0) {
                int leftRight = child.getLeft() - params.leftMargin;
                dividerDrawable.setBounds(leftRight - headerPadding, top,
                                          leftRight, bottom);
                dividerDrawable.draw(c);
            }

            int rightLeft = child.getRight() + params.rightMargin;
            dividerDrawable.setBounds(rightLeft, top,
                                      rightLeft + ((pos < childCount - 1) ? itemPadding : footerPadding), bottom);
            dividerDrawable.draw(c);
        }
    }

    private static boolean hasMark(int value, int mark) {
        return (value&mark) == mark;
    }
}
