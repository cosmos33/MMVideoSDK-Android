package com.immomo.videosdk.recorder.musicpanel.edit;

import com.immomo.videosdk.base.BaseFragment;
import com.immomo.videosdk.recorder.model.MusicContent;

import androidx.annotation.NonNull;

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
