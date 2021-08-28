package com.mm.recorduisdk.recorder.specialfilter.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.recorder.specialfilter.bean.FrameFilter;
import com.mm.recorduisdk.utils.ViewClipHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class FilterSeekView extends View {

    //普通模式
    public static final int MODE_NORMAL = 1;
    // 有时间bar
    public static final int MODE_TIME = 2;

    @IntDef({MODE_NORMAL, MODE_TIME})
    @Retention(RetentionPolicy.SOURCE)
    @interface Mode {
    }


    private static final int DEFAULT_VIEW_WIDTH = UIUtils.getPixels(130);
    private static final int DEFAULT_VIEW_HEIGHT = UIUtils.getPixels(130);

    private static final float TOUCH_SCALE = 1.2f;
    private static final float TIME_TOUCH_SCALE = 1.1f;

    private final int bgPaddingTop = UIUtils.getPixels(5);
    private final int bgPaddingBottom = UIUtils.getPixels(5);

    private Paint bgPaint;
    private RectF bgRectf;

    private List<FrameFilter> frameFilters;

    private FrameFilter tempFilter;

    private long totleTime;

    // 普通进度条seek
    private float seekBarLocation;
    // 用于时间特效的seek
    private float timeSeekBarLocation;

    private boolean onTouchSeekbar = false;
    private boolean onTounchTimeBar = false;
    private SeekListener seekListener;
    private TimeFilterSeekListener timeFilterSeekListener;

    private ViewClipHelper viewClipHelper;

    private float mCornerRadius = UIUtils.getPixels(4);

    private final Path clipPath = new Path();
    private Paint clipPaint;

    private Drawable mTimeSeekSelectDrawable;
    private Rect mTimeSeekNormalRect;
    private Rect mTimeSeekSelectRect;


    private Drawable mNormalSeekSelectDrawable;
    private Rect mSeekNormalRect;
    private Rect mSeekSelectRect;

    @Mode
    private int model = MODE_NORMAL;

    // 是否是倒放模式
    private boolean timeBack;
    private boolean needShowTimeBackBg = false;
    private int timeBackBg = UIUtils.getColor(R.color.time_back_bg);


    public FilterSeekView(Context context) {
        super(context);
        init();
    }

    public FilterSeekView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FilterSeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FilterSeekView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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


        mTimeSeekSelectDrawable = UIUtils.getDrawable(R.drawable.ic_video_edit_time_seek_bar);

        mNormalSeekSelectDrawable = UIUtils.getDrawable(R.drawable.video_edit_select_seek_bar);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        updateClipPath(w, h);
        Rect timeRect = mTimeSeekSelectDrawable.getBounds();
        if (timeRect.width() <= 0 || timeRect.height() <= 0) {
            mTimeSeekNormalRect = new Rect(0, 0, mTimeSeekSelectDrawable.getIntrinsicWidth(), h);
            int selectW = (int) (mTimeSeekSelectDrawable.getIntrinsicWidth() * TIME_TOUCH_SCALE);
            int selectH = (int) (h * TIME_TOUCH_SCALE);
            mTimeSeekSelectRect = new Rect(0, 0, selectW, selectH);
            mTimeSeekSelectDrawable.setBounds(mTimeSeekNormalRect);
        }


        Rect normalRect = mNormalSeekSelectDrawable.getBounds();
        if (normalRect.width() <= 0 || normalRect.height() <= 0) {
            int padding = mTimeSeekSelectDrawable.getIntrinsicWidth() / 4;
            mSeekNormalRect = new Rect(-padding, 0, mNormalSeekSelectDrawable.getIntrinsicWidth() - padding, h);
            int selectW = (int) (mNormalSeekSelectDrawable.getIntrinsicWidth() * TOUCH_SCALE);
            int selectH = (int) (h * TOUCH_SCALE);
            mSeekSelectRect = new Rect(-padding, 0, selectW - padding, selectH);
            mNormalSeekSelectDrawable.setBounds(mSeekNormalRect);
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
                if (model == MODE_TIME && Math.abs(event.getX() - timeSeekBarLocation) < UIUtils.getPixels(20)) {
                    mTimeSeekSelectDrawable.setBounds(mTimeSeekSelectRect);
                    onTounchTimeBar = true;
                } else if (Math.abs(event.getX() - seekBarLocation) < UIUtils.getPixels(20)) {
                    mNormalSeekSelectDrawable.setBounds(mSeekSelectRect);
                    onTouchSeekbar = true;
                } else {
                    onTouchSeekbar = false;
                    onTounchTimeBar = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (onTouchSeekbar) {
                    if (seekBarLocation == event.getX()) {
                        return true;
                    }
                    seekBarLocation = event.getX();
                    //避免超出
                    if (seekBarLocation >= getWidth()) {
                        seekBarLocation = getWidth();
                    }

                    if (seekBarLocation < 0) {
                        seekBarLocation = 0;
                    }
                    invalidate();
                } else if (onTounchTimeBar) {
                    if (timeSeekBarLocation == event.getX()) {
                        return true;
                    }
                    timeSeekBarLocation = event.getX();
                    Rect rect = mTimeSeekSelectDrawable.getBounds();
                    //避免超出
                    if (timeSeekBarLocation + rect.width() >= getWidth()) {
                        timeSeekBarLocation = getWidth() - rect.width();
                    }
                    if (timeSeekBarLocation < 0) {
                        timeSeekBarLocation = 0;
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (onTouchSeekbar) {
                    if (seekListener != null) {
                        seekListener.onFinish(seekBarLocation / getWidth());
                    }
                    mNormalSeekSelectDrawable.setBounds(mSeekNormalRect);
                    invalidate();
                } else if (onTounchTimeBar) {
                    if (timeFilterSeekListener != null) {
                        timeFilterSeekListener.onSelect(timeSeekBarLocation / getWidth());
                    }
                    mTimeSeekSelectDrawable.setBounds(mTimeSeekNormalRect);
                    invalidate();
                }
                onTouchSeekbar = false;
                onTounchTimeBar = false;
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
        drawFilter(canvas);
        drawTimeBackBg(canvas);
        drawSeekBar(canvas);
        drawTimeSeekBar(canvas);
    }

    private void drawTimeBackBg(Canvas canvas) {
        if (!needDrawTimeBackBg()) {
            return;
        }
        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(),
                null, Canvas.ALL_SAVE_FLAG);
        bgRectf.set(0F, bgPaddingTop, getWidth(), getHeight() - bgPaddingBottom);
        bgPaint.setColor(timeBackBg);
        canvas.drawRect(bgRectf, bgPaint);

        canvas.drawPath(clipPath, clipPaint);
        canvas.restoreToCount(layerId);
    }

    private void drawTimeSeekBar(Canvas canvas) {
        if (model != MODE_TIME) {
            return;
        }
        canvas.save();
        Rect rect = mTimeSeekSelectDrawable.getBounds();
        canvas.translate(timeSeekBarLocation, (getHeight() - rect.height()) / 2);
        mTimeSeekSelectDrawable.draw(canvas);
        canvas.restore();
    }


    private void drawFilter(Canvas canvas) {
        if (needDrawTimeBackBg() || model == MODE_TIME || totleTime <= 0L || frameFilters == null) {
            // 不是普通model 或者是倒放模式  不绘制颜色条
            return;
        }
        int layerId = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(),
                null, Canvas.ALL_SAVE_FLAG);
        for (int i = 0; i < frameFilters.size(); i++) {
            tempFilter = frameFilters.get(i);
            bgRectf.set(getFilterStart(tempFilter), bgPaddingTop, getFilterEnd(tempFilter), getHeight() - bgPaddingBottom);
            bgPaint.setColor(tempFilter.getColor());
            canvas.drawRect(bgRectf, bgPaint);
        }
        canvas.drawPath(clipPath, clipPaint);
        canvas.restoreToCount(layerId);
    }

    private boolean needDrawTimeBackBg() {
        return needShowTimeBackBg && this.timeBack;
    }


    private void drawSeekBar(Canvas canvas) {
        canvas.save();
        Rect rect = mNormalSeekSelectDrawable.getBounds();
        canvas.translate(seekBarLocation, (getHeight() - rect.height()) / 2);
        mNormalSeekSelectDrawable.draw(canvas);
        canvas.restore();
    }

    private float getFilterStart(FrameFilter frameFilter) {
        if (timeBack) {
            return getWidth() * (1 - (frameFilter.getEndTime() * 1F / totleTime));
        }
        return getWidth() * (frameFilter.getStartTime() * 1F / totleTime);
    }

    private float getFilterEnd(FrameFilter frameFilter) {
        if (timeBack) {
            return getWidth() * (1 - (frameFilter.getStartTime() * 1F / totleTime));
        }
        return getWidth() * (frameFilter.getEndTime() * 1F / totleTime);
    }

    public void initLength(long totleTime, @NonNull List<FrameFilter> frameFilters) {
        this.totleTime = totleTime;
        this.frameFilters = frameFilters;
        update(0f);
    }

    public void update() {
        postInvalidate();
    }

    /**
     * 更新进度条
     *
     * @param progress 百分比
     */
    public void update(float progress) {
        if (onTouchSeekbar) {
            // 正在seek时  不处理进度更新
            return;
        }
        if (progress < 0 || progress > 1 || getWidth() <= 0) {
            return;
        }
        float temp = progress * getWidth();
        if (temp == seekBarLocation) {
            return;
        }
        seekBarLocation = temp;
        postInvalidate();
    }

    public void setSeekListener(SeekListener seekListener) {
        this.seekListener = seekListener;
    }

    public void setTimeFilterSeekListener(TimeFilterSeekListener timeFilterSeekListener) {
        this.timeFilterSeekListener = timeFilterSeekListener;
    }

    @Mode
    public int getModel() {
        return model;
    }

    /**
     * 设置进度条模式
     *
     * @param model
     * @param timeBarSeek 时间特效需要的进度调
     * @param isBack      是否是倒放模式
     */
    public void setModel(@Mode int model, long timeBarSeek, boolean isBack) {
        setModel(model, timeBarSeek, isBack, false);
    }

    public void setModel(@Mode int model, long timeBarSeek, boolean isBack, boolean needShowTimeBackBg) {
        if (totleTime <= 0) {
            return;
        }
        this.needShowTimeBackBg = needShowTimeBackBg;
        this.timeBack = isBack;
        this.model = model;
        if (model == MODE_TIME) {
            setTimeFilterLocation(timeBarSeek / (totleTime * 1F));
        } else {
            postInvalidate();
        }
    }

    private void setTimeFilterLocation(float progress) {
        if (progress < 0 || progress > 1 || getWidth() <= 0) {
            return;
        }
        timeSeekBarLocation = progress * getWidth();
        postInvalidate();
    }

    /**
     * 不同进度条seek回调
     */
    public interface SeekListener {
        void onSeek(float progress);

        void onFinish(float progress);
    }

    /**
     * 时间特效进度条seek回调
     */
    public interface TimeFilterSeekListener {
        void onSelect(float progress);
    }

}
