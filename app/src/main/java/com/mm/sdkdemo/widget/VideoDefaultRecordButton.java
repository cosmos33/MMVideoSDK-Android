package com.mm.sdkdemo.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mm.mmutil.task.MomoMainThreadExecutor;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.utils.RecordButtonTouchEventHelper;

/**
 * Created by XiongFangyu on 2017/6/5.
 *
 * 普通录制按钮
 */
public class VideoDefaultRecordButton extends View implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener, RecordButtonTouchEventHelper.LongPressCallback {
    private static final int[] colors = new int[]{
            0xffffbf00,
            0xffe2305c,
            0xff00aeff,
            0xff9e1dff,
            0xffffb800
    };

    public static final int BACK_COLOR_DARK = 0x33ffffff;
    public static final int BACK_COLOR_LIGHT = 0xffffffff;

    private ProgressDrawable progressDrawable;
    private boolean drawProgress = false;
    private ValueAnimator progressAnim;
    private ValueAnimator recordAnim;
    private int fps = 60;
    private long recordDuration = 400;
    private Rect backRect;
    private Rect drawRect;
    private Paint mPaint;
    private int innerAlpha = 255;
    private int circleWidth = 4;

    private int backColor = BACK_COLOR_LIGHT;

    private int innerColor = Color.WHITE;

    private int maxSize;
    private int minSize;
    private int progressSize;
    private int innerSize;

    private boolean recordAnimCancel = false;
    private boolean progressAnimCancel = false;

    private Callback callback;
    private RecordButtonTouchEventHelper touchEventHelper;
    private android.animation.ValueAnimator mScaleAnim;

    public VideoDefaultRecordButton(Context context) {
        this(context, null);
    }

    public VideoDefaultRecordButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoDefaultRecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoDefaultRecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        progressDrawable = new ProgressDrawable(context, attrs, defStyleAttr, defStyleRes);
        progressDrawable.setCallback(this);
        backRect = new Rect();
        drawRect = new Rect();
        touchEventHelper = new RecordButtonTouchEventHelper();
        touchEventHelper.setLongPressCallback(this);

