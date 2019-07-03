package com.mm.sdkdemo.recorder.model;

import android.support.annotation.NonNull;
import android.view.View;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.cement.CementAdapter;
import com.mm.sdkdemo.base.cement.CementModel;
import com.mm.sdkdemo.base.cement.CementViewHolder;


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
