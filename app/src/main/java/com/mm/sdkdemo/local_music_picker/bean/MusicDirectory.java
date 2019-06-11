package com.mm.sdkdemo.local_music_picker.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.mm.sdkdemo.recorder.model.MusicContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XiongFangyu on 17/2/16.
 * 音乐文件夹
 */
public class MusicDirectory implements Parcelable {
    /**
     * 地址
     */
    private String pathName;
    /**
     * 文件夹名称
     */
    private String name;

    private List<MusicContent> musics;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MusicDirectory that = (MusicDirectory) o;

        if (pathName != null ? !pathName.equals(that.pathName) : that.pathName != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = pathName != null ? pathName.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MusicContent> getMusics() {
        return musics;
    }

    public void setMusics(List<MusicContent> musics) {
        this.musics = musics;
    }

    public void addMusic(MusicContent music) {
        if (musics == null) musics = new ArrayList<>();
        musics.add(music);
    }

    public int getMusicCount() {
        return musics != null ? musics.size() : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.pathName);
        dest.writeString(this.name);
        dest.writeTypedList(this.musics);
    }

    public MusicDirectory() {
    }

    protected MusicDirectory(Parcel in) {
        this.pathName = in.readString();
        this.name = in.readString();
        this.musics = in.createTypedArrayList(MusicContent.CREATOR);
    }

    public static final Creator<MusicDirectory> CREATOR = new Creator<MusicDirectory>() {
        @Override
        public MusicDirectory createFromParcel(Parcel source) {
            return new MusicDirectory(source);
        }

        @Override
        public MusicDirectory[] newArray(int size) {
            return new MusicDirectory[size];
        }
    };
}
