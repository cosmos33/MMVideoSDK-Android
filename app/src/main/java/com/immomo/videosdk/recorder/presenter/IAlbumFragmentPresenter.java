package com.immomo.videosdk.recorder.presenter;

import com.immomo.videosdk.base.cement.CementAdapter;
import com.immomo.videosdk.base.cement.CementModel;
import com.immomo.videosdk.base.cement.SimpleCementAdapter;
import com.immomo.videosdk.recorder.model.AlbumDirectory;
import com.immomo.videosdk.recorder.model.Photo;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * @author shidefeng
 * @since 2017/6/13.
 */

public interface IAlbumFragmentPresenter extends AlbumPresenterImpl.onMultiMediaLoad {
    List<AlbumDirectory> getDirectories();

    List<Photo> getCurrentMedias();

    List<Photo> getSelectedMedias();

    void changeDirectory(int position);

    void handleItemClick(Photo item, int position);

    void updateSelectMedias(ArrayList<Photo> resultMedias, boolean backDirectly);

    void onDestroy();

    @NonNull
    List<CementModel<?>> getModels(int position);

    @NonNull
    List<CementModel<?>> getModels(AlbumDirectory albumDirectory);

    void setAdapter(SimpleCementAdapter adapter);

    void setMaxSelectedCount(int count);

    void refreshData();

    void notifyDataSetChanged();

    void noMedia();

    CementAdapter getAdapter();
}
