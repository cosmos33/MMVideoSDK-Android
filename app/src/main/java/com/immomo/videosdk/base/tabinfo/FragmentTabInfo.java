package com.immomo.videosdk.base.tabinfo;

import android.os.Bundle;

import com.google.android.material.tabs.MomoTabLayout;
import com.immomo.videosdk.base.BaseTabOptionFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class FragmentTabInfo extends MomoTabLayout.TabInfo {
    @NonNull
    private final Class<? extends BaseTabOptionFragment> fragmentClazz;
    @Nullable
    private final Bundle args;
    private final boolean preLoad;

    public FragmentTabInfo(@NonNull Class<? extends BaseTabOptionFragment> fragmentClazz,
                           @Nullable Bundle args, boolean preLoad) {
        this.fragmentClazz = fragmentClazz;
        this.args = args;
        this.preLoad = preLoad;
    }

    @NonNull
    public Class<? extends BaseTabOptionFragment> getFragmentClazz() {
        return fragmentClazz;
    }

    @Nullable
    public Bundle getArgs() {
        return args;
    }

    public boolean isPreLoad() {
        return preLoad;
    }
}
