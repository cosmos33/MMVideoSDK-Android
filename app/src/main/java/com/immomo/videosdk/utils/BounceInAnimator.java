package com.immomo.videosdk.utils;

import android.animation.ObjectAnimator;
import android.view.View;

/**
 * BounceInAnimator
 * author: tian.yuting@immomo.com
 * since: 2015-04-07
 * MomoTech
 * <p/>
 * _-----_
 * |       |    .------------------------------.
 * |--(o)--|    |  If this comment is removed  |
 * `---------´   |      this program will       |
 * ( _´U`_ )    |          blow up             |
 * /___A___\    '------------------------------'
 * |  ~  |
 * __'.___.'__
 * ´   `  |° ´ Y `
 */

public class BounceInAnimator extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 0, 1, 1, 1),
                ObjectAnimator.ofFloat(target,"scaleX",0.3f,1.05f,0.9f,1),
                ObjectAnimator.ofFloat(target,"scaleY",0.3f,1.05f,0.9f,1)
        );
    }
}
