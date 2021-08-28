package com.mm.recorduisdk.recorder.musicpanel.edit.model;

import android.view.View;

import androidx.annotation.NonNull;

import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;

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
