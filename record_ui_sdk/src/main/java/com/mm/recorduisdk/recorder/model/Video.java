package com.mm.recorduisdk.recorder.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.mm.mmutil.StringUtils;

/**
 * Created by wangduanqing on 16/1/7.
 */
public class Video implements Parcelable {

    private int id;
    public String videoId;
    public int rotate;
    private int width;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int height;
    /**
     * 视频大小
     */
    public int size;
    /**
     * 平均码率
     */
    public int avgBitrate;
    /**
     * 视频长度
     */
    public long length;//毫秒单位
    /**
     * 视频路径
     */
    public String path;

    /**
     * 是否已经转码 避免使用特效滤镜时  多次转码
     */
    public boolean hasTranscoding;
    /**
     * 是否是选择的本地视频
     */
    public boolean isChosenFromLocal = false;
    /**
     * 播放的音乐
     */
    public MusicContent playingMusic;
    /**
     * 原音大小[0, 100]
     */
    public int osPercent = 50;
    /**
     * 音乐大小[0,100]
     */
    public int psPercent = 50;

    /**
     * 视频的的帧率
     */
    public float frameRate;

    /**
     * 是否截取过
     */
    public boolean isCut = false;
    /**
     * 变声
     */
    public int soundPitchMode = 0;

    /**
     * 本地上传视频原始文件大小
     */
    public long originSize = 0;

    public Video() {

    }

    public Video(String path) {
        this.id = -1;
        this.path = path;
    }

    public Video(int id, String path) {
        this.id = id;
        this.path = path;
    }

    protected Video(Parcel in) {
        id = in.readInt();
        videoId = in.readString();
        rotate = in.readInt();
        width = in.readInt();
        height = in.readInt();
        size = in.readInt();
        avgBitrate = in.readInt();
        length = in.readLong();
        path = in.readString();
        hasTranscoding = in.readByte() != 0;
        isChosenFromLocal = in.readByte() != 0;
        playingMusic = in.readParcelable(MusicContent.class.getClassLoader());
        osPercent = in.readInt();
        psPercent = in.readInt();
        frameRate = in.readFloat();
        isCut = in.readByte() != 0;
        soundPitchMode = in.readInt();
        originSize = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(videoId);
        dest.writeInt(rotate);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(size);
        dest.writeInt(avgBitrate);
        dest.writeLong(length);
        dest.writeString(path);
        dest.writeByte((byte) (hasTranscoding ? 1 : 0));
        dest.writeByte((byte) (isChosenFromLocal ? 1 : 0));
        dest.writeParcelable(playingMusic, flags);
        dest.writeInt(osPercent);
        dest.writeInt(psPercent);
        dest.writeFloat(frameRate);
        dest.writeByte((byte) (isCut ? 1 : 0));
        dest.writeInt(soundPitchMode);
        dest.writeLong(originSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public int getId() {
        return id;
    }

    public boolean hasMusic() {
        return playingMusic != null && !TextUtils.isEmpty(playingMusic.path);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Video) {
            Video video = (Video) o;
            return StringUtils.notEmpty(this.path) && this.path.equals(video.path);
        }
        return false;
    }
}
