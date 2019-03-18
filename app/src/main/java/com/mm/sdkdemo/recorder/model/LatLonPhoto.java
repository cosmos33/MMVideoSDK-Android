package com.mm.sdkdemo.recorder.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chenxin on 2018/10/10.
 */

public class LatLonPhoto implements Parcelable {

    public String path;

    public String id;

    public String guid;//图片的唯一标识

    public String longitude;

    public String latitude;

    public Bitmap thumbnailImg;

    public long dateAdded;

    public boolean isVideo;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.id);
        dest.writeString(this.guid);
        dest.writeString(this.longitude);
        dest.writeString(this.latitude);
        dest.writeParcelable(this.thumbnailImg, flags);
        dest.writeLong(this.dateAdded);
        dest.writeByte(this.isVideo ? (byte) 1 : (byte) 0);
    }

    public LatLonPhoto() {
    }

    protected LatLonPhoto(Parcel in) {
        this.path = in.readString();
        this.id = in.readString();
        this.guid = in.readString();
        this.longitude = in.readString();
        this.latitude = in.readString();
        this.thumbnailImg = in.readParcelable(Bitmap.class.getClassLoader());
        this.dateAdded = in.readLong();
        this.isVideo = in.readByte() != 0;
    }

    public static final Creator<LatLonPhoto> CREATOR = new Creator<LatLonPhoto>() {
        @Override
        public LatLonPhoto createFromParcel(Parcel source) {
            return new LatLonPhoto(source);
        }

        @Override
        public LatLonPhoto[] newArray(int size) {
            return new LatLonPhoto[size];
        }
    };
}
