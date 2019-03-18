package com.mm.sdkdemo.utils;

import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.immomo.mmutil.StringUtils;
import com.immomo.mmutil.log.Log4Android;
import com.immomo.moment.mediautils.VideoDataRetrieverBySoft;
import com.mm.sdkdemo.recorder.model.Video;

import java.io.File;

/**
 * Created by XiongFangyu on 2017/3/16.
 */

public class VideoUtils {
    public static final String DIR_LOC_TRANS = "local_trans";//该文件夹用于存储在视频选取压缩和截取时所生成的临时文件夹

    private static final int MAX_ANDROID_SIZE = 1080;
    private static final int MAX_ANDROID_MAX_SIZE = 1920;

    public static final boolean isLocalVideoSizeTooLarge(@NonNull Video video) {
        final int[] maxSize = getMaxVideoSize();
        final int maxPx = maxSize[0] * maxSize[1];
        final int min = Math.min(video.getWidth(), video.height);
        final long px = video.getWidth() * video.height;
        return min > maxSize[0] && px > maxPx;
    }

    public static final int[] getMaxVideoSize() {
        return new int[]{MAX_ANDROID_SIZE, MAX_ANDROID_MAX_SIZE};
    }

    public static boolean isSupportSoundPitch() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static final int[] getCompressVideoSize(Video video, int[] size) {
        final int[] result = new int[2];
        final int[] src = new int[]{video.getWidth(), video.height};
        final float mwh = size[0] / (float) size[1];
        int minIndex = 0, maxIndex = 1;
        //宽大于高
        if (src[0] > src[1]) {
            minIndex = 1;
            maxIndex = 0;
        } else {
            minIndex = 0;
            maxIndex = 1;
        }
        float srcS = src[minIndex] / (float) src[maxIndex];
        //以短边作为基准
        if (srcS > mwh) {
            result[minIndex] = size[0];
            result[maxIndex] = (int) (result[minIndex] / srcS);
        }
        //已长边作为基准
        else {
            result[maxIndex] = size[1];
            result[minIndex] = (int) (result[maxIndex] * srcS);
        }
        return result;
    }

    public static final boolean getVideoMetaInfo(@NonNull Video video) {
        final String path = video.path;
        if (TextUtils.isEmpty(path))
            throw new NullPointerException("Video path cannot be null.");
        boolean result = false;
        VideoDataRetrieverBySoft vdrbs = new VideoDataRetrieverBySoft();
        try {
            if (vdrbs.init(path)) {
                video.length = vdrbs.getDuration() / 1000;
                video.setWidth(vdrbs.getWidth());
                video.height = vdrbs.getHeight();
                video.rotate = vdrbs.getRotation();
                video.frameRate = vdrbs.getFrameRate();
                result = true;
            }
        } catch (Throwable e) {
            Log4Android.getInstance().e(e);
        } finally {
            vdrbs.release();
        }
        return result;
    }

    /**
     * 获取经过修正的视频信息
     */
    public static boolean getVideoFixMetaInfo(Video video) {
        boolean result = getVideoMetaInfo(video);
        if (video.rotate == 90 || video.rotate == 270) {
            int temp = video.getWidth();
            video.setWidth(video.height);
            video.height = temp;
        }
        return result;
    }

    /**
     * 删除废弃文件，注意该方法仅删除DIR_LOC_TRAN目录下的文件
     */
    public static void deleteTempFile(String path) {
        if (!StringUtils.isEmpty(path) && path.contains(DIR_LOC_TRANS)) {
            File oldFile = new File(path);
            oldFile.delete();
        }
    }

    /**
     * 检测文件是否为有效的视频文件
     */
    public static boolean isValidFile(String path) {
        boolean isVideo = false;
        try {
            File fileToTest = new File(path);
            if (fileToTest.exists()) {
                VideoDataRetrieverBySoft vdrbs = new VideoDataRetrieverBySoft();
                isVideo = vdrbs.init(path);
                if (!isVideo) {
                    Log4Android.getInstance().i("isValidFile false");
                    fileToTest.delete();
                }
                vdrbs.release();
            }
        } catch (Exception ex) {
            Log4Android.getInstance().e(ex);
        }
        return isVideo;
    }

    /**
     * 读取视频文件，并初始化参数
     *
     * @param video {@link Video#path}不能为空
     */
    public static final boolean initVideo(@NonNull Video video) {
        final String path = video.path;
        if (TextUtils.isEmpty(path))
            throw new NullPointerException("Video path cannot be null.");
        boolean result = false;
        VideoDataRetrieverBySoft vdrbs = new VideoDataRetrieverBySoft();
        try {
            if (vdrbs.init(path)) {
                video.length = vdrbs.getDuration() / 1000;
                video.setWidth(vdrbs.getWidth());
                video.height = vdrbs.getHeight();
                result = true;
            }
        } catch (Throwable e) {
            Log4Android.getInstance().e(e);
        } finally {
            vdrbs.release();
        }
        return result;
    }

    /**
     * 获取视频时长
     */
    public static final long getVideoDuration(@NonNull String path) {
        VideoDataRetrieverBySoft vdrbs = new VideoDataRetrieverBySoft();
        long result = 0;
        try {
            if (vdrbs.init(path)) {
                result = vdrbs.getDuration() / 1000;
            }
        } catch (Throwable e) {
            Log4Android.getInstance().e(e);
        } finally {
            vdrbs.release();
        }
        return result;
    }

    /**
     * 复制视频文件到指定目录，并加入到相册
     * 异步
     */
    public static final void copyVideoFileToGallery(final File src) {
        if (src.exists()) {
            AlbumNotifyHelper.getAblumNotifyHelper().copyVideoFile(src);
        }
    }

}
