package com.mm.recorduisdk.recorder.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;

import com.mm.base_business.base.BaseTabOptionFragment;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.toast.Toaster;
import com.mm.recorduisdk.Constants;
import com.mm.recorduisdk.base.cement.CementAdapter;
import com.mm.recorduisdk.base.cement.CementModel;
import com.mm.recorduisdk.base.cement.SimpleCementAdapter;
import com.mm.recorduisdk.base.cement.eventhook.OnClickEventHook;
import com.mm.recorduisdk.bean.MMChooseMediaParams;
import com.mm.recorduisdk.recorder.MediaConstants;
import com.mm.recorduisdk.recorder.model.AlbumDirectory;
import com.mm.recorduisdk.recorder.model.AlbumItemModel;
import com.mm.recorduisdk.recorder.model.Photo;
import com.mm.recorduisdk.recorder.model.TakePhotoModel;
import com.mm.recorduisdk.recorder.model.Video;
import com.mm.recorduisdk.recorder.view.IAlbumFragment;
import com.mm.recorduisdk.utils.MediaSourceHelper;
import com.mm.recorduisdk.utils.VideoCompressUtil;
import com.mm.recorduisdk.utils.VideoUtils;
import com.mm.recorduisdk.utils.album.AlbumConstant;
import com.mm.recorduisdk.utils.album.ItemConstant;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mm.recorduisdk.utils.album.ItemConstant.TYPE_IMAGE;

/**
 * @author wangduanqing
 */
public abstract class BaseAlbumPresenter implements IAlbumFragmentPresenter {

    private static final int MODE_DEFAULT = 0;
    private static final int MODE_CHOOSE = 1;   //0001
    private static final int MODE_SELECT = 2;   //0010

    private OnItemClickListener mCLickListener;
    protected MMChooseMediaParams mConditions;
    protected SimpleCementAdapter mAdapter;
    protected IAlbumFragment mFragment;

    protected List<AlbumDirectory> mDirectories = new ArrayList<>();
    protected List<Photo> mSelectedMedias = new ArrayList<>();

    private int mCurrSelectedType = -1;
    protected int mCurrDirectoryIndex;
    private int mode = MODE_DEFAULT;
    private int maxCount;
    protected boolean needShowImage = false;
    protected int imageSize = (UIUtils.getScreenWidth() - UIUtils.getPixels(2) * 3) / 4;

    public BaseAlbumPresenter(IAlbumFragment fragment, MMChooseMediaParams conditions) {
        mFragment = fragment;
        mConditions = conditions;
        if (mConditions.getChooseMode() == Constants.EditChooseMode.MODE_MULTIPLE) {
            setSelectMode();
        }else {
            setChooseMode();
        }
    }

    @Override
    public void setAdapter(SimpleCementAdapter adapter) {
        if (adapter == null) {
            return;
        }
        adapter.addEventHook(new OnClickEventHook<AlbumItemModel.ViewHolder>(AlbumItemModel.ViewHolder.class) {
            @Override
            public void onClick(@NonNull View view, @NonNull AlbumItemModel.ViewHolder viewHolder, int position, @NonNull CementModel rawModel) {
                if (!AlbumItemModel.class.isInstance(rawModel)) {
                    return;
                }
                int curStatus = viewHolder.mItemLayout.mItemFlags & ItemConstant.STATUS_MASK;
                if (getCurrentMedias() == null || curStatus == ItemConstant.STATUS_DISABLE) {
                    return;
                }
                AlbumItemModel albumItemModel = ((AlbumItemModel) rawModel);

                final Photo item = albumItemModel.getPhoto();

                if (view == viewHolder.mItemLayout.mImageView) {
                    if (isSelectMode() && item.type == TYPE_IMAGE) {
                        onPostSelectClicked(item);
                    } else {
                        onPostImageClicked(item, albumItemModel.getPosition());
                    }
                } else if (view == viewHolder.mItemLayout.mSelectView) {
                    if (isChooseMode()) {
                        onPostImageClicked(item, albumItemModel.getPosition());
                    } else {
                        onPostSelectClicked(item);
                    }
                }
            }

            @Nullable
            @Override
            public List<? extends View> onBindMany(@NonNull AlbumItemModel.ViewHolder viewHolder) {
                return Arrays.asList(viewHolder.mItemLayout.mImageView, viewHolder.mItemLayout.mSelectView);
            }
        });

        adapter.addEventHook(new OnClickEventHook<TakePhotoModel.ViewHolder>(TakePhotoModel.ViewHolder.class) {
            @Override
            public void onClick(@NonNull View view, @NonNull TakePhotoModel.ViewHolder
                    viewHolder, int position, @NonNull CementModel rawModel) {
                bindTakePhotoLayout();
            }

            @Nullable
            @Override
            public View onBind(@NonNull TakePhotoModel.ViewHolder viewHolder) {
                return viewHolder.view;
            }
        });
        mAdapter = adapter;
    }

