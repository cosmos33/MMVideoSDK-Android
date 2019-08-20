package com.mm.recorduisdk.widget.videorangebar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.IntRange;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.mm.mmutil.log.Log4Android;
import com.mm.recorduisdk.R;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 视频分段选择的View 覆盖在 {@link VideoRangeBar} 里面
 * Project MomoDemo
 * Package com.mm.momo.videorangebar
 * Created by tangyuchun on 2/14/17.
 */

class VideoRangeSelectorView extends LinearLayout {
    public VideoRangeSelectorView(Context context) {
        super(context);
        init(context);
    }

    public VideoRangeSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoRangeSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public VideoRangeSelectorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private Paint outsidePaint;
    private List<VideoRange> mRanges = null;
    /**
     * 视频总长度 单位 ms
     */
    private long totalVideoDurationInMs = 0;
    /**
     * 左右两端空白尺寸
     */
    private int emptyHeaderFooterWidth;
    /**
     */
    private int extraHeight = 0;

    private List<RangeBarListener> rangeBarListeners;
    /**
     * 是否触摸选框外面，就会取消选中
     */
    private boolean cancelSelectOnTouchOutside = true;

    private void init(Context context) {
        outsidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outsidePaint.setColor(0xC0B7B7B7);
        outsidePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int startPos = emptyHeaderFooterWidth;
        if (mRanges == null || mRanges.isEmpty()) {
            canvas.drawRect(startPos, extraHeight / 2, getWidth() - emptyHeaderFooterWidth, getHeight() - extraHeight / 2, outsidePaint);
        } else {
            int left;
            for (VideoRange range : mRanges) {
                canvas.save();
                left = calculateRangeLeftPos(range);
                canvas.drawRect(startPos, extraHeight / 2, left, getHeight() - extraHeight / 2, outsidePaint);
                //绘制range
                canvas.translate(left, 0);
                range.draw(canvas);
                startPos = left + range.getBounds().width();
                canvas.restore();
            }
            int end = getWidth() - emptyHeaderFooterWidth;
            if (startPos < end) {
                canvas.drawRect(startPos, extraHeight / 2, end, getHeight() - extraHeight / 2, outsidePaint);
            }
        }
    }

    private int calculateRangeLeftPos(VideoRange range) {
        if (range == null || totalVideoDurationInMs <= 0) {
            return emptyHeaderFooterWidth;
        }
        return emptyHeaderFooterWidth + (int) (((float) range.getStartTime() / totalVideoDurationInMs) * (getWidth() - emptyHeaderFooterWidth * 2));
    }

    /**
     * 计算一个Range的宽度
     *
     * @param range
     * @return
     */
    private int calculateRangeWidthPixel(VideoRange range) {
        if (range == null || totalVideoDurationInMs <= 0) {
            return 0;
        }
        return (int) (((float) range.getDuration() / totalVideoDurationInMs) * (getWidth() - emptyHeaderFooterWidth * 2));
    }

    /**
     * 获得视频的长度对应的View的宽度
     *
     * @return
     */
    int getWidthOfVideo() {
        return getWidth() - emptyHeaderFooterWidth * 2;
    }

    public void setTotalVideoDurationInMs(long totalVideoDurationInMs) {
        if (totalVideoDurationInMs <= 0) {
            throw new InvalidParameterException("参数错误，totalVideoDurationInMs 必须大于0");
        }
        this.totalVideoDurationInMs = totalVideoDurationInMs;
    }

    public void setCancelSelectOnTouchOutside(boolean cancelSelectOnTouchOutside) {
        this.cancelSelectOnTouchOutside = cancelSelectOnTouchOutside;
    }

    public long getTotalVideoDurationInMs() {
        return totalVideoDurationInMs;
    }

    void setExtraHeight(int extraHeight) {
        this.extraHeight = extraHeight;
    }

    public void addOnSelectedRangeChangedListener(RangeBarListener listener) {
        if (listener == null) {
            return;
        }
        if (rangeBarListeners == null) {
            rangeBarListeners = new ArrayList<>();
        }
        rangeBarListeners.add(listener);
    }

    public void removeOnSelectedRangeChangedListener(RangeBarListener listener) {
        if (listener == null || rangeBarListeners == null || rangeBarListeners.isEmpty()) {
            return;
        }
        rangeBarListeners.remove(listener);
    }

    private void callbackOnSelectedRangeSwitched(VideoRange newRange) {
        if (rangeBarListeners == null || rangeBarListeners.isEmpty()) {
            return;
        }
        for (RangeBarListener listener : rangeBarListeners) {
            if (listener != null) {
                listener.onSelectedRangeSwitched(newRange);
            }
        }
    }

