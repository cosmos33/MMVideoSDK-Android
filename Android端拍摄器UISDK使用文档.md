# 拍摄器 UISDK 使用文档
采用源码接入和 demo 查看 可前往 [https://github.com/cosmos33/MMVideoSDK-Android](https://github.com/cosmos33/MMVideoSDK-Android)

## 接入前准备

### 注册

* 注册应用

* 开通短视频服务

### 工程配置

* 权限配置

接入SDK需要如下权限，将如下代码copy到主app的AndroidManifest.xml对应位置

```java
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

* 最小支持版本

  4.4  API19

* 添加工程依赖

```java
implementation 'com.cosmos.mediax:recorderuisdk:2.2.5'
```

* so架构

SDK目前只提供了armeabi-v7a 与armeabi 架构，请在app/build.gradle文件中配置如下代码。在android/defaultConfig/结构下添加

```
ndk {
    abiFilters "armeabi-v7a","armeabi"
}
```

* 混淆配置

```
# mmfile
-keep class com.cosmos.mmfile.**{*;}


# 保留本地native方法不被混淆
-keepclasseswithmembernames class * {
native <methods>;
}



-keep class com.core.glcore.util.** {*;}
-keep class com.momocv.** {*;}
-keep class com.imomo.momo.mediaencoder.** {*;}

-keep class com.imomo.momo.mediaencoder.MediaEncoder{*;}

-keep class com.imomo.momo.mediamuxer.** {*;}

-keep class com.cosmos.mdlog.** {*;}
-keep class com.immomo.moment.mediautils.VideoDataRetrieverBySoft {*;}
-keep class com.immomo.moment.mediautils.YuvEditor {*;}
-keep class com.immomo.moment.mediautils.AudioMixerNative {*;}
-keep class com.immomo.moment.mediautils.MP4Fast {*;}
-keep class com.immomo.moment.mediautils.AudioResampleUtils {*;}
-keep class com.immomo.moment.mediautils.AudioSpeedControlPlayer {*;}
-keep interface com.immomo.moment.mediautils.AudioSpeedControlPlayer$* {*;}
-keep interface com.immomo.moment.mediautils.VideoDataRetrieverBySoft$* {*;}
-keep class com.immomo.moment.mediautils.VideoDataRetrieverBySoft$* {*;}
-keep class * extends com.immomo.moment.mediautils.MediaUtils {*;}
-keep class com.immomo.moment.mediautils.FFVideoDecoder* {*;}
-keep class com.momoap.pitchshift.** {*;}

-keep class com.immomo.doki.media.entity.** {*;}
-keep class com.momo.mcamera.** {*;}
-dontwarn com.momo.mcamera.mask.**
-keep class com.google.gson.** {*;}

```

## 使用
----
### 初始化
调用 RecordUISDK init 方法

```java
    public static void init(Application context, String appId, IRecordResourceGetter recordResourceGetter)
```
其中需要实现 IRecordResourceGetter 来定制自己的资源，可定制 滤镜资源、美妆资源、静态贴纸资源、动态贴纸资源、拍摄器道具资源、配乐资源等。

### 去拍摄

```java
VideoRecordAndEditActivity.startRecord(this, new MMRecorderParams.Builder().build(), requestCode);
```

##### MMRecorderParams api 介绍
* 设置最终视频输出地址

```java
        /**
         * 设置最终视频输出地址
         *
         * @param videoOutputPath
         * @return
         */
        public Builder setVideoOutputPath(String videoOutputPath)
``` 

* 设置最终照片输出地址

```java
/**
         * 设置最终照片输出地址
         *
         * @param photoOutputPath
         * @return
         */
        public Builder setPhotoOutputPath(String photoOutputPath)
```

* 设置摄像头类型，前置或者后置

```java
/**
         * 设置摄像头类型，前置或者后置
         *
         * @param cameraType
         * @return
         */
        public Builder setCameraType(@CameraType int cameraType)
```

* 设置分辨率

```java
/**
         * 设置分辨率
         *
         * @param resolutionMode
         * @return
         */
        public Builder setResolutionMode(@Resolution int resolutionMode)
```

* 设置码率

```java
/**
         * 设置帧率
         *
         * @param frameRate
         * @return
         */
        public Builder setFrameRate(int frameRate)
```

* 最小录制时长

```java

        /**
         * 最小录制时长
         *
         * @param minDuration
         * @return
         */
        public Builder setMinDuration(long minDuration)

```

* 最大录制时长

```java
/**
         * 最大录制时长
         *
         * @param maxDuration
         * @return
         */
        public Builder setMaxDuration(long maxDuration)
```

* 视频比例

```java
/**
         * 视频比例
         *
         * @param videoRatio
         * @return
         */
        public Builder setVideoRatio(@VideoRatio int videoRatio)
```

* 需要使用的美颜版本

```java
/**
         * 需要使用的美颜版本
         *
         * @param beautyFaceVersion
         * @return
         */
        public Builder setBeautyFaceVersion(int beautyFaceVersion)
```

* 录制视频时是否录制音频

```java
/**
         * 录制视频时是否录制音频
         *
         * @param enableAudioRecorder
         * @return
         */
        public Builder setEnableAudioRecorder(boolean enableAudioRecorder)
```

* 录制视频时是否录制特效

```java
/**
         * 录制视频时是否录制特效
         *
         * @param enableSourceVideoRecord
         * @return
         */
        public Builder setEnableSourceVideoRecord(boolean enableSourceVideoRecord) 
```

* 进入录制页面时默认 tab

```java
 /**
         * 进入录制页面时默认 tab
         *
         * @param gotoTab
         * @return
         */
        public Builder setGotoTab(@RecordTab int gotoTab)
```

* 操作完成后 goto 信息，可以配置为跳转其他页面或者 onActivityResult 返回

```java
/**
         * 操作完成后 goto 信息，可以配置为跳转其他页面或者 onActivityResult 返回
         *
         * @param finishGotoInfo
         * @return
         */
        public Builder setFinishGotoInfo(FinishGotoInfo finishGotoInfo)
```

* 是否使用根据人脸区域自动调光

```java
        /**
         * 是否使用根据人脸区域自动调光
         */
        public Builder setEnableFaceAutoMetering(boolean enableFaceAutoMetering)
```

* 拍照的时候是否使用大分辨率
```java
        /**
         * 拍照的时候是否使用大分辨率
         */
        public Builder setEnableTakePhotoMaxResolution(boolean enableTakePhotoMaxResolutionMode)
``` 




-----

### 去选择媒体并编辑

```java
VideoRecordAndEditActivity.startChooseMediaToEdit(this,new MMChooseMediaParams.Builder().build(),requestCode);
```

##### MMChooseMediaParams api 介绍

* 单选或者多选

```java
/**
         * 单选或者多选
         * @param chooseMode
         * @return
         */
        public Builder setChooseMode(@EditChooseMode int chooseMode)
```

* 操作完成后 goto 信息，可以配置为跳转其他页面或者 onActivityResult 返回

```java
/**
         * 操作完成后 goto 信息，可以配置为跳转其他页面或者 onActivityResult 返回
         * @param finishGotoInfo
         * @return
         */
        public Builder setFinishGotoInfo(FinishGotoInfo finishGotoInfo)
```

* 设置需要加入选择的媒体类型，相册会跟据以下三种类型，去媒体库中拿相应类型的数据

```java
/**
         * 相册会跟据以下三种类型，去媒体库中拿相应类型的数据
         * @param mediaChooseType
         * @return
         */
        public Builder setMediaChooseType(@EditChooseMediaType int mediaChooseType)
```

* 选择本地图片时是否显示相机图标

```java
/**
         * 选择本地图片时是否显示相机图标
         * @param showCameraIcon
         * @return
         */
        public Builder setShowCameraIcon(boolean showCameraIcon)
```

* 选择本地媒体时显示哪些 tab

```java
/**
         * 选择本地媒体时显示哪些 tab
         * @param showAlbumTabs
         * @return
         */
        public Builder setShowAlbumTabs(@ShowMediaTabType int showAlbumTabs)
```

* 在编辑页面点击相机 icon 时的参数配置

```java
 /**
         * 在编辑页面点击相机 icon 时的参数配置
         * @param recordParams
         * @return
         */
        public Builder setGotoRecordParams(MMRecorderParams recordParams)
```

-----
### 获得操作完成后的媒体数据：

如果 FinishGotoInfo 配置的了 gotoActivityName 那么将跳转这个 Activity 并在 Intent 中带入处理完成后的信息，如果没有那么将在 onActivityResult 中返回。

###### 获取图片数据：

```java
if (data.hasExtra(MediaConstants.EXTRA_KEY_IMAGE_DATA)) {
                List<Photo> photos = (List<Photo>) data.getSerializableExtra(MediaConstants.EXTRA_KEY_IMAGE_DATA);
                if(photos!=null&&photos.size()>0){
                    Photo photo = photos.get(0);
                    Toaster.show(TextUtils.isEmpty(photo.tempPath)? photo.path: photo.tempPath);
                }
            }
```

###### 获取视频数据：

```java
if(data.hasExtra(MediaConstants.EXTRA_KEY_VIDEO_DATA)){
                Video video = data.getParcelableExtra(MediaConstants.EXTRA_KEY_VIDEO_DATA);
                Toaster.show(video.path);
            }
```


