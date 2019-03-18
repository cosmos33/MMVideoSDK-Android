package com.mm.sdkdemo.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.MMTabLayout;
import com.immomo.mmutil.log.Log4Android;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.tabinfo.DefaultSlidingIndicator;
import com.mm.sdkdemo.base.tabinfo.FragmentTabInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 带滚动效果的Tab容器Fragment, 作为{@link BaseActivity}的Tab
 * 嵌套{@link BaseTabOptionFragment} 展示多个子Tab
 * Created by ruanlei on 20/4/15.
 */
public abstract class BaseScrollTabGroupFragment extends BaseTabOptionFragment {

    protected static final String SAVED_INSTANCE_STATE_KEY_TAB_INDEX = "SAVED_INSTANCE_STATE_KEY_TAB_INDEX";

    private final ArrayList<FragmentTabInfo> tabs = new ArrayList<>();
    private int customOffscreenPageLimit = -1;

    protected Map<Integer, BaseTabOptionFragment> fragments = new HashMap<>();
    private ViewPager viewPager;
    private int currentTab = -1;

    private MMTabLayout tabLayout;
    private TabsAdapter tabsAdapter;
    private boolean isStarted = false;
    private boolean isInitedFirstTab = false;

    private FragmentManager childFragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        viewPager = findViewById(R.id.pagertabcontent);
        tabLayout = findViewById(R.id.tablayout_id);
        tabLayout.setTabMode(MMTabLayout.MODE_SCROLLABLE);
        tabLayout.setSelectedTabSlidingIndicator(new DefaultSlidingIndicator());
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void onLoad() {
        addTab(onLoadTabs());

        viewPager.setOffscreenPageLimit(customOffscreenPageLimit > 0
                ? customOffscreenPageLimit : (tabs.size() - 1));

        childFragmentManager = this.getChildFragmentManager();
        tabsAdapter = new TabsAdapter(this.getActivity(), childFragmentManager, viewPager, tabs);

        try {
            tabLayout.setupWithViewPager(viewPager);
        } catch (Throwable e) {
            //hold住design包底层的crash
            Log4Android.getInstance().e(e);
        }

        for (int i = 0; i < tabs.size(); i++) {
            FragmentTabInfo tabInfo = tabs.get(i);
            if (tabInfo.isPreLoad()) {
                preloadFragment(i);
            }
            MMTabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setTabInfo(tabInfo);
            }
        }
    }

    public final ArrayList<FragmentTabInfo> getTabs() {
        return tabs;
    }

    @Nullable
    public final <TAB extends FragmentTabInfo> TAB getTabAt(int index) {
        return (index >= 0 && index < tabs.size()) ? (TAB) tabs.get(index) : null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewPager != null) {
            if (tabsAdapter != null) {
                viewPager.removeOnPageChangeListener(tabsAdapter);
                tabsAdapter = null;
            }
            viewPager = null;
        }
        tabLayout = null;
    }

    protected abstract List<? extends FragmentTabInfo> onLoadTabs();


    protected void setOffscreenPageLimit(int offscreenPageLimit) {
        if (offscreenPageLimit <= 0) {
            throw new IllegalArgumentException("customOffscreenPageLimit must be > 0");
        }
        customOffscreenPageLimit = offscreenPageLimit;
        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(customOffscreenPageLimit);
        }
    }

    public BaseTabOptionFragment getFragment(int index) {
        return fragments.get(index);
    }

    @Nullable
    public BaseTabOptionFragment getCurrentFragment() {
        return fragments.get(getCurrentTab());
    }

    public MMTabLayout getTabLayout() {
        return tabLayout;
    }

    public int getCurrentTab() {
        if (viewPager != null) {
            return viewPager.getCurrentItem();
        }
        return 0;
    }

    public void setCurrentTab(int currentTab) {
        if (viewPager != null) {
            viewPager.setCurrentItem(currentTab);
            if (this.currentTab == -1) {
                onPageSelectDown(currentTab);
            }
            if (this.currentTab > -1 && !this.isStarted && this.currentTab != currentTab) {
                BaseTabOptionFragment oldFragment = fragments.get(this.currentTab);
                if (oldFragment != null) {
                    oldFragment.setSelected(false);
                }
            }
        }
        if (!isInitedFirstTab) {
            this.currentTab = currentTab;
            this.isInitedFirstTab = true;
        }
    }

    protected void addTab(FragmentTabInfo tab) {
        tabs.add(tab);
    }

    protected void addTab(List<? extends FragmentTabInfo> tabs) {
        for (FragmentTabInfo tab : tabs) {
            addTab(tab);
        }
    }

    private void preloadFragment(int index) {
        FragmentTabInfo info = tabs.get(index);
        BaseTabOptionFragment fragment = (BaseTabOptionFragment) Fragment.instantiate(getContext(), info.getFragmentClazz().getName());
        if (info.getArgs() != null) {
            fragment.setArguments(info.getArgs());
        }
        fragment.setTabInfo(info);
        fragments.put(index, fragment);
        fragment.isPreLoading = true;

        FragmentTransaction curTransaction = childFragmentManager.beginTransaction();
        curTransaction.add(viewPager.getId(), fragment);
        curTransaction.commitAllowingStateLoss();
    }

    protected void onTabChanged(int index, BaseTabOptionFragment fragment) {
    }

    //增加一个页面滑动回调
    protected void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (viewPager != null) {
            outState.putInt(SAVED_INSTANCE_STATE_KEY_TAB_INDEX, getCurrentTab());
        }
        super.onSaveInstanceState(outState);
    }

    protected void onFragmentCreated(BaseTabOptionFragment fragment, int index) {

    }

    @Override
    public void onResume() {
        super.onResume();
        BaseTabOptionFragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment.isCreated() && !currentFragment.isForeground() && this.isForeground()) {
            currentFragment.dispatchResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BaseTabOptionFragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment.isCreated() && this.isForeground()) {
            currentFragment.dispatchPause();
        }
    }

    @Override
    public void onStop() {
        isStarted = false;
        super.onStop();
    }

    @Override
    public void onStart() {
        isStarted = true;
        super.onStart();
    }

    @Override
    protected void onFragmentResume() {
        super.onFragmentResume();
        BaseTabOptionFragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment.isCreated() && !currentFragment.isForeground()) {
            currentFragment.dispatchResume();
        }
    }

    @Override
    protected void onFragmentPause() {
        super.onFragmentPause();
        BaseTabOptionFragment currentFragment = getCurrentFragment();
        if (currentFragment != null && currentFragment.isCreated()) {
            currentFragment.dispatchPause();
        }
    }

    /**
     * @return true for destroy, false for not
     */
    protected boolean onDestroyChildFragment(int position, BaseTabOptionFragment fragment) {
        return false;
    }

    protected void clear() {
        if (viewPager != null) {
            viewPager.removeAllViews();
            tabs.clear();
        }
    }

    public void refreshTab() {
        if (tabsAdapter != null) {
            tabsAdapter.notifyDataSetChanged();
        }
    }

    public class TabsAdapter extends FixFragmentStatePagerAdapter implements ViewPager.OnPageChangeListener {
        private final Context context;
        private final ViewPager viewPager;
        private ArrayList<FragmentTabInfo> tabs = null;
        private boolean firstUpdated = true;

        int index = -1;

        public TabsAdapter(Context context, FragmentManager childFragmentManager, ViewPager pager, ArrayList<FragmentTabInfo> tabInfos) {
            super(childFragmentManager);
            this.tabs = new ArrayList<>(tabInfos);

            this.context = context;
            viewPager = pager;

            viewPager.addOnPageChangeListener(this);
            viewPager.setAdapter(this);
        }

        @Override
        public int getCount() {
            return tabs.size();
        }

        @Override
        public void startUpdate(ViewGroup container) {
            super.startUpdate(container);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            super.finishUpdate(container);

            if (firstUpdated) {
                firstUpdated = false;
                onPageSelected(viewPager.getCurrentItem());
            }
        }

        @Override
        public Fragment getItem(int position) {
            Fragment preFragment = fragments.get(position);
            if (preFragment != null) {
                return preFragment;
            }
            FragmentTabInfo info = tabs.get(position);
            BaseTabOptionFragment fragment = (BaseTabOptionFragment) Fragment.instantiate(context, info.getFragmentClazz().getName());
            if (info.getArgs() != null)
                fragment.setArguments(info.getArgs());
            fragment.setTabInfo(info);
            onFragmentCreated(fragment, position);
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Object object = super.instantiateItem(container, position);
            fragments.put(position, (BaseTabOptionFragment) object);
            return object;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            BaseScrollTabGroupFragment.this.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (customOffscreenPageLimit > 0
                    && BaseTabOptionFragment.class.isInstance(object)
                    && onDestroyChildFragment(position, (BaseTabOptionFragment) object)) {
                super.destroyItem(container, position, object);
                fragments.remove(position);
            }
        }

        private int previousScrollState = -1;
        private int selectedPosition = -1;

        @Override
        public void onPageSelected(int position) {
            Log4Android.getInstance().i("BaseScrollTabGroupFragment ===* onPageSelected : " + position);

            selectedPosition = position;

            //第一次初始化，onPageScrollStateChanged方法不会被调，直接调用onPageSelectDown
            if ((previousScrollState == -1 && currentTab != -1) || (previousScrollState == ViewPager.SCROLL_STATE_IDLE)) {
                onPageSelectDown(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            Log4Android.getInstance().i("BaseScrollTabGroupFragment ===* onPageScrollStateChanged : " + state);

            //当page状态由SCROLL_STATE_SETTLING变为SCROLL_STATE_IDLE，说明是页面被拖动或tab点击选中后滑动动画播放完成，调用onPageSelectDown执行页面切换
            if ((previousScrollState == ViewPager.SCROLL_STATE_SETTLING || previousScrollState == ViewPager.SCROLL_STATE_DRAGGING) && state == ViewPager.SCROLL_STATE_IDLE) {
                if (selectedPosition != currentTab) {
                    onPageSelectDown(selectedPosition);
                }
            }

            previousScrollState = state;
        }

    }

    private void onPageSelectDown(int position) {
        Log4Android.getInstance().i("BaseScrollTabGroupFragment ===* onPageSelectDown : " + position);
        BaseTabOptionFragment oldFragment = fragments.get(currentTab);
        BaseTabOptionFragment newFragment = fragments.get(position);
        if (currentTab >= 0 && currentTab != position && oldFragment != null) {
            oldFragment.dispatchPause();
            oldFragment.setSelected(false);
        }

        if (newFragment != null) {
            newFragment.setForeground(true);
            if (newFragment.canDoLazyLoad()) {
                BaseFragmentLifecycleEventDispatcher.dispatchFragmentLoad(newFragment);
                newFragment.onLoad();
                newFragment.setLoadFinished();
                newFragment.dispatchResume();
            } else if (isLazyLoadFinished() && isForeground()) {
                newFragment.dispatchResume();
            }

            currentTab = position;
            BaseScrollTabGroupFragment.this.onTabChanged(position, newFragment);
            newFragment.setSelected(true);
        }
    }

    @Override
    public boolean getUserVisibleHint() {
        return true;
    }
}

