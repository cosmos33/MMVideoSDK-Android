package com.immomo.videosdk.recorder.helper;

import android.graphics.Bitmap;

import com.immomo.mdlog.MDLog;
import com.immomo.mediasdk.dynamicresources.DynamicResourceConstants;
import com.immomo.mediasdk.dynamicresources.DynamicResourceManager;
import com.immomo.mediasdk.utils.ImageUtil;
import com.immomo.moment.mediautils.VideoDataRetrieverBySoft;
import com.immomo.videosdk.utils.RecoderFileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 时刻合成处理
 * Project momodev
 * Package com.immomo.momo.moment.publish
 * Created by tangyuchun on 8/22/16.
 * <p>
 * 生成视频路径: 原视频文件夹+{@link System#currentTimeMillis()}+".mp4_"
 */
public class MomentVideoProcessor {

    /**
     * 获取视频2s以内一帧，回调不在UI线程
     *
     * @param path
     * @param callback
     */
    public static void generateThumbnail(final String path, final int rotate, final ThumbnailCallback callback) {
        final VideoDataRetrieverBySoft retriever = new VideoDataRetrieverBySoft();
        if (!retriever.initWithType(path, VideoDataRetrieverBySoft.GET_FRAME_TYPE_BY_BEST, 0, 1)) {
        } else {
            retriever.setImageFrameFilterListener(new VideoDataRetrieverBySoft.ImageFrameFilterListener() {
                @Override
                public void doFilterFrame(Bitmap bitmap) {
                    try {
                        File thumbnailFile = new File(RecoderFileUtils.getRecoderCache(), System.currentTimeMillis() + ".jpg");
                        //                        if (thumbnailFile != null && !thumbnailFile.exists()) {
                        //                            thumbnailFile.createNewFile();
                        //                        }
                        if (bitmap != null) {
                            if (rotate != 0) {
                                Bitmap rotateBitmap = ImageUtil.rotateBitmap(bitmap, rotate);
                                RecoderFileUtils.saveImageFile(rotateBitmap, thumbnailFile);
                                if (!rotateBitmap.isRecycled())
                                    rotateBitmap.recycle();
                            } else
                                RecoderFileUtils.saveImageFile(bitmap, thumbnailFile);
                            if (!bitmap.isRecycled())
                                bitmap.recycle();
                            if (callback != null) {
                                callback.onSaveThumb(thumbnailFile);
                            }
                        }
                    } catch (Exception e) {
                        MDLog.printErrStackTrace("MomentVideoProcessor", e);
                    }
                }

                @Override
                public void doFilterComplete() {
                    retriever.release();
                }

                @Override
                public void doFilterError(Exception e) {
                    MDLog.printErrStackTrace("MomentVideoProcessor", e);
                }
            });

            File faFilePath = DynamicResourceManager.getInstance().getResource(DynamicResourceConstants.ITEM_NAME_MMCV_FA_MODEL);
            File fdFilePath = DynamicResourceManager.getInstance().getResource(DynamicResourceConstants.ITEM_NAME_MMCV_MACE_FD_MODEL);
            List<String> modlePath = new ArrayList<>();
            if (fdFilePath != null && fdFilePath.exists() && faFilePath != null && faFilePath.exists()) {
                modlePath.add(0, fdFilePath.getAbsolutePath());
                modlePath.add(1, faFilePath.getAbsolutePath());
                retriever.setmFaceModeList(modlePath);
            }
            retriever.executeFrameFilter();

        }
    }

    public interface ThumbnailCallback {
        void onSaveThumb(File file);
    }
}
