package com.mm.recorduisdk.recorder.musicpanel.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.mm.recorduisdk.R;

/**
 * 音量调节控件
 * properties:
 * leftColor
 * rightColor
 * thumbDrawable
 * <p>
 * <p>
 * Created by tangyuchun on 2018/5/10.
 */

public class VolumeSeekBar extends View {

    public VolumeSeekBar(Context context) {
        super(context);
        init(context, null);
    }

    public VolumeSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public VolumeSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VolumeSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private int startColor = 0xff00ffcf;
    private int endColor = 0xff0099ff;

    /**
     * 50%时背景高度
     */
    private int BG_HEIGHT_MIN = 15;
    private int BG_HEIGHT_MAX = 60;

    private Paint bgPaint;
    private Path bgPath;
    private float thumbDrawableStartX = 0;
    private Drawable thumbDrawable;

    private OnVolumeSeekListener onVolumeSeekListener;

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VolumeSeekBar);
            startColor = typedArray.getColor(R.styleable.VolumeSeekBar_vsb_startColor, startColor);
            endColor = typedArray.getColor(R.styleable.VolumeSeekBar_vsb_endColor, endColor);
            thumbDrawable = typedArray.getDrawable(R.styleable.VolumeSeekBar_vsb_thumbDrawable);
            BG_HEIGHT_MIN = typedArray.getDimensionPixelSize(R.styleable.VolumeSeekBar_vsb_bgMinHeight, 20);
            BG_HEIGHT_MAX = typedArray.getDimensionPixelSize(R.styleable.VolumeSeekBar_vsb_bgMaxHeight, 40);

            typedArray.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBg(canvas);
        drawThumb(canvas);
    }

    private void drawBg(Canvas canvas) {
        if (bgPaint == null) {
            bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bgPaint.setShader(new LinearGradient(0f, 0f, getWidth(), getHeight(), startColor, endColor, Shader.TileMode.CLAMP));
        }
        //calculate minHeight maxHeight
        float percent = 1 - getCurrentPercent();
        int leftHeight = (int) (BG_HEIGHT_MIN + (BG_HEIGHT_MAX - BG_HEIGHT_MIN) * percent);
        int rightHeight = (int) (BG_HEIGHT_MAX - (BG_HEIGHT_MAX - BG_HEIGHT_MIN) * percent);


        int leftRadius = leftHeight / 2;
        int rightRadius = rightHeight / 2;
        if (bgPath == null) {
            bgPath = new Path();
        }
        bgPath.reset();
        int left = leftRadius;
        int right = getWidth() - rightRadius;

        int height = getHeight();

        int leftMarginVertical = (height - leftHeight) / 2;
        int rightMarginVertical = (height - rightHeight) / 2;

        bgPath.moveTo(left, height - leftHeight - leftMarginVertical);
        bgPath.lineTo(right, rightMarginVertical);
        bgPath.lineTo(right, height - rightMarginVertical);
        bgPath.lineTo(left, height - leftMarginVertical);

        bgPath.addCircle(left, height - leftMarginVertical - leftRadius, leftRadius, Path.Direction.CW);
        bgPath.addCircle(right, height - rightMarginVertical - rightRadius, rightRadius, Path.Direction.CW);

        canvas.drawPath(bgPath, bgPaint);
    }

    private void drawThumb(Canvas canvas) {
        if (thumbDrawable != null) {
            Rect rect = thumbDrawable.getBounds();
            //避免超出
            if (thumbDrawableStartX >= getWidth() - rect.width()) {
                thumbDrawableStartX = getWidth() - rect.width();
            }
            canvas.translate(thumbDrawableStartX, (getHeight() - rect.height()) / 2);
            thumbDrawable.draw(canvas);
        }
    }

    private float getCurrentPercent() {
        if (thumbDrawable == null) {
            return 0f;
        }
        if (thumbDrawableStartX + thumbDrawable.getBounds().width() >= getWidth()) {
            return 1f;
        }
        return thumbDrawableStartX / getWidth();
    }

    private void reset() {
        if (thumbDrawable != null) {
            Rect rect = thumbDrawable.getBounds();
            if (rect.width() <= 0 || rect.height() <= 0) {
                int height = BG_HEIGHT_MAX < 0 ? getHeight() : BG_HEIGHT_MAX + 20;
                Rect bounds = new Rect(0, 0, (int) (height * 0.75), height);
                thumbDrawable.setBounds(bounds);
            }
        }
        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        ViewParent parent = getParent();
        if (parent == null) {
            return super.dispatchTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                parent.requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                parent.requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        super.onTouchEvent(event);
        if (event != null && event.getAction() == MotionEvent.ACTION_MOVE && thumbDrawable != null) {
            int thumbWidth = thumbDrawable.getBounds().width();

            thumbDrawableStartX = event.getX();
            if (thumbDrawableStartX < 0) {
                thumbDrawableStartX = 0;
            }
            if (thumbDrawableStartX > getWidth() - thumbWidth) {
                thumbDrawableStartX = getWidth() - thumbWidth;
            }
            invalidate();
            if (onVolumeSeekListener != null) {
                onVolumeSeekListener.onSeekChanged(100 * getCurrentPercent());
            }
        }
        return true;
    }

    private int initProgress = 0;

    /**
     * 设置当前值
     *
     * @param progress
     */
    public void setCurrentProgress(@IntRange(from = 0, to = 100) int progress) {
        if (progress < 0) {
            progress = 0;
        }
        if (progress > 100) {
            progress = 100;
        }
        initProgress = progress;
        thumbDrawableStartX = (((float) progress) / 100f) * getWidth();
        if (thumbDrawable != null) {
            int thumbWidth = thumbDrawable.getBounds().width();
            if (thumbDrawableStartX - thumbWidth >= getWidth()) {
                thumbDrawableStartX = getWidth() - thumbWidth;
            }
        }
        invalidate();
        if (onVolumeSeekListener != null) {
            onVolumeSeekListener.onSeekChanged(progress);
        }
    }

    public int getCurrentProgress() {
        return (int) (100 * getCurrentPercent());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (initProgress > 0) {
            setCurrentProgress(initProgress);
            initProgress = 0;
        }
        reset();
    }

    public void setOnVolumeSeekListener(OnVolumeSeekListener onVolumeSeekListener) {
        this.onVolumeSeekListener = onVolumeSeekListener;
    }

    public interface OnVolumeSeekListener {
        void onSeekChanged(@FloatRange(from = 0f, to = 100f) float percent);
    }
}
