package com.mm.recorduisdk.recorder.specialfilter;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class SpecialFilterAnimationUtils {

    public static void showAnimation(View view) {
        TranslateAnimation showAnimation = new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_SELF, 0);
        showAnimation.setDuration(300);
        view.startAnimation(showAnimation);
    }

    public static void hideAnimation(View view, final View needHideView) {
        TranslateAnimation hideAnimation = new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_SELF, 1);
        hideAnimation.setDuration(500);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                needHideView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(hideAnimation);

    }

    public static ObjectAnimator showScaleAnimation(View view, float from, float to) {
        return ObjectAnimator.ofPropertyValuesHolder(view,
                                                     PropertyValuesHolder.ofFloat(View.SCALE_X, from, to), PropertyValuesHolder.ofFloat(View.SCALE_Y, from, to));
    }

    public static ObjectAnimator showTranslateAnimation(View view, float from, float to) {
        return ObjectAnimator.ofPropertyValuesHolder(view,
                                                     PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, from, to));
    }

    public static ObjectAnimator showTranslateAndScaleAnimation(View view, float fromT, float toT, float fromS, float toS) {
        return ObjectAnimator.ofPropertyValuesHolder(view,
                                                     PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, fromT, toT),
                                                     PropertyValuesHolder.ofFloat(View.SCALE_Y, fromS, toS),
                                                     PropertyValuesHolder.ofFloat(View.SCALE_X, fromS, toS));
    }

}
