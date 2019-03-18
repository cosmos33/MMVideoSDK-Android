package com.mm.sdkdemo.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.immomo.mmutil.log.Log4Android;
import com.immomo.mmutil.task.MomoMainThreadExecutor;
import com.mm.sdkdemo.utils.toolbar.ToolbarHelper;

import java.lang.ref.WeakReference;

/**
 * Created by ruanlei on 12/4/16.
 */
public abstract class BaseFragment extends Fragment {
    protected static final int RESULT_OK = Activity.RESULT_OK;
    protected static final int RESULT_CANCELED = Activity.RESULT_CANCELED;
    private final Log4Android log = Log4Android.getInstance();
    private boolean isLazyLoadFinished = false;
    private int requestCode;
    private int resultCode;
    private Intent data;
    private boolean isPrepared = false;
    private boolean isViewCreated = false;
    private WeakReference<View> contentViewReference = null;
    private SparseArray<WeakReference<View>> viewFounds = null;
    private Dialog dialog = null;

    private boolean isFragmentCreated = false;
    private boolean callOnResult = false;
    private Toolbar toolbar;
    protected ToolbarHelper toolbarHelper;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentAttach(this, activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewFounds = new SparseArray<>();
        contentViewReference = null;
        isFragmentCreated = false;
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentCreate(this, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (contentViewReference == null || contentViewReference.get() == null) {
            view = inflater.inflate(getLayout(), container, false);
            contentViewReference = new WeakReference<>(view);
        } else {
            view = contentViewReference.get();
        }

        toolbarHelper = ToolbarHelper.buildFromFragment(this);
        toolbar = toolbarHelper.getToolbar();

        isPrepared = true;
        initViews(view);
        isViewCreated = true;
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentCreateView(this, inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        isFragmentCreated = true;
        if (callOnResult) {
            onActivityResultReceived(requestCode, resultCode, data);
            callOnResult = false;
        }

        if (isNeedLazyLoad()) {
            MomoMainThreadExecutor.post(hashCode(), new Runnable() {
                @Override
                public void run() {
                    if (canDoLazyLoad()) {
                        doLazyLoad();
                        setLoadFinished();
                    }
                }
            });
        } else {
            if (canDoLazyLoad()) {
                doLazyLoad();
                setLoadFinished();
            }
        }
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentActivityCreated(this, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentStart(this);
    }

    public void onResume() {
        super.onResume();
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentPause(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentStop(this);
    }

    @Override
    public void onDestroyView() {
        MomoMainThreadExecutor.cancelAllRunnables(hashCode());
        super.onDestroyView();
        isViewCreated = false;
        contentViewReference.clear();
        contentViewReference = null;
        viewFounds.clear();
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentDestroyView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentDestroy(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isFragmentCreated = false;
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentDetach(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentSaveInstanceState(this, outState);
    }

    public boolean onBackPressed() {
        return false;
    }

    public View getContentView() {
        if (contentViewReference == null || contentViewReference.get() == null) {
            // 改为当前activity避免使用AppKit.getcontext() theme style不对导致crash
            if (getActivity() != null) {
                View view = LayoutInflater.from(getActivity()).inflate(getLayout(), null, false);
                contentViewReference = new WeakReference<>(view);
            } else {
                return null;
            }
        }

        return contentViewReference.get();
    }

    /**
     * 通过资源号在界面中查找View。容器是{@link #getLayout()} 中指定的布局。
     *
     * @param id 资源的ID
     * @return 找到的View
     */
    @Nullable
    public <T extends View> T findViewById(@IdRes int id) {
        T v = (T) (viewFounds.get(id) != null ? viewFounds.get(id).get() : null);
        if (v == null) {
            v = (T) (getContentView() == null ? null : getContentView().findViewById(id));
            if (v != null) {
                viewFounds.put(id, new WeakReference<>((View)v));
            }
        }
        return v;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setTitle(CharSequence titleText) {
        Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTitle(titleText);
        } else {
            getActivity().setTitle(titleText);
        }
    }

    public void setTitle(int titleId) {
        Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTitle(titleId);
        } else {
            getActivity().setTitle(titleId);
        }
    }

    public boolean isCreated() {
        return isFragmentCreated;
    }

    @Override
    public final void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isFragmentCreated) {
            onActivityResultReceived(requestCode, resultCode, data);
        } else {
            log.w("requestCode=" + requestCode + ", resultCode=" + resultCode + ", fragment not created");
            callOnResult = true;
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.data = data;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onActivityResultReceived(int requestCode, int resultCode, Intent data) {
    }

    /**
     * 在此方法返回布局文件的资源号 R.layout.xxx
     *
     * @return 当前页面的布局文件ID
     */
    protected abstract int getLayout();

    public synchronized void showDialog(Dialog dialog) {
        closeDialog();
        this.dialog = dialog;
        if (getActivity() != null && !getActivity().isFinishing()) {
            dialog.show();
        }
    }

    /**
     * 关闭当前显示的Dialog。
     */
    public synchronized void closeDialog() {
        if (dialog != null && dialog.isShowing() && getActivity() != null && !getActivity().isFinishing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    /**
     * 在这里实现Fragment数据的缓加载.
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        }
    }

    private void onVisible() {
        if (canDoLazyLoad()) {
            doLazyLoad();
            setLoadFinished();
        }
    }

    void doLazyLoad() {
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentLoad(this);
        onLoad();
    }

    protected abstract void initViews(View contentView);

    protected abstract void onLoad();

    protected boolean isNeedLazyLoad() {
        return true;
    }

    /**
     * View准备好，而且没有完成过lazyLoad，子类才可以继续执行LazyLoad
     */
    protected boolean canDoLazyLoad() {
        return isPrepared && !isLazyLoadFinished && getUserVisibleHint() && isViewCreated;
    }

    protected void setLoadFinished() {
        isLazyLoadFinished = true;
    }

    protected boolean isLazyLoadFinished() {
        return isLazyLoadFinished;
    }

}

