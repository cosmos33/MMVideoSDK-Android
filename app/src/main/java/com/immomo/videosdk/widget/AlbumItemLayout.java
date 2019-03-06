package com.immomo.videosdk.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.immomo.mmutil.StringUtils;
import com.immomo.mmutil.app.AppContext;
import com.immomo.videosdk.R;
import com.immomo.videosdk.glide.ImageLoaderX;
import com.immomo.videosdk.utils.album.ItemConstant;

import androidx.annotation.IdRes;
import androidx.appcompat.content.res.AppCompatResources;

/**
 * @author shidefeng
 * @since 2016/9/7
 */
public class AlbumItemLayout extends RatioFrameLayout {

    private View mGifView;
    private View mEditView;
    private View mShadowView;
    private View mVideoShadowView;
    private TextView mDurationView;
    public ImageView mImageView;
    public SelectView mSelectView;

    public int mItemFlags;

    public AlbumItemLayout(Context context) {
        this(context, null);
    }

    public AlbumItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlbumItemLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        View.inflate(context, R.layout.item_layout_multimedia_inner, this);

        mImageView = findView(R.id.iv_item_image);
        mShadowView = findView(R.id.v_item_shadow);
        mVideoShadowView = findView(R.id.v_video_shadow);
        mSelectView = findView(R.id.iv_item_select);
        mGifView = findView(R.id.tv_item_gif);
        mDurationView = findView(R.id.tv_item_duration);
        mEditView = findView(R.id.iv_item_edit);

        //mImageView.setOnClickListener(this);
        //mSelectView.setOnClickListener(this);
    }


    public void setImage(String path, int width, int height) {
        ImageLoaderX.load(path).size(width, height).requestOptions(new RequestOptions().centerCrop()).into(mImageView);
    }

    public void setImageWithReset(String path, int width, int height) {
        ImageLoaderX.loadWithReset(path).size(width, height).requestOptions(new RequestOptions().centerCrop()).into(mImageView);
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void setDuration(String duration) {
        mDurationView.setText(duration);
        if (StringUtils.notEmpty(duration)) {
            Drawable leftDrawable = AppCompatResources.getDrawable(AppContext.getContext(), R.drawable.ic_album_play);
            mDurationView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
            mVideoShadowView.setVisibility(VISIBLE);
        } else {
            mDurationView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            mVideoShadowView.setVisibility(GONE);
        }
    }

    public void setTextNumber(String num) {
        mSelectView.setText(num);
    }

    public void update(int itemFlags) {

        mItemFlags = itemFlags;

        updateType(itemFlags);

        updateGif(itemFlags);

        updateSelect(itemFlags);

        updateEdit(itemFlags);
    }

    private void updateType(int itemFlags) {
        // update type
        int curType = itemFlags & ItemConstant.TYPE_MASK;
        switch (curType) {
            case ItemConstant.TYPE_VIDEO:
                mDurationView.setVisibility(View.VISIBLE);
                mShadowView.setVisibility(GONE);
                break;
            case ItemConstant.TYPE_IMAGE:
            default:
                mVideoShadowView.setVisibility(GONE);
                mDurationView.setVisibility(View.GONE);
                mShadowView.setVisibility(VISIBLE);
                break;
        }
    }

    private void updateGif(int itemFlags) {
        // update gif
        int curType = itemFlags & ItemConstant.TYPE_MASK;
        int curGif = itemFlags & ItemConstant.GIF_MASK;

        if (curType == ItemConstant.TYPE_VIDEO) {
            mGifView.setVisibility(View.GONE);
        } else {
            if (curGif == ItemConstant.GIF_SHOW) {
                mGifView.setVisibility(View.VISIBLE);
            } else {
                mGifView.setVisibility(View.GONE);
            }
        }
    }

    private void updateSelect(int itemFlags) {
        int curSelect = itemFlags & ItemConstant.SELECT_MASK;
        switch (curSelect) {
            case ItemConstant.SELECT_HIDE:
                mSelectView.setVisibility(View.GONE);
                break;
            case ItemConstant.SELECT_SELECT:
                mSelectView.setSelected(true);
                mSelectView.setVisibility(View.VISIBLE);
                break;
            case ItemConstant.SELECT_UNSELECT:
            default:
                mSelectView.setSelected(false);
                mSelectView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateEdit(int itemFlags) {
        int curEdit = itemFlags & ItemConstant.EDIT_MASK;

        switch (curEdit) {
            case ItemConstant.EDIT_HIDE:
                mEditView.setVisibility(View.GONE);
                break;
            case ItemConstant.EDIT_SHOW:
            default:
                mEditView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private <V extends View> V findView(@IdRes int id) {
        return (V) findViewById(id);
    }

}
