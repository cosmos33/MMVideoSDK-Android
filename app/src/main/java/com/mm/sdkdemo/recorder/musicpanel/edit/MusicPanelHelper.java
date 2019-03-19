package com.mm.sdkdemo.recorder.musicpanel.edit;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.mm.mediasdk.utils.MusicPlayer;
import com.mm.mmutil.toast.Toaster;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.recorder.model.MusicContent;
import com.mm.sdkdemo.utils.AnimUtils;

public class MusicPanelHelper implements IEditMusic {

    private FragmentManager fragmentManager;
    private View contentView;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String[] titles;
    private VolFragment volFragment;
    private CutMusicFragment cutMusicFragment;
    private BaseEditMusicFragment[] fragments;

    private OnMusicListener onMusicListener;

    @Nullable
    private MusicContent selectMusic;
    private boolean pauseMusic;

    private int initVolume;
    private MusicPlayer player = new MusicPlayer();
    private boolean canSeekVol;

    public MusicPanelHelper(FragmentManager fragmentManager, View rootView, int volume) {
        this.fragmentManager = fragmentManager;
        this.initVolume = volume;
        titles = new String[]{"音量", "截取"};
        volFragment = new VolFragment();
        cutMusicFragment = new CutMusicFragment();
        fragments = new BaseEditMusicFragment[]{volFragment, cutMusicFragment};

        initView(rootView);

        volFragment.setiEditMusic(this);
        cutMusicFragment.setiEditMusic(this);
    }

    public void setOnMusicListener(OnMusicListener listener) {
        this.onMusicListener = listener;
    }

    private void initView(View rootView) {
        ViewStub vs = rootView.findViewById(R.id.music_panel);
        contentView = vs.inflate();

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.edit_viewpager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(new ViewPageAdapter(fragmentManager));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < fragments.length; i++) {
                    if (i == position) {
                        fragments[i].onFragmentResume();
                    } else {
                        fragments[i].onFragmentPause();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        findViewById(R.id.close_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            p.setMargins(0, 0, UIUtils.getPixels(28), 0);
            tab.requestLayout();
        }
    }

    public void show(@Nullable MusicContent selectMusic, boolean canSeekVol) {
        this.canSeekVol = canSeekVol;
        this.selectMusic = checkMusic(selectMusic);
        for (BaseEditMusicFragment fragment : fragments) {
            fragment.onSelectMusic(selectMusic, canSeekVol);
        }
        AnimUtils.Default.showFromBottom(contentView, 200);

        if (null != selectMusic) {
            selectMusic(selectMusic);
        }
    }

    @Nullable
    private MusicContent checkMusic(@Nullable MusicContent music) {
        if (music == null) {
            return null;
        }
        if (music.length <= 0L) {
            Toaster.show("歌曲文件失效！");
            return null;
        }
        return music;
    }

    public void hide() {
        AnimUtils.Default.hideToBottom(contentView, true, 200);
        if (onMusicListener != null) {
            onMusicListener.onHide();
        }
        for (BaseEditMusicFragment fragment : fragments) {
            fragment.release();
        }
        if (!canSeekVol) {
            player.release();
        }
    }

    public boolean isShowing() {
        return contentView.getVisibility() == View.VISIBLE;
    }

    public boolean onBackPress() {
        if (contentView.getVisibility() == View.VISIBLE) {
            hide();
            return true;
        }
        return false;
    }

    private <T extends View> T findViewById(@IdRes int id) {
        return (T) contentView.findViewById(id);
    }

    public boolean onActivityResultReceived(int requestCode, int resultCode, Intent data) {
        return false;
    }

    public void updatePlayingTime(final int position) {
        cutMusicFragment.updatePlayingTime(position);
    }

    @Override
    public void selectMusic(@NonNull MusicContent musicContent) {
        selectMusic = checkMusic(musicContent);
        if (selectMusic == null) {
            Toaster.show("歌曲错误！");
            return;
        }
        if (onMusicListener != null) {
            onMusicListener.onSelect(musicContent);
        }
        if (!canSeekVol) {
            try {
                player.reset();
                player.setMusic(musicContent.path);
                player.start(musicContent.startMillTime, musicContent.endMillTime, false, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void clearSelectMusic() {
        selectMusic = null;
        if (onMusicListener != null) {
            onMusicListener.clearMusic();
        }
        if (!canSeekVol) {
            player.release();
        }
    }

    @Override
    public void cutMusic(int startMillTime, int endMillTime) {
        if (onMusicListener != null) {
            onMusicListener.onCut(startMillTime, endMillTime);
        }
        if (!canSeekVol) {
            player.start(startMillTime, endMillTime, true, 1);
        }
    }

    @Override
    public void gotoCutPage() {
        if (viewPager == null) {
            return;
        }
        viewPager.setCurrentItem(1);
    }

    @Override
    public void setVolume(int percent) {
        if (onMusicListener != null) {
            onMusicListener.onVolumeChanged(percent);
        }
    }

    @Nullable
    @Override
    public MusicContent getSelectMusic() {
        return selectMusic;
    }

    @Override
    public int getVolume() {
        return initVolume;
    }

    public void onDestory() {
        for (BaseEditMusicFragment fragment : fragments) {
            fragment.release();
        }
    }

    public void onPause() {
        if (!canSeekVol) {
            if (selectMusic != null && player.isPlaying()) {
                player.pause();
                pauseMusic = true;
            }
        }
    }

    public void onResume() {
        if (!canSeekVol) {
            if (null != selectMusic && pauseMusic) {
                player.resume(1);
            }
        }
    }

    private class ViewPageAdapter extends FragmentPagerAdapter {

        public ViewPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        public String makeFragmentName(ViewGroup parent, long id) {
            return "android:switcher:e" + id;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        private String getFragmentName(int id) {
            switch (id) {
                case 0:
                    return "volFragment";
                case 1:
                    return "cutMusicFragment";
                default:
                    break;
            }
            return "EditMusicPanelHelper";
        }
    }

    public interface OnMusicListener {

        void onSelect(@NonNull MusicContent musicContent);

        void onCut(int startTime, int endTime);

        void onVolumeChanged(int percent);

        void onHide();

        void pauseVideo();

        void clearMusic();

    }

}
