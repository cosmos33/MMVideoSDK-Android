package com.mm.sdkdemo.recorder.listener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;

import com.core.glcore.config.MRConfig;
import com.mm.sdkdemo.widget.OrientationTextView;

/**
 * Created by XiongFangyu on 2017/5/26.
 */
public class RecordOrientationSwitchListener extends OrientationSwitchListener {
    private static final int SCREEN_ROTATE_ANIM_TIME = 200;     //屏幕旋转动画时长
    ValueAnimator anim;
    private float lastAngle = 0;
    private View[] normalRotationViews;
    private MRConfig mrConfig;
    private OrientationTextView finishBtn;

    public void setNormalRotationViews(View... views) {
        normalRotationViews = views;
    }

    public void setFinishBtn(OrientationTextView btn) {
        finishBtn = btn;
    }

    public void setMrConfig(MRConfig mrConfig) {
        this.mrConfig = mrConfig;
    }

    ValueAnimator.AnimatorUpdateListener listener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float angle = (float) animation.getAnimatedValue();
            setRotation(angle, normalRotationViews);
        }
    };

    private void setRotation(float angle, View[] views) {
        if (views != null && views.length > 0) {
            for (View v : views) {
                if (v != null) {
                    v.setRotation(angle);
                }
            }
        }
    }

    private void initAnim() {
        if (anim == null) {
            anim = new ValueAnimator();
            anim.addUpdateListener(listener);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (lastAngle == 0) {
                        finishBtn.setVertical(true);
                    } else {
                        finishBtn.setVertical(false);
                        finishBtn.setRotateLeft(lastAngle == 90);
                    }
                }
            });
        }
    }

    private void startAnim(float start, float end) {
        initAnim();
        anim.setFloatValues(start, end);
        anim.setDuration(SCREEN_ROTATE_ANIM_TIME);
        anim.start();
    }

    @Override
    protected void toLeft() {
        startAnim(0, 90);
        lastAngle = 90;
        setConfig();
    }

    @Override
    protected void toRight() {
        startAnim(0, -90);
        lastAngle = -90;
        setConfig();
    }

    @Override
    protected void toNormal() {
        startAnim(lastAngle, 0);
        lastAngle = 0;
        setConfig();
    }

    @Override
    protected void fromLeftToRight() {
        startAnim(90, -90);
        lastAngle = -90;
        setConfig();
    }

    @Override
    protected void fromRightToLeft() {
        startAnim(-90, 90);
        lastAngle = 90;
        setConfig();
    }

    private void setConfig() {
        if (mrConfig != null) {
            if (lastAngle == 0) {
                mrConfig.setVideoRotation(0);
            } else {
                mrConfig.setVideoRotation((int) (lastAngle + 180));
            }
        }
    }

    @Override
    protected long getDelayTime() {
        return 1000;
    }
}