    private void callbackOnRangeMoving(VideoRange range) {
        if (rangeBarListeners == null || rangeBarListeners.isEmpty()) {
            return;
        }
        for (RangeBarListener listener : rangeBarListeners) {
            if (listener != null) {
                listener.onRangeMoving(range);
            }
        }
    }

    private void callbackOnRangeMoveStopped(VideoRange range) {
        if (rangeBarListeners == null || rangeBarListeners.isEmpty()) {
            return;
        }
        for (RangeBarListener listener : rangeBarListeners) {
            if (listener != null) {
                listener.onRangeMoveStopped(range);
            }
        }
    }

    private void log(String msg) {
        if (Log4Android.getInstance().isDebug()) {
            Log.d("VideoRangeSelectorView", "tang---" + msg);
        }
    }

    public int getRangeCount() {
        return mRanges != null ? mRanges.size() : 0;
    }

    /**
     * 增加一个新的区段,所有的选区都是按照时间顺序排列存放
     *
     * @param start
     * @param end
     * @param playAddAnim 是否在添加的时候播放动画
     * @return
     */
    public VideoRange addRange(long start,
                               long end,
                               float speed,
                               boolean playAddAnim,
                               boolean selected,
                               long minDuration,
                               long maxDuration,
                               @IntRange(from = VideoRange.RangeType.TYPE_NORMAL, to = VideoRange.RangeType.TYPE_FIXED_LENGTH) int type) {
        if (totalVideoDurationInMs <= 0) {
            throw new InvalidParameterException("参数错误，请先设置 totalVideoDurationInMs");
        }
        if (start < 0 || start >= end) {
            //参数错误，start必须小于end
            log("参数错误，start必须小于end");
            return null;
            //            throw new InvalidParameterException("参数错误，start必须小于end");
        }
        long duration = end - start;
        if (duration < minDuration || duration > maxDuration) {
            log("参数错误，选区时间段 超过了最小时间和最大时间限制");
            return null;
            //            throw new InvalidParameterException("参数错误，选区时间段 超过了最小时间和最大时间限制");
        }
        if (mRanges != null && !mRanges.isEmpty()) {
            VideoRange lastRange = null;
            int count = mRanges.size(), index = 0;
            for (VideoRange range : mRanges) {
                //如果范围重叠了，则不能添加
                if (range.contains(start) || range.overlappingIn(start, end)) {
                    log("范围重叠了，则不能添加");
                    return null;
                }
                if (range.contains(end)) {
                    //在左侧
                    if (start < range.getStartTime()) {
                        long lastStart = lastRange != null ? lastRange.getEndTime() : 0;
                        if (range.getStartTime() - lastStart > duration) {
                            //左侧有足够的空间放置
                            end = range.getStartTime();
                            start = end - duration;
                            break;
                        } else {
                            log("空间不足，放不下");
                            return null;
                        }
                    } else {
                        //在右侧
                        if (index == count - 1) {
                            long space = totalVideoDurationInMs - range.getEndTime();
                            if (space >= duration) {
                                end = totalVideoDurationInMs;
                                start = end - duration;
                                break;
                            } else {
                                log("空间不足，放不下");
                                return null;
                            }
                        }
                    }
                } else {
                    //左侧
                    if (start < range.getStartTime()) {
                        long lastStart = lastRange != null ? lastRange.getEndTime() : 0;
                        if (range.getStartTime() - lastStart >= duration) {
                            break;
                        } else {
                            log("空间不足，放不下");
                            return null;
                        }
                    } else {
                        //右侧
                        //有足够的空间
                        if (index == count - 1) {
                            long space = totalVideoDurationInMs - range.getEndTime();
                            if (space >= duration) {
                                if (end > totalVideoDurationInMs) {
                                    end = totalVideoDurationInMs;
                                    start = end - duration;
                                }
                                break;
                            } else {
                                log("空间不足，放不下");
                                return null;
                            }
                        }
                    }
                }
                lastRange = range;
                index++;
            }
        } else {
            if (duration > totalVideoDurationInMs) {
                log("空间不足，放不下");
                return null;
            }
            if (end > totalVideoDurationInMs) {
                end = totalVideoDurationInMs;
                start = end - duration;
            }
        }
        VideoRange range = new VideoRange(start, end);
        range.setType(type);
        setupRangeParams(range);
        range.setStrokeWidth(extraHeight / 2);
        range.setMinDuration(minDuration);
        range.setMaxDuration(maxDuration);
        range.setHandlerWidth(getResources().getDimensionPixelSize(R.dimen.vrb_range_handler_width));
        range.setSpeed(speed);

        if (mRanges == null) {
            mRanges = new ArrayList<>();
        }
        mRanges.add(range);
        if (selected) {
            innerMarkCurrentRange(range, false);
        }

        //排序，确保分段都按照开始时间进行排列
        Collections.sort(mRanges);

        invalidate();
        if (playAddAnim) {
            startAddAnim(range);
        }
        return range;
    }

