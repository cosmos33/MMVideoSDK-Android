package com.mm.sdkdemo.utils;

import android.content.Context;

import com.core.glcore.util.PreferenceUtil;
import com.mm.mmutil.app.AppContext;

import java.io.File;

/**
 * Created by jiabin on 2017/6/29.
 */

public class LivePhotoUtil {

    public static final String LIVE_PHOTO_FILE = "photoLiveSrc";
    public static final int VERSION = 3;
    public static final String KEY_DEMO_PHOTO_LIVE_VERSION = "key_demo_photo_live_version";

    /**
     * 保存滤镜数据根目录
     *
     * @return
     */
    public static File getLivePhotoHomeDir() {
        File dir = new File(getCacheDirectory(), LIVE_PHOTO_FILE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static boolean needUpdate(Context context) {
        int currentSaveVersion = PreferenceUtil.getDefault(context).getInt(KEY_DEMO_PHOTO_LIVE_VERSION, -1);

        return VERSION != currentSaveVersion;
    }

    public static void saveCurrentVersion(Context context) {
        PreferenceUtil.getDefault(context).edit().putInt(KEY_DEMO_PHOTO_LIVE_VERSION, VERSION).apply();
    }

    public static File getCacheDirectory() {
        return AppContext.getContext().getFilesDir();
    }

}
