package com.mm.recorduisdk.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.mm.recorduisdk.R;

/**
 * Created by XiongFangyu on 2017/6/6.
 *
 * 环形带有shader
 * 且shader可移动
 */
public class ShaderCircleDrawable extends Drawable {

    private static final int[] SHADER_COLORS = new int[]{
            0xffffbf00,
            0xffe2305c,
            0xff00aeff,
            0xff9e1dff,
            0xffffbf00,
            0xffe2305c
    };

    private Paint ringPaint;
    private Paint innerPaint;
    private Path clipPath;

    private Rect shaderRect;

    private int centerX, centerY;

    private int circleSize;
    private int shaderEndWidth;

    private float shaderTranslate;

    private int ringWidth = 20;
    private int drawRingWidth = 20;
    private float ringWdithScale = 1;
    private float scale = 1;

    private Shader shader;

    public ShaderCircleDrawable() {
        ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerPaint.setColor(0);
        innerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        shaderRect = new Rect();
        clipPath = new Path();
    }

    public ShaderCircleDrawable(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this();
        if (context == null || attrs == null)
            return;
        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs,
                                                    R.styleable.ShaderCircleDrawable, defStyleAttr, defStyleRes);
        TypedArray appearance = null;
        int ap = a.getResourceId(
                R.styleable.ShaderCircleDrawable_scd_style, -1);
        if (ap != -1) {
            appearance = theme.obtainStyledAttributes(
                    ap, R.styleable.ShaderCircleDrawable);
        }
        initStyle(appearance);
        initStyle(a);
        drawRingWidth = ringWidth;
    }

    public void setInnerColor(int color) {
        innerPaint.setColor(color);
    }

    private void initStyle(TypedArray a) {
        if (a != null) {
            ringWidth = a.getDimensionPixelOffset(R.styleable.ShaderCircleDrawable_scd_ring_width, ringWidth);
            a.recycle();
        }
    }

    @Override
    public void setBounds(int l, int t, int r, int b) {
        super.setBounds(l, t, r, b);
        final Rect bounds = getBounds();
        centerX = bounds.centerX();
        centerY = bounds.centerY();
        circleSize = Math.min(bounds.width(), bounds.height());
        final int len = SHADER_COLORS.length - 1;
        shaderRect.set(l, t, l + len * circleSize, t + circleSize);
        clipPath.addCircle(centerX, centerY, circleSize >> 1, Path.Direction.CW);
        shader = new LinearGradient(0, 0, len * circleSize, 0, SHADER_COLORS, null, Shader.TileMode.CLAMP);
        shaderEndWidth = circleSize * (SHADER_COLORS.length - 2);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.clipPath(clipPath);
        canvas.save();
        canvas.translate(shaderTranslate, 0);
        canvas.drawRect(shaderRect, ringPaint);
        canvas.restore();
        float r = (circleSize >> 1) - drawRingWidth * ringWdithScale;
        r *= scale;
        canvas.drawCircle(centerX, centerY, r, innerPaint);
        canvas.restore();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        ringPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        ringPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    public void setRingColor(int color) {
        ringPaint.setColor(color);
        ringPaint.setShader(null);
        invalidateSelf();
    }

    public void setRingToShader() {
        ringPaint.setShader(shader);
        invalidateSelf();
    }

    public void setScale(@FloatRange(from = 0, to = 1f) float scale) {
        if (scale < 0.6f) {
            ringPaint.setAlpha(255);
            ringPaint.setShader(null);
        } else {
            ringPaint.setAlpha((int) (255 * scale));
            ringPaint.setShader(shader);
        }
        this.scale = 0.96f + scale * 0.04f;
        drawRingWidth = ringWidth;
        invalidateSelf();
    }

    public void setRingWidthScale(float s) {
        ringWdithScale = s;
        drawRingWidth = ringWidth;
        invalidateSelf();
    }

    public void setRingScaleWithoutScaleWidth(float s) {
        drawRingWidth = (int) (ringWidth / s);
    }

    public void setShaderTranslate(float x) {
        final int sw = shaderEndWidth;
        if (x > 0) {
            x = x % sw - sw;
        } else if (x < -sw) {
            x = x % sw;
        }
        shaderTranslate = x;
        invalidateSelf();
    }

    public float getShaderTranslate() {
        return shaderTranslate;
    }

    public int getShaderEndWidth() {
        return shaderEndWidth;
    }
}
