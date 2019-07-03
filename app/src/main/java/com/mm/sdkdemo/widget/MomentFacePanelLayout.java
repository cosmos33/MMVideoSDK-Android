package com.mm.sdkdemo.widget;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.task.MomoMainThreadExecutor;
import com.mm.mmutil.toast.Toaster;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.cement.CementAdapter;
import com.mm.sdkdemo.base.cement.CementModel;
import com.mm.sdkdemo.base.cement.CementViewHolder;
import com.mm.sdkdemo.base.cement.SimpleCementAdapter;
import com.mm.sdkdemo.bean.IMomentFacePresenter;
import com.mm.sdkdemo.bean.MomentFace;
import com.mm.sdkdemo.bean.MomentFacePanelElement;
import com.mm.sdkdemo.recorder.helper.MomentFaceUtil;
import com.mm.sdkdemo.recorder.model.MomentFaceItemModel;
import com.mm.sdkdemo.utils.BounceInAnimator;
import com.mm.sdkdemo.widget.recyclerview.layoutmanager.GridLayoutManagerWithSmoothScroller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 变脸面板         <br/>
 * <br/>
 * 该类只处理和UI相关的逻辑，其他逻辑不放在这，需要使用，请用{@link MomentFacePanelElement}
 * 配合{@link android.view.ViewStub}进行懒加载使用，因为该类比较重       <br/>
 * <br/>
 * Created by momo on 2017/5/10.
 */

public class MomentFacePanelLayout extends FrameLayout implements IMomentFaceView {

    //变脸面板
    RecyclerView facePanelRecView;
    Context context;
    private CementAdapter mFacePanelAdapter;

    private IndeterminateDrawable progressDrawable;
    private View progressView;
    private TextView errorTipView;

    //记录选中的face model
    private MomentFaceItemModel mSelectedItemModel;

    private IMomentFacePresenter mPresenter;

    private OnFaceResourceSelectListener mOnFaceResourceSelectListener;

    // 因为数据还没拉取下来，所以，需要先预约，等数据初始化完之后再帮你选~
    private MomentFace mScheduledMomentFace;
    // 当前选中的所有models
    private ArrayMap<String, List<MomentFaceItemModel>> allModels = new ArrayMap<>();
    private View mTvPanelRecord;
    private OnClickListener mOnPanelRecordBtnClickListener;
    private Map<String, List<MomentFace>> mData;
    private View mTvStickerTab;
    private View mTvPropTab;
    private int mCurrentTabIndex = 0;

    public MomentFacePanelLayout(Context context) {
        this(context, null);
    }