        if (context == null || attrs == null)
            return;

        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs,
                                                    R.styleable.VideoDefaultRecordButton, defStyleAttr, defStyleRes);
        TypedArray appearance = null;
        int ap = a.getResourceId(
                R.styleable.VideoDefaultRecordButton_vdrb_style, -1);
        if (ap != -1) {
            appearance = theme.obtainStyledAttributes(
                    ap, R.styleable.VideoDefaultRecordButton);
        }
        initStyle(appearance);
        initStyle(a);
    }

    private void initStyle(TypedArray a) {
        if (a != null) {
            maxSize = a.getDimensionPixelSize(R.styleable.VideoDefaultRecordButton_vdrb_max_size, maxSize);
            minSize = a.getDimensionPixelSize(R.styleable.VideoDefaultRecordButton_vdrb_min_size, minSize);
            innerSize = a.getDimensionPixelSize(R.styleable.VideoDefaultRecordButton_vdrb_inner_size, innerSize);
            backColor = a.getColor(R.styleable.VideoDefaultRecordButton_vdrb_back_color, backColor);
            progressSize = a.getDimensionPixelSize(R.styleable.VideoDefaultRecordButton_vdrb_progress_size, progressSize);
            innerColor = a.getColor(R.styleable.VideoDefaultRecordButton_vdrb_inner_color, innerColor);
            recordDuration = a.getInt(R.styleable.VideoDefaultRecordButton_vdrb_record_duration, (int) recordDuration);
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int wm = MeasureSpec.getMode(widthMeasureSpec);
        final int hm = MeasureSpec.getMode(heightMeasureSpec);
        final int ws = MeasureSpec.getSize(widthMeasureSpec);
        final int hs = MeasureSpec.getSize(heightMeasureSpec);

        final int pl = getPaddingLeft();
        final int pt = getPaddingTop();
        final int pr = getPaddingRight();
        final int pb = getPaddingBottom();

        final int max = Math.max(maxSize, progressSize);
        int resultWidth = ws;
        int resultHeight = hs;
        switch (wm) {
            case MeasureSpec.AT_MOST:
                resultWidth = Math.min(ws, max + pl + pr);
                break;
            default:
                resultWidth = ws;
                break;
        }
        switch (hm) {
            case MeasureSpec.AT_MOST:
                resultHeight = Math.min(hs, max + pt + pb);
                break;
            default:
                resultHeight = hs;
                break;
        }
        setMeasuredDimension(resultWidth, resultHeight);

        backRect.set(pl, pt, pl + max, pt + max);
        drawRect.set(backRect);
        final int o = (max - maxSize) >> 1;
        drawRect.inset(o, o);

        final int offset = (max - progressSize) >> 1;
        progressDrawable.setBounds(backRect);
        Rect rect = progressDrawable.getBounds();
        rect.inset(offset, offset);

        //只能这么创建
        @SuppressLint("DrawPerformanceDetector")
        Shader shader = new SweepGradient(rect.centerX(), rect.centerY(), colors, null);
        progressDrawable.setProgressShader(shader);

        touchEventHelper.setBackRect(backRect);
    }

    private void resetRect() {
        final int max = Math.max(maxSize, progressSize);
        drawRect.set(backRect);
        final int o = (max - maxSize) >> 1;
        drawRect.inset(o, o);
    }

    @Override
    public void invalidateDrawable(Drawable d) {
        if (d == progressDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(d);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (backRect.width() <= 0)
            return;
        canvas.save();
        mPaint.setAlpha(255);
        mPaint.setColor(backColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(UIUtils.getPixels(circleWidth));
        final int size = drawRect.width() * innerSize / backRect.width();
        canvas.drawCircle(drawRect.centerX(), drawRect.centerY(), size >> 1, mPaint);
        if (drawProgress)
            progressDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        if (!isEnabled())
            return false;
        return touchEventHelper.onTouchEvent(me);
    }

    @Override
    public void onLongPressUp() {
        if (recordAnim != null && recordAnim.isRunning()) {
            recordAnim.cancel();
            // 为了在onAnimatorCancel之后执行，复位动画
        }
        MomoMainThreadExecutor.post(new Runnable() {
            @Override
            public void run() {
                resetRect();
                invalidate();
            }
        });
        if (isRunning()) {
            progressAnim.pause();
            callbackAnimEnd();
        }
    }

    public void reset() {
        releaseAnim();
        resetRect();
        big2Small();
        innerAlpha = 255;
        drawProgress = false;
        progressDrawable.setProgress(0);
    }

    public void setProgress(float progress) {
        progressDrawable.setProgress(progress);
        if (callback != null) {
            callback.onProgress(progress);
        }
    }

    public float getProgress() {
        return progressDrawable.getProgress();
    }

    public void setProgress(float progress, long time) {
        cancel();
        initAnim();
        progressAnim.setFloatValues(getProgress(), progress);
        progressAnim.setDuration(time);
        progressAnim.start();
    }

    public void small2Big() {
        startScaleAnim(1.15f, BACK_COLOR_DARK);
    }

    public void big2Small() {
        startScaleAnim(1f, BACK_COLOR_LIGHT);
    }

    private void startScaleAnim(float endScale, int endColor) {
        if (mScaleAnim != null) {
            mScaleAnim.cancel();
        }

        final float startScale = Math.max(getScaleX(), getScaleY());
        final float deltaScale = endScale - Math.max(getScaleX(), getScaleY());

        final int startA = Color.alpha(backColor);
        final int startRGB = backColor & 0xFFFFFF;
        final int deltaA = Color.alpha(endColor) - startA;

        mScaleAnim = android.animation.ValueAnimator.ofFloat(0f, 1f);
        mScaleAnim.setDuration(150);
        mScaleAnim.addUpdateListener(new android.animation.ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(android.animation.ValueAnimator animation) {
                final float value = (float) animation.getAnimatedValue();

                backColor = (int) (startA + deltaA * value) << 24 | startRGB;
                invalidate();

                final float scale = startScale + deltaScale * value;
                setScaleX(scale);
                setScaleY(scale);
            }
        });
        mScaleAnim.start();
    }

    public void cancel() {
        if (isRunning())
            progressAnim.cancel();
    }

    public boolean isRunning() {
        return progressAnim != null && progressAnim.isRunning();
    }

    public void pause() {
        if (isRunning())
            progressAnim.pause();
    }

    public void release() {
        cancel();
        releaseAnim();
        callback = null;
        touchEventHelper.release();
    }

    public void releaseAnim() {
        if (progressAnim != null) {
            progressAnim.removeAllUpdateListeners();
            progressAnim.removeAllListeners();
        }
        progressAnim = null;
        if (recordAnim != null) {
            recordAnim.removeAllUpdateListeners();
            recordAnim.removeAllListeners();
        }
        recordAnim = null;
    }

    public void startAnimToRecord() {
        if (recordAnim == null) {
            recordAnim = ValueAnimator.ofFloat(0, 2).setDuration(recordDuration);
            recordAnim.addUpdateListener(this);
            recordAnim.setInterpolator(null);
            recordAnim.addListener(this);
        }
        recordAnim.start();
    }

    public void setInnerAlpha(int alpha) {
        this.innerAlpha = alpha;
        invalidate();
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    private void onRecordAnimEnd() {
        drawProgress = true;
        if (callback != null) {
            callback.onRecordAnimEnd();
        }
    }

    private void callbackAnimEnd() {
        if (callback != null) {
            callback.onProgressEnd();
        }
    }

    private void initAnim() {
        if (progressAnim == null) {
            progressAnim = ValueAnimator.ofFloat(0, 1);
            progressAnim.addUpdateListener(this);
            progressAnim.setInterpolator(null);
            progressAnim.addListener(this);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        final float value = (float) animation.getAnimatedValue();
        if (animation == progressAnim) {
            setProgress(value);
        } else if (recordAnim == animation) {
            float percent = 0;
            if (value <= 1) {
                percent = value;
                final int offset = (int) ((maxSize - minSize) * percent) >> 1;
                resetRect();
                drawRect.inset(offset, offset);
                innerAlpha = (int) (255 - percent * 255);
                invalidate();
            }

            if (value > 1 && value <= 2) {
                innerAlpha = 0;
                percent = value - 1;
                final int offset = (int) (maxSize - minSize + ((minSize - progressSize) * percent)) >> 1;
                resetRect();
                drawRect.inset(offset, offset);
                invalidate();
            }
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if (animation == recordAnim) {
            recordAnimCancel = false;
        } else if (animation == progressAnim) {
            progressAnimCancel = false;
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (animation == recordAnim) {
            if (!recordAnimCancel)
                onRecordAnimEnd();
        } else if (animation == progressAnim) {
            if (!progressAnimCancel)
                callbackAnimEnd();
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        if (animation == recordAnim) {
            recordAnimCancel = true;
        } else if (animation == progressAnim) {
            progressAnimCancel = true;
        }
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public void setCallback(Callback callback) {
        this.callback = callback;
        touchEventHelper.setCallback(callback);
    }

    public void setCanLongPress(boolean can) {
        touchEventHelper.setCanLongPress(can);
    }

    public interface Callback extends RecordButtonTouchEventHelper.Callback {

        void onRecordAnimEnd();

        void onProgress(float progress);

        void onProgressEnd();
    }
}
