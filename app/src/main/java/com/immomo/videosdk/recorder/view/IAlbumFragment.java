package com.immomo.videosdk.recorder.view;

import android.view.View;

import com.immomo.videosdk.bean.VideoInfoTransBean;
import com.immomo.videosdk.recorder.model.Photo;
import com.immomo.videosdk.recorder.model.Video;
import com.immomo.videosdk.utils.album.ScanResult;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

    void gotoRecord(VideoInfoTransBean transBean);

    void handleResult(List<Photo> obj);

    void showVideoFormatError();

    void onSendClick();

    void changeChecked(Photo photo, boolean isSelected);

    boolean isChecked(Photo photo);

    void showTip(View view);

    int getCurrentSelectedType();

    int getSelectNumber(Photo photo);

    void clearSelect();


    void showDirectoriesView();
}
