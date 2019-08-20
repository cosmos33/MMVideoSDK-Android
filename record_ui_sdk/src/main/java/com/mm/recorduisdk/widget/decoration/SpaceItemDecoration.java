package com.mm.recorduisdk.widget.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by joel on 16/3/16.
 * <p/>
 * Momo Tech 2011-2016 © All Rights Reserved.
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int bottomSpace;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    public SpaceItemDecoration(int space, int bottom) {
        this.space = space;
        this.bottomSpace = bottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildPosition(view);
//        if(position % 4 != 3) {
//            outRect.right = space;
//        }
        outRect.right = space;
        outRect.bottom = bottomSpace;
    }
}
