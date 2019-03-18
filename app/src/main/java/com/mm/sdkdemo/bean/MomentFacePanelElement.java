package com.mm.sdkdemo.bean;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.immomo.mmutil.toast.Toaster;
import com.mm.sdkdemo.bean.element.Element;
import com.mm.sdkdemo.recorder.helper.DownloadFaceCallbackAdapter;
import com.mm.sdkdemo.recorder.helper.MomentFaceDownloadPublisher;
import com.mm.sdkdemo.recorder.helper.MomentFaceUtil;
import com.mm.sdkdemo.widget.MomentFaceDataManager;
import com.mm.sdkdemo.widget.MomentFacePanelLayout;
import com.momo.mcamera.mask.MaskModel;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by chenwangwang on 2018/3/28   <br/>
 * 管理变脸面板的单元        <br/>
 * 注意，使用前，需要先设置{@link MomentFaceDataManager}对象，
 * 使用{@link #setFaceDataManager(MomentFaceDataManager)}接口进行设置          <br/>
 *                                                                          <br/>
 * PS:
 * 外部可以设置变脸面板需要选中的变脸项。但是，因为UI和列表数据并不是在{@link MomentFacePanelElement}对象创建的时候就已经准备好的。
 * 随意这就有一个问题，在设置需要选中的变脸项的时候，UI或者数据没准备好。                 <br/>
 * 目前的解决方案就是在数据准备好的回调中做一次判断{@link #onFaceDataLoadSuccess(List<MomentFace>)}，
 * 在UI准备好之后的回调中也做一次判断{@link #initView()}
 * @see Element
 */
public class MomentFacePanelElement extends Element<ViewStub> implements IMomentFacePresenter {

    // 变脸列表数据 管理/加载 对象
    private MomentFaceDataManager mFaceDataManager;
    // 加载列表的回调对象
    private FaceDataLoadCallback mFaceDataLoadCallback = new FaceDataLoadCallback(this);
    // 资源下载结果回调对象
    private FaceDownloadCallback mFaceDownloadCallback = new FaceDownloadCallback(this);
    // 变脸模型加载到内存回调对象
    private MyMaskLoadCallback mMaskLoadCallback = new MyMaskLoadCallback(this);
    // 一个用来配置面板的辅助类
    private MomentFacePanelHelper mMomentFacePanelHelper;
    // 用来懒加载变脸面板UI
    private SimpleViewStubProxy<MomentFacePanelLayout> mViewProxy;
    // 在面板UI未加载前，用来临时存放要选中的变脸项
    private MomentFace mScheduledFaceItem;
    // 拉取下来的数据列表，如果View还没有inflate，而数据拉取回来了，这个不为空
    private List<MomentFace> mScheduledFaceData;
    // 是否拉取数据列表失败了
    private boolean isFaceDataLoadFailed;
    // 默认需要显示的变脸信息
    private TolerantMoment mTolerantMoment;
    // 标记当前界面的变脸列表数据是否加载完成
    private boolean mIsFaceDataFetched;
    // 监听资源下载成功回调，包括其他界面发出的下载请求
    private MomentFaceDownloadObserver mMomentFaceDownloadObserver = new MomentFaceDownloadObserver(this);

    public MomentFacePanelElement(ViewStub view) {
        super(view);
        this.mViewProxy = new SimpleViewStubProxy<>(view);
        this.mViewProxy.addInflateListener(new SimpleViewStubProxy.OnInflateListener<MomentFacePanelLayout>() {
            @Override
            public void onInflate(MomentFacePanelLayout view) {
                initView();
            }
        });
    }

    /**
     * 初始化UI，该方法在变脸面板懒加载到内存时调用
     */
    private void initView() {
        mViewProxy.getStubView().setOnFaceResourceSelectListener(mMomentFaceSelectListener);
        mViewProxy.getStubView().attachPresenter(this);

        if (mScheduledFaceData != null) {
            mViewProxy.getStubView().onFaceDataLoadSuccess(mScheduledFaceData);
            // 尝试设置预加载的选项
            if (mScheduledFaceItem != null) {
            } else if (mTolerantMoment != null) {
                // 如果数据列表已经拉取回来，找到指定的变脸信息
                final MomentFace momentFace = MomentFaceUtil.findMomentFace((List<MomentFace>) mFaceDataManager.getData(), mTolerantMoment.getFaceId());
                if (momentFace == null) {
                    return;
                }
            }
        } else if (isFaceDataLoadFailed) {
            mViewProxy.getStubView().onFaceDataLoadFailed();
        } else {
            mViewProxy.getStubView().showLoadingView();
        }

    }

    /**
     * 变脸面板点击事件回调
     */
    private MomentFacePanelLayout.OnFaceResourceSelectListener mMomentFaceSelectListener = new MomentFacePanelLayout.OnFaceResourceSelectListener() {
        @Override
        public void onSelected(MomentFace face) {
            // 选中时进行下载操作
            mFaceDownloadCallback.setRequestData(face);
            if (!face.isEmptyFace()) {
                MomentFaceUtil.downloadFace(face, mFaceDownloadCallback);
            }
        }

        @Override
        public void onClear() {
            if (mMomentFacePanelHelper != null) {
                mMomentFacePanelHelper.onClear();
            }
        }
    };

    /**
     * 设置数据管理类对象
     */
    public void setFaceDataManager(MomentFaceDataManager manager) {
        this.mFaceDataManager = manager;
    }

    /**
     * 资源下载成功，这个下载不一定仅仅是当前面板发出的，可能有推荐面板发出的下载请求，也会回调到这里。<br/>
     * 本面板发出的下载请求见{@link #onFaceDownloadSuccess(MomentFace, boolean)}
     * @param face 更新的变脸资源
     */
    private void updateMomentFace(MomentFace face) {
        if (mFaceDataManager == null) {
            return;
        }
        List<MomentFaceDataProcessor> dataProcessors = mFaceDataManager.getDataProcessors();
        if (dataProcessors == null || dataProcessors.isEmpty()) {
            return;
        }
        for (MomentFaceDataProcessor dataProcessor : dataProcessors) {
            dataProcessor.onMomentFaceDownloadSuccess(face);
        }
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        MomentFaceDownloadPublisher.getInstance().addObserver(mMomentFaceDownloadObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MomentFaceDownloadPublisher.getInstance().deleteObserver(mMomentFaceDownloadObserver);
    }

    /**
     * @see MomentFaceDataManager#addDataProcessor(MomentFaceDataProcessor)
     */
    public void addDataProcessor(MomentFaceDataProcessor processor) {
        mFaceDataManager.addDataProcessor(processor);
    }

    public void setMomentFacePanelHelper(MomentFacePanelHelper momentFacePanelHelper) {
        mMomentFacePanelHelper = momentFacePanelHelper;
    }

    private void onFaceDataLoadSuccess(List<MomentFace> data) {
        this.mIsFaceDataFetched = true;
        if (mViewProxy.isInflate()) {
            mViewProxy.getStubView().onFaceDataLoadSuccess(data);
            // 尝试设置预加载的选项
            if (mScheduledFaceItem != null) {
            } else if (mTolerantMoment != null) {
                // 如果数据列表已经拉取回来，找到指定的变脸信息
                MomentFace momentFace = MomentFaceUtil.findMomentFace((List<MomentFace>) mFaceDataManager.getData(), mTolerantMoment.getFaceId());
                if (momentFace == null) {
                    return;
                }
                mMomentFaceSelectListener.onSelected(momentFace);
            }
        } else {
            this.mScheduledFaceData = data;
            // 这需求...即使UI没准备好，也需要先回调默认变脸项。
            if (mTolerantMoment != null && canSetTolerantMoment()) {
                // 如果数据列表已经拉取回来，找到指定的变脸信息
                MomentFace momentFace = MomentFaceUtil.findMomentFace((List<MomentFace>) mFaceDataManager.getData(), mTolerantMoment.getFaceId());
                mMomentFaceSelectListener.onSelected(momentFace);
            }
        }
    }

    /**
     * 设置默认选中的变脸项
     * @param tolerantMoment 默认选中的变脸项信息
     * @see TolerantMoment
     */
    public void setTolerantMoment(TolerantMoment tolerantMoment) {
        if (!canSetTolerantMoment()) {
            return;
        }
        if (mIsFaceDataFetched) {
            // 如果数据列表已经拉取回来，找到指定的变脸信息
            MomentFace momentFace = MomentFaceUtil.findMomentFace((List<MomentFace>) mFaceDataManager.getData(), tolerantMoment.getFaceId());
            if (momentFace == null) {
                return;
            }
            mMomentFaceSelectListener.onSelected(momentFace);
        } else {
            // 等待列表数据拉取回来
            mTolerantMoment = tolerantMoment;
        }
    }

    /**
     * 是否可以设置默认变脸
     * @return true 可以，false 不可以
     */
    private boolean canSetTolerantMoment() {
        if (mScheduledFaceItem != null) {
            // 如果外部已经设置了需要选中的变脸项，还设置个P默认项
            return false;
        } else if (mViewProxy.isInflate() && mIsFaceDataFetched && mViewProxy.getStubView().hasSelectItem()) {
            // UI已经加载，并且数据列表已经获取，并且，有选中的项目，还设置个P默认项
            return false;
        }
        return true;
    }

    /**
     * 拉取列表成功
     */
    private void onFaceDataLoadFailed() {
        if (mViewProxy.isInflate()) {
            mViewProxy.getStubView().onFaceDataLoadFailed();
        } else {
            this.isFaceDataLoadFailed = true;
        }
    }

    /**
     * 下载资源失败
     */
    private void onFaceDownloadFailed(MomentFace face) {
        if (mViewProxy.isInflate()) {
            mViewProxy.getStubView().showDownLoadFailedTip();
            mViewProxy.getStubView().notifyItemChanged(face);
        }
    }

    /**
     * 下载资源成功
     * @param isLocalExit 当前资源是否是本地已经存在并且可用的资源。
     */
    private void onFaceDownloadSuccess(MomentFace face, boolean isLocalExit) {
        if (mMomentFacePanelHelper != null && isLocalExit) {
            mMomentFacePanelHelper.onItemSelected(face);

            if (mMomentFacePanelHelper.loadMaskWhenClick(face)) {
                // 选中时直接加载模型数据到内存
                mMaskLoadCallback.setMomentFace(face);
                MomentFaceUtil.loadFaceMask(face, mMaskLoadCallback, false);
            }
        }

        if (mViewProxy.isInflate()) {
            mViewProxy.getStubView().notifyItemChanged(face);
            if (isLocalExit) {
            }
        }
    }

    /**
     * 本地存在的资源被修改过，无效了，提醒重新下载
     */
    private void onIntegrityDetectionFailed(MomentFace face) {
        Toaster.showInvalidate("资源无效，重新下载");
    }

    /**
     * 加载资源到内存成功
     */
    private void onMaskLoadSuccess(MaskModel maskModel, MomentFace face) {
        if (mMomentFacePanelHelper != null) {
            mMomentFacePanelHelper.onMaskLoadSuccess(maskModel, face);
        }
        if (mViewProxy.isInflate()) {
            mViewProxy.getStubView().notifyItemChanged(face);
        }
    }

    /**
     * 加载资源到内存失败
     */
    private void onMaskLoadFailed(MomentFace face) {
        if (mMomentFacePanelHelper != null) {
            mMomentFacePanelHelper.onMaskLoadFailed(face);
        }
        if (mViewProxy.isInflate()) {
            mViewProxy.getStubView().showDownLoadFailedTip();
            mViewProxy.getStubView().notifyItemChanged(face);
        }
    }

    /**
     * 通知UI更新变脸项
     */
    private void notifyItemChanged(MomentFace face) {
        if (mViewProxy.isInflate()) {
            mViewProxy.getStubView().notifyItemChanged(face);
        }
    }

    @Override
    public void loadFaceData() {
        if (mViewProxy.isInflate()) {
            mViewProxy.getStubView().showLoadingView();
        }
        mFaceDataManager.loadData(mFaceDataLoadCallback);
    }

    /**
     * 设置选中的变脸项
     */
    public void setSelectedItem(MomentFace face) {
        if (mViewProxy.isInflate() && mIsFaceDataFetched) {
        } else {
            this.mScheduledFaceItem = face;
        }
    }

    /**
     * 变脸界面是否显示
     * @return true 显示，false 隐藏
     */
    public boolean isShown() {
        return mViewProxy.isInflate() && mViewProxy.getStubView().getVisibility() == View.VISIBLE;
    }

    /**
     * 显示变脸页面
     * @see #show(boolean)
     */
    public void show() {
        show(true);
    }

    /**
     * 显示变脸界面
     * @param withAnimation 是否播放进入动画
     * @see #show()
     */
    public void show(boolean withAnimation) {
        mViewProxy.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏变脸界面
     * @see #hide(boolean)
     */
    public void hide() {
        hide(true);
    }

    /**
     * 隐藏变脸界面
     * @param withAnimation true
     * @see #hide()
     */
    public void hide(boolean withAnimation) {
        if (!mViewProxy.isInflate() || mViewProxy.getStubView().getVisibility() != View.VISIBLE) {
            return;
        }
        mViewProxy.getStubView().setVisibility(View.GONE);
    }

    public ViewGroup.LayoutParams getLayoutParams() {
        return mViewProxy.getLayoutParams();
    }

    public MomentFaceDataManager getFaceManager() {
        return mFaceDataManager;
    }

    /**
     * 清除选中的变脸项
     */
    public void clearSelectedItem() {
        if (mViewProxy.isInflate()) {
        }
    }

    public void setSelectTab(String tabId) {
        if (mViewProxy.isInflate()) {
        }
    }

    /**
     * 当前View是否已经inflate到内存
     * @return true 已经inflate，false 未inflate
     */
    public boolean isViewInflated() {
        return mViewProxy.isInflate();
    }

    /**
     * 列表数据拉取回调
     */
    private static class FaceDataLoadCallback implements MomentFaceDataManager.FaceDataLoadCallback<List<MomentFace>> {

        private WeakReference<MomentFacePanelElement> mRef;

        private FaceDataLoadCallback(MomentFacePanelElement instance) {
            this.mRef = new WeakReference<>(instance);
        }

        @Override
        public void onFaceDataLoadSuccess(List<MomentFace> data) {
            MomentFacePanelElement element = mRef.get();
            if (element == null || element.isDestroy()) {
                return;
            }
            element.onFaceDataLoadSuccess(data);
        }

        @Override
        public void onFaceDataLoadFailed() {
            MomentFacePanelElement element = mRef.get();
            if (element == null || element.isDestroy()) {
                return;
            }
            element.onFaceDataLoadFailed();
        }
    }

    /**
     * 文件下载回调
     */
    private static class FaceDownloadCallback extends DownloadFaceCallbackAdapter {

        private WeakReference<MomentFacePanelElement> mRef;

        private MomentFace mMomentFace;

        private void setRequestData(MomentFace face) {
            this.mMomentFace = face;
        }

        private FaceDownloadCallback(MomentFacePanelElement instance) {
            this.mRef = new WeakReference<>(instance);
        }

        @Override
        public void onFaceDownloadSuccess(MomentFace face, boolean isLocalExit) {
            MomentFacePanelElement element = mRef.get();
            if (element == null || element.isDestroy()) {
                return;
            }
            if (face != mMomentFace) {
                element.notifyItemChanged(face);
                return;
            }
            element.onFaceDownloadSuccess(face, isLocalExit);
        }

        @Override
        public void onFaceDownloadFailed(MomentFace face) {
            MomentFacePanelElement element = mRef.get();
            if (element == null || element.isDestroy()) {
                return;
            }
            if (face != mMomentFace) {
                element.notifyItemChanged(face);
                return;
            }
            element.onFaceDownloadFailed(face);
        }

        @Override
        public void onIntegrityDetectionFailed(MomentFace face) {
            MomentFacePanelElement element = mRef.get();
            if (element == null || element.isDestroy()) {
                return;
            }
            if (face != mMomentFace) {
                element.notifyItemChanged(face);
                return;
            }
            element.onIntegrityDetectionFailed(face);
        }
    }

    /**
     * 模型加载进内存回调
     */
    private static class MyMaskLoadCallback implements MaskLoadCallback {

        private WeakReference<MomentFacePanelElement> mRef;

        private MomentFace mMomentFace;

        private MyMaskLoadCallback(MomentFacePanelElement instance) {
            this.mRef = new WeakReference<>(instance);
        }

        public void setMomentFace(MomentFace momentFace) {
            mMomentFace = momentFace;
        }

        @Override
        public void onMaskLoadSuccess(MaskModel maskModel, MomentFace face) {
            MomentFacePanelElement element = mRef.get();
            if (element == null || element.isDestroy()) {
                return;
            }
            if (face != mMomentFace) {
                element.notifyItemChanged(face);
                return;
            }
            element.onMaskLoadSuccess(maskModel, face);
        }

        @Override
        public void onMaskLoadFailed(MomentFace face) {
            MomentFacePanelElement element = mRef.get();
            if (element == null || element.isDestroy()) {
                return;
            }
            if (face != mMomentFace) {
                element.notifyItemChanged(face);
                return;
            }
            element.onMaskLoadFailed(face);
        }
    }

    /**
     * 变脸资源下载成功回调接口
     */
    private static class MomentFaceDownloadObserver implements Observer {

        private WeakReference<MomentFacePanelElement> mRef;

        public MomentFaceDownloadObserver(MomentFacePanelElement element) {
            this.mRef = new WeakReference<>(element);
        }

        @Override
        public void update(Observable o, Object arg) {
            MomentFacePanelElement element = mRef.get();
            if (element == null || element.isDestroy()) {
                return;
            }
            element.updateMomentFace((MomentFace) arg);
        }
    }

}
