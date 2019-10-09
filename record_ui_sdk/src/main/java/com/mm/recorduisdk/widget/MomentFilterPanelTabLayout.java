package com.mm.recorduisdk.widget;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import com.mm.recorduisdk.IRecordResourceConfig;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.RecordUISDK;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jiabin on 2017/7/13.
 */

public class MomentFilterPanelTabLayout extends DrawLineLinearLayout implements View.OnClickListener {
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

    private DrawLineTextView filterTv, beautyTv, bigeyeTv, slimmingTv, legsTv;

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
        checkHide();
    }

    private void checkHide() {
        IRecordResourceConfig<File> filtersImgHomeDirConfig = RecordUISDK.getResourceGetter().getFiltersImgHomeDirConfig();
        if (filtersImgHomeDirConfig == null || !filtersImgHomeDirConfig.isOpen()) {
            filterTv.setVisibility(GONE);
        } else {
            filterTv.setVisibility(VISIBLE);
        }
    }

    private void initViews() {
        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        layoutInflater.inflate(R.layout.moment_filter_panel_tab, this);

        filterTv = findViewById(R.id.filter_text);
        beautyTv = findViewById(R.id.filter_beauty_text);
        bigeyeTv = findViewById(R.id.filter_bigeye_thin_text);
        slimmingTv = findViewById(R.id.filter_slimming_text);
        legsTv = findViewById(R.id.filter_long_legs_text);
        filterTv.setOnClickListener(this);
        beautyTv.setOnClickListener(this);
        bigeyeTv.setOnClickListener(this);
        slimmingTv.setOnClickListener(this);
        legsTv.setOnClickListener(this);

        tabMore = findViewById(R.id.tab_more_btn);
        tabMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onMoreViewClick(v);
                }
            }
        });


    }

    public void hideOtherTab() {
        beautyTv.setVisibility(View.INVISIBLE);
        bigeyeTv.setVisibility(View.INVISIBLE);
        slimmingTv.setVisibility(View.INVISIBLE);
        legsTv.setVisibility(View.INVISIBLE);
    }

    private DrawLineTextView lastView;

    public void onClick(View view) {
        if (lastView == view) {
            return;
        }
        if (null != lastView) {
            lastView.setSelected(false);
            lastView.setDrawline(false);
        }
        lastView = (DrawLineTextView) view;
        lastView.setSelected(true);
        lastView.setDrawline(true);

        if (view == filterTv) {
            selectType.set(ON_CLICK_FILTER);
            listener.onTabClick(ON_CLICK_FILTER, CLICK_INNER);
        } else if (view == beautyTv) {
            selectType.set(ON_CLICK_SKIN);
            listener.onTabClick(ON_CLICK_SKIN, CLICK_INNER);
        } else if (view == bigeyeTv) {
            selectType.set(ON_CLICK_FACE);
            listener.onTabClick(ON_CLICK_FACE, CLICK_INNER);
        } else if (view == slimmingTv) {
            selectType.set(ON_CLICK_SLIMMING);
            listener.onTabClick(ON_CLICK_SLIMMING, CLICK_INNER);
        } else if (view == legsTv) {
            selectType.set(ON_CLICK_LONG_LEGS);
            listener.onTabClick(ON_CLICK_LONG_LEGS, CLICK_INNER);
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
        selectType.set(tabSelectPosition);
        if (null != lastView) {
            lastView.setSelected(false);
            lastView.setDrawline(false);
        }
        switch (tabSelectPosition) {
            case ON_CLICK_FILTER:
                lastView = filterTv;
                break;
            case ON_CLICK_SKIN:
                lastView = beautyTv;
                break;
            case ON_CLICK_FACE:
                lastView = bigeyeTv;
                break;
            case ON_CLICK_SLIMMING:
                lastView = slimmingTv;
                break;
            case ON_CLICK_LONG_LEGS:
                lastView = legsTv;
                break;
        }
        if (lastView != null) {
            lastView.setSelected(true);
            lastView.setDrawline(true);
        }
        if (tabSelectPosition == MomentFilterPanelTabLayout.ON_CLICK_SKIN || tabSelectPosition == MomentFilterPanelTabLayout.ON_CLICK_FACE || tabSelectPosition == MomentFilterPanelTabLayout.ON_CLICK_FILTER) {
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
