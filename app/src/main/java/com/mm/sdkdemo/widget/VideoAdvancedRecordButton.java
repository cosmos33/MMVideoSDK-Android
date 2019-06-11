package com.mm.sdkdemo.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.utils.RecordButtonTouchEventHelper;

/**
 * Created by XiongFangyu on 2017/6/6.
 * <p>
 * 高级录制按钮
 */
public class VideoAdvancedRecordButton extends View implements ValueAnimator.AnimatorUpdateListener, RecordButtonTouchEventHelper.LongPressCallback {

    private ShaderCircleDrawable shaderCircleDrawable;
    private ValueAnimator progressAnim;
    //    private ValueAnimator switchAnim;
    private ValueAnimator scaleAnim;

    private int maxSize;
    private int minSize;
    private int ringMinSize;
    private int ringStartSize = UIUtils.getPixels(64);
    private long switchDuration = 1000;
    private long scaleDuration = 1000;
    private int initRingColor = Color.WHITE;
    private int backColor = Color.WHITE;
    private int fps = 60;
    private int progressSpeed = 1;
    private float minRingScale = 0.9f;
    private float maxRingScale = 1.1f;

    private int centerX, centerY;
    private Paint backPaint;

    private int drawBackSize;
    private float shaderScale = 1;
    private float shaderStartScale;
    private float shaderMinScale;

    private Callback callback;
    private RecordButtonTouchEventHelper touchEventHelper;

    public VideoAdvancedRecordButton(Context context) {
        this(context, null);
    }

