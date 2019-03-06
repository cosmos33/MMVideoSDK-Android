package com.immomo.videosdk.widget.videorangebar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import com.immomo.mmutil.log.Log4Android;
import com.immomo.videosdk.utils.CanvasUtils;

import java.security.InvalidParameterException;

/**
 * 视频分段选择的选框
 * Project MomoDemo
 * Package com.immomo.momo.videorangebar
 * Created by tangyuchun on 2/14/17.
 */

public class VideoRange extends ColorDrawable implements Comparable<VideoRange> {
    public static class RangeType {
        /**
         * 普通类型 可以拖拽左右的把手
         */
        public static final int TYPE_NORMAL = 1;
        /**
         * 只能拖拽左侧的把手，右侧把手不能拖动
         */
        public static final int TYPE_ONLY_DRAG_LEFT = 2;
        /**
         * 只能拖拽右侧的把手，左侧把手不能拖动
         */
        public static final int TYPE_ONLY_DRAG_RIGHT = 3;
        /**
         * 长度固定的选框 不能拖拽左右把手
         */
        public static final int TYPE_FIXED_LENGTH = 4;
    }

    private Paint mPaint;
    private RectF handleIconRect = null;

    /**
     * 视频时间戳
     */
    private long startTime;
    private long endTime;

    //最短时长
    private long minDuration = 1000;
    private long maxDuration = Integer.MAX_VALUE;
    //左右把手的宽度
    private int handlerWidth = 30;
    //是否处于选中状态
    private boolean isSelected = false;
    //是否可以整体拖动  默认开启移动功能
    private boolean canMove = true;
    //选框类型
    private int type = RangeType.TYPE_NORMAL;
    private boolean showBorderIfNotSelected = false;
    private float speed = 1f;//速度

    public VideoRange(long startTime, long endTime) {
        super();
        if (startTime >= endTime) {
            throw new InvalidParameterException("参数错误，start必须小于end");
        }
        this.startTime = startTime;
        this.endTime = endTime;
        if (mPaint == null) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getDuration() {
        return endTime - startTime;
    }

    void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    boolean isSelected() {
        return isSelected;
    }

    public void setMinDuration(long minDuration) {
        this.minDuration = minDuration;
    }

    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    public boolean canMoved() {
        return canMove;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setShowBorderIfNotSelected(boolean showBorderIfNotSelected) {
        this.showBorderIfNotSelected = showBorderIfNotSelected;
    }

    @Override
    public void draw(Canvas canvas) {
        //没有选中，且未选中状态不显示边框的话
        if (!showBorderIfNotSelected && !isSelected) {
            return;
        }
        Rect rect = getBounds();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xFFFFFFFF);
        float strokeWidth = mPaint.getStrokeWidth();
        float halfStrokeWidth = Math.round(strokeWidth / 2);// +1 是防止在边缘处存在小缝隙

        if (isSelected && handlerWidth > 0) {
            //绘制
            canvas.drawRect(halfStrokeWidth, halfStrokeWidth, rect.width() - halfStrokeWidth, rect.height() - halfStrokeWidth, mPaint);
            mPaint.setStyle(Paint.Style.FILL);
            float handleHeight = rect.height() - strokeWidth * 2;

            //绘制把手 要防止遮盖住圆角
            canvas.drawRect(0, strokeWidth, handlerWidth, strokeWidth + handleHeight, mPaint);
            canvas.drawRect(rect.width() - handlerWidth, strokeWidth, rect.width(), strokeWidth + handleHeight, mPaint);

            //            绘制中间的小 圆块
            mPaint.setColor(0xFFDCDCDC);
            if (handleIconRect == null) {
                handleIconRect = new RectF(0, 0, 0, 0);
            }
            int iconWidth = 10, iconHeight = 70;
            float left = (handlerWidth - iconWidth) / 2, top = (handleHeight - iconHeight) / 2;

            if (canDragLeftHandle()) {
                handleIconRect.set(left, top, left + iconWidth, top + iconHeight);
                CanvasUtils.drawRoundRect(canvas, mPaint, handleIconRect, 10);
            }
            if (canDragRightHandle()) {
                left = rect.width() - handlerWidth + (handlerWidth - iconWidth) / 2;
                handleIconRect.set(left, top, left + iconWidth, top + iconHeight);
                CanvasUtils.drawRoundRect(canvas, mPaint, handleIconRect, 10);
            }
        }
    }

    int getHandlerWidth() {
        return handlerWidth;
    }

    void setStrokeWidth(int widthInPixels) {
        if (mPaint != null) {
            mPaint.setStrokeWidth(widthInPixels);
        }
    }

    void setHandlerWidth(int handlerWidth) {
        this.handlerWidth = handlerWidth;
    }

    private void log(String msg) {
        if (Log4Android.getInstance().isDebug()) {
            Log.d("VideoRangeSelectorView", "tang---" + msg);
        }
    }

    boolean changeStart(long changeValue, long minStart) {
        log("tang---changeStart " + minStart);
        long newStart = startTime + changeValue;
        if (newStart < minStart) {
            return false;
        }
        long duration = endTime - newStart;
        if (duration < minDuration || duration > maxDuration) {
            return false;
        }
        startTime = newStart;
        return true;
    }

    boolean changeEnd(long changeValue, long maxEnd) {
        log("tang---changeEnd " + maxEnd);
        long newEnd = endTime + changeValue;
        if (newEnd > maxEnd) {
            return false;
        }
        long duration = newEnd - startTime;
        if (duration < minDuration || duration > maxDuration) {
            return false;
        }
        endTime = newEnd;
        return true;
    }

    /**
     * 整体移动选框
     *
     * @param changeValue
     * @param minStart
     * @param maxEnd
     * @return
     */
    boolean moveTime(long changeValue, long minStart, long maxEnd) {
        long newEnd = endTime + changeValue;
        long newStart = startTime + changeValue;
        if (newStart < minStart || newEnd > maxEnd) {
            return false;
        }
        long duration = newEnd - newStart;
        if (duration < minDuration || duration > maxDuration) {
            return false;
        }
        startTime = newStart;
        endTime = newEnd;
        return true;
    }

    @Override
    public String toString() {
        return "start:" + startTime + " end:" + endTime;
    }

    boolean contains(long timestampMS) {
        return startTime <= timestampMS && endTime >= timestampMS;
    }

    /**
     * 是否完全重叠
     * @param start
     * @param end
     * @return
     */
    public boolean overlappingIn(long start, long end) {
        return (startTime <= start && endTime >= end) || (startTime >= start && endTime <= end);
    }

    /**
     * 排序，小的排在前面
     *
     * @param another
     * @return
     */
    @Override
    public int compareTo(VideoRange another) {
        if (another == null) {
            return 1;
        }
        if (this.equals(another)) {
            return 0;
        }
        if (startTime == another.startTime) {
            return 0;
        }
        return startTime < another.startTime ? -1 : 1;
    }

    boolean canDragLeftHandle() {
        return type == RangeType.TYPE_NORMAL || type == RangeType.TYPE_ONLY_DRAG_LEFT;
    }

    boolean canDragRightHandle() {
        return type == RangeType.TYPE_NORMAL || type == RangeType.TYPE_ONLY_DRAG_RIGHT;
    }
}