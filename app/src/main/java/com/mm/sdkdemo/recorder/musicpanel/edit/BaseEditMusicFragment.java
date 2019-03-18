package com.mm.sdkdemo.recorder.musicpanel.edit;

import android.support.annotation.NonNull;

import com.mm.sdkdemo.base.BaseFragment;
import com.mm.sdkdemo.recorder.model.MusicContent;

/**
 * create time 2018/11/20
 * by wangrenguang
 */
public abstract class BaseEditMusicFragment extends BaseFragment {

    protected IEditMusic iEditMusic;


    public void setiEditMusic(IEditMusic iEditMusic) {
        this.iEditMusic = iEditMusic;
    }


    public void onSelectMusic(@NonNull MusicContent musicContent, boolean canSeekVol) {
    }

    public void updatePlayingTime(int position) {

    }

    protected void onFragmentResume() {

    }

    protected void onFragmentPause() {

    }

    public void clearSelectMusic(){

    }

    public void release(){

    }

}
