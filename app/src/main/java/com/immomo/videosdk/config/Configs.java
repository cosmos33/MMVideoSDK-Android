package com.immomo.videosdk.config;

import android.os.Environment;

import com.immomo.mmutil.log.Log4Android;

import java.io.File;
import java.io.IOException;

/**
 * 配置信息
 *
 * @author wenjianhua
 */
public abstract class Configs {

    public static final boolean DEBUG = true;

    // 默认档位
    public static final int DEFAULT_BEAUTY = 3;
    public static final int DEFAULT_BIG_EYE = 3;
    public static final int DEFAULT_FILTER_INDEX = 1;

    public static final float[] DOKI_BEAUTY = new float[]{0.0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f};

    public static final float[] DOKI_BIG_EYE = new float[]{0.0f, 0.1f, 0.2f, 0.35f, 0.45f, 0.65f};

    public static final float[] DOKI_THIN_FACE = new float[]{0.0f, 0.1f, 0.2f, 0.35f, 0.5f, 0.7f};

    public final static int IMAGE_TYPE_LOCAL_PATH = 1;
    public final static int IMAGE_TYPE_URL = 2;

    public final static int IMAGE_SAVE_QUALITY = 85;

    /**
     * SD卡的路径名
     */
    public static final String PATH_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 程序在SD卡的文件夹名称
     */
    public static final String DIR_APPHOME = "MomoVideoSDK";
    /**
     * 一级目录: 短视频缓存
     */
    public static final String DIR_NAME_MICROVIDEO = "/microVideo";

    /**
     * 应用程序在SD卡的根路径
     */
    public static final String PATH_APP_HOME;

    static {
        String homeName = DIR_APPHOME;

        if (!PATH_SDCARD.endsWith("/")) {
            homeName = "/" + DIR_APPHOME;
        }

        PATH_APP_HOME = PATH_SDCARD + homeName;
    }

    private static final String DIR_NAME_BUFFERED_AUDIO = "/music";
    /**
     * 一级目录：时刻所在的目录
     */
    public static final String PATH_MOMENT = PATH_APP_HOME + "/moment";

    private static File appHome = null;

    /**
     * 获取应用程序在SD卡的主目录。根据{@link Configs#PATH_APP_HOME}构建目录结构。
     * 如果文件夹是未创建的，那么就创建它。
     *
     * @return
     */
    public final static File getAppHome() {
        if (appHome == null) {
            File file = new File(PATH_APP_HOME);
            appHome = file;
        }

        if (!appHome.exists()) {
            appHome.mkdirs();
        }
        return appHome;
    }

    /**
     * 此方法不要在业务中调用
     * 改动：使用Glide替换UIL后，将所有文件都缓存到了 newcache目录下面，文件太多，会导致外接SD卡无法写入图片，图片加载失败
     * 解决方案：1.使用分级缓存；2.使用新的目录 newcache2 ，将旧的 newcache目录放入Cleaner里面慢慢清除
     *
     * @return
     */
    @Deprecated
    public static File getImageCacheDirOld() {
        return new File(getAppHome(), "newcache");
    }

    /**
     * 获取录制时网络音乐缓存目录
     * immomo/temp_audio
     *
     * @return
     */
    public final static File getMusicFile(String musicId) {
        File file = new File(getAppHome() + DIR_NAME_BUFFERED_AUDIO);
        if (!file.exists())
            file.mkdir();

        return new File(file, musicId);
    }

    public static File getDir(String dir, boolean nomediaDir) {
        File ret = new File(PATH_APP_HOME, dir);
        if (!ret.exists()) {
            ret.mkdirs();
        }
        if (nomediaDir) {
            //增加nomedia文件 防止临时的视频，被小米等手机的相册能够查看到
            File noMedia = new File(ret, ".nomedia");
            if (!noMedia.exists()) {
                try {
                    noMedia.createNewFile();
                } catch (IOException e) {
                    Log4Android.getInstance().e(e);
                }
            }
        }
        return ret;
    }

    public static File getDir(String dir) {
        return getDir(dir, false);
    }
}