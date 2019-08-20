package com.mm.recorduisdk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.utils.ViewClipHelper;

public class StickerSeekView extends View {


    private static final int DEFAULT_VIEW_WIDTH = UIUtils.getPixels(130);
    private static final int DEFAULT_VIEW_HEIGHT = UIUtils.getPixels(130);

    private static final float TOUCH_SCALE = 1.2f;
    private static final float TIME_TOUCH_SCALE = 1.1f;

    private final int bgPaddingTop = UIUtils.getPixels(5);
    private final int bgPaddingBottom = UIUtils.getPixels(5);

    private Paint bgPaint;
    private RectF bgRectf;


    private long totleTime;

    // 普通进度条seek
    private float startSeekBarLocation;
    // 用于时间特效的seek
    private float endSeekBarLocation;

    private float progressBarLocation;

    private boolean onTouchStartSeekBar = false;
    private boolean onTouchEndSeekBar = false;
    private boolean onTouchProgressSeekBar = false;

    private OnSeekListener startSeekListener;
    private OnSeekListener endFilterSeekListener;
    private OnSeekListener progressSeekListener;

    private ViewClipHelper viewClipHelper;

    private float mCornerRadius = UIUtils.getPixels(4);
    private float mProgressBarWidth = UIUtils.getPixels(1.5f);

    private final Path clipPath = new Path();
    private Paint clipPaint;

    private Drawable mEndSeekSelectDrawable;
    private Rect mEndSeekRect;
    private Rect mEndSeekSelectRect;


    private Drawable mStartSeekSelectDrawable;
    private Rect mStartSeekRect;
    private Rect mSeekSelectRect;
    private Paint mProgressSeekPaint;
    private int mMinInterval = UIUtils.getPixels(10);

    public StickerSeekView(Context context) {
        super(context);
        init();
    }

    public StickerSeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StickerSeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StickerSeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setAlpha((int) (0.95 * 255));
        bgRectf = new RectF();

        viewClipHelper = new ViewClipHelper();

        clipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clipPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        clipPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));


        mEndSeekSelectDrawable = UIUtils.getDrawable(R.drawable.video_edit_select_seek_bar);

        mStartSeekSelectDrawable = UIUtils.getDrawable(R.drawable.video_edit_select_seek_bar);

        mProgressSeekPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressSeekPaint.setStrokeWidth(mProgressBarWidth);
        mProgressSeekPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        updateClipPath(w, h);
        Rect endRect = mEndSeekSelectDrawable.getBounds();
        if (endRect.width() <= 0 || endRect.height() <= 0) {
            int padding = mEndSeekSelectDrawable.getIntrinsicWidth();
            mEndSeekRect = new Rect(0, 0, mEndSeekSelectDrawable.getIntrinsicWidth(), h);
            int selectW = (int) (mEndSeekSelectDrawable.getIntrinsicWidth() * TIME_TOUCH_SCALE);
            int selectH = (int) (h * TIME_TOUCH_SCALE);
            mEndSeekSelectRect = new Rect(0, 0, selectW, selectH);
            mEndSeekSelectDrawable.setBounds(mEndSeekRect);

            Rect rect = mEndSeekSelectDrawable.getBounds();
            endSeekBarLocation = getWidth() - (rect.width() * 0.8f);
        }


        Rect normalRect = mStartSeekSelectDrawable.getBounds();
        if (normalRect.width() <= 0 || normalRect.height() <= 0) {
            int padding = mStartSeekSelectDrawable.getIntrinsicWidth();
            mStartSeekRect = new Rect(-padding / 5, 0, mStartSeekSelectDrawable.getIntrinsicWidth(), h);
            int selectW = (int) (mStartSeekSelectDrawable.getIntrinsicWidth() * TOUCH_SCALE);
            int selectH = (int) (h * TOUCH_SCALE);
            mSeekSelectRect = new Rect(-padding / 5, 0, selectW, selectH);
            mStartSeekSelectDrawable.setBounds(mStartSeekRect);
        }


    }

    private void updateClipPath(int w, int h) {
        clipPath.reset();
        clipPath.addRoundRect(new RectF(0, bgPaddingTop, w, h - bgPaddingBottom), mCornerRadius, mCornerRadius,
                Path.Direction.CW);
        viewClipHelper.updateClipPath(clipPath);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureDimension(DEFAULT_VIEW_WIDTH, widthMeasureSpec);
        int height = measureDimension(DEFAULT_VIEW_HEIGHT, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureDimension(int defaultSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.AT_MOST:
                result = specSize;
                break;
            default:
                result = defaultSize;
                break;
        }
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (Math.abs(event.getX() - startSeekBarLocation) < UIUtils.getPixels(15)) {
                    mStartSeekSelectDrawable.setBounds(mSeekSelectRect);
                    onTouchStartSeekBar = true;
                } else if (Math.abs(event.getX() - endSeekBarLocation) < UIUtils.getPixels(15)) {
                    mEndSeekSelectDrawable.setBounds(mEndSeekSelectRect);
                    onTouchEndSeekBar = true;
                } else if (Math.abs(event.getX() - progressBarLocation) < UIUtils.getPixels(20)) {
                    onTouchProgressSeekBar = true;
                } else {
                    onTouchStartSeekBar = false;
                    onTouchEndSeekBar = false;
                    onTouchProgressSeekBar = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (onTouchStartSeekBar) {
                    if (startSeekBarLocation == event.getX()) {
                        return true;
                    }
                    startSeekBarLocation = event.getX();
                    if (startSeekBarLocation > endSeekBarLocation - mMinInterval) {
                        startSeekBarLocation = endSeekBarLocation - mMinInterval;
                    }
                    //避免超出
                    if (startSeekBarLocation >= getWidth()) {
                        startSeekBarLocation = getWidth();
                    }

                    if (startSeekBarLocation < 0) {
                        startSeekBarLocation = 0;
                    }
                    invalidate();
                } else if (onTouchEndSeekBar) {
                    if (endSeekBarLocation == event.getX()) {
                        return true;
                    }
                    endSeekBarLocation = event.getX();
                    if (startSeekBarLocation > endSeekBarLocation - mMinInterval) {
                        endSeekBarLocation = startSeekBarLocation + mMinInterval;
                    }

                    Rect rect = mEndSeekSelectDrawable.getBounds();
                    //避免超出
                    if (endSeekBarLocation + rect.width() >= getWidth()) {
                        endSeekBarLocation = getWidth() - (rect.width() * 0.8f);
                    }
                    if (endSeekBarLocation < 0) {
                        endSeekBarLocation = 0;
                    }
                    invalidate();
                } else if (onTouchProgressSeekBar) {
                    if (progressBarLocation == event.getX()) {
                        return true;
                    }
                    progressBarLocation = event.getX();
                    if (progressBarLocation < startSeekBarLocation + mStartSeekRect.width() / 2) {
                        progressBarLocation = startSeekBarLocation + mStartSeekRect.width() / 2;
                    } else if (progressBarLocation > endSeekBarLocation) {
                        progressBarLocation = endSeekBarLocation;
                    }
                    if (startSeekBarLocation >= getWidth()) {
                        startSeekBarLocation = getWidth();
                    } else if (startSeekBarLocation < 0) {
                        startSeekBarLocation = 0;
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (onTouchStartSeekBar) {
                    if (startSeekListener != null) {
                        startSeekListener.onSeekFinish(startSeekBarLocation / getWidth());
                    }
                    mStartSeekSelectDrawable.setBounds(mStartSeekRect);
                    invalidate();
                } else if (onTouchEndSeekBar) {
                    if (endFilterSeekListener != null) {
                        endFilterSeekListener.onSeekFinish(endSeekBarLocation / getWidth());
                    }
                    mEndSeekSelectDrawable.setBounds(mEndSeekRect);
                    invalidate();
                } else if (onTouchProgressSeekBar) {
                    if (progressSeekListener != null) {
                        progressSeekListener.onSeekFinish(progressBarLocation / (getWidth() - mStartSeekRect.width() / 2 - mEndSeekRect.width()));
                    }
                    invalidate();
                }
                onTouchStartSeekBar = false;
                onTouchEndSeekBar = false;
                onTouchProgressSeekBar = false;
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (totleTime <= 0L) {
            return;
        }

        drawContent(canvas);

        canvas.drawLine(progressBarLocation, 0, progressBarLocation, getHeight(), mProgressSeekPaint);

        drawStartBar(canvas);
        drawEndSeekBar(canvas);
    }


    private void drawEndSeekBar(Canvas canvas) {

        canvas.save();
        Rect rect = mEndSeekSelectDrawable.getBounds();
        canvas.translate(endSeekBarLocation, (getHeight() - rect.height()) / 2);
        mEndSeekSelectDrawable.draw(canvas);
        canvas.restore();
    }


    private void drawContent(Canvas canvas) {
        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(),
                null, Canvas.ALL_SAVE_FLAG);

        bgRectf.set(startSeekBarLocation, bgPaddingTop, endSeekBarLocation + mEndSeekRect.width() / 2, getHeight() - bgPaddingBottom);
        bgPaint.setColor(UIUtils.getColor(R.color.filter_shake));
        canvas.drawRect(bgRectf, bgPaint);

        canvas.drawPath(clipPath, clipPaint);
        canvas.restoreToCount(layerId);
    }


    private void drawStartBar(Canvas canvas) {
        canvas.save();
        Rect rect = mStartSeekSelectDrawable.getBounds();
        canvas.translate(startSeekBarLocation, (getHeight() - rect.height()) / 2);
        mStartSeekSelectDrawable.draw(canvas);
        canvas.restore();
    }


    public void initLength(long totleTime) {
        this.totleTime = totleTime;
        updateStartLocation(0f);
    }

    /**
     * 更新进度条
     *
     * @param progress 百分比
     */
    public void updateStartLocation(float progress) {
        if (onTouchStartSeekBar) {
            // 正在seek时  不处理进度更新
            return;
        }
        if (progress < 0 || progress > 1 || getWidth() <= 0) {
            return;
        }
        float temp = progress * getWidth();
        if (temp == startSeekBarLocation) {
            return;
        }
        startSeekBarLocation = temp;
        postInvalidate();
    }

    public void updateProgressLocation(float progress) {
        if (onTouchProgressSeekBar) {
            // 正在seek时  不处理进度更新
            return;
        }
        if (progress < 0 || progress > 1 || getWidth() <= 0) {
            return;
        }
        float temp = progress * getWidth();
        if (temp == progressBarLocation) {
            return;
        }
        progressBarLocation = temp;
        if (progressBarLocation < startSeekBarLocation + mStartSeekRect.width() / 2) {
            progressBarLocation = startSeekBarLocation + mStartSeekRect.width() / 2;
        } else if (progressBarLocation > endSeekBarLocation) {
            progressBarLocation = endSeekBarLocation;
        }
        postInvalidate();
    }

    public void updateEndTimeLocation(float progress) {
        if (progress < 0 || progress > 1 || getWidth() <= 0) {
            return;
        }
        Rect rect = mEndSeekSelectDrawable.getBounds();
        endSeekBarLocation = progress * (getWidth() - (rect.width() * 0.8f));

        postInvalidate();
    }

    public void setStartSeekListener(OnSeekListener startSeekListener) {
        this.startSeekListener = startSeekListener;
    }

    public void setEndFilterSeekListener(OnSeekListener endFilterSeekListener) {
        this.endFilterSeekListener = endFilterSeekListener;
    }

    public void setProgressSeekListener(OnSeekListener progressSeekListener) {
        this.progressSeekListener = progressSeekListener;
    }


    public interface OnSeekListener {
        void onSeekFinish(float progress);
    }

}
