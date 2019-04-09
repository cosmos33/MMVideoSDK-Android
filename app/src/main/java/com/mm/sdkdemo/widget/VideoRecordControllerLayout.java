package com.mm.sdkdemo.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mm.sdkdemo.R;

/**
 * Created by wangsiyuan on 2017/7/21.
 */

public class VideoRecordControllerLayout extends RelativeLayout {

    private static final float BIG_SCALE = 1.25f;
    private static final float SMALL_SCALE = 1.25f * 0.9f;

    private View mRecordLayout;
    private ImageView mRecordFaceIcon;

    private ValueAnimator mScaleAnim;

    public VideoRecordControllerLayout(Context context) {
        this(context, null);
    }

    public VideoRecordControllerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoRecordControllerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            inflater.inflate(R.layout.view_video_record_controler_layout, this);
            setClipChildren(false);
        }
    }

    public void showRecordingFace(boolean visiable) {
        ensureRecordLayout();
        if (visiable) {
            mRecordLayout.setVisibility(VISIBLE);
            startScaleAnim(SMALL_SCALE, false);
        } else {
            startScaleAnim(BIG_SCALE, true);
        }
    }

    private void startScaleAnim(float endScale, final boolean toBig) {
        if (mScaleAnim != null) {
            mScaleAnim.cancel();
        }
        final float startScale = Math.max(mRecordLayout.getScaleX(), mRecordLayout.getScaleY());
        mScaleAnim = ValueAnimator.ofFloat(startScale, endScale);
        mScaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float scale = (float) animation.getAnimatedValue();
                mRecordLayout.setScaleX(scale);
                mRecordLayout.setScaleY(scale);
            }
        });
        mScaleAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                if (toBig) {
                    mRecordLayout.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (toBig) {
                    mRecordLayout.setVisibility(GONE);
                }
            }
        });
        mScaleAnim.start();
    }

    private void ensureRecordLayout() {
        if (mRecordLayout == null || mRecordFaceIcon == null) {
            ViewStub stub = findViewById(R.id.recording_face_stub);
            mRecordLayout = stub.inflate();
            mRecordLayout.setScaleX(BIG_SCALE);
            mRecordLayout.setScaleY(BIG_SCALE);
            mRecordFaceIcon = mRecordLayout.findViewById(R.id.moment_face_icon);
        }
    }
}
