package com.immomo.videosdk.widget.sticker.text;

import android.graphics.LinearGradient;
import android.graphics.Shader;

import androidx.annotation.FloatRange;

/**
 * Created by XiongFangyu on 17/2/10.
 *
 * 渐变shader帮助类
 */
public class GradientShaderHelper {
    private int width;
    private int height;
    private Shader.TileMode mode;

    public GradientShaderHelper() {
        mode = Shader.TileMode.CLAMP;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setMode(Shader.TileMode mode) {
        this.mode = mode;
    }

    /**
     * Create a shader that draws a linear gradient along a line.
     * @param x0       The x-coordinate for the start of the gradient line
     * @param y0       The y-coordinate for the start of the gradient line
     * @param x1       The x-coordinate for the end of the gradient line
     * @param y1       The y-coordinate for the end of the gradient line
     * @param  color0  The color at the start of the gradient line.
     * @param  color1  The color at the end of the gradient line.
     */
    public Shader getLinearGradient(float x0, float y0, float x1, float y1, int color0, int color1) {
        return new LinearGradient(x0, y0, x1, y1, color0, color1, mode);
    }

    /**
     * Create a shader that draws a linear gradient along a line.
     * @param x0           The x-coordinate for the start of the gradient line
     * @param y0           The y-coordinate for the start of the gradient line
     * @param x1           The x-coordinate for the end of the gradient line
     * @param y1           The y-coordinate for the end of the gradient line
     * @param  colors      The colors to be distributed along the gradient line
     * @param  positions   May be null. The relative positions [0..1] of
     * each corresponding color in the colors array. If this is null,
     * the the colors are distributed evenly along the gradient line.
     */
    public Shader getLinearGradient(float x0, float y0, float x1, float y1, int colors[], float positions[]) {
        return new LinearGradient(x0, y0, x1, y1, colors, positions, mode);
    }

    public Shader getLinearGradient(final int color0, final int color1, final boolean vertical) {
        if (vertical) {
            return getLinearGradient(width>>1, 0, width>>1, height, color0, color1);
        } else {
            return getLinearGradient(0, height>>1, width, height>>1, color0, color1);
        }
    }

    public Shader getLinearGradient(final int colors[], final float positions[], final boolean vertical) {
        if (vertical) {
            return getLinearGradient(width>>1, 0, width>>1, height, colors, positions);
        } else {
            return getLinearGradient(0, height>>1, width, height>>1, colors, positions);
        }
    }

    public Shader getLinearGradient(final int color0, final int color1, @FloatRange(from = -90, to = 90) final float degree) {
        float finalY = (float) (width * Math.tan(Math.toRadians(degree)));
        return getLinearGradient(0, 0, width, finalY, color0, color1);
    }

    public Shader getLinearGradient(final int colors[], final float positions[], @FloatRange(from = -90, to = 90) final float degree) {
        float finalY = (float) (width * Math.tan(Math.toRadians(degree)));
        return getLinearGradient(0, 0, width, finalY, colors, positions);
    }
}
