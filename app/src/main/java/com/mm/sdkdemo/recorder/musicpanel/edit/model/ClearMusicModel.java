package com.mm.sdkdemo.recorder.musicpanel.edit.model;

import android.support.annotation.NonNull;
import android.view.View;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.cement.CementAdapter;
import com.mm.sdkdemo.base.cement.CementModel;
import com.mm.sdkdemo.base.cement.CementViewHolder;

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
