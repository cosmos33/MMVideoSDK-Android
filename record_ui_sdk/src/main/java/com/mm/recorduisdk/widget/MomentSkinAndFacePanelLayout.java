package com.mm.recorduisdk.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;
import com.mm.recorduisdk.base.cement.SimpleCementAdapter;
import com.mm.recorduisdk.config.Configs;
import com.mm.recorduisdk.recorder.helper.VideoPanelFaceAndSkinManager;
import com.mm.recorduisdk.recorder.listener.FilterSelectListener;
import com.mm.recorduisdk.recorder.model.MomentFilterEditFaceItemModel;
import com.mm.recorduisdk.widget.recyclerview.layoutmanager.GridLayoutManagerWithSmoothScroller;
import com.mm.recorduisdk.widget.seekbar.OnSeekChangeListener;
import com.mm.recorduisdk.widget.seekbar.SeekParams;
import com.mm.recorduisdk.widget.seekbar.TickSeekBar;
import com.momo.xeengine.lightningrender.ILightningRender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiabin on 2017/7/13.
 * 美颜、大眼瘦脸相关处理
 */

public class MomentSkinAndFacePanelLayout extends RelativeLayout {

    public static final int TYPE_BEAUTY = 1;
    public static final int TYPE_EYE_AND_THIN = 2;
    public static final int TYPE_SLIMMING = 3;
    public static final int TYPE_LONG_LEGS = 4;
    public static final int TYPE_MICROBEAUTY = 5;
    public static final int TYPE_MAKEUP = 6;
    public static final int TYPE_MAKEUP_LUT = 7;

    private static final int COLUMNS = 6;

    protected Context context;
    MomentFilterPanelTabLayout tabLayout;
    private View tabMoreContainer;

    private SimpleCementAdapter panelSkinAndFaceAdapter;
    private RecyclerView panelSkinAndFaceRecView;

    private MomentFilterEditFaceItemModel skinAndFaceItemModel;
    //上一次选中的美颜、瘦脸item
    private MomentFilterEditFaceItemModel lastSkinAndFaceItemModel;

    private List mSkinAndFaceList = new ArrayList();
    private List microBeautyList = new ArrayList();
    private List makeupList = new ArrayList();
    protected FilterSelectListener selectListener;

    //首次进入默认的2来自loadData初始化
    protected int filterBeautySelectPos;
    protected int filterEyeThinSelectPos;

    protected int filterSlimmingSelectPos = 0;
    protected int filterLongLegsSelectPos = 0;
    protected int makeupSelectPos = 0;
    protected int microBeautySelectPos = 0;
    private BeautyAdapterData currentData;


    public MomentSkinAndFacePanelLayout(Context context) {
        super(context);
        init(context);
    }

