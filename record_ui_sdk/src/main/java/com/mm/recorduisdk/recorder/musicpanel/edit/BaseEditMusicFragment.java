package com.mm.recorduisdk.recorder.musicpanel.edit;

import androidx.annotation.NonNull;

import com.mm.base_business.base.BaseFragment;
import com.mm.recorduisdk.recorder.model.MusicContent;

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