    protected FragmentActivity getActivity() {
        if (mFragment == null) {
            return null;
        }
        BaseTabOptionFragment fragment = (BaseTabOptionFragment) mFragment.getFragment();
        return fragment.getActivity();
    }

    public void update(int position, AlbumDirectory albumDirectory) {
        if (position == mCurrDirectoryIndex) {
            return;
        }
        clearSelectMedias();
        mSelectedMedias.clear();
        mCurrDirectoryIndex = position;
        mAdapter.removeAllModels();
        mAdapter.addModels(getModels(albumDirectory));
    }

    /**
     * 设置选中数据集合
     *
     * @param newSelectedMedias 新的选中数据集
     */
    public void setSelectMedias(@NonNull List<Photo> newSelectedMedias) {
        mSelectedMedias = newSelectedMedias;
        updateCurrSelectedType();
        if (mCLickListener != null) {
            mCLickListener.onSelectClick(mSelectedMedias.size());
        }
    }

    public void clearSelectMedias() {
        if (mSelectedMedias.size() <= 0) {
            return;
        }
        for (Photo photo : mSelectedMedias) {
            if (mFragment != null) {
                mFragment.changeChecked(photo, false);
            }
        }
        mCurrSelectedType = -1;
        mSelectedMedias.clear();
        if (mCLickListener != null) {
            mCLickListener.onSelectClick(mSelectedMedias.size());
        }
        refreshData();
    }

    private void updateCurrSelectedType() {
        int type = ItemConstant.TYPE_MASK;
        for (Photo media : mSelectedMedias) {
            type = type & media.type;
        }
        if (type == ItemConstant.TYPE_MASK || type == 0) {
            mCurrSelectedType = -1;
        } else {
            mCurrSelectedType = type;
        }
    }

    /**
     * 更新选中数据集
     */
    private void updateSelectMedias(Photo item, boolean isSelected) {
        if (isSelected) {
            if (!mSelectedMedias.contains(item)) {
                mSelectedMedias.add(item);
            }
        } else {
            if (mSelectedMedias.contains(item)) {
                mSelectedMedias.remove(item);
            }
        }
    }

    /**
     * 返回选中数据的index
     *
     * @param media 选中数据
     */
    public int getSelectNumber(Photo media) {
        return mSelectedMedias.indexOf(media) + 1;
    }

    private void bindTakePhotoLayout() {
        mFragment.gotoRecord(mConditions);
    }

    private void onPostImageClicked(Photo item, int position) {
        handleItemClick(item, position);
    }

    protected void onPostSelectClicked(Photo item) {

        final boolean isSelected = !mFragment.isChecked(item);

        int count = mSelectedMedias.size();
        count += (isSelected ? 1 : -1);

        final int mixCount = maxCount;
        if (mixCount <= 0) {
            Toaster.show("已选够6张图片");
            return;
        }
        if (count > mixCount) {
            Toaster.show("最多只能选" + mixCount + "个");
            return;
        }

        // 图片、视频选择互斥逻辑
        if (count <= 0) {
            mCurrSelectedType = -1;
        } else {
            mCurrSelectedType = item.type;
        }

        if (mFragment != null) {
            mFragment.changeChecked(item, isSelected);
        }

        updateSelectMedias(item, isSelected);

        notifyDataSetChanged();

        if (mCLickListener != null) {
            mCLickListener.onSelectClick(count);
        }
    }

