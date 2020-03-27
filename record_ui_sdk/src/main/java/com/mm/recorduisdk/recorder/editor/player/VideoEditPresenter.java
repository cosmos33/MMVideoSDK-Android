package com.mm.recorduisdk.recorder.editor.player;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.TextureView;

import com.core.glcore.config.Size;
import com.cosmos.mdlog.MDLog;
import com.immomo.moment.config.MRecorderActions;
import com.immomo.moment.mediautils.cmds.EffectModel;
import com.immomo.moment.mediautils.cmds.TimeRangeScale;
import com.immomo.moment.mediautils.cmds.VideoCut;
import com.immomo.moment.mediautils.cmds.VideoEffects;
import com.mm.mediasdk.IVideoProcessor;
import com.mm.mediasdk.MoMediaManager;
import com.mm.mediasdk.utils.ImageUtil;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.app.AppContext;
import com.mm.mmutil.task.MomoMainThreadExecutor;
import com.mm.recorduisdk.R;
import com.mm.recorduisdk.bean.MMVideoEditParams;
import com.mm.recorduisdk.bean.MomentExtraInfo;
import com.mm.recorduisdk.log.LogTag;
import com.mm.recorduisdk.recorder.model.MusicContent;
import com.mm.recorduisdk.recorder.model.Video;
import com.mm.recorduisdk.recorder.sticker.StickerEntity;
import com.mm.recorduisdk.recorder.sticker.StickerIDUtils;
import com.mm.recorduisdk.utils.VideoUtils;
import com.mm.recorduisdk.utils.filter.FiltersManager;
import com.momo.mcamera.filtermanager.MMPresetFilter;
import com.momo.mcamera.mask.MaskModel;
import com.momo.mcamera.mask.MaskStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import project.android.imageprocessing.ext.BitmapBlendFilter;
import project.android.imageprocessing.filter.BasicFilter;

/**
 * Created by wang.renguang on 2018/8/7.
 */

public class VideoEditPresenter implements IProcessPresenter {

    @NonNull
    private final IProcessPlayerView view;
    private final MMVideoEditParams videoEditParams;
    private final Size mCustomPreviewSize;
    @NonNull
    private Video video;

    private IVideoProcessor videoProcessor;

    private boolean needAutoPlay = true;
    @Nullable
    private EffectModel effectModelForSpeedAdjust = null;
    private boolean loopback = true;

    private long currentPlayTime = 0L;

    private MomentExtraInfo momentExtraInfo;

    private boolean started = false;
    private boolean needForceNewFilter = true;
    private boolean isFirstCreate = true;
    private int stickerCount;

    //filter相关
    private BitmapBlendFilter textFilter;

    @Nullable
    private OnProcessListener mListener;

    public VideoEditPresenter(@NonNull IProcessPlayerView view, @NonNull MMVideoEditParams videoEditParams) {
        this(view, videoEditParams, null);
    }

    public VideoEditPresenter(@NonNull IProcessPlayerView view, @NonNull MMVideoEditParams videoEditParams, Size customPreviewSize) {
        this.view = view;
        this.videoEditParams = videoEditParams;
        this.video = videoEditParams.getVideo();
        videoProcessor = MoMediaManager.createVideoProcessor();
        videoProcessor.setSoftAudioDecoder(false);
        //        editFilterGroupWapper.addEndFilter(FiltersManager.getInstance().getFilterGroupByIndex(0, AppContext.getContext()));

        momentExtraInfo = new MomentExtraInfo(video);
        int size[] = customPreviewSize == null ? momentExtraInfo.getTargetSize() : new int[]{customPreviewSize.getWidth(), customPreviewSize.getHeight()};
        int fps = momentExtraInfo.getVideoFPS();
        int bitrate = momentExtraInfo.getVideoBitRate();
        boolean isUseCQ = momentExtraInfo.getUseCQ();
        videoProcessor.setOutVideoInfo(size[0], size[1], fps, bitrate);
        if(customPreviewSize!=null){
            videoProcessor.setCustomPreviewSizeAndOpenFixSizeMode(customPreviewSize);
        }
        mCustomPreviewSize = customPreviewSize;
    }

