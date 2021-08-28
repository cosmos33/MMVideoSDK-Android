package com.mm.recorduisdk.moment;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mm.base_business.utils.UIUtils;
import com.mm.mmutil.toast.Toaster;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.CementViewHolder;
import com.mm.recorduisdk.base.cement.SimpleCementAdapter;
import com.mm.recorduisdk.bean.CommonMomentFaceBean;
import com.mm.recorduisdk.bean.FaceClass;
import com.mm.recorduisdk.bean.IMomentFacePresenter;
import com.mm.recorduisdk.bean.MomentFace;
import com.mm.recorduisdk.moment.adapter.FaceClassTabAdapter;
import com.mm.recorduisdk.moment.model.MomentFaceEmptyModel;
import com.mm.recorduisdk.moment.model.MomentFaceItemModel;
import com.mm.recorduisdk.recorder.helper.MomentFaceUtil;
import com.mm.recorduisdk.recorder.listener.OnRecyclerItemClickListener;
import com.mm.recorduisdk.utils.BounceInAnimator;
import com.mm.recorduisdk.widget.IMomentFaceView;
import com.mm.recorduisdk.widget.IndeterminateDrawable;
import com.mm.recorduisdk.widget.recyclerview.layoutmanager.GridLayoutManagerWithSmoothScroller;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by chenxin on 2019/4/19.
 */
public class MomentFacePanelLayout extends FrameLayout implements View.OnClickListener, IMomentFaceView {

    //变脸面板
    RecyclerView facePanelRecView;
    //变脸分类tab
    RecyclerView bottomTabRecView;
    Context context;
    private List<FaceClass> mFaceClassesList;

    private CementAdapter mFacePanelAdapter;

    private IndeterminateDrawable progressDrawable;
    private View progressView;
    private TextView errorTipView;

    private FaceClassTabAdapter mClassTabAdapter;
    //记录选中的face model
    private MomentFaceItemModel mSelectedItemModel;

    private MomentFaceModelsManager mModelsManager;
    private IMomentFacePresenter mPresenter;

    private OnFaceResourceSelectListener mOnFaceResourceSelectListener;

    // 因为数据还没拉取下来，所以，需要先预约，等数据初始化完之后再帮你选~
    private MomentFace mScheduledMomentFace;
    // 当前选中的类别
    private FaceClass mSelectedFaceClass;
    // 辅助定位变脸项的类对象
    private MomentFacePanelLayout.LocateHelper mLocateHelper;
    // 当前选中的所有models
    private List<MomentFaceItemModel> mSelectedModels;
    private int mSelectedClassIndex = -1;
    private String mScheduledMomentFaceTabId;
    private int currentModelsSize;
    private int spanCount = 5;

    public MomentFacePanelLayout(Context context) {
        this(context, null);
    }

