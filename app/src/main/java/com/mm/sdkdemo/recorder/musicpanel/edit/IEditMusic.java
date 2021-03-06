package com.mm.sdkdemo.recorder.musicpanel.edit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mm.sdkdemo.recorder.model.MusicContent;

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
