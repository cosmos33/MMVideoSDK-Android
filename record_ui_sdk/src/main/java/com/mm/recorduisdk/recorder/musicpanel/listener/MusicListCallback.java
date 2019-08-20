package com.mm.recorduisdk.recorder.musicpanel.listener;

import com.mm.recorduisdk.recorder.model.MusicContent;
import com.mm.recorduisdk.recorder.musicpanel.widget.MusicRangeBar;

/**
 * Created by tangyuchun on 2018/5/15.
 */

public interface MusicListCallback {
    /**
     * 音乐点击后
     *
     * @param music
     */
    boolean onMusicClicked(MusicContent music);

    /**
     * 音乐下载完成后
     *
     * @param music
     */
    void onMusicDownloaded(MusicContent music, MusicRangeBar rangeBar);

    void onInitMusicDownloaded(MusicContent initMusic, MusicRangeBar rangeBar);

    void bindPlayingMusicRangeBar(MusicRangeBar currentBar);

    void onLocalClicked();
}
