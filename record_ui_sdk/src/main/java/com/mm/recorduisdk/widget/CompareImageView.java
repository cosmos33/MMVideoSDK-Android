package com.mm.recorduisdk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

public class CompareImageView extends AppCompatImageView {
    public OnTouchEventListener onTouchEventListener = null;

    public CompareImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (onTouchEventListener != null) {
                            onTouchEventListener.onTouchDown();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (onTouchEventListener != null) {
                            onTouchEventListener.onTouchUp();
                        }
                        break;
                }
                return true;
            }
        });
    }

    public CompareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CompareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface OnTouchEventListener {
        void onTouchDown();

        void onTouchUp();
    }

    public void setOnTouchEventListener(OnTouchEventListener onTouchEventListener) {
        this.onTouchEventListener = onTouchEventListener;
    }
}