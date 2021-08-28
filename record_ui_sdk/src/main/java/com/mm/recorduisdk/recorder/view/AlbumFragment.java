package com.mm.recorduisdk.recorder.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.PopupWindow;
import android.widget.Space;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mm.base_business.base.BaseActivity;
import com.mm.base_business.base.BaseTabOptionFragment;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.toast.Toaster;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.base.cement.SimpleCementAdapter;
import com.mm.recorduisdk.bean.FinishGotoInfo;
import com.mm.recorduisdk.bean.MMChooseMediaParams;
import com.mm.recorduisdk.bean.MMImageEditParams;
import com.mm.recorduisdk.bean.MMVideoEditParams;
import com.mm.recorduisdk.bean.VideoRecordDefs;
import com.mm.recorduisdk.recorder.MediaConstants;
import com.mm.recorduisdk.recorder.activity.ImageEditActivity;
import com.mm.recorduisdk.recorder.activity.VideoCutActivity;
import com.mm.recorduisdk.recorder.activity.VideoRecordAndEditActivity;
import com.mm.recorduisdk.recorder.editor.image_composition_video.bean.LiveAnimate;
import com.mm.recorduisdk.recorder.editor.image_composition_video.view.LivePhotoFuctionHelper;
import com.mm.recorduisdk.recorder.model.AlbumDirectory;
import com.mm.recorduisdk.recorder.model.Photo;
import com.mm.recorduisdk.recorder.model.Video;
import com.mm.recorduisdk.recorder.presenter.AlbumFragmentPresenter;
import com.mm.recorduisdk.recorder.presenter.BaseAlbumPresenter;
import com.mm.recorduisdk.utils.VideoCompressUtil;
import com.mm.recorduisdk.utils.album.AlbumConstant;
import com.mm.recorduisdk.utils.album.AlbumResultHelper;
import com.mm.recorduisdk.utils.album.ItemConstant;
import com.mm.recorduisdk.utils.album.ScanResult;
import com.mm.recorduisdk.widget.DirectoriesPopWindow;
import com.mm.recorduisdk.widget.DropDownTabInfo;
import com.mm.recorduisdk.widget.decoration.GridItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.mm.recorduisdk.recorder.activity.VideoRecordAndEditActivity.GOTO_WHERE;

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

    private MMChooseMediaParams mChooseMediaParams = new MMChooseMediaParams.Builder().build();
    private ProgressDialog progressDialog;
    private Bundle mArgs;
    private boolean mIsCompressing = false;//避免多次点击多次调用压缩方法
    private boolean mIsMediasLoaded;
    private boolean mBackDirectly;
    private String mediaType = MediaConstants.MEDIA_TYPE_IMAGES;
    private Video backVideo;

    private AlbumFragmentPresenter mPresenter;

    private Space space;
    private DropDownTabInfo dropDownTabInfo;
    private ScanResult scanResult;
    private LiveAnimate mLiveAnimate;
    private LivePhotoFuctionHelper mLivePhotoFuctionHelper;

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
        if (mArgs.containsKey(MediaConstants.KEY_CHOOSE_MEDIA_PARAMS) && mArgs.getParcelable(MediaConstants.KEY_CHOOSE_MEDIA_PARAMS) != null) {
            mChooseMediaParams = mArgs.getParcelable(MediaConstants.KEY_CHOOSE_MEDIA_PARAMS);
        } else if (mArgs.containsKey(MediaConstants.KEY_CACHE_EXTRA_PARAMS) && mArgs.getParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS) != null) {
            mChooseMediaParams = mArgs.getParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS);
        }

        // 获取瘦身、长腿tips显示的次数
        mPresenter = new AlbumFragmentPresenter(this, mChooseMediaParams);
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
        mPresenter.setMaxSelectedCount(mChooseMediaParams.getMaxSelectedPhotoCount());
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
            //                view.onSelectClick(mAdapter.getmSelectedMediasCount(), mChooseMediaParams.sendText);
            //            }
        }
    }

    private void receivedFromImageEdit(int resultCode, Intent data) {
        ArrayList<Photo> resultMedias = new ArrayList<>();
        MMImageEditParams imageEditParams = data.getParcelableExtra(MediaConstants.KEY_IMAGE_EDIT_PARAMS);
        if (imageEditParams != null && imageEditParams.getPhoto() != null) {
            resultMedias.add(imageEditParams.getPhoto());
        }

        final Intent intent = new Intent();
        intent.putParcelableArrayListExtra(MediaConstants.EXTRA_KEY_IMAGE_DATA, resultMedias);

        if (imageEditParams != null && imageEditParams.getFinishGotoInfo() != null) {
            FinishGotoInfo finishGotoInfo = imageEditParams.getFinishGotoInfo();
            if (finishGotoInfo.getExtraBundle() != null) {
                intent.putExtras(finishGotoInfo.getExtraBundle());
            }
            if (finishGotoInfo.isNeedFinishResultMode()) {
                final FragmentActivity activity = getActivity();
                if (activity != null && !activity.isFinishing()) {

                    activity.setResult(resultCode, intent);
                    activity.finish();
                }
            } else {
                if (!TextUtils.isEmpty(finishGotoInfo.getGotoActivityName()) && getActivity() != null) {
                    intent.setComponent(new ComponentName(getActivity(), finishGotoInfo.getGotoActivityName()));
                    getActivity().startActivity(intent);
                    if (finishGotoInfo.isFinishCurrentActivity()) {
                        getActivity().finish();
                    }
                }
            }

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
        if (null != progressDialog) {
            progressDialog.dismiss();
        }
        progressDialog = new ProgressDialog(activity);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mIsCompressing = false;
                VideoCompressUtil.stopCompress();
                Toaster.showInvalidate("已停止处理", Toaster.LENGTH_SHORT);
                hideCompressDialog();
                mPresenter.cancelImageConvert();
            }
        });
        progressDialog.setMessage("视频处理中......");
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
        mArgs.putParcelable(MediaConstants.KEY_VIDEO_EDIT_PARAMS, new MMVideoEditParams.Builder(video).setFinishGotoInfo(mChooseMediaParams.getFinishGotoInfo()).build());
        mArgs.putParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS, mChooseMediaParams);
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
        MMImageEditParams editParams = new MMImageEditParams.Builder(photo).
                setFinishGotoInfo(mChooseMediaParams.getFinishGotoInfo())
                .build();
        intent.putExtra(MediaConstants.KEY_IMAGE_EDIT_PARAMS, editParams);
        getActivity().startActivityForResult(intent, REQUEST_CODE_IMAGE_EDIT);
    }

    @Override
    public void gotoRecord(MMChooseMediaParams params) {
        VideoRecordAndEditActivity.startRecord(getActivity(),params.getRecordParams(),0);
    }

    public void onSendClick() {
        mBackDirectly = false;
        List<Photo> photos = mPresenter == null ? null : mPresenter.getSelectedMedias();
        if (photos == null || photos.size() <= 0) {
            return;
        }
        if (photos.size() == 1) {
            gotoImageEdit(photos.get(0));
        } else {
            mPresenter.startPhotoCompressVideo();
        }

//        handleResult(photos);
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
    public FinishGotoInfo getGotoInfo() {
        return mChooseMediaParams.getFinishGotoInfo();
    }

    @Override
    public boolean backDirectly() {
        return mBackDirectly;
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
//            todo wujian 修改下文字
            view.onSelectClick(count, "完成");
        }
        if (count <= 1) {
            if (mLivePhotoFuctionHelper != null) {
                mLivePhotoFuctionHelper.hide();
            }
        } else {
            if (mLivePhotoFuctionHelper == null) {
                mLivePhotoFuctionHelper = new LivePhotoFuctionHelper(getFragmentManager(), findViewById(R.id.fl_root), mPresenter);
            }
            if (!mLivePhotoFuctionHelper.isShowing()) {
                mLivePhotoFuctionHelper.show();
            }
        }
        mPresenter.updatePhotoList();
        if (mLivePhotoFuctionHelper != null) {
            mLivePhotoFuctionHelper.onPhotoChange();
        }
    }

    @SuppressWarnings("unchecked")
    public static <V extends View> V findView(View v, @IdRes int id) {
        return (V) v.findViewById(id);
    }

}
