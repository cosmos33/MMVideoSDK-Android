package com.immomo.videosdk.widget;

import android.view.MotionEvent;

/**
 * Created by XiongFangyu on 2017/6/9.
 */
public class VerticalTouchHelper {

    private boolean moving = false;
    private int touchSlop;
    private int minFlingDis;
    private int minUpDis;

    private float downX = -1, downY = -1;
    private float lasty, my;

    private OnFlingListener listener;

    public boolean onTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moving = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (downX == -1) {
                    downX = x;
                    downY = y;
                }
                float dy = getCheckMoveAndDiffY(x, y);
                if (moving && listener != null) {
                    listener.onMoving(dy);
                    lasty = my;
                    my = y;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (moving) {
                    checkFling(x, y);
                }
                if (listener != null) {
                    float diffy = getCheckMoveAndDiffY(x, y);
                    //up
                    if (diffy <= -minUpDis && lasty > y) {
                        listener.onUp(diffy);
                    } else if (diffy >= minUpDis && lasty < y) {
                        listener.onUp(diffy);
                    } else {
                        listener.onCancel();
                    }
                }
                moving = false;
                downX = -1;
                downY = -1;
                break;
        }
        return moving;
    }

    private float getCheckMoveAndDiffY(float x, float y) {
        final float dx = x - downX;
        final float dy = y - downY;
        final float absDx = Math.abs(dx);
        final float absDy = Math.abs(dy);
        if (absDy > absDx && absDy > touchSlop)
            moving = true;
        return dy;
    }

    private void checkFling(float x, float y) {
        final float dx = x - downX;
        final float dy = y - downY;
        final float absDx = Math.abs(dx);
        final float absDy = Math.abs(dy);
        if (absDy > absDx && absDy > minFlingDis) {
            if (listener != null) {
                listener.onFling(dy < 0, absDy);
            }
        }
    }

    public void setTouchSlop(int touchSlop) {
        this.touchSlop = touchSlop;
    }

    public void setMinFlingDis(int minFlingDis) {
        this.minFlingDis = minFlingDis;
    }

    public void setMinUpDis(int minUpDis) {
        this.minUpDis = minUpDis;
    }

    public void setListener(OnFlingListener listener) {
        this.listener = listener;
    }

    public interface OnFlingListener {
        void onFling(boolean up, float absDy);

        void onMoving(float dy);

        void onUp(float dy);

        void onCancel();
    }
}
