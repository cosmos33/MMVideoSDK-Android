package com.mm.sdkdemo.recorder.specialfilter.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.mm.sdkdemo.widget.CircleImageView;

public class FilterImageView extends CircleImageView {


    private FilterEvent filterEvent;
    private CheckForLongPress checkForLongPress = new CheckForLongPress();
    private boolean mHasPerformedLongPress = false;


    public FilterImageView(Context context) {
        super(context);
    }

    public FilterImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FilterImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHasPerformedLongPress = false;
                postDelayed(checkForLongPress, ViewConfiguration.getLongPressTimeout());
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                removeCallbacks(checkForLongPress);
                if (!mHasPerformedLongPress) {
                    onClickEvent();
                } else {
                    onLongPressEndEvent();
                }
                break;
            default:
                break;
        }
        return true;
    }


    private void onLongPressStartEvent() {
        mHasPerformedLongPress = true;
        if (filterEvent != null) {
            filterEvent.onLongPressStartEvent();
        }
    }


    private void onLongPressEndEvent() {
        mHasPerformedLongPress = false;
        if (filterEvent != null) {
            filterEvent.onLongPressEndEvent();
        }
    }

    private void onClickEvent() {
        if (filterEvent != null) {
            filterEvent.onClickEvent();
        }
    }

    public void setFilterEvent(FilterEvent filterEvent) {
        this.filterEvent = filterEvent;
    }


    public interface FilterEvent {
        void onLongPressStartEvent();

        void onLongPressEndEvent();

        void onClickEvent();
    }


    private class CheckForLongPress implements Runnable {

        @Override
        public void run() {
            onLongPressStartEvent();
        }
    }
}
