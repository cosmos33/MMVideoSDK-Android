package com.immomo.videosdk.recorder.presenter;

import com.immomo.videosdk.base.cement.CementModel;
import com.immomo.videosdk.bean.VideoInfoTransBean;
import com.immomo.videosdk.recorder.model.AlbumDirectory;
import com.immomo.videosdk.recorder.model.AlbumItemModel;
import com.immomo.videosdk.recorder.model.Photo;
import com.immomo.videosdk.recorder.view.IAlbumFragment;
import com.immomo.videosdk.recorder.view.VideoFragment;
import com.immomo.videosdk.utils.album.AlbumConstant;
import com.immomo.videosdk.utils.album.ScanResult;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by chenxin on 2018/8/28.
 */

public class VideoFragmentPresenter extends BaseAlbumPresenter {

    private List<CementModel<?>> models = new ArrayList<>();

    public VideoFragmentPresenter(@NonNull IAlbumFragment<VideoFragment> fragment, @NonNull VideoInfoTransBean conditions) {
        super(fragment, conditions);
    }

    /**
     * 图片编辑页返回更新数据
     *
     * @param resultMedias 返回的选中数据
     * @param backDirectly 是否从相册页直接返回，如跳转到发布页面等
     */
    @Override
    public void updateSelectMedias(ArrayList<Photo> resultMedias, boolean backDirectly) {

        boolean resultIsEmpty = (resultMedias == null || resultMedias.isEmpty());

        mSelectedMedias.clear();

        if (backDirectly) {
            if (resultIsEmpty) {
                return;
            }
            // 返回列表中，可能存在编辑了图片，但是没有勾选的情况
            for (Photo photo : resultMedias) {
                if (photo.isCheck) {
                    mSelectedMedias.add(photo);
                }
            }
            return;
        }

        final List<Photo> allMedias = getCurrentMedias();

        if (resultIsEmpty) {
            for (Photo media : allMedias) {
                media.isCheck = false;
            }
        } else {
            for (Photo photo : resultMedias) {
                final int index = allMedias.indexOf(photo);
                if (index < 0) {
                    continue;
                }
                final Photo media = allMedias.get(index);
                media.setPhoto(photo);

                // 返回列表中，可能存在编辑了图片，但是没有勾选的情况
                if (media.isCheck) {
                    mSelectedMedias.add(media);
                }
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public List<CementModel<?>> getModels(AlbumDirectory albumDirectory) {
        if (models != null) {
            return models;
        }

        List<Photo> photos = mDirectories.get(AlbumConstant.INDEX_ALL_CATEGORY).getMedias();
        int length = photos.size();
        models = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            models.add(new AlbumItemModel(mFragment, mTransBean, photos.get(i), needShowImage, imageSize, i));
        }
        return models;
    }


    @Override
    public void onMultiMediaLoad(ScanResult scanResult) {
        models = null;
        needShowImage = scanResult.showImage;
        ArrayList<AlbumDirectory> list = scanResult.videoDirectories;
        // 设置默认选中全部文件夹
        changeDirectory(AlbumConstant.INDEX_ALL_CATEGORY);
        if (list != null) {
            mDirectories = scanResult.videoDirectories;
        } else {
            mDirectories.clear();
        }
        refreshData();
    }
}
