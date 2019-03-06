package com.immomo.videosdk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.immomo.videosdk.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by jiabin on 2017/7/13.
 */

public class MomentFilterPanelTabLayout extends DrawLineLinearLayout {

    // 滤镜、美肌、美颜、瘦身、长腿
    public static final int ON_CLICK_FILTER = 0, ON_CLICK_SKIN = 1, ON_CLICK_FACE = 2, ON_CLICK_SLIMMING = 3, ON_CLICK_LONG_LEGS = 4;

    public static final int CLICK_INNER = 1, CLICK_OUTER = 2;

    @IntDef({ON_CLICK_FILTER, ON_CLICK_SKIN, ON_CLICK_FACE, ON_CLICK_SLIMMING, ON_CLICK_LONG_LEGS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TabSelectedPosition {

    }

    @IntDef({CLICK_INNER, CLICK_OUTER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CLICK {

    }

    private Context mContext;
    private OnTabClickListener listener;

    private View tabMore;
    private TabLayout tabLayout;

    private AtomicInteger selectType = new AtomicInteger();

    public MomentFilterPanelTabLayout(@NonNull Context context) {
        super(context);
        mContext = context;
        initViews();
    }

    public MomentFilterPanelTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initViews();
    }

    private void initViews() {
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        layoutInflater.inflate(R.layout.moment_filter_panel_tab, this);

        tabLayout = findViewById(R.id.filter_drawer_top_panel);
        //        tabLayout.setSelectedTabSlidingIndicator(new DefaultSlidingIndicator());
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onClick(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabMore = findViewById(R.id.tab_more_btn);
        tabMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onMoreViewClick(null);
                }
            }
        });
    }

    public void hideOtherTab() {
        tabLayout.getTabAt(4).setText("");
        tabLayout.getTabAt(3).setText("");
        tabLayout.getTabAt(2).setText("");
        tabLayout.getTabAt(1).setText("");
        //        tabLayout.removeTabAt(4);
        //        tabLayout.removeTabAt(3);
        //        tabLayout.removeTabAt(2);
        //        tabLayout.removeTabAt(1);
        //        tabSkin.setVisibility(INVISIBLE);
        //        tabFace.setVisibility(INVISIBLE);
        //        tabSlimming.setVisibility(INVISIBLE);
        //        tabLongLegs.setVisibility(INVISIBLE);
    }

    private void onClick(int position) {
        switch (position) {
            case ON_CLICK_FILTER:
                selectType.set(ON_CLICK_FILTER);
                listener.onTabClick(ON_CLICK_FILTER, CLICK_INNER);
                break;
            case ON_CLICK_SKIN:
                selectType.set(ON_CLICK_SKIN);
                listener.onTabClick(ON_CLICK_SKIN, CLICK_INNER);
                break;
            case ON_CLICK_FACE:
                selectType.set(ON_CLICK_FACE);
                listener.onTabClick(ON_CLICK_FACE, CLICK_INNER);
                break;
            case ON_CLICK_SLIMMING:
                selectType.set(ON_CLICK_SLIMMING);
                listener.onTabClick(ON_CLICK_SLIMMING, CLICK_INNER);
                break;
            case ON_CLICK_LONG_LEGS:
                selectType.set(ON_CLICK_LONG_LEGS);
                listener.onTabClick(ON_CLICK_LONG_LEGS, CLICK_INNER);
                break;
            default:
                break;
        }
    }

    public AtomicInteger getCurrentSelected() {
        return selectType;
    }

    public interface OnTabClickListener {
        void onTabClick(int position, @CLICK int flag);
    }

    public void setOnTabClickListener(OnTabClickListener listener) {
        this.listener = listener;
    }

    // 设置切换View选中状态
    public void setSelectTab(@TabSelectedPosition int tabSelectPosition) {
        tabLayout.setScrollPosition(tabSelectPosition, 0, true);
        if (tabSelectPosition == MomentFilterPanelTabLayout.ON_CLICK_SKIN || tabSelectPosition == MomentFilterPanelTabLayout.ON_CLICK_FACE) {
            tabMore.setVisibility(View.VISIBLE);
        } else {
            tabMore.setVisibility(View.INVISIBLE);
        }
    }

    private OnMoreViewClickListener mListener;

    public void setonMoreViewClickListener(OnMoreViewClickListener listener) {
        this.mListener = listener;
    }

    public interface OnMoreViewClickListener {
        void onMoreViewClick(View view);
    }

}
