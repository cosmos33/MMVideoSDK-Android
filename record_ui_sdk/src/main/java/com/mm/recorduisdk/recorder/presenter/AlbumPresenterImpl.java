package com.mm.recorduisdk.recorder.presenter;

import androidx.fragment.app.FragmentActivity;

import com.mm.recorduisdk.bean.MMChooseMediaParams;
import com.mm.recorduisdk.recorder.view.IAlbumView;
import com.mm.recorduisdk.utils.album.AlbumCollection;
import com.mm.recorduisdk.utils.album.ScanResult;

/**
 * @author wangduanqing
 */
public class AlbumPresenterImpl implements IAlbumPresenter, AlbumCollection.OnMediaListener {

    private MMChooseMediaParams mChooseMediaParams;
    private AlbumCollection mCollection;
    private IAlbumView albumView;

    public AlbumPresenterImpl(MMChooseMediaParams params) {
        this.mChooseMediaParams = params;
    }

    @Override
    public void loadMedia() {
        mCollection = new AlbumCollection(mChooseMediaParams, mChooseMediaParams.getMediaChooseType(), (FragmentActivity) albumView.getContext(), this);
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
