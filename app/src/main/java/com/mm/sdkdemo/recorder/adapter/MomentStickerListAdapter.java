package com.mm.sdkdemo.recorder.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.bean.MomentSticker;
import com.mm.sdkdemo.glide.ImageLoaderX;

import java.util.ArrayList;

/**
 * Created by xudong on 16/7/25.
 */

public class MomentStickerListAdapter extends RecyclerView.Adapter<MomentStickerListAdapter.ViewHolder> {
    private OnClickListener onClickListener;
    private ArrayList<MomentSticker> stickers;
    private RecyclerView mRecyclerView;

    public MomentStickerListAdapter(ArrayList<MomentSticker> stickers, RecyclerView pRecyclerView) {
        this.stickers = stickers;
        mRecyclerView = pRecyclerView;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    public MomentSticker getItem(int pos) {
        return stickers.get(pos);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //do some load
        MomentSticker sticker = getItem(position);
        ImageLoaderX.load(sticker.getPic()).into(holder.stickerImageView);
    }

    @Override
    public int getItemCount() {
        return stickers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.layout_moment_sticker_item;
    }

    public interface OnClickListener {
        void onClick(View view, ViewHolder vh, int position);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView stickerImageView;

        public ViewHolder(final View itemView) {
            super(itemView);
            this.stickerImageView = (ImageView) itemView.findViewById(R.id.sticker_iv);
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
