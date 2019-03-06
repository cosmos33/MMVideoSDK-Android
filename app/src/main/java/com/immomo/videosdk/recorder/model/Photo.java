package com.immomo.videosdk.recorder.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.immomo.mmutil.StringUtils;

/**
 * Created by wangduanqing on 16/1/7.
 */
public class Photo implements Parcelable {

    // from MediaStore
    @Expose
    public long id;
    @Expose
    public long size;
    @Expose
    public long dateAdded;
    @Expose
    public String path;
    @Expose
    public String mimeType;
    @Expose
    public String bucketId;
    @Expose
    public String bucketName;

    // from user
    @Expose
    public int type; // 类型：TYPE_IMAGE、TYPE_VIDEO
    @Expose
    public boolean isCheck;
    @Expose
    public boolean isAlbumCheck;//相册桢选中
    @Expose
    public boolean isPictureCheck;//影集桢选中

    // for video
    @Expose
    public long duration;

    // for photo
    @Expose
    public int width;
    @Expose
    public int height;
    @Expose
    public int rotate;
    @Expose
    public boolean isOriginal;
    @Expose
    public boolean isLong;      // 是否为长图
    @Expose
    public boolean isTakePhoto; // 是否通过拍照返回的照片
    @Expose
    public String longThumbPath;// 长图缩略图路径，用于在图片面板中展示
    @Expose
    public String thumbPath;    // 缩略图路径
    @Expose
    public String tempPath;     // 经过编辑的图片路径
    @Expose
    public int positionInAll;   // 当前media在集合中的位置
    @Expose
    public int positionInSelect = -1;   // 当前media在选中的集合中的位置
    /**
     * 拍照打点参数
     */
    @Expose
    public String shootExra = "";
    /**
     * 编辑打点参数
     */
    @Expose
    public String editExtra = "";

    /**
     * face detect info
     */
    @Expose
    public String faceDetect;

    public boolean isFromCamera = false;

    public Photo() {
    }

    public Photo(int id, String path) {
        this.id = id;
        this.path = path;
    }

    protected Photo(Parcel in) {
        id = in.readLong();
        size = in.readLong();
        dateAdded = in.readLong();
        path = in.readString();
        mimeType = in.readString();
        bucketId = in.readString();
        bucketName = in.readString();
        type = in.readInt();
        isCheck = in.readByte() != 0;
        duration = in.readLong();
        width = in.readInt();
        height = in.readInt();
        rotate = in.readInt();
        isOriginal = in.readByte() != 0;
        isLong = in.readByte() != 0;
        isTakePhoto = in.readByte() != 0;
        longThumbPath = in.readString();
        thumbPath = in.readString();
        tempPath = in.readString();
        positionInAll = in.readInt();
        positionInSelect = in.readInt();
        shootExra = in.readString();
        editExtra = in.readString();
        faceDetect = in.readString();
        isAlbumCheck = in.readByte() != 0;
        isPictureCheck = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(size);
        dest.writeLong(dateAdded);
        dest.writeString(path);
        dest.writeString(mimeType);
        dest.writeString(bucketId);
        dest.writeString(bucketName);
        dest.writeInt(type);
        dest.writeByte((byte) (isCheck ? 1 : 0));
        dest.writeLong(duration);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(rotate);
        dest.writeByte((byte) (isOriginal ? 1 : 0));
        dest.writeByte((byte) (isLong ? 1 : 0));
        dest.writeByte((byte) (isTakePhoto ? 1 : 0));
        dest.writeString(longThumbPath);
        dest.writeString(thumbPath);
        dest.writeString(tempPath);
        dest.writeInt(positionInAll);
        dest.writeInt(positionInSelect);
        dest.writeString(shootExra);
        dest.writeString(editExtra);
        dest.writeString(faceDetect);
        dest.writeByte((byte) (isAlbumCheck ? 1 : 0));
        dest.writeByte((byte) (isPictureCheck ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public long getId() {
        return id;
    }

    /**
     * 改变photo选中状态
     *
     * @param newCheck boolean
     */
    public void changeChecked(boolean newCheck) {
        this.isCheck = newCheck;
        if (!isCheck) {
            //不再选中后，将原来设置的原图、旋转选项还原
            isOriginal = false;
            rotate = 0;
        }
    }

    public String getTempPath() {
        return tempPath;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public void setPhoto(Photo media) {
        if (media == null || media == this) {
            return;
        }
        this.id = media.id;
        this.size = media.size;
        this.dateAdded = media.dateAdded;
        this.path = media.path;
        this.mimeType = media.mimeType;
        this.bucketId = media.bucketId;
        this.bucketName = media.bucketName;
        this.type = media.type;
        this.isCheck = media.isCheck;
        this.isAlbumCheck = media.isAlbumCheck;
        this.isPictureCheck = media.isPictureCheck;

        // for video
        this.duration = media.duration;

        // for photo
        this.width = media.width;
        this.height = media.height;
        this.rotate = media.rotate;
        this.isOriginal = media.isOriginal;
        this.isTakePhoto = media.isTakePhoto;
        this.longThumbPath = media.longThumbPath;
        this.thumbPath = media.thumbPath;
        this.tempPath = media.tempPath;
        this.positionInAll = media.positionInAll;
        this.positionInSelect = media.positionInSelect;
        this.shootExra = media.shootExra;
        this.editExtra = media.editExtra;
    }

    public static boolean isImage(String type) {
        return TextUtils.equals(type, "image/jpg")
                || TextUtils.equals(type, "image/jpeg")
                || TextUtils.equals(type, "image/png")
                || TextUtils.equals(type, "image/gif")
                || TextUtils.equals(type, "image/webp")
                || TextUtils.equals(type, "image/heif")
                || TextUtils.equals(type, "image/heic");
    }

    /**
     * 判断图片文件后缀是否是图片后缀
     * 目前发现华为手机扫描图片时，会扫描到华为杂志锁屏的以.downloading为后缀的文件，此文件
     * 还没下载完，无法正常显示。
     *
     * @param path
     * @return
     */
    public static boolean isImageSuffix(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        return path.endsWith(".jpg")
                || path.endsWith(".jpeg")
                || path.endsWith(".png")
                || path.endsWith(".gif")
                || path.endsWith(".webp")
                || path.endsWith(".heif")
                || path.endsWith(".heic");
    }

    public static boolean isGif(String type) {
        return TextUtils.equals(type, "image/gif");
    }

    public static boolean isVideo(String type) {
        return TextUtils.equals(type, "video/mp4");
    }

    public static boolean isMp4(String type) {
        return TextUtils.equals(type, "video/mp4");
    }

    @Override
    public String toString() {
        return new StringBuilder("Photo[ ").append("id:").append(id).append("  path:").append(path).append("  isOriginal:").append(isOriginal).append("  size:").append(size).append("   tempPath:").append(tempPath).append("   isCheck:").append(isCheck).append("   mimeType:").append(mimeType).append("]").append(" isLong").append(" longThumbPath").toString();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Photo
                && StringUtils.notEmpty(this.path)
                && TextUtils.equals(this.path, ((Photo) o).path);
    }

}
