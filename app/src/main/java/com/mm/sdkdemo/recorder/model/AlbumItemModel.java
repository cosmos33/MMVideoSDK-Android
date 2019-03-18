package com.mm.sdkdemo.recorder.model;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.cement.CementAdapter;
import com.mm.sdkdemo.base.cement.CementModel;
import com.mm.sdkdemo.base.cement.CementViewHolder;
import com.mm.sdkdemo.bean.VideoInfoTransBean;
import com.mm.sdkdemo.recorder.view.IAlbumFragment;
import com.mm.sdkdemo.utils.album.ItemConstant;
import com.mm.sdkdemo.widget.AlbumItemLayout;

/**
 * Created by chenxin on 2018/10/14.
 */

public class AlbumItemModel extends CementModel<AlbumItemModel.ViewHolder> {

    private VideoInfoTransBean mTransBean;
    private IAlbumFragment fragment;
    private boolean mShowImage;
    private int mImageSize;
    private Photo photo;
    private int position;


    public AlbumItemModel(IAlbumFragment fragment, VideoInfoTransBean mTransBean, Photo photo, boolean mShowImage, int mImageSize, int position) {
        this.mTransBean = mTransBean;
        this.mShowImage = mShowImage;
        this.mImageSize = mImageSize;
        this.fragment = fragment;
        this.photo = photo;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public Photo getPhoto() {
        return photo;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_layout_multimedia;
    }

    @NonNull
    @Override
    public CementAdapter.IViewHolderCreator<AlbumItemModel.ViewHolder> getViewHolderCreator() {
        return new CementAdapter.IViewHolderCreator<AlbumItemModel.ViewHolder>() {
            @NonNull
            @Override
            public AlbumItemModel.ViewHolder create(@NonNull View view) {
                return new AlbumItemModel.ViewHolder(view);
            }
        };
    }

    @Override
    public void bindData(@NonNull ViewHolder viewHolder) {
        super.bindData(viewHolder);

        if (photo == null) {
            return;
        }

        final Photo item = photo;

        // 解析Flags
        final boolean showGif = mTransBean.gifEnable && Photo.isGif(item.mimeType);

        int type = item.type;
        int gif = showGif ? ItemConstant.GIF_SHOW : ItemConstant.GIF_HIDE;

        int select;
        if (mTransBean.mode == VideoInfoTransBean.MODE_STYLE_ONE) {
            select = ItemConstant.SELECT_HIDE;
        } else {
            // 此处屏蔽只要是视频，均不显示选择按钮
            if (type == ItemConstant.TYPE_VIDEO) {
                select = ItemConstant.SELECT_HIDE;
            } else {
                select = fragment.isChecked(item) ? ItemConstant.SELECT_SELECT : ItemConstant.SELECT_UNSELECT;
            }
        }

        int status = ItemConstant.STATUS_ENABLE;
        // 图片、视频选择互斥逻辑
        switch (fragment.getCurrentSelectedType()) {
            case ItemConstant.TYPE_IMAGE:
                if (type == ItemConstant.TYPE_VIDEO) {
                    status = ItemConstant.STATUS_DISABLE;
                }
                break;

            case ItemConstant.TYPE_VIDEO:
                if (type == ItemConstant.TYPE_IMAGE) {
                    status = ItemConstant.STATUS_DISABLE;
                }
                break;
        }
        int edit = !TextUtils.isEmpty(item.tempPath) && TextUtils.equals(item.path, item.tempPath)
                ? ItemConstant.EDIT_HIDE
                : ItemConstant.EDIT_SHOW;

        int itemFlags = type | gif | select | status | edit;

        // 更新layout
        final AlbumItemLayout itemLayout = viewHolder.mItemLayout;
        itemLayout.update(itemFlags);

        if (fragment != null && fragment.isChecked(item)) {
            final int num = fragment.getSelectNumber(item);
            itemLayout.setTextNumber(num <= 0 ? "" : "" + num);
        }

        if (!mShowImage) {
            return;
        }

        final String path;
        final boolean thumbIsAvailable = TextUtils.equals(item.tempPath, item.path) && !TextUtils.isEmpty(item.thumbPath);
        if (item.type == ItemConstant.TYPE_VIDEO) {
            itemLayout.setDuration(DateUtils.formatElapsedTime(item.duration / 1000));
            path = thumbIsAvailable ? item.thumbPath : item.tempPath;
        } else {
            path = thumbIsAvailable ? item.thumbPath : ((TextUtils.isEmpty(item.tempPath) ? item.path : item.tempPath));
        }

        itemLayout.setImage(path, mImageSize, mImageSize);
    }

    public static class ViewHolder extends CementViewHolder {

        public final AlbumItemLayout mItemLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemLayout = itemView.findViewById(R.id.item_layout);
        }
    }

}
