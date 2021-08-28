package com.mm.recorduisdk.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.log.Log4Android;
import com.mm.recorduisdk.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Project MomoDemo
 * Package com.mm.momo.view
 * Created by tangyuchun on 2/13/17.
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * <p>
 * 举例
 * rangeBar = (SlideIndicatorBar) findViewById(R.id.range_bar);
 * rangeBar.setIndicatorBuilder(new SlideIndicatorBuilder() {
 *
 * @Override public View buildIndicator(SlideIndicatorBar parent) {
 * vIndicator = new TextView(parent.getContext());
 * vIndicator.setGravity(Gravity.CENTER);
 * vIndicator.setBackgroundResource(R.drawable.bg_range_indicator);
 * int size = dp2px(40f);
 * FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size, size);
 * vIndicator.setLayoutParams(lp);
 * return vIndicator;
 * }
 * });
 * rangeBar.setBackAreaHeight(dp2px(24f));
 * rangeBar.setBackAreaColor(Color.GREEN);
 * <p>
 * rangeBar.setIndicatorPointColor(Color.RED);
 * rangeBar.setIndicatorPointRadius(dp2px(2f));
 * rangeBar.setCurrentIndicatorIndex(2);
 * <p>
 * rangeBar.addIndicatorSlideListener(new SlideIndicatorBar.OnIndicatorSlideListener() {
 * @Override public void onIndicatorSliding(View indicator, int pos) {
 * refreshIndicatorText(pos);
 * }
 * @Override public void onIndicatorSettled(View indicator, int pos) {
 * refreshIndicatorText(pos);
 * }
 * });
 */

public class SlideIndicatorBar extends FrameLayout {
    /**
     * 有多少个档
     */
    private int indicatorPointCount = 0;

    private CharSequence[] indicators;

    private List<OnIndicatorSlideListener> mSlideListeners;
    /**
     * 当前位置
     */
    private int currentIndicatorIndex = -1;

    private View vIndicator;
    private ViewDragHelper mDragHelper;
    private TextPaint textPaint;
    private float textHeight;


    public SlideIndicatorBar(Context context) {
        super(context);
        init(context);
    }

    public SlideIndicatorBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SlideIndicatorBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlideIndicatorBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setIndicatorBuilder(SlideIndicatorBuilder builder) {
        if (builder == null) {
            vIndicator = null;
        } else {
            vIndicator = builder.buildIndicator(this);
        }
        removeAllViews();
        if (vIndicator != null) {
            ViewGroup.LayoutParams lp = vIndicator.getLayoutParams();
            if (lp == null || !(lp instanceof LayoutParams)) {
                LayoutParams flp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                flp.gravity = Gravity.CENTER_VERTICAL;
                vIndicator.setLayoutParams(flp);
            } else {
                LayoutParams flp = (LayoutParams) lp;
                flp.gravity = Gravity.CENTER_VERTICAL;
                vIndicator.setLayoutParams(flp);
            }
            addView(vIndicator, lp);

            mDragHelper = ViewDragHelper.create(this, 1f, new SlideViewDragCallback(vIndicator));
        } else {
            mDragHelper = null;
        }
    }

