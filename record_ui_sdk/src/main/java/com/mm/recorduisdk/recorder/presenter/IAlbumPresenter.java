package com.mm.recorduisdk.recorder.presenter;

import com.mm.recorduisdk.recorder.view.IAlbumView;
import com.mm.recorduisdk.utils.album.AlbumCollection;

/**
 * Created by chenxin on 2018/8/31.
 */

public interface IAlbumPresenter {

    void loadMedia();

    void bindView(IAlbumView view);

    void destroy();

    AlbumCollection getCollection();
}
