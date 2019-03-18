package com.mm.sdkdemo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.mm.sdkdemo.R;

/**
 * Created by XiongFangyu on 2017/6/5.
 *
 * 圆形进度drawable
 * 可设置开始角度{@link #setStartAngle(float)} (xml : prod_start_angle)
 * 可设置宽度 {@link #setWidth(float)} (xml : prod_progress_width)
 * 可设置shader {@link #setProgressShader(Shader)}
 */
public class ProgressDrawable extends Drawable {
    private static final int ONE_CIRCLE = 360;
    private Paint mPaint;
    private float progress;
    private float startAngle = 0;
    private RectF rect;

    public ProgressDrawable() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        rect = new RectF();
    }

    public ProgressDrawable(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this();
        if (context == null || attrs == null)
            return;

        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs,
                                                    R.styleable.ProgressDrawable, defStyleAttr, defStyleRes);
        TypedArray appearance = null;
        int ap = a.getResourceId(
                R.styleable.ProgressDrawable_prod_style, -1);
        if (ap != -1) {
            appearance = theme.obtainStyledAttributes(
                    ap, R.styleable.ProgressDrawable);
        }
        initStyle(appearance);
        initStyle(a);
    }

    private void initStyle(TypedArray a) {
        if (a != null) {
            setStartAngle(a.getFloat(R.styleable.ProgressDrawable_prod_start_angle, startAngle));
            setWidth(a.getDimensionPixelOffset(R.styleable.ProgressDrawable_prod_progress_width, (int) mPaint.getStrokeWidth()));
            a.recycle();
        }
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawArc(rect, startAngle, progress * ONE_CIRCLE, false, mPaint);
    }

    @Override
    public void setBounds(int l, int t, int r, int b) {
        super.setBounds(l, t, r, b);
        rect.set(getBounds());
        final float offset = mPaint.getStrokeWidth() / 2;
        rect.inset(offset, offset);
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    public void setProgressColor(int color) {
        mPaint.setColor(color);
        mPaint.setShader(null);
    }

    public void setProgressShader(Shader shader) {
        mPaint.setShader(shader);
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidateSelf();
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
        invalidateSelf();
    }

    public void setWidth(float w) {
        mPaint.setStrokeWidth(w);
        final float offset = w / 2;
        rect.inset(offset, offset);
    }
}
