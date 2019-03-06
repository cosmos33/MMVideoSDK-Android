package com.immomo.videosdk.recorder.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.PopupWindow;

import com.immomo.videosdk.base.cement.SimpleCementAdapter;
import com.immomo.mmutil.toast.Toaster;
import com.immomo.videosdk.R;
import com.immomo.videosdk.base.BaseActivity;
import com.immomo.videosdk.base.BaseTabOptionFragment;
import com.immomo.videosdk.widget.decoration.GridItemDecoration;
import com.immomo.videosdk.bean.VideoInfoTransBean;
import com.immomo.videosdk.bean.VideoRecordDefs;
import com.immomo.videosdk.recorder.MediaConstants;
import com.immomo.videosdk.recorder.activity.ImageEditActivity;
import com.immomo.videosdk.recorder.activity.VideoCutActivity;
import com.immomo.videosdk.recorder.model.AlbumDirectory;
import com.immomo.videosdk.recorder.model.Photo;
import com.immomo.videosdk.recorder.model.Video;
import com.immomo.videosdk.recorder.presenter.AlbumFragmentPresenter;
import com.immomo.videosdk.recorder.presenter.BaseAlbumPresenter;
import com.immomo.videosdk.widget.DirectoriesPopWindow;
import com.immomo.videosdk.widget.DropDownTabInfo;
import com.immomo.videosdk.utils.MediaSourceHelper;
import com.immomo.videosdk.utils.UIUtils;
import com.immomo.videosdk.utils.VideoCompressUtil;
import com.immomo.videosdk.utils.album.AlbumConstant;
import com.immomo.videosdk.utils.album.AlbumResultHelper;
import com.immomo.videosdk.utils.album.ItemConstant;
import com.immomo.videosdk.utils.album.ScanResult;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.legacy.widget.Space;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.immomo.videosdk.recorder.activity.VideoRecordAndEditActivity.GOTO_WHERE;

/**
 * @author shidefeng
 * @since 2017/6/2.
 */

