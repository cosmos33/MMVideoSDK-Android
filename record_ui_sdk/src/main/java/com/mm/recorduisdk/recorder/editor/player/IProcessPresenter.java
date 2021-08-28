package com.mm.recorduisdk.recorder.editor.player;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.view.TextureView;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

import com.immomo.moment.mediautils.cmds.EffectModel;
import com.immomo.moment.mediautils.cmds.TimeRangeScale;
import com.immomo.moment.mediautils.cmds.VideoCut;
import com.mm.mediasdk.IVideoEffectFilterModule;
import com.mm.recorduisdk.bean.MomentExtraInfo;
import com.mm.recorduisdk.recorder.model.MusicContent;
import com.mm.recorduisdk.recorder.model.Video;
import com.momo.mcamera.filtermanager.MMPresetFilter;

import java.util.List;

import project.android.imageprocessing.filter.BasicFilter;

/**
 * Created by XiongFangyu on 2017/8/9.
 */

public interface IProcessPresenter extends TextureView.SurfaceTextureListener {
    void addSpecialFilter(List<BasicFilter> basicFilters);

    List<MMPresetFilter> getAllFilters();

    void changeToFilter(int index);

    void setEffectModelForSpeedAdjust(EffectModel effectModel);

    EffectModel getEffectModelForSpeedAdjust();

    boolean hasChangeSpeed();

    List<TimeRangeScale> getTimeRangeScales(EffectModel em);

    long getCurrentRealVideoTime();

    void setLoopBack(boolean loopBack);

    void seekVideo(long time, boolean pause);

    boolean playVideo();

    void focusPlayVideo();

    void seekStatus(boolean status);

    void pause();

    void setNeedAutoPlay(boolean needAutoPlay);

    void onResume();

    boolean isPlaying();

    void updateEffectModelAndPlay(@Nullable List<TimeRangeScale> timeRangeScales, long seekTime);

    void updateEffectModelAndPlay(@Nullable List<VideoCut> videoCut, @Nullable List<TimeRangeScale> timeRangeScales, long seekTime);

    void updateEffectModelWithoutPlay(@Nullable List<TimeRangeScale> timeRangeScales, long seekTime);

    void updateEffectModelAndPlay(long seekTime);

    void onDestroy();

    void restartVideo();

    void releaseProcess();

    void addMaskModel(String folderPath, float posX, float posY, float width);

    int getDynamicStickerCount();

    void updateMaskModel(PointF centerPoint, float scale, float angle, int stickerId);

    void setStickerTimeRange(int stickerId, long startTime, long endTime);

    void removeMaskModel(int stickerId);

    void setBlendBitmap(Bitmap bitmap, Bitmap easeBitmap);

    void updateVideo(Video video);

    /**
     * 强制合成
     *
     * @param forceMake
     */
    void makeVideo(boolean forceMake);

    void setProcessListener(VideoEditPresenter.OnProcessListener listener);

    String getPitchFile();

    void changeToPreviewMode();

    OnVolumeChangeListener getVolumeChangeListener();

    MomentExtraInfo getMomentExtraInfo();

    void setPlayMusic(MusicContent musicContent);

    void restoreEffectModel();

    /**
     * 调节滤镜强度
     *
     * @param value 取值范围：(0,1]
     */
    void setFilterIntensity(@FloatRange(from = 0, to = 1.0f) float value);

    /**
     * 添加视频处理虑镜模块
     *
     * @param effectFilterModule
     */
    void addVideoEffectFilterModel(IVideoEffectFilterModule effectFilterModule);

    /**
     * 删除视频处理虑镜模块
     *
     * @param effectFilterModule
     */
    void removeVideoEffectFilterModel(IVideoEffectFilterModule effectFilterModule);

    //</editor-fold>

}
