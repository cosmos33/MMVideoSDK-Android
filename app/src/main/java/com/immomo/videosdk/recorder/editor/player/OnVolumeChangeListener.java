package com.immomo.videosdk.recorder.editor.player;

import androidx.annotation.IntRange;

/**
 * Created by tangyuchun on 2018/5/23.
 */

public interface OnVolumeChangeListener {

    void onMusicVolumeChanged(@IntRange(from = 0, to = 100) int musicVolume);

    void onVideoVolumeChanged(@IntRange(from = 0, to = 100) int videoVolume);

}
