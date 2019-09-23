package com.mm.recorduisdk.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;

import com.mm.mediasdk.RecorderConstants;
import com.mm.mediasdk.scope.BigEyeThinFaceTypeScope;
import com.mm.mediasdk.scope.BuffingTypeScope;
import com.mm.mediasdk.scope.WhiteningTypeScope;
import com.mm.recorduisdk.Constants;
import com.mm.recorduisdk.config.Configs;
import com.mm.recorduisdk.recorder.model.MusicContent;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created on 2018/2/5.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */

public class MMRecorderParams implements Parcelable {


    @IntDef({Constants.Resolution.RESOLUTION_720, Constants.Resolution.RESOLUTION_540, Constants.Resolution.RESOLUTION_480, Constants.Resolution.RESOLUTION_1080})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Resolution {
    }

    @IntDef({Constants.CameraType.FRONT, Constants.CameraType.BACK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CameraType {
    }

    @IntDef({Constants.VideoRatio.RATIO_1X1, Constants.VideoRatio.RATIO_3X4, Constants.VideoRatio.RATIO_9X16})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoRatio {

    }

    @IntDef({Constants.BeautyFaceVersion.V1, Constants.BeautyFaceVersion.V2, Constants.BeautyFaceVersion.V3})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BeautyFaceVersion {

    }

    @IntDef({Constants.RecordTab.PHOTO, Constants.RecordTab.VIDEO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RecordTab {

    }


    private final String photoOutputPath;
    private final String videoOutputPath;
    private final @CameraType
    int cameraType;
    private final @Resolution
    int resolutionMode;
    private final int videoBitrate;
    private final int frameRate;
    private final long minDuration;
    private final long maxDuration;
    private final @VideoRatio
    int videoRatio;

    private final boolean enableAudioRecorder;


    private final boolean enableSourceVideoRecord;
    private final String initFaceId;
    private final String initFaceClassId;
    private final int gotoTab;
    private final FinishGotoInfo finishGotoInfo;
    private final int speedIndex;

    private final MusicContent initMusic;
    private final int buffingType;

    private final int whiteningType;
    private final int bigEyeThinFaceType;

    private final boolean enableFaceAutoMetering;


    private final boolean enableTakePhotoMaxResolution;

    private MMRecorderParams(String photoOutputPath, String videoOutputPath, @CameraType int cameraType, @Resolution int resolutionMode,
                             int videoBitrate, int frameRate, long minDuration,
                             long maxDuration, @VideoRatio int videoRatio,
                             boolean enableAudioRecorder, boolean enableSourceVideoRecord,
                             String initFaceId, String initFaceClassId, int gotoTab,
                             FinishGotoInfo finishGotoInfo, int speedIndex, MusicContent initMusic,
                             int buffingType, int whiteningType, int bigEyeThinFaceType, boolean enableFaceAutoMetering,
                             boolean enableTakePhotoMaxResolutionMode
    ) {


        this.photoOutputPath = photoOutputPath;
        this.videoOutputPath = videoOutputPath;
        this.cameraType = cameraType;
        this.resolutionMode = resolutionMode;
        this.videoBitrate = videoBitrate;
        this.frameRate = frameRate;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.videoRatio = videoRatio;
        this.enableAudioRecorder = enableAudioRecorder;
        this.enableSourceVideoRecord = enableSourceVideoRecord;
        this.initFaceId = initFaceId;
        this.initFaceClassId = initFaceClassId;
        this.gotoTab = gotoTab;
        this.finishGotoInfo = finishGotoInfo;
        this.speedIndex = speedIndex;
        this.initMusic = initMusic;
        this.buffingType = buffingType;
        this.whiteningType = whiteningType;
        this.bigEyeThinFaceType = bigEyeThinFaceType;
        this.enableFaceAutoMetering = enableFaceAutoMetering;
        this.enableTakePhotoMaxResolution = enableTakePhotoMaxResolutionMode;
    }


    protected MMRecorderParams(Parcel in) {
        photoOutputPath = in.readString();
        videoOutputPath = in.readString();
        cameraType = in.readInt();
        resolutionMode = in.readInt();
        videoBitrate = in.readInt();
        frameRate = in.readInt();
        minDuration = in.readLong();
        maxDuration = in.readLong();
        videoRatio = in.readInt();
        enableAudioRecorder = in.readByte() != 0;
        enableSourceVideoRecord = in.readByte() != 0;
        initFaceId = in.readString();
        initFaceClassId = in.readString();
        gotoTab = in.readInt();
        finishGotoInfo = in.readParcelable(FinishGotoInfo.class.getClassLoader());
        speedIndex = in.readInt();
        initMusic = in.readParcelable(MusicContent.class.getClassLoader());
        buffingType = in.readInt();
        whiteningType = in.readInt();
        bigEyeThinFaceType = in.readInt();
        enableFaceAutoMetering = in.readByte() != 0;
        enableTakePhotoMaxResolution = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photoOutputPath);
        dest.writeString(videoOutputPath);
        dest.writeInt(cameraType);
        dest.writeInt(resolutionMode);
        dest.writeInt(videoBitrate);
        dest.writeInt(frameRate);
        dest.writeLong(minDuration);
        dest.writeLong(maxDuration);
        dest.writeInt(videoRatio);
        dest.writeByte((byte) (enableAudioRecorder ? 1 : 0));
        dest.writeByte((byte) (enableSourceVideoRecord ? 1 : 0));
        dest.writeString(initFaceId);
        dest.writeString(initFaceClassId);
        dest.writeInt(gotoTab);
        dest.writeParcelable(finishGotoInfo, flags);
        dest.writeInt(speedIndex);
        dest.writeParcelable(initMusic, flags);
        dest.writeInt(buffingType);
        dest.writeInt(whiteningType);
        dest.writeInt(bigEyeThinFaceType);
        dest.writeByte((byte) (enableFaceAutoMetering ? 1 : 0));
        dest.writeByte((byte) (enableTakePhotoMaxResolution ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MMRecorderParams> CREATOR = new Creator<MMRecorderParams>() {
        @Override
        public MMRecorderParams createFromParcel(Parcel in) {
            return new MMRecorderParams(in);
        }

        @Override
        public MMRecorderParams[] newArray(int size) {
            return new MMRecorderParams[size];
        }
    };

    public boolean isEnableTakePhotoMaxResolution() {
        return enableTakePhotoMaxResolution;
    }

    public boolean isEnableFaceAutoMetering() {
        return enableFaceAutoMetering;
    }

    public MusicContent getInitMusic() {
        return initMusic;
    }

    public boolean isEnableSourceVideoRecord() {
        return enableSourceVideoRecord;
    }

    public @VideoRatio
    int getVideoRatio() {
        return videoRatio;
    }

    public String getVideoOutputPath() {
        return videoOutputPath;
    }


    public String getInitFaceId() {
        return initFaceId;
    }

    public String getInitFaceClassId() {
        return initFaceClassId;
    }

    public int getGotoTab() {
        return gotoTab;
    }

    public String getPhotoOutputPath() {
        return photoOutputPath;
    }

    public FinishGotoInfo getFinishGotoInfo() {
        return finishGotoInfo;
    }

    public int getSpeedIndex() {
        return speedIndex;
    }

    public boolean isEnableAudioRecorder() {
        return enableAudioRecorder;
    }

    public @BuffingTypeScope
    int getBuffingType() {
        return buffingType;
    }

    public @WhiteningTypeScope
    int getWhiteningType() {
        return whiteningType;
    }

    public @BigEyeThinFaceTypeScope
    int getBigEyeThinFaceType() {
        return bigEyeThinFaceType;
    }

    public @CameraType
    int getCameraType() {
        return cameraType;
    }

    public @Resolution
    int getResolutionMode() {
        return resolutionMode;
    }

    public int getVideoBitrate() {
        return videoBitrate;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public long getMinDuration() {
        return minDuration;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public static class Builder {
        private String videoOutputPath = new File(Configs.getDir("ProcessVideo"), System.currentTimeMillis() + ".mp4").toString();
        private String photoOutputPath = new File(Configs.getDir("ProcessImage"), System.currentTimeMillis() + "_process.jpg").toString();

        private @CameraType
        int cameraType = Constants.CameraType.FRONT;

        private @Resolution
        int resolutionMode = Constants.Resolution.RESOLUTION_540;

        private int videoBitrate;

        private int frameRate = 20;

        private long minDuration = 2 * 1000;

        private long maxDuration = 60 * 1000;

        private @VideoRatio
        int videoRatio = Constants.VideoRatio.RATIO_9X16;
        private @BeautyFaceVersion
        int beautyFaceVersion = Constants.BeautyFaceVersion.V1;

        private boolean enableAudioRecorder = true;

        private boolean enableSourceVideoRecord;

        /**
         * 若initFaceId和initFaceClassId都不为空，进入录制页会自动下载并添加变脸
         */
        private String initFaceId;

        private String initFaceClassId;

        /**
         * 拍摄器中默认选中的tab
         */
        private @RecordTab
        int gotoTab = Constants.RecordTab.PHOTO;


        /**
         * 操作完成后的操作信息
         */
        private FinishGotoInfo finishGotoInfo = new FinishGotoInfo();

        /**
         * 变速档位
         */
        @IntRange(from = 0, to = 4)
        private int speedIndex = 2;


        /**
         * 初始的配乐
         */
        private MusicContent initMusic;

        private @BuffingTypeScope
        int buffingType = RecorderConstants.BuffingType.AILightweightBuffing;
        private @WhiteningTypeScope
        int whiteningType = RecorderConstants.WhiteningType.AIWhitening;
        private @BigEyeThinFaceTypeScope
        int bigEyeThinFaceType = RecorderConstants.BigEyeThinFaceType.AIBigEyeThinFace;

        /**
         * 是否使用根据人脸区域自动调光
         */
        private boolean enableFaceAutoMetering = true;
        /**
         * 拍照的时候是否使用大分辨率
         */
        private boolean enableTakePhotoMaxResolutionMode;

        public Builder() {

        }

        public Builder(MMRecorderParams mmRecorderParams) {
            setVideoOutputPath(mmRecorderParams.getVideoOutputPath());
            setPhotoOutputPath(mmRecorderParams.getPhotoOutputPath());
            setCameraType(mmRecorderParams.getCameraType());
            setResolutionMode(mmRecorderParams.getResolutionMode());
            setVideoBitrate(mmRecorderParams.getVideoBitrate());
            setFrameRate(mmRecorderParams.getFrameRate());
            setMinDuration(mmRecorderParams.getMinDuration());
            setMaxDuration(mmRecorderParams.getMaxDuration());
            setVideoRatio(mmRecorderParams.getVideoRatio());
            setEnableAudioRecorder(mmRecorderParams.isEnableAudioRecorder());
            setEnableSourceVideoRecord(mmRecorderParams.isEnableSourceVideoRecord());
            setInitFaceId(mmRecorderParams.getInitFaceId());
            setInitFaceClassId(mmRecorderParams.getInitFaceClassId());
            setGotoTab(mmRecorderParams.getGotoTab());
            setFinishGotoInfo(mmRecorderParams.getFinishGotoInfo());
            setSpeedIndex(mmRecorderParams.getSpeedIndex());
            setInitMusic(mmRecorderParams.getInitMusic());
            setBuffingType(mmRecorderParams.getBuffingType());
            setWhiteningType(mmRecorderParams.getWhiteningType());
            setEnableFaceAutoMetering(mmRecorderParams.isEnableFaceAutoMetering());
        }

        /**
         * 设置最终视频输出地址
         *
         * @param videoOutputPath
         * @return
         */
        public Builder setVideoOutputPath(String videoOutputPath) {
            this.videoOutputPath = videoOutputPath;
            return this;
        }

        /**
         * 设置最终照片输出地址
         *
         * @param photoOutputPath
         * @return
         */
        public Builder setPhotoOutputPath(String photoOutputPath) {
            this.photoOutputPath = photoOutputPath;
            return this;
        }

        /**
         * 设置摄像头类型，前置或者后置
         *
         * @param cameraType
         * @return
         */
        public Builder setCameraType(@CameraType int cameraType) {
            this.cameraType = cameraType;
            return this;
        }

        /**
         * 设置分辨率
         *
         * @param resolutionMode
         * @return
         */
        public Builder setResolutionMode(@Resolution int resolutionMode) {
            this.resolutionMode = resolutionMode;
            return this;
        }

        /**
         * 设置码率
         *
         * @param videoBitrate
         * @return
         */
        public Builder setVideoBitrate(int videoBitrate) {
            this.videoBitrate = videoBitrate;
            return this;
        }

        /**
         * 设置帧率
         *
         * @param frameRate
         * @return
         */
        public Builder setFrameRate(int frameRate) {
            this.frameRate = frameRate;
            return this;
        }

        /**
         * 最小录制时长
         *
         * @param minDuration
         * @return
         */
        public Builder setMinDuration(long minDuration) {
            this.minDuration = minDuration;
            return this;
        }

        /**
         * 最大录制时长
         *
         * @param maxDuration
         * @return
         */
        public Builder setMaxDuration(long maxDuration) {
            this.maxDuration = maxDuration;
            return this;
        }

        /**
         * 视频比例
         *
         * @param videoRatio
         * @return
         */
        public Builder setVideoRatio(@VideoRatio int videoRatio) {
            this.videoRatio = videoRatio;
            return this;
        }

        /**
         * 需要使用的美颜版本
         *
         * @param beautyFaceVersion
         * @return
         */
        public Builder setBeautyFaceVersion(int beautyFaceVersion) {
            this.beautyFaceVersion = beautyFaceVersion;
            return this;
        }

        /**
         * 录制视频时是否录制音频
         *
         * @param enableAudioRecorder
         * @return
         */
        public Builder setEnableAudioRecorder(boolean enableAudioRecorder) {
            this.enableAudioRecorder = enableAudioRecorder;
            return this;
        }

        /**
         * 录制视频时是否录制特效
         *
         * @param enableSourceVideoRecord
         * @return
         */
        public Builder setEnableSourceVideoRecord(boolean enableSourceVideoRecord) {
            this.enableSourceVideoRecord = enableSourceVideoRecord;
            return this;
        }


        /**
         * 进入录制页面时默认 tab
         *
         * @param gotoTab
         * @return
         */
        public Builder setGotoTab(@RecordTab int gotoTab) {
            this.gotoTab = gotoTab;
            return this;
        }

        /**
         * 操作完成后 goto 信息，可以配置为跳转其他页面或者 onActivityResult 返回
         *
         * @param finishGotoInfo
         * @return
         */
        public Builder setFinishGotoInfo(FinishGotoInfo finishGotoInfo) {
            this.finishGotoInfo = finishGotoInfo;
            return this;
        }


        public Builder setSpeedIndex(@IntRange(from = 0, to = 4) int speedIndex) {
            this.speedIndex = speedIndex;
            return this;
        }


        public Builder setInitMusic(MusicContent initMusic) {
            this.initMusic = initMusic;
            return this;
        }

        public Builder setInitFaceId(String initFaceId) {
            this.initFaceId = initFaceId;
            return this;
        }

        public Builder setInitFaceClassId(String initFaceClassId) {
            this.initFaceClassId = initFaceClassId;
            return this;
        }

        public Builder setBuffingType(@BuffingTypeScope int buffingType) {
            this.buffingType = buffingType;
            return this;
        }

        public Builder setWhiteningType(@WhiteningTypeScope int whiteningType) {
            this.whiteningType = whiteningType;
            return this;
        }
        /**
         * 是否使用根据人脸区域自动调光
         */
        public Builder setEnableFaceAutoMetering(boolean enableFaceAutoMetering) {
            this.enableFaceAutoMetering = enableFaceAutoMetering;
            return this;
        }
        /**
         * 拍照的时候是否使用大分辨率
         */
        public Builder setEnableTakePhotoMaxResolution(boolean enableTakePhotoMaxResolutionMode) {
            this.enableTakePhotoMaxResolutionMode = enableTakePhotoMaxResolutionMode;
            return this;
        }

        public MMRecorderParams build() {
            return new MMRecorderParams(photoOutputPath, videoOutputPath, cameraType, resolutionMode,
                    videoBitrate, frameRate, minDuration, maxDuration, videoRatio, enableAudioRecorder, enableSourceVideoRecord,
                    initFaceId, initFaceClassId, gotoTab, finishGotoInfo, speedIndex,
                    initMusic, buffingType, whiteningType, bigEyeThinFaceType, enableFaceAutoMetering, enableTakePhotoMaxResolutionMode
            );
        }


    }
}
