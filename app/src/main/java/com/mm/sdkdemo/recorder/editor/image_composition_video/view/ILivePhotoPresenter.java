package com.mm.sdkdemo.recorder.editor.image_composition_video.view;

import com.mm.sdkdemo.recorder.editor.image_composition_video.bean.LiveAnimate;
import com.mm.sdkdemo.recorder.model.Photo;

import java.util.List;

public interface ILivePhotoPresenter {


    List<Photo> getLiveImageList();

    void deletePhoto(Photo photo);

    void setLiveAnimate(LiveAnimate liveAnimate);

    void swapPhotoList(int srcPosition, int targetPosition);

    LiveAnimate getCurrentAnimate();

    void startPhotoCompressVideo();

    List<LiveAnimate> getLiveAnimates();

    void cancelImageConvert();
}
