package com.immomo.videosdk.recorder.editor.player;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.TextureView;

import com.immomo.videosdk.recorder.sticker.StickerEntity;

import java.util.List;

import project.android.imageprocessing.filter.BasicFilter;

/**
 * Created by XiongFangyu on 2017/8/9.
 */

public interface IProcessPlayerView{

    void hideOrShowCover(boolean show, boolean release);

    void showProcessAudioProgress();

    TextureView getVideoView();

    void onPitchShiftProcessError(int msg, Exception e);

    void onPitchShiftProcessFinished();

    void onPlaying(long ptsMs);

    void onProcessProgress(float progress);

    void onProcessFinish();


    void onAddSticker(Rect viewRect, Bitmap bitmap, StickerEntity entity);

    void onPlayingPaused();

    List<BasicFilter> getSpeicalFilter();
}
