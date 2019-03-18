package com.mm.sdkdemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.mm.sdkdemo.R;

/**
 * 绘制分割线的View类，可以在控件的顶部和底部绘制分割线
 * 可以通过 {@link #lineColor} 来指定分割线的颜色
 * 使用此控件时，默认会绘制底部的分割线
 * 切记：此类控件只支持 1px的分割线，如果需要的分割线太宽，请自行实现
 * <p/>
 * Project momodev
 * Package com.mm.momo.view
 * Created by tangyuchun on 3/31/16.
 */
public class DrawLineViewHelper {
    /**
     * 分割线颜色
     */
    private int lineColor = -1;
    /**
     * 是否绘制顶部的分割线
     */
    private boolean drawTopLine = false;
    /**
     * 是否绘制底部的分割线
     */
    private boolean drawBottomLine = true;

    /**
     * 是否绘制左侧，右侧的分割线
     */
    private boolean drawLeftLine = false;
    private boolean drawRightLine = false;
    private Paint linePaint = null;
    private int lineWidth;

    /**
     * 手动指定分割线的边距，这四个属性如果不指定的话，会画满
     */
    private int lineLeftMargin = -1, lineTopMargin = -1, lineRightMargin = -1, lineBottomMargin = -1;

    public DrawLineViewHelper() {
    }

    public void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DrawLineWidget);
            lineColor = typedArray.getColor(R.styleable.DrawLineWidget_dlw_lineColor, -1);
            lineWidth = typedArray.getInt(R.styleable.DrawLineWidget_dlw_lineWidth, -1);

            drawTopLine = typedArray.getBoolean(R.styleable.DrawLineWidget_drawTopLine, drawTopLine);
            //使用此控件时，默认会绘制底部的分割线
            drawBottomLine = typedArray.getBoolean(R.styleable.DrawLineWidget_drawBottomLine, drawBottomLine);
            drawLeftLine = typedArray.getBoolean(R.styleable.DrawLineWidget_drawLeftLine, drawLeftLine);
            drawRightLine = typedArray.getBoolean(R.styleable.DrawLineWidget_drawRightLine, drawRightLine);

            lineLeftMargin = typedArray.getDimensionPixelSize(R.styleable.DrawLineWidget_lineLeftMargin, lineLeftMargin);
            lineTopMargin = typedArray.getDimensionPixelSize(R.styleable.DrawLineWidget_lineTopMargin, lineTopMargin);
            lineRightMargin = typedArray.getDimensionPixelSize(R.styleable.DrawLineWidget_lineRightMargin, lineRightMargin);
            lineBottomMargin = typedArray.getDimensionPixelSize(R.styleable.DrawLineWidget_lineBottomMargin, lineBottomMargin);

            if (lineColor == -1) {
                lineColor = context.getResources().getColor(R.color.C03);
            }
            typedArray.recycle();
        }

        linePaint = new Paint();
        if (lineWidth > 0) {
            linePaint.setStrokeWidth(lineWidth);
        }
        linePaint.setAntiAlias(true);
        linePaint.setColor(lineColor);
        linePaint.setStyle(Paint.Style.FILL);
    }

    public void onDrawLine(View rootView, Canvas canvas) {
        if ((drawTopLine || drawBottomLine || drawLeftLine || drawRightLine)) {
            canvas.save();

            int width = rootView.getWidth();
            int height = rootView.getHeight();

            int left = lineLeftMargin >= 0 ? lineLeftMargin : 0;
            int right = lineRightMargin >= 0 ? (width - lineRightMargin) : width;
            int top = lineTopMargin >= 0 ? lineTopMargin : 0;
            int bottom = lineBottomMargin >= 0 ? (height - lineBottomMargin) : height;

            if (drawTopLine) {
                canvas.drawLine(left, 1, right, 1, linePaint);
            }
            if (drawBottomLine) {
                canvas.drawLine(left, height - 1, right, height - 1, linePaint);
            }
            if (drawLeftLine) {
                canvas.drawLine(1, top, 1, bottom, linePaint);
            }
            if (drawRightLine) {
                canvas.drawLine(width - 1, top, width - 1, bottom, linePaint);
            }
            canvas.restore();
        }
    }

    public void setDrawLine(boolean left, boolean top, boolean right, boolean bottom) {
        drawLeftLine = left;
        drawTopLine = top;
        drawRightLine = right;
        drawBottomLine = bottom;
    }
}
