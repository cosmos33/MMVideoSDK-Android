package com.immomo.videosdk.recorder.presenter;

import com.immomo.videosdk.bean.VideoInfoTransBean;
import com.immomo.videosdk.recorder.view.IAlbumView;
import com.immomo.videosdk.utils.album.AlbumCollection;
import com.immomo.videosdk.utils.album.ScanResult;

import androidx.fragment.app.FragmentActivity;

/**
 * Created by chenxin on 2018/8/31.
 */

public class AlbumPresenterImpl implements IAlbumPresenter, AlbumCollection.OnMediaListener {

    private VideoInfoTransBean mTransBean;
    private AlbumCollection mCollection;
    private IAlbumView albumView;
    public AlbumPresenterImpl(VideoInfoTransBean mTransBean) {
        this.mTransBean = mTransBean;
    }

    @Override
    public void loadMedia() {
        mCollection = new AlbumCollection(mTransBean, mTransBean.mediaType, (FragmentActivity) albumView.getContext(), this);
        mCollection.load();
    }

    @Override
    public void bindView(IAlbumView view) {
        albumView = view;
    }

    @Override
    public void destroy() {
        if (mCollection != null) {
            mCollection.destroy();
            mCollection = null;
        }
    }

    @Override
    public AlbumCollection getCollection() {
        return mCollection;
    }

    @Override
    public void onMultiMediaLoad(ScanResult scanResult) {
        albumView.onMultiMediaLoad(scanResult);
    }

    @Override
    public void onMultiMediaReset() {

    }


    public interface onMultiMediaLoad {
        void onMultiMediaLoad(ScanResult scanResult);
    }
}
