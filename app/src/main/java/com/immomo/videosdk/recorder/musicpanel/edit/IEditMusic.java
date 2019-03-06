package com.immomo.videosdk.recorder.musicpanel.edit;

import com.immomo.videosdk.recorder.model.MusicContent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * create time 2018/11/20
 * by wangrenguang
 */
public interface IEditMusic {


    void selectMusic(@NonNull MusicContent musicContent);

    void clearSelectMusic();

    void cutMusic(int startMillTime, int endMillTime);

    void gotoCutPage();

    void setVolume(int percent);

    @Nullable
    MusicContent getSelectMusic();

    int getVolume();
}
