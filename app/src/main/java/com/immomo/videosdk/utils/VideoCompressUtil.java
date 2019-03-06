package com.immomo.videosdk.utils;

import com.immomo.mmutil.app.AppContext;
import com.immomo.mmutil.task.MomoMainThreadExecutor;
import com.immomo.mmutil.task.ThreadUtils;
import com.immomo.moment.config.MRecorderActions;
import com.immomo.moment.mediautils.cmds.AudioEffects;
import com.immomo.moment.mediautils.cmds.EffectModel;
import com.immomo.moment.mediautils.cmds.TimeRangeScale;
import com.immomo.moment.mediautils.cmds.VideoEffects;
import com.immomo.moment.recorder.MomoProcess;
import com.immomo.videosdk.recorder.MediaConstants;
import com.immomo.videosdk.recorder.model.Video;

import java.io.File;

/**
 * 7.6新增视频压缩工具，指将分辨率超过1280的视频等比压缩至1280
 * Created by yichao on 17/3/1.
 */

public class VideoCompressUtil {
    private static final int FRAME_RATE = MediaConstants.DEFAULT_FRAME_RATE;
    private static final int AVG_BITRATE = MediaConstants.BIT_RATE_FOR_CUT_VIDEO;

    public static final String TASK_TAG = "VideoCompressUtil";
    public static final String DIR_LOC_TRANS = "local_trans";//该文件夹用于存储在视频选取压缩和截取时所生成的临时文件夹
    private static MomoProcess compressProcess;

    public static void compressVideo(Video video, int[] maxSize, boolean needCompress, final CompressVideoListener compressVideoListener) {
        if (needCompress) {
            compressVideo(video, maxSize[0], maxSize[1], compressVideoListener);
        } else {
            compressVideoListener.onFinishCompress(video, false);
        }
    }

    public static void compressVideo(Video video, int[] maxSize, final CompressVideoListener compressVideoListener) {
        compressVideo(video, maxSize[0], maxSize[1], compressVideoListener);
    }

    public static void compressVideo(Video video, int maxWidth, int maxHeight, final CompressVideoListener compressVideoListener) {
        compressVideoListener.onStartCompress();
        final Video tempVideo = new Video(video.getId(), video.path);
        //获取图片存储路径
        File tempDir = AppContext.getContext().getCacheDir();
        if (tempDir == null) {
            return;
        }

        if (compressProcess != null) {
            compressProcess.release();
        }
        compressProcess = new MomoProcess();
        compressProcess.setIFrameOnly(true);

        String tempVideoDir = tempDir.getAbsolutePath();
        String fileName = String.valueOf(System.currentTimeMillis());
        String finalVideoPath = tempVideoDir + File.separator + fileName + ".mp4";
        //计算压缩宽高
        int[] compressSize = VideoUtils.getCompressVideoSize(video, new int[]{maxWidth, maxHeight});

        //bit 3M
        video.frameRate = FRAME_RATE;
        if (video.avgBitrate <= 0)
            video.avgBitrate = AVG_BITRATE;
        compressProcess.setOutMediaVideoInfo(compressSize[0], compressSize[1], (int) video.frameRate, video.avgBitrate, true);
        tempVideo.frameRate = video.frameRate;
        tempVideo.avgBitrate = video.avgBitrate;
        tempVideo.length = VideoUtils.getVideoDuration(video.path);

        EffectModel effectModel = new EffectModel();
        effectModel.setMediaPath(video.path);
        VideoEffects effects = new VideoEffects();
        effects.setTimeRangeScales(new TimeRangeScale(0, tempVideo.length, 1));
        effectModel.setVideoEffects(effects);
        effectModel.setAudioEffects(new AudioEffects());
        String json = EffectModel.toEffectCmd(effectModel);
        //设置process压缩回调
        compressProcess.setOnStatusListener(new MRecorderActions.OnProcessProgressListener() {
            @Override
            public void onProcessProgress(final float progress) {
                MomoMainThreadExecutor.post(TASK_TAG, new Runnable() {
                    @Override
                    public void run() {
                        compressVideoListener.onUpdateCompress(progress);
                    }
                });
            }

            @Override
            public void onProcessFinished() {
                MomoMainThreadExecutor.post(TASK_TAG, new Runnable() {
                    @Override
                    public void run() {
                        compressVideoListener.onFinishCompress(tempVideo, true);
                    }
                });
            }
        });

        compressProcess.setOnProcessErrorListener(new MRecorderActions.OnProcessErrorListener() {
            @Override
            public void onErrorCallback(int what, int errorCode, String msg) {
                MomoMainThreadExecutor.post(TASK_TAG, new Runnable() {
                    @Override
                    public void run() {
                        compressVideoListener.onErrorCompress(tempVideo);
                    }
                });
            }
        });

        if (!compressProcess.prepare(json)) {
            if (compressVideoListener != null)
                compressVideoListener.onErrorCompress(tempVideo);
            return;
        }

        //开始压缩
        compressProcess.makeVideo(finalVideoPath);
        tempVideo.path = finalVideoPath;
    }

    /**
     * 停止压缩
     */
    public static void stopCompress() {
        ThreadUtils.execute(ThreadUtils.TYPE_RIGHT_NOW, new Runnable() {
            @Override
            public void run() {
                if (compressProcess != null) {
                    compressProcess.release();
                }
            }
        });
    }

    public interface CompressVideoListener {
        void onStartCompress();

        void onUpdateCompress(float progress);

        void onFinishCompress(Video result, boolean hasTranscoding);

        void onErrorCompress(Video result);
    }
}
