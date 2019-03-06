package com.immomo.videosdk.recorder.helper;

import com.immomo.videosdk.bean.MomentFace;

import java.util.Observable;

/**
 * Created by chenwangwang on 2018/3/28   <br/>
 * 发布变脸素材下载完成的事件            <br/>
 *
 * 注意：                             <br/>
 * 1. 回调发生在异步线程                  <br/>
 * 2. 注册监听的小伙伴，记得取消注册       <br/>
 */
public class MomentFaceDownloadPublisher extends Observable {

    private static class Holder {
        private Holder() {}
        private static final MomentFaceDownloadPublisher sInstance = new MomentFaceDownloadPublisher();
    }

    private MomentFaceDownloadPublisher() { }

    public static MomentFaceDownloadPublisher getInstance() {
        return Holder.sInstance;
    }

    public void updateMomentFace(MomentFace momentFace) {
        synchronized (this) {
            setChanged();
            notifyObservers(momentFace);
        }
    }
}
