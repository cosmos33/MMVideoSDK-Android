package com.mm.recorduisdk.recorder.presenter;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.core.glcore.camera.ICamera;
import com.core.glcore.config.MRConfig;
import com.core.glcore.config.Size;
import com.cosmos.mdlog.MDLog;
import com.immomo.moment.config.MRecorderActions;
import com.immomo.moment.model.VideoFragment;
import com.mm.mediasdk.IMultiRecorder;
import com.mm.mediasdk.MoMediaManager;
import com.mm.mediasdk.RecorderConstants;
import com.mm.mediasdk.bean.MRSDKConfig;
import com.mm.mediasdk.utils.CameraSizeUtil;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.app.AppContext;
import com.mm.mmutil.log.Log4Android;
import com.mm.mmutil.task.MomoMainThreadExecutor;
import com.mm.mmutil.toast.Toaster;
import com.mm.recorduisdk.Constants;
import com.mm.recorduisdk.bean.MMRecorderParams;
import com.mm.recorduisdk.config.Configs;
import com.mm.recorduisdk.log.LogTag;
import com.mm.recorduisdk.recorder.model.MusicContent;
import com.mm.recorduisdk.recorder.view.CameraZoomChecker;
import com.mm.recorduisdk.recorder.view.IMomoRecordView;
import com.mm.recorduisdk.recorder.view.IRecordView;
import com.momo.mcamera.filtermanager.MMPresetFilter;
import com.momo.mcamera.mask.MaskModel;
import com.momo.mcamera.mask.Sticker;
import com.momo.mcamera.mask.StickerBlendFilter;
import com.momo.xeengine.XE3DEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import project.android.imageprocessing.filter.BasicFilter;

/**
 * Created by wangduanqing on 2019/2/10.
 */
public class RecordPresenter implements IRecorder, SurfaceHolder.Callback, IMomoRecorder {

    private MMRecorderParams mRecorderParams;
    //    protected MultiRecorder mRecorder;
    protected IMultiRecorder multiRecorder;
    protected Activity activity;
    protected IMomoRecordView mView;

    protected MRConfig mrConfig;
    private SurfaceHolder mHolder;

    private boolean isFirstCreateSurface = false;
    protected boolean isSurfaceCreated = false;

    protected int mScreenWidth, mScreenHeight;

    private boolean startRecord;
    protected boolean takingPhoto;

    protected String mPhotoPath;

    private boolean startMusicOnce = false;
    private MusicContent playMusic = null;
    private long lastSwitchCameraTime;
    private static final long SWITCH_CAMERA_TIME = 1000;

    private final CameraZoomChecker mCameraZoomChecker;
    private MaskModel mCurrentMaskModel;

