package com.immomo.videosdk.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 切记：此类控件只支持 1px的分割线，如果需要的分割线太宽，请自行实现
 * Project momodev
 * Package com.immomo.momo.view
 * Created by tangyuchun on 3/31/16.
 */
public class DrawLineTextView extends TextView {
    public DrawLineTextView(Context context) {
        super(context);
        init(context, null);
    }

    public DrawLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DrawLineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawLineTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }


    private DrawLineViewHelper mDrawLineViewHelper;

    private void init(Context context, AttributeSet attrs) {
        mDrawLineViewHelper = new DrawLineViewHelper();
        mDrawLineViewHelper.init(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 绘制分割线
         */
        if (mDrawLineViewHelper != null) {
            mDrawLineViewHelper.onDrawLine(this, canvas);
        }
    }

    public void setDrawLine(boolean left, boolean top, boolean right, boolean bottom) {
        if (mDrawLineViewHelper != null) {
            mDrawLineViewHelper.setDrawLine(left, top, right, bottom);
            invalidate();
        }
    }
}
