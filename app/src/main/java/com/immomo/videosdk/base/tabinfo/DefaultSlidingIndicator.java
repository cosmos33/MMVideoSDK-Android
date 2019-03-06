package com.immomo.videosdk.base.tabinfo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.google.android.material.tabs.MomoTabLayout;
import com.immomo.videosdk.utils.UIUtils;

public class DefaultSlidingIndicator implements MomoTabLayout.ISlidingIndicator {
    private Paint paint;
    private int width;
    private int height;
    private int radius;

    private int paddingBottom;

    public DefaultSlidingIndicator() {
        this(0);
    }

    public DefaultSlidingIndicator(int paddingBottom) {
        this(paddingBottom, 0xff4a4a4a);
    }

    public DefaultSlidingIndicator(int paddingBottom, int color) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);

        width = UIUtils.getPixels(6);
        height = UIUtils.getPixels(4);
        radius = UIUtils.getPixels(2);

        this.paddingBottom = paddingBottom;

    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void onDraw(Canvas canvas, int left, int top, int right, int bottom, float percent) {
        int newWidth = width + (int) ((0.5F - Math.abs(percent - 0.5F)) * width * 2);
        int drawableLeft = (left + right) / 2 - width / 2;
        int drawableRight = drawableLeft + newWidth;
        bottom = bottom - paddingBottom;
        canvas.drawRoundRect(new RectF(drawableLeft, bottom - height, drawableRight, bottom),
                radius, radius, paint);
    }
}
