package com.mm.sdkdemo.utils;

import android.animation.ValueAnimator;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.immomo.mmutil.task.MomoMainThreadExecutor;
import com.mm.mediasdk.utils.UIUtils;

import java.lang.reflect.Method;

/**
 * Created by XiongFangyu on 17/1/5.
 */

public class AnimUtils {
    private static final String TAG = "AnimUtils";

    private static volatile Interpolator superOverShoot;
    private static volatile Interpolator linear;
    private static volatile AnimationHandler handler;

    public static Handler getHandler() {
        if (handler == null) {
            synchronized (handler) {
                if (handler == null) {
                    handler = new AnimationHandler();
                }
            }
        }
        return handler;
    }

    public static void removeAnimationCallbacks() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        handler = null;
    }

    public static Interpolator getSuperOverShoot() {
        if (superOverShoot == null) {
            synchronized (AnimUtils.class) {
                if (superOverShoot == null) {
                    superOverShoot = new SuperOvershootInterpolator(1, 0.8, -8);
                }
            }
        }
        return superOverShoot;
    }

    public static Interpolator getLinear() {
        if (linear == null) {
            synchronized (AnimUtils.class) {
                if (linear == null) {
                    linear = new LinearInterpolator();
                }
            }
        }
        return linear;
    }

    /**
     * 判断是否在开发者选项中禁止了动画
     * @return true: 禁止
     */
    public static boolean isAnimInhibited() {
        return getAnimDurationScale() == 0;
    }

    public static float getAnimDurationScale() {
        try {
            Method m = ValueAnimator.class.getMethod("getDurationScale");
            if (m != null) {
                return (float) m.invoke(null);
            }
        } catch (Exception e) {
        }
        return -1;
    }

    public static class Default {
        /**
         * 震动加速动画，匀速震动一段时间，加速震动并保持一段时间，再加速；无线循环
         * @param v view
         * @param orientation 0 横向，1 纵向
         * @param maxRelateToSelf 位移大小是自身的多少倍
         * @param eachDuration 每次震动时间
         * @param intervalTimes 初始震动速率，值越小，越快，最小不小于0
         * @param maxDuration 动画最长时长
         */
        public static void startShakeAccelerateAnimation(final View v, int orientation, float maxRelateToSelf, long eachDuration, int intervalTimes, long maxDuration) {
            Animation a = Animations.newShakeAccelerateAnimation(orientation, maxRelateToSelf, eachDuration, intervalTimes);
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    v.clearAnimation();
                }
            }, maxDuration);
            v.startAnimation(a);
        }

        public static Animation fadeWeight(View view, boolean show, long duration) {
            if (show) {
                if (view.getVisibility() == View.VISIBLE) {
                    return null;
                }
            } else {
                if (view.getVisibility() != View.VISIBLE) {
                    return null;
                }
            }
            float from = show ? 0f : 1f;
            float to = 1 - from;
            Animation alpha = AnimUtils.Animations.newFadeAnimation(from, to, duration);
            view.clearAnimation();
            view.startAnimation(alpha);
            view.setVisibility(show ? View.VISIBLE : View.GONE);
            return alpha;
        }

        public static Animation fadeWeight(View view, boolean show) {
            return fadeWeight(view, show, show ? 150 : 100);
        }

        public static void hideToBottom(final View view, final boolean goneOrDisable, long duration) {
            Animation anim = AnimUtils.Animations.newToBottonAnimation(duration);
            anim.setAnimationListener(new OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (goneOrDisable)
                        view.setVisibility(View.GONE);
                    else {
                        view.setVisibility(View.INVISIBLE);
                        view.setEnabled(false);
                    }
                }
            });
            view.startAnimation(anim);
        }

        public static Animation showFromBottom(View v, long duration) {
            Animation anim = AnimUtils.Animations.newFromBottonAnimation(duration);
            v.setVisibility(View.VISIBLE);
            v.startAnimation(anim);
            return anim;
        }

        public static void hideToTop(final View view, final boolean goneOrDisable, long duration) {
            Animation anim = AnimUtils.Animations.newToTopAnimation(duration);
            anim.setAnimationListener(new OnAnimationEndListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (goneOrDisable)
                        view.setVisibility(View.GONE);
                    else {
                        view.setVisibility(View.INVISIBLE);
                        view.setEnabled(false);
                    }
                }
            });
            view.startAnimation(anim);
        }

        public static void showFromTop(View v, long duration) {
            Animation anim = AnimUtils.Animations.newFromTopAnimation(duration);
            v.setVisibility(View.VISIBLE);
            v.startAnimation(anim);
            v.setEnabled(true);
        }

        /**
         * 多个view从左边屏幕外转到屏幕里
         * @param listener
         * @param eachDelay 100
         * @param duration 1000
         * @param views
         */
        public static void rotateViewsFromLeftOutScreen(final Animation.AnimationListener listener, long eachDelay, long duration, final View... views) {
            if (views == null)
                return;
            int l = views.length;
            Interpolator interpolator = new AccelerateDecelerateInterpolator();
            for (int i = 0; i < l; i++) {
                final Animation a = Animations.newRotateFromLeftOutScreen(duration);
                a.setInterpolator(interpolator);
                if (i == 0) {
                    views[i].setVisibility(View.VISIBLE);
                    views[i].startAnimation(a);
                } else {
                    final int fi = i;
                    MomoMainThreadExecutor.postDelayed(TAG, new Runnable() {
                        @Override
                        public void run() {
                            views[fi].setVisibility(View.VISIBLE);
                            views[fi].startAnimation(a);
                        }
                    }, eachDelay * i);
                    if (i == l - 1) {
                        a.setAnimationListener(listener);
                    }
                }
            }
        }

    }

    public static class Animations {

        public static Animation setListener(Animation anim, Animation.AnimationListener listener) {
            anim.setAnimationListener(listener);
            return anim;
        }

        public static Animation.AnimationListener newGoneListener(final View view) {
            return new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            };
        }

        public static Animation.AnimationListener newDisabledListener(final View view) {
            return new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setEnabled(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            };
        }

        public static void startAnimTogether(Animation anim, View... views) {
            if (views != null && anim != null && views.length > 0) {
                for (View v : views) {
                    if (v != null)
                        v.startAnimation(anim);
                }
            }
        }

        public static Animation playTogether(Animation... anims) {
            AnimationSet set = new AnimationSet(false);
            if (anims != null) {
                for (Animation a : anims) {
                    if (a != null) {
                        set.addAnimation(a);
                    }
                }
            }
            return set;
        }

        public static Animation playSequentially(Animation... anims) {
            AnimationSet set = new AnimationSet(false);
            if (anims != null) {
                for (int i = 0, l = anims.length; i < l; i++) {
                    Animation a = anims[i];
                    if (a != null) {
                        set.addAnimation(a);
                        if (i > 0) {
                            Animation pre = anims[i - 1];
                            a.setStartOffset(pre.getDuration());
                        }
                    }
                }
            }
            return set;
        }

        public static Animation newFadeInThenFadeOutAnimation(long fadeInDuration, long fadeOutDuration) {
            Animation in = newFadeInAnimation(fadeInDuration);
            Animation out = newFadeOutAnimation(fadeOutDuration);
            return playSequentially(in, out);
        }

        public static Animation newFromLeftAnimation(long duration) {
            TranslateAnimation tran = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_SELF, 0, 0, 0, 0, 0);
            tran.setDuration(duration);
            return tran;
        }

        public static Animation newFromRightAnimation(long duration) {
            TranslateAnimation tran = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_SELF, 0, 0, 0, 0, 0);
            tran.setDuration(duration);
            return tran;
        }

        public static Animation newFromTopAnimation(long duration) {
            TranslateAnimation tran = new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_SELF, 0);
            tran.setDuration(duration);
            return tran;
        }

        public static Animation newFromBottonAnimation(long duration) {
            TranslateAnimation tran = new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_SELF, 0);
            tran.setDuration(duration);
            return tran;
        }

        public static Animation newToLeftAnimation(long duration) {
            TranslateAnimation tran = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, -1, 0, 0, 0, 0);
            tran.setDuration(duration);
            return tran;
        }

        public static Animation newToRightAnimation(long duration) {
            TranslateAnimation tran = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, 1, 0, 0, 0, 0);
            tran.setDuration(duration);
            return tran;
        }

        public static Animation newToTopAnimation(long duration) {
            TranslateAnimation tran = new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, -1);
            tran.setDuration(duration);
            return tran;
        }

        public static Animation newToBottonAnimation(long duration) {
            TranslateAnimation tran = new TranslateAnimation(0, 0, 0, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_PARENT, 1);
            tran.setDuration(duration);
            return tran;
        }

        public static Animation newFadeInAnimation(long duration) {
            return newFadeAnimation(0, 1, duration);
        }

        public static Animation newFadeOutAnimation(long duration) {
            return newFadeAnimation(1, 0, duration);
        }

        public static Animation newFadeAnimation(float from, float to, long duration) {
            AlphaAnimation alpha = new AlphaAnimation(from, to);
            alpha.setDuration(duration);
            return alpha;
        }

        public static Animation newRotateRelateToSelfCenterAnimation(float fromDegree, float toDegree, long duration) {
            RotateAnimation rotate = new RotateAnimation(fromDegree, toDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(duration);
            return rotate;
        }

        public static Animation newScaleRelateToSelfCenterAnimation(float fromScale, float toScale, long duration) {
            ScaleAnimation scale = new ScaleAnimation(fromScale, toScale, fromScale, toScale, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scale.setDuration(duration);
            return scale;
        }

        public static Animation newZoomInAnimation(long duration) {
            return newScaleRelateToSelfCenterAnimation(0, 1, duration);
        }

        public static Animation newZoomOutAnimation(long duration) {
            return newScaleRelateToSelfCenterAnimation(1, 0, duration);
        }

        public static Animation newRotateFromLeftOutScreen(long duration) {
            int w = UIUtils.getScreenWidth();
            int h = UIUtils.getScreenHeight();
            return newRotate(-60, 0, w >> 1, h, duration);
        }

        public static Animation newRotate(float fromDegree, float toDegree, float x, float y, long duration) {
            RotateAnimation ret = new RotateAnimation(fromDegree, toDegree, 0, x, 0, y);
            ret.setDuration(duration);
            return ret;
        }

        /**
         * 震动加速动画，匀速震动一段时间，加速震动并保持一段时间，再加速；无线循环
         * @param orientation 0 横向，1 纵向
         * @param maxRelateToSelf 位移大小是自身的多少倍
         * @param eachDuration 每次震动时间
         * @param intervalTimes 初始震动速率，值越小，越快，最小不小于0
         * @return 动画
         */
        public static Animation newShakeAccelerateAnimation(int orientation, float maxRelateToSelf, long eachDuration, int intervalTimes) {
            TranslateAnimation t = null;
            switch (orientation) {
                case 0:
                    t = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, maxRelateToSelf,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0);
                    break;
                case 1:
                    t = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, maxRelateToSelf);
                    break;
            }
            t.setDuration(eachDuration);
            t.setRepeatCount(Animation.INFINITE);
            t.setRepeatMode(Animation.REVERSE);
            final ShakeInterpolator interpolator = new ShakeInterpolator(intervalTimes);
            t.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    interpolator.minusIntervalTimes();
                }
            });
            t.setInterpolator(interpolator);
            return t;
        }
    }


    private static class AnimationHandler extends Handler {
        AnimationHandler() {
            super(Looper.getMainLooper());
        }
    }
}
