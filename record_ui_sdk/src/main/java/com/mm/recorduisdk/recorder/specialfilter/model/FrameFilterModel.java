package com.mm.recorduisdk.recorder.specialfilter.model;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;
import com.mm.recorduisdk.recorder.specialfilter.bean.FrameFilter;
import com.mm.recorduisdk.recorder.specialfilter.widget.FilterImageView;

public class FrameFilterModel extends CementModel<FrameFilterModel.ViewHolder> {

    @NonNull
    private final FrameFilter frameFilter;

    public FrameFilterModel(@NonNull FrameFilter frameFilter) {
        this.frameFilter = frameFilter;
    }


    public FrameFilter getFrameFilter(){
        return frameFilter;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.edit_video_frame_filter_model;
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

    @Override
    public void bindData(@NonNull ViewHolder holder) {
        holder.name.setText(frameFilter.getName());
        holder.cover.setImageResource(frameFilter.getNormalRes());
    }

    public static class ViewHolder extends CementViewHolder {

        public TextView name;
        public FilterImageView cover;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.fiter_name);
            cover = itemView.findViewById(R.id.filter_img);
        }
    }

}
