package com.immomo.videosdk.utils;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

/**
 * Created by zhoukai on 13-8-22.
 */
public class AnimationUtil {
    public static void hiddenAlphaAnimation(View view) {
        AlphaAnimation animation2 = new AlphaAnimation(1, 0);
        animation2.setDuration(300);
        animation2.setStartTime(300);
        animation2.setFillAfter(true);
        view.startAnimation(animation2);
    }

    public static void showAlphaAnimation(View view) {
        AlphaAnimation animation2 = new AlphaAnimation(0, 1);
        animation2.setDuration(300);
        animation2.setStartTime(300);
        animation2.setFillAfter(true);
        view.startAnimation(animation2);
    }


    public static Animation playFilterTipAnim(final View target){
        target.clearAnimation();

        AnimationSet anim = new AnimationSet(true);
        ScaleAnimation scale= new ScaleAnimation(1.6f, 1.75f, 1.6f, 1.75f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        AlphaAnimation alpha1 = new AlphaAnimation(0f, 1);
        AlphaAnimation alpha2 = new AlphaAnimation(1f, 0);
        scale.setDuration(350);
        alpha1.setDuration(350);
        alpha2.setDuration(600);
        alpha2.setStartOffset(2400);

        anim.addAnimation(scale);
        anim.addAnimation(alpha1);
        anim.addAnimation(alpha2);
        anim.setAnimationListener(new OnAnimationEndListener(){

            @Override
            public void onAnimationStart(Animation animation) {
                target.setScaleY(1.6f);
                target.setScaleX(1.6f);
                target.setVisibility(View.VISIBLE);
                target.setAlpha(1);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                target.setAlpha(0);
                target.setVisibility(View.INVISIBLE);
            }

        });
        target.setAnimation(anim);
        return anim;
    }
}
