package com.mm.recorduisdk.recorder.musicpanel.edit.model;

import android.view.View;

import androidx.annotation.NonNull;

import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;

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