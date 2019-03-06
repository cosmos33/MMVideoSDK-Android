package com.immomo.videosdk.recorder.specialfilter.model;

import android.view.View;
import android.widget.TextView;

import com.immomo.videosdk.base.cement.CementAdapter;
import com.immomo.videosdk.base.cement.CementModel;
import com.immomo.videosdk.base.cement.CementViewHolder;
import com.immomo.videosdk.R;
import com.immomo.videosdk.recorder.specialfilter.bean.FrameFilter;
import com.immomo.videosdk.recorder.specialfilter.widget.FilterImageView;

import androidx.annotation.NonNull;

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
                return new FrameFilterModel.ViewHolder(view);
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