    @Override
    public void updateVideo(Video video) {
        videoProcessor.updateVideo(video.path);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            int bufferWidth = video.getWidth();
            int bufferHeight = video.getHeight();

            if (mCustomPreviewSize != null) {
                bufferWidth = mCustomPreviewSize.getWidth();
                bufferHeight = mCustomPreviewSize.getHeight();
            }

            surface.setDefaultBufferSize(bufferWidth, bufferHeight);
        }
        if (isFirstCreate) {
            // 只有第一次需要初始化
            isFirstCreate = false;
            initForVideo(surface);
        } else {
            videoProcessor.addSurfaceTexture(surface);
            if (videoProcessor.isPreviewMode()) {
                videoProcessor.startPreview();
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        int bufferWidth = video.getWidth();
        int bufferHeight = video.getHeight();

        if (mCustomPreviewSize != null) {
            bufferWidth = mCustomPreviewSize.getWidth();
            bufferHeight = mCustomPreviewSize.getHeight();
        }

        surface.setDefaultBufferSize(bufferWidth, bufferHeight);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (!alreadyRelease) {
            videoProcessor.stopPreview();
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        int bufferWidth = video.getWidth();
        int bufferHeight = video.getHeight();

        if (mCustomPreviewSize != null) {
            bufferWidth = mCustomPreviewSize.getWidth();
            bufferHeight = mCustomPreviewSize.getHeight();
        }

        surface.setDefaultBufferSize(bufferWidth, bufferHeight);
    }

    private void initForVideo(@Nullable SurfaceTexture surfaceTexture) {
        if (surfaceTexture != null) {
            videoProcessor.addSurfaceTexture(surfaceTexture);
        }
        if (null != video.playingMusic) {
            videoProcessor.prepareVideo(video.path, video.playingMusic.path, video.playingMusic.startMillTime, video.playingMusic.endMillTime, video.osPercent, video.psPercent);
        } else {
            videoProcessor.prepareVideo(video.path, null, 0, 0, video.osPercent, video.psPercent);
        }
        videoProcessor.initFilters(getAllFilters());
        initProcessAndPlay();
    }

    private void initProcessAndPlay() {

        videoProcessor.setPlayingStatusListener(new MRecorderActions.OnPlayingStatusListener() {
            @Override
            public void onPlayingPaused() {
                view.onPlayingPaused();
            }

            @Override
            public void onPlayingPtsMs(long ptsMs) {
                view.onPlaying(ptsMs);
            }

            @Override
            public void onPlayingProgress(float progress) {
                currentPlayTime = (long) (progress * video.length);
                view.onProcessProgress(progress);
            }

            @Override
            public void onPlayingFinished() {
                view.onProcessFinish();
            }
        });

        if (video.soundPitchMode != 0 && VideoUtils.isSupportSoundPitch()) {
            videoProcessor.setPitchShiftProcessMode(getPitchFile(), video.path, video.soundPitchMode, new MRecorderActions.DataProcessListener() {
                @Override
                public void onProcessError(int msg, Exception e) {
                    view.onPitchShiftProcessError(msg, e);
                }

                @Override
                public void onProcessProgress(float progress) {

                }

                @Override
                public void onProcessFinished() {
                    view.onPitchShiftProcessFinished();
                    MomoMainThreadExecutor.post(new Runnable() {
                        @Override
                        public void run() {
                            startPlayVideoIfNeed();
                        }
                    });
                }
            });
            view.showProcessAudioProgress();
        } else {
            startPlayVideoIfNeed();
        }
    }

    public void startPlayVideoIfNeed() {
        startPlayVideo(currentPlayTime);
    }

    @Override
    public void setNeedAutoPlay(boolean needAutoPlay) {
        videoProcessor.setNeedAutoPlay(needAutoPlay);
    }

    @Override
    public void addSpecialFilter(List<BasicFilter> basicFilters) {
        videoProcessor.addSpecialFilter(basicFilters);
    }

    private BasicFilter lastFilter;

    private List<MMPresetFilter> filters;

    @Override
    public List<MMPresetFilter> getAllFilters() {
        if (null == filters) {
            filters = FiltersManager.getAllFilters();
        }
        return filters;
    }

    @Override
    public void changeToFilter(int index) {
        videoProcessor.changeToFilter(index, false, 0);
    }


    @Override
    public void setEffectModelForSpeedAdjust(EffectModel effectModel) {
        effectModelForSpeedAdjust = effectModel;
        if (null != effectModelForSpeedAdjust && null != effectModelForSpeedAdjust.getVideoEffects()) {
            updateEffectModelAndPlay(effectModelForSpeedAdjust.getVideoEffects().getTimeRangeScales(), 0L);
        } else {
            updateEffectModelAndPlay(null, 0);
        }
    }

    @Override
    public EffectModel getEffectModelForSpeedAdjust() {
        return effectModelForSpeedAdjust;
    }

    @Override
    public boolean hasChangeSpeed() {
        List<TimeRangeScale> timeRangeScales = getTimeRangeScales(effectModelForSpeedAdjust);
        if (timeRangeScales == null || timeRangeScales.size() <= 0) {
            return false;
        }
        for (TimeRangeScale timeRangeScale : timeRangeScales) {
            if (timeRangeScale.getSpeed() != 1.0f) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void pause() {
        if (started) {
            videoProcessor.pause();
        }
    }

    @Override
    public void onResume() {
        restartVideo();
    }

    @Override
    public void onDestroy() {
        releaseProcess();
        MomoMainThreadExecutor.cancelAllRunnables(getTaskTag());
        File pitch = new File(getPitchFile());
        pitch.delete();
    }

    @Override
    public void restartVideo() {
        videoProcessor.restartVideo(view.getVideoView().getSurfaceTexture());

    }

    @Override
    public boolean isPlaying() {
        return videoProcessor.isPlaying();
    }

    @Override
    public void updateEffectModelAndPlay(@Nullable List<TimeRangeScale> timeRangeScales, long seekTime) {
        videoProcessor.updateEffect(timeRangeScales, seekTime, true);
    }

    @Override
    public void updateEffectModelAndPlay(@Nullable List<VideoCut> videoCut, @Nullable List<TimeRangeScale> timeRangeScales, long seekTime) {
        if (videoCut == null) {
            videoCut = new ArrayList<>();
            videoCut.add(new VideoCut(video.path, 0, video.length, false));
        }
        videoProcessor.updateEffect(videoCut, timeRangeScales, seekTime, true);
    }

    @Override
    public void updateEffectModelWithoutPlay(@Nullable List<TimeRangeScale> timeRangeScales, long seekTime) {
        videoProcessor.updateEffect(timeRangeScales, seekTime, false);
    }

    @Override
    public void updateEffectModelAndPlay(long seekTime) {
        videoProcessor.updateEffect(seekTime, true);
    }

    private boolean alreadyRelease;

    @Override
    public void releaseProcess() {
        videoProcessor.release();
        alreadyRelease = true;
        needForceNewFilter = true;
        started = false;
    }

    @Override
    public void addMaskModel(String folderPath, float posX, float posY, float width) {
        TextureView videoView = view.getVideoView();
        if (videoView == null)
            return;
        MaskModel model = MaskStore.getInstance().getMask(AppContext.getContext(), folderPath);
        if (null == model) {
            return;
        }

        Rect viewRect = new Rect();
        videoView.getGlobalVisibleRect(viewRect);


        //计算坐标
        //为满足"动态贴纸在视频中的位置与面板中一致。"这一需求。
        final int displayWidth = videoView.getWidth();
        final int displayHeight = videoView.getHeight();


        posX = displayWidth / 2 - (int) width / 2;
        posY = displayHeight / 2 - (int) width / 2;
        if (posX < 0) {
            posX = 0;
        }
        if (posY < 0) {
            posY = 0;
        }

        int stickerId = StickerIDUtils.nextSeqId();


        final StickerEntity stickerEntity = new StickerEntity();
        stickerEntity.setCover(model.getPreviewPath());
        stickerEntity.setId(stickerId);
        StickerEntity.StickerLocationEntity entity = new StickerEntity.StickerLocationEntity();
        entity.setOriginx(posX);
        entity.setOriginy(posY);
        entity.setWidth(width);
        entity.setHeight(width);
        stickerEntity.setLocationScreen(entity);

        Drawable drawable = UIUtils.getDrawable(R.drawable.bg_sticker_edit);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);

        view.onAddSticker(viewRect, bitmap, stickerEntity);


        videoProcessor.addMaskModel(model, stickerId, 0.5f, 0.5f);
        stickerCount++;
    }

    @Override
    public void removeMaskModel(int stickerId) {
        videoProcessor.removeMaskModel(stickerId);
        stickerCount--;
    }

    @Override
    public int getDynamicStickerCount() {
        return stickerCount;
    }

    @Override
    public void updateMaskModel(PointF centerPoint, float scale, float angle, int stickerId) {
        videoProcessor.updateMaskModel(centerPoint, scale, angle, stickerId);
    }

    @Override
    public void setStickerTimeRange(int stickerId, long startTime, long endTime) {
        videoProcessor.setStickerTimeRange(stickerId, startTime, endTime);
    }

    @Override
    public void setBlendBitmap(Bitmap bitmap, Bitmap easeBitmap) {
        videoProcessor.setBlendBitmap(bitmap, easeBitmap);
    }

    @Override
    public String getPitchFile() {
        File dir = new File(video.path).getParentFile();
        File pitch = new File(dir, "pitch.pcm");
        return pitch.getAbsolutePath();
    }

    private void startPlayVideo(long seek) {
        if (started) {
            return;
        }
        view.hideOrShowCover(false, true);
        videoProcessor.setLoopBack(loopback);
        videoProcessor.startPreview();
        started = true;
    }

    @Override
    public List<TimeRangeScale> getTimeRangeScales(EffectModel em) {
        if (em != null) {
            VideoEffects ve = em.getVideoEffects();
            if (ve != null)
                return ve.getTimeRangeScales();
        }
        return null;
    }

    @Override
    public long getCurrentRealVideoTime() {
        EffectModel em = getEffectModelForSpeedAdjust();
        if (em != null && em.getVideoEffects() != null && em.getVideoEffects().getTimeRangeScales() != null && em.getVideoEffects().getTimeRangeScales().size() > 0) {
            List<TimeRangeScale> timeRangeScales = em.getVideoEffects().getTimeRangeScales();

            long videoLength = video.length;
            for (TimeRangeScale timeRangeScale : timeRangeScales) {
                long start = timeRangeScale.getStart();
                long end = timeRangeScale.getEnd();
                float speed = timeRangeScale.getSpeed();
                long diff = end - start;
                videoLength = (long) (videoLength - diff + speed * diff);
            }
            return videoLength;
        }
        return video.length;
    }

    @Override
    public void setLoopBack(boolean loopBack) {
        this.loopback = loopBack;
        videoProcessor.setLoopBack(loopBack);
    }

    @Override
    public void seekVideo(long time, boolean pause) {
        videoProcessor.seekVideo(time, pause);
    }

    @Override
    public boolean playVideo() {
        return videoProcessor.playVideo();
    }

    @Override
    public void focusPlayVideo() {
        videoProcessor.resume();
    }

    @Override
    public void seekStatus(boolean status) {
        videoProcessor.setSeekStatus(status);
    }

    private Object getTaskTag() {
        return hashCode();
    }

    @Override
    public MomentExtraInfo getMomentExtraInfo() {
        return momentExtraInfo;
    }

    @Override
    public OnVolumeChangeListener getVolumeChangeListener() {
        return onVolumeChangeListener;
    }

    private OnVolumeChangeListener onVolumeChangeListener = new OnVolumeChangeListener() {
        @Override
        public void onMusicVolumeChanged(int musicVolume) {
            video.psPercent = musicVolume;
            videoProcessor.setPlayingMusicAudioRatio(musicVolume / 100f);

        }

        @Override
        public void onVideoVolumeChanged(int videoVolume) {
            video.osPercent = videoVolume;
            videoProcessor.setPlayingSrcAudioRatio(videoVolume / 100f);
        }
    };

    //----------------------------------------------  for encoding

    @Override
    public void setProcessListener(OnProcessListener listener) {
        mListener = listener;
    }

    @Override
    public void makeVideo(boolean forceMake) {

        if (momentExtraInfo == null) {
            if (mListener != null) {
                mListener.onProcessFailed("合成失败");
            }
            return;
        }

        final String sourceVideoPath = video.path;

        File videoFile = null;
        if (!TextUtils.isEmpty(sourceVideoPath))
            videoFile = new File(sourceVideoPath);
        if (videoFile == null || !videoFile.exists() || videoFile.length() != momentExtraInfo.getVideoFileSize()) {
            if (mListener != null) {
                mListener.onProcessFailed("合成失败");
            }
            return;
        }


        final File destFile = new File(videoEditParams.getOutputPath());

        boolean needProcess = forceMake || momentExtraInfo.isVideoChanged();
        if (!needProcess) {
            if (mListener != null) {
                mListener.onProcessFinish(sourceVideoPath);
            }
            return;
        }

        videoProcessor.setOnProcessErrorListener(new MRecorderActions.OnProcessErrorListener() {
            @Override
            public void onErrorCallback(int what, int errorCode, final String msg) {
                MomoMainThreadExecutor.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) {
                            mListener.onProcessFailed(msg);
                        }
                    }
                });

            }
        });

