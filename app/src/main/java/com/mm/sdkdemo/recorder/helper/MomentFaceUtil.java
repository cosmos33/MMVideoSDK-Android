package com.mm.sdkdemo.recorder.helper;

import android.text.TextUtils;

import com.immomo.mdlog.MDLog;
import com.mm.mediasdk.utils.VideoFaceUtils;
import com.immomo.mmutil.FileUtil;
import com.immomo.mmutil.app.AppContext;
import com.immomo.mmutil.task.MomoMainThreadExecutor;
import com.immomo.mmutil.task.ThreadUtils;
import com.mm.sdkdemo.bean.MaskLoadCallback;
import com.mm.sdkdemo.bean.MomentFace;
import com.mm.sdkdemo.log.LogTag;
import com.mm.sdkdemo.widget.MomentFaceDataManager;
import com.momo.mcamera.mask.MaskModel;

import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chenwangwang on 2018/3/27.
 * 时刻变脸工具类
 */
public class MomentFaceUtil {

    /**
     * 下载变脸资源到磁盘
     * @param face 变脸信息
     * @param callback 下载回调
     */
    public static void downloadFace(MomentFace face, DownloadFaceCallback callback) {
        FaceSyncTask.Builder builder = new FaceSyncTask.Builder();
        builder.setMomentFace(face);
        builder.setDownloadFaceCallback(callback);
        MomentFaceDownloader.getInstance().execute(builder.build());
    }

    /**
     * 加载变脸资源到内存，如果没有下载，会先进行下载操作（进行下载根据网络不一样下载时间会不同）
     * @param face 变脸信息
     * @param callback 资源加载回调接口
     */
    public static void loadFaceMask(MomentFace face, final MaskLoadCallback callback) {
        loadFaceMask(face, callback, true);
    }

    /**
     * 加载变脸资源到内存，请注意 checkDownloadFirst 参数的使用
     * @param face 变脸信息
     * @param callback 资源加载回调接口
     * @param checkDownloadFirst 是否需要检测本地文件可用性，true 需要，false 不需要。 如果该值是true，本地资源文件不可用，将会进行下载。
     */
    public static void loadFaceMask(MomentFace face, final MaskLoadCallback callback, boolean checkDownloadFirst) {
        if (face == null || callback == null) {
            return;
        }
        if (checkDownloadFirst) {
            FaceSyncTask.Builder builder = new FaceSyncTask.Builder();
            builder.setMomentFace(face);
            builder.setDownloadFaceCallback(new DownloadFaceCallbackAdapter() {
                @Override
                public void onFaceDownloadSuccess(final MomentFace face, boolean isLocalExit) {
                    readFaceMaskAsync(face, callback);
                }

                @Override
                public void onFaceDownloadFailed(MomentFace face) {
                    callback.onMaskLoadFailed(face);
                }
            });
            MomentFaceDownloader.getInstance().execute(builder.build());
        } else {
            readFaceMaskAsync(face, callback);
        }
    }

    /**
     * 异步读取变脸模型
     * @param face 变脸信息
     * @param callback 回调方法
     * @see #loadFaceMask(MomentFace, MaskLoadCallback)
     * @see #loadFaceMask(MomentFace, MaskLoadCallback, boolean)
     */
    private static void readFaceMaskAsync(final MomentFace face, final MaskLoadCallback callback) {
        ThreadUtils.execute(ThreadUtils.TYPE_RIGHT_NOW_LOCAL, new Runnable() {
            @Override
            public void run() {

                final MaskModel maskModel = VideoFaceUtils.readMaskModel(AppContext.getContext(), MomentFaceFileUtil.getFaceResourceDir(face));
                MomoMainThreadExecutor.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onMaskLoadSuccess(maskModel, face);
                    }
                });
            }
        });
    }

    /**
     * 工厂方法，根据类型创建对应的{@link MomentFaceDataManager}对象
     * @param faceType 变脸类型（不同业务对应不同类型），{@link MomentPanelType}
     * @return 变脸数据管理对象
     */
    public static MomentFaceDataManager createMomentFaceDataManager(@MomentPanelType int faceType) {
        MomentFaceDataManager momentFaceDataManager;
        switch (faceType) {
            case MomentFaceConstants.MOMENT_FACE:
            default:
                momentFaceDataManager = new CommonMomentFaceDataManager();
                break;
        }
        return momentFaceDataManager;
    }

    /**
     * 从变脸数据集合中查找出指定的变脸项
     * @param faceId 变脸元素id
     */
    public static MomentFace findMomentFace(List<MomentFace> faceList, String faceId) {
        for (MomentFace momentFace : faceList) {
            if (TextUtils.equals(faceId, momentFace.getId())) {
                return momentFace;
            }
        }
        return null;
    }

    /**
     * 是否正在下载队列中，可能在下载中，或者等待下载的状态，如果想知道是否在下载
     * @return true 在队列里
     */
    public static boolean isOnDownloadTask(MomentFace face) {
        return MomentFaceDownloader.getInstance().isOnDownloadTask(face);
    }

    /**
     * 通过文件是否存在，判断变脸资源是否已经下载
     * @param face 变脸信息
     * @return true 已经下载，false 未下载
     */
    public static boolean simpleCheckFaceResource(MomentFace face) {
        return MomentFaceFileUtil.getFaceResourceDir(face).exists();
    }

    /**
     * 是否已经下载了素材
     * 耗时较长，2~7ms，不适合循环多次调用（在刷新list中）
     */
    public static boolean isFaceResourceOK(MomentFace face) {
        File dir = MomentFaceFileUtil.getFaceResourceDir(face);
        if (dir == null) {
            return false;
        }

        File[] list = dir.listFiles();
        if (list == null || list.length <= 0) {
            return false;
        }

        File lmf = MomentFaceFileUtil.getRecordLTFile(dir);
        if (!lmf.exists() || lmf.length() <= 0) {
            return false;
        }

        return checkRecordTime(dir, lmf);
    }

    private static boolean checkRecordTime(File dir, File recordFile) {
        try {
            String jsonstr = FileUtil.readStr(recordFile);
            if (TextUtils.isEmpty(jsonstr))
                return false;
            JSONObject jo = new JSONObject(jsonstr);
            long dirLM = jo.optLong(MomentFaceConstants.ROOT_NAME);
            if (dirLM != dir.lastModified())
                return false;
            jo.remove(MomentFaceConstants.ROOT_NAME);
            int len = jo.length();
            File[] list = dir.listFiles(MomentFaceFileUtil.sDirFilter);
            int dirLen = list == null ? 0 : list.length;
            if (dirLen != len)
                return false;
            if (len == 0 && dirLen == len)
                return true;
            Iterator<String> keys = jo.keys();
            while (keys.hasNext()) {
                String k = keys.next();
                File f = new File(dir, k);
                if (!f.exists() || !f.isDirectory())
                    return false;
                long lm = jo.optLong(k);
                if (lm != f.lastModified())
                    return false;
            }
            return true;
        } catch (Exception e) {
            MDLog.printErrStackTrace(LogTag.RECORDER.FACE, e);
            return false;
        }
    }
}
