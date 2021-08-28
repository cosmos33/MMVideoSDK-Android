package com.mm.base_business.base;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.mm.base_business.R;


/**
 * Tab容器Activity, 主帧类业务使用
 * 可嵌套{@link BaseTabOptionFragment} 作为Tab展示单帧的子Tab
 * 可嵌套{@link BaseScrollTabGroupFragment} 作为TAB再嵌套更多BaseTabOptionFragment子项
 * Created by ruanlei on 13/4/16.
 */
public abstract class BaseTabGroupActivity extends BaseActivity{

    private ViewGroup tabwidgetLayout;
    protected BaseTabOptionFragment currentFragment;
    protected int currentIndex ;
    private boolean fragmentInited;

    protected final SparseArray<TabInfo> tabs = new SparseArray<>();
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //activity被回收，已经add到FragmentManager中的fragment不被释放，activity恢复后，旧的fragment的内存实例始终存在，且view也始终在展示，使用这种方式fix
            savedInstanceState.remove("android:support:fragments");
        }
        super.onCreate(savedInstanceState);
        addTab(getTabs());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //activity被回收，已经add到FragmentManager中的fragment不被释放，activity恢复后，旧的fragment的内存实例始终存在，且view也始终在展示，使用这种方式fix
        outState.remove("android:support:fragments");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentFragment != null && currentFragment.isCreated() && !currentFragment.isForeground()) {
            currentFragment.dispatchResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentFragment != null && currentFragment.isCreated()) {
            currentFragment.dispatchPause();
        }
    }

    protected abstract TabInfo[] getTabs();

    public void addTab(int index, TabInfo tab) {
        tabs.put(index, tab.clone());
    }

    private void addTab(TabInfo... tabs) {
        for (int i = 0; i < tabs.length; i++) {
            addTab(i, tabs[i]);
        }
    }

    public static final class TabInfo implements Cloneable {
        private Class<? extends BaseTabOptionFragment> clazz;
        private int viewRes;
        private BaseTabOptionFragment fragment;
        private View view;
        private boolean preLoad = false;

        public TabInfo(Class<? extends BaseTabOptionFragment> clazz,int viewRes) {
            this.clazz = clazz;
            this.viewRes = viewRes;
        }

        public TabInfo(Class<? extends BaseTabOptionFragment> clazz,int viewRes, boolean preLoad) {
            this.clazz = clazz;
            this.viewRes = viewRes;
            this.preLoad = preLoad;
        }

        @Override
        public TabInfo clone() {
            TabInfo newTabInfo = new TabInfo(this.clazz, viewRes, preLoad);
            newTabInfo.view = this.view;

            return newTabInfo;
        }
    }

    private final View.OnClickListener tabClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            BaseTabOptionFragment checkedFragment = null;
            int i = 0;
            for (; i < tabs.size(); i++) {
                if (tabs.get(i).view == v) {
                    checkedFragment = tabs.get(i).fragment;
                    break;
                }
            }
            onTabclick(i);
            if (checkedFragment != null) {
                if (currentFragment == checkedFragment) {
                    currentFragment.scrollToTop();
                } else {
                    showFragment(checkedFragment);
                    currentFragment.onShowFromOtherTab();
                    if (specificFragmentShowedCallback != null) {
                        specificFragmentShowedCallback.onSpecificFragmentShowed(checkedFragment);
                    }
                }
            } else {
                showFragment(i);
            }
        }
    };

    private final View.OnClickListener guestTabClickedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = 0;
            for (; i < tabs.size(); i++) {
                if (tabs.get(i).view == v) {
                    break;
                }
            }
            onTabclick(i);
        }
    };

    /**
     * 五个TAB被点击时时回调
     * @param index
     */
    protected void onTabclick(int index) {
    }

    private SpecificFragmentShowedCallback specificFragmentShowedCallback;

    public void setSpecificFragmentShowedCallback(SpecificFragmentShowedCallback callback) {
        specificFragmentShowedCallback = callback;
    }

    public interface SpecificFragmentShowedCallback {
        void onSpecificFragmentShowed(BaseTabOptionFragment fragment);
    }

    protected void onTabChanged(int index, BaseTabOptionFragment fragment) {}


    private void initTabFragments() {
        tabwidgetLayout = (ViewGroup) findViewById(R.id.tabwidget);

        for (int i = tabs.size() - 1; i >= 0; i--) {
            int viewResid = tabs.get(i).viewRes;
            View v = tabwidgetLayout.findViewById(viewResid);
            tabs.get(i).view = v;
            v.setOnClickListener(tabClickedListener);
            final int index = i;

            if (tabs.get(i).preLoad) {
                initSpecificFragment(index);

            }
        }
        fragmentInited = true;
    }

    private void initGuestTabFragmentsClick() {
        tabwidgetLayout = (ViewGroup) findViewById(R.id.tabwidget);

        for (int i = tabs.size() - 1; i >= 0; i--) {
            int viewResid = tabs.get(i).viewRes;
            View v = tabwidgetLayout.findViewById(viewResid);
            tabs.get(i).view = v;
            v.setOnClickListener(guestTabClickedListener);
        }
        fragmentInited = true;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public BaseFragment getCurrentFragment() {
        if(tabs == null || tabs.size() <= 0) {
            return null;
        }
        return tabs.get(currentIndex).fragment;
    }

    public boolean showFragment(int index) {
        if(!fragmentInited) {
            initTabFragments();
        }
        if (index >= 0 && index < tabs.size()) {
            // 首先判断是否已经实例化
            // 如果未实例化，先实例化
            initSpecificFragment(index);

            BaseTabOptionFragment fragment = tabs.get(index).fragment;
            return showFragment(fragment);
        }
        return false;
    }

    //用于访客模式，只初始化第一个fragment
    public boolean showFragment(int index, boolean initOtherFragment) {
        if (initOtherFragment) {
            initTabFragments();
        } else {
            initGuestTabFragmentsClick();
        }

        if (index >= 0 && index < tabs.size()) {
            // 首先判断是否已经实例化
            // 如果未实例化，先实例化
            initSpecificFragment(index);

            BaseTabOptionFragment fragment = tabs.get(index).fragment;
            return showFragment(fragment);
        }
        return false;
    }

    private boolean showFragment(BaseTabOptionFragment fragment) {
        // 不需要切换
        if (fragment == currentFragment) {
            return false;
        }

        // 改变选中的View
        for (int i = 0; i < tabs.size(); i++) {
            View view = tabs.get(i).view;
            if (view == null) continue;
            if (fragment == tabs.get(i).fragment) {
                view.setSelected(true);
                currentIndex = i;
            } else {
                view.setSelected(false);
            }
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        BaseTabOptionFragment oldFragment = this.currentFragment;
        this.currentFragment = fragment;

        if (oldFragment != null) {
            oldFragment.dispatchPause();
            transaction.hide(oldFragment);
            oldFragment.setSelected(false);
        }

        if (fragment.isCreated()) {
            fragment.dispatchResume();
        }

        transaction.show(currentFragment);
        fragment.setSelected(true);

        transaction.commitAllowingStateLoss();

        onTabChanged(currentIndex, currentFragment);

        return true;
    }

    private void initSpecificFragment(int fragmentIndex) {
        TabInfo tabInfo = tabs.get(fragmentIndex);
        if (tabInfo != null && tabInfo.fragment == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Class<? extends BaseTabOptionFragment> fragmentClass = tabInfo.clazz;

            BaseTabOptionFragment fragment = (BaseTabOptionFragment) Fragment.instantiate(this, fragmentClass.getName());
            onFragmentInstantiated(fragment);
            fragment.setForeground(false);
            if(tabInfo.preLoad) {
                fragment.isPreLoading = true;
            }
            tabInfo.fragment = fragment;
            if(!tabInfo.fragment.isAdded()) {
                transaction.add(R.id.tabcontent, fragment);
            }

            int viewResid = tabInfo.viewRes;
            View tabView = tabwidgetLayout.findViewById(viewResid);
            tabInfo.view = tabView;
            tabView.setOnClickListener(tabClickedListener);

            transaction.hide(fragment);
            transaction.commitAllowingStateLoss();
        }
    }

    /**
     * fragment 被实例化以后的回调
     * @param fragment
     */
    public void onFragmentInstantiated(BaseTabOptionFragment fragment){

    }

    public void removeTab(int index) {
        TabInfo tabInfo = tabs.get(index);
        if(tabInfo != null && tabInfo.fragment != null && tabInfo.fragment.isAdded()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(tabInfo.fragment);
            transaction.commitAllowingStateLoss();
            addTab(index, tabs.get(index));
            if(currentFragment != null && currentFragment == tabInfo.fragment) {
                currentFragment = null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        for(int i = 0; i < tabs.size(); i++) {
            TabInfo tabInfo = tabs.get(i);
            if(tabInfo != null && tabInfo.fragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(tabInfo.fragment);
                transaction.commitAllowingStateLoss();
            }
        }
        tabs.clear();
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if (currentFragment != null && currentFragment.isCreated()) {
            if (currentFragment.onBackPressed()) {
                return;
            }
        }
    }

}