    @Override
    public void handleItemClick(final Photo item, final int position) {
        if (item.type == ItemConstant.TYPE_VIDEO) {
            if ((mConditions.getMediaChooseType() & Constants.EditChooseMediaType.MEDIA_TYPE_VIDEO) == 0) {
                mFragment.showVideoAlertToast("不能选择视频");
                return;
            }

            doFinishVideoChoose(item.path);

        } else if (item.type == TYPE_IMAGE) {
            if ((mConditions.getMediaChooseType() & Constants.EditChooseMediaType.MEDIA_TYPE_IMAGE) == 0) {
                mFragment.showVideoAlertToast("不能选择图片");
                return;
            }

           /* if (mConditions.mode == VideoInfoTransBean.MODE_MULTIPLE) {
                ArrayList<Photo> result = new ArrayList<>(1);
                result.add(item);
                mFragment.handleResult(result);
                return;
            }*/
            if (mConditions.getChooseMode() == Constants.EditChooseMode.MODE_STYLE_ONE) {
                mSelectedMedias.clear();
                mSelectedMedias.add(item);
                mFragment.gotoImageEdit(item);
                return;
            }
            // 更新media在选中集合中的位置
            final List<Photo> selectMedias = mSelectedMedias;
            if (selectMedias != null) {
                final int size = selectMedias.size();
                for (int i = 0; i < size; i++) {
                    final Photo media = selectMedias.get(i);
                    media.positionInSelect = i;
                }
            }
            MediaSourceHelper.sAllMedias = getFilteredMedias(item);
            mFragment.gotoImagePreview(getFixedPosition(MediaSourceHelper.sAllMedias, position));
        }
    }

    protected void doFinishVideoChoose(String path) {
        final Video video = new Video();
        video.path = path;

        if (!checkValid(video)) {
            return;
        }
            /*if (mConditions.mode == VideoInfoTransBean.MODE_MULTIPLE) {
                ArrayList<Photo> result = new ArrayList<>(1);
                result.add(item);
                mFragment.handleResult(result);
                return;
            }*/
        video.isChosenFromLocal = true;
        long maxDuration = MediaConstants.MIN_CUT_VIDEO_DURATION;

        maxDuration += MediaConstants.MOMENT_DURATION_EXPAND;

        if (video.length > maxDuration) {
            mFragment.gotoVideoCut(video);
        } else {
            //获取修正过rotate的视频信息
            VideoUtils.getVideoFixMetaInfo(video);
            mFragment.gotoVideoEdit(video);
        }
    }

    /**
     * 当前media在过滤后的集合中位置
     *
     * @return List
     */
    private int getFixedPosition(List<Photo> filteredMedias, int position) {
        ArrayList<Photo> medias = mDirectories.get(mCurrDirectoryIndex).getMedias();
        if (position < 0 || position >= medias.size()) {
            return 0;
        }
        Photo media = medias.get(position);
        int fixedPosition = filteredMedias.indexOf(media);
        if (fixedPosition < 0) {
            fixedPosition = 0;
        }
        return fixedPosition;
    }

    /**
     * 获取所有的图片和视频集合
     *
     * @return List
     */
    protected ArrayList<Photo> getAllMedias() {
        return mDirectories.get(AlbumConstant.INDEX_ALL_CATEGORY).getMedias();
    }

    /**
     * 获取经过类型过滤后的集合
     *
     * @return List
     */
    private ArrayList<Photo> getFilteredMedias(Photo item) {

        List<Photo> medias = getCurrentMedias();
        ArrayList<Photo> result = new ArrayList<>();

        for (int i = 0, size = medias.size(); i < size; i++) {
            final Photo media = medias.get(i);

            if (media.type == item.type) {
                media.positionInAll = i;
                result.add(media);
            }
        }
        return result;
    }

    /**
     * 检查视频是否卖满足条件进行处理
     *
     * @param video Video
     */
    private boolean checkValid(Video video) {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return false;
        }
        if (video == null
                || TextUtils.isEmpty(video.path)
                || !VideoUtils.getVideoFixMetaInfo(video)
                || (video.frameRate > AlbumConstant.MAX_VIDEO_FRAME_RATE)) {
            mFragment.showNonsupportToast();
            return false;
        }
        final long minDuration = MediaConstants.MIN_VIDEO_DURATION;

        File file = new File(video.path);
        if (file.exists()) {
            video.size = (int) file.length();
        }
        video.avgBitrate = (int) ((long) video.size * 8000 / video.length);

        long maxDuration = MediaConstants.MIN_CUT_VIDEO_DURATION;

        maxDuration += MediaConstants.MOMENT_DURATION_EXPAND;

        if (VideoUtils.isLocalVideoSizeTooLarge(video)) {
            if (!mFragment.isCompressing()) {
                VideoCompressUtil.compressVideo(video
                        , VideoUtils.getMaxVideoSize()
                        , new VideoPickerCompressListener(mFragment, maxDuration, false));
            }
            return false;
        }

