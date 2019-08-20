package com.mm.recorduisdk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by XiongFangyu on 2017/6/9.
 */
public class FilterScrollMoreViewPager extends ScrollMoreViewPager {

    private VerticalTouchHelper verticalTouchHelper;
    private VerticalMovingListener listener;

    public FilterScrollMoreViewPager(Context context) {
        this(context, null);
    }

    public FilterScrollMoreViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        verticalTouchHelper = new VerticalTouchHelper();
        verticalTouchHelper.setTouchSlop(mTouchSlop);
        verticalTouchHelper.setMinFlingDis(mTouchSlop * 3);
        verticalTouchHelper.setMinUpDis(context.getResources().getDisplayMetrics().heightPixels / 10);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result = verticalTouchHelper.onTouchEvent(ev);
        return super.onTouchEvent(ev) || result;
    }

    public void setOnVerticalFlingListener(final VerticalMovingListener li) {
        if (listener == null) {
            listener = li;
            verticalTouchHelper.setListener(new VerticalTouchHelper.OnFlingListener() {
                @Override
                public void onFling(boolean up, float absDy) {
                    if (absDy * 3 > getHeight()) {
                        if (listener != null) {
                            listener.onFling(up);
                        }
                    }
                }

                @Override
                public void onMoving(float dy) {
                    if (listener != null) {
                        listener.onMoving(dy / getHeight());
                    }
                }

                @Override
                public void onUp(float dy) {
                    if (listener != null) {
                        listener.onUp(dy);
                    }
                }

                @Override
                public void onCancel() {
                    if (listener != null) {
                        listener.onCancel();
                    }
                }
            });
        } else {
            verticalTouchHelper.setListener(null);
            listener = null;
        }
    }

    public interface VerticalMovingListener {
        void onMoving(float offset);

        void onFling(boolean up);

        void onUp(float offset);

        void onCancel();
    }
}
