package com.mm.sdkdemo.recorder.listener;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;

import com.mm.sdkdemo.utils.AnimUtils;
import com.mm.sdkdemo.widget.sticker.StickerContainerView;
import com.mm.sdkdemo.widget.sticker.StickerView;

/**
 * Created by XiongFangyu on 2017/8/9.
 */

public abstract class StickerEditListener implements StickerContainerView.StickerEditListener {
    boolean isEditing = false;
    private Interpolator interpolator = new AccelerateInterpolator();
    private View toolsLayout;

    public StickerEditListener(View toolsLayout) {
        this.toolsLayout = toolsLayout;
    }

    private void weakView(final View view) {
        Animation scale = AnimUtils.Animations.newScaleRelateToSelfCenterAnimation(1, 0, 150);
        scale.setInterpolator(interpolator);

        Animation animation = AnimUtils.Animations.playTogether(
                AnimUtils.Animations.newFadeOutAnimation(150),
                scale
        );
        view.startAnimation(animation);
        view.setVisibility(View.INVISIBLE);
    }

    private void strongView(final View view) {
        Animation scale = AnimUtils.Animations.newScaleRelateToSelfCenterAnimation(1, 0, 150);
        scale.setInterpolator(interpolator);
        Animation animation = AnimUtils.Animations.playTogether(
                AnimUtils.Animations.newFadeInAnimation(150),
                scale
        );
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public void beginEdit() {
        if (!isEditing) {
            weakView(toolsLayout);
        }
        isEditing = true;
    }

    @Override
    public void editingStickerView(StickerView stickerView) {

    }

    @Override
    public void endEdit() {
        if (isEditing) {
            strongView(toolsLayout);
        }
        isEditing = false;
    }
}
