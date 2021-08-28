package com.mm.recorduisdk.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorInt;
import androidx.core.view.animation.PathInterpolatorCompat;

/**
 * 实现 无限重复的圆形进度条
 * Project MomoDemo
 * Package com.mm.framework.view.progress
 * Created by tangyuchun on 4/22/16.
 */
public class IndeterminateDrawable extends Drawable {
    private static final Interpolator PATH_START_INTERPOLATOR;

    static {
        Path mPathStart = new Path();
        mPathStart.lineTo(0.5f, 0);
        mPathStart.cubicTo(0.7f, 0, 0.6f, 1, 1, 1);
        PATH_START_INTERPOLATOR = PathInterpolatorCompat.create(mPathStart);
    }

    private static final Interpolator PATH_END_INTERPOLATOR;

    static {
        Path mPathEnd = new Path();
        mPathEnd.cubicTo(0.2f, 0, 0.1f, 1, 0.5f, 1);
        mPathEnd.lineTo(1, 1);
        PATH_END_INTERPOLATOR = PathInterpolatorCompat.create(mPathEnd);
    }

    private Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mArcRect = new RectF();

    private int mRotationCount = 0;
    private float mRotation;
    private float mOffset;
    private float mStart;
    private float mEnd;

    //插值动画，实现进度条的变化
    private ValueAnimator mAnimator;

    private long mDuration = 2000;//动画时长

    public IndeterminateDrawable(@ColorInt int color, float stroke) {
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.SQUARE);
        progressPaint.setStrokeJoin(Paint.Join.MITER);
        progressPaint.setColor(color);
        progressPaint.setStrokeWidth(stroke);

        // The animator used to animate the spinning wheel (works back to API 7)
        mAnimator = new ValueAnimator();
        mAnimator.setFloatValues(0f, 0.25f);
        mAnimator.setDuration(mDuration);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mOffset = animator.getAnimatedFraction();
                float fraction = animator.getAnimatedFraction();
                mStart = PATH_START_INTERPOLATOR.getInterpolation(fraction) * 0.75f;
                mEnd = PATH_END_INTERPOLATOR.getInterpolation(fraction) * 0.75f;
                mRotation = (mRotationCount * 144) + mOffset * 567;
                mRotation %= 360;
                invalidateSelf();
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                mRotationCount = (mRotationCount + 1) % 5;
            }
        });
    }

    /**
     * 设置进度条旋转一次需要的时间，默认 2000ms
     *
     * @param pDuration
     */
    public void setDuration(long pDuration) {
        mDuration = pDuration;
    }

    public void setProgressColor(int color) {
        if (color != progressPaint.getColor()) {
            progressPaint.setColor(color);
            invalidateSelf();
        }
    }

    public void setProgressStrokeWidth(int stroke) {
        if (stroke != progressPaint.getStrokeWidth()) {
            progressPaint.setStrokeWidth(stroke);
            invalidateSelf();
        }
    }

    /**
     * 设置进度条的参数：颜色，进度条线的粗细，时长
     *
     * @param color
     * @param stroke
     * @param duration
     */
    public void setupProgress(int color, int stroke, int duration) {
        setProgressColor(color);
        setProgressStrokeWidth(stroke);
        setDuration(duration);
    }

    @Override
    public void draw(Canvas canvas) {
        int saveCount = canvas.save();

        //为了保证绘制完成后，能够连续，绘制完一次后，将canvas进行旋转
        canvas.rotate(mRotation, mArcRect.centerX(), mArcRect.centerY());

        //根据贝塞尔曲线计算 弧形的起始和终止角度
        float startAngle = -90 + 360 * (mOffset + mStart);
        float sweepAngle = 360 * (mEnd - mStart);
        canvas.drawArc(mArcRect, startAngle, sweepAngle, false, progressPaint);
        canvas.restoreToCount(saveCount);
    }


    @Override
    public void setBounds(Rect bounds) {
        resetArc(bounds.left, bounds.top, bounds.right, bounds.bottom);
        super.setBounds(bounds);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        resetArc(left, top, right, bottom);
        super.setBounds(left, top, right, bottom);
    }

    /**
     * 根据边框和线条的宽度计算 弧形的变化,保证进度条在中心位置
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void resetArc(int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;

        int size = Math.min(width, height);
        int l = (width - size) / 2;
        int t = (height - size) / 2;
        int r = l + size;
        int b = t + size;
        float stroke = progressPaint.getStrokeWidth();
        mArcRect.set(l + stroke, t + stroke, r - stroke, b - stroke);
    }

    @Override
    public void setAlpha(int i) {
        progressPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        progressPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void startProgress() {
        mAnimator.start();
    }

    public void stopProgress() {
        mAnimator.cancel();
    }

    public boolean isRunning() {
        return mAnimator.isRunning();
    }
}