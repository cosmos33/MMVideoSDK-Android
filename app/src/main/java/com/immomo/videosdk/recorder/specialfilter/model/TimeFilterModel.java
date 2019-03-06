package com.immomo.videosdk.recorder.specialfilter.model;

import android.view.View;
import android.widget.TextView;

import com.immomo.videosdk.base.cement.CementAdapter;
import com.immomo.videosdk.base.cement.CementModel;
import com.immomo.videosdk.base.cement.CementViewHolder;
import com.immomo.videosdk.R;
import com.immomo.videosdk.recorder.specialfilter.bean.TimeFilter;

import androidx.annotation.NonNull;

public class TimeFilterModel extends CementModel<TimeFilterModel.ViewHolder> {

    @NonNull
    private final TimeFilter timeFilter;

    public TimeFilterModel(@NonNull TimeFilter timeFilter) {
        this.timeFilter = timeFilter;
    }

    @NonNull
    public TimeFilter getTimeFilter() {
        return timeFilter;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.edit_video_time_filter_model;
    }

    @NonNull
    @Override
    public CementAdapter.IViewHolderCreator<ViewHolder> getViewHolderCreator() {
        return new CementAdapter.IViewHolderCreator<ViewHolder>() {
            @NonNull
            @Override
            public ViewHolder create(@NonNull View view) {
                return new TimeFilterModel.ViewHolder(view);
            }
        };
    }

    @Override
    public void bindData(@NonNull ViewHolder holder) {
        holder.name.setText(timeFilter.getName());
        holder.cover.setBackgroundResource(timeFilter.getNormalRes());
        if (timeFilter.isSelect()) {
            holder.name.setSelected(true);
            //holder.cover.setScaleX(1.15f);
            //holder.cover.setScaleY(1.15f);
            holder.cover.setBackgroundResource(R.drawable.ic_filter_selected);
        } else {
            holder.name.setSelected(false);
            //holder.cover.setScaleX(1.0f);
            //holder.cover.setScaleY(1.0f);
        }

    }

    public static class ViewHolder extends CementViewHolder {
        public View cover;
        public TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            cover = itemView.findViewById(R.id.filter_img);
            name = itemView.findViewById(R.id.fiter_name);
        }
    }

}
