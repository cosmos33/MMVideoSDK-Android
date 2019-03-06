package com.immomo.videosdk.recorder.activity;

import android.os.Bundle;

import com.immomo.videosdk.R;
import com.immomo.videosdk.recorder.model.Photo;
import com.immomo.videosdk.recorder.view.ImageEditFragment;
import com.immomo.videosdk.utils.album.AlbumConstant;

public class ImageEditActivity extends BaseFullScreenActivity {
    public static final int SIZE_CROP_MAX = 1080;

    public static final int ACTIVITY_RESULT_CROP_ERROR = 1000;
    /**
     * 设置截出来的图的最小大小。假如源图小于值，那么将异常返回。
     */
    public static final String EXTAR_MIN_SIZE = "minsize";
    public static final int ACTIVITY_RESULT_SIZE_ERROR = 1003;

    /**
     * 设置选择框的X边与Y边的关系，假设X为1,Y为2，那么框的高度始终是宽度的2倍
     */
    public static final String EXTRA_ASPECT_X = "aspectX";

    /**
     * @see #EXTRA_ASPECT_X
     */
    public static final String EXTRA_ASPECT_Y = "aspectY";

    public static final String EXTRA_OUTPUT_FILE_PATH = "outputFilePath";

    private Photo image;
    private ImageEditFragment editFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showUI();
    }

    private void showUI() {
        if (isDestroyed()) {
            return;
        }

        initIntent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initIntent() {
        image = getIntent().getParcelableExtra(AlbumConstant.KEY_EDIT_MEDIA);
        if (image == null) {
            finish();
            return;
        }
        showImageEdit(false);
    }

    private void showImageEdit(boolean fromCrop) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AlbumConstant.KEY_EDIT_MEDIA, image);
        bundle.putBoolean(ImageEditFragment.KEY_FROM_CROP, fromCrop);
        bundle.putString(AlbumConstant.KEY_FINISH_TEXT, getIntent().getStringExtra(AlbumConstant.KEY_FINISH_TEXT));
        bundle.putBoolean(AlbumConstant.KEY_IS_FROM_DIGIMON, getIntent().getBooleanExtra(AlbumConstant.KEY_IS_FROM_DIGIMON, false));
        bundle.putBoolean(AlbumConstant.KEY_IS_FROM_ARPET, getIntent().getBooleanExtra(AlbumConstant.KEY_IS_FROM_ARPET, false));
        if (editFragment == null) {
            editFragment = new ImageEditFragment();
        }
        editFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, editFragment)
                .commitAllowingStateLoss();
    }

    private void resetImage() {
        if (image != null) {
            image.changeChecked(false);
            image.isAlbumCheck = false;
            image.isPictureCheck = false;
        }
    }

    @Override
    public void onBackPressed() {
        boolean processed;
        synchronized (this) {
            processed = (editFragment != null && editFragment.onBackPressed());
        }
        if (!processed) {
            super.onBackPressed();
        }
    }
}
