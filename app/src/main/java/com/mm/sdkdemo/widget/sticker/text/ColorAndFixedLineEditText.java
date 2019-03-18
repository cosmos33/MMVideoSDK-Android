package com.mm.sdkdemo.widget.sticker.text;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Created by XiongFangyu on 17/2/27.
 */
public class ColorAndFixedLineEditText extends ColorEditText {
    private FixedLineTextHelper helper;

    public ColorAndFixedLineEditText(Context context) {
        this(context, null);
    }

    public ColorAndFixedLineEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorAndFixedLineEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public ColorAndFixedLineEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        helper = new FixedLineTextHelper(context, attrs, defStyleAttr, defStyleRes);
        int maxLine = helper.getMaxLine();
        if (maxLine <= 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            maxLine = getMaxLines();
        }
        int maxTextSize = helper.getMaxTextSize();

        if (maxTextSize <= 0)
            maxTextSize = px2Sp(getTextSize());
        if (maxTextSize > 0) {
            helper.setMaxTextSize(maxTextSize);
            setTextSize(TypedValue.COMPLEX_UNIT_SP, maxTextSize);
        }

        helper.setMaxLine(maxLine);
        setMaxLines(maxLine);
        helper.initListener(this);
    }

    @Override
    protected void onMeasure(int ws, int hs) {
        super.onMeasure(ws, hs);
        int textWidth = getMeasuredWidth();
        final int sw = getScreenWidth();
        if (textWidth >= sw) {
            textWidth = sw;
        }
        textWidth -= getPaddingLeft() + getPaddingRight();
        helper.setTextWidth(textWidth);
    }

    private int px2Sp(float px) {
        if (px >= 0) {
            return (int) (px / getResources().getDisplayMetrics().scaledDensity + 0.5f);
        } else {
            return 0;
        }
    }

    private int getScreenWidth() {
        return getResources().getDisplayMetrics().widthPixels;
    }
}
