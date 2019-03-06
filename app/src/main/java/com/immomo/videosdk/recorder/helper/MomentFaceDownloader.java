package com.immomo.videosdk.recorder.helper;

import com.immomo.downloader.DownloadListenerAdapter;
import com.immomo.downloader.DownloadManager;
import com.immomo.downloader.bean.DownloadConstant;
import com.immomo.downloader.bean.DownloadTask;
import com.immomo.mdlog.MDLog;
import com.immomo.mmutil.FileUtil;
import com.immomo.mmutil.MD5Utils;
import com.immomo.mmutil.task.MomoMainThreadExecutor;
import com.immomo.mmutil.task.ThreadUtils;
import com.immomo.videosdk.bean.MomentFace;
import com.immomo.videosdk.log.LogTag;

import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;

/**
 * Created by chenwangwang on 2018/3/27.
 * 变脸资源下载统一管理器          <br/>
 * 想用吗？ -->  {@link MomentFaceUtil#downloadFace(MomentFace, DownloadFaceCallback)}
 */
class MomentFaceDownloader {

    private static class Holder {
        private Holder() {
        }

        private static MomentFaceDownloader sResourceCenter = new MomentFaceDownloader();
    }

    private MomentFaceDownloader() {
    }

    static MomentFaceDownloader getInstance() {
        return Holder.sResourceCenter;
    }

    private String getKey(MomentFace face) {
        return MD5Utils.getMD5(face.getZip_url());
    }

    /**
     * 想用吗？ -->  {@link MomentFaceUtil#downloadFace(MomentFace, DownloadFaceCallback)}
     */
    void execute(final FaceSyncTask task) {
        if (task == null || task.getMomentFace() == null) {
            MDLog.e(LogTag.RECORDER.FACE, "兄弟，变脸信息错了！！！");
            return;
        }

        if (MomentFaceUtil.simpleCheckFaceResource(task.getMomentFace())) {
            simpleCheck(task);
            return;
        }

        executeInternal(task);
    }

    /**
     * 如果本地文件已经存在，进行简单的校验
     * @param task 任务
     */
    private void simpleCheck(final FaceSyncTask task) {
        // 本地存在文件，启动异步线程进行完整性校验
        ThreadUtils.execute(ThreadUtils.TYPE_INNER, new Runnable() {
            @Override
            public void run() {
                final MomentFace momentFace = task.getMomentFace();
                final DownloadFaceCallback downloadFaceCallback = task.getDownloadFaceCallback();
                String key = getKey(momentFace);
                try {
                    IntegrityDetectionChain integrityDetectionChain = new IntegrityDetectionChain(momentFace);
                    boolean handle = integrityDetectionChain.handle();
                    if (handle) {
                        MDLog.e(LogTag.COMMON, "%s 完整性检查通过", key);
                        if (downloadFaceCallback != null) {
                            // 文件有效，直接回调成功
                            MomoMainThreadExecutor.post(new Runnable() {
                                @Override
                                public void run() {
                                    downloadFaceCallback.onFaceDownloadSuccess(momentFace, true);
                                }
                            });
                        }
                    } else {
                        MDLog.e(LogTag.COMMON, "%s 完整性检查不通过", key);
                        // 文件无效
                        // 删除文件
                        FileUtil.deleteDir(MomentFaceFileUtil.getFaceResourceDir(momentFace));
                        if (downloadFaceCallback != null) {
                            MomoMainThreadExecutor.post(new Runnable() {
                                @Override
                                public void run() {
                                    downloadFaceCallback.onIntegrityDetectionFailed(momentFace);
                                }
                            });
                        }
                        // 请求下载
                        executeInternal(task);
                    }
                } catch (Exception e) {
                    MDLog.printErrStackTrace(LogTag.COMMON, e);
                    MDLog.e(LogTag.COMMON, "%s 完整性检查不通过", key);
                    // 文件无效
                    // 删除文件
                    FileUtil.deleteDir(MomentFaceFileUtil.getFaceResourceDir(momentFace));
                    if (downloadFaceCallback != null) {
                        MomoMainThreadExecutor.post(new Runnable() {
                            @Override
                            public void run() {
                                downloadFaceCallback.onIntegrityDetectionFailed(momentFace);
                            }
                        });
                    }
                    // 请求下载
                    executeInternal(task);
                }
            }
        });
    }

    /**
     * 是否在下载任务队列中
     * @param face 变脸资源
     * @return true 在任务队列，可能在下载或者没在下载。 false 代表不在任务队列
     */
    boolean isOnDownloadTask(MomentFace face) {
        if (face == null) {
            return false;
        }
        return DownloadManager.getInstance().findTaskById(getKey(face)) != null;
    }

    private void executeInternal(final FaceSyncTask syncTask) {
        // 启动线程进行下载
        final MomentFace face = syncTask.getMomentFace();
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.taskID = getKey(face);
        downloadTask.sourceUrl = face.getZip_url();
        //                downloadTask.imageUrl = face.getImage_url();
        downloadTask.isShowNotify = true;
        downloadTask.downloadType = DownloadConstant.DOWNLOAD_TYPE_COMMON;

        File file = MomentFaceFileUtil.getDownloadFile(downloadTask.taskID);
        downloadTask.savePath = file.getAbsolutePath();
        DownloadManager.getInstance().add(downloadTask, new DownloadListenerAdapter() {
            @Override
            public void onCompleted(DownloadManager manager, DownloadTask downloadTask1) {
                try {
                    File desFile = MomentFaceFileUtil.getFaceResourceDir(face);
                    FileUtil.deleteDir(desFile);

                    FileUtil.unzip(downloadTask1.savePath, desFile.getAbsolutePath(), true);
                    recordModifyTime(desFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MomoMainThreadExecutor.post(new Runnable() {
                    @Override
                    public void run() {
                        MomentFaceDownloadPublisher.getInstance().updateMomentFace(face);
                        DownloadFaceCallback downloadFaceCallback = syncTask.getDownloadFaceCallback();
                        if (downloadFaceCallback != null) {
                            downloadFaceCallback.onFaceDownloadSuccess(syncTask.getMomentFace(), true);
                        }
                    }
                });
            }

            @Override
            public void onFailed(DownloadManager manager, DownloadTask task, int cause) {
                DownloadFaceCallback downloadFaceCallback = syncTask.getDownloadFaceCallback();
                if (downloadFaceCallback != null) {
                    downloadFaceCallback.onFaceDownloadFailed(syncTask.getMomentFace());
                }
            }
        });
    }

    private void recordModifyTime(File dir) throws Exception {
        long now = System.currentTimeMillis() / 1000 * 1000;
        File[] list = dir.listFiles(sDirFilter);

        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt(MomentFaceConstants.ROOT_NAME, now);
        for (File f : list) {
            if (f == null)
                continue;
            jsonObject.putOpt(f.getName(), f.lastModified());
        }

        File recorderFile = new File(dir, MomentFaceConstants.LAST_MODIFY_NAME);
        if (!recorderFile.exists())
            recorderFile.createNewFile();
        FileUtil.writeStr(recorderFile, jsonObject.toString());
        dir.setLastModified(now);
    }

    /**
     * 文件过滤器
     */
    public static final FileFilter sDirFilter = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };
}
