package com.mm.recorduisdk.recorder.presenter;

import androidx.annotation.NonNull;

import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.SimpleCementAdapter;
import com.mm.recorduisdk.recorder.model.AlbumDirectory;
import com.mm.recorduisdk.recorder.model.Photo;

import java.util.ArrayList;
import java.util.List;

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
