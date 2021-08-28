package com.mm.recorduisdk.moment.model;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.cosmos.mdlog.MDLog;
import com.mm.base_business.glide.ImageLoaderX;
import com.mm.base_business.utils.UIUtils;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;
import com.mm.recorduisdk.bean.MomentFace;
import com.mm.recorduisdk.recorder.helper.MomentFaceUtil;


/**
 * Created by chenxin on 2019/4/19.
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

        if (isSelected) {
            holder.selectBg.setVisibility(View.VISIBLE);
        } else {
            holder.selectBg.setVisibility(View.GONE);
        }

        //标签
        String tag = face.getTag();
        if (TextUtils.equals("蝴蝶头饰", tag)) {
            MDLog.i("jianxi", face.getTagColor() + "");
        }
        if (face.hasSound()) {
            if (TextUtils.isEmpty(tag)) {
                holder.tagView.setVisibility(View.GONE);
                holder.soundView.setVisibility(View.VISIBLE);
            } else {
                holder.tagView.setVisibility(View.VISIBLE);
                holder.tagView.setText(tag);
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(TextUtils.isEmpty(face.getTagColor()) ? UIUtils.getColor(R.color.C07) : Color.parseColor(face.getTagColor()));
                gd.setCornerRadius(UIUtils.getPixels(2));
                holder.tagView.setBackground(gd);
                holder.tagView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_moment_face_sound, 0, 0, 0);

                holder.soundView.setVisibility(View.GONE);
            }
        } else {
            holder.soundView.setVisibility(View.GONE);
            holder.tagView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            if (TextUtils.isEmpty(tag)) {
                holder.tagView.setVisibility(View.GONE);
            } else {
                holder.tagView.setVisibility(View.VISIBLE);
                holder.tagView.setText(tag);
                GradientDrawable gd = new GradientDrawable();
                gd.setColor(TextUtils.isEmpty(face.getTagColor()) ? UIUtils.getColor(R.color.C07) : Color.parseColor(face.getTagColor()));
                gd.setCornerRadius(UIUtils.getPixels(2));
                holder.tagView.setBackground(gd);
            }
        }

        holder.iconView.setScaleType(ImageView.ScaleType.FIT_XY);
        //加载icon
        ImageLoaderX.loadWithReset(face.getImage_url()).cornerRadius(UIUtils.getPixels(8)).into(holder.iconView);

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
        private View loadingLayout, soundView, selectBg;
        private TextView tagView;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView;
            tagView = (TextView) itemView.findViewById(R.id.moment_face_tag);
            soundView = itemView.findViewById(R.id.moment_face_only_sound);
            loadingLayout = itemView.findViewById(R.id.moment_face_loading_layout);
            loadingView = (ImageView) itemView.findViewById(R.id.moment_face_loading);
            iconView = (ImageView) itemView.findViewById(R.id.moment_face_icon);
            downloadView = (ImageView) itemView.findViewById(R.id.moment_face_download);
            selectBg = itemView.findViewById(R.id.moment_face_select_bg);
        }

        public ImageView getIconView() {
            return iconView;
        }

    }
}
