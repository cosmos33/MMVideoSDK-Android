package com.immomo.videosdk.recorder.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by XiongFangyu on 17/2/14.
 * 音乐
 */
public class MusicContent implements Parcelable, Serializable {
    public String id;
    public String name;
    public String path;
    public int length;
    public int startMillTime = 0;
    public int endMillTime = MUSIC_LENGTH;
    public String cover;

    public static final int MUSIC_LENGTH = 15000;

    public MusicContent() {
    }

    @Override
    public String toString() {
        return "MusicContent{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", length=" + length +
                ", startMillTime=" + startMillTime +
                ", endMillTime=" + endMillTime +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeInt(this.length);
        dest.writeInt(startMillTime);
        dest.writeInt(endMillTime);
        dest.writeString(this.cover);

    }

    protected MusicContent(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.path = in.readString();
        this.length = in.readInt();
        startMillTime = in.readInt();
        endMillTime = in.readInt();
        this.cover = in.readString();
    }

    public static final Creator<MusicContent> CREATOR = new Creator<MusicContent>() {
        @Override
        public MusicContent createFromParcel(Parcel source) {
            return new MusicContent(source);
        }

        @Override
        public MusicContent[] newArray(int size) {
            return new MusicContent[size];
        }
    };

    public static boolean isSame(MusicContent m1, MusicContent m2) {
        if (m1 == null || m2 == null) {
            return false;
        }
        if ((!TextUtils.isEmpty(m1.path) && !TextUtils.isEmpty(m2.path) && TextUtils.equals(m1.path, m2.path))) {
            return true;
        }
        return false;
    }

    public void reset() {
        startMillTime = 0;
        endMillTime = MUSIC_LENGTH;
    }
}
