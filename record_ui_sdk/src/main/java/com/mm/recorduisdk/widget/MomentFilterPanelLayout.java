package com.mm.recorduisdk.widget;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;
import com.mm.recorduisdk.base.cement.SimpleCementAdapter;
import com.mm.recorduisdk.recorder.listener.OnFilterDensityChangeListener;
import com.mm.recorduisdk.recorder.model.MomentFilterItemModel;
import com.mm.recorduisdk.widget.decoration.LinearPaddingItemDecoration;
import com.mm.recorduisdk.widget.recyclerview.layoutmanager.LinearLayoutManagerWithSmoothScroller;
import com.mm.recorduisdk.widget.seekbar.OnSeekChangeListener;
import com.mm.recorduisdk.widget.seekbar.SeekParams;
import com.mm.recorduisdk.widget.seekbar.TickSeekBar;
import com.momo.mcamera.filtermanager.MMPresetFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiabn on 2017/7/13.
 * 滤镜的相关处理
 */
public class MomentFilterPanelLayout extends MomentSkinAndFacePanelLayout {

    private RecyclerView panelFilterRecView;
    private SimpleCementAdapter panelFilterAdapter;
    //普通滤镜的item
    //获取方式：1、点击onClick 正向获取 2、通过上下滑动反向获取
    private MomentFilterItemModel filterItemModel;
    //上一次命中的滤镜
    private MomentFilterItemModel lastFilterItemModel;
    private List mPresetModels = new ArrayList();

    private int filterSelectPos = 0;
    private View mLlFilterDensity;
    private TickSeekBar mFilterDensitySeekbar;
    private OnFilterDensityChangeListener mFilterDensityChangeListener;

    public MomentFilterPanelLayout(Context context) {
        super(context);
    }

