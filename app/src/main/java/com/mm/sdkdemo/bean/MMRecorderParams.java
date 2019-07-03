package com.mm.sdkdemo.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.mm.mediasdk.RecorderConstants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created on 2018/2/5.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */

public class MMRecorderParams implements Parcelable {


    @IntDef({RecorderConstants.Resolution.RESOLUTION_720, RecorderConstants.Resolution.RESOLUTION_540, RecorderConstants.Resolution.RESOLUTION_480, RecorderConstants.Resolution.RESOLUTION_1080})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Resolution {
    }

    @IntDef({RecorderConstants.CameraType.FRONT, RecorderConstants.CameraType.BACK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CameraType {
    }

    @IntDef({RecorderConstants.VideoRatio.RATIO_1X1, RecorderConstants.VideoRatio.RATIO_3X4, RecorderConstants.VideoRatio.RATIO_9X16})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VideoRatio {

    }

    @IntDef({RecorderConstants.BeautyFaceVersion.V1, RecorderConstants.BeautyFaceVersion.V2, RecorderConstants.BeautyFaceVersion.V3})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BeautyFaceVersion {

    }

    private final String outPath;
    private final @CameraType
    int cameraType;
    private final @Resolution
    int resolutionMode;
    private final int videoBitrate;
    private final int frameRate;
    private final long minDuration;
    private final long maxDuration;
    private final @BeautyFaceVersion
    int beautyFaceVersion;
    private final @VideoRatio
    int videoRatio;

    private final boolean enableAudioRecorder;


    protected MMRecorderParams(Parcel in) {
        outPath = in.readString();
        cameraType = in.readInt();
        resolutionMode = in.readInt();
        videoBitrate = in.readInt();
        frameRate = in.readInt();
        minDuration = in.readLong();
        maxDuration = in.readLong();
        beautyFaceVersion = in.readInt();
        videoRatio = in.readInt();
        enableAudioRecorder = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(outPath);
        dest.writeInt(cameraType);
        dest.writeInt(resolutionMode);
        dest.writeInt(videoBitrate);
        dest.writeInt(frameRate);
        dest.writeLong(minDuration);
        dest.writeLong(maxDuration);
        dest.writeInt(beautyFaceVersion);
        dest.writeInt(videoRatio);
        dest.writeByte((byte) (enableAudioRecorder ? 1 : 0));
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

    public @VideoRatio
    int getVideoRatio() {
        return videoRatio;
    }

    private MMRecorderParams(String outPath, @CameraType int cameraType, @Resolution int resolutionMode,
                             int videoBitrate, int frameRate, long minDuration,
                             long maxDuration, @VideoRatio int videoRatio, int beautyFaceVersion, boolean enableAudioRecorder) {

        this.outPath = outPath;
        this.cameraType = cameraType;
        this.resolutionMode = resolutionMode;
        this.videoBitrate = videoBitrate;
        this.frameRate = frameRate;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
        this.videoRatio = videoRatio;
        this.beautyFaceVersion = beautyFaceVersion;
        this.enableAudioRecorder = enableAudioRecorder;
    }


    public String getOutPath() {
        return outPath;
    }

    public int getBeautyFaceVersion() {
        return beautyFaceVersion;
    }

    public boolean isEnableAudioRecorder() {
        return enableAudioRecorder;
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
        private String outputPath;

        private @CameraType
        int cameraType = RecorderConstants.CameraType.FRONT;

        private @Resolution
        int resolutionMode = RecorderConstants.Resolution.RESOLUTION_540;

        private int videoBitrate;

        private int frameRate = 20;

        private long minDuration = 2 * 1000;

        private long maxDuration = 60 * 1000;

        private @VideoRatio
        int videoRatio;
        private @BeautyFaceVersion
        int beautyFaceVersion = RecorderConstants.BeautyFaceVersion.V1;

        private boolean enableAudioRecorder = true;

        public Builder setOutputPath(String outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public Builder setCameraType(@CameraType int cameraType) {
            this.cameraType = cameraType;
            return this;
        }

        public Builder setResolutionMode(@Resolution int resolutionMode) {
            this.resolutionMode = resolutionMode;
            return this;
        }

        public Builder setVideoBitrate(int videoBitrate) {
            this.videoBitrate = videoBitrate;
            return this;
        }

        public Builder setFrameRate(int frameRate) {
            this.frameRate = frameRate;
            return this;
        }

        public Builder setMinDuration(long minDuration) {
            this.minDuration = minDuration;
            return this;
        }

        public Builder setMaxDuration(long maxDuration) {
            this.maxDuration = maxDuration;
            return this;
        }

        public Builder setVideoRatio(@VideoRatio int videoRatio) {
            this.videoRatio = videoRatio;
            return this;
        }

        public Builder setBeautyFaceVersion(int beautyFaceVersion) {
            this.beautyFaceVersion = beautyFaceVersion;
            return this;
        }

        public Builder setEnableAudioRecorder(boolean enableAudioRecorder) {
            this.enableAudioRecorder = enableAudioRecorder;
            return this;
        }

        public MMRecorderParams build() {
            return new MMRecorderParams(outputPath, cameraType, resolutionMode
                    , videoBitrate, frameRate, minDuration, maxDuration, videoRatio, beautyFaceVersion, enableAudioRecorder);
        }


    }
}
