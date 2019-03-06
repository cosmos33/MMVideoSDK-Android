package com.immomo.videosdk.recorder.musicpanel.listener;

import com.immomo.videosdk.recorder.model.MusicContent;

import androidx.annotation.Nullable;

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
