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
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import com.mm.mmutil.log.Log4Android;
import com.immomo.moment.model.VideoFragment;
import com.mm.sdkdemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Project momodev
 * Package com.mm.momo.moment.view
 * Created by tangyuchun on 12/30/16.
 */

public class MomentRecordProgressView extends View {

    private int colorNotRecord = 0x4CFFFFFF;
    private int colorRecord = 0xFF25ecff;
    private int colorDelete = Color.RED;
    private int colorEmpty = Color.BLACK;
    private long maxDuration = 0L;
    /**
     * 分割线的宽度
     */
    private int emptyWidth = 1;

    private Paint progressPaint = null;

    private ArrayList<MomentRecordSlice> slices = null;
    /**
     * 动画的帧率:限制帧动画的帧率，防止造成录制页面的卡顿
     */
    private int animFPS = 60;

    private ProgressListener listener;

    public MomentRecordProgressView(Context context) {
        this(context, null);
    }

    public MomentRecordProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentRecordProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MomentRecordProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (context == null || attrs == null)
            return;
        final Resources.Theme theme = context.getTheme();
        TypedArray a = theme.obtainStyledAttributes(attrs,
                                                    R.styleable.MomentRecordProgressView, defStyleAttr, defStyleRes);
        TypedArray appearance = null;
        int ap = a.getResourceId(
                R.styleable.MomentRecordProgressView_mrpv_style, -1);
        if (ap != -1) {
            appearance = theme.obtainStyledAttributes(
                    ap, R.styleable.MomentRecordProgressView);
        }
        initStyle(appearance);
        initStyle(a);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) {
            maxDuration = 60000;
            slices = new ArrayList<>();
            MomentRecordSlice s = new MomentRecordSlice();
            s.setStart(0);
            s.setEnd(10000);
            slices.add(s);
            s = new MomentRecordSlice();
            s.setStart(0);
            s.setEnd(10000);
            slices.add(s);
            s = new MomentRecordSlice();
            s.setStart(0);
            s.setEnd(10000);
            s.setDeleting(true);
            slices.add(s);
        }
    }

    private void initStyle(TypedArray a) {
        if (a != null) {
            colorNotRecord = a.getColor(R.styleable.MomentRecordProgressView_mrpv_color_not_reocrd, colorNotRecord);
            colorRecord = a.getColor(R.styleable.MomentRecordProgressView_mrpv_color_record, colorRecord);
            colorDelete = a.getColor(R.styleable.MomentRecordProgressView_mrpv_color_delete, colorDelete);
            colorEmpty = a.getColor(R.styleable.MomentRecordProgressView_mrpv_color_empty, colorEmpty);
            emptyWidth = a.getDimensionPixelOffset(R.styleable.MomentRecordProgressView_mrpv_split_width, emptyWidth);
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (progressPaint == null) {
            progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        progressPaint.setStyle(Paint.Style.FILL);
        if (maxDuration <= 0 || slices == null || slices.isEmpty()) {
            canvas.drawColor(colorNotRecord);
        } else {
            final int height = getHeight();
            int drawedLength = 0;//已经绘制的长度
            for (MomentRecordSlice slice : slices) {
                if (slice == null || slice.getDuration() <= 0) {
                    continue;
                }
                //绘制一段进度条
                if (slice.isDeleting()) {
                    progressPaint.setColor(colorDelete);
                } else {
                    progressPaint.setColor(colorRecord);
                }
                int sliceWidth = getSliceProgressWidth(slice);
                int end = drawedLength + sliceWidth;
                slice.drawEnd = end;
                canvas.drawRect(drawedLength, 0, end, height, progressPaint);
                drawedLength = end;
            }
            drawEmpty(canvas);
            //绘制剩余的空白 需要除去 分割线占据的像素
            int left = getWidth() - drawedLength;
            if (left > 0) {
                progressPaint.setStyle(Paint.Style.FILL);
                progressPaint.setColor(colorNotRecord);
                canvas.drawRect(drawedLength, 0, (drawedLength + left), height, progressPaint);
            }
        }
    }

    private void drawEmpty(Canvas canvas) {
        progressPaint.setColor(colorEmpty);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(emptyWidth);
        final int h = getHeight();
        for (int i = 0, l = slices.size(); i < l; i++) {
            final MomentRecordSlice slice = slices.get(i);
            if (slice == null || slice.getDuration() <= 0 || slice.isRecording() || i == l - 1) {
                continue;
            }
            canvas.drawLine(slice.drawEnd, 0, slice.drawEnd, h, progressPaint);
        }
    }

    private ValueAnimator progressAnim = null;

    /**
     * 当前录制的片段
     */
    private MomentRecordSlice recordingSlice = null;

    public void setListener(ProgressListener listener) {
        this.listener = listener;
    }

    public void startRecord() {
        startRecord(1);
    }

    public void startRecord(final float speed) {
        final long recordDuration = getRecordDuration();
        if (recordDuration >= maxDuration)
            return;
        if (progressAnim != null && progressAnim.isRunning()) {
            return;
        }

        markLastNormal();

        MomentRecordSlice slice = new MomentRecordSlice();
        slice.setStart(SystemClock.uptimeMillis());
        slice.setRecording(true);
        slice.setSpeed(speed);
        recordingSlice = slice;
        if (slices == null) {
            slices = new ArrayList<>();
        }
        slices.add(slice);

        int current = (int) recordDuration;
        int max = (int) maxDuration;
        progressAnim = ValueAnimator.ofInt(current, max);
        progressAnim.setDuration((long) ((maxDuration - recordDuration) / speed));
        progressAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                recordingSlice.setEnd(SystemClock.uptimeMillis());
                invalidate();
                callbackProgress(-1);
            }
        });
        progressAnim.addListener(animatorListener);
        progressAnim.start();
    }

    private void callbackProgress(long force) {
        if (listener != null && slices != null) {
            long d = 0;
            if (force > 0) {
                listener.onProgress(force);
                return;
            }
            for (MomentRecordSlice s : slices) {
                if (s != null)
                    d += s.getDuration();
            }
            listener.onProgress(d);
        }
    }

    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public void removeLast() {
        if (slices == null || slices.isEmpty()) {
            return;
        }
        slices.remove(slices.size() - 1);
        invalidate();
    }

    /**
     * 是否用过变速
     * @return
     */
    public boolean isUseChangeSpeed() {
        if (slices == null || slices.isEmpty()) {
            return false;
        }

        for (MomentRecordSlice s : slices) {
            if (s.speed != 1L) {
                return true;
            }
        }
        return false;
    }


    public void clear() {
        if (slices != null) {
            slices.clear();
            invalidate();
        }
    }

    /**
     * 将最后一个标记为等待删除状态
     */
    public void markLastDeleting() {
        if (slices == null || slices.isEmpty()) {
            return;
        }
        MomentRecordSlice slice = slices.get(slices.size() - 1);
        slice.setDeleting(true);
        invalidate();
    }

    /**
     * 如果最后一个处于删除状态，则标记为正常状态
     */
    private void markLastNormal() {
        if (slices == null || slices.isEmpty()) {
            return;
        }
        MomentRecordSlice slice = slices.get(slices.size() - 1);
        if (slice.isDeleting()) {
            slice.setDeleting(false);
            invalidate();
        }
    }

    public int getCount() {
        return slices != null ? slices.size() : 0;
    }

    public void removeAll() {
        if (slices != null) {
            slices.clear();
            invalidate();
        }
    }

    /**
     * 恢复旧的分段
     *
     * @param fragments
     * @return
     */
    public void restoreKeepedSlice(List<VideoFragment> fragments) {
        if (fragments == null || fragments.isEmpty()) {
            return;
        }
        if (slices == null) {
            slices = new ArrayList<>();
        }
        slices.clear();

        long startTimeStamp = SystemClock.uptimeMillis();
        for (VideoFragment frag : fragments) {
            if (frag == null || frag.getDuration() <= 0L) {
                continue;
            }
            MomentRecordSlice slice = new MomentRecordSlice();
            slice.setStart(startTimeStamp);
            slice.setEnd(startTimeStamp + frag.getDuration());
            slice.setSpeed(frag.getSpeed());
            startTimeStamp += 1000;

            slices.add(slice);
        }
        Log4Android.getInstance().d("tang------恢复旧的分段 " + slices.size());
        invalidate();
    }

    /**
     * 结束一段录制，注意：结束一段录制，需要给slice设置录制时长
     */
    public void stopRecord() {
        if (slices == null || slices.isEmpty()) {
            return;
        }
        MomentRecordSlice slice = slices.get(slices.size() - 1);
        if (slice.isRecording()) {
            //更新片段的结束时间
            slice.setEnd(SystemClock.uptimeMillis());
            slice.setRecording(false);
            invalidate();
        }
        if (progressAnim != null && progressAnim.isRunning()) {
            progressAnim.cancel();
        }
        recordingSlice = null;
    }

    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        boolean fromCancel = false;

        @Override
        public void onAnimationStart(Animator animation) {
            fromCancel = false;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (!fromCancel) {
                callbackProgress(maxDuration);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            fromCancel = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    /**
     * 已经录制的时长
     *
     * @return
     */
    public long getRecordDuration() {
        if (slices == null || slices.isEmpty()) {
            return 0L;
        }
        long duration = 0L;
        for (MomentRecordSlice slice : slices) {
            if (slice == null || slice.getDuration() <= 0) {
                continue;
            }
            duration += slice.getDurationOfSpeed();
        }
        return duration;
    }

    public long getLastSliceDuration() {
        if (slices == null || slices.isEmpty()) {
            return 0L;
        }
        MomentRecordSlice slice = slices.get(slices.size() - 1);
        return slice != null ? slice.getDuration() : 0L;
    }

    public void release() {
        listener = null;
        if (progressAnim != null) {
            if (progressAnim.isRunning())
                progressAnim.cancel();
            progressAnim.removeAllUpdateListeners();
            progressAnim.removeAllListeners();
        }
        if (slices != null)
            slices.clear();
    }

    /**
     * 获得一段的绘制长度
     *
     * @param slice
     * @return
     */
    private int getSliceProgressWidth(MomentRecordSlice slice) {
        return (int) (getWidth() * slice.getDurationOfSpeed() / maxDuration);
    }

    private class MomentRecordSlice {
        /**
         * 是否处于删除状态
         */
        private boolean isDeleting = false;
        private boolean isRecording = false;
        private long start = 0L;
        private long end = 0L;
        private float drawEnd = 0;
        private float speed = 1L;

        public long getDuration() {
            return this.end - this.start;
        }

        /**
         * 变速后的时间
         * @return
         */
        public float getDurationOfSpeed() {
            return getDuration() * speed;
        }

        public float getSpeed() {
            return speed;
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }

        public boolean isDeleting() {
            return isDeleting;
        }

        public void setRecording(boolean recording) {
            isRecording = recording;
        }

        public boolean isRecording() {
            return isRecording;
        }

        public void setDeleting(boolean deleting) {
            isDeleting = deleting;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public void setEnd(long end) {
            this.end = end;
        }
    }

    public interface ProgressListener {
        void onProgress(long ms);
    }
}
