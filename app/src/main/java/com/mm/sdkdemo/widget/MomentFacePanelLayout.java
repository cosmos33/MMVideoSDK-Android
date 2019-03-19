package com.mm.sdkdemo.widget;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mm.mmutil.toast.Toaster;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.cement.CementAdapter;
import com.mm.sdkdemo.base.cement.CementModel;
import com.mm.sdkdemo.base.cement.CementViewHolder;
import com.mm.sdkdemo.base.cement.SimpleCementAdapter;
import com.mm.sdkdemo.bean.IMomentFacePresenter;
import com.mm.sdkdemo.bean.MomentFace;
import com.mm.sdkdemo.bean.MomentFacePanelElement;
import com.mm.sdkdemo.recorder.model.MomentFaceItemModel;
import com.mm.sdkdemo.utils.BounceInAnimator;
import com.mm.sdkdemo.widget.recyclerview.layoutmanager.GridLayoutManagerWithSmoothScroller;

import java.util.ArrayList;
import java.util.List;

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
    private List<MomentFaceItemModel> allModels = new ArrayList<>(5);

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
    }

    private void initViews() {
        facePanelRecView = (RecyclerView) findViewById(R.id.face_panel);
        initFacepanelView();
        progressDrawable = new IndeterminateDrawable(Color.WHITE, UIUtils.getPixels(3));
        progressView = new View(context);
        progressView.setBackgroundDrawable(progressDrawable);
        int size = UIUtils.getPixels(64f);
        LayoutParams progressLP = new LayoutParams(size, size);
        progressLP.gravity = Gravity.CENTER;
        addView(progressView, progressLP);
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
                if (position == 0) {
                    clearFace(((MomentFaceItemModel.ViewHolder) viewHolder).getIconView());
                } else {
                    playBoundAnim(((MomentFaceItemModel.ViewHolder) viewHolder).getIconView());
                    // face点击事件
                    MomentFace face = ((MomentFaceItemModel) model).getFace();
                    if (((MomentFaceItemModel) model).isSelected()) {
                        return;
                    }
                    if (mOnFaceResourceSelectListener != null) {
                        mOnFaceResourceSelectListener.onSelected(face);
                    }
                }
                for (MomentFaceItemModel model1 : allModels) {
                    model1.setSelected(false);
                }
                allModels.get(position).setSelected(true);
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
    public void onFaceDataLoadSuccess(List<MomentFace> data) {
        if (data == null || data.isEmpty()) {
            return;
        }

        // 更新UI
        if (errorTipView != null) {
            errorTipView.setVisibility(View.GONE);
        }
        showProgress(false);
        facePanelRecView.setVisibility(VISIBLE);

        // 初始化选中的变脸项
        //        scrollToPosition(1, 0, null, true);

        allModels.clear();
        for (MomentFace face : data) {
            MomentFaceItemModel model = new MomentFaceItemModel(face);
            if (face.isEmptyFace()) {
                model.setSelected(true);
            }
            allModels.add(model);
        }
        mFacePanelAdapter.replaceAllModels(allModels);
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
        for (MomentFaceItemModel model : allModels) {
            if (model.getFace() == face) {
                mFacePanelAdapter.notifyModelChanged(model);
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
