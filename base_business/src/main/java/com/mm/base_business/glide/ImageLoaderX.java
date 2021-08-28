package com.mm.base_business.glide;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mm.base_business.utils.UIUtils;
import com.mm.mmutil.app.AppContext;

/**
 * 图片加载工具
 * @author wangduanqing
 */
public class ImageLoaderX {
    private String imageId;
    private boolean reset;

    private int imageWidth;
    private int imageHeight;

    private int leftTopPx;
    private int leftBottomPx;
    private int rightTopPx;
    private int rightBottomPx;

    private boolean showDefaultImg = false;
    private int defaultImageResId;

    private RequestListener requestListener;

    private boolean showCross = false; //是否显示渐显动画
    private int durationCross = 300;

    private RequestOptions requestOptions;

    public ImageLoaderX(@NonNull String imageId, boolean reset) {
        this.imageId = imageId;
        this.reset = reset;
    }

    public static ImageLoaderX load(@NonNull String imageId) {
        return new ImageLoaderX(imageId, false);
    }

    public static ImageLoaderX loadWithReset(@NonNull String imageId) {
        return new ImageLoaderX(imageId, true);
    }

    public ImageLoaderX width(int width) {
        this.imageWidth = width;
        return this;
    }

    public ImageLoaderX height(int height) {
        this.imageHeight = height;
        return this;
    }

    public ImageLoaderX size(int width, int height) {
        this.imageWidth = width;
        this.imageHeight = height;
        return this;
    }

    public ImageLoaderX cornerRadius() {
        return cornerRadius(UIUtils.getPixels(6));
    }

    public ImageLoaderX cornerRadius(int radius) {
        return cornerRadius(radius, radius, radius, radius);
    }

    public ImageLoaderX cornerRadius(int topLeft, int topRight, int bottomRight, int bottomLeft) {
        this.leftTopPx = topLeft;
        this.rightTopPx = topRight;
        this.rightBottomPx = bottomRight;
        this.leftBottomPx = bottomLeft;
        return this;
    }

    public ImageLoaderX showDefault() {
        this.showDefaultImg = true;
        return this;
    }

    public ImageLoaderX showDefault(@DrawableRes @ColorRes int defaultImageResId) {
        this.showDefaultImg = true;
        this.defaultImageResId = defaultImageResId;
        return this;
    }

    public ImageLoaderX shoeCrossFade() {
        this.showCross = true;
        return this;
    }

    public ImageLoaderX shoeCrossFade(int duration) {
        this.showCross = true;
        durationCross = duration;
        return this;
    }

    public ImageLoaderX requestOptions(RequestOptions requestOptions) {
        this.requestOptions = requestOptions;
        return this;
    }

    //<editor-fold desc="Action Method">
    public Bitmap intoBitmap(boolean cache) {
        return null;
    }

    public Bitmap loadAsync() {
        try {
            return Glide.with(AppContext.getContext()).asBitmap().load(imageId).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isCachedInDisk() {
        return false;
    }

    public void into(@NonNull ImageView targetView) {
        try {
            //            GlideApp.with(AppContext.getContext()).load(imageId).centerCrop().placeholder(R.drawable.ic_launcher).into(targetView);
            if (defaultImageResId > 0) {
                GlideApp.with(AppContext.getContext()).load(imageId).centerCrop().placeholder(defaultImageResId).into(targetView);
            } else {
                Glide.with(AppContext.getContext()).load(imageId).into(targetView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //        GlideApp.with(myFragment)
        //                .load(url)
        //                .centerCrop()
        //                .placeholder(R.drawable.loading_spinner)
        //                .into(myImageView);
    }
    //</editor-fold>
}
