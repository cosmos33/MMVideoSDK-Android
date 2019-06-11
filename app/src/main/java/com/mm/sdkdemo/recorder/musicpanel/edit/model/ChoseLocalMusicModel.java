package com.mm.sdkdemo.recorder.musicpanel.edit.model;

import android.support.annotation.NonNull;
import android.view.View;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.cement.CementAdapter;
import com.mm.sdkdemo.base.cement.CementModel;
import com.mm.sdkdemo.base.cement.CementViewHolder;

/**
 * Created on 2019/5/24.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class ChoseLocalMusicModel extends CementModel<ChoseLocalMusicModel.ViewHolder> {

    @Override
    public int getLayoutRes() {
        return R.layout.edit_video_chose_local_music_item;
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