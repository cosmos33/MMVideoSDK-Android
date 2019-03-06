package com.immomo.videosdk.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.immomo.videosdk.R;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by XiongFangyu on 2017/6/27.
 */
public class OrientationTextView extends AppCompatTextView {
    private boolean vertical = true;
    private boolean topDown;

    public OrientationTextView(Context context) {
        this(context, null);
    }

    public OrientationTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OrientationTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs == null)
            return;
        TypedArray a = context.obtainStyledAttributes(attrs,
                                                      R.styleable.OrientationTextView, defStyleAttr, 0);
        setVertical(a.getBoolean(R.styleable.OrientationTextView_otv_vertical, vertical));
        a.recycle();
    }

    public void setVertical(boolean vertical) {
        boolean changed = vertical != this.vertical;
        if (changed) {
            this.vertical = vertical;
            requestLayout();
        }
    }

    public boolean isVertical() {
        return this.vertical;
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        if (!vertical) {
            return super.setFrame(l, t, l + (b - t), t + (r - l));
        }
        return super.setFrame(l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (vertical) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(heightMeasureSpec, widthMeasureSpec);
            setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (!vertical) {
            if (topDown) {
                canvas.translate(getHeight() - getPaddingTop(), 0);
                canvas.rotate(90);
            } else {
                canvas.translate(0, getWidth() - getPaddingLeft());
                canvas.rotate(-90);
            }
            canvas.clipRect(0, 0, getWidth(), getHeight(), android.graphics.Region.Op.REPLACE);
        }
        super.draw(canvas);
    }

    public void setRotateLeft(boolean left) {
        topDown = left;
        invalidate();
    }
}
