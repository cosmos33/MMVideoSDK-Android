package com.mm.recorduisdk.recorder.presenter;

import androidx.annotation.NonNull;

import com.immomo.moment.ImageMovieManager;
import com.mm.mmutil.app.AppContext;
import com.mm.mmutil.task.MomoMainThreadExecutor;
import com.mm.recorduisdk.IRecordResourceConfig;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.RecordUISDK;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.bean.MMChooseMediaParams;
import com.mm.recorduisdk.config.Configs;
import com.mm.recorduisdk.recorder.editor.image_composition_video.bean.LiveAnimate;
import com.mm.recorduisdk.recorder.editor.image_composition_video.view.ILivePhotoPresenter;
import com.mm.recorduisdk.recorder.model.AlbumDirectory;
import com.mm.recorduisdk.recorder.model.AlbumItemModel;
import com.mm.recorduisdk.recorder.model.DirectoryModel;
import com.mm.recorduisdk.recorder.model.EmptyItemModel;
import com.mm.recorduisdk.recorder.model.Photo;
import com.mm.recorduisdk.recorder.model.TakePhotoModel;
import com.mm.recorduisdk.recorder.view.AlbumFragment;
import com.mm.recorduisdk.recorder.view.IAlbumFragment;
import com.mm.recorduisdk.utils.album.AlbumConstant;
import com.mm.recorduisdk.utils.album.ScanResult;
import com.momo.mcamera.mask.TransFieldGroupFilterChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangduanqing
 */
public class AlbumFragmentPresenter extends BaseAlbumPresenter implements ILivePhotoPresenter {

    private Map<AlbumDirectory, List<CementModel<?>>> models = new HashMap<>();
    private int newPosPhotoLength;
    private LiveAnimate mCurrentLiveAnimate;
    private LiveAnimate mCurrentAnimate;
    private final ImageMovieManager mImageMovieManager;
    private final TransFieldGroupFilterChooser mFieldGroupFilterChooser;


    public AlbumFragmentPresenter(@NonNull IAlbumFragment<AlbumFragment> fragment, @NonNull MMChooseMediaParams conditions) {
        super(fragment, conditions);
        mFieldGroupFilterChooser = new TransFieldGroupFilterChooser();
        mImageMovieManager = new ImageMovieManager();
        mImageMovieManager.setFilterChooser(mFieldGroupFilterChooser);
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
            if (mConditions.isShowCameraIcon()) {
                result.add(new TakePhotoModel());
            }
            return result;
        }
        List<CementModel<?>> result = new ArrayList<>(albumDirectory.getMedias().size());
        if (AlbumConstant.DIRECTORY_ID_ALL.equals(albumDirectory.getId())) {
            if (newPosPhotoLength > 0) {
                result.add(new DirectoryModel());
            } else {
                if (mConditions.isShowCameraIcon()) {
                    result.add(new TakePhotoModel());
                }
            }

            List<Photo> photos = mDirectories.get(AlbumConstant.INDEX_ALL_CATEGORY).getMedias();

            for (int i = 0; i < photos.size(); i++) {
                result.add(new AlbumItemModel(mFragment, mConditions, photos.get(i), needShowImage, imageSize, i));
                if (i == newPosPhotoLength - 1) {
                    result.add(new DirectoryModel("全部照片"));
                    if (mConditions.isShowCameraIcon()) {
                        result.add(new TakePhotoModel());
                    }
                }
            }

        } else {
            List<Photo> photos = albumDirectory.getMedias();
            int length = photos.size();
            for (int i = 0; i < length; i++) {
                result.add(new AlbumItemModel(mFragment, mConditions, photos.get(i), needShowImage, imageSize, i));
            }
        }

