package com.immomo.videosdk.recorder.musicpanel.edit.model;

import android.view.View;

import com.immomo.videosdk.base.cement.CementAdapter;
import com.immomo.videosdk.base.cement.CementModel;
import com.immomo.videosdk.base.cement.CementViewHolder;
import com.immomo.videosdk.R;

import androidx.annotation.NonNull;

/**
 * create time 2018/11/20
 * by wangrenguang
 */
public class ClearMusicModel extends CementModel<ClearMusicModel.ViewHolder> {

    @Override
    public int getLayoutRes() {
        return R.layout.edit_video_clear_music_item;
    }

    @NonNull
    @Override
    public CementAdapter.IViewHolderCreator<ViewHolder> getViewHolderCreator() {
        return new CementAdapter.IViewHolderCreator<ViewHolder>() {
            @NonNull
            @Override
            public ViewHolder create(@NonNull View view) {
                return new ViewHolder(view);
            }
        };
    }

    public class ViewHolder extends CementViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