    /**
     * 添加进去时的动画
     */
    private ValueAnimator addAnimator;

    private void startAddAnim(final VideoRange range) {
        if (addAnimator != null && addAnimator.isRunning()) {
            return;
        }
        int width = calculateRangeWidthPixel(range);
        addAnimator = ValueAnimator.ofInt(range.getHandlerWidth(), width);
        addAnimator.setDuration(300);
        addAnimator.setInterpolator(new AccelerateInterpolator());
        addAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int newV = (int) animation.getAnimatedValue();
                range.setBounds(0, 0, newV, getHeight());
                invalidate();
            }
        });
        addAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                addAnimator = null;
            }
        });
        addAnimator.start();
    }

    private boolean isRunningAnim() {
        return addAnimator != null && addAnimator.isRunning();
    }

    /**
     * 当Range的范围发生变化时，要重新计算Range的 bounds，即选框的范围
     *
     * @param range
     */
    private void setupRangeParams(VideoRange range) {
        int width = calculateRangeWidthPixel(range);
        range.setBounds(0, 0, width, getHeight());
    }

    public void removeRange(VideoRange range) {
        if (range == null || mRanges == null) {
            return;
        }
        mRanges.remove(range);
        if (range == mSelectedRange) {
            mSelectedRange = null;
        }
        invalidate();

        callbackOnSelectedRangeSwitched(mSelectedRange);
    }

    void clearAllRanges() {
        if (mRanges != null) {
            mRanges.clear();
        }
        mSelectedRange = null;
        invalidate();
    }

    void cancelSelectedRange() {
        if (mRanges == null || mRanges.isEmpty()) {
            return;
        }
        for (VideoRange r : mRanges) {
            r.setSelected(false);
        }
        mSelectedRange = null;
        invalidate();

        callbackOnSelectedRangeSwitched(null);
    }

    private VideoRange mSelectedRange;

    private void innerMarkCurrentRange(VideoRange range, boolean refresh) {
        if (mRanges == null || mRanges.isEmpty()) {
            return;
        }
        if (!mRanges.contains(range)) {
            throw new InvalidParameterException("range 不在 mRanges中");
        }
        for (VideoRange r : mRanges) {
            r.setSelected(false);
        }
        range.setSelected(true);
        mSelectedRange = range;

        if (refresh) {
            invalidate();
        }

        callbackOnSelectedRangeSwitched(mSelectedRange);
    }

    /**
     * 指定的时间戳是否在选框范围内
     *
     * @param time
     * @return 如果在选框范围内，则返回该选框，否则返回 null
     */
    VideoRange inVideoRange(long time) {
        if (mRanges == null || mRanges.isEmpty()) {
            return null;
        }
        for (VideoRange range : mRanges) {
            if (range.contains(time)) {
                return range;
            }
        }
        return null;
    }

    public VideoRange getSelectedRange() {
        return mSelectedRange;
    }

    public List<VideoRange> getAllRanges() {
        return mRanges;
    }

    public void setEmptyHeaderFooterWidth(int emptyHeaderFooterWidth) {
        this.emptyHeaderFooterWidth = emptyHeaderFooterWidth;
    }

    private int rangeTouchState = TouchHandleState.STATE_NONE;
    private float lastX;
    /**
     * 额外增加的距离 用于阔大触摸左右把手的拖动范围
     */
    private int EXTRA_TOUCH_DISTANCE = 30;

    public boolean handleTouchEvent(MotionEvent event, int scrollX) {
        if (isRunningAnim()) {
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX() + scrollX;
                float y = event.getY();
                lastX = x;

                if (mSelectedRange != null) {
                    int left = calculateRangeLeftPos(mSelectedRange);
                    int right = left + calculateRangeWidthPixel(mSelectedRange);
                    int leftHandleLeft = left;
                    int leftHandleRight = leftHandleLeft + mSelectedRange.getHandlerWidth();

                    int rightHandleLeft = right - mSelectedRange.getHandlerWidth();
                    int rightHandleRight = right;

                    //在选区范围内
                    if (y >= 0 && y <= getHeight() && x >= left && x <= right) {
                        if (x >= leftHandleLeft && x <= (leftHandleRight + EXTRA_TOUCH_DISTANCE)) {
                            rangeTouchState = TouchHandleState.STATE_LEFT;
                        } else if (x >= (rightHandleLeft - EXTRA_TOUCH_DISTANCE) && x <= rightHandleRight) {
                            rangeTouchState = TouchHandleState.STATE_RIGHT;
                        } else {
                            rangeTouchState = TouchHandleState.STATE_MOVE;
                        }
                        return true;
                    }
                }
                //没有触摸非选中区域
                if (mRanges != null && cancelSelectOnTouchOutside) {
                    float percentInVideo = (x - emptyHeaderFooterWidth) / getWidthOfVideo();
                    long posInVideo = (long) (percentInVideo * totalVideoDurationInMs);
                    log("tang-----没有选中的范围 ，点击的时间点是 " + posInVideo);

                    boolean touchInRange = false;
                    for (VideoRange range : mRanges) {
                        if (range.contains(posInVideo)) {
                            touchInRange = true;
                            if (mSelectedRange != null) {
                                mSelectedRange.setSelected(false);
                                mSelectedRange = null;
                            }
                            if (!range.isSelected()) {
                                range.setSelected(true);
                                mSelectedRange = range;
                            }
                            invalidate();
                            callbackOnSelectedRangeSwitched(mSelectedRange);
                            log("tang-----新选中的范围 是 " + range.toString());
                            break;
                        }
                    }
                    //点击外部区域，没有点击选框，则取消选中
                    if (!touchInRange && mSelectedRange != null) {
                        mSelectedRange.setSelected(false);
                        mSelectedRange = null;
                        invalidate();
                        callbackOnSelectedRangeSwitched(mSelectedRange);
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isTouchingRange() && mSelectedRange != null) {
                    callbackOnRangeMoveStopped(mSelectedRange);
                }
                lastX = 0;
                rangeTouchState = TouchHandleState.STATE_NONE;
                break;
            case MotionEvent.ACTION_CANCEL:
                lastX = 0;
                rangeTouchState = TouchHandleState.STATE_NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isTouchingRange() && mSelectedRange != null) {
                    float moveX = event.getX() + scrollX;
                    float dx = moveX - lastX;
                    lastX = moveX;

                    log("MOVE  " + mSelectedRange.canMoved());

                    int duration = (int) (dx / getWidthOfVideo() * totalVideoDurationInMs);

                    //找到高亮的选框左右两侧的两个选区，用于确保拖拽时，区域不能重叠
                    int indexOfRange = mRanges.indexOf(mSelectedRange);
                    VideoRange leftRange = null, rightRange = null;
                    if (indexOfRange - 1 >= 0) {
                        leftRange = mRanges.get(indexOfRange - 1);
                    }
                    if (indexOfRange + 1 < mRanges.size()) {
                        rightRange = mRanges.get(indexOfRange + 1);
                    }

                    long minStart = leftRange != null ? leftRange.getEndTime() : 0;
                    long maxEnd = rightRange != null ? rightRange.getStartTime() : totalVideoDurationInMs;

                    //修改开头
                    if (mSelectedRange.canDragLeftHandle() && rangeTouchState == TouchHandleState.STATE_LEFT) {
                        //修改开头
                        if (mSelectedRange.changeStart(duration, minStart)) {
                            setupRangeParams(mSelectedRange);
                            invalidate();
                            callbackOnRangeMoving(mSelectedRange);
                        }
                    } else if (mSelectedRange.canDragRightHandle() && rangeTouchState == TouchHandleState.STATE_RIGHT) {
                        //修改结尾
                        if (mSelectedRange.changeEnd(duration, maxEnd)) {
                            setupRangeParams(mSelectedRange);
                            invalidate();
                            callbackOnRangeMoving(mSelectedRange);
                        }
                    } else if (mSelectedRange.canMoved()) {
                        //如果不能拖拽左侧，右侧把手，则手指放在 左右把手时，会整体拖动选框
                        //整体移动选框
                        if (mSelectedRange.moveTime(duration, minStart, maxEnd)) {
                            setupRangeParams(mSelectedRange);
                            invalidate();
                            callbackOnRangeMoving(mSelectedRange);
                        } else {
                            log("不能整体移动");
                        }
                    } else {
                        return false;
                    }
                    return true;
                }
                break;
            default:
                break;
        }
        rangeTouchState = TouchHandleState.STATE_NONE;
        return false;
    }

    private boolean isTouchingRange() {
        return rangeTouchState == TouchHandleState.STATE_LEFT || rangeTouchState == TouchHandleState.STATE_RIGHT || rangeTouchState == TouchHandleState.STATE_MOVE;
    }

    static class TouchHandleState {
        static final int STATE_NONE = 0;
        static final int STATE_LEFT = 1;
        static final int STATE_RIGHT = 2;
        static final int STATE_MOVE = 3;
    }
}
