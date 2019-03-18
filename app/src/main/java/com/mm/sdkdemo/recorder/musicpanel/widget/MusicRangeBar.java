package com.mm.sdkdemo.recorder.musicpanel.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mm.sdkdemo.R;

/**
 * Created by tangyuchun on 2018/5/19.
 */

public class MusicRangeBar extends View {

    public MusicRangeBar(Context context) {
        super(context);
        init(context, null);
    }

    public MusicRangeBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MusicRangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MusicRangeBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    //开始，结束位置
    private int startOfMs, endOfMs;
    private int musicDurationOfMs;
    private int minDurationOfMs = 1000;//最短1s
    private int currentPlayingPosOfMs;

    private int colorNormal = Color.GRAY;
    private int colorPlayed = Color.WHITE;
    private int colorSelected = Color.LTGRAY;

    private int colorCover = Color.TRANSPARENT;

    private int touchBarColorNormal = 0xA0DDDDDD;
    private int touchBarColorDragging = 0xFF5c5c5c;
    private int touchBarWidth = 2;
    private int touchBarEnableDiff = 80;

    private int lineWidth = 5;// 线条宽度
    private int lineMargin = 15;//线条间隔

    private int lineHeightLong = 40;
    private int lineHeightMiddle = 20;
    private int lineHeightShort = 15;