        videoProcessor.setOnStatusListener(new MRecorderActions.OnProcessProgressListener() {
            @Override
            public void onProcessProgress(final float progress) {
                MomoMainThreadExecutor.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null)
                            mListener.onProcessProgress(progress);
                    }
                });

            }

            @Override
            public void onProcessFinished() {
                MomoMainThreadExecutor.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mListener != null) {
                            if (textFilter != null) {
                                videoProcessor.deleteFilter(textFilter);
                            }
                            if (destFile.exists()) {
                                mListener.onProcessFinish(destFile.getAbsolutePath());
                            } else {
                                mListener.onProcessFailed("合成失败");
                            }
                        }
                    }
                });
            }
        });

        videoProcessor.setAudioMixMode(momentExtraInfo.getUseBgChanger());
        //添加文字贴纸或者水印
        BasicFilter textFilter = getTextFilter(momentExtraInfo.getBlendBitmap());

        if (textFilter != null) {
            videoProcessor.addFilter(textFilter);
        }
        videoProcessor.makeVideo(destFile.getAbsolutePath());
    }

    @Override
    public void changeToPreviewMode() {
        if (textFilter != null) {
            videoProcessor.deleteFilter(textFilter);
        }
        videoProcessor.changeToPreviewMode();
    }

    private BasicFilter getTextFilter(Bitmap textBitmap) {
        if (textBitmap == null || textBitmap.isRecycled()) {
            return null;
        }

        final int bw = textBitmap.getWidth();
        int[] size = momentExtraInfo.getTargetSize();

        if (bw > size[0]) {
            textBitmap = ImageUtil.zoomBitmap(textBitmap, size[0], size[1]);
        }

        if (textFilter == null) {
            textFilter = new BitmapBlendFilter();
        }
        textFilter.setBlendBitmap(textBitmap);

        return textFilter;
    }

    public interface OnProcessListener {

        void onProcessProgress(float progress);

        void onProcessFinish(final String videoPath);

        void onProcessStart();

        void onProcessFailed(String error);
    }

    @Override
    public void setPlayMusic(MusicContent musicContent) {
        if (null != musicContent) {
            MDLog.i(LogTag.PROCESSOR.PROCESS, "setPlayMusic %s start:%d  end:%d", musicContent.path, musicContent.startMillTime, musicContent.endMillTime);
            videoProcessor.setMusic(musicContent.path, musicContent.startMillTime, musicContent.endMillTime);
        } else {
            MDLog.i(LogTag.PROCESSOR.PROCESS, "setPlayMusic null");
            videoProcessor.setMusic(null, 0, 0);
        }
    }

    @Override
    public void restoreEffectModel() {
        updateEffectModelAndPlay(null, null, 0);
    }

    @Override
    public void setFilterIntensity(float value) {
        if (null != videoProcessor) {
            videoProcessor.setFilterIntensity(value);
        }
    }
}

