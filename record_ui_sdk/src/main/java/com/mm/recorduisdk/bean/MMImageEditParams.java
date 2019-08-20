package com.mm.recorduisdk.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.mm.recorduisdk.config.Configs;
import com.mm.recorduisdk.recorder.model.Photo;

import java.io.File;

/**
 * Created on 2019/7/18.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class MMImageEditParams implements Parcelable {

    private final Photo photo;
    private final FinishGotoInfo finishGotoInfo;
    private final long upperVideoCompressDuration;
    private final long upperVideoCompressBitRate;


    private final String outputPath;


    public MMImageEditParams(Photo photo, FinishGotoInfo finishGotoInfo, long upperVideoCompressDuration, long upperVideoCompressBitRate, String outputPath) {

        this.photo = photo;
        this.finishGotoInfo = finishGotoInfo;
        this.upperVideoCompressDuration = upperVideoCompressDuration;
        this.upperVideoCompressBitRate = upperVideoCompressBitRate;
        this.outputPath = outputPath;
    }


    protected MMImageEditParams(Parcel in) {
        photo = in.readParcelable(Photo.class.getClassLoader());
        finishGotoInfo = in.readParcelable(FinishGotoInfo.class.getClassLoader());
        upperVideoCompressDuration = in.readLong();
        upperVideoCompressBitRate = in.readLong();
        outputPath = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(photo, flags);
        dest.writeParcelable(finishGotoInfo, flags);
        dest.writeLong(upperVideoCompressDuration);
        dest.writeLong(upperVideoCompressBitRate);
        dest.writeString(outputPath);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MMImageEditParams> CREATOR = new Creator<MMImageEditParams>() {
        @Override
        public MMImageEditParams createFromParcel(Parcel in) {
            return new MMImageEditParams(in);
        }

        @Override
        public MMImageEditParams[] newArray(int size) {
            return new MMImageEditParams[size];
        }
    };

    public Photo getPhoto() {
        return photo;
    }

    public String getOutputPath() {
        return outputPath;
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


    public static class Builder {

        /**
         * 视频信息
         */
        private final Photo photo;
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

        private String outputPath = new File(Configs.getDir("ProcessImage"), System.currentTimeMillis() + "_process.jpg").toString();

        public Builder(Photo photo) {
            this.photo = photo;
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

        public MMImageEditParams build() {
            return new MMImageEditParams(photo, finishGotoInfo, upperVideoCompressDuration, upperVideoCompressBitRate, outputPath);
        }
    }
}
