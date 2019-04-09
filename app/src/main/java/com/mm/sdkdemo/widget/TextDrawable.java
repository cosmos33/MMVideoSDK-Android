package com.mm.sdkdemo.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;

/**
 * Created by XiongFangyu on 2017/5/3.
 */

public class TextDrawable extends Drawable {

    private String text;
    private TextPaint textPaint;

    private float translateX, translateY;
    private float scale = 1;

    public TextDrawable() {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (TextUtils.isEmpty(text))
            return;
        Rect bounds = getBounds();
        final float left = getTextLeft();
        final float y = getTextY();
        canvas.save();
        canvas.translate(translateX, translateY);
        canvas.scale(scale, scale, bounds.centerX(), bounds.centerY());
        canvas.drawText(text, left, y, textPaint);
        canvas.restore();
    }

    public float getTextLeft() {
        Rect bounds = getBounds();
        final float len = getMeasureTextWidth();
        return bounds.left + (bounds.width() - len) / 2;
    }

    public float getTextY() {
        Rect bounds = getBounds();
        final Paint.FontMetrics metrics = textPaint.getFontMetrics();
        final float height = metrics.bottom - metrics.top;
        return height / 2 - metrics.bottom + bounds.centerY();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        if (textPaint != null)
            textPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        if (textPaint != null)
            textPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    @Override
    public void setBounds(int l, int t, int r, int b) {
        super.setBounds(l, t, r, b);
    }

    public void setText(String text) {
        if (isTextSame(this.text, text))
            return;
        this.text = text;
        invalidateSelf();
    }

    public String getText() {
        return text;
    }

    public void setTextSize(float px) {
        if (textPaint != null)
            textPaint.setTextSize(px);
        invalidateSelf();
    }

    public void setTextColor(int color) {
        if (textPaint != null)
            textPaint.setColor(color);
        invalidateSelf();
    }

    public void setShader(Shader shader) {
        if (textPaint != null)
            textPaint.setShader(shader);
        invalidateSelf();
    }

    public void setXfermode(Xfermode xfermode) {
        if (textPaint != null)
            textPaint.setXfermode(xfermode);
        invalidateSelf();
    }

    public void setStyle(Paint.Style style) {
        if (textPaint != null)
            textPaint.setStyle(style);
        invalidateSelf();
    }

    public Paint getPaint() {
        return textPaint;
    }

    public float getTranslateX() {
        return translateX;
    }

    public void setTranslateX(float translateX) {
        this.translateX = translateX;
        invalidateSelf();
    }

    public float getTranslateY() {
        return translateY;
    }

    public void setTranslateY(float translateY) {
        this.translateY = translateY;
        invalidateSelf();
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        invalidateSelf();
    }

    public
    @IntRange(from = 0, to = 255)
    int getAlpha() {
        if (textPaint != null)
            return textPaint.getAlpha();
        return 255;
    }

    public int getWidth() {
        return getBounds().width();
    }

    public int getHeight() {
        return getBounds().height();
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) getMeasureTextHeight();
    }

    public int getIntrinsicWidth() {
        return (int) getMeasureTextWidth();
    }

    public float getMeasureTextWidth() {
        return textPaint.measureText(text);
    }

    public float getMeasureTextHeight() {
        final Paint.FontMetrics metrics = textPaint.getFontMetrics();
        final float height = metrics.bottom - metrics.top;
        return height;
    }

    private boolean isTextSame(String t1, String t2) {
        if (t1 == null && t2 == null)
            return true;
        return t1 != null && t1.equals(t2);
    }
}
