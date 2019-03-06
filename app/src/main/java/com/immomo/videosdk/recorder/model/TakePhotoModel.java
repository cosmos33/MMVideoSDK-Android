package com.immomo.videosdk.recorder.model;

import android.view.View;

import com.immomo.videosdk.base.cement.CementAdapter;
import com.immomo.videosdk.base.cement.CementModel;
import com.immomo.videosdk.base.cement.CementViewHolder;
import com.immomo.videosdk.R;

import androidx.annotation.NonNull;

/**
 * Created by chenxin on 2018/10/14.
 */

public class TakePhotoModel extends CementModel<TakePhotoModel.ViewHolder> {

    @Override
    public int getLayoutRes() {
        return R.layout.item_layout_multimedia_takephoto;
    }

    @NonNull
    @Override
    public CementAdapter.IViewHolderCreator<ViewHolder> getViewHolderCreator() {
        return new CementAdapter.IViewHolderCreator<TakePhotoModel.ViewHolder>() {
            @NonNull
            @Override
            public TakePhotoModel.ViewHolder create(@NonNull View view) {
                return new TakePhotoModel.ViewHolder(view);
            }
        };
    }

   public static class ViewHolder extends CementViewHolder {

        public final View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.item_layout);
        }
    }
}
