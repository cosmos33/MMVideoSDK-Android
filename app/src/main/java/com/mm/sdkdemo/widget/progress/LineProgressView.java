package com.mm.sdkdemo.widget.progress;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.mm.sdkdemo.R;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 水平的线性进度条，具有以下特性：
 * 1.可以直接 {@link #setProgress(float)} 走到对应的进度
 * 2.可以指定在一定时间内自动走完整个进度；{@link #startFrom(int, Interpolator)} ，对应的方法还有 {@link #stop()} {@link #reset()}
 * <p/>
 * Project MomoDemo
 * Package com.mm.framework.view.progress
 * Created by tangyuchun on 4/19/16.
 */
public class LineProgressView extends View {
    public LineProgressView(Context context) {
        super(context);
        init(context, null);
    }

    public LineProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LineProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LineProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private float progress;
    private int progressColor = Color.GRAY;
    private long duration;

    private Paint progressPaint;


    private AtomicBoolean isRunning = new AtomicBoolean(false);

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineProgressView);
            if (typedArray != null) {
                progressColor = typedArray.getColor(R.styleable.LineProgressView_line_progress_color, progressColor);
                typedArray.recycle();
            }
        }
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.FILL);
    }


    public float getProgress() {
        return progress;
    }

    public void setProgress(float pProgress) {
        if (pProgress < 0) {
            pProgress = 0;
        }
        progress = pProgress > 100 ? 100 : pProgress;
        invalidate();

        callback();
    }

    public void setProgressColor(int pProgressColor) {
        progressColor = pProgressColor;
        progressPaint.setColor(progressColor);
        invalidate();
    }

    /**
     * 设置多长时间内更新完毕
     *
     * @param duration 单位ms
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * 从某个值开始自动增加进度
     *
     * @param startProgress 起始进度
     * @param pInterpolator 进度插值器
     *                      eg {@link android.view.animation.AccelerateDecelerateInterpolator}
     *                      {@link LinearInterpolator}  {@link android.view.animation.AccelerateInterpolator}  {@link android.view.animation.OvershootInterpolator} 等
     */
    public void startFrom(int startProgress, Interpolator pInterpolator) {
        if (isRunning.get()) {
            return;
        }
        if (startProgress < 0) {
            startProgress = 0;
        }
        progress = startProgress > 100 ? 100 : startProgress;
        isRunning.set(true);
        if (ob != null) {
            ob.cancel();
        }
        ob = ObjectAnimator.ofFloat(this, "progress", 100);
        ob.setDuration(this.duration);
        ob.setInterpolator(pInterpolator == null ? new LinearInterpolator() : pInterpolator);
        ob.start();

        ob.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                callback();
            }
        });

        ob.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isRunning.set(false);
                ob.cancel();
                ob = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isRunning.set(false);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    private ObjectAnimator ob;

    public void stop() {
        isRunning.set(false);
        if (ob != null) {
            ob.cancel();
        }
    }

    public void reset() {
        stop();
        setProgress(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int progressWidth = (int) (getWidth() * progress / 100);
        canvas.drawRect(0, 0, progressWidth, getHeight(), progressPaint);
    }

    private OnProgressChangedListener mOnProgressChangedListener;

    public void setOnProgressChangedListener(OnProgressChangedListener pOnProgressChangedListener) {
        mOnProgressChangedListener = pOnProgressChangedListener;
    }

    public interface OnProgressChangedListener {
        void onProgressChanged(int newProgress);
    }

    private void callback() {
        if (mOnProgressChangedListener != null) {
            mOnProgressChangedListener.onProgressChanged((int) progress);
        }
    }
}

