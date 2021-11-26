package com.mm.sdkdemo.utils;

import android.content.Context;

import com.core.glcore.util.PreferenceUtil;
import com.mm.mmutil.app.AppContext;

import java.io.File;

/**
 * Created by jiabin on 2017/6/29.
 */

public class FilterFileUtil {

    public static final String MOMENT_FILTER_FILE = "filterData";
    public static final String DOKI_FILTER_FILE = "doki_res";
    public static final String MOMENT_FILTER_IMG_FILE = "filterImg";
    public static final String MAKEUP_FILES = "makeup";
    public static final String MAKEUP_ASSETS_FILE = "makeup.zip";
    public static final String MAKE_UP_PNG = "makeup.png";
    public static final int FILTER_VERSION = 3;
    public static final int MAKEUP_VERSION = 10;
    public static final String KEY_DEMO_FILTER_VERSION = "key_demo_filter_version";
    public static final String KEY_DEMO_MAKEUP_VERSION = "key_demo_make_version";
    // 妆感文件下载地址
    public static final String MAKE_UP_FILE_URL = "https://img.momocdn.com/banner/7B/47/7B47D324-0069-F0FD-EB75-D4E3FC96790A20180727.png";

    /**
     * 保存滤镜数据根目录
     *
     * @return
     */
    public static File getMomentFilterHomeDir() {
        File dir = new File(getCacheDirectory(), MOMENT_FILTER_FILE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getMakeupHomeDir() {
        File dir = new File(getCacheDirectory(), MAKEUP_FILES);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static File getDokiFilterHomeDir() {
        File dir = new File(getCacheDirectory(), DOKI_FILTER_FILE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static boolean needUpdateFilter(Context context) {
        int currentSaveVersion = PreferenceUtil.getDefault(context).getInt(KEY_DEMO_FILTER_VERSION, -1);

        return FILTER_VERSION != currentSaveVersion;
    }

    public static void saveCurrentFilterVersion(Context context) {
        PreferenceUtil.getDefault(context).edit().putInt(KEY_DEMO_FILTER_VERSION, FILTER_VERSION).apply();
    }

    public static boolean needUpdateMakeup(Context context) {
        int currentSaveVersion = PreferenceUtil.getDefault(context).getInt(KEY_DEMO_MAKEUP_VERSION, -1);

        return MAKEUP_VERSION != currentSaveVersion;
    }

    public static void saveCurrentMakeupVersion(Context context) {
        PreferenceUtil.getDefault(context).edit().putInt(KEY_DEMO_MAKEUP_VERSION, MAKEUP_VERSION).apply();
    }

    public static File getCacheDirectory() {
        return AppContext.getContext().getFilesDir();
    }

    /**
     * 保存滤镜图片素材
     *
     * @return
     */
    public static File getMomentFilterImgHomeDir() {
        File dir = new File(getMomentFilterHomeDir().getPath(), MOMENT_FILTER_IMG_FILE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}