    public MomentFacePanelLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentFacePanelLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MomentFacePanelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_face_panel_layout, this);
        initViews();
        initEvent();
    }

    public void setPanelRecordBtnClickListener(OnClickListener onClickListener) {
        mOnPanelRecordBtnClickListener = onClickListener;
    }


    private void initEvent() {
        mTvPanelRecord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPanelRecordBtnClickListener != null) {
                    mOnPanelRecordBtnClickListener.onClick(v);
                }
            }
        });
        mTvStickerTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTvStickerTab.isSelected()) {
                    selectTab(mTvStickerTab);
                }
            }
        });

        mTvPropTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTvPropTab.isSelected()) {
                    selectTab(mTvPropTab);
                }
            }
        });

    }

    private void initViews() {
        facePanelRecView = findViewById(R.id.face_panel);
        mTvPanelRecord = findViewById(R.id.iv_panel_record);
        mTvStickerTab = findViewById(R.id.tv_sticker_tab);
        mTvPropTab = findViewById(R.id.tv_prop_tab);
        mTvStickerTab.setSelected(true);

        initFacepanelView();
        progressDrawable = new IndeterminateDrawable(Color.WHITE, UIUtils.getPixels(3));
        progressView = new View(context);
        progressView.setBackgroundDrawable(progressDrawable);
        int size = UIUtils.getPixels(64f);
        LayoutParams progressLP = new LayoutParams(size, size);
        progressLP.gravity = Gravity.CENTER;
        addView(progressView, progressLP);

        selectTab(mTvStickerTab);
    }


    private void selectTab(View view) {
        if (mData == null) {
            return;
        }

        mTvStickerTab.setSelected(false);
        mTvPropTab.setSelected(false);
        List<MomentFace> dataList = null;
        List<MomentFaceItemModel> modelList = null;
        String key = null;
        if (view == mTvStickerTab) {
            mTvStickerTab.setSelected(true);
            key = "sticker";
            mCurrentTabIndex = 0;
        } else {
            mTvPropTab.setSelected(true);
            key = "prop";
            mCurrentTabIndex = 1;

        }
        modelList = allModels.get(key);
        if (modelList == null) {
            dataList = mData.get(key);
        }
        if (modelList != null) {
            mFacePanelAdapter.replaceAllModels(modelList);
        } else if (dataList != null) {
            modelList = new ArrayList<>(dataList.size());
            for (MomentFace face : dataList) {
                MomentFaceItemModel model = new MomentFaceItemModel(face);
                modelList.add(model);
            }
            allModels.put(key, modelList);
            mFacePanelAdapter.replaceAllModels(modelList);
        }
    }

    @Override
    public void attachPresenter(IMomentFacePresenter presenter) {
        this.mPresenter = presenter;
    }

    /**
     * 初始化变脸面板
     */
    private void initFacepanelView() {
        final int spanCount = 5;
        final int spacing = (UIUtils.getScreenWidth() - spanCount * UIUtils.getDimensionPixelSize(R.dimen.moment_face_panel_width)) / 3;
        facePanelRecView.setLayoutManager(new GridLayoutManagerWithSmoothScroller(context, spanCount));
        facePanelRecView.setHasFixedSize(true);
        facePanelRecView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position
                int column = position % spanCount;

                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
            }
        });
        mFacePanelAdapter = new SimpleCementAdapter();

        mFacePanelAdapter.replaceAllModels(new ArrayList<CementModel<?>>());
        facePanelRecView.setItemAnimator(null);
        mFacePanelAdapter.setOnItemClickListener(new CementAdapter.OnItemClickListener() {
            @Override
            public void onClick(@NonNull View itemView, @NonNull CementViewHolder viewHolder, int position, @NonNull CementModel<?> model) {
                MomentFace face = ((MomentFaceItemModel) model).getFace();

                if (face.isEmptyFace()) {
                    clearFace(((MomentFaceItemModel.ViewHolder) viewHolder).getIconView());
                } else {
                    playBoundAnim(((MomentFaceItemModel.ViewHolder) viewHolder).getIconView());
                    // face点击事件
                    if (((MomentFaceItemModel) model).isSelected()||MomentFaceUtil.isOnDownloadTask(face)) {
                        return;
                    }
                    if (mOnFaceResourceSelectListener != null) {
                        mOnFaceResourceSelectListener.onSelected(face);
                    }
                }
                Set<String> keys = allModels.keySet();
                for (String key : keys) {
                    List<MomentFaceItemModel> momentFaceItemModels = allModels.get(key);
                    for (MomentFaceItemModel momentFaceItemModel : momentFaceItemModels) {
                        momentFaceItemModel.setSelected(false);
                    }
                }

                allModels.valueAt(mCurrentTabIndex).get(position).setSelected(true);
                mFacePanelAdapter.notifyDataSetChanged();
            }

        });
        facePanelRecView.setAdapter(mFacePanelAdapter);
    }

    private void showProgress(boolean show) {
        if (show) {
            progressView.setVisibility(View.VISIBLE);
            progressDrawable.startProgress();
        } else {
            progressDrawable.stopProgress();
            progressView.setVisibility(View.GONE);
        }
    }

    private void playBoundAnim(View view) {
        BounceInAnimator bounce = new BounceInAnimator();
        bounce.setDuration(300);
        bounce.getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f, 1.1f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f, 1.1f, 1f)
        );
        bounce.start();
    }

    @Override
    public void onFaceDataLoadSuccess(Map<String, List<MomentFace>> data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        mData = data;
        // 更新UI
        if (errorTipView != null) {
            errorTipView.setVisibility(View.GONE);
        }
        showProgress(false);
        facePanelRecView.setVisibility(VISIBLE);

        // 初始化选中的变脸项
        //        scrollToPosition(1, 0, null, true);

        selectTab(mTvStickerTab);
    }

    @Override
    public void onFaceDataLoadFailed() {
        if (errorTipView == null) {
            errorTipView = new TextView(getContext());
            errorTipView.setTextColor(Color.WHITE);
            errorTipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f);
            errorTipView.setText("加载失败，点击重试");
            errorTipView.setGravity(Gravity.CENTER);

            LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            addView(errorTipView, lp);
            errorTipView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (errorTipView != null) {
                        errorTipView.setVisibility(View.GONE);
                    }
                    mPresenter.loadFaceData();
                }
            });
        }
        if (facePanelRecView != null)
            facePanelRecView.setVisibility(INVISIBLE);
        errorTipView.setVisibility(View.VISIBLE);
        showProgress(false);
    }

    public boolean isSelectedItem(MomentFace face) {
        return face != null && mSelectedItemModel != null && TextUtils.equals(mSelectedItemModel.getFace().getId(), face.getId());
    }

    public void setOnFaceResourceSelectListener(OnFaceResourceSelectListener pOnFaceResourceSelectListener) {
        mOnFaceResourceSelectListener = pOnFaceResourceSelectListener;
    }

    @Override
    public void showLoadingView() {
        showProgress(true);
    }

    @Override
    public void showDownLoadFailedTip() {
        Toaster.showInvalidate("下载失败，请重试");
    }

    @Override
    public void notifyItemChanged(MomentFace face) {
        Set<String> keys = allModels.keySet();
        for (String key : keys) {
            List<MomentFaceItemModel> momentFaceItemModels = allModels.get(key);
            for (final MomentFaceItemModel momentFaceItemModel : momentFaceItemModels) {
                if (momentFaceItemModel.getFace() == face) {
                    if(Looper.myLooper()!=null&&Looper.myLooper()==Looper.getMainLooper()){
                        mFacePanelAdapter.notifyModelChanged(momentFaceItemModel);
                    }else {
                        MomoMainThreadExecutor.post(new Runnable() {
                            @Override
                            public void run() {
                                mFacePanelAdapter.notifyModelChanged(momentFaceItemModel);
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public boolean hasSelectItem() {
        return mSelectedItemModel != null;
    }

    private void clearFace(View view) {
        playBoundAnim(view);
        if (mOnFaceResourceSelectListener != null) {
            mOnFaceResourceSelectListener.onClear();
        }
    }

    public interface OnFaceResourceSelectListener {
        /**
         * 点击某一个变脸选项时
         *
         * @param face 素材信息
         */
        void onSelected(MomentFace face);

        void onClear();
    }

    /**
     * 辅助定位变脸的接口定义
     */
    public interface LocateHelper {
        /**
         * 过滤某一个分类时
         *
         * @return true 跳过该类别的搜索，false 不跳过
         */
        boolean isSkipClass(String faceClassId);
    }

}