public class AlbumFragment extends BaseTabOptionFragment implements
        IAlbumFragment<AlbumFragment>,
        AlbumResultHelper.IResultWrapper<Parcelable>,
        BaseAlbumPresenter.OnItemClickListener {

    // request code
    private static final int REQUEST_CODE_IMAGE_PREVIEW = 10010;
    private static final int REQUEST_CODE_IMAGE_EDIT = 10011;
    private static final int REQUEST_CODE_CUT_VIDEO = 10012;
    private static final int REQUEST_CODE_RECORD = 10013;

    private VideoInfoTransBean mTransBean = new VideoInfoTransBean();
    private ProgressDialog progressDialog;
    private Bundle mArgs;
    private boolean mIsCompressing = false;//避免多次点击多次调用压缩方法
    private boolean mIsMediasLoaded;
    private boolean mBackDirectly;
    private String mediaType = MediaConstants.MEDIA_TYPE_IMAGES;
    private Video backVideo;

    private BaseAlbumPresenter mPresenter;

    private Space space;
    private DropDownTabInfo dropDownTabInfo;
    private ScanResult scanResult;

    public static AlbumFragment newInstance(Bundle args) {
        final AlbumFragment fragment = new AlbumFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArgs = getArguments();
        if (mArgs == null) {
            return;
        }
        if (mArgs.containsKey(MediaConstants.EXTRA_KEY_VIDEO_TRANS_INFO)) {
            VideoInfoTransBean transBean = mArgs.getParcelable(MediaConstants.EXTRA_KEY_VIDEO_TRANS_INFO);
            if (transBean != null) {
                mTransBean = transBean;
            }
        }
        // 获取瘦身、长腿tips显示的次数
        mPresenter = new AlbumFragmentPresenter(this, mTransBean);
        if (getTabInfo() != null && getTabInfo() instanceof DropDownTabInfo) {
            dropDownTabInfo = (DropDownTabInfo) getTabInfo();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_multimedia;
    }

    @Override
    protected void initViews(View view) {
        space = findView(view, R.id.space);
        final int halfSpace = getResources().getDimensionPixelOffset(R.dimen.multimedia_list_item_space_half);
        RecyclerView recyclerView = findView(view, R.id.rl_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridItemDecoration(halfSpace));
        SimpleCementAdapter adapter = new SimpleCementAdapter();
        adapter.setSpanCount(3);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setSpanSizeLookup(adapter.getSpanSizeLookup());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        mPresenter.setOnItemClickListener(this);
        mPresenter.setAdapter(adapter);
        mPresenter.setMaxSelectedCount(mTransBean.maxSelectedCount);
    }

    @Override
    protected void onLoad() {
    }

    private void showDirectoriesPopWindow() {
        final DirectoriesPopWindow directoriesPopWindow = new DirectoriesPopWindow(this.getActivity(), space);
        directoriesPopWindow.setOnDirectorySelect(new DirectoriesPopWindow.OnDirectorySelectListener() {
            @Override
            public void onSelect(int position, AlbumDirectory albumDirectory) {
                if (dropDownTabInfo != null) {
                    dropDownTabInfo.setTitle(albumDirectory.getName());
                }
                directoriesPopWindow.dismiss();
                if (albumDirectory == null || albumDirectory.getMedias() == null || albumDirectory.getMedias().isEmpty()) {
                    mPresenter.noMedia();
                    return;
                }
                mPresenter.update(position, albumDirectory);
            }
        });
        directoriesPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (dropDownTabInfo != null) {
                    dropDownTabInfo.setSelect(false);
                }
            }
        });
        directoriesPopWindow.setDirectories(mPresenter.getDirectories());
        directoriesPopWindow.show();
        if (dropDownTabInfo != null) {
            dropDownTabInfo.setSelect(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onFragmentResume() {
        super.onFragmentResume();
        if (mIsMediasLoaded) {
            mPresenter.setSelectMedias(mPresenter.getSelectedMedias());
        } else if (scanResult != null) {
            onActivityMediasLoaded(scanResult);
            mPresenter.setSelectMedias(mPresenter.getSelectedMedias());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
    }

    @Override
    public void onActivityResultReceived(int requestCode, int resultCode, Intent data) {
        super.onActivityResultReceived(requestCode, resultCode, data);
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_IMAGE_PREVIEW:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    receivedFromImagePreview(data);
                    return;
                }
                onReceivedCanceled();
                break;
            case REQUEST_CODE_IMAGE_EDIT:
                if (resultCode != Activity.RESULT_OK || data == null) {
                    onReceivedCanceledFromEdit();
                    return;
                }
                receivedFromImageEdit(resultCode, data);
                break;
            case REQUEST_CODE_CUT_VIDEO:
                if (resultCode != Activity.RESULT_OK || data == null) {
                    closeDialog();
                    return;
                }
                if (mArgs == null) {
                    mArgs = getArguments();
                }
                if (mArgs != null) {
                    mArgs.putParcelable(MediaConstants.EXTRA_KEY_VIDEO_DATA,
                                        data.getParcelableExtra(MediaConstants.KEY_PICKER_VIDEO));
                }
                receiveFromVideoCut(resultCode, data);
                break;
            case REQUEST_CODE_RECORD:
                if (resultCode != Activity.RESULT_OK || data == null) {
                    return;
                }
                receivedFromRecord(data);
                break;
            default:
                break;
        }

    }

    private void onReceivedCanceledFromEdit() {
        if (mPresenter == null) {
            return;
        }
        List<Photo> selectedMedias = mPresenter.getSelectedMedias();
        if (selectedMedias == null || selectedMedias.isEmpty()) {
            return;
        }
        final Iterator<Photo> it = selectedMedias.iterator();
        while (it.hasNext()) {
            final Photo photo = it.next();
            photo.changeChecked(false);
            photo.isPictureCheck = false;
            photo.isAlbumCheck = false;
        }
        if (mPresenter != null) {
            selectedMedias.clear();
            mPresenter.setSelectMedias(selectedMedias);
            mPresenter.notifyDataSetChanged();
        }
    }

    private void onReceivedCanceled() {
        if (mPresenter == null) {
            return;
        }
        List<Photo> selectedMedias = mPresenter.getSelectedMedias();
        if (selectedMedias == null || selectedMedias.isEmpty()) {
            return;
        }
        final Iterator<Photo> it = selectedMedias.iterator();
        while (it.hasNext()) {
            final Photo photo = it.next();
            if (!photo.isCheck) {
                photo.isAlbumCheck = false;
                it.remove();
            } else {
                photo.isAlbumCheck = true;
            }
        }
        if (mPresenter != null && mPresenter.getAdapter() != null) {
            mPresenter.setSelectMedias(selectedMedias);
            mPresenter.notifyDataSetChanged();
        }
    }

    private void receivedFromImagePreview(@NonNull Intent data) {
        final Activity activity = getActivity();
        if (mPresenter == null || mPresenter.getAdapter() == null || activity == null) {
            return;
        }

        final boolean isPublish = data.getBooleanExtra(AlbumConstant.KEY_RESULT_IS_PUBLISH, false);
        ArrayList<Photo> resultMedias = data.getParcelableArrayListExtra(AlbumConstant.KEY_RESULT_MEDIA_LIST);

        if (mPresenter != null) {
            mPresenter.updateSelectMedias(resultMedias, isPublish);
        }

        if (isPublish) {
            mBackDirectly = false;
            List<Photo> photos = mPresenter == null ? null : mPresenter.getSelectedMedias();
            handleResult(photos);
            return;
        }

        if (mPresenter != null) {
            mPresenter.setSelectMedias(mPresenter.getSelectedMedias());
            mPresenter.notifyDataSetChanged();
            //            IAlbumView view = (IAlbumView) getParentFragment();
            //            if (view != null && getUserVisibleHint()) {
            //                view.onSelectClick(mAdapter.getmSelectedMediasCount(), mTransBean.sendText);
            //            }
        }
    }

    private void receivedFromImageEdit(int resultCode, Intent data) {
        ArrayList<Photo> resultMedias = new ArrayList<>();
        Photo photo = data.getParcelableExtra(AlbumConstant.KEY_RESULT_IMAGE_EDIT);
        if (photo != null) {
            resultMedias.add(photo);
        }

        final Intent intent = new Intent();
        intent.putParcelableArrayListExtra(MediaConstants.EXTRA_KEY_IMAGE_DATA, resultMedias);

        final FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            activity.setResult(resultCode, intent);
            activity.finish();
        }
    }

    public void receivedFromRecord(Intent data) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        activity.setResult(Activity.RESULT_OK, data);
        activity.finish();
    }

    private void receiveFromVideoCut(int resultCode, Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle == null) {
            return;
        }
        if (resultCode == RESULT_OK && bundle.getBoolean(MediaConstants.KEY_CUT_VIDEO_RESULT)) {
            Video video = bundle.getParcelable(MediaConstants.KEY_PICKER_VIDEO);
            gotoVideoEdit(video);
            return;
        }
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        Toaster.show("视频格式不正确");
    }

    /**
     * implement {@link IAlbumFragment}
     */
    @NonNull
    @Override
    public AlbumFragment getFragment() {
        return this;
    }

    @Override
    public void onActivityMediasLoaded(ScanResult scanResult) {
        if (getUserVisibleHint() || mIsMediasLoaded) {
            if (mPresenter != null) {
                mPresenter.onMultiMediaLoad(scanResult);
            }
            mIsMediasLoaded = true;
        } else {
            this.scanResult = scanResult;
        }
    }

    @Override
    public void showVideoAlertToast(String alert) {
        Toaster.show(alert);
    }

    @Override
    public void showNonsupportToast() {
        Toaster.show("该视频不支持");
    }

    @Override
    public void showCompressDialog() {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mIsCompressing = false;
                    VideoCompressUtil.stopCompress();
                    Toaster.showInvalidate("已停止压缩", Toaster.LENGTH_SHORT);
                    hideCompressDialog();
                }
            });
        }
        progressDialog.setMessage("视频压缩中......");
        final Window window = progressDialog.getWindow();
        if (window != null) {
            window.setLayout(UIUtils.getPixels(170), UIUtils.getPixels(50));
        }
        if (!progressDialog.isShowing()) {
            showDialog(progressDialog);
        }
        mIsCompressing = true;
    }

    @Override
    public void updateCompressDialog(float progress) {
        if (progress > 1.0f) {
            progress = 1.0f;
        }
        String str = "正在压缩 " + (int) (progress * 100) + "%";
        if (mIsCompressing) {
            if (!progressDialog.isShowing()) {
                showDialog(progressDialog);
            }
            progressDialog.setMessage(str);
        }
    }

    @Override
    public void hideCompressDialog() {
        Activity ac = getActivity();
        if (ac == null) {
            return;
        }
        BaseActivity activity = (BaseActivity) ac;

        if (activity.isDestroyed()) {
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        mIsCompressing = false;
    }

    @Override
    public boolean isCompressing() {
        return mIsCompressing;
    }

    @Override
    public void onErrorCompress() {
        mIsCompressing = false;
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Toaster.show("压缩异常，请稍后再试");
        activity.setResult(RESULT_OK, null);
    }

    @Override
    public void gotoVideoCut(Video video) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        // 跳转至视频裁剪界面
        Intent it = new Intent(activity, VideoCutActivity.class);
        it.putExtra(VideoRecordDefs.KEY_VIDEO, video);
        it.putExtra(VideoRecordDefs.VIDEO_LENGTH_TIME, MediaConstants.MAX_LONG_VIDEO_DURATION);
        it.putExtra(VideoRecordDefs.VIDEO_MIN_CUT_TIME, MediaConstants.MIN_VIDEO_DURATION);
        getActivity().startActivityForResult(it, REQUEST_CODE_CUT_VIDEO);
    }

    @Override
    public void gotoVideoEdit(Video video) {
        if (video.length > 0 && video.avgBitrate <= 0) {
            File file = new File(video.path);
            video.size = (int) (file.length());
            //文件大小以B为单位，比特率以 bps 为单位，length以ms为单位
            video.avgBitrate = (int) (video.size * 1f / video.length * 8000);
        }
        mTransBean.state = VideoInfoTransBean.STATE_CHOOSE_MEDIA;
        mTransBean.initAlbumIndex = AlbumHomeFragment.STATE_ALBUM;
        mArgs.putParcelable(MediaConstants.EXTRA_KEY_VIDEO_DATA, video);
        mArgs.putParcelable(MediaConstants.EXTRA_KEY_VIDEO_TRANS_INFO, mTransBean);
        mArgs.putString(GOTO_WHERE, VideoEditFragment.class.getSimpleName());

        IAlbumView albumView = (IAlbumView) getParentFragment();
        if (albumView != null) {
            albumView.changeFragment(this, mArgs);
        }
    }

    @Override
    public void gotoImagePreview(int fixedPosition) {
    }

    @Override
    public void gotoImageEdit(Photo photo) {
        Intent intent = new Intent(getActivity(), ImageEditActivity.class);
        intent.putExtra(AlbumConstant.KEY_EDIT_MEDIA, photo);
        if (mTransBean.extraBundle != null) {
            intent.putExtras(mTransBean.extraBundle);
        }
        getActivity().startActivityForResult(intent, REQUEST_CODE_IMAGE_EDIT);
    }

    @Override
    public void gotoRecord(VideoInfoTransBean transBean) {
        transBean.showAlbum = false;
        transBean.video = null;
        transBean.initAlbumIndex = AlbumHomeFragment.STATE_ALBUM;
        IAlbumView albumView = (IAlbumView) getParentFragment();
        if (transBean.mediaType != AlbumConstant.MEDIA_TYPE_IMAGE) {
            mArgs.putInt(MediaConstants.EXTRA_KEY_VIDEO_STATE, VideoInfoTransBean.STATE_DEFAULT_RECORD);
        }
        mArgs.putString(GOTO_WHERE, VideoRecordFragment.class.getSimpleName());
        if (albumView != null) {
            albumView.changeFragment(this, mArgs);
        }
    }

    public void onSendClick() {
        mBackDirectly = false;
        List<Photo> photos = mPresenter == null ? null : mPresenter.getSelectedMedias();
        if (photos == null || photos.size() <= 0) {
            return;
        }
        handleResult(photos);
    }

    @Override
    public void changeChecked(Photo photo, boolean isSelected) {
        photo.isAlbumCheck = isSelected;
        photo.changeChecked(isSelected);
    }

    @Override
    public boolean isChecked(Photo photo) {
        return photo.isAlbumCheck;
    }

    @Override
    public void showTip(final View view) {
        if (mTransBean == null || TextUtils.isEmpty(mTransBean.chooseMediaTips)) {
            return;
        }
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (mTransBean == null) {
            return;
        }
//        String from = mTransBean.from;
//        final AlbumTipDialog dialog = new AlbumTipDialog(activity);
//
//        dialog.setTips(mTransBean.chooseMediaTips);
//        if (!TextUtils.isEmpty(from)) {
//            if (showCount >= 2) {
//                return;
//            }
//            view.post(new Runnable() {
//                @Override
//                public void run() {
//                    //防止activity还没有加载完成就show()。
//                    dialog.show(view);
//                }
//            });
//            mTransBean.chooseMediaTips = "";
//            showCount++;
//        } else {
//            view.post(new Runnable() {
//                @Override
//                public void run() {
//                    //防止activity还没有加载完成就show()。
//                    dialog.show(view);
//                }
//            });
//            mTransBean.chooseMediaTips = "";
//        }

        Toaster.show(mTransBean.chooseMediaTips);
    }

    @Override
    public int getCurrentSelectedType() {
        return mPresenter.getCurrentSelectedTType();
    }

    @Override
    public int getSelectNumber(Photo photo) {
        return mPresenter.getSelectNumber(photo);
    }

    @Override
    public void clearSelect() {
        if (mPresenter != null) {
            mPresenter.clearSelectMedias();
        }
    }

    @Override
    public void showDirectoriesView() {
        showDirectoriesPopWindow();
    }

    @Override
    public void handleResult(List<Photo> photo) {
        Activity activity = getActivity();
        if (activity == null)
            return;
        mediaType = MediaConstants.MEDIA_TYPE_IMAGES;
        backVideo = null;
        if (photo.size() == 1) {
            Photo p = photo.get(0);
            if (p.type == ItemConstant.TYPE_VIDEO) {
                mediaType = MediaConstants.MEDIA_TYPE_VIDEO;
                backVideo = new Video(p.path);
            }
        }
        AlbumResultHelper.handleResult((BaseActivity) activity, this);
    }

    @Override
    public void showVideoFormatError() {
        Toaster.show("视频介绍仅支持竖屏9:16视频");
    }

    /**
     * implement {@link AlbumResultHelper.IResultWrapper}
     */
    @Override
    public ArrayList<Parcelable> getResultMedias() {
        ArrayList<Parcelable> result = null;
        if (backVideo != null && TextUtils.equals(mediaType, MediaConstants.MEDIA_TYPE_VIDEO)) {
            result = new ArrayList<>();
            result.add(backVideo);
            return result;
        }
        List<Photo> photos = mPresenter == null ? null : mPresenter.getSelectedMedias();
        result = new ArrayList<>();
        result.addAll(photos);
        return result;
    }

    @Override
    public String getMediaType() {
        return mediaType;
    }

    @NonNull
    @Override
    public String getDataKey() {
        return MediaConstants.EXTRA_KEY_IMAGE_DATA;
    }

    @Override
    public String getGotoActivityName() {
        return mTransBean.gotoActivityName;
    }

    @Override
    public Bundle getExtras() {
        return mTransBean.extraBundle;
    }

    @Override
    public boolean backDirectly() {
        return mBackDirectly;
    }

    @Override
    public String getPresentContent() {
        if (mTransBean == null || !mTransBean.hasLatLonPhotos) {
            return null;
        }
        return MediaSourceHelper.sLatLonMedias == null ? null : MediaSourceHelper.sLatLonMedias.site;
    }

    @Override
    public VideoInfoTransBean getVideoInfoTransBean() {
        return mTransBean;
    }

    @Override
    public void onItemClick(Photo item, int position) {
        if (mPresenter != null) {
            mPresenter.handleItemClick(item, position);
        }
    }

    @Override
    public void onSelectClick(int count) {
        IAlbumView view = (IAlbumView) getParentFragment();
        if (view != null) {
            view.onSelectClick(count, mTransBean.sendText);
        }
    }

    @SuppressWarnings("unchecked")
    public static <V extends View> V findView(View v, @IdRes int id) {
        return (V) v.findViewById(id);
    }

}
