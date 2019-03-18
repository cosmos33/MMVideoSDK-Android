package com.mm.sdkdemo.utils;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.MotionEvent;

/**
 * Created by XiongFangyu on 2017/6/6.
 *
 * 录制按钮触摸事件
 */
public class RecordButtonTouchEventHelper {
    private static final int LONG_PRESS_TIME = 300;
    private static final int MAX_CLICK_TIME = 200;

    private Handler handler;
    private boolean longPress = false;
    private boolean out = false;
    private Rect backRect;

    private boolean canLongPress = true;
    private boolean touchBack = false;

    private Callback callback;
    private LongPressCallback longPressCallback;

    private long downTime = 0;

    public RecordButtonTouchEventHelper() {
        handler = new Handler(Looper.getMainLooper());
    }

    public void setBackRect(Rect rect) {
        backRect = rect;
    }

    private Runnable longPressRunnable = new Runnable() {
        @Override
        public void run() {
            longPress = true;
            if (callback != null) {
                callback.onLongPressed();
            }
        }
    };

    public boolean onTouchEvent(MotionEvent me) {
        switch (me.getAction()) {
            case MotionEvent.ACTION_DOWN:
                longPress = false;
                out = false;
                removeCallbacks(longPressRunnable);
                if (touchBack) {
                    if (callback != null) {
                        callback.onClick();
                    }
                    return true;
                }
                if (canLongPress)
                    postDelayed(longPressRunnable, LONG_PRESS_TIME);
                downTime = SystemClock.uptimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!canLongPress)
                    return true;
                if (longPress) {
                    final float x = me.getX();
                    final float y = me.getY();
                    if (backRect.contains((int) x, (int) y)) {
                        if (out && callback != null) {
                            callback.onDragIn();
                        }
                        out = false;
                    } else {
                        if (!out && callback != null) {
                            callback.onDragOut();
                        }
                        out = true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                removeCallbacks(longPressRunnable);
                if (longPress) {
                    if (out) {
                        if (callback != null)
                            callback.onCancel();
                        break;
                    }
                    longPressCallback.onLongPressUp();
                    break;
                } else if (!touchBack && SystemClock.uptimeMillis() - downTime <= MAX_CLICK_TIME) {
                    if (callback != null) {
                        callback.onClick();
                    }
                }
                break;
        }
        return true;
    }

    private void postDelayed(Runnable action, long delay) {
        handler.postDelayed(action, delay);
    }

    private void removeCallbacks(Runnable action) {
        handler.removeCallbacks(action);
    }

    public void release() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
        callback = null;
        longPressCallback = null;
        backRect = null;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setLongPressCallback(LongPressCallback longPressCallback) {
        this.longPressCallback = longPressCallback;
    }

    public void setCanLongPress(boolean canLongPress) {
        this.canLongPress = canLongPress;
    }

    public boolean isCanLongPress() {
        return canLongPress;
    }

    public void setTouchBack(boolean touchBack) {
        this.touchBack = touchBack;
    }

    public interface Callback {
        void onLongPressed();

        void onDragIn();

        void onDragOut();

        void onCancel();

        void onClick();
    }

    public interface LongPressCallback {
        void onLongPressUp();
    }
}
