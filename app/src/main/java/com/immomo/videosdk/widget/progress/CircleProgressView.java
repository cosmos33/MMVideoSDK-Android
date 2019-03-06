package com.immomo.videosdk.widget.progress;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Property;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.immomo.mmutil.log.Log4Android;
import com.immomo.videosdk.R;
import com.immomo.videosdk.utils.CanvasUtils;

/**
 * Project MomoDemo
 * Package com.immomo.framework.view.progress
 * Created by tangyuchun on 4/19/16.
 */
public class CircleProgressView extends View {
    public CircleProgressView(Context context) {
        super(context);
        init(context, null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * 圆圈背景色
     */
    private int backProgressColor = Color.GRAY;
    /**
     * 进度条颜色
     */
    private int progressColor = Color.RED;
    /**
     * 进度条的宽度
     */
    private float strokeWidth = dp2px(5f);

    private Paint backgroundPaint, progressPaint;

    private RectF rectF;
    private Path clipPath;

    private float progress = 0f;

    //绘制的方向，默认顺时针
    private float startAngle = -90;

    private boolean reverse = false;

    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
            if (typedArray != null) {
                try {
                    backProgressColor = typedArray.getColor(R.styleable.CircleProgressView_circle_progress_background_color, backProgressColor);
                    progressColor = typedArray.getColor(R.styleable.CircleProgressView_circle_progress_color, progressColor);
                    strokeWidth = typedArray.getDimension(R.styleable.CircleProgressView_circle_progress_width, strokeWidth);
                } catch (Exception ex) {
                    Log4Android.getInstance().e(ex);
                } finally {
                    typedArray.recycle();
                }
            }
        }
        //进度条的绘制
        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidth);
        clipPath = new Path();
    }

    private int dp2px(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }

    /**
     * 重写onMeasure，保证是圆形，同时根据 进度条宽度设置 rectF
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int size = Math.min(width, height);
        setMeasuredDimension(size, size);
        //设置绘制的圆圈的尺寸
        rectF.set(strokeWidth / 2, strokeWidth / 2, size - strokeWidth / 2, size - strokeWidth / 2);
        clipPath.addCircle(rectF.centerX(), rectF.centerY(), size >> 1, Path.Direction.CCW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if (backgroundPaint != null) {
            if (CanvasUtils.isSupportClippath(canvas))
                canvas.clipPath(clipPath, Region.Op.INTERSECT);
            canvas.drawPaint(backgroundPaint);
        }
        progressPaint.setColor(backProgressColor);
        canvas.drawOval(rectF, progressPaint);

        if (progress > 0) {
            float angle = 360 * progress / 100;//绘制的角度
            if (reverse) {
                angle = -angle;
            }
            progressPaint.setColor(progressColor);
            canvas.drawArc(rectF, startAngle, angle, false, progressPaint);
        }
        canvas.restore();
    }

    public void setProgressNoAnim(float pProgress) {
        stopAnim();
        setProgress(pProgress);
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    private void setProgress(float pProgress) {
        if (pProgress < 0) {
            pProgress = 0;
        }
        progress = pProgress <= 100 ? pProgress : 100;
        invalidate();
    }

    /**
     * 动画的帧率
     */
    private int animFps = 20;

    public void setAnimFps(int animFps) {
        if (this.animFps <= 0) {
            animFps = 20;
        }
        this.animFps = animFps;
    }

    public int getAnimFps() {
        return animFps;
    }

    public float getProgress() {
        return progress;
    }

    public void setStrokeWidth(float pStrokeWidth) {
        strokeWidth = pStrokeWidth;
        progressPaint.setStrokeWidth(strokeWidth);
        //需要重新计算圆形区域的尺寸
        requestLayout();
        invalidate();
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public int getBackProgressColor() {
        return backProgressColor;
    }

    public void setProgressColor(int pProgressColor) {
        progressColor = pProgressColor;
        progressPaint.setColor(progressColor);
        invalidate();
    }

    @Override
    public void setBackgroundColor(int pBackgroundColor) {
        backProgressColor = pBackgroundColor;
        invalidate();
    }

    public void setInnerBackgroundColor(int color) {
        if (backgroundPaint == null) {
            backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            backgroundPaint.setStyle(Paint.Style.FILL);
        }
        backgroundPaint.setColor(color);
    }

    /**
     * 设置进度，带有动画效果，动画时间默认为 1500ms
     *
     * @param newProgress
     */
    public void setProgressWithAnim(float newProgress) {
        setProgressWithAnim(newProgress, 1500);
    }

    /**
     * 设置进度，带有动画效果
     *
     * @param newProgress
     * @param duration    动画的时长
     */
    public void setProgressWithAnim(float newProgress, long duration) {
        setProgressWithAnim(newProgress, duration, new LinearInterpolator());
    }

    /**
     * 设置进度，带有动画效果
     *
     * @param newProgress
     * @param duration      动画时长
     * @param pInterpolator 进度值改变的插值器，任何 {@link Interpolator} 的子类都可以
     */
    public void setProgressWithAnim(float newProgress, long duration, Interpolator pInterpolator) {
        setProgressWithAnim(0, newProgress, duration, pInterpolator);
    }

    public void setProgressWithAnim(float from, float to, long duration, Interpolator interpolator) {
        stopAnim();
        if (oa == null) {
            oa = ObjectAnimator.ofFloat(this, new Property<CircleProgressView, Float>(Float.class, "progress") {
                @Override
                public Float get(CircleProgressView object) {
                    return object != null ? object.getProgress() : 0;
                }

                @Override
                public void set(CircleProgressView object, Float value) {
                    if (object != null && value != null)
                        object.setProgress(value);
                }
            }, from, to);
            if (listener != null)
                oa.addListener(listener);
        }
        oa.setFloatValues(from, to);
        oa.setDuration(duration);
        oa.setInterpolator(interpolator == null ? new DecelerateInterpolator() : interpolator);
        oa.start();
    }

    private Animator.AnimatorListener listener;

    public void setAnimListener(Animator.AnimatorListener listener) {
        if (oa != null && this.listener != listener) {
            oa.removeListener(this.listener);
            if (listener != null)
                oa.addListener(listener);
        }
        this.listener = listener;
    }

    private ObjectAnimator oa;

    private void stopAnim() {
        if (oa != null) {
            oa.cancel();
        }
    }

    private boolean isAnimRunning() {
        return oa != null && oa.isRunning();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnim();
        super.onDetachedFromWindow();
    }
}
