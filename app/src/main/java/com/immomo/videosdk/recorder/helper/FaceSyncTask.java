package com.immomo.videosdk.recorder.helper;

import com.immomo.videosdk.bean.MomentFace;

/**
 * Created by chenwangwang on 2018/3/27.
 * 同步（下载/加载进入内存）的任务
 */
class FaceSyncTask {
    private MomentFace mMomentFace;
    private DownloadFaceCallback mDownloadFaceCallback;

    private FaceSyncTask(MomentFace momentFace, DownloadFaceCallback downloadFaceCallback) {
        mMomentFace = momentFace;
        mDownloadFaceCallback = downloadFaceCallback;
    }

    public MomentFace getMomentFace() {
        return mMomentFace;
    }

    DownloadFaceCallback getDownloadFaceCallback() {
        return mDownloadFaceCallback;
    }

    static class Builder {
        private MomentFace mMomentFace;
        private DownloadFaceCallback mDownloadFaceCallback;

        public Builder setMomentFace(MomentFace momentFace) {
            mMomentFace = momentFace;
            return this;
        }

        public Builder setDownloadFaceCallback(DownloadFaceCallback downloadFaceCallback) {
            mDownloadFaceCallback = downloadFaceCallback;
            return this;
        }

        public FaceSyncTask build() {
            return new FaceSyncTask(mMomentFace, mDownloadFaceCallback);
        }
    }

}
