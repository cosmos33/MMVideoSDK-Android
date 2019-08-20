package com.mm.recorduisdk.recorder.activity;

import android.os.Bundle;

import com.mm.base_business.base.BaseFullScreenActivity;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.bean.MMImageEditParams;
import com.mm.recorduisdk.recorder.MediaConstants;
import com.mm.recorduisdk.recorder.view.ImageEditFragment;
import com.mm.recorduisdk.utils.album.AlbumConstant;

/**
 * @author wangduanqing
 */
public class ImageEditActivity extends BaseFullScreenActivity {
    private MMImageEditParams imageEditParams;
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
        imageEditParams = getIntent().getParcelableExtra(MediaConstants.KEY_IMAGE_EDIT_PARAMS);
        if (imageEditParams == null) {
            finish();
            return;
        }
        showImageEdit();
    }

    private void showImageEdit() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MediaConstants.KEY_IMAGE_EDIT_PARAMS, imageEditParams);
        bundle.putString(AlbumConstant.KEY_FINISH_TEXT, getIntent().getStringExtra(AlbumConstant.KEY_FINISH_TEXT));
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
