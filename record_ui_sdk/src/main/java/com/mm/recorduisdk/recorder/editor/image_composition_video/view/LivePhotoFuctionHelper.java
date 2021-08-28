package com.mm.recorduisdk.recorder.editor.image_composition_video.view;

import android.animation.Animator;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.recorder.specialfilter.SpecialFilterAnimationUtils;


public class LivePhotoFuctionHelper {

    public static final int ORDER_INDEX = 0;
    public static final int ANIMATE_INDEX = 1;

    private static final String TAG = "LivePhotoFuctionHelper";
    private final FragmentManager fragmentManager;

    @NonNull
    private BaseLivePhotoFragment[] fragments;



    private final String[] tabs;
    private View liveFuctionLayout;
    private View bottomLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    private boolean isShowing = false;

    private int mCurrentTab = ORDER_INDEX;

    public LivePhotoFuctionHelper(@Nullable FragmentManager fragmentManager, View rootView, ILivePhotoPresenter presenter) {
        this.fragmentManager = fragmentManager;
        this.fragments = new BaseLivePhotoFragment[]{new OrderFragment(), new AnimateFragment()};
        attachView2Fragment(presenter);
        this.tabs = new String[]{"拼接", "特效"};
        initView(rootView);
        initEvent();

    }

    private void attachView2Fragment(ILivePhotoPresenter presenter) {
        for(BaseLivePhotoFragment fragment:fragments){
            fragment.attachPresenter(presenter);
        }
    }

    private void initView(View rootView) {
        ViewStub vs = rootView.findViewById(R.id.live_photo_fuction_vs);
        liveFuctionLayout = vs.inflate();
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        bottomLayout = findViewById(R.id.bottom_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fragments[tab.getPosition()].setResume(true);
                mCurrentTab = tab.getPosition();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                fragments[tab.getPosition()].setResume(false);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        LivePhotoAdapter livePhotoAdapter = new LivePhotoAdapter(fragmentManager);
        viewPager.setAdapter(livePhotoAdapter);
        viewPager.setOffscreenPageLimit(2);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, UIUtils.getPixels(28), 0);
            tab.requestLayout();
        }
    }


    private <T extends View> T findViewById(@IdRes int id) {
        return (T) liveFuctionLayout.findViewById(id);
    }


    private void initEvent() {

    }


    public void show(){
        show(mCurrentTab,null);
    }

    public void show(int index, final Animator.AnimatorListener listener) {
        isShowing = true;
        mCurrentTab = index;

        viewPager.setCurrentItem(index);
        liveFuctionLayout.setVisibility(View.VISIBLE);
        SpecialFilterAnimationUtils.showAnimation(bottomLayout);
        this.fragments[index].setResume(true);
    }


    public void hide() {
        for (BaseLivePhotoFragment fragment : fragments) {
            fragment.setResume(false);
        }
        isShowing = false;
        liveFuctionLayout.setVisibility(View.GONE);
    }


    public boolean isShowing() {
        return isShowing;
    }

    public boolean onBackPress() {
        return false;
    }


    public void onPhotoChange() {
        for (BaseLivePhotoFragment fragment : fragments) {
            fragment.onChange();
        }
    }

    private class LivePhotoAdapter extends FragmentPagerAdapter {

        public LivePhotoAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return tabs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

//        @Override
//        public String makeFragmentName(ViewGroup parent, long id) {
//            return getFragmentName((int) id);
//        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

/*        @Override
        public String makeFragmentName(ViewGroup parent, long id) {
            return "android:switcher:a" + id;
        }*/

        private String getFragmentName(int id) {
            switch (id) {
                case 0:
                    return "OrderFragment";
                case 1:
                    return "AnimateFragment";
                default:
                    break;
            }
            return "LivePhotoFuctionHelper";
        }

    }

}
