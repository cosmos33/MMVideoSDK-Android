package com.immomo.videosdk.recorder.model;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.immomo.videosdk.base.cement.CementAdapter;
import com.immomo.videosdk.base.cement.CementModel;
import com.immomo.videosdk.base.cement.CementViewHolder;
import com.immomo.videosdk.R;

import androidx.annotation.NonNull;

/**
 * Created by chenxin on 2018/10/15.
 */

public class DirectoryModel extends CementModel<DirectoryModel.ViewHolder> {

    private String directory;

    public DirectoryModel() {
    }

    public DirectoryModel(String directory) {
        this.directory = directory;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_layout_multimedia_directory;
    }

    @NonNull
    @Override
    public CementAdapter.IViewHolderCreator<DirectoryModel.ViewHolder> getViewHolderCreator() {
        return new CementAdapter.IViewHolderCreator<DirectoryModel.ViewHolder>() {
            @NonNull
            @Override
            public DirectoryModel.ViewHolder create(@NonNull View view) {
                return new DirectoryModel.ViewHolder(view);
            }
        };
    }

    @Override
    public void bindData(@NonNull DirectoryModel.ViewHolder holder) {
        super.bindData(holder);
        if (!TextUtils.isEmpty(directory)) {
            holder.view.setText(directory);
        }
    }

    public static class ViewHolder extends CementViewHolder {

        public final TextView view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.text);
        }
    }

    @Override
    public int getSpanSize(int totalSpanCount, int position, int itemCount) {
        return 3;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }
}