    public MomentFacePanelLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MomentFacePanelLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MomentFacePanelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_face_panel_layout, this);
        initViews();
        initListener();
    }

    private void initListener() {
    }

    private void initViews() {
        facePanelRecView = (RecyclerView) findViewById(R.id.face_panel);
        bottomTabRecView = (RecyclerView) findViewById(R.id.face_panel_bottom_slide);
        initFacepanelView();
        initTabView();
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
        final int spanCount = this.spanCount;
        final int spacing = (UIUtils.getScreenWidth() - UIUtils.getPixels(40) - spanCount * UIUtils.getPixels(45)) / 4;
        GridLayoutManagerWithSmoothScroller gridLayoutManagerWithSmoothScroller = new GridLayoutManagerWithSmoothScroller(context, spanCount);
        facePanelRecView.setLayoutManager(gridLayoutManagerWithSmoothScroller);
        facePanelRecView.setHasFixedSize(true);
        facePanelRecView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                // item position
                int column = position % spanCount;

                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;

                outRect.bottom = UIUtils.getPixels(20);


//                if (currentModelsSize == position + 1) {
//                    outRect.bottom = UIUtils.getPixels(50);
//                } else {
//                }

            }
        });
        mFacePanelAdapter = new SimpleCementAdapter();

        mFacePanelAdapter.replaceAllModels(new ArrayList<CementModel<?>>());
        facePanelRecView.setItemAnimator(null);
        mFacePanelAdapter.setOnItemClickListener(new CementAdapter.OnItemClickListener() {
            @Override
            public void onClick(@NonNull View itemView, @NonNull CementViewHolder viewHolder, int position, @NonNull CementModel<?> model) {
                playBoundAnim(((MomentFaceItemModel.ViewHolder) viewHolder).getIconView());
                // face点击事件
                MomentFace face = ((MomentFaceItemModel) model).getFace();

                boolean isGoSelect = false;
                if (((MomentFaceItemModel) model).isSelected()) {
                    return;
                } else if (!MomentFaceUtil.isOnDownloadTask(face)
                        && MomentFaceUtil.simpleCheckFaceResource(face)) {
                    // 没有在下载，并且文件存在
                    setSelectPositionInternal(face, false);
                    isGoSelect = true;
                }
                if (mOnFaceResourceSelectListener != null) {
                    mOnFaceResourceSelectListener.onSelected(face);
                }

                if (!isGoSelect) {
                    mFacePanelAdapter.notifyModelChanged(model);
                }
            }

        });
        facePanelRecView.setAdapter(mFacePanelAdapter);
    }

    /**
     * 设置当前面板选中的变脸选项
     *
     * @param face 素材信息
     */
    public void setSelectedItem(MomentFace face) {
        setSelectedItem(face, true);
    }

    public void setSelectedItem(MomentFace face, boolean scrollToPosition) {
        if (face == null) {
            return;
        }
        if (!isDataInit()) {
            // 如果数据
            this.mScheduledMomentFace = face;
        } else {
            if (isSelectedItem(face)) {
                return;
            }
            setSelectPositionInternal(face, scrollToPosition);
        }
    }

    public boolean setSelectTab(String tabId) {
        if (!isDataInit()) {
            this.mScheduledMomentFaceTabId = tabId;
        } else {
            if (mClassTabAdapter != null) {
                int position = findPosition4TabId(tabId);
                if (position >= 0) {
                    boolean result = mClassTabAdapter.setSelectTab(position);
                    bottomTabRecView.scrollToPosition(position);
                    onTabChanged(position);
                    return result;
                }
            }
        }
        return false;
    }

    private int findPosition4TabId(String tabId) {
        if (mFaceClassesList != null && !mFaceClassesList.isEmpty()) {
            int size = mFaceClassesList.size();
            for (int i = 0; i < size; i++) {
                FaceClass faceClass = mFaceClassesList.get(i);
                if (faceClass != null && TextUtils.equals(tabId, faceClass.getId())) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 清除选中项目
     */
    public void clearSelectedItem() {
        if (mSelectedModels == null) {
            return;
        }
        for (MomentFaceItemModel selectedModel : mSelectedModels) {
            if (selectedModel.isSelected()) {
                selectedModel.setSelected(false);
                mFacePanelAdapter.notifyModelChanged(selectedModel);
            }
        }
    }

    /**
     * 设置定位辅助对象
     */
    public void setLocateHelper(MomentFacePanelLayout.LocateHelper locateHelper) {
        mLocateHelper = locateHelper;
    }

    /**
     * 初始化tab
     */
    private void initTabView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        bottomTabRecView.setLayoutManager(layoutManager);
        bottomTabRecView.setHasFixedSize(true);
        bottomTabRecView.setItemAnimator(null);
        bottomTabRecView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position

                int count = 0;
                if (mFaceClassesList != null) {
                    count = mFaceClassesList.size();
                }

                if (position != count - 1) {
                    outRect.right = UIUtils.getPixels(19);
                    outRect.left = 0;
                } else {
                    outRect.right = UIUtils.getPixels(0);
                    outRect.left = UIUtils.getPixels(0);
                }
            }
        });

        mClassTabAdapter = new FaceClassTabAdapter(mFaceClassesList);
        mClassTabAdapter.setSelectTab(1);
        mClassTabAdapter.setItemClickListener(new OnRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                onTabChanged(pos);
            }
        });
        bottomTabRecView.setAdapter(mClassTabAdapter);
    }

    /**
     * 底部tab切换
     *
     * @param i
     */
    private void onTabChanged(int i) {
        if (mFaceClassesList == null || mFaceClassesList.isEmpty() || i < 0 || mModelsManager == null) {
            return;
        }

        FaceClass faceClass = mFaceClassesList.get(i);
        List<MomentFaceItemModel> datas = mModelsManager.findMomentFaceModels(faceClass.getId());
        this.mSelectedFaceClass = faceClass;

        if (datas == null) {
            //偶现根据clsID获取不到model数据情况，此时展示空数据项
            datas = new ArrayList<>();
        }

//        List<CementModel> list = new ArrayList<>(datas.size()+1);
//        list.addAll(datas);
//        list.add(new MomentFaceEmptyModel());

        mFacePanelAdapter.removeAllModels();
        mFacePanelAdapter.addModels(datas);

        int count = datas.size();
        int emptyCount = count % spanCount == 0 ? 1 : spanCount - count % spanCount + 1;
        for (int j = 0; j < emptyCount; j++) {
            mFacePanelAdapter.addModel(new MomentFaceEmptyModel());
        }
        this.currentModelsSize = datas.size();
        this.mSelectedClassIndex = i;

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

    /**
     * 当前界面的数据时候已经就绪
     *
     * @return true 就绪， false 未就绪
     */
    private boolean isDataInit() {
        return mModelsManager != null;
    }

    private void setSelectPositionInternal(MomentFace face) {
        setSelectPositionInternal(face, true);
    }

    private void setSelectPositionInternal(MomentFace face, boolean scrollToPosition) {
        List<MomentFaceItemModel> models = mModelsManager.findModelsByFace(face);
        if (models == null) {
            return;
        }
        for (MomentFaceItemModel model : models) {
            String classId = model.getFace().getClassId();
            if (mLocateHelper != null && mLocateHelper.isSkipClass(classId)) {
                continue;
            }
            int i, j;
            for (int i1 = 0; i1 < mFaceClassesList.size(); i1++) {
                if (mFaceClassesList.get(i1).getId().equals(classId)) {
                    i = i1;
                    j = mFaceClassesList.get(i1).getFaces().indexOf(model.getFace());
                    scrollToPosition(i, j, model, scrollToPosition);
                }
            }
        }
    }

    private void scrollToPosition(int i, int j, MomentFaceItemModel model, boolean scrollToPosition) {
        if (scrollToPosition) {
            if (i != mSelectedClassIndex) {
                mClassTabAdapter.setSelectTab(i);
                onTabChanged(i);
                i = i <= 2 ? 0 : i; // fix position
                bottomTabRecView.scrollToPosition(i);
            }
            facePanelRecView.scrollToPosition(j);
        }

        clearSelectedItem();

        if (model != null) {
            List<MomentFaceItemModel> models = mModelsManager.findModelsByFace(model.getFace());
            this.mSelectedModels = models;

            if (mSelectedFaceClass != null && mSelectedFaceClass.getId() != null) {
                for (MomentFaceItemModel localModel : models) {
                    localModel.setSelected(true);
                    if (localModel.getFace() != null && mSelectedFaceClass.getId().equals(localModel.getFace().getClassId())) {
                        mFacePanelAdapter.notifyModelChanged(localModel);
                        mSelectedItemModel = localModel;
                    }
                }
            }
        }
    }

    @Override
    public void onFaceDataLoadSuccess(CommonMomentFaceBean data) {
        if (data == null || data.getFaceClasses() == null || data.getFaceClasses().isEmpty()) {
            return;
        }

        // 保存数据
        mModelsManager = new MomentFaceModelsManager(data);
        mModelsManager.setOnItemChangedListener(new MomentFaceModelsManager.OnItemChangedListener() {
            @Override
            public void onChanged(FaceClass faceClass, MomentFace face, MomentFaceItemModel itemModel, List<MomentFaceItemModel> modelList) {
                if (mSelectedFaceClass == faceClass) {
                    // 当前选中的分类的数据集合有改变，刷新UI
                    mFacePanelAdapter.replaceAllModels(modelList);
                }
            }
        });
        mFaceClassesList = data.getFaceClasses();

        // 更新UI
        if (errorTipView != null) {
            errorTipView.setVisibility(View.GONE);
        }
        showProgress(false);
        facePanelRecView.setVisibility(VISIBLE);
        bottomTabRecView.setVisibility(VISIBLE);

        // 为UI设置数据
        mClassTabAdapter.setDatas(mFaceClassesList);

        // 初始化选中的变脸项
        if (mScheduledMomentFace != null) {
            setSelectPositionInternal(mScheduledMomentFace);
        } else if (!TextUtils.isEmpty(mScheduledMomentFaceTabId)) {
            int position = findPosition4TabId(mScheduledMomentFaceTabId);
            if (position >= 0) {
                mClassTabAdapter.setSelectTab(position);
                bottomTabRecView.scrollToPosition(position);
                onTabChanged(position);
            } else {
                scrollToPosition(1, 0, null, true);
            }
        } else {
            scrollToPosition(1, 0, null, true);
        }
    }

    public MomentFaceModelsManager getModelsManager() {
        return mModelsManager;
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
        if (bottomTabRecView != null)
            bottomTabRecView.setVisibility(INVISIBLE);
        errorTipView.setVisibility(View.VISIBLE);
        showProgress(false);
    }

    public boolean isSelectedItem(MomentFace face) {
        return face != null && mSelectedItemModel != null && TextUtils.equals(mSelectedItemModel.getFace().getId(), face.getId());
    }

    @Override
    public void onClick(View v) {
        /*switch (v.getId()) {
            case R.id.rl_face_none:
                if (!isDataInit()) {
                    // 数据没准备好，不响应
                    return;
                }
                playBoundAnim(v);
                clearSelectedItem();
                if (mOnFaceResourceSelectListener != null) {
                    mOnFaceResourceSelectListener.onClear();
                }
                break;
            default:
                break;
        }*/
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
        if (mSelectedFaceClass == null || face == null) {
            return;
        }
        MomentFaceItemModel momentFaceModel = mModelsManager.findMomentFaceModel(mSelectedFaceClass.getId(), face);
        if (momentFaceModel == null) {
            return;
        }
        mFacePanelAdapter.notifyModelChanged(momentFaceModel);
    }

    @Override
    public boolean hasSelectItem() {
        return mSelectedItemModel != null;
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