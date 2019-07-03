package com.mm.sdkdemo.recorder.activity;

import android.os.Bundle;

import com.mm.sdkdemo.R;
import com.mm.sdkdemo.recorder.model.Photo;
import com.mm.sdkdemo.recorder.view.ImageEditFragment;
import com.mm.sdkdemo.utils.album.AlbumConstant;

/**
 * @author wangduanqing
 */
public class ImageEditActivity extends BaseFullScreenActivity {
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
        showImageEdit();
    }

    private void showImageEdit() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AlbumConstant.KEY_EDIT_MEDIA, image);
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
