package com.mm.recorduisdk.recorder.musicpanel.listener;

import androidx.annotation.Nullable;

import com.mm.recorduisdk.recorder.model.MusicContent;

/**
 * 音乐最终选择后回调
 * Created by tangyuchun on 2018/5/16.
 */

public interface MusicPanelCallback {

    void onMusicSelected(@Nullable MusicContent music);

    void onMusicRangeChanged(int startOfMs, int endOfMs);

    /**
     * @param percent [0,100]
     */
    void onVolumeChanged(int percent);

    void onPanelClosed(@Nullable MusicContent selectedMusic);

    void onPanelShowed(@Nullable MusicContent selectedMusic);
}
