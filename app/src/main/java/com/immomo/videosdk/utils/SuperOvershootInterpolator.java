package com.immomo.videosdk.utils;

import android.view.animation.Interpolator;

/**
 * Created by XiongFangyu on 16/7/22.
 * 超级弹跳模式~
 * {@link android.view.animation.OvershootInterpolator}只弹跳一次
 * 这个会跳很多次
 *
 * 如果取1,0.8,-8 会在0.325处达到最大值1.137141
 * 如果取1,0.5,-6 会在0.225处到达最大值1.373092
 */
public class SuperOvershootInterpolator implements Interpolator {
    
    private double amplitude;
    
    private double period;

    private float what = -6;

    public SuperOvershootInterpolator(){
        this(1,0.5,-6);
    }

    public SuperOvershootInterpolator(double amplitude, double period){
        this(amplitude,period,-6);
    }

    public SuperOvershootInterpolator(double amplitude, double period, float what){
        this.amplitude = amplitude;
        this.period = period;
        this.what = what;
    }

    public static SuperOvershootInterpolator getNewInterpolator(){
        return new SuperOvershootInterpolator(1,0.8,-8);
    }

    @Override
    public float getInterpolation(float input) {
        if (input == 0 || input == 1) return input;

        double pi2 = Math.PI * 2;
        double s = period / pi2 * Math.asin(1 / amplitude);
        return (float) (amplitude * Math.pow(2, what * input) * Math.sin((input - s) * pi2 / period) + 1);
    }

    public void setAmplitude(double amplitude) {
        this.amplitude = amplitude;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

    public void setWhat(float what) {
        this.what = what;
    }
}
