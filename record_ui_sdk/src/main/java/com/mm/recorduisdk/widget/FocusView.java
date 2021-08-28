package com.mm.recorduisdk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.mm.base_business.utils.UIUtils;
import com.mm.mmutil.task.MomoMainThreadExecutor;

/**
 * Created on 2019/8/27.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class FocusView extends View {

    private Paint mPaint;
    private int mRectEdgeLength;
    private int mSlideWidth;
    private Rect mRect;
    private int mInnerLineLength;
    private float mPercentage = 0.5f;
    private static final int sSunOuterSize = UIUtils.getPixels(30);
    private Point mSlideLinePoint1;
    private Point mSlideLinePoint2;
    private Point mSlideLinePoint3;
    private Point mSlideLinePoint4;
    private int visibleWidth = UIUtils.getPixels(120);
    private int visibleHeight = UIUtils.getPixels(200);
    private Point mFocusCenter;
    private boolean isNeedDraw;
    private OnSlideListener mSlideListener;

    public FocusView(Context context) {
        this(context, null);
    }

    public FocusView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mPaint.setColor(0xffffff00);
        mPaint.setStyle(Paint.Style.STROKE);

        mRectEdgeLength = (int) (visibleWidth * 3 / 5.0f);
        mInnerLineLength = mRectEdgeLength / 10;

        if (mPaint != null) {
            mPaint.setStrokeWidth(UIUtils.getPixels(1));
        }


        mRect = new Rect(0, (visibleHeight >> 1) - (mRectEdgeLength >> 1), mRectEdgeLength, (visibleHeight >> 1) + (mRectEdgeLength >> 1));

        mSlideWidth = visibleWidth - mRectEdgeLength;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isNeedDraw) {
            return;
        }
        canvas.save();
        canvas.translate(mFocusCenter.x - (mRectEdgeLength >> 1), mFocusCenter.y - (visibleHeight >> 1));
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(mRect, mPaint);
//        left
        canvas.drawLine(0, visibleHeight >> 1, mInnerLineLength, visibleHeight >> 1, mPaint);
//        right
        canvas.drawLine(mRectEdgeLength, visibleHeight >> 1, mRectEdgeLength - mInnerLineLength, visibleHeight >> 1, mPaint);
//        top
        canvas.drawLine(mRectEdgeLength >> 1, mRect.top, mRectEdgeLength >> 1, mRect.top + mInnerLineLength, mPaint);
//        bottom
        canvas.drawLine(mRectEdgeLength >> 1, mRect.bottom, mRectEdgeLength >> 1, mRect.bottom - mInnerLineLength, mPaint);

//        drawSlideLine
        canvas.drawLine(mSlideLinePoint1.x, mSlideLinePoint1.y, mSlideLinePoint2.x, mSlideLinePoint2.y, mPaint);
        canvas.drawLine(mSlideLinePoint3.x, mSlideLinePoint3.y, mSlideLinePoint4.x, mSlideLinePoint4.y, mPaint);

//        drawSun
        int sunCenterY = mSlideLinePoint2.y + (sSunOuterSize >> 1);

        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mSlideLinePoint2.x, sunCenterY, sSunOuterSize / 7, mPaint);

        canvas.save();

        for (int i = 1; i <= 8; i++) {
            canvas.drawLine(mSlideLinePoint2.x + sSunOuterSize / 6 + sSunOuterSize / 10, sunCenterY, mSlideLinePoint2.x + 2 * sSunOuterSize / 5, sunCenterY, mPaint);
            canvas.rotate(i * (360 / 8.0f), mSlideLinePoint2.x, sunCenterY);
        }

        canvas.restore();
        canvas.restore();
    }


    public void showFocusView(Point focusCenter) {
        mPercentage = 0.5f;
        mFocusCenter = focusCenter;
        updatePercentage(mPercentage);
        isNeedDraw = true;
        MomoMainThreadExecutor.cancelAllRunnables(this);
        MomoMainThreadExecutor.postDelayed(this, new Runnable() {
            @Override
            public void run() {
                setVisibility(GONE);
            }
        }, 2000);
        setVisibility(VISIBLE);
    }

    private void updatePercentage(float percentage) {

        percentage = Math.max(0, percentage);
        percentage = Math.min(1.0f, percentage);
        int height = visibleHeight;
        int v = (int) ((height - sSunOuterSize * 3) * percentage);
        int x = mFocusCenter.x > (UIUtils.getScreenWidth() >> 1) ?
                -mSlideWidth + (mSlideWidth >> 1)
                : visibleWidth - mSlideWidth + (mSlideWidth >> 1);
        mSlideLinePoint1 = new Point(x, sSunOuterSize);
        mSlideLinePoint2 = new Point(x, v + sSunOuterSize);
        mSlideLinePoint3 = new Point(x, Math.min(height - sSunOuterSize, v + 2 * sSunOuterSize));
        mSlideLinePoint4 = new Point(x, height - (sSunOuterSize));
        mPercentage = percentage;
        if (mSlideListener != null) {
            mSlideListener.onSlide(mPercentage);
        }
        invalidate();
    }


    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float currentPercentage = distanceY * 1.0f / visibleHeight;
            updatePercentage(mPercentage - currentPercentage);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }
    };


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MomoMainThreadExecutor.cancelAllRunnables(this);
    }

    private GestureDetector gestureDetector = new GestureDetector(gestureListener);

    public void feedEvent(MotionEvent event) {
        if (isNeedDraw) {
            gestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                MomoMainThreadExecutor.cancelAllRunnables(this);
                MomoMainThreadExecutor.postDelayed(this, new Runnable() {
                    @Override
                    public void run() {
                        setVisibility(GONE);
                    }
                }, 2000);
            }else {
                MomoMainThreadExecutor.cancelAllRunnables(this);
            }
        }
    }

    public OnSlideListener getSlideListener() {
        return mSlideListener;
    }

    public void setOnSlideListener(OnSlideListener slideListener) {
        this.mSlideListener = slideListener;
    }

    public interface OnSlideListener {
        void onSlide(float percentage);
    }
}