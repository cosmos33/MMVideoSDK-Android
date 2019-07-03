package com.mm.sdkdemo.recorder.editor.image_composition_video.view;


import com.mm.sdkdemo.base.BaseFragment;

public abstract class BaseLivePhotoFragment extends BaseFragment {

    protected boolean isResume = false;
    protected ILivePhotoPresenter mPresenter;

    protected abstract void onChange();

    public void setResume(boolean isResume){
        this.isResume = isResume;
    }

    public void attachPresenter(ILivePhotoPresenter presenter){
        mPresenter = presenter;
    }
}
