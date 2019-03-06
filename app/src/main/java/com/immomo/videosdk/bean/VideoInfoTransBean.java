package com.immomo.videosdk.bean;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.immomo.videosdk.recorder.MediaConstants;
import com.immomo.videosdk.recorder.model.MusicContent;
import com.immomo.videosdk.recorder.model.Video;
import com.immomo.videosdk.recorder.view.AlbumHomeFragment;
import com.immomo.videosdk.recorder.view.VideoRecordFragment;
import com.immomo.videosdk.utils.MomentUtils;
import com.immomo.videosdk.utils.album.AlbumConstant;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

/**
 * Created by XiongFangyu on 2017/6/8.
 */

@SuppressWarnings("WrongConstant")
public class VideoInfoTransBean implements Parcelable {

    public static final String BTN_TEXT_COMPLETE = "完成";

    /**
     * 底部tab锚点{@link #state}
     */
    public static final int STATE_CHOOSE_MEDIA = VideoRecordFragment.STATE_CHOOSE_MEDIA;
    public static final int STATE_DEFAULT_RECORD = VideoRecordFragment.STATE_DEFAULT_RECORD;
    public static final int STATE_ADVANCED_RECORD = VideoRecordFragment.STATE_ADVANCED_RECORD;

    /**
     * 特殊模式for {@link #mode}
     * {@link #mode}
     */
    public static final int MODE_DEFAULT = 0;   // 默认模式
    public static final int MODE_STYLE_ONE = 1; // 头像选择上传时，设置该mode
    public static final int MODE_BACK_RESULT_IMMEDIATLY = 2;    //在相册选择完图片或视频后，直接返回结果（先通过检测）

    public static final int SHOW_TAB_ALBUM = 0x0001;            // 显示相册tab
    public static final int SHOW_TAB_DEFAULT_RECORD = 0x0002;   // 显示普通拍摄tab
    public static final int SHOW_TAB_ADVANCE_RECORD = 0x0004;   // 显示高级拍摄tab
    public static final int SHOW_TAB_MASK = SHOW_TAB_ALBUM | SHOW_TAB_DEFAULT_RECORD | SHOW_TAB_ADVANCE_RECORD;