    public RecordPresenter(MMRecorderParams recorderParams) {
        this.mRecorderParams = recorderParams;
        mRecorderParams = (mRecorderParams == null) ? new MMRecorderParams.Builder().build() : mRecorderParams;
        multiRecorder = MoMediaManager.createRecorder();
        mCameraZoomChecker = new CameraZoomChecker(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        isFirstCreateSurface = true;
        MDLog.d(LogTag.RECORDER.RECORD, "surfaceCreated %s", holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        MDLog.d(LogTag.RECORDER.RECORD, "surfaceChanged: %d, %d, isFirstCreateSurface: %b", width, height, isFirstCreateSurface);
        mHolder = holder;
        if (isFirstCreateSurface) {
            isSurfaceCreated = true;
            multiRecorder.setPreviewDisplay(holder);
            multiRecorder.setVisualSize(width, height);
            isFirstCreateSurface = false;
            startPreview();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        MDLog.d(LogTag.RECORDER.RECORD, "surfaceDestroyed %s", holder);
        mHolder = null;
        //        stopPreview();
        isSurfaceCreated = false;
    }

    @Override
    public void initWith(Activity activity, IRecordView view) {
        view.getHolder().addCallback(this);
        this.activity = activity;
        mView = (IMomoRecordView) view;
    }

    @Override
    public boolean prepare() {
        if (activity == null) {
            return false;
        }
        //        createRecorder();
        getMRConfig();
        //        mRecorder.setFaceBeautiful(0);
        //        mRecorder.setUseCameraVersion2(true);
        MRSDKConfig recorderSdkConfig = fullMRSDKConfig();
        return multiRecorder.prepare(activity, recorderSdkConfig);
        //        multiRecorder.setUseCameraVersion2(true);
    }

    private MRSDKConfig fullMRSDKConfig() {
        MRSDKConfig.Builder build = new MRSDKConfig.Builder(mrConfig);


        build.setWhiteningType(mRecorderParams.getWhiteningType())
                .setBigEyeThinFaceType(mRecorderParams.getBigEyeThinFaceType())
                .setBuffingType(mRecorderParams.getBuffingType())
                .setEnableAudioRecorder(mRecorderParams.isEnableAudioRecorder())
                .setEnableSourceVideoRecord(mRecorderParams.isEnableSourceVideoRecord());
        return build.build();
    }

    @Override
    public void takePhoto() {
        if (takingPhoto)
            return;
        if (multiRecorder != null) {
            //            setFlashMode(mPresenter == null ? -1 : mPresenter.getFlashMode());
            mPhotoPath = generateCachePhotoPath();
            if (TextUtils.isEmpty(mPhotoPath)) {
                if (mView != null) {
                    mView.onTakePhoto(null, new IllegalStateException("没有找到SD卡，无法拍照"));
                }
                return;
            }
            takingPhoto = true;
            try {
                multiRecorder.takePhoto(mPhotoPath, new MRecorderActions.OnTakePhotoListener() {
                    @Override
                    public void onTakePhotoComplete(int status, Exception e) {
                        takingPhoto = false;
                        if (e == null) {
                            File photo = new File(mPhotoPath);
                            if (!photo.exists())
                                e = new FileNotFoundException();
                        }
                        final Exception fe = e;
                        if (Looper.getMainLooper() == Looper.myLooper()) {
                            if (mView != null)
                                mView.onTakePhoto(mPhotoPath, e);
                        } else {
                            MomoMainThreadExecutor.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mView != null)
                                        mView.onTakePhoto(mPhotoPath, fe);
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                takingPhoto = false;
                if (mView != null)
                    mView.onTakePhoto(null, e);
                try {
                    multiRecorder.resetCamera();
                } catch (Exception ignore) {
                }
            }
        }
    }

    @Override
    public void setVideoOutputPath(String outputPath) {
        multiRecorder.setMediaOutPath(outputPath);

    }

    @Override
    public boolean isTakingPhoto() {
        return takingPhoto;
    }

    @Override
    public void stopRecording() {
        multiRecorder.pauseRecording();
//        multiRecorder.pauseRecording(new MRecorderActions.OnRecordStoppedListener() {
//            @Override
//            public void onRecordStopped() {
//                MomoMainThreadExecutor.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mPresenter.onRecordPaused();
//                    }
//                });
//            }
//        });
    }

    @Override
    public void cancelRecording() {
        //        if (mRecorder != null) {
        //            if (isRecording)
        //                mRecorder.stopRecording();
        //            mRecorder.removeLast();
        //        }
        //        isRecording = false;
        //        if (mPresenter != null)
        //            mPresenter.removeLast();
        //        startRecord = mPresenter != null && mPresenter.getCount() > 0;
        //        onClearVideo();
        //        //防止 recorder没有录制上，这时应该清空progressView
        //        //mRecorder.getFragmentCount() 不靠谱 ，有可能太短，没录制上
        //        if (mRecorder != null && mPresenter != null && mPresenter.getCount() <= 0) {
        //            mPresenter.clearProgress();
        //            onClearVideo();
        //        }
        multiRecorder.cancelRecording();
    }

    @Override
    public boolean finishRecord(MRecorderActions.OnRecordFinishedListener onRecordFinishedListener) {
        if (multiRecorder == null || multiRecorder.getFragmentCount() <= 0) {
            Toaster.showInvalidate("请先录制视频");
            return false;
        }
        if (!mView.isVideoDurationValid()) {
            return false;
        }
        //        //不在录制状态时，可以完成录制
        //        if (mRecorder != null) {
        //            final List<String> fragmentList = mRecorder.getFragmentList();
        //            ThreadUtils.execute(ThreadUtils.TYPE_RIGHT_NOW, new Runnable() {
        //                @Override
        //                public void run() {
        //                    //
        //                    MultiRecorder.finishRecording(fragmentList, mVideoPath, getCfgPath(), onRecordFinishedListener, activity.getBaseContext());
        //                }
        //            });
        //            if (mPresenter != null) {
        //                mPresenter.onStartFinish();
        //            }
        //        }
        return multiRecorder.finishRecord(onRecordFinishedListener);
    }

    @Override
    public void removeLast() {
        multiRecorder.removeLast();
    }

    @Override
    public void clearTempFiles() {
        //        if (null != mRecorder) {
        //            mRecorder.cancelRecording();
        //        }
    }

    @Override
    public void onResume() {
        //        stopPreview();
        multiRecorder.setStickerStateChangeListener(new StickerBlendFilter.StickerStateChangeListener() {
            @Override
            public void stickerStateChanged(int trigerType, int state) {
            }

            @Override
            public void stickerGestureTypeChanged(String trigerType, boolean isDraw) {
            }

            @Override
            public void faceDetected(boolean hasFace) {
                if (mView != null) {
                    mView.faceDetected(hasFace);
                }
            }

            @Override
            public void playStateChanged(int soundId, boolean play) {
                if (mView != null) {
                    mView.playStateChanged(soundId, play);
                }

            }

            @Override
            public void distortionStateChanged(boolean faceBeauty, float faceValue, float eyeValue, float skinValue, float skinWhitevalue) {

            }
        });
        multiRecorder.setOnFirstFrameRenderedListener(new MRecorderActions.OnFirstFrameRenderedListener() {
            @Override
            public void onFirstFrameRendered() {
                if (mView != null) {
                    mView.hidePreImageView();
                }
            }
        });
        multiRecorder.setOnCameraSetListener(new ICamera.onCameraSetListener() {
            @Override
            public void onCameraSet(Camera camera) {
                if (camera == null) {
                    Toaster.show("相机打开失败，请检查系统相机是否可用");
                    if (mView != null)
                        mView.finish();
                    return;
                }
                if (mView != null) {
                    mView.onCameraSet();
                }
            }
        });


        //在此处设置文件路径
        //        setVideoPath();
    }

    @Override
    public void onPause() {
        stopRecording();
        if (mView != null) {
            mView.refreshView(false);
            mView.resetRecordButton(false);
        }
        //释放资源
        stopPreview();
    }

    @Override
    public boolean isFrontCamera() {
        return multiRecorder.isFrontCamera();
    }

    @Override
    public boolean supporFrontCamera() {
        return multiRecorder.isSupportFrontCamera();
    }

    @Override
    public boolean supportFlash() {
        return multiRecorder.supportFlash();
    }

    @Override
    public MRConfig getMRConfig() {
        if (mrConfig == null) {
            if (mScreenWidth == 0) {
                mScreenWidth = UIUtils.getScreenWidth();
                mScreenHeight = UIUtils.getScreenHeight();
            }
            //设置摄像头
            //上次使用的是否是前置摄像头
            //支持前置摄像头，且上次使用的是前置摄像头，才使用前置  1前置，0后置
            mrConfig = MRConfig.obtain();
            // view预览分辨率

            mrConfig.setVisualSize(getVisualSize());
            mrConfig.setDefaultCamera(mRecorderParams.getCameraType());
            mrConfig.setUseDefaultEncodeSize(true);
            mrConfig.setAudioChannels(1);
            mrConfig.setEncoderGopMode(1);
            mrConfig.setVideoFPS(mRecorderParams.getFrameRate());
            mrConfig.setRatioType(mRecorderParams.getVideoRatio());
            switch (mRecorderParams.getResolutionMode()) {
                case MRConfig.VideoResolution.RESOLUTION_1920:
                    mrConfig.setTargetVideoSize(new Size(1920, 1080));
                    break;
                case MRConfig.VideoResolution.RESOLUTION_1280:
                    mrConfig.setTargetVideoSize(new Size(1280, 720));
                    break;
                case MRConfig.VideoResolution.RESOLUTION_960:
                    mrConfig.setTargetVideoSize(new Size(960, 540));
                    break;
                default:
                    mrConfig.setTargetVideoSize(new Size(640, 480));
                    break;
            }
            if (mRecorderParams.getVideoBitrate() > 0) {
                mrConfig.setVideoEncodeBitRate(mRecorderParams.getVideoBitrate());
            } else {
                setCameraAndCodecInfo(mrConfig.getTargetVideoSize());
            }

        }
        return mrConfig;
    }

    @Override
    public Size getVisualSize() {
        int screenWidth = UIUtils.getScreenWidth();
        int screenHeight = UIUtils.getScreenHeight();
        switch (mRecorderParams.getVideoRatio()) {
            case Constants.VideoRatio.RATIO_1X1: {
                return new Size(screenWidth, screenWidth);
            }
            case Constants.VideoRatio.RATIO_3X4: {
                return new Size(screenWidth, (int) Math.min(screenWidth / 3.0f * 4, screenHeight));
            }
            case Constants.VideoRatio.RATIO_9X16:
            default: {
                return new Size(screenWidth, screenHeight);
            }
        }
    }

    @Override
    public int getMaxZoomLevel() {
        return multiRecorder.getMaxZoomLevel();
    }

    @Override
    public void setZoomLevel(int zoomLevel) {
        multiRecorder.setZoomLevel(zoomLevel);
    }

    @Override
    public int getCurrentZoomLevel() {
        return multiRecorder.getCurrentZoomLevel();
    }

    private void setCameraAndCodecInfo(Size target) {
        target = CameraSizeUtil.selectMatchSize(activity.getApplicationContext(), target, RecorderConstants.ScaleMode.SCALE_MODE_WIDTH_FIXED, 16.0f / 9.0f);

        if (target.getWidth() >= 1280) {
            mrConfig.setVideoEncodeBitRate(8 << 20);
        } else if (target.getWidth() >= 960) {
            mrConfig.setVideoEncodeBitRate(7 << 20);
        } else if (target.getWidth() >= 640) {
            mrConfig.setVideoEncodeBitRate(6 << 20);
        }
        //        mrConfig.setEncodeSize(new Size(720, 1280));

        // 设置camera 的采集分辨率
//        mrConfig.setTargetVideoSize(target);
    }

    @Override
    public boolean isStartRecorded() {
        return startRecord;
    }

    @Override
    public void setStartRecorded(boolean start) {
        startRecord = start;
    }

    @Override
    public boolean isRecording() {
        return multiRecorder.isRecording();
    }

    @Override
    public int getAvgBitrate() {
        return 0;
    }

    @Override
    public void focusOnTouch(double x, double y, int viewWidth, int viewHeight) {
        multiRecorder.focusOnTouch(x, y, viewWidth, viewHeight);
    }

    private void stopPreview() {
        multiRecorder.stopPreview();
    }

    protected Object getTaskTag() {
        return hashCode();
    }

    private String generateCachePhotoPath() {
        File tempMomentDir = Configs.getDir("Camera");
        File file = new File(tempMomentDir, System.currentTimeMillis() + ".png_");
        return file.getAbsolutePath();
    }

    public static String getCfgPath() {
        File dir = null;
        String path = null;
        try {
            dir = AppContext.getContext().getCacheDir();
        } catch (Exception e) {
            Log4Android.getInstance().e(e);
        }
        if (dir != null) {
            path = dir.getAbsolutePath() + File.separator;
        }
        return path;
    }

    @Override
    public void setItemSelectSkinLevel(float[] value) {
        multiRecorder.setSkinAndLightingLevel(value[0], value[1]);
    }

    @Override
    public void setFaceEyeScale(float eyeScale) {
        multiRecorder.setFaceEyeScale(eyeScale);
    }

    @Override
    public void setFaceThinScale(float thinFaceScale) {
        multiRecorder.setFaceThinScale(thinFaceScale);
    }

    @Override
    public void setSlimmingScale(float value) {
        multiRecorder.setSlimmingScale(value);
    }

    @Override
    public void setLongLegScale(float value) {
        multiRecorder.setLongLegScale(value);
    }

    @Override
    public void switchCamera() {
        if (SystemClock.uptimeMillis() - lastSwitchCameraTime > SWITCH_CAMERA_TIME) {

            multiRecorder.switchCamera();
            MomoMainThreadExecutor.post(getTaskTag(), new Runnable() {
                @Override
                public void run() {
                    if (mView != null)
                        mView.initFlashAndSwitchButton();
                }
            });
            lastSwitchCameraTime = SystemClock.uptimeMillis();
        }
    }

    @Override
    public void startPreview() {
        if (!isSurfaceCreated) {
            return;
        }
        multiRecorder.startPreview();

        List<VideoFragment> fragments = multiRecorder.getVideoFragments();

        if (fragments != null && !fragments.isEmpty()) {
            mView.restoreByFragments(fragments);
        }
    }

    @Override
    public void startRecording() {
        multiRecorder.startRecording();
//        multiRecorder.startRecording(new MRecorderActions.OnRecordStartListener() {
//            @Override
//            public void onRecordStarted(final boolean isSuccessed) {
//                if (isSuccessed) {
//                    MomoMainThreadExecutor.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            mPresenter.onRecordStarted();
//                        }
//                    });
//                }
//            }
//        });
    }

    @Override
    public void setSpeed(float speed) {
        multiRecorder.setRecorderSpeed(speed);
    }

    @Override
    public void release() {
        //        if (null != mFilterChooser) {
        //            mFilterChooser.clear();
        //            mFilterChooser = null;
        //        }
        multiRecorder.release();
        MomoMainThreadExecutor.cancelAllRunnables(getTaskTag());
        activity = null;
        mView = null;
        XE3DEngine.getInstance().clearEvent();
    }

    @Override
    public void changeToFilter(int index, boolean up, float offset) {
        multiRecorder.changeToFilter(index, up, offset);
    }

    @Override
    public void setFilterIntensity(@FloatRange(from = 0, to = 1.0f) float intensity) {
        multiRecorder.setFilterIntensity(intensity);
    }

    @Override
    public void addFilter(BasicFilter filter) {
        multiRecorder.addFilter(filter, true);
    }

    /**
     * @param maskModel
     */
    @Override
    public boolean addMaskModel(MaskModel maskModel) {
        boolean result = multiRecorder.getRecorderMaskModelOperator().addMomentMaskModel(maskModel);
        if (result && mView != null) {
            mCurrentMaskModel = maskModel;
            mView.onMaskModelSet(maskModel);
        }
        return result;
    }

    @Override
    public boolean addCustomTypeMaskModel(MaskModel maskModel) {
        if (maskModel != null && maskModel.getStickers() != null) {
            List<Sticker> stickers = maskModel.getStickers();
            for (Sticker sticker : stickers) {
                sticker.setDuration(4 * 1000);
            }
        }
        boolean result = multiRecorder.getRecorderMaskModelOperator().addCustomMultiTypeMaskModel(888, maskModel);
        return result;
    }

    @Override
    public void clearFace() {
        mCurrentMaskModel = null;
        multiRecorder.getRecorderMaskModelOperator().clearMomentMaskModel();
    }

    @Override
    @Nullable
    public MusicContent getPlayMusic() {
        return playMusic;
    }

    @Override
    public boolean setPlayMusic(MusicContent musicContent) {
        playMusic = musicContent;
        if (null != musicContent) {
            return multiRecorder.setMusic(musicContent.path, musicContent.startMillTime, musicContent.endMillTime, true);
        } else {
            return multiRecorder.cancelMusic();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        multiRecorder.setVisualSize(UIUtils.getPixels(newConfig.screenWidthDp), UIUtils.getPixels(newConfig.screenHeightDp));

    }

    @Override
    public void initFilter(List<MMPresetFilter> filters) {
        multiRecorder.initFilters(filters);
    }

    @Override
    public void feedCameraZoomEvent(MotionEvent motionEvent) {
        mCameraZoomChecker.feedEvent(motionEvent);
    }

    @Override
    public void setFlashMode(int mode) {
        multiRecorder.setFlashMode(mode);
    }

    @Override
    public MaskModel getCurrentMaskModel() {
        return mCurrentMaskModel;
    }
}