        for (int i = 0; i < 6; i++) {
            result.add(new EmptyItemModel());
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
            if (models.size() <= 0 && (mConditions != null && mConditions.isShowCameraIcon())) {
                models.add(new TakePhotoModel());
            }
            mAdapter.clearData();
            mAdapter.addModels(models);
            if (models.size() <= 0) {
                noMedia();
            }
        }
    }


    @Override
    public List<Photo> getLiveImageList() {
        return getSelectedMedias();
    }

    @Override
    public void deletePhoto(Photo photo) {
        onPostSelectClicked(photo);
        updatePhotoList();
    }

    @Override
    public void setLiveAnimate(LiveAnimate liveAnimate) {
        mCurrentLiveAnimate = liveAnimate;
        updateLivePhotoAnimate(liveAnimate);
    }

    @Override
    public void swapPhotoList(int srcPosition, int targetPosition) {
        List<Photo> liveImageList = getLiveImageList();
        Photo scrPhoto = liveImageList.remove(srcPosition);
        liveImageList.add(targetPosition, scrPhoto);
        notifyDataSetChanged();
    }

    @Override
    public LiveAnimate getCurrentAnimate() {
        return mCurrentLiveAnimate;
    }

    @Override
    public void startPhotoCompressVideo() {
        final File destFile = new File(Configs.getDir("record"), System.currentTimeMillis() + ".mp4");
        final String path = destFile.getPath();
        final IAlbumFragment fragment = mFragment;
        if (fragment != null) {
            fragment.showCompressDialog();
        }
        mImageMovieManager.makeImageToVideo(path, new ImageMovieManager.ImageMovieManagerListener() {
            @Override
            public void onError(int errorCode, String desp) {
            }

            @Override
            public void onProgress(final float ratio) {
                MomoMainThreadExecutor.post(new Runnable() {
                    @Override
                    public void run() {
                        final IAlbumFragment fragment = mFragment;
                        if (fragment != null) {
                            fragment.updateCompressDialog(ratio);
                        }
                    }
                });
            }

            @Override
            public void onComplete() {
                MomoMainThreadExecutor.post(new Runnable() {
                    @Override
                    public void run() {
                        final IAlbumFragment fragment = mFragment;
                        if (fragment != null) {
                            fragment.hideCompressDialog();
                        }
                        doFinishVideoChoose(path);
                    }
                });
            }
        });
    }

    public void updatePhotoList() {
        mImageMovieManager.updateImageList(getImages());
    }

    private List<String> getImages() {
        List<String> result = new ArrayList<>();
        List<Photo> liveImageList = getLiveImageList();
        for (Photo photo : liveImageList) {
            result.add(photo.tempPath);
        }
        return result;
    }

    @Override
    public List<LiveAnimate> getLiveAnimates() {
        List<LiveAnimate> liveAnimates = new ArrayList();
        liveAnimates.add(new LiveAnimate("简单", R.drawable.live_photo_animate_simple, LiveAnimate.AnimateType.ANIMATE_HORIZONTAL));

        LiveAnimate soft = new LiveAnimate("轻松", R.drawable.live_photo_animate_soft, LiveAnimate.AnimateType.ANIMATE_SOFT);
        liveAnimates.add(soft);

        LiveAnimate quic = new LiveAnimate("欢快", R.drawable.live_photo_animate_quic, LiveAnimate.AnimateType.ANIMATE_QUIC);
        liveAnimates.add(quic);

        LiveAnimate showLA = new LiveAnimate("秀动", R.drawable.live_photo_animate_show, LiveAnimate.AnimateType.ANIMATE_SHOW);
        liveAnimates.add(showLA);
        setLiveAnimate(liveAnimates.get(0));
        return liveAnimates;
    }

    @Override
    public void cancelImageConvert() {
        if (mImageMovieManager != null) {
            mImageMovieManager.cancelImageConvert(null);
        }
    }

    private void updateLivePhotoAnimate(@NonNull LiveAnimate liveAnimate) {
        mCurrentAnimate = liveAnimate;
        mImageMovieManager.setNeedPreWatermark(false);
        mImageMovieManager.setNeedPostWatermark(false);
        switch (liveAnimate.getAnimateType()) {
            case LiveAnimate.AnimateType.ANIMATE_SOFT: {
                mFieldGroupFilterChooser.setEffectTemplateSoft(AppContext.getContext(), "");
                break;
            }
            case LiveAnimate.AnimateType.ANIMATE_QUIC: {
                mFieldGroupFilterChooser.setEffectTemplateQuick(AppContext.getContext(), "", null);
                break;
            }
            case LiveAnimate.AnimateType.ANIMATE_SHOW: {
                IRecordResourceConfig<File> livePhotoHomeDirConfig = RecordUISDK.getResourceGetter().getLivePhotoHomeDirConfig();
                if (livePhotoHomeDirConfig != null && livePhotoHomeDirConfig.isOpen()) {
                    if (livePhotoHomeDirConfig.getResource() != null && livePhotoHomeDirConfig.getResource().exists()) {
                        mFieldGroupFilterChooser.setLookupPath(livePhotoHomeDirConfig.getResource() + "/show/Lookup");
                    }
                }
                mFieldGroupFilterChooser.setEffectSeries();
                break;
            }
            default:
            case LiveAnimate.AnimateType.ANIMATE_HORIZONTAL: {
                mFieldGroupFilterChooser.setEffectRightToLeft();
                break;
            }
        }

    }
}