    private int[] lines;
    private Paint coverPaint;
    private Paint linePaint;
    private RectF lineRectF;
    private OnMusicRangeChangedListener onMusicRangeChangedListener;

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MusicRangeBar);
            colorNormal = typedArray.getColor(R.styleable.MusicRangeBar_mrb_lineColorNormal, colorNormal);
            colorSelected = typedArray.getColor(R.styleable.MusicRangeBar_mrb_lineColorSelected, colorSelected);
            colorPlayed = typedArray.getColor(R.styleable.MusicRangeBar_mrb_lineColorPlayed, colorPlayed);
            colorCover = typedArray.getColor(R.styleable.MusicRangeBar_mrb_colorCover, colorCover);

            lineHeightLong = typedArray.getDimensionPixelSize(R.styleable.MusicRangeBar_mrb_lineHeightLong, lineHeightLong);
            lineHeightMiddle = typedArray.getDimensionPixelSize(R.styleable.MusicRangeBar_mrb_lineHeightMiddle, lineHeightMiddle);
            lineHeightShort = typedArray.getDimensionPixelSize(R.styleable.MusicRangeBar_mrb_lineHeightShort, lineHeightShort);

            lineWidth = typedArray.getDimensionPixelSize(R.styleable.MusicRangeBar_mrb_lineWidth, lineWidth);
            lineMargin = typedArray.getDimensionPixelSize(R.styleable.MusicRangeBar_mrb_lineMargin, lineMargin);

            touchBarColorNormal = typedArray.getColor(R.styleable.MusicRangeBar_mrb_touchBarColorNormal, touchBarColorNormal);
            touchBarColorDragging = typedArray.getColor(R.styleable.MusicRangeBar_mrb_touchBarColorDragging, touchBarColorDragging);
            touchBarWidth = typedArray.getDimensionPixelSize(R.styleable.MusicRangeBar_mrb_touchBarWidth, touchBarWidth);

            typedArray.recycle();
        }
        if (isInEditMode()) {
            musicDurationOfMs = 30 * 1000;
            startOfMs = 10 * 1000;
            endOfMs = 20 * 1000;
            currentPlayingPosOfMs = 15 * 1000;
            minDurationOfMs = 5 * 1000;
        }
        lines = new int[]{lineHeightShort, lineHeightMiddle, lineHeightShort, lineHeightLong};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (musicDurationOfMs <= 0) {
            return;
        }

        int start = getStartPos();
        int end = getEndPos();
        drawMusicLine(canvas, start, end);
        drawCover(canvas, start, end);
    }

    private void drawCover(Canvas canvas, int start, int end) {
        if (coverPaint == null) {
            coverPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        coverPaint.setColor(colorCover);
        canvas.drawRect(start, 0, end + touchBarWidth, getHeight(), coverPaint);

        if (endOfMs <= 0) {
            return;
        }
        //draw touch bar
        coverPaint.setColor(isDraggingRangeLeft ? touchBarColorDragging : touchBarColorNormal);
        canvas.drawRect(start, 0, start + touchBarWidth, getHeight(), coverPaint);
        int right = end + touchBarWidth;
        if (right > getWidth()) {
            right = getWidth() - touchBarWidth;
        }
        coverPaint.setColor(isDraggingRangeRight ? touchBarColorDragging : touchBarColorNormal);
        canvas.drawRect(end, 0, right, getHeight(), coverPaint);
    }

    private void drawMusicLine(Canvas canvas, int start, int end) {
        if (lines == null || lines.length <= 0) {
            return;
        }
        if (lineRectF == null) {
            lineRectF = new RectF();
        }
        if (linePaint == null) {
            linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        float playingPos = (((float) currentPlayingPosOfMs) / musicDurationOfMs) * getWidth();

        float radius = lineWidth;
        float lineLeft = lineMargin + getPaddingLeft();
        int maxRight = getWidth() - getPaddingRight() - lineMargin;

        int lineMarginVertical;
        int lineIndex = 0;// 短，长，短，更长
        while (lineLeft < maxRight) {
            if (lineLeft < start || lineLeft > end) {
                linePaint.setColor(colorNormal);
            } else if (lineLeft < playingPos) {
                linePaint.setColor(colorPlayed);
            } else {
                linePaint.setColor(colorSelected);
            }
            int lineIndexDiff = lineIndex % lines.length;
            lineMarginVertical = (getHeight() - lines[lineIndexDiff]) / 2;
            lineRectF.set(lineLeft, lineMarginVertical, lineLeft + lineWidth, getHeight() - lineMarginVertical);
            drawRoundRect(canvas, linePaint, lineRectF, radius);
            lineLeft += lineMargin + lineWidth;
            lineIndex++;
        }
    }

    private static void drawRoundRect(Canvas canvas, Paint paint, RectF rectF, float radius) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(rectF, radius, radius, paint);
        } else {
            Path path = new Path();
            path.moveTo(rectF.left + radius, rectF.top);
            path.lineTo(rectF.right - radius, rectF.top);
            path.quadTo(rectF.right, rectF.top, rectF.right, rectF.top + radius);

            path.lineTo(rectF.right, rectF.bottom - radius);
            path.quadTo(rectF.right, rectF.bottom, rectF.right - radius, rectF.bottom);

            path.lineTo(rectF.left + radius, rectF.bottom);
            path.quadTo(rectF.left, rectF.bottom, rectF.left, rectF.bottom - radius);

            path.lineTo(rectF.left, rectF.top + radius);
            path.quadTo(rectF.left, rectF.top, rectF.left + radius, rectF.top);
            canvas.drawPath(path, paint);
        }
    }

    private int getStartPos() {
        return getPaddingLeft() + (int) ((((float) startOfMs) / musicDurationOfMs) * getAvailableWidth());
    }

    private int getEndPos() {
        return getPaddingLeft() + (int) ((((float) endOfMs) / musicDurationOfMs) * getAvailableWidth());
    }

    private boolean isDraggingRangeLeft = false;
    private boolean isDraggingRangeRight = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean canClick = true;
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (isInLeftRangeChange(event.getX())) {
                onTouchingBar();
                isDraggingRangeLeft = true;
                canClick = false;
            } else if (isInRightRangeChange(event.getX())) {
                onTouchingBar();
                isDraggingRangeRight = true;
                canClick = false;
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            float x = event.getX();
            if (x > getPaddingLeft() && x < getWidth() - getPaddingRight()) {
                int minDurationLength = getMinDurationLength();
                int posOfMs = (int) (((x - getPaddingLeft()) / getAvailableWidth()) * musicDurationOfMs);
                if (isDraggingRangeLeft && x < (getEndPos() - minDurationLength)) {
                    startOfMs = posOfMs;
                    invalidate();
                } else if (isDraggingRangeRight && x > (getStartPos() + minDurationLength)) {
                    endOfMs = posOfMs;
                    invalidate();
                }
            }
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if ((isDraggingRangeLeft || isDraggingRangeRight) && onMusicRangeChangedListener != null) {
                onMusicRangeChangedListener.onRangeChanged(this, startOfMs, endOfMs);
                setCurrentPlayingPosOfMs(startOfMs);
            }
            onReleaseDragging();
        }
        if (canClick) {
            return super.onTouchEvent(event);
        }
        super.onTouchEvent(event);
        return true;
    }

    private void onTouchingBar() {
        //避免与上层冲突
        getParent().requestDisallowInterceptTouchEvent(true);
    }

    private void onReleaseDragging() {
        isDraggingRangeLeft = false;
        isDraggingRangeRight = false;
        getParent().requestDisallowInterceptTouchEvent(false);
    }

    private int getAvailableWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int getMinDurationLength() {
        return (int) ((((float) minDurationOfMs) / musicDurationOfMs) * getAvailableWidth());
    }

    public void setMinDurationOfMs(int minDurationOfMs) {
        this.minDurationOfMs = minDurationOfMs;
    }

    private boolean isInLeftRangeChange(float x) {
        int start = getStartPos();
        return x >= start - touchBarEnableDiff && x <= start + touchBarEnableDiff;
    }

    private boolean isInRightRangeChange(float x) {
        int end = getEndPos();
        return x >= end - touchBarEnableDiff && x <= end + touchBarEnableDiff;
    }

    public void setStartAndEnd(int startOfMs, int endOfMs, int musicDurationOfMs) {
        this.startOfMs = startOfMs;
        this.endOfMs = endOfMs;
        this.musicDurationOfMs = musicDurationOfMs;
        invalidate();
    }


    public void setLines(final int[] lines) {
        this.lines = lines;
        invalidate();
    }


    public void setCurrentPlayingPosOfMs(int currentPlayingPosOfMs) {
        this.currentPlayingPosOfMs = currentPlayingPosOfMs;
        invalidate();
    }

    public void setOnMusicRangeChangedListener(OnMusicRangeChangedListener onMusicRangeChangedListener) {
        this.onMusicRangeChangedListener = onMusicRangeChangedListener;
    }

    public interface OnMusicRangeChangedListener {
        void onRangeChanged(MusicRangeBar rangeBar, int startOfMs, int endOfMs);
    }
}
