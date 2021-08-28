package com.mm.base_business.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.mm.base_business.base.tabinfo.FragmentTabInfo;
import com.mm.mmutil.log.Log4Android;

/**
 * Created by ruanlei on 18/4/16.
 */
public abstract class BaseTabOptionFragment extends BaseFragment {

    private Toolbar mToolbar;
    private FragmentTabInfo tabInfo;

    private boolean foreground = false;
    private boolean isResumed = false;
    private boolean isPaused = false;
    private boolean calledFirstDispatchResume = false;
    private boolean isSelected;
    private boolean isViewCreated = false;
    boolean isPreLoading = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        isViewCreated = true;
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!isNeedLazyLoad() && !isLazyLoadFinished()) {
            BaseFragmentLifecycleEventDispatcher.dispatchFragmentLoad(this);
            onLoad();
            setLoadFinished();
        }
    }

    public FragmentTabInfo getTabInfo() {
        return tabInfo;
    }

    public void setTabInfo(FragmentTabInfo tabInfo) {
        this.tabInfo = tabInfo;
    }

    public void scrollToTop() {

    }

    public boolean isForeground() {
        return foreground;
    }

    public void setForeground(boolean foreground) {
        this.foreground = foreground;
    }

    public boolean isSelected() {
        return isSelected;
    }

    void setSelected(boolean selected) {
        if (isViewCreated) {
            isSelected = selected;
        }
    }

    void dispatchResume() {
        if (isResumed) {
            return;
        }
        if (isPreLoading) {
            Fragment fragment = getParentFragment();
            // 如果当前fragment为BaseScrollTabGroupFragment中的一项，它的pre loading过程尚未结束
            if (fragment != null && fragment instanceof BaseScrollTabGroupFragment) {
                return;
            }
            isPreLoading = false;
            return;
        }
        if (isCreated()) {
            setForeground(true);

            if (!isSelected()) {
                Toolbar toolbar = findToolbar();

                if (toolbar != null) {
                    int menuRes = getToolbarMenuRes();
                    Toolbar.OnMenuItemClickListener menuItemClickListener = this.getToolbarMenuClickListener();

                    if (menuRes < 0) {
                        if (this instanceof BaseScrollTabGroupFragment) {
                            BaseTabOptionFragment currentTabFragment = ((BaseScrollTabGroupFragment) this).getCurrentFragment();
                            if (currentTabFragment != null) {
                                menuRes = currentTabFragment.getToolbarMenuRes();
                                menuItemClickListener = currentTabFragment.getToolbarMenuClickListener();
                            }
                        }
                    }

                    if (menuRes > 0) {
                        Log4Android.getInstance().i("leicurl--->>> " + this.getClass().getSimpleName() + " inflate menu ");
                        try {
                            toolbar.getMenu().clear();
                            toolbar.inflateMenu(menuRes);
                            toolbar.setOnMenuItemClickListener(menuItemClickListener);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log4Android.getInstance().i("leicurl--->>> " + this.getClass().getSimpleName() + " clear menu ");
                        toolbar.getMenu().clear();
                    }
                }
            }
        }
        onFragmentResume();
        setSelected(true);
    }

    void dispatchPause() {
        if (isPaused) {
            return;
        }
        setForeground(false);
        if (isCreated()) {
            onFragmentPause();
        }
    }

    public int getToolbarMenuRes() {
        return -1;
    }

    public Toolbar.OnMenuItemClickListener getToolbarMenuClickListener() {
        return null;
    }

    @Override
    public void setTitle(CharSequence titleText) {
        Toolbar toolbar = findToolbar();
        if (toolbar != null) {
            toolbar.setTitle(titleText);
        } else if (getActivity() != null) {
            getActivity().setTitle(titleText);
        }
    }

    @Override
    public void setTitle(int titleId) {
        Toolbar toolbar = findToolbar();
        if (toolbar != null) {
            toolbar.setTitle(titleId);
        } else if (getActivity() != null) {
            getActivity().setTitle(titleId);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            onVisible();
        }
    }

    private void onVisible() {
        if (canDoLazyLoad()) {
            BaseFragmentLifecycleEventDispatcher.dispatchFragmentLoad(this);
            onLoad();
            setLoadFinished();
            dispatchResume();
        } else if (isLazyLoadFinished() && isForeground()) {
            dispatchResume();
        }
    }

    @Override
    protected boolean canDoLazyLoad() {
        return super.canDoLazyLoad() && isForeground();
    }

    protected void onFragmentResume() {
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentVisibleResume(this);
        isResumed = true;
        isPaused = false;
    }

    protected void onFragmentPause() {
        BaseFragmentLifecycleEventDispatcher.dispatchFragmentVisiblePause(this);
        isResumed = false;
        isPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!calledFirstDispatchResume) {
            if (super.canDoLazyLoad()) {
                BaseFragmentLifecycleEventDispatcher.dispatchFragmentLoad(this);
                onLoad();
                setLoadFinished();
                dispatchResume();
            } else if (isLazyLoadFinished() && isForeground()) {
                dispatchResume();
            }

            calledFirstDispatchResume = true;
        }
    }

    /**
     * 从其他tab切换过来 会调用此方法 （for MaintabActivity）
     */
    protected void onShowFromOtherTab() {

    }

    public Toolbar findToolbar() {
        if (mToolbar == null) {
            mToolbar = getToolbar();
        }
        if (mToolbar != null) {
            return mToolbar;
        } else {
            Fragment fragment = getParentFragment();
            if (fragment != null && fragment instanceof BaseTabOptionFragment) {
                return ((BaseTabOptionFragment) fragment).getToolbar();
            }
        }

        Activity activity = getActivity();
        if (activity != null && activity instanceof BaseActivity) {
            return ((BaseActivity) activity).getToolbar();
        }

        return null;
    }

}