    public VideoAdvancedRecordButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoAdvancedRecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoAdvancedRecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }


    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        shaderCircleDrawable = new ShaderCircleDrawable(context, attrs, defStyleAttr, defStyleRes);
        shaderCircleDrawable.setCallback(this);
        backPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shaderCircleDrawable.setScale(1f);
        touchEventHelper = new RecordButtonTouchEventHelper();
        touchEventHelper.setLongPressCallback(this);

        if (context == null || attrs == null)
            return;

        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs,
                R.styleable.VideoAdvancedRecordButton, defStyleAttr, defStyleRes);
        TypedArray appearance = null;
        int ap = a.getResourceId(
                R.styleable.VideoAdvancedRecordButton_varb_style, -1);
        if (ap != -1) {
            appearance = theme.obtainStyledAttributes(
                    ap, R.styleable.VideoAdvancedRecordButton);
        }
        initStyle(appearance);
        initStyle(a);
    }

    private void initStyle(TypedArray a) {
        if (a != null) {
            setBackColor(a.getColor(R.styleable.VideoAdvancedRecordButton_varb_back_color, backPaint.getColor()));
            setMaxSize(a.getDimensionPixelSize(R.styleable.VideoAdvancedRecordButton_varb_max_size, maxSize));
            setMinSize(a.getDimensionPixelSize(R.styleable.VideoAdvancedRecordButton_varb_min_size, minSize));
            setRingMinSize(a.getDimensionPixelSize(R.styleable.VideoAdvancedRecordButton_varb_ring_min_size, ringMinSize));
            setSwitchDuration(a.getInt(R.styleable.VideoAdvancedRecordButton_varb_switch_duration, (int) switchDuration));
            setScaleDuration(a.getInt(R.styleable.VideoAdvancedRecordButton_varb_scale_duration, (int) scaleDuration));
            setInitRingColor(a.getColor(R.styleable.VideoAdvancedRecordButton_varb_init_ring_color, initRingColor));
            setProgressSpeed(a.getInt(R.styleable.VideoAdvancedRecordButton_varb_progress_speed, progressSpeed));
            setMinRingScale(a.getFloat(R.styleable.VideoAdvancedRecordButton_varb_min_ring_scale, minRingScale));
            setMaxRingScale(a.getFloat(R.styleable.VideoAdvancedRecordButton_varb_max_ring_scale, maxRingScale));
            a.recycle();
        }
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        if (drawable == shaderCircleDrawable) {
            invalidate();
            return;
        }
        super.invalidateDrawable(drawable);
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

        int rw = ws, rh = hs;
        switch (wm) {
            case MeasureSpec.EXACTLY:
                rw = ws;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                rw = maxSize + pl + pr;
                break;
        }
        switch (hm) {
            case MeasureSpec.EXACTLY:
                rh = hs;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                rh = maxSize + pt + pb;
                break;
        }
        setMeasuredDimension(rw, rh);

        centerX = (maxSize >> 1) + pl;
        centerY = (maxSize >> 1) + pt;

        shaderCircleDrawable.setBounds(pl, pt, pl + maxSize, pt + maxSize);
        touchEventHelper.setBackRect(shaderCircleDrawable.getBounds());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        shaderScale = shaderMinScale;
        drawBackSize = minSize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.drawCircle(centerX, centerY, drawBackSize >> 1, backPaint);
        canvas.scale(shaderScale, shaderScale, centerX, centerY);
        shaderCircleDrawable.setRingScaleWithoutScaleWidth(shaderScale);
        shaderCircleDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (!isEnabled())
            return false;
        return touchEventHelper.onTouchEvent(e);
    }

    @Override
    public void onLongPressUp() {
        stopRecord();
    }

    public void stopRecord() {
        if (scaleAnim != null && scaleAnim.isRunning())
            scaleAnim.cancel();
        initScaleAnim(false);
        scaleAnim.start();
        callbackStopRecord();
    }

    private void callbackStopRecord() {
        if (callback != null)
            callback.onStopRecording();
    }

    public void reset(boolean anim) {
        if (scaleAnim != null && scaleAnim.isRunning())
            scaleAnim.cancel();
        if (anim) {
            initScaleAnim(false);
            scaleAnim.start();
        } else {
            onScaleAnim(shaderMinScale, true);
        }
    }

    public void startAnimToRecord() {
        if (scaleAnim != null && scaleAnim.isRunning())
            scaleAnim.cancel();
        initScaleAnim(true);
        scaleAnim.start();
    }

    private void initScaleAnim(boolean toBigger) {
        if (scaleAnim == null) {
            scaleAnim = new ValueAnimator().setDuration(scaleDuration);
            scaleAnim.addUpdateListener(this);
            scaleAnim.setInterpolator(null);
            scaleAnim.addListener(new Animator.AnimatorListener() {
                boolean fromCancel = false;

                @Override
                public void onAnimationStart(Animator animation) {
                    fromCancel = false;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (fromCancel)
                        return;
                    final float endValue = (float) scaleAnim.getAnimatedValue();
                    //end 为1
                    if (Math.abs(endValue - 1) < endValue) {
                        if (callback != null)
                            callback.onStartRecording();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    fromCancel = true;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        if (toBigger) {
            scaleAnim.setFloatValues(shaderMinScale, 1);
        } else {
            scaleAnim.setFloatValues(1, shaderMinScale);
        }
    }

    private void onScaleAnim(float value, boolean autoAnim) {
        shaderScale = value;
        if (autoAnim) {
            final float percent = (value - shaderMinScale) / (1 - shaderMinScale);
            drawBackSize = (int) (minSize + percent * (maxSize - minSize));
            backPaint.setColor(backColor);
        } else {
            final float delta = shaderStartScale - shaderMinScale;
            final float fraction = 1 - (value - shaderMinScale) / delta;
            drawBackSize = (int) (ringStartSize + (minSize - ringStartSize) * fraction);
            final int alpha = Color.alpha(backColor);
            final int rgb = backColor & 0xFFFFFF;
            backPaint.setColor((int) (alpha * fraction) << 24 | rgb);
        }
        invalidate();
    }

    public void switchToAdvanced() {
        onSwitchAnim(1);
        shaderCircleDrawable.setRingToShader();
        startProgress();
    }

    public void switchFromAdvanced() {
        onSwitchAnim(0f);
        shaderCircleDrawable.setRingColor(initRingColor);
        cancelProgressAnim();
        if (callback != null) {
            callback.onSwitchAnimEnd();
        }
    }

    public void onSwitchAnim(float value) {
        final float deltaScale = shaderStartScale - shaderMinScale;
        shaderCircleDrawable.setScale(value);
        onScaleAnim(shaderStartScale - deltaScale * value, false);
    }

    private void startProgress() {
        cancelProgressAnim();
        initProgressAnim();
        progressAnim.start();
    }

    private void cancelProgressAnim() {
        if (progressAnim != null && progressAnim.isRunning())
            progressAnim.cancel();
    }

    private void initProgressAnim() {
        if (progressAnim == null) {
            progressAnim = ValueAnimator.ofFloat(0, 1).setDuration(8000);
            progressAnim.addUpdateListener(this);
            progressAnim.setInterpolator(null);
            progressAnim.setRepeatCount(ValueAnimator.INFINITE);
            progressAnim.setRepeatMode(ValueAnimator.RESTART);
        }
    }

    private void onProgressAnim(float value) {
        final int endWidth = shaderCircleDrawable.getShaderEndWidth();
        final float tx = value * endWidth * progressSpeed;
        shaderCircleDrawable.setShaderTranslate(tx);
        //圆环最大时，圆环会间歇性大小变换
        if (shaderScale == 1) {
            float percent = Math.abs(shaderCircleDrawable.getShaderTranslate() / endWidth);
            percent = Math.abs(1 - 2 * percent);
            float s = percent * (maxRingScale - minRingScale) + minRingScale;
            shaderCircleDrawable.setRingWidthScale(s);
        } else {
            shaderCircleDrawable.setRingWidthScale(1);
        }
    }

    public void releaseAnim() {
        if (progressAnim != null) {
            if (progressAnim.isRunning())
                progressAnim.cancel();
            progressAnim.removeAllUpdateListeners();
            progressAnim.removeAllListeners();
        }
        progressAnim = null;
        if (scaleAnim != null) {
            if (scaleAnim.isRunning())
                scaleAnim.cancel();
            scaleAnim.removeAllUpdateListeners();
            scaleAnim.removeAllListeners();
        }
        scaleAnim = null;
    }

    public void release() {
        releaseAnim();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        final float value = (float) animation.getAnimatedValue();
        if (animation == progressAnim) {
            onProgressAnim(value);
        } else if (animation == scaleAnim) {
            onScaleAnim(value, true);
        }
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
        touchEventHelper.setCallback(callback);
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
        backPaint.setColor(backColor);
        shaderCircleDrawable.setInnerColor(backColor);
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        caculateShaderMinScale();
        calculateShaderStartScale();
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    public void setRingMinSize(int ringMinSize) {
        this.ringMinSize = ringMinSize;
        caculateShaderMinScale();
    }

    public void setSwitchDuration(long switchDuration) {
        this.switchDuration = switchDuration;
    }

    public void setScaleDuration(long scaleDuration) {
        this.scaleDuration = scaleDuration;
    }

    public void setInitRingColor(int initRingColor) {
        this.initRingColor = initRingColor;
        shaderCircleDrawable.setRingColor(initRingColor);
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public void setProgressSpeed(int progressSpeed) {
        this.progressSpeed = progressSpeed;
    }

    public void setMinRingScale(float minRingScale) {
        this.minRingScale = minRingScale;
    }

    public void setMaxRingScale(float maxRingScale) {
        this.maxRingScale = maxRingScale;
    }

    public void setCanLongPress(boolean canLongPress) {
        touchEventHelper.setCanLongPress(canLongPress);
    }

    public boolean canLongPress() {
        return touchEventHelper.isCanLongPress();
    }

    public void setTouchBack(boolean touchBack) {
        touchEventHelper.setTouchBack(touchBack);
    }

    private void caculateShaderMinScale() {
        if (maxSize == 0)
            return;
        shaderMinScale = ringMinSize / (float) maxSize;
    }

    private void calculateShaderStartScale() {
        if (maxSize == 0)
            return;
        shaderStartScale = ringStartSize / (float) maxSize;
    }

    public Callback getCallback() {
        return callback;
    }

    public interface Callback extends RecordButtonTouchEventHelper.Callback {
        void onSwitchAnimEnd();

        void onStartRecording();

        void onStopRecording();
    }
}
