package com.immomo.videosdk.recorder.presenter;

import com.immomo.videosdk.base.cement.CementModel;
import com.immomo.videosdk.bean.VideoInfoTransBean;
import com.immomo.videosdk.recorder.model.AlbumDirectory;
import com.immomo.videosdk.recorder.model.AlbumItemModel;
import com.immomo.videosdk.recorder.model.DirectoryModel;
import com.immomo.videosdk.recorder.model.LatLonPhotoList;
import com.immomo.videosdk.recorder.model.Photo;
import com.immomo.videosdk.recorder.model.TakePhotoModel;
import com.immomo.videosdk.recorder.view.AlbumFragment;
import com.immomo.videosdk.recorder.view.IAlbumFragment;
import com.immomo.videosdk.utils.MediaSourceHelper;
import com.immomo.videosdk.utils.album.AlbumConstant;
import com.immomo.videosdk.utils.album.ScanResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * @author shidefeng
 * @since 2017/6/12.
 */

public class AlbumFragmentPresenter extends BaseAlbumPresenter {

    private Map<AlbumDirectory, List<CementModel<?>>> models = new HashMap<>();
    private int newPosPhotoLength;


    public AlbumFragmentPresenter(@NonNull IAlbumFragment<AlbumFragment> fragment, @NonNull VideoInfoTransBean conditions) {
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
                    photo.isAlbumCheck = true;
                    mSelectedMedias.add(photo);
                } else {
                    photo.isAlbumCheck = false;
                }
            }
            return;
        }

        final List<Photo> allMedias = getCurrentMedias();

        if (resultIsEmpty) {
            for (Photo media : allMedias) {
                media.isCheck = false;
                media.isAlbumCheck = false;
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
                    media.isAlbumCheck = true;
                    mSelectedMedias.add(media);
                } else {
                    media.isAlbumCheck = false;
                }
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public List<CementModel<?>> getModels(@NonNull AlbumDirectory albumDirectory) {
        List<CementModel<?>> result = models.get(albumDirectory);
        if (result != null) {
            return result;
        }
        result = createModels(albumDirectory);
        models.put(albumDirectory, result);
        return result;
    }


    private List<CementModel<?>> createModels(@NonNull AlbumDirectory albumDirectory) {
        if (albumDirectory.getMedias().isEmpty()) {
            List<CementModel<?>> result = new ArrayList<>();
            if (mTransBean.isShowCamera()) {
                result.add(new TakePhotoModel());
            }
            return result;
        }
        List<CementModel<?>> result = new ArrayList<>(albumDirectory.getMedias().size());
        if (AlbumConstant.DIRECTORY_ID_ALL.equals(albumDirectory.getId())) {
            if (newPosPhotoLength > 0) {
                LatLonPhotoList lonPhotoList = MediaSourceHelper.sLatLonMedias;
                result.add(new DirectoryModel(mTransBean.hasLatLonPhotos ? (lonPhotoList == null ? "" : lonPhotoList.site) : ""));
            } else {
                if (mTransBean.isShowCamera()) {
                    result.add(new TakePhotoModel());
                }
            }

            List<Photo> photos = mDirectories.get(AlbumConstant.INDEX_ALL_CATEGORY).getMedias();

            for (int i = 0; i < photos.size(); i++) {
                result.add(new AlbumItemModel(mFragment, mTransBean, photos.get(i), needShowImage, imageSize, i));
                if (i == newPosPhotoLength - 1) {
                    result.add(new DirectoryModel("全部照片"));
                    if (mTransBean.isShowCamera()) {
                        result.add(new TakePhotoModel());
                    }
                }
            }

        } else {
            List<Photo> photos = albumDirectory.getMedias();
            int length = photos.size();
            for (int i = 0; i < length; i++) {
                result.add(new AlbumItemModel(mFragment, mTransBean, photos.get(i), needShowImage, imageSize, i));
            }
        }
        return result;
    }


    @Override
    public void onMultiMediaLoad(ScanResult scanResult) {
        models.clear();
        needShowImage = scanResult.showImage;
        newPosPhotoLength = scanResult.newPosPhotoLength;
        ArrayList<AlbumDirectory> list = scanResult.albumDirectories;
        // 设置默认选中全部文件夹
        changeDirectory(AlbumConstant.INDEX_ALL_CATEGORY);
        if (list != null) {
            mDirectories = scanResult.albumDirectories;
        } else {
            mDirectories.clear();
        }
        refreshData();
    }

    @Override
    public void refreshData() {
        if (mAdapter != null) {
            List<CementModel<?>> models = getModels(mCurrDirectoryIndex);
            if (models.size() <= 0 && (mTransBean != null && mTransBean.isShowCamera())) {
                models.add(new TakePhotoModel());
            }
            mAdapter.clearData();
            mAdapter.addModels(models);
            if (models.size() <= 0) {
                noMedia();
            }
        }
    }


}
