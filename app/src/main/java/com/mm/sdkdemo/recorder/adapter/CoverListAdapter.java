package com.mm.sdkdemo.recorder.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Project momodev
 * Package com.mm.momo.moment
 * Created by tangyuchun on 12/27/16.
 */

public class CoverListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_EMPTY = 404;
    private final int VIEW_TYPE_IMAGE = 1;
    /**
     * RecyclerView中item的尺寸
     */
    protected int itemWidth;
    protected int itemHeight;
    /**
     * 头部和尾部的空白距离
     */
    protected int emptyHeaderFooterWidth;

    private List<Bitmap> bmpList;

    public CoverListAdapter(int itemWidth, int itemHeight, List<Bitmap> bmpList) {
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
        this.bmpList = bmpList;
    }

    public class CoverViewHolder extends RecyclerView.ViewHolder {
        public CoverViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setEmptyHeaderFooterWidth(int emptyHeaderFooterWidth) {
        this.emptyHeaderFooterWidth = emptyHeaderFooterWidth;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_IMAGE:
                CoverListAdapter.CoverImageView iv = new CoverListAdapter.CoverImageView(parent.getContext());
                iv.setLayoutParams(new RecyclerView.LayoutParams(itemWidth, itemHeight));
                iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                return new CoverListAdapter.CoverViewHolder(iv);
            default:
                View v = new View(parent.getContext());
                v.setLayoutParams(new RecyclerView.LayoutParams(emptyHeaderFooterWidth, itemHeight));
//                v.setBackgroundColor(Color.WHITE);
                return new CoverListAdapter.EmptyViewHolder(v);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //空白的头部 尾部
        if (position == 0 || position == bmpList.size() + 1) {
            return VIEW_TYPE_EMPTY;
        }
        return VIEW_TYPE_IMAGE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_IMAGE) {
            ImageView iv = (ImageView) holder.itemView;
            //减去头部
            iv.setImageBitmap(bmpList.get(position - 1));
        }
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public int getEmptyHeaderFooterWidth() {
        return emptyHeaderFooterWidth;
    }

    /**
     * 包含两个空白的头部 尾部
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return bmpList.size() + 2;
    }

    private class CoverImageView extends ImageView {
        public CoverImageView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(0x7F000000);
        }
    }
}
