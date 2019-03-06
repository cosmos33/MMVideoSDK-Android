package com.immomo.videosdk.bean;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.immomo.mdlog.MDLog;
import com.immomo.videosdk.config.Configs;
import com.immomo.videosdk.log.LogTag;
import com.immomo.videosdk.recorder.model.Video;
import com.immomo.videosdk.utils.VideoUtils;

import java.io.File;

public class MomentExtraInfo {

    private static final int MAX_DURATION = 3 * 60 * 1000;  //3分钟以上的视频需要压缩
    private static final int MAX_SIZE = 720;                //720P适配需压缩
    private static final int WIDTH_640P = 480;
    private static final int HEIGHT_640P = 640;
    private static final int DEFAULT_FPS = 30;
    private static final int DEFAULT_BITRATE = 4516582; //2.4M

    //不序列化的
    private transient Bitmap blendBmp;

    private Video video;

    private boolean isUseBgChanger;

    private boolean videoChanged;

    public int encodeMode;


    public MomentExtraInfo(Video video) {
        this.video = video;
    }

    public int[] getTargetSize() {
        int width = video.getWidth();
        int height = video.height;

        if (width > 1000 && height > 1000) {
            if (width > height) {
                width = 1280;
                height = 720;
            } else {
                width = 720;
                height = 1280;
            }
        }

        int[] size = new int[]{width, height};

        if (video.isChosenFromLocal) {
            if (video.length > MAX_DURATION) {
                final int w = size[0];
                final int h = size[1];
                final Video temp = new Video();
                temp.setWidth(w);
                temp.height = h;
                int[] compressSize = VideoUtils.getCompressVideoSize(temp, new int[]{WIDTH_640P, HEIGHT_640P});
                size[0] = compressSize[0];
                size[1] = compressSize[1];
            }
        }
        return size;
    }


    public int getVideoBitRate() {
        int bitRate = -1;

        //本地视频使用原始码率
        if (video.isChosenFromLocal && video.length > 0) {
            bitRate = (int) (video.size * 1.0f / video.length * 8000);
        }
        if (bitRate <= 0)
            bitRate = DEFAULT_BITRATE;

        return bitRate;
    }

    public boolean getUseCQ() {
        //是否使用cq从配置中获得
        Boolean isUseCQ = true;

        //本地视频永远使用CQ，使用原始码率
        //拍摄视频走机型配置，默认使用CQ
        if (video.isChosenFromLocal) {
            isUseCQ = true;
        } else {
        }

        return isUseCQ;
    }

    public int getVideoFPS() {
        int fps = (int) video.frameRate;
        if (fps <= 0)
            fps = DEFAULT_FPS;

        return fps;
    }

    public long getVideoFileSize() {
        if (video.size != 0) {
            return video.size;
        }

        return new File(video.path).length();
    }

    public boolean getUseBgChanger() {
        return isUseBgChanger;
    }

    public void setUseBgChanger(boolean isUseBgChanger) {
        this.isUseBgChanger = isUseBgChanger;
    }

    public void setEncodeMode(int encodeMode) {
        this.encodeMode = encodeMode;
    }

    public void setBlendBmp(Bitmap pBlendBmp) {
        this.blendBmp = pBlendBmp;
    }

    public boolean isVideoChanged() {
        return videoChanged;
    }

    public void setVideoChanged(boolean videoChanged) {
        this.videoChanged = videoChanged;
    }

    public Bitmap getBlendBitmap() {
        return blendBmp;
    }
}
