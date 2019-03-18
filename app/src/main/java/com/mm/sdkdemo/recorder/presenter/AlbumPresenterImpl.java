package com.mm.sdkdemo.recorder.presenter;

import android.support.v4.app.FragmentActivity;

import com.mm.sdkdemo.bean.VideoInfoTransBean;
import com.mm.sdkdemo.recorder.view.IAlbumView;
import com.mm.sdkdemo.utils.album.AlbumCollection;
import com.mm.sdkdemo.utils.album.ScanResult;

/**
 * @author wangduanqing
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
