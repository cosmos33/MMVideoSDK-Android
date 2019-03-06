package com.immomo.videosdk.recorder.musicpanel.edit;

import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.immomo.mmutil.task.MomoMainThreadExecutor;
import com.immomo.videosdk.R;
import com.immomo.videosdk.recorder.model.MusicContent;
import com.immomo.videosdk.recorder.musicpanel.widget.MusicHorizontalScrollView;
import com.immomo.videosdk.recorder.musicpanel.widget.MusicWaveView;
import com.immomo.videosdk.utils.UIUtils;

import androidx.annotation.NonNull;

/**
 * create time 2018/11/20
 * by wangrenguang
 */
public class CutMusicFragment extends BaseEditMusicFragment {

    private TextView nameTv;
    private TextView timeTv;
    private MusicWaveView musicWave;
    private MusicHorizontalScrollView scrollBar;
    private MusicContent selectMusic = null;
    private View musicLayout;

    private int leftTime;
    private int rightTime = leftTime + MusicContent.MUSIC_LENGTH;

    @Override
    protected int getLayout() {
        return R.layout.fragment_edit_video_cut_music;
    }

    @Override
    protected void initViews(View contentView) {
        nameTv = contentView.findViewById(R.id.music_name);
        timeTv = contentView.findViewById(R.id.music_time);
        musicWave = contentView.findViewById(R.id.wave_view);
        scrollBar = contentView.findViewById(R.id.scroll_bar);
        musicLayout = contentView.findViewById(R.id.music_selector);
        scrollBar.setScrollViewListener(new MusicHorizontalScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldx, int oldy) {
                if (null != selectMusic) {
                    leftTime = (int) ((float) x / musicWave.getMusicLayoutWidth() * selectMusic.length);
                    rightTime = leftTime + MusicContent.MUSIC_LENGTH;
                    timeTv.setText(UIUtils.formatTime(leftTime) + "-" + UIUtils.formatTime(rightTime));
                }
            }

            @Override
            public void onScrollStop() {
                if (null != selectMusic) {
                    iEditMusic.cutMusic(leftTime, rightTime);
                }
            }
        });
    }

    @Override
    protected void onLoad() {
    }

    @Override
    public void onFragmentPause() {
        super.onPause();
    }

    @Override
    public void updatePlayingTime(int position) {
    }

    @Override
    public void onSelectMusic(@NonNull MusicContent musicContent, boolean canSeekVol) {
        selectMusic = musicContent;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onFragmentResume() {
        if (iEditMusic != null) {
            selectMusic = iEditMusic.getSelectMusic();
        }
        updateViews();
    }

    @Override
    public void release() {
    }

    @Override
    public void clearSelectMusic() {
        updateViews();
    }

    private void updateViews() {
        if (selectMusic != null) {
            musicLayout.setVisibility(View.VISIBLE);
            nameTv.setText(selectMusic.name);
            timeTv.setText(UIUtils.formatTime(leftTime) + "-" + UIUtils.formatTime(rightTime));
            musicWave.setDisplayTime(MusicContent.MUSIC_LENGTH);
            musicWave.setTotalTime(selectMusic.length);
            musicWave.layout();
            final int x = selectMusic.startMillTime * musicWave.getMusicLayoutWidth() / selectMusic.length;
            //            MDLog.i(LogTag.COMMON, "musicWave width: %d  x: %d", musicWave.getMusicLayoutWidth(), x);
            MomoMainThreadExecutor.post(new Runnable() {
                @Override
                public void run() {
                    scrollBar.scrollTo(x, 0);
                }
            });

        } else {
            leftTime = 0;
            rightTime = leftTime + MusicContent.MUSIC_LENGTH;
            scrollBar.scrollTo(0, 0);
            musicLayout.setVisibility(View.GONE);
            nameTv.setText(R.string.music_panel_tip_no_music_2);
            timeTv.setText((UIUtils.formatTime(0)));
        }
    }
}
