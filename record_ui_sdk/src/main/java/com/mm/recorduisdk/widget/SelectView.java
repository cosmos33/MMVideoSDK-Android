package com.mm.recorduisdk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.recorduisdk.R;

/**
 * @author shidefeng
 * @since 2017/6/5.
 */

public class SelectView extends View {

    float mStrokeWidth;

    private Paint mRingPaint;
    private Paint mFillPaint;
    private String mText = "";

    private boolean mSelected;
    private TextPaint mTextPaint;
    private int shadowColor = UIUtils.getColor(R.color.album_item_shadow_color);

    public SelectView(Context context) {
        this(context, null);
    }

    public SelectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mStrokeWidth = dp2px(1.5f);

        mRingPaint = new Paint();
        mRingPaint.setAntiAlias(true);
        mRingPaint.setDither(true);
        mRingPaint.setStyle(Paint.Style.STROKE);
        mRingPaint.setStrokeWidth(mStrokeWidth);
        mRingPaint.setShadowLayer(UIUtils.getPixels(2f),0,0,shadowColor);


        mFillPaint = new Paint();
        mFillPaint.setAntiAlias(true);
        mFillPaint.setDither(true);
        mFillPaint.setStyle(Paint.Style.FILL);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(sp2px(14));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mSelected) {
            if (TextUtils.isEmpty(mText)) {
                return;
            }

            drawCircle(canvas, 0xffffffff, 0xff3bb3fa, true);

            final int baseX = (int) (getWidth() - mTextPaint.measureText(mText)) >> 1;
            final int baseY = (int) (getHeight() - mTextPaint.descent() - mTextPaint.ascent()) >> 1;
            canvas.drawText(mText, baseX, baseY, mTextPaint);

        } else {
            drawCircle(canvas, 0x4cffffff, 0x19ffffff, false);
        }
    }

    private void drawCircle(Canvas canvas, int ringColor, int fillColor, boolean isSelected) {
        final int w = getWidth();
        final int h = getHeight();

        final int halfW = w >> 1;
        final int halfH = h >> 1;

        final int cx = (w - getPaddingLeft() - getPaddingRight()) >> 1;
        final int cy = (h - getPaddingBottom() - getPaddingTop()) >> 1;

        final int radius = Math.min(cx, cy);

        float r1;
        float r2;

        if (isSelected) {
            r1 = radius - mStrokeWidth * 0.5f;
            r2 = radius - mStrokeWidth * 0.8f;
        } else {
            r1 = radius - mStrokeWidth * 0.5f;
            r2 = r1;
        }

        mRingPaint.setColor(ringColor);
        mFillPaint.setColor(fillColor);

        canvas.drawCircle(halfW, halfH, r1, mRingPaint);
        canvas.drawCircle(halfW, halfH, r2, mFillPaint);
    }

    @Override
    public void setSelected(boolean selected) {
        mSelected = selected;
        invalidate();
    }

    public void setText(String text) {
        mText = text;
        invalidate();
    }

    private float dp2px(float dip) {
        return getResources().getDisplayMetrics().density * dip;
    }

    private float sp2px(float sp) {
        return getResources().getDisplayMetrics().scaledDensity * sp;
    }
}