    @IntDef({STATE_CHOOSE_MEDIA, STATE_DEFAULT_RECORD, STATE_ADVANCED_RECORD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    @IntDef({MODE_DEFAULT, MODE_STYLE_ONE, MODE_BACK_RESULT_IMMEDIATLY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    @IntDef({AlbumHomeFragment.STATE_PICTURE_ALBUM, AlbumHomeFragment.STATE_ALBUM, AlbumHomeFragment.STATE_VIDEO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AlbumInitTabIndex {
    }

    /**
     * 若faceId和faceClassId都不为空，进入录制页会自动下载并添加变脸
     */
    @Nullable
    public String faceId = null;

    @Nullable
    public String faceClassId = null;

    public int fromState = -1;

    /**
     * 发布动态来源xxx.class.getName()
     */
    @Nullable
    public String from = null;

    /**
     * 是否需要水印，默认需要
     */

    private boolean needWaterMask = true;


    /**
     * 要显示的tab，默认全显示
     */
    public int showTabs = SHOW_TAB_MASK;

    /**
     * 若不为空，选择视频或者拍视频时将弹出toast提示
     */
    public String alertToast = null;

    /**
     * 相册页中发送\完成按钮，由外部传入
     */
    public String sendText = BTN_TEXT_COMPLETE;

    public boolean isNineToSixteen;

    public boolean originalCovers;

    /**
     * 初始进入那个tab
     * 默认普通录制
     */
    @State
    public int state = STATE_DEFAULT_RECORD;

    @Mode
    public int mode = MODE_DEFAULT;

    /**
     * 跳转类名，若不为空，处理成功后将通过startActivity方式跳转
     */
    public String gotoActivityName;

    /**
     * 可作为返回参数或跳转编辑图片参数
     * 不做修改，原样传入或返回
     * 若设置了gotoActivityName，则参数原样设置进入Intent中
     */
    public Bundle extraBundle;

    /** see {@link AlbumConstant#KEY_MEDIA_CONDITIONS} */
    /**
     * 相册会跟据以下三种类型，去媒体库中拿相应类型的数据
     * see {@link AlbumConstant#MEDIA_TYPE_IMAGE}
     * see {@link AlbumConstant#MEDIA_TYPE_VIDEO}
     * see {@link AlbumConstant#MEDIA_TYPE_MIXED}
     */
    public int mediaType = AlbumConstant.MEDIA_TYPE_MIXED;

    /**
     * 当包含图片类型时，是否支持gif
     */
    public boolean gifEnable;

    /**
     * 相册最多能选多少个图片
     */
    public int maxSelectedCount = AlbumConstant.MAX_SELECTED_COUNT;


    /**
     * 是否带有经纬度图片 8.10.3相册引导增加
     */
    public boolean hasLatLonPhotos;

    /**
     * 进入相册 需要的提示文案 null 不显示
     */
    public String chooseMediaTips;

    /**
     * 是否显示数码兽
     */
    public boolean showMonster;

    /**
     * goto 带过来的参数，打点需求
     */
    public String activityId;

    /**
     * 运营带过来的自动配乐的音乐id
     */

    public MusicContent musicContent;

    // 变速档位
    @IntRange(from = 0, to = 4)
    public int speedIndex = 2;
    /**
     * 编辑完成生成的视频是否需要拷贝一份到相册
     */
    public boolean saveToGallery = true;

    /**
     * 选择本地视频时，不需要压缩视频的条件： 新逻辑
     * 1、视频码率 <= upperVideoCompressBitRate
     * 2、视频时长 <= upperVideoCompressDuration
     */
    public long upperVideoCompressDuration = MediaConstants.UPPER_VIDEO_COMPRESS_DURATION;
    public long upperVideoCompressBitRate = MediaConstants.UPPER_VIDEO_COMPRESS_BITRATE;

    /**
     * 是否来自数码宝贝
     */
    public boolean isFromDigimonRecorder;

    /**
     * 是否业务拦截，有些业务在开启摄像头时不能只去相册，有点囧
     */
    public boolean blockBusiness = true;

    /**
     * 期望分辨率width
     */
    public int desireWidth = 0;
    /**
     * 期望分辨率height
     */
    public int desireHeight = 0;
    public boolean isFromArpetRecorder;

    ///

    public boolean isFragment;

    public int choseDelayTime;

    private long maxDuration;

    public int checkedTopicIndex;

    /**
     * 美颜等级
     */
    public int beautyLevel;
    /**
     * 大眼瘦脸等级
     */
    public int bigEyeAndThinLevel;

    /**
     * 瘦身等级
     */
    public int slimmingLevel;

    /**
     * 长腿等级
     */
    public int longLegsLevel;

    /**
     * 闪光灯
     */
    public int flashMode;

    /**
     * 相册界面相册桢是否显示拍摄入口。
     */
    private boolean showCamera = true;

    /**
     * 拍摄界面是否显示相册入口。
     */
    public boolean showAlbum = true;

    // 不序列化
    public Video video;

    public int showAlbumTabs = AlbumHomeFragment.STATE_ALBUM;//相册帧显示哪些tab(影集，相册，视频)

    @AlbumInitTabIndex
    public int initAlbumIndex;//相册帧初始化展示哪个tab(影集，相册，视频)

    public String logKey;


    public boolean needWaterMask() {
        return needWaterMask;
    }

    public boolean isShowCamera() {
        return !MomentUtils.isOnlyAlbum(this) && this.showCamera;
    }

    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public VideoInfoTransBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.faceId);
        dest.writeString(this.faceClassId);
        dest.writeInt(this.fromState);
        dest.writeString(this.from);
        dest.writeByte(this.needWaterMask ? (byte) 1 : (byte) 0);
        dest.writeInt(this.showTabs);
        dest.writeString(this.alertToast);
        dest.writeString(this.sendText);
        dest.writeByte(this.isNineToSixteen ? (byte) 1 : (byte) 0);
        dest.writeByte(this.originalCovers ? (byte) 1 : (byte) 0);
        dest.writeInt(this.state);
        dest.writeInt(this.mode);
        dest.writeString(this.gotoActivityName);
        dest.writeBundle(this.extraBundle);
        dest.writeInt(this.mediaType);
        dest.writeByte(this.gifEnable ? (byte) 1 : (byte) 0);
        dest.writeInt(this.maxSelectedCount);
        dest.writeByte(this.hasLatLonPhotos ? (byte) 1 : (byte) 0);
        dest.writeString(this.chooseMediaTips);
        dest.writeByte(this.showMonster ? (byte) 1 : (byte) 0);
        dest.writeString(this.activityId);
        dest.writeParcelable(this.musicContent, flags);
        dest.writeInt(this.speedIndex);
        dest.writeLong(this.upperVideoCompressDuration);
        dest.writeLong(this.upperVideoCompressBitRate);
        dest.writeByte(this.isFromDigimonRecorder ? (byte) 1 : (byte) 0);
        dest.writeByte(this.blockBusiness ? (byte) 1 : (byte) 0);
        dest.writeInt(this.desireWidth);
        dest.writeInt(this.desireHeight);
        dest.writeByte(this.isFromArpetRecorder ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFragment ? (byte) 1 : (byte) 0);
        dest.writeInt(this.choseDelayTime);
        dest.writeLong(this.maxDuration);
        dest.writeInt(this.checkedTopicIndex);
        dest.writeInt(this.beautyLevel);
        dest.writeInt(this.bigEyeAndThinLevel);
        dest.writeInt(this.slimmingLevel);
        dest.writeInt(this.longLegsLevel);
        dest.writeInt(this.flashMode);
        dest.writeByte(this.showCamera ? (byte) 1 : (byte) 0);
        dest.writeByte(this.showAlbum ? (byte) 1 : (byte) 0);
        dest.writeInt(this.showAlbumTabs);
        dest.writeInt(this.initAlbumIndex);
        dest.writeString(this.logKey);
    }

    protected VideoInfoTransBean(Parcel in) {
        this.faceId = in.readString();
        this.faceClassId = in.readString();
        this.fromState = in.readInt();
        this.from = in.readString();
        this.needWaterMask = in.readByte() != 0;
        this.showTabs = in.readInt();
        this.alertToast = in.readString();
        this.sendText = in.readString();
        this.isNineToSixteen = in.readByte() != 0;
        this.originalCovers = in.readByte() != 0;
        this.state = in.readInt();
        this.mode = in.readInt();
        this.gotoActivityName = in.readString();
        this.extraBundle = in.readBundle();
        this.mediaType = in.readInt();
        this.gifEnable = in.readByte() != 0;
        this.maxSelectedCount = in.readInt();
        this.hasLatLonPhotos = in.readByte() != 0;
        this.chooseMediaTips = in.readString();
        this.showMonster = in.readByte() != 0;
        this.activityId = in.readString();
        this.musicContent = in.readParcelable(MusicContent.class.getClassLoader());
        this.speedIndex = in.readInt();
        this.upperVideoCompressDuration = in.readLong();
        this.upperVideoCompressBitRate = in.readLong();
        this.isFromDigimonRecorder = in.readByte() != 0;
        this.blockBusiness = in.readByte() != 0;
        this.desireWidth = in.readInt();
        this.desireHeight = in.readInt();
        this.isFromArpetRecorder = in.readByte() != 0;
        this.isFragment = in.readByte() != 0;
        this.choseDelayTime = in.readInt();
        this.maxDuration = in.readLong();
        this.checkedTopicIndex = in.readInt();
        this.beautyLevel = in.readInt();
        this.bigEyeAndThinLevel = in.readInt();
        this.slimmingLevel = in.readInt();
        this.longLegsLevel = in.readInt();
        this.flashMode = in.readInt();
        this.showCamera = in.readByte() != 0;
        this.showAlbum = in.readByte() != 0;
        this.showAlbumTabs = in.readInt();
        this.initAlbumIndex = in.readInt();
        this.logKey = in.readString();
    }

    public static final Creator<VideoInfoTransBean> CREATOR = new Creator<VideoInfoTransBean>() {
        @Override
        public VideoInfoTransBean createFromParcel(Parcel source) {
            return new VideoInfoTransBean(source);
        }

        @Override
        public VideoInfoTransBean[] newArray(int size) {
            return new VideoInfoTransBean[size];
        }
    };
}
