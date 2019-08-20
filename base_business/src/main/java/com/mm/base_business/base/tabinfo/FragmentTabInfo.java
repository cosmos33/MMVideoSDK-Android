package com.mm.base_business.base.tabinfo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mm.base_business.base.BaseTabOptionFragment;

import com.google.android.material.tabs.MMTabLayout;


public abstract class FragmentTabInfo extends MMTabLayout.TabInfo {
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
