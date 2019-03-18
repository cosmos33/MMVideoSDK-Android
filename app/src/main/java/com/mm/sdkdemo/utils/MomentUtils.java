package com.mm.sdkdemo.utils;

import android.hardware.Camera;
import android.os.Build;
import android.text.TextUtils;

import com.immomo.moment.mediautils.cmds.AudioBackground;
import com.immomo.moment.mediautils.cmds.AudioEffects;
import com.immomo.moment.mediautils.cmds.EffectModel;
import com.immomo.moment.mediautils.cmds.TimeRangeScale;
import com.immomo.moment.mediautils.cmds.VideoCut;
import com.immomo.moment.mediautils.cmds.VideoEffects;
import com.immomo.moment.mediautils.cmds.VideoFilter;
import com.mm.sdkdemo.bean.VideoInfoTransBean;
import com.mm.sdkdemo.utils.album.AlbumConstant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Project momodev
 * Package com.mm.momo.moment.utils
 * Created by tangyuchun on 7/22/16.
 * {
 * "media": "path",
 * "effects": {
 * "timeRangeScale": [
 * {
 * " start": 1000,
 * "end": 2000,
 * "speed": 1.5
 * },
 * {
 * "start": 3000,
 * "end": 5000,
 * "speed": 2
 * }
 * ],
 * "videoFilter": [
 * {
 * "filterType": "xxx",
 * "start": 1000,
 * "end": 5000,
 * "resource": "xxx"
 * },
 * {
 * "filterType": "xxx",
 * "start": 1000,
 * "end": 5000,
 * "resource": "xxx"
 * }
 * ],
 * "cut": [
 * {
 * "media": "xxx",
 * "start": 0,
 * "end": 15000
 * },
 * {
 * "media": "xxx",
 * "start": 0,
 * "end": 15000
 * }
 * ]
 * },
 * "audio": {
 * "source": {
 * "ratio": 1.0,
 * "cycle": true
 * },
 * "bg": [
 * {
 * "path": "xxx",
 * "ratio": 1.0,
 * "start": 2000,
 * "end": 5000,
 * "cycle": true
 * },
 * {
 * "path": "xxx",
 * "ratio": 1.0,
 * "start": 2000,
 * "end": 5000,
 * "cycle": true
 * }
 * ]
 * }
 * }
 */
public class MomentUtils {
    /**
     * 是否支持前置摄像头
     *
     * @return
     */
    public static boolean isSupportFrontCamera() {
        return Camera.getNumberOfCameras() >= 2;
    }

