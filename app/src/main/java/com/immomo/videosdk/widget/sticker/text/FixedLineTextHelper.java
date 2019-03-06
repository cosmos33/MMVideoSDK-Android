package com.immomo.videosdk.widget.sticker.text;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;

import com.immomo.videosdk.R;

/**
 * Created by XiongFangyu on 17/2/27.
 *
 * 固定行数的textView 帮助类
 * 当text过长时，自动缩小textsize，直到{@link #minTextSize}
 * 当textsize和{@link #minTextSize}相等时，若此时行数小于{@link #maxLine}则增加一行，否则丢弃更改
 * 删除text时，若只有一行，则增大textsize直到{@link #maxTextSize}
 *
 * {@link #minTextSize} {@link #maxTextSize}的单位为sp，不用在xml里添加sp
 *
 * {@link R.styleable#FixedLineTextView_fltv_max_lines}能覆盖
 * @attr ref android.R.styleable#TextView_maxLines
 *
 * {@link R.styleable#FixedLineTextView_fltv_max_text_size}能覆盖
 * @attr ref android.R.styleable#TextView_textsize
 */
public class FixedLineTextHelper {
    private int maxLine = 2;
    private int textLine = 1;

    private int textSize;
    private int maxTextSize;
    private int minTextSize;

    private int textWidth = 0;
    private Paint textPaint;

    public FixedLineTextHelper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (context != null && attrs != null) {
            final Resources.Theme theme = context.getTheme();
            TypedArray t = theme.obtainStyledAttributes(attrs,
                                                        R.styleable.FixedLineTextView,
                                                        defStyleAttr, defStyleRes);
            TypedArray a = null;
            int id = t.getResourceId(R.styleable.FixedLineTextView_fltv_style, -1);
            if (id != -1) {
                a = theme.obtainStyledAttributes(id, R.styleable.FixedLineTextView);
            }
            initStyle(a);
            initStyle(t);
        }
        textSize = maxTextSize;
    }

    public void setTextWidth(int width) {
        textWidth = width;
    }

    public int getMaxTextSize() {
        return maxTextSize;
    }

    public int getMinTextSize() {
        return minTextSize;
    }

    public int getMaxLine() {
        return maxLine;
    }

    public void setMaxLine(int maxLine) {
        this.maxLine = maxLine;
    }

    public void setMaxTextSize(int maxTextSize) {
        this.maxTextSize = maxTextSize;
        this.textSize = maxTextSize;
    }

    private void initStyle(TypedArray a) {
        if (a != null) {
            maxLine = a.getInt(R.styleable.FixedLineTextView_fltv_max_lines, maxLine);
            minTextSize = a.getInt(R.styleable.FixedLineTextView_fltv_min_text_size, minTextSize);
            maxTextSize = a.getInt(R.styleable.FixedLineTextView_fltv_max_text_size, maxTextSize);
            a.recycle();
        }
    }

    public void initListener(final TextView v) {
        textPaint = v.getPaint();
        v.addTextChangedListener(new TextWatcher() {
            private CharSequence beforeText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (count > 0 && after <= 0 && textLine == 1 && textSize < maxTextSize) {
                    textSize++;
                    internalSetTextSize(v);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (textSize == minTextSize) {
                    if (v.getLineCount() > maxLine) {
                        v.setText(beforeText);
                        if (v instanceof EditText)
                            ((EditText) v).setSelection(beforeText.length());
                        return;
                    }
                } else {
                    final float px = measureText(s);
                    if (px > textWidth) {
                        //行数会增加
                        if (!getTextSizeByChar(v, s)) {
                            final int mtl = textLine + 1;
                            if (mtl > maxLine) {
                                v.setText(beforeText);
                                if (v instanceof EditText)
                                    ((EditText) v).setSelection(beforeText.length());
                                return;
                            }
                        }
                    }
                }
                beforeText = s.toString();
            }
        });

        v.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int c = v.getLineCount();
                if (c > 0)
                    textLine = c;
            }
        });
    }

    private boolean getTextSizeByChar(TextView tv, CharSequence c) {
        while (textSize > minTextSize) {
            textSize--;
            internalSetTextSize(tv);
            if (measureText(c) <= textWidth) {
                return true;
            }
        }
        return false;
    }

    private void internalSetTextSize(TextView tv) {
        setTextSize(tv, textSize);
    }

    public void setTextSize(TextView tv, int sp) {
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
    }

    private float measureText(CharSequence s) {
        return textPaint.measureText(s, 0, s.length());
    }

    @Override
    public String toString() {
        return "FixedLineTextHelper{" +
                "textWidth=" + textWidth +
                ", maxLine=" + maxLine +
                ", textLine=" + textLine +
                ", textSize=" + textSize +
                ", maxTextSize=" + maxTextSize +
                ", minTextSize=" + minTextSize +
                '}';
    }
}
