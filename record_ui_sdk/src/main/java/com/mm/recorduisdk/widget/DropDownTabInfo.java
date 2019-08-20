package com.mm.recorduisdk.widget;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.mm.base_business.base.BaseTabOptionFragment;
import com.mm.base_business.base.tabinfo.TextTabInfo;
import com.mm.recorduisdk.R;

import com.google.android.material.tabs.MMTabLayout;

/**
 * 带红点的tabinfo
 */
public class DropDownTabInfo extends TextTabInfo {
    @Nullable
    private View icon;

    private boolean showCustom = false;

    public DropDownTabInfo(@Nullable CharSequence title,
                           @NonNull Class<? extends BaseTabOptionFragment> fragmentClazz) {
        super(title, fragmentClazz);
    }

    public DropDownTabInfo(@Nullable CharSequence title,
                           @NonNull Class<? extends BaseTabOptionFragment> fragmentClazz,
                           @Nullable Bundle args) {
        super(title, fragmentClazz, args);
    }

    public DropDownTabInfo(@Nullable CharSequence title,
                           @NonNull Class<? extends BaseTabOptionFragment> fragmentClazz,
                           @Nullable Bundle args, boolean preLoad) {
        super(title, fragmentClazz, args, preLoad);
    }

    public void showIcon(boolean show) {
        if (icon == null) {
            return;
        }
        if (show) {
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.INVISIBLE);
        }
    }

    public void setSelect(boolean select) {
        if (icon == null) {
            return;
        }
        if (select) {
            icon.setBackgroundResource(R.drawable.ic_gray_cate_arrow_up);
        } else {
            icon.setBackgroundResource(R.drawable.ic_gray_cate_arrow_bottom);
        }
    }

    public void setTitle(String title) {
        if (titleTextView == null) {
            return;
        }
        titleTextView.setText(title);
    }

    @NonNull
    @Override
    protected View inflateCustomView(@NonNull MMTabLayout tabLayout) {
        View rootLayout = LayoutInflater.from(tabLayout.getContext()).inflate(
                R.layout.layout_coustom_tab, tabLayout, false);
        titleScaleLayout = rootLayout.findViewById(R.id.tab_title_scale_layout);
        titleTextView = rootLayout.findViewById(R.id.tab_title);
        icon = rootLayout.findViewById(R.id.tab_coustom_img);
        setTitle(title);
        if (showCustom) {
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.INVISIBLE);
        }
        setSelect(false);

        return rootLayout;
    }

}
