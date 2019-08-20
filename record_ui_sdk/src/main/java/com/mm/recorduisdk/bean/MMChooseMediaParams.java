package com.mm.recorduisdk.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.mm.recorduisdk.Constants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created on 2019/7/18.
 *
 * @author jianxi[mabeijianxi@gmail.com]
 */
public class MMChooseMediaParams implements Parcelable {


    private final int chooseMode;
    private final FinishGotoInfo finishGotoInfo;
    private final int mediaChooseType;
    private final int maxSelectedPhotoCount;
    private final long upperVideoCompressDuration;
    private final long upperVideoCompressBitRate;
    private final boolean showCameraIcon;
    private final int showAlbumTabs;
    private final int initAlbumIndex;

    private final MMRecorderParams recordParams;

    private MMChooseMediaParams(int chooseMode, FinishGotoInfo finishGotoInfo, int mediaChooseType, int maxSelectedPhotoCount, long upperVideoCompressDuration, long upperVideoCompressBitRate, boolean showCameraIcon, int showAlbumTabs, int initAlbumIndex, MMRecorderParams recordParams) {

        this.chooseMode = chooseMode;
        this.finishGotoInfo = finishGotoInfo;
        this.mediaChooseType = mediaChooseType;
        this.maxSelectedPhotoCount = maxSelectedPhotoCount;
        this.upperVideoCompressDuration = upperVideoCompressDuration;
        this.upperVideoCompressBitRate = upperVideoCompressBitRate;
        this.showCameraIcon = showCameraIcon;
        this.showAlbumTabs = showAlbumTabs;
        this.initAlbumIndex = initAlbumIndex;
        this.recordParams = recordParams;
    }


