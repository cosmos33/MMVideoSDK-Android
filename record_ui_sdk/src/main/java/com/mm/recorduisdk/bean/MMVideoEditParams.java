package com.mm.recorduisdk.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.mm.recorduisdk.config.Configs;
import com.mm.recorduisdk.recorder.model.Video;

import java.io.File;

/**
 * Created on 2019/7/18.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class MMVideoEditParams implements Parcelable {

    private final Video video;
    private final FinishGotoInfo finishGotoInfo;
    private final long upperVideoCompressDuration;
    private final long upperVideoCompressBitRate;


    private final String outputPath;

    public MMVideoEditParams(Video video, FinishGotoInfo finishGotoInfo, long upperVideoCompressDuration, long upperVideoCompressBitRate, String outputPath) {

        this.video = video;
        this.finishGotoInfo = finishGotoInfo;
        this.upperVideoCompressDuration = upperVideoCompressDuration;
        this.upperVideoCompressBitRate = upperVideoCompressBitRate;
        this.outputPath = outputPath;
    }


    protected MMVideoEditParams(Parcel in) {
        video = in.readParcelable(Video.class.getClassLoader());
        finishGotoInfo = in.readParcelable(FinishGotoInfo.class.getClassLoader());
        upperVideoCompressDuration = in.readLong();
        upperVideoCompressBitRate = in.readLong();
        outputPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(video, flags);
        dest.writeParcelable(finishGotoInfo, flags);
        dest.writeLong(upperVideoCompressDuration);
        dest.writeLong(upperVideoCompressBitRate);
        dest.writeString(outputPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MMVideoEditParams> CREATOR = new Creator<MMVideoEditParams>() {
        @Override
        public MMVideoEditParams createFromParcel(Parcel in) {
            return new MMVideoEditParams(in);
        }

        @Override
        public MMVideoEditParams[] newArray(int size) {
            return new MMVideoEditParams[size];
        }
    };

    public Video getVideo() {
        return video;
    }

    public FinishGotoInfo getFinishGotoInfo() {
        return finishGotoInfo;
    }

    public long getUpperVideoCompressDuration() {
        return upperVideoCompressDuration;
    }

    public long getUpperVideoCompressBitRate() {
        return upperVideoCompressBitRate;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public static class Builder {

        /**
         * 视频信息
         */
        private final Video video;
        /**
         * 完成后的操作信息
         */
        private FinishGotoInfo finishGotoInfo = new FinishGotoInfo();
        /**
         * 本地视频时时长大于这个值会压缩。默认 60秒
         */
        private long upperVideoCompressDuration = 60 * 1000;

        /**
         * 本地视频时码率大于这个值会压缩。默认 5M
         */
        private long upperVideoCompressBitRate = 5 << 20;

        private String outputPath = new File(Configs.getDir("ProcessVideo"), System.currentTimeMillis() + ".mp4").toString();

        public Builder(Video video) {
            this.video = video;
        }

        public Builder(MMVideoEditParams videoEditParams, Video video) {
            this.video = video;
            setOutputPath(videoEditParams.getOutputPath());
            setFinishGotoInfo(videoEditParams.getFinishGotoInfo());
            setUpperVideoCompressBitRate(videoEditParams.getUpperVideoCompressBitRate());
            setUpperVideoCompressDuration(videoEditParams.getUpperVideoCompressDuration());
        }

        public Builder setFinishGotoInfo(FinishGotoInfo finishGotoInfo) {
            this.finishGotoInfo = finishGotoInfo;
            return this;
        }

        public Builder setUpperVideoCompressDuration(long upperVideoCompressDuration) {
            this.upperVideoCompressDuration = upperVideoCompressDuration;
            return this;
        }

        public Builder setUpperVideoCompressBitRate(long upperVideoCompressBitRate) {
            this.upperVideoCompressBitRate = upperVideoCompressBitRate;
            return this;
        }

        public Builder setOutputPath(String outputPath) {
            this.outputPath = outputPath;
            return this;
        }

        public MMVideoEditParams build() {
            return new MMVideoEditParams(video, finishGotoInfo, upperVideoCompressDuration, upperVideoCompressBitRate, outputPath);
        }
    }
}
