package com.mm.recorduisdk.recorder.model;

import android.support.annotation.NonNull;
import android.view.View;

import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;

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
        return new CementAdapter.IViewHolderCreator<ViewHolder>() {
            @NonNull
            @Override
            public ViewHolder create(@NonNull View view) {
                return new ViewHolder(view);
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
