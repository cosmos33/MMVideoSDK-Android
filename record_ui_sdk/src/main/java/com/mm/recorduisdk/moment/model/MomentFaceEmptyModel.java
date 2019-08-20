package com.mm.recorduisdk.moment.model;

import android.support.annotation.NonNull;
import android.view.View;

import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;


/**
 * Created by chenxin on 2019/4/23.
 */
public class MomentFaceEmptyModel extends CementModel<MomentFaceEmptyModel.ViewHolder> {


    @Override
    public int getLayoutRes() {
        return R.layout.listitem_face_empty_model;
    }

    @NonNull
    @Override
    public CementAdapter.IViewHolderCreator<MomentFaceEmptyModel.ViewHolder> getViewHolderCreator() {
        return new CementAdapter.IViewHolderCreator<MomentFaceEmptyModel.ViewHolder>() {
            @NonNull
            @Override
            public MomentFaceEmptyModel.ViewHolder create(@NonNull View view) {
                return new MomentFaceEmptyModel.ViewHolder(view);
            }
        };
    }

    public class ViewHolder extends CementViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

    }
}