    public MomentFilterPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MomentFilterPanelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initFilterPanel();
    }

    private void initFilterPanel() {
        panelFilterRecView = this.findViewById(R.id.filter_drawer_main_panel);
        panelFilterRecView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller
                (this.context, LinearLayoutManager.HORIZONTAL, false));
        panelFilterRecView.setHasFixedSize(true);
        panelFilterAdapter = new SimpleCementAdapter();
        panelFilterRecView.setItemAnimator(null);

        panelFilterAdapter.setOnItemClickListener(new SimpleCementAdapter.OnItemClickListener() {
            @Override
            public void onClick(@NonNull View itemView, @NonNull CementViewHolder viewHolder, int position, @NonNull CementModel<?> model) {
                handleFilterSelect(position);
            }
        });

        panelFilterRecView.addItemDecoration(new LinearPaddingItemDecoration(UIUtils.getPixels(0), UIUtils.getPixels(0), UIUtils.getPixels(15)));
        panelFilterRecView.setAdapter(panelFilterAdapter);

        mLlFilterDensity = findViewById(R.id.ll_filter_density);
        mFilterDensitySeekbar = mLlFilterDensity.findViewById(R.id.sb_filter_density_seekbar);
        mFilterDensitySeekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                if (mFilterDensityChangeListener != null) {
                    mFilterDensityChangeListener.onFilterDensityChange((int) seekParams.progress);
                }
            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {

            }
        });
    }

    public void changeFilterDensityProgress(@IntRange(from = 0, to = 100) int progress) {
        if (mFilterDensitySeekbar != null) {
            mFilterDensitySeekbar.setProgress(progress);
        }
    }

    @Override
    protected void selectFilter() {
        super.selectFilter();
        if (panelFilterRecView != null && panelFilterRecView.getVisibility() == GONE) {
            panelFilterRecView.setVisibility(VISIBLE);
        }
        if (mLlFilterDensity != null && mLlFilterDensity.getVisibility() != GONE) {
            mLlFilterDensity.setVisibility(GONE);
        }
    }

    @Override
    protected void handleMoreClick() {
        super.handleMoreClick();
        if (lastTabPos == MomentFilterPanelTabLayout.ON_CLICK_FILTER) {
            if (panelFilterRecView.getVisibility() == VISIBLE) {
                panelFilterRecView.setVisibility(GONE);
                mLlFilterDensity.setVisibility(VISIBLE);

            } else {
                panelFilterRecView.setVisibility(VISIBLE);
                mLlFilterDensity.setVisibility(GONE);
            }
        } else {
            mLlFilterDensity.setVisibility(GONE);
        }
    }

    @Override
    protected void selectSkinAndFace() {
        super.selectSkinAndFace();
        if (panelFilterRecView != null && panelFilterRecView.getVisibility() == VISIBLE) {
            panelFilterRecView.setVisibility(GONE);
        }
        if (mLlFilterDensity != null && mLlFilterDensity.getVisibility() == VISIBLE) {
            mLlFilterDensity.setVisibility(GONE);
        }

    }

    //外层最终实现滤镜效果
    protected void handleFilterSelect(int position) {
        if (filterSelectPos != position) {
            mFilterDensitySeekbar.setProgress(100);
        }
        filterSelectPos = position;
        if (selectListener != null) {
            selectListener.onFilterTabSelect(position);
        }
        showFilterBg(position);
    }

    //for outter
    public void showSwitchSelect(int switchIndex) {
        showFilterBg(switchIndex);
    }

    //    public void loadData(List<MMPresetFilter> mmPresetFilters, int filterSelectPos, int filterBeautySelectPos, int filterEyeThinSelectPos) {
    //        this.filterSelectPos = filterSelectPos;
    //        this.filterBeautySelectPos = filterBeautySelectPos;
    //        this.filterEyeThinSelectPos = filterEyeThinSelectPos;
    //        onTabChanged(0);
    //        initFilterData(mmPresetFilters);
    //    }

    public void switchTabPanel(int tabPanelPosition, List<MMPresetFilter> mmPresetFilters, int filterSelectPos,
                               int filterBeautySelectPos, int filterEyeThinSelectPos, int filterSlimmingSelectPos, int filterLongLegsSelectPos) {
        this.filterSelectPos = filterSelectPos;
        this.filterBeautySelectPos = filterBeautySelectPos;
        this.filterEyeThinSelectPos = filterEyeThinSelectPos;
        this.filterSlimmingSelectPos = filterSlimmingSelectPos;
        this.filterLongLegsSelectPos = filterLongLegsSelectPos;
        onTabChanged(tabPanelPosition);  // 切换到美颜、瘦身布局
        initFilterData(mmPresetFilters);
        if (tabPanelPosition != 0) {
            initSkinAndFaceData(filterEyeThinSelectPos);
        }

    }

    protected void initFilterData(List mmPresetFilters) {
        if (mmPresetFilters != null && mmPresetFilters.size() > 0) {
            mPresetModels = transFilter2Models(mmPresetFilters);
            panelFilterAdapter.updateDataList(mPresetModels);
        }
        handleFilterSelect(filterSelectPos);
    }

    private void showFilterBg(int index) {
        if (index < panelFilterAdapter.getItemCount() && index >= 0) {
            filterItemModel = (MomentFilterItemModel) panelFilterAdapter.getModel(index);
            if (filterItemModel != null && filterItemModel != lastFilterItemModel) {
                filterItemModel.showFilterBg(true);
                if (lastFilterItemModel != null) {
                    lastFilterItemModel.showFilterBg(false);
                    panelFilterAdapter.notifyModelChanged(lastFilterItemModel);
                }
                panelFilterAdapter.notifyModelChanged(filterItemModel);
                panelFilterRecView.scrollToPosition(index);
                lastFilterItemModel = filterItemModel;
            }
        }
    }

    private List<CementModel<?>> transFilter2Models(List list) {
        List<CementModel<?>> models = new ArrayList<>();
        for (Object object : list) {
            if (object instanceof MMPresetFilter) {
                models.add(new MomentFilterItemModel((MMPresetFilter) object));
            }
        }
        return models;
    }

    public int getFilterPos() {
        return filterSelectPos;
    }

    public OnFilterDensityChangeListener getFilterDensityChangeListener() {
        return mFilterDensityChangeListener;
    }

    public void setFilterDensityChangeListener(OnFilterDensityChangeListener filterDensityChangeListener) {
        this.mFilterDensityChangeListener = filterDensityChangeListener;
    }
}