    private void init(Context context) {
        setWillNotDraw(false);
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(UIUtils.getColor(R.color.white));
        textPaint.setTextSize(UIUtils.sp2pix(11));
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        textHeight = fm.descent + fm.ascent;
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                setCurrentIndicatorIndex(currentIndicatorIndex);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mDragHelper == null || indicatorPointCount <= 0) {
            return super.onInterceptTouchEvent(ev);
        }
        if (ev.getAction() == MotionEvent.ACTION_CANCEL) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    private ValueAnimator moveIndicatorAnim = null;

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (mDragHelper == null || indicatorPointCount <= 0) {
            return super.onTouchEvent(event);
        }
        if (isRunningAnim()) {
            return true;
        }
        //触摸其他位置时，需要将 indicator 切换到手指按下的位置，且需要一个过渡动画
        if (vIndicator != null && event != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getX(), y = event.getY();
                if (!touchInIndicator(x, y)) {
                    int oldLeft = vIndicator.getLeft();
                    int finalIndex = calculateIndicatorIndex((int) x);
                    //对应到中点位置
                    int finalLeft = finalIndex * getWidthOfPoint() + (getWidthOfPoint() - vIndicator.getWidth()) / 2;
                    moveIndicatorAnim = ValueAnimator.ofInt(oldLeft, finalLeft);
                    moveIndicatorAnim.setDuration(150);
                    moveIndicatorAnim.setInterpolator(new DecelerateInterpolator());
                    moveIndicatorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int newLeft = (int) animation.getAnimatedValue();
                            vIndicator.layout(newLeft, getPaddingTop(), newLeft + vIndicator.getWidth(), getPaddingTop() + vIndicator.getHeight());
                        }
                    });
                    moveIndicatorAnim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            //触发回调
                            int centerX = vIndicator.getLeft() + vIndicator.getWidth() / 2;
                            int finalIndex = calculateIndicatorIndex(centerX);
                            onIndicatorSettled(finalIndex);
                            //动画结束时，捕获拖拽
                            mDragHelper.captureChildView(vIndicator, 0);
                        }
                    });
                    moveIndicatorAnim.start();
                    return true;
                }
            }
        }
        try {
            mDragHelper.processTouchEvent(event);
        } catch (IllegalArgumentException e) {
        }
        return true;
    }

    private boolean touchInIndicator(float x, float y) {
        return x >= vIndicator.getLeft() && x <= vIndicator.getRight() && y >= vIndicator.getTop() && y <= vIndicator.getBottom();
    }

    private boolean isRunningAnim() {
        return moveIndicatorAnim != null && moveIndicatorAnim.isRunning();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawIndicatorPoints(canvas);
    }

    private void drawIndicatorPoints(Canvas canvas) {
        if (indicatorPointCount <= 0) {
            return;
        }
        int widthOfPoint = getWidthOfPoint();
        float textHeightCenter = (getHeight() - textHeight) / 2;
        for (int i = 0; i < indicatorPointCount; i++) {
            String text = indicators[i].toString();
            canvas.drawText(text, widthOfPoint * (i + 0.5f), textHeightCenter, textPaint);
        }
    }

    public void setIndicators(@NonNull CharSequence[] indicators) {
        this.indicators = indicators;
        indicatorPointCount = this.indicators.length;

    }

    public void setCurrentIndicatorIndex(int currentIndicatorIndex) {
        if (currentIndicatorIndex < 0 || currentIndicatorIndex >= indicatorPointCount) {
            return;
        }
        this.currentIndicatorIndex = currentIndicatorIndex;
        int left = (int) (((currentIndicatorIndex + 0.5) * getWidthOfPoint()) - vIndicator.getWidth() / 2);
        vIndicator.layout(left, getPaddingTop(), left + vIndicator.getWidth(), getPaddingTop() + vIndicator.getHeight());
    }


    /**
     * 一个档次占据的宽度
     * 忽略左右padding
     *
     * @return
     */
    private int getWidthOfPoint() {
        return indicatorPointCount <= 0 ? 0 : getWidth() / indicatorPointCount;
    }

    private void onIndicatorSliding(int indexOfIndicator) {
        if (mSlideListeners != null) {
            for (OnIndicatorSlideListener listener : mSlideListeners) {
                listener.onIndicatorSliding(vIndicator, indexOfIndicator);
            }
        }
    }

    private void onIndicatorSettled(int pos) {
        //fix https://fabric.io/momo6/android/apps/com.mm.momo/issues/5b0ddc6e6007d59fcd56145c?time=last-seven-days
        if (pos >= indicators.length) {
            return;
        }
        if (mSlideListeners != null) {
            for (OnIndicatorSlideListener listener : mSlideListeners) {
                listener.onIndicatorSettled(vIndicator, pos);
            }
        }
    }

    private void log(String msg) {
        if (Log4Android.getInstance().isDebug()) {
            Log.d("VideoRangeSelectorView", "tang---" + msg);
        }
    }

    private class SlideViewDragCallback extends ViewDragHelper.Callback {
        private View indicator;

        public SlideViewDragCallback(View indicator) {
            this.indicator = indicator;
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //只有slideView能够拖拽
            return child == indicator;
        }

        /**
         * 控制水平方向的拖拽范围，不要超出父容器
         *
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            log("tang-----clampViewPositionHorizontal  " + left + "  " + dx);
            int finalLeft = left;
            int paddingLeft = getPaddingLeft();
            if (left < paddingLeft) {
                finalLeft = paddingLeft;
            } else {
                int posX = getWidth() - child.getWidth() - getPaddingRight();
                if (left > posX) {
                    finalLeft = posX;
                }
            }
            int indexOfIndicator = calculateIndicatorIndex(finalLeft);
            onIndicatorSliding(indexOfIndicator);
            return finalLeft;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            int paddingTop = getPaddingTop();
            if (top < paddingTop) {
                return paddingTop;
            }
            int pos = getHeight() - child.getHeight() - getPaddingBottom();
            if (top > pos) {
                return pos;
            }
            return top;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            /**
             * 让slidview回到对应的位置，不要停留在两个item的中间
             */
            int centerX = releasedChild.getLeft() + releasedChild.getWidth() / 2;
            int finalIndex = calculateIndicatorIndex(centerX);
            int finalLeft = finalIndex * getWidthOfPoint() + (getWidthOfPoint() - releasedChild.getWidth()) / 2;
            currentIndicatorIndex = finalIndex;
            mDragHelper.settleCapturedViewAt(finalLeft, getPaddingTop());
            onIndicatorSettled(currentIndicatorIndex);

            SlideIndicatorBar.this.invalidate();
        }
    }

    /**
     * 可以让 slideView 回到指定位置
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mDragHelper != null && mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    /**
     * 计算SlideView当前指向列表中的哪个位置
     *
     * @param centerXOfIndicator Indicator的中点位置
     * @return
     */
    private int calculateIndicatorIndex(int centerXOfIndicator) {
        int widthOfPoint = getWidthOfPoint();
        return centerXOfIndicator / widthOfPoint;
    }

    public void addIndicatorSlideListener(OnIndicatorSlideListener listener) {
        if (listener == null) {
            return;
        }
        if (mSlideListeners == null) {
            mSlideListeners = new ArrayList<>();
        }
        mSlideListeners.add(listener);
    }

    public void removeIndicatorSlideListener(OnIndicatorSlideListener listener) {
        if (mSlideListeners != null) {
            mSlideListeners.remove(listener);
        }
    }

    public void clearIndicatorSlideListener() {
        if (mSlideListeners != null) {
            mSlideListeners.clear();
        }
    }

    public interface OnIndicatorSlideListener {
        void onIndicatorSliding(View indicator, int indexOfIndicator);

        void onIndicatorSettled(View indicator, int indexOfIndicator);
    }
}
