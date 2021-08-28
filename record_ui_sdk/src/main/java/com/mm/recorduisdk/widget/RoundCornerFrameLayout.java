package com.mm.recorduisdk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mm.recorduisdk.R;
import com.mm.recorduisdk.utils.ViewClipHelper;

/**
 * Created by xudong on 2018/7/18
 * <p>
 * Crop a round corner layout
 * WARN: Only support SurfaceView with match_parent, match_parent
 */
public class RoundCornerFrameLayout extends FrameLayout {
    private int topLeftRadius = 0;
    private int topRightRadius = 0;
    private int bottomLeftRadius = 0;
    private int bottomRightRadius = 0;
    private int borderWidth = 0;
    private int borderColor = Color.BLACK;
    @Nullable
    private Shader borderShader;

    @NonNull
    private final Path clipPath = new Path();
    @Nullable
    private ViewClipHelper viewClipHelper;
    @Nullable
    private ViewClipHelper.SuperDrawAction drawAction;

    @NonNull
    private final Path borderPath = new Path();
    @Nullable
    private Paint borderPathPaint;

    public RoundCornerFrameLayout(Context context) {
        this(context, null, 0);
    }

    public RoundCornerFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundCornerFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundCornerLayout);

        int radius = a.getDimensionPixelOffset(R.styleable.RoundCornerLayout_radius, 0);
        topLeftRadius = a.getDimensionPixelOffset(
                R.styleable.RoundCornerLayout_topLeftRadius, radius);
        topRightRadius = a.getDimensionPixelOffset(
                R.styleable.RoundCornerLayout_topRightRadius, radius);
        bottomLeftRadius = a.getDimensionPixelOffset(
                R.styleable.RoundCornerLayout_bottomLeftRadius, radius);
        bottomRightRadius = a.getDimensionPixelOffset(
                R.styleable.RoundCornerLayout_bottomRightRadius, radius);

        borderWidth = a.getDimensionPixelOffset(
                R.styleable.RoundCornerLayout_borderWidth, 0);
        borderColor = a.getColor(
                R.styleable.RoundCornerLayout_borderColor, 0);
        a.recycle();

        initialClip();
        initialPaint();
    }

    private boolean initialPaint() {
        if (borderWidth > 0) {
            borderPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            borderPathPaint.setStrokeWidth(borderWidth);
            borderPathPaint.setStyle(Paint.Style.STROKE);
            borderPathPaint.setStrokeJoin(Paint.Join.ROUND);
            borderPathPaint.setStrokeCap(Paint.Cap.ROUND);
            if (borderShader != null) {
                borderPathPaint.setShader(borderShader);
            } else {
                borderPathPaint.setColor(borderColor);
            }
            return true;
        } else {
            borderPathPaint = null;
            return false;
        }
    }

    private boolean initialClip() {
        if (topLeftRadius != 0 || topRightRadius != 0
                || bottomLeftRadius != 0 || bottomRightRadius != 0 || borderWidth > 0) {
            setWillNotDraw(false);

            if (viewClipHelper == null) {
                viewClipHelper = new ViewClipHelper();
            }
            if (drawAction == null) {
                drawAction = new ViewClipHelper.SuperDrawAction() {
                    @Override
                    public void innerDraw(Canvas canvas) {
                        RoundCornerFrameLayout.super.draw(canvas);
                    }
                };
            }
            return true;
        } else {
            viewClipHelper = null;
            drawAction = null;
        }
        return false;
    }

    //<editor-fold desc="Public Method">
    public void setRadius(int radius) {
        setRadius(radius, radius, radius, radius);
    }

    public void setRadius(int topLeft, int topRight, int bottomLeft, int bottomRight) {
        topLeftRadius = topLeft;
        topRightRadius = topRight;
        bottomLeftRadius = bottomLeft;
        bottomRightRadius = bottomRight;

        if (initialClip()) {
            updatePath(getMeasuredWidth(), getMeasuredHeight());
        }
        invalidate();
    }

    public void setBorderWidth(@IntRange(from = 0) int borderWidth) {
        this.borderWidth = borderWidth;

        if (initialPaint()) {
            updatePath(getMeasuredWidth(), getMeasuredHeight());
        }
        invalidate();
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        this.borderShader = null;

        if (initialPaint()) {
            updatePath(getMeasuredWidth(), getMeasuredHeight());
        }
        invalidate();
    }

    public void setBorderShader(@Nullable Shader borderShader) {
        this.borderShader = borderShader;

        if (initialPaint()) {
            updatePath(getMeasuredWidth(), getMeasuredHeight());
        }
        invalidate();
    }
    //</editor-fold>

    private void updatePath(int w, int h) {
        int extraRadius = (int) Math.ceil(borderWidth * 1F / 2);
        float clipTLRadius = topLeftRadius > 0 ? topLeftRadius + extraRadius : 0;
        float clipTRRadius = topRightRadius > 0 ? topRightRadius + extraRadius : 0;
        float clipBLRadius = bottomLeftRadius > 0 ? bottomLeftRadius + extraRadius : 0;
        float clipBRRadius = bottomRightRadius > 0 ? bottomRightRadius + extraRadius : 0;
        clipPath.reset();
        clipPath.addRoundRect(new RectF(0, 0, w, h),
                new float[]{clipTLRadius, clipTLRadius, clipTRRadius, clipTRRadius,
                        clipBRRadius, clipBRRadius, clipBLRadius, clipBLRadius},
                Path.Direction.CW);
        if (viewClipHelper != null) {
            viewClipHelper.updateClipPath(clipPath);
        }

        float borderPathPadding = borderWidth * 1F / 2;
        borderPath.reset();
        borderPath.addRoundRect(new RectF(borderPathPadding, borderPathPadding,
                        w - borderPathPadding, h - borderPathPadding),
                new float[]{topLeftRadius, topLeftRadius, topRightRadius, topRightRadius,
                        bottomRightRadius, bottomRightRadius, bottomLeftRadius, bottomLeftRadius},
                Path.Direction.CW);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updatePath(w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        if (viewClipHelper != null) {
            viewClipHelper.clip(canvas, drawAction, ViewClipHelper.containsSurfaceView(this));
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (borderPathPaint != null && !borderPath.isEmpty()) {
            canvas.drawPath(borderPath, borderPathPaint);
        }
    }
}
