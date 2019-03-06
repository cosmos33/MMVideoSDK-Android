package com.immomo.videosdk.base.tabinfo;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.MomoTabLayout;
import com.google.android.material.tabs.ScaleLayout;
import com.immomo.videosdk.base.BaseTabOptionFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TextTabInfo extends FragmentTabInfo {
    @Nullable
    protected ScaleLayout titleScaleLayout;
    @Nullable
    protected TextView titleTextView;
    @Nullable
    protected CharSequence title;

    public TextTabInfo(@Nullable CharSequence title,
                       @NonNull Class<? extends BaseTabOptionFragment> fragmentClazz) {
        this(title, fragmentClazz, null, false);
    }

    public TextTabInfo(@Nullable CharSequence title,
                       @NonNull Class<? extends BaseTabOptionFragment> fragmentClazz,
                       @Nullable Bundle args) {
        this(title, fragmentClazz, args, false);
    }

    public TextTabInfo(@Nullable CharSequence title,
                       @NonNull Class<? extends BaseTabOptionFragment> fragmentClazz,
                       @Nullable Bundle args, boolean preLoad) {
        super(fragmentClazz, args, preLoad);
        this.title = title;
    }

    public void setTitle(@Nullable CharSequence title) {
        this.title = title;
        if (titleTextView != null) {
            titleTextView.setText(title);
        }
    }

    @Nullable
    public CharSequence getTitle() {
        return title;
    }

    @Override
    protected void onAnimatorUpdate(@NonNull MomoTabLayout tabLayout,
                                    @NonNull View customView, float percent) {
        if (tabLayout.isEnableScale() && titleScaleLayout != null) {
            titleScaleLayout.setChildScale(1 + MAX_SCALE_OFFSET * percent,
                    1 + MAX_SCALE_OFFSET * percent);
        }
        if (titleTextView != null) {
            titleTextView.setTypeface(null, percent > 0.3 ? Typeface.BOLD : Typeface.NORMAL);
        }
    }

    @NonNull
    @Override
    protected View inflateCustomView(@NonNull MomoTabLayout tabLayout) {
        titleTextView = new TextView(tabLayout.getContext());
        inheritTabLayoutStyle(titleTextView, tabLayout);
        titleTextView.setText(title);

        titleScaleLayout = new ScaleLayout(titleTextView);
        return titleScaleLayout;
    }
}
