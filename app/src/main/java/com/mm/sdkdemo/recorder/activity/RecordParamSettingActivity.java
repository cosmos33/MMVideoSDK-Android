package com.mm.sdkdemo.recorder.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mm.mediasdk.RecorderConstants;
import com.mm.sdkdemo.bean.MMRecorderParams;
import com.mm.mmutil.toast.Toaster;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.base.BaseActivity;
import com.mm.sdkdemo.bean.VideoInfoTransBean;

/**
 * Created on 2019/5/23.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class RecordParamSettingActivity extends BaseActivity implements View.OnClickListener {

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
    private View mBtBeautyVersion1;
    private View mBtBeautyVersion2;
    private View mBtBeautyVersion3;
    private int mBeautyVersion;
    private Toolbar mToolbar;
    private View mBtOpenAudio;
    private View mBtCloseAudio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initEvents();
    }

    private void initEvents() {
        mBtVideoRatio9_16.setOnClickListener(this);
        mBtVideoRatio3_4.setOnClickListener(this);
        mBtVideoRatio1_1.setOnClickListener(this);
        mBtRecordResolution480p.setOnClickListener(this);
        mBtRecordResolution540p.setOnClickListener(this);
        mBtRecordResolution720p.setOnClickListener(this);
        mBtRecordResolution1080p.setOnClickListener(this);
        mBtStartRecord.setOnClickListener(this);
        mBtBeautyVersion1.setOnClickListener(this);
        mBtBeautyVersion2.setOnClickListener(this);
        mBtBeautyVersion3.setOnClickListener(this);
        mBtOpenAudio.setOnClickListener(this);
        mBtCloseAudio.setOnClickListener(this);
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

        mBtBeautyVersion1 = findViewById(R.id.bt_beauty_version1);
        mBtBeautyVersion2 = findViewById(R.id.bt_beauty_version2);
        mBtBeautyVersion3 = findViewById(R.id.bt_beauty_version3);

        mBtOpenAudio = findViewById(R.id.bt_open_audio);
        mBtCloseAudio = findViewById(R.id.bt_close_audio);


        mBtStartRecord = findViewById(R.id.bt_start_record);

        selectResolution(mBtRecordResolution720p);
        selectRatio(mBtVideoRatio9_16);
        selectBeautyVersion(mBtBeautyVersion1);
        selectAudioState(mBtOpenAudio);
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


    private void selectBeautyVersion(View view) {
        mBtBeautyVersion1.setSelected(false);
        mBtBeautyVersion2.setSelected(false);
        mBtBeautyVersion3.setSelected(false);

        if (mBtBeautyVersion2 == view) {
            mBeautyVersion = RecorderConstants.BeautyFaceVersion.V2;
            mBtBeautyVersion2.setSelected(true);
        } else if (mBtBeautyVersion3 == view) {
            mBeautyVersion = RecorderConstants.BeautyFaceVersion.V3;
            mBtBeautyVersion3.setSelected(true);
        } else {
            mBeautyVersion = RecorderConstants.BeautyFaceVersion.V1;
            mBtBeautyVersion1.setSelected(true);
        }
    }

    private void selectRatio(View view) {
        mBtVideoRatio9_16.setSelected(false);
        mBtVideoRatio3_4.setSelected(false);
        mBtVideoRatio1_1.setSelected(false);
        if (mBtVideoRatio3_4 == view) {
            mBtVideoRatio3_4.setSelected(true);
            mRatioMode = RecorderConstants.VideoRatio.RATIO_3X4;
        } else if (mBtVideoRatio1_1 == view) {
            mBtVideoRatio1_1.setSelected(true);
            mRatioMode = RecorderConstants.VideoRatio.RATIO_1X1;
        } else {
            mBtVideoRatio9_16.setSelected(true);
            mRatioMode = RecorderConstants.VideoRatio.RATIO_9X16;
        }
    }

    private void selectResolution(View view) {
        mBtRecordResolution480p.setSelected(false);
        mBtRecordResolution540p.setSelected(false);
        mBtRecordResolution720p.setSelected(false);
        mBtRecordResolution1080p.setSelected(false);

        if (view == mBtRecordResolution480p) {
            mResolutionMode = RecorderConstants.Resolution.RESOLUTION_480;
            mBtRecordResolution480p.setSelected(true);
        } else if (view == mBtRecordResolution540p) {
            mResolutionMode = RecorderConstants.Resolution.RESOLUTION_540;
            mBtRecordResolution540p.setSelected(true);
        } else if (view == mBtRecordResolution1080p) {
            mResolutionMode = RecorderConstants.Resolution.RESOLUTION_1080;
            mBtRecordResolution1080p.setSelected(true);
        } else {
            mResolutionMode = RecorderConstants.Resolution.RESOLUTION_720;
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

    @Override
    public void onClick(View v) {
        if (v == mBtStartRecord) {
            MMRecorderParams.Builder builder = new MMRecorderParams.Builder();
            builder.setResolutionMode(mResolutionMode)
                    .setVideoRatio(mRatioMode)
                    .setBeautyFaceVersion(mBeautyVersion)
                    .setEnableAudioRecorder(mBtOpenAudio.isSelected());

            String bitrate = mEtBitrateEdit.getText().toString().trim();
            String frameRate = mEtFrameRateEdit.getText().toString().trim();
            String minDuration = mEtMinDurationEdit.getText().toString().trim();
            String maxDuration = mEtMaxDurationEdit.getText().toString().trim();


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
            VideoInfoTransBean videoRecordInfo = new VideoInfoTransBean();
            videoRecordInfo.setRecorderParams(recorderParams);

            VideoRecordAndEditActivity.startActivity(this, videoRecordInfo, -1);

        } else if (v == mBtRecordResolution480p || v == mBtRecordResolution540p || v == mBtRecordResolution720p || v == mBtRecordResolution1080p) {
            selectResolution(v);
        } else if (v == mBtVideoRatio9_16 || v == mBtVideoRatio3_4 || v == mBtVideoRatio1_1) {
            selectRatio(v);
        } else if (v == mBtBeautyVersion1 || v == mBtBeautyVersion2 || v == mBtBeautyVersion3) {
            selectBeautyVersion(v);
        } else if (v == mBtOpenAudio || v == mBtCloseAudio) {
            selectAudioState(v);
        }
    }

    private boolean checkMaxError(long max, long value, String tip) {
        if (value > max) {
            Toaster.show(tip);
            return true;
        }
        return false;
    }
}