    public MomentSkinAndFacePanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MomentSkinAndFacePanelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_filter_drawer_panel_layout, this);
    }

    public void setFilterSelectListener(FilterSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public void setFilterSelectListener(FilterSelectListener selectListener, boolean hideFace) {
        this.selectListener = selectListener;
        if (hideFace) {
            tabLayout.hideOtherTab();
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
    }

    private void initViews() {
        initTabs();
        initMoreLayout();
        initFilterEditFacePanel();
    }

    private void initTabs() {
        filterBeautySelectPos = Configs.DEFAULT_BEAUTY;
        filterEyeThinSelectPos = Configs.DEFAULT_BIG_EYE;
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setOnTabClickListener(new MomentFilterPanelTabLayout.OnTabClickListener() {
            @Override
            public void onTabClick(int position, int flag) {
                switch (position) {
                    case MomentFilterPanelTabLayout.ON_CLICK_FILTER:
                        break;
                    case MomentFilterPanelTabLayout.ON_CLICK_SKIN:
                        initSkinAndFaceData(filterBeautySelectPos);
                        break;
                    case MomentFilterPanelTabLayout.ON_CLICK_FACE:
                        initSkinAndFaceData(filterEyeThinSelectPos);
                        break;
                    case MomentFilterPanelTabLayout.ON_CLICK_SLIMMING:
                        initSkinAndFaceData(filterSlimmingSelectPos);
                        break;
                    case MomentFilterPanelTabLayout.ON_CLICK_LONG_LEGS:
                        initSkinAndFaceData(filterLongLegsSelectPos);
                        break;
                    case MomentFilterPanelTabLayout.ON_CLICK_MAKEUP:
                        initMakeupData(makeupSelectPos);
                        break;
                    case MomentFilterPanelTabLayout.ON_CLICK_MICRO_BEAUTY:
                        initMicroBeautyData(microBeautySelectPos);
                        break;
                    default:
                        break;
                }
                if (flag == MomentFilterPanelTabLayout.CLICK_INNER) {
                    onTabChanged(position);
                }

            }
        });

        tabLayout.setonMoreViewClickListener(new MomentFilterPanelTabLayout.OnMoreViewClickListener() {
            @Override
            public void onMoreViewClick(View view) {
                handleMoreClick(currentData);
            }
        });
    }

    private TextView titleTv1, titleTv2;
    private TickSeekBar seekBar1, seekBar2;

    private void initMoreLayout() {
        tabMoreContainer = findViewById(R.id.tab_more_layout);

        titleTv1 = tabMoreContainer.findViewById(R.id.more_layout_subtitle1);
        titleTv2 = tabMoreContainer.findViewById(R.id.more_layout_subtitle2);
        seekBar1 = tabMoreContainer.findViewById(R.id.more_layout_seekbar1);
        seekBar2 = tabMoreContainer.findViewById(R.id.more_layout_seekbar2);
        seekBar1.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                float[] value = new float[2];
                value[0] = seekBar1.getProgress() / 100f;
                value[1] = seekBar2.getProgress() / 100f;
                if (selectListener != null) {
                    selectListener.onBeautyMoreChanged(value, tabLayout.getCurrentSelected().get(), currentData);
                }
                if (lastTabPos == MomentFilterPanelTabLayout.ON_CLICK_SKIN) {
                    mopiPercent = (int) seekParams.progress;
                } else if (lastTabPos == MomentFilterPanelTabLayout.ON_CLICK_FACE) {
                    dayanPercent = (int) seekParams.progress;
                } else if (lastTabPos == MomentFilterPanelTabLayout.ON_CLICK_MICRO_BEAUTY) {
                    currentData.intensity = seekParams.progress;
                } else if (lastTabPos == MomentFilterPanelTabLayout.ON_CLICK_MAKEUP) {
                    currentData.intensity = seekParams.progress;
                }
            }

            @Override
            public void onStartTrackingTouch(TickSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(TickSeekBar seekBar) {

            }
        });
        seekBar2.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                float[] value = new float[2];
                value[0] = seekBar1.getProgress() / 100f;
                value[1] = seekBar2.getProgress() / 100f;
                if (selectListener != null) {
                    int type = tabLayout.getCurrentSelected().get();
                    if (type == MomentSkinAndFacePanelLayout.TYPE_MAKEUP) {
                        type = TYPE_MAKEUP_LUT;
                    }
                    selectListener.onBeautyMoreChanged(value, type, currentData);
                }

                if (lastTabPos == MomentFilterPanelTabLayout.ON_CLICK_SKIN) {
                    meibaiPercent = (int) seekParams.progress;
                } else if (lastTabPos == MomentFilterPanelTabLayout.ON_CLICK_FACE) {
                    shoulianPercent = (int) seekParams.progress;
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

    private void initFilterEditFacePanel() {
        panelSkinAndFaceRecView = this.findViewById(R.id.filter_drawer_edit_face_panel);
        panelSkinAndFaceRecView.setLayoutManager(new GridLayoutManagerWithSmoothScroller(context, COLUMNS));
        panelSkinAndFaceRecView.setHasFixedSize(true);
        panelSkinAndFaceAdapter = new SimpleCementAdapter();
        panelSkinAndFaceRecView.setItemAnimator(null);
        panelSkinAndFaceAdapter.setOnItemClickListener(new SimpleCementAdapter.OnItemClickListener() {
            @Override
            public void onClick(@NonNull View itemView, @NonNull CementViewHolder viewHolder, int position, @NonNull CementModel<?> model) {
                handleBeautySelect(position, ((MomentFilterEditFaceItemModel) model).getData());
                showFaceAndSkinBg(position);
            }
        });
        panelSkinAndFaceRecView.setAdapter(panelSkinAndFaceAdapter);
    }

    //外层最终实现美颜效果
    private void handleBeautySelect(int selectIndex, BeautyAdapterData data) {
        currentData = data;
        if (selectListener != null) {
            selectListener.onBeautyTabSelect(selectIndex, tabLayout.getCurrentSelected().get(), data);
        }
    }

    //普通美颜 大眼/瘦脸 瘦身 长腿共用一套数据
    protected void initSkinAndFaceData(int index) {
        if (mSkinAndFaceList == null || mSkinAndFaceList.size() <= 0) {
            mSkinAndFaceList = transInteger2Models(VideoPanelFaceAndSkinManager.getInstance().getFaceEditType());
        }
        panelSkinAndFaceAdapter.updateDataList(mSkinAndFaceList);

        showFaceAndSkinBg(index);
        handleBeautySelect(index, ((MomentFilterEditFaceItemModel) mSkinAndFaceList.get(0)).getData());
    }

    //微整形
    protected void initMicroBeautyData(int index) {
        if (microBeautyList == null || microBeautyList.size() <= 0) {
            microBeautyList = transInteger2Models(VideoPanelFaceAndSkinManager.getInstance().getMicroBeautyData());
        }
        panelSkinAndFaceAdapter.updateDataList(microBeautyList);

        showFaceAndSkinBg(index);
        handleBeautySelect(index, ((MomentFilterEditFaceItemModel) microBeautyList.get(microBeautySelectPos)).getData());
    }

    //美妆
    protected void initMakeupData(int index) {
        if (makeupList == null || makeupList.size() <= 0) {
            makeupList = transInteger2Models(VideoPanelFaceAndSkinManager.getInstance().getMakeupData());
        }
        panelSkinAndFaceAdapter.updateDataList(makeupList);

        showFaceAndSkinBg(index);
        handleBeautySelect(index, ((MomentFilterEditFaceItemModel) makeupList.get(makeupSelectPos)).getData());
    }

    protected void selectFilter() {
        if (panelSkinAndFaceRecView != null && panelSkinAndFaceRecView.getVisibility() == VISIBLE) {
            panelSkinAndFaceRecView.setVisibility(GONE);
        }
    }

    protected void selectSkinAndFace() {
        if (panelSkinAndFaceRecView != null && panelSkinAndFaceRecView.getVisibility() == GONE) {
            panelSkinAndFaceRecView.setVisibility(VISIBLE);
        }
    }

    // tap页切换
    protected void onTabChanged(int index) {
        tabMoreContainer.setVisibility(View.GONE);
        switch (index) {
            case MomentFilterPanelTabLayout.ON_CLICK_FILTER:
                selectFilter();
                break;
            case MomentFilterPanelTabLayout.ON_CLICK_SKIN:
            case MomentFilterPanelTabLayout.ON_CLICK_FACE:
            case MomentFilterPanelTabLayout.ON_CLICK_SLIMMING:
            case MomentFilterPanelTabLayout.ON_CLICK_LONG_LEGS:
            case MomentFilterPanelTabLayout.ON_CLICK_MICRO_BEAUTY:
            case MomentFilterPanelTabLayout.ON_CLICK_MAKEUP:
                selectSkinAndFace();
                break;
            default:
                break;
        }
        changeTabSelected(index);
    }

    protected int lastTabPos;

    private void changeTabSelected(int tabPosition) {
        tabLayout.setSelectTab(tabPosition);
        lastTabPos = tabPosition;
        if (lastTabPos != MomentFilterPanelTabLayout.ON_CLICK_SKIN && lastTabPos != MomentFilterPanelTabLayout.ON_CLICK_FACE) {
            tabMoreContainer.setVisibility(View.GONE);
        }
    }

    private int mopiPercent;
    private int meibaiPercent;
    private int dayanPercent;
    private int shoulianPercent;

    protected void handleMoreClick(BeautyAdapterData data) {
        if (lastTabPos != MomentFilterPanelTabLayout.ON_CLICK_SKIN
                && lastTabPos != MomentFilterPanelTabLayout.ON_CLICK_FACE
                && lastTabPos != MomentFilterPanelTabLayout.ON_CLICK_MAKEUP
                && lastTabPos != MomentFilterPanelTabLayout.ON_CLICK_MICRO_BEAUTY) {
            tabMoreContainer.setVisibility(View.GONE);
            return;
        }
        if (tabMoreContainer.getVisibility() == View.VISIBLE) {
            tabMoreContainer.setVisibility(View.GONE);
            panelSkinAndFaceRecView.setVisibility(View.VISIBLE);
        } else {
            panelSkinAndFaceRecView.setVisibility(View.GONE);
            tabMoreContainer.setVisibility(View.VISIBLE);
            if (lastTabPos == MomentFilterPanelTabLayout.ON_CLICK_SKIN) {
                titleTv2.setVisibility(View.VISIBLE);
                seekBar2.setVisibility(View.VISIBLE);
                //美颜，磨皮
                titleTv1.setText("磨皮");
                titleTv2.setText("美白");
                seekBar1.setProgress(mopiPercent);
                seekBar2.setProgress(meibaiPercent);
            } else if (lastTabPos == MomentFilterPanelTabLayout.ON_CLICK_FACE) {
                titleTv2.setVisibility(View.VISIBLE);
                seekBar2.setVisibility(View.VISIBLE);
                //大眼、瘦脸
                titleTv1.setText("大眼");
                titleTv2.setText("瘦脸");
                seekBar1.setProgress(dayanPercent);
                seekBar2.setProgress(shoulianPercent);
            } else if (lastTabPos == MomentFilterPanelTabLayout.ON_CLICK_MICRO_BEAUTY) {
                titleTv2.setVisibility(View.GONE);
                seekBar2.setVisibility(View.GONE);
                titleTv1.setText("浓度");
                seekBar1.setProgress(data.intensity);
            } else if (lastTabPos == MomentFilterPanelTabLayout.ON_CLICK_MAKEUP) {
                if (currentData.beautyInnerType.equals(ILightningRender.IMakeupLevel.MAKEUP_ALL)) {
                    titleTv2.setVisibility(View.VISIBLE);
                    seekBar2.setVisibility(View.VISIBLE);
                    titleTv1.setText("整妆");
                    seekBar1.setProgress(data.intensity);
                    titleTv2.setText("滤镜");
                    seekBar2.setProgress(data.intensity);
                } else {
                    titleTv2.setVisibility(View.GONE);
                    seekBar2.setVisibility(View.GONE);
                    titleTv1.setText("浓度");
                    seekBar1.setProgress(data.intensity);
                }
            }
        }
    }

    private void showFaceAndSkinBg(int index) {
        if (index < panelSkinAndFaceAdapter.getItemCount() && index >= 0) {
            skinAndFaceItemModel = (MomentFilterEditFaceItemModel) panelSkinAndFaceAdapter.getModel(index);
            if (skinAndFaceItemModel != null && skinAndFaceItemModel != lastSkinAndFaceItemModel) {
                skinAndFaceItemModel.showFilterBg(true);
                if (lastSkinAndFaceItemModel != null) {
                    lastSkinAndFaceItemModel.showFilterBg(false);
                    panelSkinAndFaceAdapter.notifyModelChanged(lastSkinAndFaceItemModel);
                }
                panelSkinAndFaceAdapter.notifyModelChanged(skinAndFaceItemModel);
                lastSkinAndFaceItemModel = skinAndFaceItemModel;
            }
            updateSkinAndFacePosition(index);
        }
    }

    private void updateSkinAndFacePosition(int position) {
        // 得到选中的类型
        int selectType = tabLayout.getCurrentSelected().get();
        switch (selectType) {
            case TYPE_BEAUTY:
                filterBeautySelectPos = position;
                mopiPercent = (int) (Configs.DOKI_BEAUTY[filterBeautySelectPos] * 100);
                meibaiPercent = (int) (Configs.DOKI_BEAUTY[filterBeautySelectPos] * 100);
                break;
            case TYPE_EYE_AND_THIN:
                filterEyeThinSelectPos = position;
                dayanPercent = (int) (Configs.DOKI_BIG_EYE[filterBeautySelectPos] * 100);
                shoulianPercent = (int) (Configs.DOKI_THIN_FACE[filterBeautySelectPos] * 100);
                break;
            case TYPE_SLIMMING:
                filterSlimmingSelectPos = position;
                break;
            case TYPE_LONG_LEGS:
                filterLongLegsSelectPos = position;
                break;
            case TYPE_MAKEUP:
                makeupSelectPos = position;
                break;
            case TYPE_MICROBEAUTY:
                microBeautySelectPos = position;
                break;
            default:
                break;
        }
    }

    private List<CementModel<?>> transInteger2Models(List<BeautyAdapterData> list) {
        List<CementModel<?>> models = new ArrayList<>();
        for (BeautyAdapterData object : list) {
            models.add(new MomentFilterEditFaceItemModel(object));
        }
        return models;
    }

    public int getBeautyLevel() {
        return filterBeautySelectPos;
    }

    public int getBigEyeAndThinLevel() {
        return filterEyeThinSelectPos;
    }

    public int getSlimmingLevel() {
        return filterSlimmingSelectPos;
    }

    public int getLongLegsLevel() {
        return filterLongLegsSelectPos;
    }
}
