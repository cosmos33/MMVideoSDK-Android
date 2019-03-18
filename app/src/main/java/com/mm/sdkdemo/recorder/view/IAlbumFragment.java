package com.mm.sdkdemo.recorder.view;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.mm.sdkdemo.bean.VideoInfoTransBean;
import com.mm.sdkdemo.recorder.model.Photo;
import com.mm.sdkdemo.recorder.model.Video;
import com.mm.sdkdemo.utils.album.ScanResult;

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
