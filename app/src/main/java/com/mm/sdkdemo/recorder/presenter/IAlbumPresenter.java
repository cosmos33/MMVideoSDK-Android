package com.mm.sdkdemo.recorder.presenter;

import com.mm.sdkdemo.recorder.view.IAlbumView;
import com.mm.sdkdemo.utils.album.AlbumCollection;

/**
 * Created by chenxin on 2018/8/31.
 */

public interface IAlbumPresenter {

    void loadMedia();

    void bindView(IAlbumView view);

    void destroy();

    AlbumCollection getCollection();
}
