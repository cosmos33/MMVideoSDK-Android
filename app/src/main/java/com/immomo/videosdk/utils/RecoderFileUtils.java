package com.immomo.videosdk.utils;

import android.graphics.Bitmap;

import com.immomo.mdlog.MDLog;
import com.immomo.mmutil.log.Log4Android;
import com.immomo.videosdk.config.Configs;
import com.immomo.videosdk.log.LogTag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author wang.renguang
 * @time 2018/12/20
 */
public class RecoderFileUtils {

    private static final String LAST_VIDEO_CACHE_CLEAR_TIME = "LAST_VIDEO_CACHE_CLEAR_TIME";

    /**
     * 拍摄生成中间文件存放的目录   发布成功视频后会删除该缓存目录
     * * @return
     */
    public static File getRecoderCache() {
        File tempMomentDir = null;
        try {
            tempMomentDir = Configs.getDir("record");
        } catch (Exception e) {
            MDLog.printErrStackTrace(LogTag.RECORDER.RECORD, e);
        }
        return tempMomentDir;
    }

    /**
     * 拍摄生成中间文件存放的目录   发布成功视频后会删除该缓存目录
     * * @return
     */
    public static File getImageCache() {
        File tempMomentDir = null;
        try {
            tempMomentDir = Configs.getDir("image");
        } catch (Exception e) {
            MDLog.printErrStackTrace(LogTag.RECORDER.RECORD, e);
        }
        return tempMomentDir;
    }

    public static File saveImageFile(Bitmap bitmap, File file) {
        return saveImageFile(bitmap, file, Configs.IMAGE_SAVE_QUALITY);
    }

    public static File saveImageFile(Bitmap bitmap, File file, int saveQuality) {
        //防止空指针无法hold住
        return saveImageFile(Bitmap.CompressFormat.JPEG, bitmap, file, saveQuality);
    }

    public static File saveImageFile(Bitmap.CompressFormat format, Bitmap bitmap, File file, int saveQuality) {
        //防止空指针无法hold住
        if (file == null) {
            return file;
        }
        if (saveQuality <= 0) {
            saveQuality = Configs.IMAGE_SAVE_QUALITY;
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(format, saveQuality, os);
        } catch (FileNotFoundException e) {
            Log4Android.getInstance().e(e);
        } catch (Exception e) {
            Log4Android.getInstance().e(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log4Android.getInstance().e(e);
                }
            }
        }
        return file;
    }
}