    /**
     * 4.4以上机器支持录制视频
     *
     * @return
     */
    public static boolean isSupportRecord() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * 4.2以下机型只支持选择相册中的图片，不支持拍照录视频
     *
     * @return
     */
    public static boolean isOnlySupportChooseImage() {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1);
    }

    /**
     * 锤子 4.4.2系统 屏蔽拍摄
     *
     * @return
     */
    public static boolean isChuiZiNt() {
        if (Build.VERSION.SDK_INT == 19 && "SM705".equals(Build.MODEL)) {
            return true;
        }
        return false;
    }

    public static boolean isOnlyAlbum(VideoInfoTransBean info) {
        boolean onlyAlbum = false;
        if (!MomentUtils.isSupportRecord()) {
            //Fabric打点 统计不支持时刻录制的手机
            info.mediaType = AlbumConstant.MEDIA_TYPE_IMAGE;
            if (TextUtils.isEmpty(info.alertToast))
                info.alertToast = "你的手机系统版本暂时不支持视频录制";
            // 系统版本 < 4.2，只展示相册
            if (MomentUtils.isOnlySupportChooseImage()) {
                onlyAlbum = true;
                info.showTabs = VideoInfoTransBean.SHOW_TAB_ALBUM;
                info.state = VideoInfoTransBean.STATE_CHOOSE_MEDIA;
                // 4.2 <= 系统版本 < 4.4，支持拍照，不支持拍摄
            } else if (info.state == VideoInfoTransBean.STATE_ADVANCED_RECORD) {
                info.state = VideoInfoTransBean.STATE_DEFAULT_RECORD;
            }
        }
        if (MomentUtils.isChuiZiNt()) {
            onlyAlbum = true;
            info.mediaType = AlbumConstant.MEDIA_TYPE_IMAGE;
            // 锤子4.4.2系统  只展示相册
            info.showTabs = VideoInfoTransBean.SHOW_TAB_ALBUM;
            info.state = VideoInfoTransBean.STATE_CHOOSE_MEDIA;
        }
        return onlyAlbum;
    }


    public static final EffectModel newSimpleEffectModel(String videoPath, float radio, List<VideoCut> videoCuts) {
        return newBuilder(videoPath).addVideoCuts(videoCuts).setAudioSource(radio, true).build();
    }


    public static final EffectModel newVideoAndBackgroundMusic(String videoPath, float videoRadio,
                                                               String musicPath, float musicRadio, int musicStart, int musicEnd, List<VideoCut> videoCuts) {
        return newBuilder(videoPath)
                .setAudioSource(videoRadio, true)
                .addVideoCuts(videoCuts)
                .addAudioBackground(musicPath, musicRadio, musicStart, musicEnd, true)
                .build();
    }

    public static final EffectModelBuilder newBuilder(String videoPath) {
        return new EffectModelBuilder(videoPath);
    }

    /**
     * 视频特效builder
     */
    public static final class EffectModelBuilder {
        private String path;

        private VideoEffects effects;
        private List<VideoCut> cuts;
        private List<TimeRangeScale> timeRangeScales;
        private List<VideoFilter> videoFilters;

        private AudioEffects audio;
        private AudioEffects.AudioSource source;
        private List<AudioBackground> bgs;

        public EffectModelBuilder(String videoPath) {
            this.path = videoPath;
        }

        public EffectModelBuilder addVideoCut(int start, int end, boolean needReverse) {
            VideoCut c = new VideoCut();
            c.setMedia(path);
            c.setStart(start);
            c.setEnd(end);
            c.setReverse(needReverse);
            return addVideoCut(c);
        }

        public EffectModelBuilder addVideoCut(VideoCut c) {
            if (c != null) {
                if (cuts == null) {
                    cuts = new ArrayList<>();
                    if (effects == null)
                        effects = new VideoEffects();
                    effects.setVideoCuts(cuts);
                }
                cuts.add(c);
            }
            return this;
        }

        public EffectModelBuilder addVideoCuts(Collection<VideoCut> cutList) {
            if (cutList != null) {
                if (cuts == null) {
                    cuts = new ArrayList<>();
                    if (effects == null)
                        effects = new VideoEffects();
                    effects.setVideoCuts(cuts);
                }
                cuts.addAll(cutList);
            }
            return this;
        }

        public EffectModelBuilder addTimeRangeScale(int start, int end, float speed) {
            TimeRangeScale t = new TimeRangeScale();
            t.setStart(start);
            t.setEnd(end);
            t.setSpeed(speed);
            return addTimeRangeScale(t);
        }

        public EffectModelBuilder addTimeRangeScale(TimeRangeScale t) {
            if (timeRangeScales == null) {
                timeRangeScales = new ArrayList<>();
                if (effects == null)
                    effects = new VideoEffects();
                effects.setTimeRangeScales(timeRangeScales);
            }
            timeRangeScales.add(t);
            return this;
        }

        public EffectModelBuilder addTimeRangeScales(Collection<TimeRangeScale> ts) {
            if (timeRangeScales == null) {
                timeRangeScales = new ArrayList<>();
                if (effects == null)
                    effects = new VideoEffects();
                effects.setTimeRangeScales(timeRangeScales);
            }
            timeRangeScales.addAll(ts);
            return this;
        }

        public EffectModelBuilder addVideoFilter(String type, String resource, int start, int end) {
            VideoFilter v = new VideoFilter();
            v.setFilterType(type);
            v.setResource(resource);
            v.setStart(start);
            v.setEnd(end);
            return addVideoFilter(v);
        }

        public EffectModelBuilder addVideoFilter(VideoFilter v) {
            if (videoFilters == null) {
                videoFilters = new ArrayList<>();
                if (effects == null)
                    effects = new VideoEffects();
                effects.setVideoFilters(videoFilters);
            }
            videoFilters.add(v);
            return this;
        }

        public EffectModelBuilder addVideoFilters(Collection<VideoFilter> vs) {
            if (videoFilters == null) {
                videoFilters = new ArrayList<>();
                if (effects == null)
                    effects = new VideoEffects();
                effects.setVideoFilters(videoFilters);
            }
            videoFilters.addAll(vs);
            return this;
        }

        public EffectModelBuilder setAudioSource(float radio, boolean cycle) {
            if (source == null) {
                source = new AudioEffects.AudioSource();
                if (audio == null)
                    audio = new AudioEffects();
                audio.setAudioSource(source);
            }
            source.setCycle(cycle);
            source.setRatio(radio);
            return this;
        }

        public EffectModelBuilder addAudioBackground(String path, float radio, int start, int end, boolean cycle) {
            AudioBackground a = new AudioBackground();
            a.setBgPath(path);
            a.setRatio(radio);
            a.setStart(start);
            a.setEnd(end);
            a.setCycle(cycle);
            return addAudioBackground(a);
        }

        public EffectModelBuilder addAudioBackground(AudioBackground a) {
            if (bgs == null) {
                bgs = new ArrayList<>();
                if (audio == null)
                    audio = new AudioEffects();
                audio.setAudioBackgrounds(bgs);
            }
            bgs.add(a);
            return this;
        }

        public EffectModelBuilder addAudioBackgrounds(Collection<AudioBackground> as) {
            if (bgs == null) {
                bgs = new ArrayList<>();
                if (audio == null)
                    audio = new AudioEffects();
                audio.setAudioBackgrounds(bgs);
            }
            bgs.addAll(as);
            return this;
        }

        public final EffectModel build() {
            EffectModel model = new EffectModel();
            model.setMediaPath(path);
            if (effects == null)
                effects = new VideoEffects();
            model.setVideoEffects(effects);
            if (audio == null)
                audio = new AudioEffects();
            model.setAudioEffects(audio);
            return model;
        }
    }
}
