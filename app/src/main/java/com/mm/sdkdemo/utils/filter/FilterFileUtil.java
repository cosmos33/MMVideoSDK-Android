package com.mm.sdkdemo.utils.filter;

import com.immomo.mmutil.app.AppContext;

import java.io.File;

/**
 * Created by jiabin on 2017/6/29.
 */

public class FilterFileUtil {

    public static final String MOMENT_FILTER_FILE = "filterData";
    public static final String MOMENT_FILTER_IMG_FILE = "filterImg";
    public static final String MAKE_UP_PNG = "makeup.png";

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
