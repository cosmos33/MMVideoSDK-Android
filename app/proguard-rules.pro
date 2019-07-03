#-------------------------------------------基本不用动区域--------------------------------------------
#---------------------------------基本指令区----------------------------------
-optimizationpasses 5       # 指定代码的压缩级别
-dontusemixedcaseclassnames     # 是否使用大小写混合
-dontskipnonpubliclibraryclasses        # 指定不去忽略非公共的库类
-dontskipnonpubliclibraryclassmembers       # 指定不去忽略包可见的库类的成员
-dontpreverify      # 混淆时是否做预校验
-verbose        # 混淆时是否记录日志
#-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*      # 混淆时所采用的算法
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
#----------------------------------------------------------------------------
-ignorewarnings     # 是否忽略检测，（是）


#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#mmfile
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

# 播放器
-keep class tv.danmaku.ijk.media.** {*;}
-keep class com.momo.proxy.**{*;}

# keep 加了 Keep 注解的
-keep class androidx.annotation.Keep

-keep @androidx.annotation.Keep class * {*;}

-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}



-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}