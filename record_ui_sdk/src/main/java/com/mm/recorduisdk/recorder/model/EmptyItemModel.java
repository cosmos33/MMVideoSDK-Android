package com.mm.recorduisdk.recorder.model;

import android.view.View;

import androidx.annotation.NonNull;

import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;


public class EmptyItemModel extends CementModel<EmptyItemModel.ViewHolder> {


    public EmptyItemModel() {

    }


    @Override
    public int getLayoutRes() {
        return R.layout.layout_empty_item;
    }

    @NonNull
    @Override
    public CementAdapter.IViewHolderCreator getViewHolderCreator() {
        return new CementAdapter.IViewHolderCreator<ViewHolder>() {
            @NonNull
            @Override
            public ViewHolder create(@NonNull View view) {
                return new ViewHolder(view);
            }
        };
    }

    @Override
    public void bindData(@NonNull ViewHolder holder) {
        super.bindData(holder);
    }


    public static class ViewHolder extends CementViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
