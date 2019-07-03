package com.mm.sdkdemo.recorder.sticker;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.mm.mediasdk.utils.DownloadUtil;
import com.mm.mmutil.FileUtil;
import com.mm.mmutil.StringUtils;
import com.mm.mmutil.log.Log4Android;
import com.mm.mmutil.task.MomoTaskExecutor;
import com.mm.sdkdemo.config.Configs;

import java.io.File;

/**
 * Created by zhu.tao on 2017/6/15.
 */

public class StickerManager {

    private static final String TAG = "StickerManager";

    private OnStickerDownloadListener stickerDownloadListener = null;

    /**
     *
     * 需要考虑的情况：
     * 1.同一个贴纸多次点击
     * 2.多个贴纸分别点击
     * 3.贴纸数量上限的判断
     *
     * ====================================
     * 1.下载
     * 2.解压
     * 3.判断是否正在下载
     * 4.下载成功的回调
     */

    public boolean startDownloadFaceResource(@NonNull final Rect viewRect, @NonNull final DynamicSticker sticker, @NonNull final int pos, @NonNull final OnStickerDownloadListener stickerDownloadListener) {
        if (sticker == null || isStickerDownloading(sticker)) {
            Log4Android.getInstance().w("tang-----资源已经开始下载 " + (sticker != null ? sticker.getId() : ""));
            return false;
        }
        this.stickerDownloadListener = stickerDownloadListener;

        //创建下载任务的ID
        String taskID = getStickerDownloadID(sticker);
        final File file = new File(getDynamicStickerHomeDir(), taskID + "_" + System.currentTimeMillis() + ".zip");
        stickerDownloadListener.onStart(viewRect, sticker, sticker.getId(), pos);
        DownloadUtil.get().download(sticker.getZip(), file.getAbsolutePath(), new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                doUnzip(file.getAbsolutePath(), pos, sticker, viewRect);
            }

            @Override
            public void onDownloading(int progress) {

            }

            @Override
            public void onDownloadFailed() {
                Log4Android.getInstance().d("tang------下载变脸资源失败 " + sticker.getZip());
                if (stickerDownloadListener != null) {
                    stickerDownloadListener.onFailed(viewRect, sticker, sticker.getId(), pos);
                }
            }
        });

        return true;
    }

    private void doUnzip(String savePath, int pos, DynamicSticker sticker, Rect viewRect) {
        if (savePath == null) {
            return;
        }
        String zipPath = savePath;
        if (TextUtils.isEmpty(zipPath)) {
            if (stickerDownloadListener != null) {
                stickerDownloadListener.onFailed(viewRect, sticker, sticker.getId(), pos);
            }
            return;
        }
        final File zipFile = new File(zipPath);
        if (!zipFile.exists() || zipFile.length() <= 0) {
            if (stickerDownloadListener != null) {
                stickerDownloadListener.onFailed(viewRect, sticker, sticker.getId(), pos);
            }
            return;
        }
        MomoTaskExecutor.executeTask(MomoTaskExecutor.EXECUTOR_TYPE_USER, getTaskTag(), new UnzipTask(savePath, pos, sticker, viewRect));
    }

    /**
     * 获得异步任务需要的唯一ID
     *
     * @return
     */
    public Object getTaskTag() {
        return this.getClass().getName() + '@' + Integer.toHexString(this.hashCode());
    }

    private class UnzipTask extends MomoTaskExecutor.Task<Object, Object, String> {
        String task;
        int pos;
        DynamicSticker sticker;
        Rect viewRect;

        public UnzipTask(String task, int pos, DynamicSticker sticker, Rect viewRect) {
            super();
            this.task = task;
            this.pos = pos;
            this.sticker = sticker;
            this.viewRect = viewRect;
        }

        @Override
        protected void onPreTask() {

        }

        @Override
        protected String executeTask(Object... params) {
            String zipPath = task;
            File zipFile = new File(zipPath);
            Log4Android.getInstance().d("tang----开始解压资源 " + sticker.getId() + "   " + zipFile.getAbsolutePath());
            long start = System.currentTimeMillis();
            File targetDir = getStickerDir(sticker);
            if (targetDir.exists()) {
                Log4Android.getInstance().w("tang----资源已经存在，删除掉 " + targetDir.getAbsolutePath());
                FileUtil.deleteDir(targetDir);
            }
            targetDir.mkdirs();

            FileUtil.unzip(zipPath, targetDir.getAbsolutePath(), true);
            Log4Android.getInstance().d("tang----解压资源结束耗时：" + (System.currentTimeMillis() - start) + "  " + targetDir.getAbsolutePath());

            //删除下载的zip包
            zipFile.delete();

            return targetDir.getAbsolutePath();
        }

        @Override
        protected void onTaskError(Exception e) {
            if (stickerDownloadListener != null) {
                stickerDownloadListener.onFailed(viewRect, sticker, sticker.getId(), pos);
            }
        }

        @Override
        protected void onTaskSuccess(String result) {
            File file = new File(result);
            File[] files = file.listFiles();
            if (files == null || files.length <= 0) {
                if (stickerDownloadListener != null) {
                    stickerDownloadListener.onFailed(viewRect, sticker, sticker.getId(), pos);
                }
            } else {
                if (stickerDownloadListener != null) {
                    stickerDownloadListener.onSuccess(viewRect, sticker, sticker.getId(), pos);
                }
            }
        }
    }

    /**
     * 获取动态贴纸的主文件夹
     * @return
     */
    public static File getDynamicStickerHomeDir() {
        File dir = new File(Configs.PATH_MOMENT, "dynamic_sticker");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    public static void cleanStickerHomeDir() {
        FileUtil.deleteDir(getDynamicStickerHomeDir());
    }

    /**
     * 资源是否正在下载
     *
     * @param sticker
     * @return
     */
    public static boolean isStickerDownloading(DynamicSticker sticker) {
        if (sticker == null) {
            return false;
        }
        return DownloadUtil.get().isDownloading(sticker.getZip());
    }

    /**
     * 变脸素材的存放目录
     *
     * @param sticker
     * @return
     */
    public static File getStickerDir(DynamicSticker sticker) {
        if (sticker == null) {
            return null;
        }
        return new File(getDynamicStickerHomeDir(), sticker.getId() + File.separator + sticker.getVersion());
    }

    /**
     * 是否已经下载了素材
     *
     * @param sticker
     * @return
     */
    public static boolean isStickerDownloaded(DynamicSticker sticker) {
        File dir = getStickerDir(sticker);
        if (dir != null) {
            File[] list = dir.listFiles();
            if (null == list || 0 == list.length) {
                return false;
            }
            for (int i = 0, length = list.length; i < length; i++) {
                File file = list[i];
                String fileName = file.getName();
                if (fileName.contains("params")) {
                    return true;
                }
            }

        }
        return false;
    }

    private static String getStickerDownloadID(DynamicSticker sticker) {
        return StringUtils.md5(sticker.getZip());
    }

    public interface OnStickerDownloadListener {
        void onStart(Rect viewRect, DynamicSticker sticker, String stickerId, int position);

        void onSuccess(Rect viewRect, DynamicSticker sticker, String stickerId, int position);

        void onFailed(Rect viewRect, DynamicSticker sticker, String stickerId, int position);
    }

}
