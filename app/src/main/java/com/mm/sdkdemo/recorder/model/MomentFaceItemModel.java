package com.mm.sdkdemo.recorder.model;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.cement.CementAdapter;
import com.mm.sdkdemo.base.cement.CementModel;
import com.mm.sdkdemo.base.cement.CementViewHolder;
import com.mm.sdkdemo.bean.MomentFace;
import com.mm.sdkdemo.glide.ImageLoaderX;
import com.mm.sdkdemo.recorder.helper.MomentFaceUtil;

/**
 * 变脸itemModel
 * Created by momo on 2017/5/11.
 */

public class MomentFaceItemModel extends CementModel<MomentFaceItemModel.ViewHolder> {

    private final MomentFace face;
    private boolean isSelected = false;

    public MomentFaceItemModel(MomentFace face) {
        this.face = face;

        id(face.getClassId(), face.getId());
    }

    public MomentFace getFace() {
        return face;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.listitem_moment_face;
    }

    //设置选中状态
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isSelected() {
        return isSelected;
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
        super.bindData(holder);
        holder.root.setSelected(isSelected);
        holder.titleView.setText(face.getTitle());

        if (face.isEmptyFace()) {
            holder.downloadView.setVisibility(View.GONE);
            holder.loadingLayout.setVisibility(View.GONE);
            holder.iconView.setBackgroundResource(R.drawable.filter_no_select);
            holder.root.setBackgroundResource(R.drawable.bg_video_edit_icon);
        } else {
            holder.root.setBackgroundResource(R.drawable.bg_moment_face_icon);
            holder.iconView.setScaleType(ImageView.ScaleType.FIT_XY);
            ImageLoaderX.loadWithReset(face.getImage_url()).into(holder.iconView);
            if (MomentFaceUtil.isOnDownloadTask(face)) {
                if (holder.loadingLayout.getVisibility() != View.VISIBLE) {
                    holder.loadingLayout.setVisibility(View.VISIBLE);
                }
                if (holder.downloadView.getVisibility() != View.GONE) {
                    holder.downloadView.setVisibility(View.GONE);
                }
                holder.loadingView.clearAnimation();
                holder.loadingView.startAnimation(AnimationUtils.loadAnimation(holder.root.getContext(), R.anim.loading));
            } else {
                boolean isDownloaded = MomentFaceUtil.simpleCheckFaceResource(face);
                if (isDownloaded) {
                    holder.downloadView.setVisibility(View.GONE);
                    holder.loadingView.clearAnimation();
                    holder.loadingLayout.setVisibility(View.GONE);
                } else {
                    holder.loadingView.clearAnimation();
                    holder.loadingLayout.setVisibility(View.GONE);
                    holder.downloadView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void unbind(@NonNull ViewHolder holder) {
        super.unbind(holder);
        holder.loadingLayout.setVisibility(View.GONE);
        holder.loadingView.clearAnimation();
    }

    @Override
    public boolean isItemTheSame(@NonNull CementModel<?> item) {
        return super.isItemTheSame(item);
    }

    public class ViewHolder extends CementViewHolder {

        private View root;
        private ImageView loadingView, iconView, downloadView;
        private TextView titleView;
        private View loadingLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.moment_face_coverbg);
            loadingLayout = itemView.findViewById(R.id.moment_face_loading_layout);
            loadingView = itemView.findViewById(R.id.moment_face_loading);
            iconView = itemView.findViewById(R.id.moment_face_icon);
            downloadView = itemView.findViewById(R.id.moment_face_download);
            titleView = itemView.findViewById(R.id.moment_face_title);
        }

        public ImageView getIconView() {
            return iconView;
        }

    }
}
