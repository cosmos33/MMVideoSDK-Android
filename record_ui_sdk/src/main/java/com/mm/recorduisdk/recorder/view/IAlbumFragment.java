package com.mm.recorduisdk.recorder.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mm.recorduisdk.bean.MMChooseMediaParams;
import com.mm.recorduisdk.recorder.model.Photo;
import com.mm.recorduisdk.recorder.model.Video;
import com.mm.recorduisdk.utils.album.ScanResult;

import java.util.List;

/**
 * @author shidefeng
 * @since 2017/6/7.
 */

public interface IAlbumFragment<T extends Fragment> {

    @NonNull
    T getFragment();

    void onActivityMediasLoaded(ScanResult scanResult);

    void showVideoAlertToast(String alert);

    void showNonsupportToast();

    void showCompressDialog();

    void updateCompressDialog(float progress);

    void hideCompressDialog();

    boolean isCompressing();

    void onErrorCompress();

    void gotoVideoCut(Video video);

    void gotoVideoEdit(Video video);

    void gotoImagePreview(int fixedPosition);

    void gotoImageEdit(Photo photo);

    void gotoRecord(MMChooseMediaParams params);

    void handleResult(List<Photo> obj);

    void showVideoFormatError();

    void onSendClick();

    void changeChecked(Photo photo, boolean isSelected);

    boolean isChecked(Photo photo);

    int getCurrentSelectedType();

    int getSelectNumber(Photo photo);

    void clearSelect();


    void showDirectoriesView();
}
