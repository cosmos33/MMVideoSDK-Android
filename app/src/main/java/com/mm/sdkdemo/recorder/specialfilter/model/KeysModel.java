package com.mm.sdkdemo.recorder.specialfilter.model;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.cement.CementAdapter;
import com.mm.sdkdemo.base.cement.CementModel;
import com.mm.sdkdemo.base.cement.CementViewHolder;
import com.mm.sdkdemo.recorder.specialfilter.view.SpecialPanelViewHelper;

/**
 * create by wang.renguang  2018/8/13
 */
public class KeysModel extends CementModel<KeysModel.ViewHolder> {

    private Bitmap bitmap;

    public KeysModel(Bitmap bitmap) {
        this.bitmap = bitmap;

    }

    @Override
    public int getLayoutRes() {
        return R.layout.edit_video_key_filter_model;
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
        holder.img.setImageBitmap(bitmap);
    }

    public static class ViewHolder extends CementViewHolder {

        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            int width = (int) ((UIUtils.getScreenWidth() - UIUtils.getPixels(15) * 2F) / SpecialPanelViewHelper.KEY_COUNT + 0.5F);
            img = (ImageView) itemView;
            ViewGroup.LayoutParams layoutParams = img.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.width = width;
                img.setLayoutParams(layoutParams);
            }
        }
    }
}
