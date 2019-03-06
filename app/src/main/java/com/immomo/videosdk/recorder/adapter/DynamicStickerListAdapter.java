package com.immomo.videosdk.recorder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.immomo.mmutil.app.AppContext;
import com.immomo.videosdk.R;
import com.immomo.videosdk.config.Configs;
import com.immomo.videosdk.glide.ImageLoaderX;
import com.immomo.videosdk.recorder.sticker.DynamicSticker;
import com.immomo.videosdk.recorder.sticker.StickerManager;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by zhutao on 17/6/16.
 * 动态贴纸的adapter
 */

public class DynamicStickerListAdapter extends RecyclerView.Adapter<DynamicStickerListAdapter.ViewHolder> {

    /**
     * checkDownload 是为了让 onBindViewHolder 执行更快一些
     * 默认是 false 有了点击以后再置为 true
     */
    public boolean checkDownload = false;
    private OnClickListener onClickListener;
    private ArrayList<DynamicSticker> stickers;
    private RecyclerView mRecyclerView;

    public DynamicStickerListAdapter(ArrayList<DynamicSticker> stickers, RecyclerView pRecyclerView) {
        this.stickers = stickers;
        mRecyclerView = pRecyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    public DynamicSticker getItem(int pos) {
        return stickers.get(pos);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //do some load
        DynamicSticker sticker = getItem(position);
        ImageLoaderX.load(sticker.getPic()).into(holder.stickerImageView);
        if (!checkDownload){
            return;
        }
        boolean isDownloaded = StickerManager.isStickerDownloaded(sticker);
        if (isDownloaded) {
            holder.progressView.setVisibility(View.GONE);
            holder.progressView.clearAnimation();
        } else {
            if (StickerManager.isStickerDownloading(sticker)) {
                holder.progressView.setVisibility(View.VISIBLE);
                holder.progressView.startAnimation(AnimationUtils.loadAnimation(AppContext.getContext(), R.anim.loading));
            } else {
                holder.progressView.setVisibility(View.GONE);
                holder.progressView.clearAnimation();
            }
        }
    }

    @Override
    public int getItemCount() {
        return stickers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.layout_dynamic_sticker_item;
    }

    public interface OnClickListener {
        void onClick(View view, ViewHolder vh, int position);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View root;
        private ImageView stickerImageView;
        private ImageView progressView;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.root = itemView;
            this.stickerImageView = (ImageView) itemView.findViewById(R.id.dynamic_sticker_iv);
            this.progressView = (ImageView) itemView.findViewById(R.id.dynamic_sticker_progress);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onClick(itemView, ViewHolder.this, getAdapterPosition());
                    }
                }
            });
        }
    }

}