        return true;
    }

    public int getCurrentSelectedTType() {
        return mCurrSelectedType;
    }

    @Override
    public List<AlbumDirectory> getDirectories() {
        return mDirectories;
    }

    @Override
    public List<Photo> getCurrentMedias() {
        AlbumDirectory albumDirectory = getCurrentDirectory();
        if (albumDirectory != null) {
            return albumDirectory.getMedias();
        }
        return null;
    }

    private AlbumDirectory getCurrentDirectory() {
        if (mCurrDirectoryIndex >= mDirectories.size()) {
            return null;
        }
        return mDirectories.get(mCurrDirectoryIndex);
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public void refreshData() {
        if (mAdapter != null) {
            List<CementModel<?>> models = getModels(mCurrDirectoryIndex);
            mAdapter.clearData();
            mAdapter.addModels(models);
            if (models.size() <= 0) {
                noMedia();
            }
        }
    }

    @NonNull
    @Override
    public List<CementModel<?>> getModels(int position) {
        if (mCurrDirectoryIndex >= mDirectories.size()) {
            return new ArrayList<>();
        }
        AlbumDirectory albumDirectory = mDirectories.get(mCurrDirectoryIndex);
        return getModels(albumDirectory);
    }

    @Override
    public List<Photo> getSelectedMedias() {
        return mSelectedMedias;
    }

    public void changeDirectory(int position) {
        mCurrDirectoryIndex = position;
    }

    @Override
    public void setMaxSelectedCount(int count) {
        this.maxCount = count;
    }

    @Override
    public void noMedia() {
        //        mAdapter.setEmptyViewModel(new EmptyViewItemModel("暂时没有图片") {{
        //            setImageRes(R.drawable.ic_empty_people);
        //        }});
        //        mAdapter.checkEmptyView();
    }

    @Override
    public CementAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    public void setChooseMode() {
        mode = MODE_CHOOSE;
    }

    public void setSelectMode() {
        mode = MODE_SELECT;
    }

    public void setDefaultMode() {
        mode = MODE_DEFAULT;
    }

    private boolean isChooseMode() {
        return mode == MODE_CHOOSE;
    }

    private boolean isSelectMode() {
        return mode == MODE_SELECT;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        mCLickListener = l;
    }

    private static class VideoPickerCompressListener implements VideoCompressUtil.CompressVideoListener {

        private final WeakReference<IAlbumFragment> mReference;
        private final long mMaxDuration;
        private final boolean isNineToSixteen;

        private VideoPickerCompressListener(IAlbumFragment fragment, long maxDuration, boolean isNineToSixteen) {
            mReference = new WeakReference<>(fragment);
            mMaxDuration = maxDuration;
            this.isNineToSixteen = isNineToSixteen;
        }

        @Override
        public void onStartCompress() {
            final IAlbumFragment fragment = mReference.get();
            if (fragment != null) {
                fragment.showCompressDialog();
            }
        }

        @Override
        public void onUpdateCompress(float progress) {
            final IAlbumFragment fragment = mReference.get();
            if (fragment != null) {
                fragment.updateCompressDialog(progress);
            }
        }

        @Override
        public void onFinishCompress(Video result, boolean hasTranscoding) {
            final IAlbumFragment fragment = mReference.get();
            if (fragment == null) {
                return;
            }
            result.hasTranscoding = hasTranscoding;
            fragment.hideCompressDialog();

            result.isChosenFromLocal = true;

            if (VideoUtils.getVideoFixMetaInfo(result)) {
                float proportion = result.getWidth() / (float) result.height;
                if (isNineToSixteen && (0.54 > proportion || proportion > 0.58)) {
                    fragment.showVideoFormatError();
                    return;
                }
                if (result.length > mMaxDuration) {
                    fragment.gotoVideoCut(result);
                } else {
                    fragment.gotoVideoEdit(result);
                }
            } else {
                VideoUtils.deleteTempFile(result.path);
                fragment.onErrorCompress();
            }
        }

        @Override
        public void onErrorCompress(Video result) {
            VideoUtils.deleteTempFile(result.path);
            IAlbumFragment fragment = mReference.get();
            if (fragment != null) {
                fragment.hideCompressDialog();
                fragment.onErrorCompress();
            }
        }
    }

    public interface OnItemClickListener {

        void onItemClick(Photo item, int position);

        void onSelectClick(int count);
    }
}