    @IntDef({Constants.EditChooseMode.MODE_STYLE_ONE, Constants.EditChooseMode.MODE_MULTIPLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EditChooseMode {
    }

    @IntDef({Constants.EditChooseMediaType.MEDIA_TYPE_IMAGE, Constants.EditChooseMediaType.MEDIA_TYPE_MIXED, Constants.EditChooseMediaType.MEDIA_TYPE_VIDEO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EditChooseMediaType {
    }

    @IntDef({Constants.ShowMediaTabType.STATE_ALBUM, Constants.ShowMediaTabType.STATE_VIDEO, Constants.ShowMediaTabType.STATE_ALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ShowMediaTabType {
    }

    @IntDef({Constants.ShowMediaTabType.STATE_ALBUM, Constants.ShowMediaTabType.STATE_VIDEO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AlbumInitTabIndex {
    }


    protected MMChooseMediaParams(Parcel in) {
        chooseMode = in.readInt();
        finishGotoInfo = in.readParcelable(FinishGotoInfo.class.getClassLoader());
        mediaChooseType = in.readInt();
        maxSelectedPhotoCount = in.readInt();
        upperVideoCompressDuration = in.readLong();
        upperVideoCompressBitRate = in.readLong();
        showCameraIcon = in.readByte() != 0;
        showAlbumTabs = in.readInt();
        initAlbumIndex = in.readInt();
        recordParams = in.readParcelable(MMRecorderParams.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(chooseMode);
        dest.writeParcelable(finishGotoInfo, flags);
        dest.writeInt(mediaChooseType);
        dest.writeInt(maxSelectedPhotoCount);
        dest.writeLong(upperVideoCompressDuration);
        dest.writeLong(upperVideoCompressBitRate);
        dest.writeByte((byte) (showCameraIcon ? 1 : 0));
        dest.writeInt(showAlbumTabs);
        dest.writeInt(initAlbumIndex);
        dest.writeParcelable(recordParams, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MMChooseMediaParams> CREATOR = new Creator<MMChooseMediaParams>() {
        @Override
        public MMChooseMediaParams createFromParcel(Parcel in) {
            return new MMChooseMediaParams(in);
        }

        @Override
        public MMChooseMediaParams[] newArray(int size) {
            return new MMChooseMediaParams[size];
        }
    };

    public MMRecorderParams getRecordParams() {
        return recordParams;
    }
    public int getChooseMode() {
        return chooseMode;
    }

    public FinishGotoInfo getFinishGotoInfo() {
        return finishGotoInfo;
    }

    public int getMediaChooseType() {
        return mediaChooseType;
    }

    public int getMaxSelectedPhotoCount() {
        return maxSelectedPhotoCount;
    }

    public long getUpperVideoCompressDuration() {
        return upperVideoCompressDuration;
    }

    public long getUpperVideoCompressBitRate() {
        return upperVideoCompressBitRate;
    }

    public boolean isShowCameraIcon() {
        return showCameraIcon;
    }

    public int getShowAlbumTabs() {
        return showAlbumTabs;
    }

    public int getInitAlbumIndex() {
        return initAlbumIndex;
    }

    public static class Builder {

        /**
         * 单选或者多选
         */
        private @EditChooseMode
        int chooseMode = Constants.EditChooseMode.MODE_MULTIPLE;
        /**
         * 完成后的操作信息
         */
        private FinishGotoInfo finishGotoInfo = new FinishGotoInfo();

        /**
         * 相册会跟据以下三种类型，去媒体库中拿相应类型的数据
         */
        private @EditChooseMediaType
        int mediaChooseType = Constants.EditChooseMediaType.MEDIA_TYPE_MIXED;

        /**
         * 最多选择多少张图片
         */
        private int maxSelectedPhotoCount = 10;

        /**
         * 选择本地视频时时长大于这个值会压缩。默认 60秒
         */
        private long upperVideoCompressDuration = 60 * 1000;

        /**
         * 选择本地视频时码率大于这个值会压缩。默认 5M
         */
        private long upperVideoCompressBitRate = 5 << 20;


        /**
         * 选择本地图片时是否显示相机图标
         */
        private boolean showCameraIcon = true;

        /**
         * 选择本地媒体时显示哪些 tab
         */
        private @ShowMediaTabType
        int showAlbumTabs = Constants.ShowMediaTabType.STATE_ALL;


        /**
         * 进入选择媒体时默认选中哪一帧
         */
        private @AlbumInitTabIndex
        int initAlbumIndex = Constants.ShowMediaTabType.STATE_ALBUM;

        private MMRecorderParams recordParams = new MMRecorderParams.Builder().build() ;

        public Builder() {

        }

        public Builder(MMChooseMediaParams chooseMediaParams) {
            setChooseMode(chooseMediaParams.getChooseMode());
            setFinishGotoInfo(chooseMediaParams.getFinishGotoInfo());
            setMediaChooseType(chooseMediaParams.getMediaChooseType());
            setMaxSelectedPhotoCount(chooseMediaParams.getMaxSelectedPhotoCount());
            setUpperVideoCompressDuration(chooseMediaParams.getUpperVideoCompressDuration());
            setUpperVideoCompressBitRate(chooseMediaParams.getUpperVideoCompressBitRate());
            setShowCameraIcon(chooseMediaParams.isShowCameraIcon());
            setShowAlbumTabs(chooseMediaParams.getShowAlbumTabs());
            setInitAlbumIndex(chooseMediaParams.getInitAlbumIndex());
        }

        /**
         * 单选或者多选
         * @param chooseMode
         * @return
         */
        public Builder setChooseMode(@EditChooseMode int chooseMode) {
            this.chooseMode = chooseMode;
            return this;
        }

        /**
         * 操作完成后 goto 信息，可以配置为跳转其他页面或者 onActivityResult 返回
         * @param finishGotoInfo
         * @return
         */
        public Builder setFinishGotoInfo(FinishGotoInfo finishGotoInfo) {
            this.finishGotoInfo = finishGotoInfo;
            return this;
        }

        /**
         * 相册会跟据以下三种类型，去媒体库中拿相应类型的数据
         * @param mediaChooseType
         * @return
         */
        public Builder setMediaChooseType(@EditChooseMediaType int mediaChooseType) {
            this.mediaChooseType = mediaChooseType;
            return this;
        }

        /**
         * 多选时最多选择的张数
         * @param maxSelectedPhotoCount
         * @return
         */
        public Builder setMaxSelectedPhotoCount(int maxSelectedPhotoCount) {
            this.maxSelectedPhotoCount = maxSelectedPhotoCount;
            return this;
        }

        /**
         * 选择本地视频时时长大于这个值会压缩。默认 60秒
         * @param upperVideoCompressDuration
         * @return
         */
        public Builder setUpperVideoCompressDuration(long upperVideoCompressDuration) {
            this.upperVideoCompressDuration = upperVideoCompressDuration;
            return this;
        }

        /**
         * 选择本地视频时码率大于这个值会压缩。默认 5M
         * @param upperVideoCompressBitRate
         * @return
         */
        public Builder setUpperVideoCompressBitRate(long upperVideoCompressBitRate) {
            this.upperVideoCompressBitRate = upperVideoCompressBitRate;
            return this;
        }

        /**
         * 选择本地图片时是否显示相机图标
         * @param showCameraIcon
         * @return
         */
        public Builder setShowCameraIcon(boolean showCameraIcon) {
            this.showCameraIcon = showCameraIcon;
            return this;
        }

        /**
         * 选择本地媒体时显示哪些 tab
         * @param showAlbumTabs
         * @return
         */
        public Builder setShowAlbumTabs(@ShowMediaTabType int showAlbumTabs) {
            this.showAlbumTabs = showAlbumTabs;
            return this;
        }

        /**
         * 进入选择媒体时默认选中哪一帧
         * @param initAlbumIndex
         * @return
         */
        public Builder setInitAlbumIndex(@AlbumInitTabIndex int initAlbumIndex) {
            this.initAlbumIndex = initAlbumIndex;
            return this;
        }

        /**
         * 在编辑页面点击相机 icon 时的参数配置
         * @param recordParams
         * @return
         */
        public Builder setGotoRecordParams(MMRecorderParams recordParams){
            this.recordParams = recordParams;
            return this;
        }

        public MMChooseMediaParams build() {
            return new MMChooseMediaParams(chooseMode, finishGotoInfo, mediaChooseType,
                    maxSelectedPhotoCount, upperVideoCompressDuration, upperVideoCompressBitRate,
                    showCameraIcon, showAlbumTabs, initAlbumIndex,recordParams
            );
        }
    }
}
