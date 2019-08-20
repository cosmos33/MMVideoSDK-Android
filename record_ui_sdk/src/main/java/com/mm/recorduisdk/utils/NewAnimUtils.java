package com.mm.recorduisdk.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by XiongFangyu on 2017/5/25.
 */

public class NewAnimUtils {

    public static class Animators {

        public static Animator newScaleAnimator(View target, float fromScale, float toScale, long duration) {
            Animator x = ObjectAnimator.ofFloat(target, View.SCALE_X, fromScale, toScale).setDuration(duration);
            Animator y = ObjectAnimator.ofFloat(target, View.SCALE_Y, fromScale, toScale).setDuration(duration);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(x, y);
            return set;
        }
    }
}
