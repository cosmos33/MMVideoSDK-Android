package com.immomo.videosdk.recorder.helper;

import com.immomo.videosdk.bean.MomentFace;

/**
 * Created by chenwangwang on 2018/4/14   <br/>
 * 为方便业务层使用，实现一个空类
 */
public class DownloadFaceCallbackAdapter implements DownloadFaceCallback {
    @Override
    public void onFaceDownloadSuccess(MomentFace face, boolean isLocalExit) {
        // empty override
    }

    @Override
    public void onFaceDownloadFailed(MomentFace face) {
        // empty override
    }

    @Override
    public void onIntegrityDetectionFailed(MomentFace momentFace) {
        // empty override
    }
}
