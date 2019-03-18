package com.mm.sdkdemo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.mm.sdkdemo.R;

/**
 * @author shidefeng
 * @since 2017/6/2.
 */
public class RatioFrameLayout extends FrameLayout {

    public static final float DEFAULT_LAYOUT_RATIO = 0.57f;

    protected float mRatio = DEFAULT_LAYOUT_RATIO;

    public RatioFrameLayout(Context context) {
        this(context, null);
    }

    public RatioFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RatioFrameLayout);
        mRatio = ta.getFloat(R.styleable.RatioFrameLayout_ratio, DEFAULT_LAYOUT_RATIO);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int resetHeight = (int) (measureWidth / getRatio());
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(resetHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setRatio(float ratio) {
        mRatio = ratio;
        requestLayout();
    }

    public float getRatio() {
        return mRatio;
    }
}