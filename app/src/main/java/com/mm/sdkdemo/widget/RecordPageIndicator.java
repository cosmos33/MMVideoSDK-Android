package com.mm.sdkdemo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mm.sdkdemo.R;

import java.util.ArrayList;

/**
 * Created by XiongFangyu on 2017/6/6.
 *
 * 拍摄页下方游标
 * 可设置多个文字
 */
public class RecordPageIndicator extends View implements PageIndicator {
    private static final int MIN_CLICK_TIME = 200;

    private ViewPager mViewPager;
    private int mCurrentPage;
    private float mPageOffset;
    private int mScrollState;

    private Paint mPaint;
    private ArrayList<TextDrawable> textDrawables;
    private TextDrawable checkedText;

    private int textSize;
    private int textColor;
    private int checkedTextColor;
    private int eachMargin;
    private int textMarginBottom;
    private int bottomWidth;
    private int bottomHeight;
    private int bottomColor;
    private int backColor;

    private int initTranslate;

    private Rect bottomRect;
    private Rect backRect;
    private float backTranslateY;
    private float maxTranslateY;

    public RecordPageIndicator(Context context) {
        this(context, null);
    }

    public RecordPageIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordPageIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RecordPageIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textDrawables = new ArrayList<>();
        bottomRect = new Rect();
        backRect = new Rect();

        if (context == null || attrs == null)
            return;

        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs,
                                                    R.styleable.RecordPageIndicator, defStyleAttr, defStyleRes);
        TypedArray appearance = null;
        int ap = a.getResourceId(
                R.styleable.RecordPageIndicator_rpi_style, -1);
        if (ap != -1) {
            appearance = theme.obtainStyledAttributes(
                    ap, R.styleable.RecordPageIndicator);
        }
        initStyle(appearance);
        initStyle(a);
    }

    private void initStyle(TypedArray a) {
        if (a != null) {
            textSize = a.getDimensionPixelSize(R.styleable.RecordPageIndicator_rpi_text_size, textSize);
            textColor = a.getColor(R.styleable.RecordPageIndicator_rpi_text_color, textColor);
            checkedTextColor = a.getColor(R.styleable.RecordPageIndicator_rpi_checked_text_color, checkedTextColor);
            eachMargin = a.getDimensionPixelOffset(R.styleable.RecordPageIndicator_rpi_each_margin, eachMargin);
            textMarginBottom = a.getDimensionPixelOffset(R.styleable.RecordPageIndicator_rpi_text_margin_bottom, textMarginBottom);
            bottomWidth = a.getDimensionPixelOffset(R.styleable.RecordPageIndicator_rpi_bottom_width, bottomWidth);
            bottomHeight = a.getDimensionPixelOffset(R.styleable.RecordPageIndicator_rpi_bottom_height, bottomHeight);
            bottomColor = a.getColor(R.styleable.RecordPageIndicator_rpi_bottom_color, bottomColor);
            backColor = a.getColor(R.styleable.RecordPageIndicator_rpi_back_color, backColor);
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int wm = MeasureSpec.getMode(widthMeasureSpec);
        final int ws = MeasureSpec.getSize(widthMeasureSpec);
        final int hm = MeasureSpec.getMode(heightMeasureSpec);
        final int hs = MeasureSpec.getSize(heightMeasureSpec);

        final int pl = getPaddingLeft();
        final int pt = getPaddingTop();
        final int pr = getPaddingRight();
        final int pb = getPaddingBottom();

        int resultWidth = ws;
        int resultHeight = hs;

        switch (wm) {
            case MeasureSpec.EXACTLY:
                resultWidth = ws;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                resultWidth = Math.min(getNeedWidth() + pl + pr, ws);
                break;
        }

        switch (hm) {
            case MeasureSpec.EXACTLY:
                resultHeight = hs;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                resultHeight = Math.min(getNeedHeight() + pt + pb, hs);
                break;
        }
        setMeasuredDimension(resultWidth, resultHeight);
        backRect.set(0, 0, resultWidth, resultHeight);
        maxTranslateY = resultHeight;

        layoutTextDrawable(pl, pt, resultWidth - pr, resultHeight - pb);

        final int hw = resultWidth >> 1;
        final int hbw = bottomWidth >> 1;
        final int bottom = resultHeight - pb;
        bottomRect.set(hw - hbw, bottom - bottomHeight, hw + hbw, bottom);

        if (textDrawables != null && !textDrawables.isEmpty()) {
            TextDrawable first = textDrawables.get(0);
            final Rect r = first.getBounds();
            final int cx = r.centerX();
            initTranslate = bottomRect.centerX() - cx;
        }
    }

    private int getNeedWidth() {
        if (textDrawables == null || textDrawables.isEmpty())
            return 0;
        int width = 0;
        for (int i = 0, l = textDrawables.size(); i < l; i++) {
            TextDrawable td = textDrawables.get(i);
            width += td.getIntrinsicWidth() + eachMargin;
        }
        return width - eachMargin;
    }

    private int getNeedHeight() {
        int textH = 0;
        if (textDrawables == null || textDrawables.isEmpty()) {
            textH = textSize;
        } else {
            TextDrawable td = textDrawables.get(0);
            textH = td.getIntrinsicHeight();
        }
        return textH + textMarginBottom + bottomHeight;
    }

    private void layoutTextDrawable(int l, int t, int r, int b) {
        if (textDrawables == null || textDrawables.isEmpty())
            return;

        int fw = l;
        for (int i = 0, len = textDrawables.size(); i < len; i++) {
            TextDrawable td = textDrawables.get(i);
            final int w = td.getIntrinsicWidth();
            final int fr = fw + w;
            td.setBounds(fw, t, fr, t + td.getIntrinsicHeight());
            fw = fr + eachMargin;
        }
    }

    private long downTime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isEnabled())
            return false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                if (System.currentTimeMillis() - downTime <= MIN_CLICK_TIME) {
                    onClick(ev.getX(), ev.getY());
                }
                break;
        }
        return true;
    }

    private void onClick(float x, float y) {
        if (textDrawables == null || textDrawables.isEmpty())
            return;
        for (int i = 0, l = textDrawables.size(); i < l; i++) {
            final TextDrawable td = textDrawables.get(i);
            Rect bounds = td.getBounds();
            final int left = bounds.left + drawTranslateX;
            final int right = bounds.right + drawTranslateX;
            if (x >= left && x <= right) {
                onClick(i);
                break;
            }
        }
    }

    private void onClick(int index) {
        mViewPager.setCurrentItem(index, true);
    }

    private int drawTranslateX = 0;
    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 原拍摄页底部，相册和其它帧切换时，会消失/出现类似动画的黑条
         * 8.9.7_photo去掉相册帧，暂注掉这部分绘制
         */
