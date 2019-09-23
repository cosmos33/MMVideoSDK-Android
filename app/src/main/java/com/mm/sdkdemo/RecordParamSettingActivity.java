package com.mm.sdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mm.base_business.base.BaseActivity;
import com.mm.mediasdk.RecorderConstants;
import com.mm.mediasdk.scope.BigEyeThinFaceTypeScope;
import com.mm.mediasdk.scope.BuffingTypeScope;
import com.mm.mediasdk.scope.WhiteningTypeScope;
import com.mm.mmutil.toast.Toaster;
import com.mm.recorduisdk.Constants;
import com.mm.recorduisdk.bean.FinishGotoInfo;
import com.mm.recorduisdk.bean.MMRecorderParams;
import com.mm.recorduisdk.config.Configs;
import com.mm.recorduisdk.recorder.MediaConstants;
import com.mm.recorduisdk.recorder.activity.VideoRecordAndEditActivity;
import com.mm.recorduisdk.recorder.model.Photo;
import com.mm.recorduisdk.recorder.model.Video;

import java.io.File;
import java.util.List;

/**
 * Created on 2019/5/23.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class RecordParamSettingActivity extends BaseActivity {

    private EditText mEtBitrateEdit;
    private EditText mEtFrameRateEdit;
    private EditText mEtMinDurationEdit;
    private EditText mEtMaxDurationEdit;
    private View mBtVideoRatio9_16;
    private View mBtVideoRatio3_4;
    private View mBtVideoRatio1_1;
    private View mBtRecordResolution480p;
    private View mBtRecordResolution540p;
    private View mBtRecordResolution720p;
    private View mBtRecordResolution1080p;
    private int mResolutionMode;
    private int mRatioMode;
    private View mBtStartRecord;
    private Toolbar mToolbar;
    private View mBtOpenAudio;
    private View mBtCloseAudio;
    private View mBtOpenSourceVideoRecord;
    private View mBtCloseSourceVideoRecord;
    private View mBtFrontCamera;
    private View mBtBackCamera;
    private View mBtTakePhoto;
    private View mBtRecordVideo;
    private EditText mEtVideoOutputPathEdit;
    private EditText mEtPhotoOutputPathEdit;
    private View mBtResultBack;
    private View mBtGotoActivity;
    private FinishGotoInfo mFinishGotoInfo;

    private View mBtAILightweightBuffing;
    private View mBtAIBuffing;
    private View mBtOldBuffing;
    private View mBtAIWhitening;
    private View mBtOldWhitening;
    private View mBtAIBigEyeThinFace;
    private View mBtOldBigEyeThinFace;
    private @BuffingTypeScope
    int mBuffingType;
    @WhiteningTypeScope
    private int mWhiteningType;
    @BigEyeThinFaceTypeScope
    private int mBigEyeThinFaceType;
    private View mBtOpenFaceAutoMetering;
    private View mBtCloseFaceAutoMetering;
    private View mBtOpenTakePhotoMaxResolution;
    private View mBtCloseTakePhotoMaxResolution;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initEvents();
    }

    private void initEvents() {
        mBtVideoRatio9_16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRatio(v);
            }
        });
        mBtVideoRatio3_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRatio(v);
            }
        });
        mBtVideoRatio1_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRatio(v);
            }
        });
        mBtRecordResolution480p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectResolution(v);
            }
        });

        mBtRecordResolution540p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectResolution(v);
            }
        });
        mBtRecordResolution720p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectResolution(v);
            }
        });
        mBtRecordResolution1080p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectResolution(v);
            }
        });


        mBtOpenAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAudioState(v);
            }
        });
        mBtCloseAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAudioState(v);
            }
        });
        mBtOpenSourceVideoRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSourceVideoRecordState(v);
            }
        });
        mBtCloseSourceVideoRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSourceVideoRecordState(v);
            }
        });
        mBtFrontCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCamera(v);
            }
        });
        mBtBackCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCamera(v);
            }
        });


        mBtTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoOrVideo(v);
            }
        });

        mBtRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhotoOrVideo(v);
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

        mBtAILightweightBuffing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBuffingType(v);
            }
        });
        mBtAIBuffing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBuffingType(v);
            }
        });
        mBtOldBuffing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBuffingType(v);
            }
        });


        mBtAIWhitening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWhiteningType(v);
            }
        });
        mBtOldWhitening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectWhiteningType(v);
            }
        });
        mBtAIBigEyeThinFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBigEyeThinFaceType(v);
            }
        });
        mBtOldBigEyeThinFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBigEyeThinFaceType(v);
            }
        });

        mBtOpenFaceAutoMetering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFaceAutoMeteringState(v);
            }
        });

        mBtCloseFaceAutoMetering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFaceAutoMeteringState(v);
            }
        });

        mBtOpenTakePhotoMaxResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTakePhotoMaxResolutionState(v);
            }
        });

        mBtCloseTakePhotoMaxResolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTakePhotoMaxResolutionState(v);
            }
        });

        mBtStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        setContentView(R.layout.activity_recorder_setting);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("拍摄设置");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEtBitrateEdit = findViewById(R.id.et_bitrate_edit);
        mEtFrameRateEdit = findViewById(R.id.et_frame_rate_edit);
        mEtMinDurationEdit = findViewById(R.id.et_min_duration_edit);
        mEtMaxDurationEdit = findViewById(R.id.et_max_duration_edit);

        mBtVideoRatio9_16 = findViewById(R.id.bt_video_ratio_9_16);
        mBtVideoRatio3_4 = findViewById(R.id.bt_video_ratio_3_4);
        mBtVideoRatio1_1 = findViewById(R.id.bt_video_ratio_1_1);

        mBtRecordResolution480p = findViewById(R.id.bt_record_resolution_480p);
        mBtRecordResolution540p = findViewById(R.id.bt_record_resolution_540p);
        mBtRecordResolution720p = findViewById(R.id.bt_record_resolution_720p);
        mBtRecordResolution1080p = findViewById(R.id.bt_record_resolution_1080p);


        mBtOpenAudio = findViewById(R.id.bt_open_audio);
        mBtCloseAudio = findViewById(R.id.bt_close_audio);

        mBtOpenSourceVideoRecord = findViewById(R.id.bt_open_source_video_record);
        mBtCloseSourceVideoRecord = findViewById(R.id.bt_close_source_video_record);

        mBtFrontCamera = findViewById(R.id.bt_front_camera);
        mBtBackCamera = findViewById(R.id.bt_back_camera);


        mBtTakePhoto = findViewById(R.id.bt_take_photo);
        mBtRecordVideo = findViewById(R.id.bt_record_video);

        mEtVideoOutputPathEdit = findViewById(R.id.et_video_output_path_edit);
        mEtPhotoOutputPathEdit = findViewById(R.id.et_photo_output_path_edit);

        mBtResultBack = findViewById(R.id.bt_result_back);
        mBtGotoActivity = findViewById(R.id.bt_goto_activity);

        mBtAILightweightBuffing = findViewById(R.id.bg_ai_lightweight_buffing);
        mBtAIBuffing = findViewById(R.id.bt_ai_buffing);
        mBtOldBuffing = findViewById(R.id.bt_old_buffing);

        mBtAIWhitening = findViewById(R.id.bt_ai_whitening);
        mBtOldWhitening = findViewById(R.id.bt_old_whitening);


        mBtAIBigEyeThinFace = findViewById(R.id.bt_ai_big_eye_thin_face);
        mBtOldBigEyeThinFace = findViewById(R.id.bt_old_big_eye_thin_face);


        mBtOpenFaceAutoMetering = findViewById(R.id.bt_open_face_auto_metering);
        mBtCloseFaceAutoMetering = findViewById(R.id.bt_close_face_auto_metering);

        mBtOpenTakePhotoMaxResolution = findViewById(R.id.bt_open_takephoto_max_resolution);
        mBtCloseTakePhotoMaxResolution = findViewById(R.id.bt_close_takephoto_max_resolution);

        mBtStartRecord = findViewById(R.id.bt_start_record);

        selectResolution(mBtRecordResolution720p);
        selectRatio(mBtVideoRatio9_16);
        selectAudioState(mBtOpenAudio);
        selectSourceVideoRecordState(mBtCloseSourceVideoRecord);
        selectCamera(mBtFrontCamera);
        selectPhotoOrVideo(mBtRecordVideo);
        selectFinishGoto(mBtResultBack);
        selectBuffingType(mBtAILightweightBuffing);
        selectWhiteningType(mBtAIWhitening);
        selectBigEyeThinFaceType(mBtAIBigEyeThinFace);
        selectFaceAutoMeteringState(mBtOpenFaceAutoMetering);
        selectTakePhotoMaxResolutionState(mBtOpenTakePhotoMaxResolution);

        mEtVideoOutputPathEdit.setText(new File(Configs.getDir("ProcessVideo"), System.currentTimeMillis() + ".mp4").toString());
        mEtPhotoOutputPathEdit.setText(new File(Configs.getDir("ProcessImage"), System.currentTimeMillis() + "_process.jpg").toString());


    }

    private void selectTakePhotoMaxResolutionState(View view) {
        mBtOpenTakePhotoMaxResolution.setSelected(false);
        mBtCloseTakePhotoMaxResolution.setSelected(false);
        if (mBtOpenTakePhotoMaxResolution == view) {
            mBtOpenTakePhotoMaxResolution.setSelected(true);
        } else if (mBtCloseTakePhotoMaxResolution == view) {
            mBtCloseTakePhotoMaxResolution.setSelected(true);
        }
    }

    private void selectFaceAutoMeteringState(View view) {
        mBtOpenFaceAutoMetering.setSelected(false);
        mBtCloseFaceAutoMetering.setSelected(false);
        if (mBtOpenFaceAutoMetering == view) {
            mBtOpenFaceAutoMetering.setSelected(true);
        } else if (mBtCloseFaceAutoMetering == view) {
            mBtCloseFaceAutoMetering.setSelected(true);
        }
    }

    private void selectBigEyeThinFaceType(View view) {
        mBtAIBigEyeThinFace.setSelected(false);
        mBtOldBigEyeThinFace.setSelected(false);
        if (mBtAIBigEyeThinFace == view) {
            mBtAIBigEyeThinFace.setSelected(true);
            mBigEyeThinFaceType = RecorderConstants.BigEyeThinFaceType.AIBigEyeThinFace;
        } else if (mBtOldBigEyeThinFace == view) {
            mBtOldBigEyeThinFace.setSelected(true);
            mBigEyeThinFaceType = RecorderConstants.BigEyeThinFaceType.OldBigEyeThinFace;
        }
    }

    private void selectWhiteningType(View view) {
        mBtAIWhitening.setSelected(false);
        mBtOldWhitening.setSelected(false);
        if (mBtAIWhitening == view) {
            mBtAIWhitening.setSelected(true);
            mWhiteningType = RecorderConstants.WhiteningType.AIWhitening;
        } else if (mBtOldWhitening == view) {
            mBtOldWhitening.setSelected(true);
            mWhiteningType = RecorderConstants.WhiteningType.OldWhitening;
        }
    }

    private void selectBuffingType(View view) {
        mBtAILightweightBuffing.setSelected(false);
        mBtAIBuffing.setSelected(false);
        mBtOldBuffing.setSelected(false);
        if (mBtAILightweightBuffing == view) {
            mBtAILightweightBuffing.setSelected(true);
            mBuffingType = RecorderConstants.BuffingType.AILightweightBuffing;
        } else if (mBtAIBuffing == view) {
            mBtAIBuffing.setSelected(true);
            mBuffingType = RecorderConstants.BuffingType.AIBuffing;
        } else if (mBtOldBuffing == view) {
            mBtOldBuffing.setSelected(true);
            mBuffingType = RecorderConstants.BuffingType.OldBuffing;
        }
    }

    private void selectPhotoOrVideo(View view) {
        mBtTakePhoto.setSelected(false);
        mBtRecordVideo.setSelected(false);

        if (mBtTakePhoto == view) {
            mBtTakePhoto.setSelected(true);
        } else if (mBtRecordVideo == view) {
            mBtRecordVideo.setSelected(true);
        }
    }

    private void selectSourceVideoRecordState(View view) {
        mBtCloseSourceVideoRecord.setSelected(false);
        mBtOpenSourceVideoRecord.setSelected(false);
        if (mBtOpenSourceVideoRecord == view) {
            mBtOpenSourceVideoRecord.setSelected(true);
        } else if (mBtCloseSourceVideoRecord == view) {
            mBtCloseSourceVideoRecord.setSelected(true);
        }
    }

    private void selectCamera(View view) {
        mBtFrontCamera.setSelected(false);
        mBtBackCamera.setSelected(false);
        if (mBtFrontCamera == view) {
            mBtFrontCamera.setSelected(true);
        } else if (mBtBackCamera == view) {
            mBtBackCamera.setSelected(true);
        }
    }


    private void selectAudioState(View view) {
        mBtOpenAudio.setSelected(false);
        mBtCloseAudio.setSelected(false);
        if (mBtOpenAudio == view) {
            mBtOpenAudio.setSelected(true);
        } else if (mBtCloseAudio == view) {
            mBtCloseAudio.setSelected(true);
        }
    }


    private void selectRatio(View view) {
        mBtVideoRatio9_16.setSelected(false);
        mBtVideoRatio3_4.setSelected(false);
        mBtVideoRatio1_1.setSelected(false);
        if (mBtVideoRatio3_4 == view) {
            mBtVideoRatio3_4.setSelected(true);
            mRatioMode = Constants.VideoRatio.RATIO_3X4;
        } else if (mBtVideoRatio1_1 == view) {
            mBtVideoRatio1_1.setSelected(true);
            mRatioMode = Constants.VideoRatio.RATIO_1X1;
        } else {
            mBtVideoRatio9_16.setSelected(true);
            mRatioMode = Constants.VideoRatio.RATIO_9X16;
        }
    }

    private void selectResolution(View view) {
        mBtRecordResolution480p.setSelected(false);
        mBtRecordResolution540p.setSelected(false);
        mBtRecordResolution720p.setSelected(false);
        mBtRecordResolution1080p.setSelected(false);

        if (view == mBtRecordResolution480p) {
            mResolutionMode = Constants.Resolution.RESOLUTION_480;
            mBtRecordResolution480p.setSelected(true);
        } else if (view == mBtRecordResolution540p) {
            mResolutionMode = Constants.Resolution.RESOLUTION_540;
            mBtRecordResolution540p.setSelected(true);
        } else if (view == mBtRecordResolution1080p) {
            mResolutionMode = Constants.Resolution.RESOLUTION_1080;
            mBtRecordResolution1080p.setSelected(true);
        } else {
            mResolutionMode = Constants.Resolution.RESOLUTION_720;
            mBtRecordResolution720p.setSelected(true);
        }
    }


    private boolean checkMinError(long min, long value, String tip) {
        if (value <= min) {
            Toaster.show(tip);
            return true;
        }
        return false;
    }

    private void startRecord() {
        MMRecorderParams.Builder builder = new MMRecorderParams.Builder();
        builder.setResolutionMode(mResolutionMode)
                .setVideoRatio(mRatioMode)
                .setEnableAudioRecorder(mBtOpenAudio.isSelected())
                .setCameraType(mBtFrontCamera.isSelected() ? Constants.CameraType.FRONT : Constants.CameraType.BACK)
                .setGotoTab(mBtTakePhoto.isSelected() ? Constants.RecordTab.PHOTO : Constants.RecordTab.VIDEO)
                .setEnableSourceVideoRecord(mBtOpenSourceVideoRecord.isSelected())
                .setBuffingType(mBuffingType)
                .setWhiteningType(mWhiteningType)
                .setEnableFaceAutoMetering(mBtOpenFaceAutoMetering.isSelected())
                .setFinishGotoInfo(mFinishGotoInfo)
                .setEnableTakePhotoMaxResolution(mBtOpenTakePhotoMaxResolution.isSelected())
        ;

        String bitrate = mEtBitrateEdit.getText().toString().trim();
        String frameRate = mEtFrameRateEdit.getText().toString().trim();
        String minDuration = mEtMinDurationEdit.getText().toString().trim();
        String maxDuration = mEtMaxDurationEdit.getText().toString().trim();
        String videoOutputPath = mEtVideoOutputPathEdit.getText().toString().trim();
        String photoOutputPath = mEtPhotoOutputPathEdit.getText().toString().trim();


        if (TextUtils.isEmpty(videoOutputPath) || !new File(videoOutputPath).getParentFile().exists()) {
            Toaster.show("视频输出地址为null或者地址路径不存在");
            return;
        }
        if (TextUtils.isEmpty(photoOutputPath) || !new File(photoOutputPath).getParentFile().exists()) {
            Toaster.show("图片输出地址为null或者地址路径不存在");
            return;
        }

        if (!TextUtils.isEmpty(bitrate)) {
            try {
                builder.setVideoBitrate(Integer.parseInt(bitrate) * 1024);
            } catch (Exception e) {

            }
        }

        if (!TextUtils.isEmpty(frameRate)) {
            try {
                builder.setFrameRate(Integer.parseInt(frameRate));
            } catch (Exception e) {

            }
        }

        if (!TextUtils.isEmpty(minDuration)) {
            try {
                builder.setMinDuration(Integer.parseInt(minDuration) * 1000);
            } catch (Exception e) {

            }
        }

        if (!TextUtils.isEmpty(maxDuration)) {
            try {
                builder.setMaxDuration(Integer.parseInt(maxDuration) * 1000);
            } catch (Exception e) {

            }
        }
        builder.setPhotoOutputPath(photoOutputPath)
                .setVideoOutputPath(videoOutputPath);
        MMRecorderParams recorderParams = builder.build();
        if (checkMinError(0, recorderParams.getMinDuration(), "最小录制时间需要大于0")
                || checkMaxError(recorderParams.getMaxDuration(), recorderParams.getMinDuration(), "最大录制时间需要大于最小录制时间")
                || checkMinError(19, recorderParams.getFrameRate(), "帧率最好大于等于20")
                || checkMinError(1000, recorderParams.getMaxDuration(), "最大录制时长最好大于1秒")
                || checkMaxError(60, recorderParams.getFrameRate(), "帧率需要小于等于60")
                || checkMaxError(15 * 1024 * 1000, recorderParams.getVideoBitrate(), "码率最好小于15M(" + 15 * 1024 + "kbps)")
        ) {
            return;
        }
        VideoRecordAndEditActivity.startRecord(this, recorderParams, 1);
    }

    private boolean checkMaxError(long max, long value, String tip) {
        if (value > max) {
            Toaster.show(tip);
            return true;
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (data.hasExtra(MediaConstants.EXTRA_KEY_IMAGE_DATA)) {
                List<Photo> photos = (List<Photo>) data.getSerializableExtra(MediaConstants.EXTRA_KEY_IMAGE_DATA);
                if (photos != null && photos.size() > 0) {
                    Photo photo = photos.get(0);
                    Toaster.show(TextUtils.isEmpty(photo.tempPath) ? photo.path : photo.tempPath);
                }
            } else if (data.hasExtra(MediaConstants.EXTRA_KEY_VIDEO_DATA)) {
                Video video = data.getParcelableExtra(MediaConstants.EXTRA_KEY_VIDEO_DATA);
                Toaster.show(video.path);
            }
        }
    }
}
