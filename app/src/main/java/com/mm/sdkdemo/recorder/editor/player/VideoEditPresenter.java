package com.mm.sdkdemo.recorder.editor.player;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.immomo.moment.config.MRecorderActions;
import com.immomo.moment.mediautils.cmds.EffectModel;
import com.immomo.moment.mediautils.cmds.TimeRangeScale;
import com.immomo.moment.mediautils.cmds.VideoCut;
import com.immomo.moment.mediautils.cmds.VideoEffects;
import com.cosmos.mdlog.MDLog;
import com.mm.mediasdk.IVideoProcessor;
import com.mm.mediasdk.MoMediaManager;
import com.mm.mediasdk.utils.ImageUtil;
import com.mm.mediasdk.utils.UIUtils;
import com.mm.mmutil.app.AppContext;
import com.mm.mmutil.task.MomoMainThreadExecutor;
import com.mm.sdkdemo.R;
import com.mm.sdkdemo.bean.MomentExtraInfo;
import com.mm.sdkdemo.config.Configs;
import com.mm.sdkdemo.log.LogTag;
import com.mm.sdkdemo.recorder.model.MusicContent;
import com.mm.sdkdemo.recorder.model.Video;
import com.mm.sdkdemo.recorder.sticker.StickerEntity;
import com.mm.sdkdemo.recorder.sticker.StickerIDUtils;
import com.mm.sdkdemo.utils.VideoUtils;
import com.mm.sdkdemo.utils.filter.FiltersManager;
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

    public VideoEditPresenter(@NonNull IProcessPlayerView view, @NonNull Video video) {
        this.view = view;
        this.video = video;
        videoProcessor = MoMediaManager.createVideoProcessor();
        //        editFilterGroupWapper.addEndFilter(FiltersManager.getInstance().getFilterGroupByIndex(0, AppContext.getContext()));

        momentExtraInfo = new MomentExtraInfo(video);
        int size[] = momentExtraInfo.getTargetSize();
        int fps = momentExtraInfo.getVideoFPS();
        int bitrate = momentExtraInfo.getVideoBitRate();
        boolean isUseCQ = momentExtraInfo.getUseCQ();
        videoProcessor.setOutVideoInfo(size[0], size[1], fps, bitrate);
    }

    @Override
    public void updateVideo(Video video) {
        videoProcessor.updateVideo(video.path);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            surface.setDefaultBufferSize(video.getWidth(), video.height);
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
        surface.setDefaultBufferSize(video.getWidth(), video.height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        videoProcessor.stopPreview();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        surface.setDefaultBufferSize(video.getWidth(), video.height);
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

    @Override
    public void releaseProcess() {
        videoProcessor.release();
        needForceNewFilter = true;
        started = false;
    }

    @Override
    public void addMaskModel(String folderPath, float posX, float posY, float width) {
        View videoView = view.getVideoView();
        if (videoView == null)
            return;
        MaskModel model = MaskStore.getInstance().getMask(AppContext.getContext(), folderPath);
        if (null == model) {
            return;
        }
        //计算坐标
        //为满足"动态贴纸在视频中的位置与面板中一致。"这一需求。
        final int displayWidth = videoView.getWidth();
        final int displayHeight = videoView.getHeight();
        final int screenWidth = UIUtils.getScreenWidth();
        final int screenHeight = UIUtils.getScreenHeight();

        Rect viewRect = new Rect();
        videoView.getGlobalVisibleRect(viewRect);

        posX = posX + (displayWidth - screenWidth) / 2;
        posY = posY + (displayHeight - screenHeight) / 2;

        int stickerDefaultWidth = (int) width;
        int stickerDefaultHeight = (int) width;

        int stickerId = StickerIDUtils.nextSeqId();
        if ((int) posX == 0 && (int) posY == 0) {
            posX = (viewRect.left + viewRect.right - stickerDefaultWidth) / 2;
            posY = (viewRect.top + viewRect.bottom - stickerDefaultWidth) / 2;
        }

        //对于可能出现在屏幕外的动态贴纸，将位置重置到中心
        if (posY + stickerDefaultHeight >= screenHeight - stickerDefaultHeight) {
            posX = (viewRect.left + viewRect.right - stickerDefaultWidth) / 2;
            posY = (viewRect.top + viewRect.bottom - stickerDefaultWidth) / 2;
        }

        //横屏模式自动显示中心
        if (displayWidth >= displayHeight) {
            posX = (viewRect.left + viewRect.right - stickerDefaultWidth) / 2;
            posY = (viewRect.top + viewRect.bottom - stickerDefaultWidth) / 2;
        }

        //针对部分特殊机型以及情况做适配，超出视频范围则重置到中心
        if (!viewRect.contains((int) (posX + stickerDefaultHeight / 2), (int) (posY + stickerDefaultHeight / 2))) {
            posX = (viewRect.left + viewRect.right - stickerDefaultWidth) / 2;
            posY = (viewRect.top + viewRect.bottom - stickerDefaultWidth) / 2;
        }

        final StickerEntity stickerEntity = new StickerEntity();
        stickerEntity.setCover(model.getPreviewPath());
        stickerEntity.setId(stickerId);
        StickerEntity.StickerLocationEntity entity = new StickerEntity.StickerLocationEntity();
        entity.setOriginx(posX);
        entity.setOriginy(posY);
        entity.setWidth(stickerDefaultWidth);
        entity.setHeight(stickerDefaultHeight);
        stickerEntity.setLocationScreen(entity);

        Drawable drawable = UIUtils.getDrawable(R.drawable.bg_sticker_edit);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_4444);

        view.onAddSticker(viewRect, bitmap, stickerEntity);

        videoProcessor.addMaskModel(model, stickerId, posX / displayWidth, posY / displayHeight);
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

        File cache = Configs.getDir("record");
        if (cache == null) {
            return;
        }

        final File destFile = new File(Configs.getDir("record"), System.currentTimeMillis() + ".mp4_");

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