//        if (mCurrentPage == 0) {
//            mPaint.setColor(backColor);
//            canvas.save();
//            backTranslateY = maxTranslateY * mPageOffset;
//            canvas.translate(0, backTranslateY);
//            canvas.drawRect(backRect, mPaint);
//            canvas.restore();
//        }

        mPaint.setColor(bottomColor);
        canvas.drawRect(bottomRect, mPaint);
        canvas.save();

        if (textDrawables == null || textDrawables.isEmpty())
            return;
        final int dSize = textDrawables.size();
        mCurrentPage = mCurrentPage < 0 ? 0 : mCurrentPage;
        mCurrentPage = mCurrentPage >= dSize ? dSize - 1 : mCurrentPage;

        drawTranslateX = initTranslate;
        if (mCurrentPage != 0) {
            drawTranslateX -= getTranslateByIndex(0, mCurrentPage);
        }
        if (mPageOffset > 0 && mPageOffset < 1 && mCurrentPage < dSize - 1) {
            final int t = getTranslateByIndex(mCurrentPage, mCurrentPage + 1);
            drawTranslateX -= t * mPageOffset;
        }
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            if (checkedText != null)
                checkedText.setTextColor(textColor);
            checkedText = textDrawables.get(mCurrentPage);
            checkedText.setTextColor(checkedTextColor);
        }

        canvas.translate(drawTranslateX, 0);
        for (int i = 0, l = textDrawables.size(); i < l; i++) {
            TextDrawable td = textDrawables.get(i);
            td.draw(canvas);
        }
        canvas.restore();
    }

    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (view.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        mViewPager.addOnPageChangeListener(this);
        invalidate();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mViewPager.setCurrentItem(item);
        mCurrentPage = item;
        invalidate();
    }

    @Deprecated
    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
    }

    @Override
    public void notifyDataSetChanged() {
        invalidate();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurrentPage = position;
        mPageOffset = positionOffset;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mCurrentPage = position;
            invalidate();
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mScrollState = state;
        if (state == ViewPager.SCROLL_STATE_IDLE)
            invalidate();
    }

    public void setText(String... text) {
        if (text != null) {
            final int l = text.length;
            for (int i = 0; i < l; i++) {
                String t = text[i];
                TextDrawable td = new TextDrawable();
                td.setText(t);
                td.setTextSize(textSize);
                td.setTextColor(textColor);
                textDrawables.add(td);
            }
            requestLayout();
        }
    }

    public void release() {
        mViewPager.clearOnPageChangeListeners();
        mViewPager = null;
        if (textDrawables != null)
            textDrawables.clear();
        checkedText = null;
    }

    private int getTranslateByIndex(int startIndex, int endIndex) {
        TextDrawable start = textDrawables.get(startIndex);
        TextDrawable end = textDrawables.get(endIndex);
        return end.getBounds().centerX() - start.getBounds().centerX();
    }
}
