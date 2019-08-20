package com.mm.recorduisdk.widget.sticker.text;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by XiongFangyu on 17/2/9.
 */

public class ColorTextView extends TextView {

    private TextPaint textPaint;

    private Shader shader;
    private GradientShaderHelper gradientShaderHelper;

    private int width;
    private int height;

    private int color0;
    private int color1;
    private float degree = Float.NaN;
    private boolean vertical;
    private int[] colors;
    private float[] positions;

    public ColorTextView(Context context) {
        this(context, null);
    }

    public ColorTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        textPaint = getPaint();
        gradientShaderHelper = new GradientShaderHelper();
    }

    @Override
    protected void onMeasure(int w, int h) {
        super.onMeasure(w, h);

        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        gradientShaderHelper.setWidth(width);
        gradientShaderHelper.setHeight(height);
        if (this.width <= 0 || this.height <= 0) {
            this.width = width;
            this.height = height;
        } else {        //非第一次
            this.width = width;
            this.height = height;
            setShaderOnMeasure();
        }
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        shader = null;
        invalidate();
    }

    public void setShader(Shader shader) {
        this.shader = shader;
        invalidate();
    }

    private void setShaderOnMeasure() {
        if (shader == null)
            return;
        if (colors != null) {
            if (Float.isNaN(degree)) {
                setLinearGradient(colors, positions, vertical);
            } else {
                setLinearGradient(colors, positions, degree);
            }
        } else {
            if (Float.isNaN(degree)) {
                setLinearGradient(color0, color1, vertical);
            } else {
                setLinearGradient(color0, color1, degree);
            }
        }
    }

    /**
     * 设置直线型渐变
     * @param color0    开始颜色
     * @param color1    结束颜色
     * @param vertical  true: 竖直渐变; false: 横向渐变
     */
    public void setLinearGradient(final int color0, final int color1, final boolean vertical) {
        this.color0 = color0;
        this.color1 = color1;
        this.vertical = vertical;
        this.colors = null;
        this.positions = null;
        this.degree = Float.NaN;
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                setShader(gradientShaderHelper.getLinearGradient(color0, color1, vertical));
            }
        };
        if (width <= 0 || height <= 0)
            post(r);
        else
            r.run();
    }

    public void setLinearGradient(final int colors[], final float positions[], final boolean vertical) {
        this.vertical = vertical;
        this.colors = colors;
        this.positions = positions;
        this.degree = Float.NaN;
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                setShader(gradientShaderHelper.getLinearGradient(colors, positions, vertical));
            }
        };
        if (width <= 0 || height <= 0)
            post(r);
        else
            r.run();
    }

    public void setLinearGradient(final int color0, final int color1, @FloatRange(from = -90, to = 90) final float degree) {
        this.color0 = color0;
        this.color1 = color1;
        this.colors = null;
        this.positions = null;
        this.degree = degree;
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                setShader(gradientShaderHelper.getLinearGradient(color0, color1, degree));
            }
        };
        if (width <= 0 || height <= 0)
            post(r);
        else
            r.run();
    }

    public void setLinearGradient(final int colors[], final float positions[], @FloatRange(from = -90, to = 90) final float degree) {
        this.colors = colors;
        this.positions = positions;
        this.degree = degree;
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                setShader(gradientShaderHelper.getLinearGradient(colors, positions, degree));
            }
        };
        if (width <= 0 || height <= 0)
            post(r);
        else
            r.run();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        textPaint.setShader(shader);
        super.onDraw(canvas);
    }
}
