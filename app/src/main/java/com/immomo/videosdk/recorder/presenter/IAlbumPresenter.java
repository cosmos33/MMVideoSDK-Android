package com.immomo.videosdk.recorder.presenter;

import com.immomo.videosdk.recorder.view.IAlbumView;
import com.immomo.videosdk.utils.album.AlbumCollection;

/**
 * Created by chenxin on 2018/8/31.
 */

public interface IAlbumPresenter {

    void loadMedia();

    void bindView(IAlbumView view);

    void destroy();

    AlbumCollection getCollection();
}
