package com.mm.sdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.mm.base_business.base.BaseActivity;
import com.mm.mmutil.toast.Toaster;
import com.mm.recorduisdk.Constants;
import com.mm.recorduisdk.bean.FinishGotoInfo;
import com.mm.recorduisdk.bean.MMChooseMediaParams;
import com.mm.recorduisdk.bean.MMRecorderParams;
import com.mm.recorduisdk.recorder.MediaConstants;
import com.mm.recorduisdk.recorder.activity.VideoRecordAndEditActivity;
import com.mm.recorduisdk.recorder.model.Photo;
import com.mm.recorduisdk.recorder.model.Video;

import java.util.List;

public class ChooseMediaParamSettingActivity extends BaseActivity {

    private View mBtChooseSingleMode;
    private View mBtChooseMultiMode;
    private View mBtChooseMixType;
    private View mBtChoosePhotoType;
    private View mBtChooseVideoType;
    private View mBtShowCameraIcon;
    private View mBtHideCameraIcon;
    private View mBtShowAllTab;
    private View mBtShowAlbumTab;
    private View mBtShowVideoTab;
    private View mBtResultBack;
    private View mBtGotoActivity;
    private FinishGotoInfo mFinishGotoInfo;
    private Toolbar mToolbar;
    private View mBtStartChoose;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initEvents();
    }

    private void initEvents() {
        mBtChooseSingleMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseMediaMode(v);
            }
        });
        mBtChooseMultiMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseMediaMode(v);
            }
        });

        mBtChooseMixType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseMediaType(v);
            }
        });
        mBtChoosePhotoType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseMediaType(v);
            }
        });
        mBtChooseVideoType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseMediaType(v);
            }
        });

        mBtShowCameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCameraIcon(v);
            }
        });
        mBtHideCameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseCameraIcon(v);
            }
        });

        mBtShowAllTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseNeedShowTab(v);
            }
        });
        mBtShowAlbumTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseNeedShowTab(v);
            }
        });
        mBtShowVideoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseNeedShowTab(v);
            }
        });

        mBtResultBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFinishGoto(v);
            }
        });
        mBtGotoActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFinishGoto(v);
            }
        });

        mBtStartChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChoose();
            }
        });
    }


    private void gotoChoose(){
        MMChooseMediaParams.Builder builder = new MMChooseMediaParams.Builder();
        builder.setChooseMode(mBtChooseMultiMode.isSelected()? Constants.EditChooseMode.MODE_MULTIPLE:Constants.EditChooseMode.MODE_STYLE_ONE)
                .setFinishGotoInfo(mFinishGotoInfo)
                .setMediaChooseType(mBtChooseMixType.isSelected()?Constants.EditChooseMediaType.MEDIA_TYPE_MIXED:(mBtChooseVideoType.isSelected()?Constants.EditChooseMediaType.MEDIA_TYPE_VIDEO:Constants.EditChooseMediaType.MEDIA_TYPE_IMAGE))
                .setShowCameraIcon(mBtShowCameraIcon.isSelected())
                .setShowAlbumTabs(mBtShowAllTab.isSelected()?Constants.ShowMediaTabType.STATE_ALL:(mBtShowVideoTab.isSelected()?Constants.ShowMediaTabType.STATE_VIDEO:Constants.ShowMediaTabType.STATE_ALBUM))
                .setGotoRecordParams(new MMRecorderParams.Builder().setFinishGotoInfo(mFinishGotoInfo).build());

        VideoRecordAndEditActivity.startChooseMediaToEdit(this,builder.build(),1);
    }

    private void selectFinishGoto(View view) {
        mBtResultBack.setSelected(false);
        mBtGotoActivity.setSelected(false);
        if (mBtResultBack == view) {
            mBtResultBack.setSelected(true);
            mFinishGotoInfo = new FinishGotoInfo();
        } else if (mBtGotoActivity == view) {
            mBtGotoActivity.setSelected(true);
            mFinishGotoInfo = new FinishGotoInfo(GotoTestActivity.class.getName(), null);
        }
    }

    private void chooseNeedShowTab(View view) {
        mBtShowAllTab.setSelected(false);
        mBtShowAlbumTab.setSelected(false);
        mBtShowVideoTab.setSelected(false);
        if (view == mBtShowAllTab) {
            mBtShowAllTab.setSelected(true);
        } else if (view == mBtShowAlbumTab) {
            mBtShowAlbumTab.setSelected(true);
        } else if (view == mBtShowVideoTab) {
            mBtShowVideoTab.setSelected(true);
        }
    }
    private void chooseCameraIcon(View view) {
        mBtShowCameraIcon.setSelected(false);
        mBtHideCameraIcon.setSelected(false);
        if (view == mBtShowCameraIcon) {
            mBtShowCameraIcon.setSelected(true);
        } else if (view == mBtHideCameraIcon) {
            mBtHideCameraIcon.setSelected(true);
        }
    }
    private void chooseMediaType(View view) {
        mBtChooseMixType.setSelected(false);
        mBtChoosePhotoType.setSelected(false);
        mBtChooseVideoType.setSelected(false);
        if (view == mBtChooseMixType) {
            mBtChooseMixType.setSelected(true);
        } else if (view == mBtChoosePhotoType) {
            mBtChoosePhotoType.setSelected(true);
        } else if (view == mBtChooseVideoType) {
            mBtChooseVideoType.setSelected(true);
        }
    }

    private void chooseMediaMode(View view) {
        mBtChooseSingleMode.setSelected(false);
        mBtChooseMultiMode.setSelected(false);
        if (view == mBtChooseSingleMode) {
            mBtChooseSingleMode.setSelected(true);
        } else if (view == mBtChooseMultiMode) {
            mBtChooseMultiMode.setSelected(true);
        }
    }

    private void initViews() {
        setContentView(R.layout.activity_choose_media_param_setting);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("选择编辑设置");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBtChooseSingleMode = findViewById(R.id.bt_choose_single_mode);
        mBtChooseMultiMode = findViewById(R.id.bt_choose_multi_mode);

        mBtChooseMixType = findViewById(R.id.bt_choose_mix_type);
        mBtChoosePhotoType = findViewById(R.id.bt_choose_photo_type);
        mBtChooseVideoType = findViewById(R.id.bt_choose_video_type);

        mBtShowCameraIcon = findViewById(R.id.bt_show_camera_icon);
        mBtHideCameraIcon = findViewById(R.id.bt_hide_camera_icon);

        mBtShowAllTab = findViewById(R.id.bt_show_all_tab);
        mBtShowAlbumTab = findViewById(R.id.bt_show_album_tab);
        mBtShowVideoTab = findViewById(R.id.bt_show_video_tab);

        mBtResultBack = findViewById(R.id.bt_result_back);
        mBtGotoActivity = findViewById(R.id.bt_goto_activity);

        mBtStartChoose = findViewById(R.id.bt_start_choose);

        chooseMediaMode(mBtChooseMultiMode);
        chooseMediaType(mBtChooseMixType);
        chooseCameraIcon(mBtShowCameraIcon);
        selectFinishGoto(mBtResultBack);
        chooseNeedShowTab(mBtShowAllTab);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&&data!=null){
            if (data.hasExtra(MediaConstants.EXTRA_KEY_IMAGE_DATA)) {
                List<Photo> photos = (List<Photo>) data.getSerializableExtra(MediaConstants.EXTRA_KEY_IMAGE_DATA);
                if(photos!=null&&photos.size()>0){
                    Toaster.show(TextUtils.isEmpty(photos.get(0).tempPath)?photos.get(0).path:photos.get(0).tempPath);
                }
            }else if(data.hasExtra(MediaConstants.EXTRA_KEY_VIDEO_DATA)){
                Video video = data.getParcelableExtra(MediaConstants.EXTRA_KEY_VIDEO_DATA);
                Toaster.show(video.path);
            }
        }
    }
}
