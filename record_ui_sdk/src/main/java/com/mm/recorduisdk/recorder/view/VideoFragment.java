package com.mm.recorduisdk.recorder.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;

import com.mm.base_business.base.BaseActivity;
import com.mm.base_business.base.BaseTabOptionFragment;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.toast.Toaster;
import com.mm.recorduisdk.Constants;
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
import com.mm.recorduisdk.recorder.listener.FragmentChangeListener;
import com.mm.recorduisdk.recorder.model.Photo;
import com.mm.recorduisdk.recorder.model.Video;
import com.mm.recorduisdk.recorder.presenter.BaseAlbumPresenter;
import com.mm.recorduisdk.recorder.presenter.VideoFragmentPresenter;
import com.mm.recorduisdk.utils.VideoCompressUtil;
import com.mm.recorduisdk.utils.album.AlbumConstant;
import com.mm.recorduisdk.utils.album.AlbumResultHelper;
import com.mm.recorduisdk.utils.album.ItemConstant;
import com.mm.recorduisdk.utils.album.ScanResult;
import com.mm.recorduisdk.widget.decoration.GridItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.mm.recorduisdk.recorder.activity.VideoRecordAndEditActivity.GOTO_WHERE;

/**
 * Created by chenxin on 2018/8/28.
 */

public class VideoFragment extends BaseTabOptionFragment implements
        IAlbumFragment<VideoFragment>,
        AlbumResultHelper.IResultWrapper<Parcelable>,
        BaseAlbumPresenter.OnItemClickListener {

    // request code
    private static final int REQUEST_CODE_IMAGE_PREVIEW = 10010;
    private static final int REQUEST_CODE_IMAGE_EDIT = 10011;
    private static final int REQUEST_CODE_CUT_VIDEO = 10012;

    private FragmentChangeListener fragmentChangeListener;

    private MMChooseMediaParams mChooseMediaParams = new MMChooseMediaParams.Builder().build();
    private ProgressDialog progressDialog;
    private Bundle mArgs;
    private boolean mIsCompressing = false;//避免多次点击多次调用压缩方法
    private boolean mIsMediasLoaded;
    private boolean mBackDirectly;
    private String mediaType = MediaConstants.MEDIA_TYPE_IMAGES;
    private Video backVideo;

    private BaseAlbumPresenter mPresenter;
    private ScanResult scanResult;


    public void setFragmentChangeListener(FragmentChangeListener fragmentChangeListener) {
        this.fragmentChangeListener = fragmentChangeListener;
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

        mPresenter = new VideoFragmentPresenter(this, mChooseMediaParams);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_multimedia;
    }

    @Override
    protected void initViews(View view) {
        final int halfSpace = getResources().getDimensionPixelOffset(R.dimen.multimedia_list_item_space_half);
        RecyclerView recyclerView = findView(view, R.id.rl_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.addItemDecoration(new GridItemDecoration(halfSpace));

        SimpleCementAdapter adapter = new SimpleCementAdapter();
        adapter.setSpanCount(3);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        gridLayoutManager.setSpanSizeLookup(adapter.getSpanSizeLookup());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        VideoFragmentPresenter presenter = (VideoFragmentPresenter) mPresenter;
        presenter.setOnItemClickListener(this);
        presenter.setAdapter(adapter);
        presenter.setMaxSelectedCount(mChooseMediaParams.getMaxSelectedPhotoCount());

    }

    @Override
    protected void onLoad() {
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
    public void clearSelect() {
        // do nothing
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
                if (fragmentChangeListener != null) {
                    fragmentChangeListener.change(this, mArgs);
                }
                break;
            default:
                break;
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
                it.remove();
            }
        }
        if (mPresenter.getAdapter() != null) {
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
    public VideoFragment getFragment() {
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
        this.startActivityForResult(it, REQUEST_CODE_CUT_VIDEO);
    }

    @Override
    public void gotoVideoEdit(Video video) {
        if (video.length > 0 && video.avgBitrate <= 0) {
            File file = new File(video.path);
            video.size = (int) (file.length());
            //文件大小以B为单位，比特率以 bps 为单位，length以ms为单位
            video.avgBitrate = (int) (video.size * 1f / video.length * 8000);
        }
        MMChooseMediaParams.Builder paramsBuilder = new MMChooseMediaParams.Builder(mChooseMediaParams);
        paramsBuilder.setInitAlbumIndex(Constants.ShowMediaTabType.STATE_VIDEO);

        mArgs.putParcelable(MediaConstants.KEY_VIDEO_EDIT_PARAMS, new MMVideoEditParams.Builder(video).setFinishGotoInfo(mChooseMediaParams.getFinishGotoInfo()).build());
        mArgs.putParcelable(MediaConstants.KEY_CACHE_EXTRA_PARAMS, paramsBuilder.build());
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

        getActivity().overridePendingTransition(R.anim.zoom_enter, R.anim.normal);
    }

    @Override
    public void gotoRecord(MMChooseMediaParams params) {
        // 视频页无拍摄入口
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

    @Override
    public void onSendClick() {

    }

    @Override
    public void changeChecked(Photo photo, boolean isSelected) {

    }

    @Override
    public boolean isChecked(Photo photo) {
        return false;
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
    public void showDirectoriesView() {
        // do nothing
    }

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
        if (view != null && getUserVisibleHint()) {
            view.onSelectClick(0, "");
        }
    }

    public int dp2px(float dip) {
        return (int) (getResources().getDisplayMetrics().density * dip + 0.5f);
    }

    @SuppressWarnings("unchecked")
    public static <V extends View> V findView(View v, @IdRes int id) {
        return (V) v.findViewById(id);
    }

}
