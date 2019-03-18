package com.mm.sdkdemo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.utils.ViewClipHelper;

/**
 * Created by huang.liangjie on 2018/6/23.
 * <p>
 * Momo Tech 2011-2018 © All Rights Reserved.
 */
public class RoundCornerImageView extends AppCompatImageView {
    private float mCornerRadius = 0;

    @NonNull
    private final Path clipPath = new Path();
    @NonNull
    private final ViewClipHelper viewClipHelper = new ViewClipHelper();
    @Nullable
    private ViewClipHelper.SuperDrawAction drawAction;

    public RoundCornerImageView(Context context) {
        super(context);
        init(null);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public RoundCornerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (null != attrs) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.RoundCornerLayout);
            mCornerRadius = a.getDimensionPixelOffset(R.styleable.RoundCornerLayout_radius, 0);
            a.recycle();
        }

        drawAction = new ViewClipHelper.SuperDrawAction() {
            @Override
            public void innerDraw(Canvas canvas) {
                RoundCornerImageView.super.draw(canvas);
            }
        };
    }

    private void updateClipPath(int w, int h) {
        clipPath.reset();

        clipPath.addRoundRect(new RectF(0, 0, w, h), mCornerRadius, mCornerRadius,
                Path.Direction.CW);
        viewClipHelper.updateClipPath(clipPath);
    }

    public void setCornerRadius(int cornerRadius) {
        mCornerRadius = cornerRadius;

        updateClipPath(getMeasuredWidth(), getMeasuredHeight());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        updateClipPath(w, h);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {
        viewClipHelper.clip(canvas, drawAction, false);
    }
}
